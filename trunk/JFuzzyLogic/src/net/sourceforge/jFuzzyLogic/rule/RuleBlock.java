package net.sourceforge.jFuzzyLogic.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethod;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethodBoundedSum;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethodMax;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethodNormedSum;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethodProbOr;
import net.sourceforge.jFuzzyLogic.ruleAccumulationMethod.RuleAccumulationMethodSum;
import net.sourceforge.jFuzzyLogic.ruleActivationMethod.RuleActivationMethod;
import net.sourceforge.jFuzzyLogic.ruleActivationMethod.RuleActivationMethodMin;
import net.sourceforge.jFuzzyLogic.ruleActivationMethod.RuleActivationMethodProduct;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethod;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndBoundedDif;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndProduct;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrBoundedSum;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrProbOr;

import org.antlr.runtime.tree.Tree;

/**
 * A set of fuzzy rules
 * 
 * 
 * Reference: See IEC 1131 - Part 7 - Fuzzy Control Programming
 * 
 * @author pcingola@users.sourceforge.net
 */
public class RuleBlock implements Iterable<Rule> {

	/** Debug mode? */
	public static boolean debug = FIS.debug;

	//-------------------------------------------------------------------------
	// Variables
	//-------------------------------------------------------------------------

	/** Which FunctionBlock does this RuleBlock belong to */
	FunctionBlock functionBlock;

	String name;

	/** Rule accumulation method: How results of the rules are combined to obtain an overall result (e.g. MAX: maximum, BSUM: bounded sum, etc.) */
	RuleAccumulationMethod ruleAccumulationMethod;

	/** Rule activation (implication) method: How the 'if' activates the 'then' (e.g. MIN: minimum, PROD: product) */
	RuleActivationMethod ruleActivationMethod;

	/** All the rules */
	LinkedList<Rule> rules;

