package net.sourceforge.jFuzzyLogic.membership;

import java.util.Iterator;

/**
 * Singleton membership function
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionSingleton extends MembershipFunctionDiscrete {

	/**
	 * Constructor for a simple (only one value) singleton
	 * @param valueX
	 * @param valueY
	 */
	public MembershipFunctionSingleton(double valueX, double valueY) {
		super();

		// Initialize
		this.parameters = new double[2];
		this.parameters[0] = valueX;
		this.parameters[1] = valueY;

		// Check parameters
		StringBuffer errors = new StringBuffer();
		if( !checkParamters(errors) ) throw new RuntimeException(errors.toString());
	}

	@Override
	public boolean checkParamters(StringBuffer errors) {
		boolean ok = true;

		if( (parameters[1] < 0) || (parameters[1] > 1) ) {
			ok = false;
			if( errors != null ) errors.append("Error: valueY out of range: " + parameters[1] + "\n");
		}

		return ok;
	}

	@Override
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;
		universeMin = parameters[0];
		universeMax = parameters[0];
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete#iterator()
	 */
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
	public double membership(double in) {
		if( in == parameters[0] ) return parameters[1];
		return 0;
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete#membership(int)
	 */
	public double membership(int index) {
		if( index == 0 ) return parameters[1];
		return 0;
	}

	@Override
	public int size() {
		return 1;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName() + " : {" + parameters[0] + ", " + parameters[1] + "}";
	}

	/** FCL representation */
	public String toStringFCL() {
		return " " + parameters[0];
	}

	@Override
	public double valueX(int index) {
		if( index == 0 ) return parameters[0];
		throw new RuntimeException("Array index out of range: " + index);
	}
}
