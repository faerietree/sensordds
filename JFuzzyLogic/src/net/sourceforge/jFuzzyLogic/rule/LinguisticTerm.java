package net.sourceforge.jFuzzyLogic.rule;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;

import org.jfree.chart.JFreeChart;

/**
 * A linguistic term is an asociation between a termName and a membership function
 * @author pcingola@users.sourceforge.net
 */
public class LinguisticTerm {

	/** Membership function */
	MembershipFunction membershipFunction;
	/** Terms's name */
	String termName;

	public LinguisticTerm(String termName, MembershipFunction membershipFunction) {
		this.termName = termName;
		this.membershipFunction = membershipFunction;
	}

	/**
	 * Create a membership function chart 
	 * @param showIt : If true, plot is displayed
	 */
	public JFreeChart chart(boolean showIt) {
		return membershipFunction.chart(termName, showIt);
	}

	public MembershipFunction getMembershipFunction() {
		return membershipFunction;
	}

	public String getTermName() {
		return termName;
	}

	public void setMembershipFunction(MembershipFunction membershipFunction) {
		this.membershipFunction = membershipFunction;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String toString() {
		return "\tTerm: " + termName + "\t" + membershipFunction.toString();
	}

	public String toString(double value) {
		return "Term: " + termName + "\t" + membershipFunction.membership(value) + "\t" + membershipFunction.toString();
	}

	public String toStringFCL() {
		return "TERM " + termName + " := " + membershipFunction.toStringFCL() + ";";
	}

}
