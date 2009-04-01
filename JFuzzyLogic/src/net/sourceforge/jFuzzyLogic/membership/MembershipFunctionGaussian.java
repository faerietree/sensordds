package net.sourceforge.jFuzzyLogic.membership;

/**
 * Gaussian membership function
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionGaussian extends MembershipFunctionContinuous {

	/**
	 * Constructor 
	 * @param mean : Mean 
	 * @param stdev : Standardt deviation
	 */
	public MembershipFunctionGaussian(double mean, double stdev) {
		super();

		// Initialize
		this.parameters = new double[2];
		this.parameters[0] = mean;
		this.parameters[1] = stdev;

		// Check parameters
		StringBuffer errors = new StringBuffer();
		if( !checkParamters(errors) ) throw new RuntimeException(errors.toString());

	}

	@Override
	public boolean checkParamters(StringBuffer errors) {
		boolean ok = true;

		if( parameters[1] < 0 ) {
			ok = false;
			if( errors != null ) errors.append("Parameter 'stdev' should be greater than zero : " + parameters[1] + "\n");
		}

		return ok;
	}

	@Override
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;
		universeMin = parameters[0] - 4.0 * parameters[1];
		universeMax = parameters[0] + 4.0 * parameters[1];
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	public double membership(double in) {
		return Math.exp(-(in - parameters[0]) * (in - parameters[0]) / (2 * parameters[1] * parameters[1]));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName() + " : " + parameters[0] + " , " + parameters[1];
	}

	/** FCL representation */
	public String toStringFCL() {
		return "GAUSS " + parameters[0] + " " + parameters[1];
	}
}
