package net.sourceforge.jFuzzyLogic.rule;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;


/** 
 * A fuzzy logic term for a 'Rule'. E.g.: "speed IS high"
 * @author pcingola@users.sourceforge.net
 */
public class RuleTerm {

	/** Is it negated? */
	boolean negated;
	/** RuleTerm's name */
	String termName;
	/** Varible */
	Variable variable;

	/**
	 * Constructor 
	 * @param variable
	 * @param term
	 * @param negated
	 */
	public RuleTerm(Variable variable, String term, boolean negated) {
		this.variable = variable;
		this.termName = term;
		this.negated = negated;
	}

	public double getMembership() {
		double memb = variable.getMembership(termName);
		if( negated ) memb = 1.0 - memb;
		return memb;
	}

	public MembershipFunction getMembershipFunction() {
		return variable.getMembershipFunction(termName);
	}

	public String getTermName() {
		return termName;
	}

	public Variable getVariable() {
		return variable;
	}

	public boolean isNegated() {
		return negated;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	public void setTermName(String term) {
		this.termName = term;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public String toString() {
		String is = "IS";
		if( negated ) is = " IS NOT";
		return variable.getName() + " " + is + " " + termName;
	}

}
