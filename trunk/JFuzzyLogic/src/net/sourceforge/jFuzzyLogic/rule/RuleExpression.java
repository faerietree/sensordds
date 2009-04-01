package net.sourceforge.jFuzzyLogic.rule;

import java.util.Iterator;
import java.util.LinkedList;

import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethod;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;

/**
 * General rule expression term
 * 
 * E.g.: "(temp IS hot AND pressure IS high) OR pressure IS low" 
 * 
 * @author pcingola@users.sourceforge.net
 */
public class RuleExpression implements Iterable<Variable> {

	/** Is it negated? */
	boolean negated;
	/** How are term1 and term2 connected? */
	RuleConnectionMethod ruleConnectionMethod;
	/** Term1 can be a either a 'RuleTerm' or 'RuleExpression' */
	Object term1;
	/** Term2 can be a either a 'RuleTerm' or 'RuleExpression' */
	Object term2;

	/**
	 * Default Constructor
	 */
	public RuleExpression() {
		this.setTerm1(null);
		this.setTerm2(null);
		this.ruleConnectionMethod = new RuleConnectionMethodAndMin(); // Default connection method: AND (minimum)
	}

	/**
	 * Constructor
	 * @param term1 : term 1 (can be a either a 'RuleTerm' or 'RuleExpression')
	 * @param term2 : term 2 (can be a either a 'RuleTerm' or 'RuleExpression')
	 * @param ruleConnectionMethod : connection method between terms (which method is used for each 'and', 'or', 'not'...)
	 */
	public RuleExpression(Object term1, Object term2, RuleConnectionMethod ruleConnectionMethod) {
		this.setTerm1(term1);
		this.setTerm2(term2);
		this.ruleConnectionMethod = ruleConnectionMethod;
	}

	/**
	 * Add a new term (using default AND method)
	 * @param fuzzyRuleTerm : term to add
	 */
	public void add(RuleTerm fuzzyRuleTerm) {
		// Can add it in term1? => add it
		if( term1 == null ) setTerm1(fuzzyRuleTerm);
		// Can add it in term2? => add it
		else if( term2 == null ) setTerm2(fuzzyRuleTerm);
		// Is term1 an expresion? => recurse
		else if( isFuzzyRuleExpression(term1) ) ((RuleExpression) term1).add(fuzzyRuleTerm);
		// Is term2 an expresion? => recurse
		else if( isFuzzyRuleExpression(term2) ) ((RuleExpression) term2).add(fuzzyRuleTerm);
		// ...there's nothing else I can do
		else throw new RuntimeException("Can't add term!");
		return;
	}

	/**
	 * Add every variable to this list
	 * @param linkedListVariables 
	 */
	private void addVariables(LinkedList<Variable> linkedListVariables) {
		// Term1: is it a 'term'? => add variables
		if( isFuzzyRuleTerm(term1) ) linkedListVariables.add(((RuleTerm) term1).getVariable());
		// Term1: is it an 'expression'? => recurse
		else if( isFuzzyRuleExpression(term1) ) ((RuleExpression) term1).addVariables(linkedListVariables);

		// Term2: is it a 'term'? => add variables
		if( isFuzzyRuleTerm(term2) ) linkedListVariables.add(((RuleTerm) term2).getVariable());
		// Term2: is it an 'expression'? => recurse
		else if( isFuzzyRuleExpression(term2) ) ((RuleExpression) term2).addVariables(linkedListVariables);
	}

