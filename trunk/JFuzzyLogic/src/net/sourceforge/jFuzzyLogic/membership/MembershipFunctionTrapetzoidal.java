package net.sourceforge.jFuzzyLogic.membership;

/**
 * Paralelogram membership function
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionTrapetzoidal extends MembershipFunctionContinuous {

	/**
	 * Constructor 
	 * @param min : Begining of trapetzoidal
	 * @param midLow : Lower midium point of trapetzoidal
	 * @param midHigh : Higher midium point of trapetzoidal
	 * @param max : End of trapetzoidal
	 */
	public MembershipFunctionTrapetzoidal(double min, double midLow, double midHigh, double max) {
		super();

		// Initialize
		this.parameters = new double[4];
		this.parameters[0] = min;
		this.parameters[1] = midLow;
		this.parameters[2] = midHigh;
		this.parameters[3] = max;

		// Check parameters
		StringBuffer errors = new StringBuffer();
		if( !checkParamters(errors) ) throw new RuntimeException(errors.toString());
	}

	@Override
	public boolean checkParamters(StringBuffer errors) {
		boolean ok = true;

		if( parameters[0] > parameters[1] ) {
			ok = false;
			if( errors != null ) errors.append("Parameter midLow is out of range (should stisfy: min <= midLow): " + parameters[0] + " > " + parameters[1] + "\n");
		}

		if( parameters[1] > parameters[2] ) {
			ok = false;
			if( errors != null ) errors.append("Parameter midHigh is out of range (should stisfy: midLow <= midHigh): " + parameters[1] + " > " + parameters[2] + "\n");
		}

		if( parameters[2] > parameters[3] ) {
			ok = false;
			if( errors != null ) errors.append("Parameter max is out of range (should stisfy: midHigh <= max): " + parameters[2] + " > " + parameters[3] + "\n");
		}

		return ok;
	}

	@Override
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;
		universeMin = parameters[0];
		universeMax = parameters[3];
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	public double membership(double in) {
		// Out of range => 0
		if( (in < parameters[0]) || (in > parameters[3]) ) return 0;
		
		// Between 'midLow' and 'midHigh' => 1
		if( (in >= parameters[1]) && (in <= parameters[2]) ) return 1;
		
		// Between 'min' and 'midLow'
		if( in < parameters[1] ) return ((in - parameters[0]) / (parameters[1] - parameters[0]));

		// Between 'midHigh' and 'max'
		return 1 - ((in - parameters[2]) / (parameters[3] - parameters[2]));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName() + " : " + parameters[0] + " , " + parameters[1] + " , " + parameters[2] + " , " + parameters[3];
	}

	/** FCL representation */
	public String toStringFCL() {
		return "TRAPE " + parameters[0] + " " + parameters[1] + " " + parameters[2] + " " + parameters[3];
	}
}
