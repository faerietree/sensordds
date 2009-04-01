package net.sourceforge.jFuzzyLogic.rule;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.defuzzifier.Defuzzifier;
import net.sourceforge.jFuzzyLogic.defuzzifier.DefuzzifierContinuous;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionDiscrete;
import net.sourceforge.jFuzzyLogic.plot.DialogGraph;
import net.sourceforge.jFuzzyLogic.plot.PlotWindow;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Fuzzy variable
 * @author pcingola@users.sourceforge.net
 */
public class Variable implements Comparable<Variable> {

	//-------------------------------------------------------------------------
	// Variables
	//-------------------------------------------------------------------------

	/** Default value, when no change */
	double defaultValue;
	/** Defuzzifier class */
	Defuzzifier defuzzifier;
	/** Latest defuzzified value */
	double latestDefuzzifiedValue;
	/** Terms for this variable */
	HashMap<String, LinguisticTerm> linguisticTerms;
	/** Variable name */
	String name;
	/** Universe max (range max) */
	double universeMax;
	/** Universe minimum (range minimum) */
	double universeMin;
	/** Variable's value */
	double value;

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	/**
	 * Default constructor 
	 * @param name : Variable's name
	 */
	public Variable(String name) {
		if( name == null ) throw new RuntimeException("Variable's name can't be null");
		this.name = name;
		linguisticTerms = new HashMap<String, LinguisticTerm>();
		defaultValue = Double.NaN;
		universeMin = Double.NaN;
		universeMax = Double.NaN;
		value = Double.NaN;
		reset(); // Reset values
	}

	/**
	 * Default constructor 
	 * @param name : Variable's name
	 */
	public Variable(String name, double universeMin, double universeMax) {
		if( name == null ) throw new RuntimeException("Variable's name can't be null");
		if( universeMax < universeMin ) throw new RuntimeException("Parameter error in variable \'" + name + "\' universeMax < universeMin");
		this.name = name;
		linguisticTerms = new HashMap<String, LinguisticTerm>();
		this.universeMin = universeMin;
		this.universeMax = universeMax;
		value = Double.NaN;
		reset(); // Reset values
	}

	/**
	 * Adds a termName to this variable
	 * @param linguisticTerm : Linguistic term to add 
	 * @return this variable
	 */
	public Variable add(LinguisticTerm linguisticTerm) {
		linguisticTerms.put(linguisticTerm.getTermName(), linguisticTerm);
		return this;
	}

	/**
	 * Adds a termName to this variable
	 * @param termName : RuleTerm name 
	 * @param membershipFunction : membershipFunction for this termName 
	 * @return this variable
	 */
	public Variable add(String termName, MembershipFunction membershipFunction) {
		this.add(new LinguisticTerm(termName, membershipFunction));
		return this;
	}

