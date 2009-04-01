package net.sourceforge.jFuzzyLogic;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.jFuzzyLogic.fcl.FclLexer;
import net.sourceforge.jFuzzyLogic.fcl.FclParser;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

/**
 * Fuzzy inference system
 * 
 * A fuzzy logic inference system based on FCL language 
 * according to "INTERNATIONAL ELECTROTECHNICAL COMMISSION" IEC 1131 - Part 7
 * 
 * @author pcingola@users.sourceforge.net
 */
public class FIS implements Iterable<FunctionBlock> {

	public static boolean debug = false;

	//-------------------------------------------------------------------------
	// Variables
	//-------------------------------------------------------------------------

	/** Several function blocks indexed by name */
	HashMap<String, FunctionBlock> functionBlocks;

	//-------------------------------------------------------------------------
	// Static Methods
	//-------------------------------------------------------------------------

	/**
	 * Create a "Fuzzy inference system (FIS)" from an FCL definition string
	 * @param lexer : lexer to use
	 * @param verbose : be verbose?
	 * @return A new FIS (or null on error)
	 */
	private static FIS createFromLexer(FclLexer lexer, boolean verbose) throws RecognitionException {
		FIS fis = new FIS();
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FclParser parser = new FclParser(tokens);
		// FclParser.fcl_return root = parser.fcl();
		FclParser.main_return root;
		root = parser.main();
		Tree parseTree = (Tree) root.getTree();

		// Error loading file?
		if( parseTree == null ) {
			System.err.println("Can't create FIS");
			return null;
		}

		if( debug ) Gpr.debug("Tree: " + parseTree.toStringTree());
		// Add every FunctionBlock (there may be more than one in each FCL file)
		for( int childNum = 0; childNum < parseTree.getChildCount(); childNum++ ) {
			Tree child = parseTree.getChild(childNum);
			if( debug ) Gpr.debug("Child " + childNum + ":\t" + child + "\tTree:'" + child.toStringTree() + "'");

			// Create a new FunctionBlock
			FunctionBlock functionBlock = new FunctionBlock(fis);

			// Generate fuzzyRuleSet based on tree
			String name = functionBlock.fclTree(child);
			if( debug ) Gpr.debug("FunctionBlock Name: '" + name + "'");
			fis.addFunctionBlock(name, functionBlock);
		}

		return fis;
	}

	/**
	 * Create a "Fuzzy inference system (FIS)" from an FCL definition string
	 * @param fclDefinition : FCL definition
	 * @param verbose : Be verbose?
	 * @return A new FIS or null on error
	 */
	public static FIS createFromString(String fclDefinition, boolean verbose) throws RecognitionException {
		// Parse string (lexer first, then parser)
		FclLexer lexer = new FclLexer(new ANTLRStringStream(fclDefinition));
		// Parse tree and create FIS
		return createFromLexer(lexer, verbose);
	}

	/**
	 * Load an FCL file and create a "Fuzzy inference system (FIS)"  
	 * @param fileName : FCL file name
	 * @param verbose : Be verbose?
	 * @return A new FIS or null on error
	 */
	public static FIS load(InputStream inputStream, boolean verbose) {
		// Parse file (lexer first, then parser)
		FclLexer lexer;
		try {
			lexer = new FclLexer(new ANTLRInputStream(inputStream));
		} catch(IOException e1) {
			System.err.println("Error reading inputStream'" + inputStream + "'");
			return null;
		}

		// Parse tree and create FIS
		try {
			return createFromLexer(lexer, verbose);
		} catch(RecognitionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load an FCL file and create a "Fuzzy inference system (FIS)"  
	 * @param fileName : FCL file name
	 * @return A new FIS or null on error
	 */
	public static FIS load(String fileName) {
		return load(fileName, false);
	}

	/**
	 * Load an FCL file and create a "Fuzzy inference system (FIS)"  
	 * @param fileName : FCL file name
	 * @param verbose : Be verbose?
	 * @return A new FIS or null on error
	 */
	public static FIS load(String fileName, boolean verbose) {
		// Parse file (lexer first, then parser)
		FclLexer lexer;
		try {
			lexer = new FclLexer(new ANTLRReaderStream(new FileReader(fileName)));
		} catch(IOException e) {
			System.err.println("Error reading file '" + fileName + "'");
			return null;
		}

		// Parse tree and create FIS
		try {
			return createFromLexer(lexer, verbose);
		} catch(RecognitionException e) {
			throw new RuntimeException(e);
		}
	}

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	/**
	 * Default constructor
	 */
	public FIS() {
		functionBlocks = new HashMap<String, FunctionBlock>();
	}

	/** Add a function block */
	public void addFunctionBlock(String name, FunctionBlock functionBlock) {
		functionBlocks.put(name, functionBlock);
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	/** Show a chart for each variable in this ruleSet */
	public void chart() {
		getFunctionBlock(null).chart();
	}

	/**
	 * Evaluate fuzzy rules in first function block  
	 */
	public void evaluate() {
		getFunctionBlock(null).evaluate();
	}

	/**
	 * Get a FunctionBlock 
	 * @param name : FunctionBlock's name (can be null to retrieve first available one)
	 * @return FunctionBlock (or null if not found)
	 */
	public FunctionBlock getFunctionBlock(String name) {
		if( name == null ) {
			if( functionBlocks.size() > 1 ) throw new RuntimeException("Can't use name=null when there are more than 1 function blocks!");
			name = functionBlocks.keySet().iterator().next();
		}
		return functionBlocks.get(name);
	}

	/**
	 * Get a variable from first available function block
	 * @param name
	 * @return
	 */
	public Variable getVariable(String name) {
		return getFunctionBlock(null).getVariable(name);
	}

	public Iterator<FunctionBlock> iterator() {
		return functionBlocks.values().iterator();
	}

	/**
	 * Set a variable from first available function block
	 * @param name : Variable's name
	 * @param value : Value to be set
	 */
	public void setVariable(String name, double value) {
		getFunctionBlock(null).getVariable(name).setValue(value);
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();

		// Sort function blocks by name
		ArrayList<String> al = new ArrayList<String>(functionBlocks.keySet());
		Collections.sort(al);

		// Iterate over each function block and append it to output string
		for( String name : al ) {
			FunctionBlock functionBlock = getFunctionBlock(name);
			out.append(functionBlock.toString());
		}

		return out.toString();
	}
}
