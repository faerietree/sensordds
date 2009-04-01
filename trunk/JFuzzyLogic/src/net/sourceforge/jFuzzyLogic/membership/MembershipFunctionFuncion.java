package net.sourceforge.jFuzzyLogic.membership;

import java.util.Iterator;

import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.membership.functions.MffAbs;
import net.sourceforge.jFuzzyLogic.membership.functions.MffCos;
import net.sourceforge.jFuzzyLogic.membership.functions.MffDivide;
import net.sourceforge.jFuzzyLogic.membership.functions.MffExp;
import net.sourceforge.jFuzzyLogic.membership.functions.MffFunction;
import net.sourceforge.jFuzzyLogic.membership.functions.MffLn;
import net.sourceforge.jFuzzyLogic.membership.functions.MffLog;
import net.sourceforge.jFuzzyLogic.membership.functions.MffModulus;
import net.sourceforge.jFuzzyLogic.membership.functions.MffNop;
import net.sourceforge.jFuzzyLogic.membership.functions.MffPow;
import net.sourceforge.jFuzzyLogic.membership.functions.MffSin;
import net.sourceforge.jFuzzyLogic.membership.functions.MffSubstract;
import net.sourceforge.jFuzzyLogic.membership.functions.MffSum;
import net.sourceforge.jFuzzyLogic.membership.functions.MffTan;
import net.sourceforge.jFuzzyLogic.membership.functions.MffTimes;

import org.antlr.runtime.tree.Tree;

/**
 * Membership function that is a (simple) mathematical funcion (the result is a singleton)
 * 
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionFuncion extends MembershipFunctionDiscrete {

	/** Function implemented by this node */
	protected MffFunction function;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	/**
	 * Constructor for a whole AST (tree)
	 */
	public MembershipFunctionFuncion(FunctionBlock functionBlock, Tree tree) {
		super();

		// Add parameters (use first parameter as X vale and second as Y value (same as MembershipFunctionSingleton)
		parameters = new double[2];

		//---
		// Create function tree
		//---
		Object fun = createFuncionTree(functionBlock, tree);

		// First item (tree's root) is a function? => Ok add tree
		if( fun instanceof MffFunction ) function = (MffFunction) fun;
		else {
			// First item is NOT a function (e.g. a variable or a double)? 
			// => Make it a function (using 'Nop')
			Object args[] = new Object[1];
			args[0] = fun;
			function = new MffNop(functionBlock, args);
		}
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	@Override
	public boolean checkParamters(StringBuffer errors) {
		// Gpr.warn("NOT IMPLEMENTED!");
		return true;
	}

	/**
	 * Create a tree o functions (MffFunction)
	 * @param functionBlock Fuzzy Set for this function
	 * @param tree : AST (tree) to parse
	 * @return A tree of MffFunctions. Each leave can be either a function, a value (Double), a Variable or a Variable's name.
	 */
	private Object createFuncionTree(FunctionBlock functionBlock, Tree tree) {
		if(debug) Gpr.debug( "Tree: " + tree.toStringTree());
		String treeName = tree.getText().toUpperCase();

		// Select appropiate funcion (and create it)
		if( treeName.equals("+") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffSum(functionBlock, terms);
		} else if( treeName.equals("-") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffSubstract(functionBlock, terms);
		} else if( treeName.equals("*") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffTimes(functionBlock, terms);
		} else if( treeName.equals("/") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffDivide(functionBlock, terms);
		} else if( treeName.equals("^") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffPow(functionBlock, terms);
		} else if( treeName.equals("%") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffModulus(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("exp") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffExp(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("ln") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffLn(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("log") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffLog(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("sin") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffSin(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("cos") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffCos(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("tan") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffTan(functionBlock, terms);
		} else if( treeName.equalsIgnoreCase("abs") ) {
			Object terms[] = parseTerms(functionBlock, tree);
			return new MffAbs(functionBlock, terms);
		} else // Try to parse it as a 'double'
		try {
			double dval = Double.parseDouble(treeName);
			// OK, it's a double
			return new Double(dval);
		} catch(Throwable e) {
			// Assume it's a variable's name (original name, not upper case)
			if(debug) Gpr.debug( "Variable:" + tree.getText());
			return new String(tree.getText());
		}
	}

	@Override
	public void estimateUniverse() {
		double val = 0;
		if( function != null ) val = function.evaluate();
		universeMin = universeMax = val;
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete#iterator()
	 */
	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {

			int i = 0;

			public boolean hasNext() {
				return (i == 0);
			}

			public Double next() {
				return Double.valueOf(parameters[0]);
			}

			public void remove() {}
		};
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	@Override
	public double membership(double in) {
		if( in == parameters[0] ) return parameters[1];
		return 0;
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete#membership(int)
	 */
	@Override
	public double membership(int index) {
		if( index == 0 ) return parameters[1];
		return 0;
	}

	/**
	 * Parse each term (from tree) creating appropriate functions. 
	 * 
	 * @param ruleBlock Fuzzy Set for this function
	 * @param tree : AST (tree) to parse
	 * @return An array of objects (terms[])
	 */
	private Object[] parseTerms(FunctionBlock functionBlock, Tree tree) {
		if(debug) Gpr.debug( "Tree:" + tree.toStringTree());
		Tree child = tree.getChild(0);
		int numberOfChilds = tree.getChildCount();
		Object terms[] = new Object[numberOfChilds];
		for( int i = 0; i < numberOfChilds; i++ ) {
			child = tree.getChild(i);
			if(debug) Gpr.debug( "\t\tChild:" + child.toStringTree());
			terms[i] = createFuncionTree(functionBlock, child);
		}
		return terms;
	}

	/** It's only one singleton */
	@Override
	public int size() {
		return 1;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + ": " + function.toString();
	}

	/** FCL representation */
	@Override
	public String toStringFCL() {
		return "FUNCTION " + function.toString();
	}

	@Override
	public double valueX(int index) {
		if( index == 0 ) {
			// Evaluate function
			if(debug) Gpr.debug( "Evaluation Begin: " + function);
			double eval = function.evaluate();
			if(debug) Gpr.debug( "Evaluation End: " + eval);
			// Update 'singleton' value
			parameters[0] = eval;
			parameters[1] = 1.0;
			return eval;
		}
		throw new RuntimeException("Array index out of range: " + index);
	}
}