	/**
	 * Create a chart showing each linguistic term
	 * @param showIt : If true, plot is displayed
	 */
	public JFreeChart chart(boolean showIt) {
		boolean discrete = true;
		boolean plotDefuzz = false;

		// Sanity check
		if( Double.isNaN(universeMin) || Double.isInfinite(universeMax) ) estimateUniverse();
		int numberOfPoints = PlotWindow.DEFAULT_CHART_NUMBER_OF_POINTS;
		double step = (universeMax - universeMin) / (numberOfPoints);

		// Create a data set
		XYSeriesCollection xyDataset = new XYSeriesCollection();

		//---
		// Current value
		//---
		if( !Double.isNaN(value) ) {
			XYSeries seriesValue = new XYSeries("Value");
			seriesValue.add(value - 2 * step, 0);
			seriesValue.add(value - step, 1);
			seriesValue.add(value, 1);
			seriesValue.add(value + step, 1);
			seriesValue.add(value + 2 * step, 0);
			xyDataset.addSeries(seriesValue);
		}

		//---
		// Plot deffuzyfier values (if any)
		//---
		if( (defuzzifier != null) && (defuzzifier instanceof DefuzzifierContinuous) ) {
			DefuzzifierContinuous def = (DefuzzifierContinuous) defuzzifier;
			String title = String.format("%s:%2.2f (%s)", name, latestDefuzzifiedValue, defuzzifier.getName());
			XYSeries series = new XYSeries(title);
			double values[] = def.getValues();
			numberOfPoints = values.length;
			double xx = def.getMin();
			step = (def.getMax() - def.getMin()) / (numberOfPoints);
			for( int i = 0; i < numberOfPoints; i++, xx += step )
				series.add(xx, values[i]);

			// Add serie to dataSet
			xyDataset.addSeries(series);
			plotDefuzz = true;
		}

		//---
		// Plot each linguistic term (create an xyDataSet and append it)
		//---
		int j = 0;
		for( Iterator<String> it = iteratorLinguisticTermNames(); it.hasNext(); j++ ) {
			// Add this linguistic term to dataset
			String termName = (String) it.next();
			MembershipFunction membershipFunction = getLinguisticTerm(termName).getMembershipFunction();
			discrete &= membershipFunction.isDiscrete();

			// Create a series and add points
			XYSeries series = new XYSeries(termName);
			if( membershipFunction.isDiscrete() ) {
				// Discrete case: Evaluate membership function and add points to dataset
				MembershipFunctionDiscrete membershipFunctionDiscrete = (MembershipFunctionDiscrete) membershipFunction;
				numberOfPoints = membershipFunctionDiscrete.size();
				for( int i = 0; i < numberOfPoints; i++ )
					series.add(membershipFunctionDiscrete.valueX(i), membershipFunctionDiscrete.membership(i));
			} else {
				// Continuous case: Add every membershipfunction's point 
				numberOfPoints = PlotWindow.DEFAULT_CHART_NUMBER_OF_POINTS;
				double xx = universeMin;
				for( int i = 0; i < numberOfPoints; i++, xx += step )
					series.add(xx, membershipFunction.membership(xx));
			}

			// Add serie to dataSet
			xyDataset.addSeries(series);
		}

		// Create chart and show it
		JFreeChart chart;
		if( !discrete ) chart = ChartFactory.createXYAreaChart(name, "x", "Membership", xyDataset, PlotOrientation.VERTICAL, true, true, false);
		else chart = ChartFactory.createScatterPlot(name, "x", "Membership", xyDataset, PlotOrientation.VERTICAL, true, true, false);

		// Set 'Value' color to BLACK 
		XYPlot plot = (XYPlot) chart.getXYPlot();
		plot.getRenderer().setSeriesPaint(0, Color.BLACK);
		// Set 'deffuzifier' color to GREY 
		if( plotDefuzz ) plot.getRenderer().setSeriesPaint(1, Color.gray);

		if( showIt ) PlotWindow.showIt(name, chart);

		return chart;
	}

	/**
	 * Create a chart showing defuzzifier 
	 * @param showIt : If true, plot is displayed
	 */
	public JFreeChart chartDefuzzifier(boolean showIt) {
		String title = String.format("%s:%2.2f (%s)", name, latestDefuzzifiedValue, defuzzifier.getName());
		JFreeChart chart = defuzzifier.chart(title, false);
		if( showIt ) DialogGraph.execute(chart);
		return chart;
	}

	public int compareTo(Variable anotherVariable) {
		if( anotherVariable == null ) return 1;
		Variable var = anotherVariable;
		return name.compareTo(var.getName());
	}

	/** 
	 * Defuzzify this (output) variable
	 * Set defuzzufied values to 'value' and 'latestDefuzzifiedValue'
	 */
	public double defuzzify() {
		double ldv = defuzzifier.defuzzify();

		// Only assign valid defuzzifier's result
		if( !Double.isNaN(ldv) ) value = latestDefuzzifiedValue = ldv;

		return latestDefuzzifiedValue;
	}

	/** Estimate universe */
	public void estimateUniverse() {
		// Are universeMin and universeMax already setted? => nothing to do
		if( (!Double.isNaN(universeMin)) && (!Double.isNaN(universeMax)) ) return;

		// Calculate max / min on every membership function
		double umin = Double.POSITIVE_INFINITY;
		double umax = Double.NEGATIVE_INFINITY;
		for( Iterator<String> it = iteratorLinguisticTermNames(); it.hasNext(); ) {
			String lingTerm = (String) it.next();
			MembershipFunction membershipFunction = getMembershipFunction(lingTerm);
			membershipFunction.estimateUniverse();

			umin = Math.min(membershipFunction.getUniverseMin(), umin);
			umax = Math.max(membershipFunction.getUniverseMax(), umax);
		}

		// Set parameters (if not setted)
		if( Double.isNaN(universeMin) ) universeMin = umin;
		if( Double.isNaN(universeMax) ) universeMax = umax;
	}

