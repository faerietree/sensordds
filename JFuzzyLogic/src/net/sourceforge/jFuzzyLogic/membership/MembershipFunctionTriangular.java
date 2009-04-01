package net.sourceforge.jFuzzyLogic.membership;

/**
 * Triangular membership function
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionTriangular extends MembershipFunctionContinuous {

	/**
	 * Constructor 
	 * @param min : Begining of triangular function
	 * @param mid : Midium of triangular function
	 * @param max : End of triangular function
	 */
	public MembershipFunctionTriangular(double min, double mid, double max) {
		super();

		// Initialize
		this.parameters = new double[3];
		this.parameters[0] = min;
		this.parameters[1] = mid;
		this.parameters[2] = max;

		// Check parameters
		StringBuffer errors = new StringBuffer();
		if( !checkParamters(errors) ) throw new RuntimeException(errors.toString());
	}

	@Override
	public boolean checkParamters(StringBuffer errors) {
		boolean ok = true;

		if( parameters[0] > parameters[1] ) {
			ok = false;
			if( errors != null ) errors.append("Parameter mid is out of range (should stisfy: min <= mid): " + parameters[0] + " > " + parameters[1] + "\n");
		}

		if( parameters[1] > parameters[2] ) {
			ok = false;
			if( errors != null ) errors.append("Parameter max is out of range (should stisfy: mid <= max): " + parameters[1] + " > " + parameters[2] + "\n");
		}

		return ok;
	}

	@Override
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;
		universeMin = this.parameters[0];
		universeMax = this.parameters[2];
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	public double membership(double in) {
		// Outside range? => membership is 0
		if( (in < this.parameters[0]) || (in > this.parameters[2]) ) return 0;
		
		// Middle point of the triangle? => membership is 1.0
		// Note: This comparison is useful when one of the extremes and the 'mid' point are the same
		// 			E.g.:	TERM negSmall := TRIAN -0.4 0 0;		
		//					=> The membership value at 0 should be one, but the formula below gives 0
		if( in == this.parameters[1] ) return 1.0;
		
		// Value between 'min' and 'mid'
		if( in < this.parameters[1] ) return ((in - this.parameters[0]) / (this.parameters[1] - this.parameters[0]));

		// Value between 'mid' and 'max'
		return 1 - ((in - this.parameters[1]) / (this.parameters[2] - this.parameters[1]));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName() + " : " + this.parameters[0] + " , " + this.parameters[1] + " , " + this.parameters[2];
	}

	/** FCL representation */
	public String toStringFCL() {
		return "TRIAN " + parameters[0] + " " + parameters[1] + " " + parameters[2];
	}
}
