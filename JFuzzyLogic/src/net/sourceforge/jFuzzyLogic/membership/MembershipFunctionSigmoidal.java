package net.sourceforge.jFuzzyLogic.membership;

/**
 * Sigmoidal membership function
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionSigmoidal extends MembershipFunctionContinuous {

	/**
	 * Constructor 
	 * @param gain : Mean 
	 * @param t0 : Standardt deviation
	 */
	public MembershipFunctionSigmoidal(double gain, double t0) {
		super();

		// Initialize
		this.parameters = new double[2];
		this.parameters[0] = gain;
		this.parameters[1] = t0;

		// Check parameters
		StringBuffer errors = new StringBuffer();
		if( !checkParamters(errors) ) throw new RuntimeException(errors.toString());
	}

	@Override
	public boolean checkParamters(StringBuffer errors) {
		boolean ok = true;
		// No checking needed
		return ok;
	}

	@Override
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;
		universeMin = parameters[1] - 9.0 / Math.abs(parameters[0]);
		universeMax = parameters[1] + 9.0 / Math.abs(parameters[0]);
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	public double membership(double in) {
		return 1.0 / (1.0 + Math.exp(-parameters[0] * (in - parameters[1])));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName() + " : " + parameters[0] + " , " + parameters[1];
	}

	/** FCL representation */
	public String toStringFCL() {
		return "SIGM " + parameters[0] + " " + parameters[1];
	}
}