	//-------------------------------------------------------------------------
	// Static Method
	//-------------------------------------------------------------------------

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		RuleBlock.debug = debug;
	}

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	public RuleBlock(FunctionBlock functionBlock) {
		name = "";
		this.functionBlock = functionBlock;
		rules = new LinkedList<Rule>();
		ruleActivationMethod = new RuleActivationMethodMin(); // Default activation method: Min
	}

	/**
	 * Add a rule to this ruleSet
	 * @param fuzzyRule : Rule to add
	 * @return this
	 */
	public RuleBlock add(Rule fuzzyRule) {
		rules.add(fuzzyRule);
		return this;
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	/**
	 * Create a rule accumulation method based on name
	 * 
	 * @param ruleAccumulationMethodType
	 * @return
	 */
	RuleAccumulationMethod createAccumulationMethod(String ruleAccumulationMethodType) {
		RuleAccumulationMethod ruleAccumulationMethod;
		if( ruleAccumulationMethodType.equalsIgnoreCase("MAX") ) ruleAccumulationMethod = new RuleAccumulationMethodMax();
		else if( ruleAccumulationMethodType.equalsIgnoreCase("BSUM") ) ruleAccumulationMethod = new RuleAccumulationMethodBoundedSum();
		else if( ruleAccumulationMethodType.equalsIgnoreCase("NSUM") ) ruleAccumulationMethod = new RuleAccumulationMethodNormedSum();
		else if( ruleAccumulationMethodType.equalsIgnoreCase("PROBOR") ) ruleAccumulationMethod = new RuleAccumulationMethodProbOr();
		else if( ruleAccumulationMethodType.equalsIgnoreCase("SUM") ) ruleAccumulationMethod = new RuleAccumulationMethodSum();
		else throw new RuntimeException("Unknown/Unimplemented Rule accumulation method '" + ruleAccumulationMethodType + "'");
		return ruleAccumulationMethod;
	}

	/**
	 * Evaluate fuzzy rule set 
	 */
	public void evaluate() {
		// Apply each rule
		for( Rule fuzzyRule : rules ) {
			if( debug ) Gpr.debug("Evaluating rule: " + fuzzyRule);
			fuzzyRule.evaluate(ruleActivationMethod);
		}
	}

	public String fclTree(Tree tree) {
		boolean rulesAdded = false;
		if( debug ) Gpr.debug("Tree: " + tree.toStringTree());
		String name = tree.getChild(0).getText();
		if( debug ) Gpr.debug("RuleBlock name: " + name);

		// Use 'default' methods
		RuleConnectionMethod and = new RuleConnectionMethodAndMin(), or = new RuleConnectionMethodOrMax();
		String ruleAccumulationMethodType = "SUM";

		// Explore each sibling in this level
		for( int childNum = 1; childNum < tree.getChildCount(); childNum++ ) {
			Tree child = tree.getChild(childNum);
			if( debug ) Gpr.debug("Parsing: " + child.toStringTree());
			String leaveName = child.getText();

			if( leaveName.equalsIgnoreCase("AND") ) {
				//---
				// Which 'AND' method to use? (Note: We also set 'OR' method accordingly to fulfill DeMorgan's law
				//---
				if( rulesAdded ) throw new RuntimeException("AND method must be defined prior to RULE definition");
				String type = child.getChild(0).getText();
				if( type.equalsIgnoreCase("MIN") ) {
					and = new RuleConnectionMethodAndMin();
					or = new RuleConnectionMethodOrMax();
				} else if( type.equalsIgnoreCase("PROD") ) {
					and = new RuleConnectionMethodAndProduct();
					or = new RuleConnectionMethodOrProbOr();
				} else if( type.equalsIgnoreCase("BDIF") ) {
					and = new RuleConnectionMethodAndBoundedDif();
					or = new RuleConnectionMethodOrBoundedSum();
				} else throw new RuntimeException("Unknown (or unimplemented) 'AND' method: " + type);
			} else if( leaveName.equalsIgnoreCase("OR") ) {
				//---
				// Which 'AND' method to use? (Note: We also set 'OR' method accordingly to fulfill DeMorgan's law
				//---
				if( rulesAdded ) throw new RuntimeException("OR method must be defined prior to RULE definition");
				String type = child.getChild(0).getText(); // Bug corrected by Arkadiusz M. amaterek@users.sourceforge.net
				if( type.equalsIgnoreCase("MAX") ) {
					or = new RuleConnectionMethodOrMax();
					and = new RuleConnectionMethodAndMin();
				} else if( type.equalsIgnoreCase("ASUM") ) {
					or = new RuleConnectionMethodOrProbOr();
					and = new RuleConnectionMethodAndProduct();
				} else if( type.equalsIgnoreCase("BSUM") ) {
					or = new RuleConnectionMethodOrBoundedSum();
					and = new RuleConnectionMethodAndBoundedDif();
				} else throw new RuntimeException("Unknown (or unimplemented) 'OR' method: " + type);
			} else if( leaveName.equalsIgnoreCase("ACT") ) fclTreeRuleBlockActivation(child);
			else if( leaveName.equalsIgnoreCase("RULE") ) {
				rulesAdded = true;
				fclTreeRuleBlockRule(child, and, or);
			} else if( leaveName.equalsIgnoreCase("ACCU") ) {
				// Accumulation method
				ruleAccumulationMethodType = child.getChild(0).getText();
			} else throw new RuntimeException("Unknown (or unimplemented) ruleblock item : " + leaveName);
		}

		// Create rule accumulation method
		ruleAccumulationMethod = createAccumulationMethod(ruleAccumulationMethodType);

		return name;
	}

	/**
	 * Parse rule Implication Method (or rule activation method)
	 * @param tree : Tree to parse
	 */
	private void fclTreeRuleBlockActivation(Tree tree) {
		String type = tree.getChild(0).getText();
		if( debug ) Gpr.debug("Parsing: " + type);

		if( type.equalsIgnoreCase("MIN") ) ruleActivationMethod = new RuleActivationMethodMin();
		else if( type.equalsIgnoreCase("PROD") ) ruleActivationMethod = new RuleActivationMethodProduct();
		else throw new RuntimeException("Unknown (or unimplemented) 'ACT' method: " + type);
	}

	/**
	 * Parse rule Implication Method (or rule activation method)
	 * @param tree : Tree to parse
	 */
	private void fclTreeRuleBlockRule(Tree tree, RuleConnectionMethod and, RuleConnectionMethod or) {
		if( debug ) Gpr.debug("Tree: " + tree.toStringTree());
		Rule fuzzyRule = new Rule(tree.getChild(0).getText(), this);

		for( int childNum = 1; childNum < tree.getChildCount(); childNum++ ) {
			Tree child = tree.getChild(childNum);
			if( debug ) Gpr.debug("\t\tChild: " + child.toStringTree());
			String type = child.getText();

			if( type.equalsIgnoreCase("IF") ) fuzzyRule.setAntecedents(fclTreeRuleBlockRuleIf(child.getChild(0), and, or));
			else if( type.equalsIgnoreCase("THEN") ) fclTreeRuleBlockRuleThen(child, fuzzyRule);
			else if( type.equalsIgnoreCase("WITH") ) fclTreeRuleBlockRuleWith(child, fuzzyRule);
			else throw new RuntimeException("Unknown (or unimplemented) rule block item: " + type);
		}

		add(fuzzyRule);
	}

	/**
	 * Parse rule 'IF' (or rule's weight)
	 * @param tree : Tree to parse
	 */
	private RuleExpression fclTreeRuleBlockRuleIf(Tree tree, RuleConnectionMethod and, RuleConnectionMethod or) {
		if( debug ) Gpr.debug("Tree: " + tree.toStringTree());
		String ifConnector = tree.getText();

		// Create a new expresion
		RuleExpression fuzzyRuleExpression = new RuleExpression();

		if( ifConnector.equalsIgnoreCase("AND") ) {
			fuzzyRuleExpression.setRuleConnectionMethod(and);
			// Recurse on term1
			fuzzyRuleExpression.setTerm1(fclTreeRuleBlockRuleIf(tree.getChild(0), and, or));
			// Recurse on term2
			fuzzyRuleExpression.setTerm2(fclTreeRuleBlockRuleIf(tree.getChild(1), and, or));
		} else if( ifConnector.equalsIgnoreCase("OR") ) {
			fuzzyRuleExpression.setRuleConnectionMethod(or);
			// Recurse on term2
			fuzzyRuleExpression.setTerm1(fclTreeRuleBlockRuleIf(tree.getChild(0), and, or));
			// Recurse on term2
			fuzzyRuleExpression.setTerm2(fclTreeRuleBlockRuleIf(tree.getChild(1), and, or));
		} else if( ifConnector.equalsIgnoreCase("NOT") ) {
			fuzzyRuleExpression.setNegated(true);
			fuzzyRuleExpression.setTerm1(fclTreeRuleBlockRuleIf(tree.getChild(0), and, or));
		} else if( ifConnector.equalsIgnoreCase("(") ) fuzzyRuleExpression.setTerm1(fclTreeRuleBlockRuleIf(tree.getChild(0), and, or));
		else {
			// It's a "(Variable IS linguisticTerm)" clause
			// or "(Variable IS NOT linguisticTerm)" clause
			String varName = tree.getText();
			String lingTerm = tree.getChild(0).getText();
			boolean negate = false;
			if( lingTerm.equalsIgnoreCase("NOT") ) {
				lingTerm = tree.getChild(1).getText();
				negate = true;
			}
			Variable variable = getVariable(varName);
			RuleTerm fuzzyRuleTerm = new RuleTerm(variable, lingTerm, negate);
			fuzzyRuleExpression.add(fuzzyRuleTerm);
		}
		return fuzzyRuleExpression;
	}

	/**
	 * Parse rule 'THEN' (or rule's weight)
	 * @param tree : Tree to parse
	 */
	private void fclTreeRuleBlockRuleThen(Tree tree, Rule fuzzyRule) {
		if( debug ) Gpr.debug("Tree: " + tree.toStringTree());

		for( int childNum = 0; childNum < tree.getChildCount(); childNum++ ) {
			Tree child = tree.getChild(childNum);
			if( debug ) Gpr.debug("\t\tChild: " + child.toStringTree());
			String thenVariable = child.getText();

			String thenValue = child.getChild(0).getText();
			Variable variable = getVariable(thenVariable);
			if( variable == null ) throw new RuntimeException("Variable " + thenVariable + " does not exist");
			fuzzyRule.addConsequent(variable, thenValue, false);
		}
	}

	/**
	 * Parse rule 'WITH' (or rule's weight)
	 * @param tree : Tree to parse
	 */
	private void fclTreeRuleBlockRuleWith(Tree tree, Rule fuzzyRule) {
		if( debug ) Gpr.debug("Parsing: " + tree.getChild(0).getText());
		fuzzyRule.setWeight(Gpr.parseDouble(tree.getChild(0)));
	}

	public FunctionBlock getFunctionBlock() {
		return functionBlock;
	}

	public String getName() {
		return name;
	}

	public RuleAccumulationMethod getRuleAccumulationMethod() {
		return ruleAccumulationMethod;
	}

	public RuleActivationMethod getRuleActivationMethod() {
		return ruleActivationMethod;
	}

	public LinkedList<Rule> getRules() {
		return rules;
	}

	public Variable getVariable(String name) {
		return functionBlock.getVariable(name);
	}

	public Iterator<Rule> iterator() {
		return rules.iterator();
	}

	/**
	 * Reset ruleset (should be done prior to each inference)
	 * Also create 'variables' list (if needed)
	 */
	public void reset() {
		HashMap<Variable, Variable> resetted = new HashMap<Variable, Variable>();

		//---
		// Reset every consequent variable on every rule
		//---
		for( Rule fr : rules ) {
			// Reset rule's degree of support
			fr.setDegreeOfSupport(0);

			//---
			// Reset every consequent variable (and add it to variables list if needed)
			//---
			LinkedList<RuleTerm> llc = fr.getConsequents();
			for( RuleTerm term : llc ) {
				Variable var = term.getVariable();
				// Not already resetted?
				if( resetted.get(var) == null ) {
					// Sanity check
					if( var.getDefuzzifier() == null ) throw new RuntimeException("Defuzzifier not setted for output variable '" + var.getName() + "'");
					// Reset variable
					var.reset();
					// Mark it as 'resetted' so we don't reset it again
					resetted.put(var, var);
				}
			}

			//---
			// Reset every antecedent's variable  (and add it to variables list if needed)
			//---
			for( Variable var : fr.getAntecedents() ) {
				// Not already resetted?
				if( resetted.get(var) == null ) {
					// Reset variable
					var.reset();
					// Mark it as 'resetted' so we don't reset it again
					resetted.put(var, var);
				}
			}
		}
	}

	public void setFunctionBlock(FunctionBlock functionBlock) {
		this.functionBlock = functionBlock;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRuleAccumulationMethod(RuleAccumulationMethod ruleAccumulationMethod) {
		this.ruleAccumulationMethod = ruleAccumulationMethod;
	}

	public void setRuleActivationMethod(RuleActivationMethod ruleImplicationMethod) {
		ruleActivationMethod = ruleImplicationMethod;
	}

	public void setRules(LinkedList<Rule> rules) {
		this.rules = rules;
	}

	/**
	 * Set a variable
	 * @param variableName : Variable's name
	 * @param value : variable's value to be setted
	 * @return this
	 */
	public RuleBlock setVariable(String variableName, double value) {
		functionBlock.setVariable(name, value);
		return this;
	}

	public void setVariables(HashMap<String, Variable> variables) {
		functionBlock.setVariables(variables);
	}

	@Override
	public String toString() {
		StringBuffer rb = new StringBuffer();
		String operator = "";

		// Show rules
		int ruleNum = 1;
		for( Rule rule : rules ) {
			// Rule name/number
			String name = rule.getName();
			if( (name == null) || (name.equals("")) ) name = Integer.toString(ruleNum);
			
			rb.append("\tRULE " + name + " : " + rule.toStringFCL() + "\n");
			if( rule.getAntecedents().getRuleConnectionMethod() != null ) operator = rule.getAntecedents().getRuleConnectionMethod().toStringFCL();
			ruleNum++;
		}

		// Build string
		String ruleBlockStr = "RULEBLOCK Rules\n";
		if( ruleActivationMethod != null ) ruleBlockStr += "\t" + ruleActivationMethod.toStringFCL() + "\n";
		if( ruleAccumulationMethod != null ) ruleBlockStr += "\t" + ruleAccumulationMethod.toStringFCL() + "\n";
		if( operator.length() > 0 ) ruleBlockStr += "\t" + operator + "\n";
		ruleBlockStr += rb + "END_RULEBLOCK\n";

		return ruleBlockStr;
	}

	/** Get an iterator for variables */
	public Iterator<Variable> variablesIterator() {
		return functionBlock.variablesIterator();
	}

	/** Get an iterator for variables (sorted by name) */
	public Iterator<Variable> variablesIteratorSorted() {
		return functionBlock.variablesIteratorSorted();
	}

	/** Does this variable exist in this RuleBlock? */
	public boolean varibleExists(String variableName) {
		return functionBlock.varibleExists(variableName);
	}
}
