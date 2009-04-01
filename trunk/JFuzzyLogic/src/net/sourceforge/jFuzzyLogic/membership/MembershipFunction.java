package net.sourceforge.jFuzzyLogic.membership;

import net.sourceforge.jFuzzyLogic.FIS;

import org.jfree.chart.JFreeChart;

/**
 * Base membership function
 * @author pcingola@users.sourceforge.net
 */
public abstract class MembershipFunction {

	/** Debug mode for this class? */
	public static boolean debug = FIS.debug;

	//-------------------------------------------------------------------------
	// Variables
	//-------------------------------------------------------------------------

	boolean discrete;
	/** Function's parameters */
	double parameters[];
	/** Universe max (range max) */
	double universeMax;
	/** Universe min (range min) */
	double universeMin;

	//-------------------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------------------

	/** Default Constructor */
	MembershipFunction() {
		universeMax = universeMin = Double.NaN;
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------
	
	/**
	 * Create a membership function chart 
	 * @param title : Title to show (if null => show membership function name)
	 * @param showIt : If true, plot is displayed
	 */
	public abstract JFreeChart chart(String title, boolean showIt);

	public abstract boolean checkParamters(StringBuffer errors);

	/** Try to guess the universe (if not setted) */
	public abstract void estimateUniverse();

	/** Short name */
	public String getName() {
		String str = this.getClass().getName();
		String mfStr = "MembershipFunction";
		int ind = str.lastIndexOf('.');
		if( ind >= 0 ) {
			str = str.substring(ind + 1);
			if( str.startsWith(mfStr) ) str = str.substring(mfStr.length());
		}
		return str;
	}

	public double getParameter(int i) {
		return parameters[i];
	}

	public int getParametersLength() {
		return (parameters != null ? parameters.length : 0);
	}

	public double getUniverseMax() {
		return universeMax;
	}

	public double getUniverseMin() {
		return universeMin;
	}

	public boolean isDiscrete() {
		return discrete;
	}

	/** 
	 * Get membership function's value.
	 * @param in : Variable's 'x' value
	 * Note: Output mu be in range [0,1] 
	 */
	public abstract double membership(double in);

	public void setDiscrete(boolean discrete) {
		this.discrete = discrete;
	}

	public void setParameter(int i, double value) {
		this.parameters[i] = value;
	}

	public void setUniverseMax(double universeMax) {
		this.universeMax = universeMax;
	}

	public void setUniverseMin(double universeMin) {
		this.universeMin = universeMin;
	}

	public String toString() {
		return getName();
	}

	public abstract String toStringFCL();
}
