package net.sourceforge.jFuzzyLogic.membership;

/**
 * Piece-wise linear membership function
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionPieceWiseLinear extends MembershipFunctionContinuous {

	/** Piece wise linear function values x */
	double x[];
	/** Piece wise linear function values y */
	double y[];

	/**
	 * Default constructor 
	 * @param x [] : x points array
	 * @param y [] : y points array
	 * A piecewise linear function is defined by 'n' points:
	 * 		(x_1,y_1) , (x_2,y2) , ... (x_n,y_n)
	 * ordered by x[] (increasing)
	 * See also 'membership()' for a precise definition.
	 */
	public MembershipFunctionPieceWiseLinear(double x[], double y[]) {
		super();

		// Check parameters
		if( x == null ) throw new RuntimeException("Parameter x[] can't be null");
		if( y == null ) throw new RuntimeException("Parameter x[] can't be null");
		if( x.length > y.length ) throw new RuntimeException("Array size differ");
		if( x.length < 1 ) throw new RuntimeException("Array size is 0");

		// Initialize (for the sake of clarity we keep parameters as 2 arrays 'x[], y[]'
		this.x = x;
		this.y = y;

		// Check parameters
		StringBuffer errors = new StringBuffer();
		if( !checkParamters(errors) ) throw new RuntimeException(errors.toString());
	}

	@Override
	public boolean checkParamters(StringBuffer errors) {
		boolean ok = true;

		if( x.length > 1 ) {
			for( int i = 0; i < x.length; i++ ) {
				if( (i > 0) && (x[i - 1] > x[i]) ) {
					ok = false;
					if( errors != null ) errors.append("Array not sorted: x[" + (i - 1) + "] = " + x[i - 1] + " , x[" + i + "] = " + x[i] + "\n");
				}

				if( (y[i] < 0) || (y[i] > 1) ) {
					ok = false;
					if( errors != null ) errors.append("Membership funcion out of range: y[" + i + "] = " + y[i] + " (should be in range [0,1]\n");
				}
			}
		}

		return ok;
	}

	@Override
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;
		universeMin = x[0];
		universeMax = x[x.length - 1];
	}

	/** Need to override this method (we store parameters differently in this function) */
	public double getParameter(int i) {
		int j = i / 2;
		if( (i % 2) == 0 ) return x[j];
		return y[j];

	}

	/** Need to override this method (we store parameters differently in this function) */
	public int getParametersLength() {
		return (x != null ? 2 * x.length : 0);
	}

	/**
	 * Membership funcion is defined as:
	 * 		membership(x) = y[0]											if x <= x[0]
	 * 		membership(x) = y[n]											if x >= x[n]  (where n = x.length)
	 * 		membership(x) = y[i - 1] + (y[i] - y[i - 1]) / (in - x[i])		if x[i-1] < x <= x[i]
	 * 	
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	public double membership(double in) {
		int i, len = x.length;
		if( in <= x[0] ) return y[0];
		if( in > x[len - 1] ) return y[len - 1];
		for( i = 1; i < len; i++ ) {
			if( in <= x[i] ) return y[i - 1] + (y[i] - y[i - 1]) * ((in - x[i - 1]) / (x[i] - x[i - 1]));
		}
		if( Double.isNaN(in) ) return Double.NaN;
		throw new RuntimeException("Error calculating membership! This should never happen! (in = " + in + ")");
	}

	/** Need to override this method (we store parameters differently in this function) */
	public void setParameter(int i, double value) {
		int j = i / 2;
		if( (i % 2) == 0 ) x[j] = value;
		else y[j] = value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer str = new StringBuffer(getName() + " : ");
		for( int i = 0; i < x.length; i++ ) {
			str.append("(" + x[i] + ", " + y[i] + ") ");
			if( i < (x.length - 1) ) str.append(", ");
		}
		str.append(";");
		return str.toString();
	}
	
	/** FCL representation */
	public String toStringFCL() {
		String str = " ";
		for( int i = 0; i < x.length; i++ ) {
			str += "(" + x[i] + ", " + y[i] + ") ";
		}
		return str;
	}
}
