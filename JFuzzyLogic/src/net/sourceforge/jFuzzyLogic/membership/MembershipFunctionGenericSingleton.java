package net.sourceforge.jFuzzyLogic.membership;

import java.util.Iterator;

/**
 * Generic singleton membership function: Allows 'n' singletons (generic discrete membership function)
 * @author pcingola@users.sourceforge.net
 */
public class MembershipFunctionGenericSingleton extends MembershipFunctionDiscrete {

	/** Singleton function values x */
	double x[];
	/** Singleton function values y */
	double y[];

	/**
	 * Constructor for generin (N-values) 
	 * @param x : x[] values array
	 * @param y : y[] values array
	 */
	public MembershipFunctionGenericSingleton(double x[], double y[]) {
		discrete = true;

		// Check parameters
		if( x == null ) throw new RuntimeException("Parameter x[] can't be null");
		if( y == null ) throw new RuntimeException("Parameter x[] can't be null");
		if( x.length > y.length ) throw new RuntimeException("Array size differ");
		if( x.length < 1 ) throw new RuntimeException("Array size is 0");

		// Initialize
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
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete#iterator()
	 */
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {

			int i = 0;

			public boolean hasNext() {
				return (i < x.length);
			}

			public Double next() {
				return Double.valueOf(x[i]);
			}

			public void remove() {}
		};
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunction#membership(double)
	 */
	public double membership(double in) {
		int i, len = x.length;
		if( in <= x[0] ) return y[0];
		if( in > x[len - 1] ) return y[len - 1];
		for( i = 1; i < len; i++ )
			if( in == x[i] ) return y[i];
		return 0;
	}

	/**
	 * @see net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete#membership(int)
	 */
	public double membership(int index) {
		if( (index < 0) || (index > x.length) ) return 0;
		return y[index];
	}

	/** Need to override this method (we store parameters differently in this function) */
	public void setParameter(int i, double value) {
		int j = i / 2;
		if( (i % 2) == 0 ) x[j] = value;
		else y[j] = value;
	}

	/**
	 * Number of points in this discrete function (i.e. number of 'singletons')
	 */
	public int size() {
		return x.length;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer str = new StringBuffer(getName() + " : ");
		for( int i = 0; i < x.length; i++ ) {
			str.append("[" + x[i] + ", " + y[i] + "] ");
			if( i < (x.length - 1) ) str.append(", ");
		}
		str.append(";");
		return str.toString();
	}

	/** FCL representation */
	public String toStringFCL() {
		String str = "SINGLETONS ";
		for( int i = 0; i < x.length; i++ ) {
			str += "(" + x[i] + ", " + y[i] + ") ";
		}
		return str;
	}

	@Override
	public double valueX(int index) {
		return x[index];
	}
}