	/**
	 * Evaluate this expression
	 * @return evaluation's result
	 */
	public double evaluate() {
		// Results for each term
		double resTerm1 = 0;
		double resTerm2 = 0;

		// Evaluate term1: if it's an expression => recurse
		if( isFuzzyRuleExpression(term1) ) resTerm1 = ((RuleExpression) term1).evaluate();
		else if( isFuzzyRuleTerm(term1) ) resTerm1 = ((RuleTerm) term1).getMembership();
		else if( term1 == null ) resTerm1 = Double.NaN;

		// Evaluate term2: if it's an expression => recurse
		if( isFuzzyRuleExpression(term2) ) resTerm2 = ((RuleExpression) term2).evaluate();
		else if( isFuzzyRuleTerm(term2) ) resTerm2 = ((RuleTerm) term2).getMembership();
		else if( term2 == null ) resTerm2 = Double.NaN;

		double result = 0;
		// No values? => return NaN
		if( (term1 == null) && (term2 == null) ) return Double.NaN;
		// if we only have 1 term => just return that result
		if( term1 == null ) result = resTerm2;
		else if( term2 == null ) result = resTerm1;
		// Ok, we've got 2 values => connect these 2 values 
		else result = ruleConnectionMethod.connect(resTerm1, resTerm2);

		// Is this clause negated?
		if( negated ) result = 1 - result;
		return result;
	}

	public RuleConnectionMethod getRuleConnectionMethod() {
		return ruleConnectionMethod;
	}

	public Object getTerm1() {
		return term1;
	}

	public Object getTerm2() {
		return term2;
	}

	/**
	 * Is this term an expression ('RuleExpression')
	 * @param term : term to analize
	 * @return true if it's an 'RuleExpression', false otherwise
	 */
	public boolean isFuzzyRuleExpression(Object term) {
		if( term == null ) return false;
		if( term.getClass().getName().equals("net.sourceforge.jFuzzyLogic.rule.RuleExpression") ) return true;
		return false;
	}

	/**
	 * Is this term a RuleTerm
	 * @param term : term to analize
	 * @return true if it's an 'RuleTerm', false otherwise
	 */
	public boolean isFuzzyRuleTerm(Object term) {
		if( term == null ) return false;
		if( term.getClass().getName().equals("net.sourceforge.jFuzzyLogic.rule.RuleTerm") ) return true;
		return false;
	}

	public boolean isNegated() {
		return negated;
	}

	/**
	 * Is this a valid term? (only a few objects are acceped as 'terms')
	 * @param term : Term to evaluate
	 */
	public boolean isValidTerm(Object term) {
		if( term == null ) return true;
		if( isFuzzyRuleExpression(term) ) return true;
		if( isFuzzyRuleTerm(term) ) return true;
		return false;
	}

	/**
	 * Iterate on every variable
	 * @return a 'variables' iterator
	 */
	public Iterator<Variable> iterator() {
		LinkedList<Variable> llvars = new LinkedList<Variable>();
		addVariables(llvars);
		return llvars.iterator();
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	public void setRuleConnectionMethod(RuleConnectionMethod ruleConnectionMethod) {
		this.ruleConnectionMethod = ruleConnectionMethod;
	}

	public void setTerm1(Object term1) {
		if( !isValidTerm(term1) ) throw new RuntimeException("Invalid object for term1. Only 'RuleTerm' or 'RuleExpression' are accepted. Class: " + term1.getClass().getName());
		this.term1 = term1;
	}

	public void setTerm2(Object term2) {
		if( !isValidTerm(term2) ) throw new RuntimeException("Invalid object for term2. Only 'RuleTerm' or 'RuleExpression' are accepted. Class: " + term2.getClass().getName());
		this.term2 = term2;
	}

	@Override
	public String toString() {
		String str = "";
		String connector = " " + ruleConnectionMethod.getName().toUpperCase() + " ";
		
		if( (term1 == null) || (term2 == null) ) {
			// Only one term?
			if( term1 != null ) str += term1.toString();
			if( term2 != null ) str += term2.toString();
		} else {
			// Both terms connected
			if( isFuzzyRuleExpression(term1) ) str += "(" + ((RuleExpression) term1).toString() + ")";
			else if( isFuzzyRuleTerm(term1) ) str += ((RuleTerm) term1).toString();

			str += connector;

			if( isFuzzyRuleExpression(term2) ) str += "(" + ((RuleExpression) term2).toString() + ")";
			else if( isFuzzyRuleTerm(term2) ) str += ((RuleTerm) term2).toString();
		}

		if( negated ) str = "NOT " + str;

		return str;
	}
}