	public double getDefaultValue() {
		return defaultValue;
	}

	public Defuzzifier getDefuzzifier() {
		return defuzzifier;
	}

	public double getLatestDefuzzifiedValue() {
		return latestDefuzzifiedValue;
	}

	/** Get 'termName' linguistic term */
	public LinguisticTerm getLinguisticTerm(String termName) {
		LinguisticTerm lt = linguisticTerms.get(termName);
		if( lt == null ) throw new RuntimeException("No such linguistic term: '" + termName + "'");
		return lt;
	}

	public HashMap<String, LinguisticTerm> getLinguisticTerms() {
		return linguisticTerms;
	}

	/** Evaluate 'termName' membershipfunction at 'value' */
	public double getMembership(String termName) {
		MembershipFunction mf = getMembershipFunction(termName);
		if( mf == null ) throw new RuntimeException("No such termName: \"" + termName + "\"");
		return mf.membership(value);
	}

	/** Get 'termName' membershipfunction */
	public MembershipFunction getMembershipFunction(String termName) {
		LinguisticTerm lt = linguisticTerms.get(termName);
		if( lt == null ) throw new RuntimeException("No such linguistic term: '" + termName + "'");
		return lt.getMembershipFunction();
	}

	public String getName() {
		return name;
	}

	public double getUniverseMax() {
		return universeMax;
	}

	public double getUniverseMin() {
		return universeMin;
	}

	public double getValue() {
		return value;
	}

	/** Return 'true' if this is an output variable */
	public boolean isOutputVarable() {
		return (defuzzifier != null); // Only output variables have defuzzyfiers
	}

	/** Get a 'linguisticTerms' iterator (by name) */
	public Iterator<String> iteratorLinguisticTermNames() {
		Set<String> keySet = linguisticTerms.keySet();
		return keySet.iterator();
	}

	/** Get a 'linguisticTerms' iterator (by name) */
	public Iterator<String> iteratorLinguisticTermNamesSorted() {
		ArrayList<String> al = new ArrayList<String>(linguisticTerms.keySet());
		Collections.sort(al);
		return al.iterator();
	}

	/** Reset defuzzifier (if any) */
	public void reset() {
		if( defuzzifier != null ) {
			defuzzifier.reset();
			// Set default value for output variables (if any default value was defined)
			if( !Double.isNaN(defaultValue) ) value = defaultValue;
		}
		latestDefuzzifiedValue = defaultValue;
	}

	public void setDefaultValue(double defualtValue) {
		defaultValue = defualtValue;
	}

	public void setDefuzzifier(Defuzzifier deffuzifier) {
		defuzzifier = deffuzifier;
	}

	public void setLatestDefuzzifiedValue(double latestDefuzzifiedValue) {
		this.latestDefuzzifiedValue = latestDefuzzifiedValue;
	}

	public void setLinguisticTerms(HashMap<String, LinguisticTerm> linguisticTerms) {
		this.linguisticTerms = linguisticTerms;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUniverseMax(double universeMax) {
		this.universeMax = universeMax;
	}

	public void setUniverseMin(double universeMin) {
		this.universeMin = universeMin;
	}

	public void setValue(double value) {
		if( (value < universeMin) || (value > universeMax) ) Gpr.warn("Value out of range?. Variable: '" + name + "', Universe: [" + universeMin + ", " + universeMax + "], Value: " + value);
		this.value = value;
	}

	/**
	 * Printable string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = name + " : \n";

		// Show defuzifier for "output" variables, value for "input" variables
		if( defuzzifier != null ) str += "\tDefuzzifier : " + defuzzifier.toString() + "\n\tLatest defuzzified value: " + latestDefuzzifiedValue + "\n";
		else str += "\tValue: " + value + "\n";

		if( !Double.isNaN(defaultValue) ) str += "\tDefault value: " + defaultValue + "\n";

		// Show each 'termName' and it's membership function
		for( Iterator<String> it = iteratorLinguisticTermNames(); it.hasNext(); ) {
			String key = (String) it.next();
			LinguisticTerm linguisticTerm = linguisticTerms.get(key);
			str += "\t" + linguisticTerm.toString(value) + "\n";
		}
		return str;
	}
}
