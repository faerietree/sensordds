package net.sourceforge.jFuzzyLogic.membership;

import java.util.Iterator;

import net.sourceforge.jFuzzyLogic.plot.PlotWindow;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Base Discrete membership function
 * @author pcingola@users.sourceforge.net
 */
public abstract class MembershipFunctionDiscrete extends MembershipFunction {

	//-------------------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------------------

	public MembershipFunctionDiscrete() {
		super();
		discrete = true;
	}

	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------

	/**
	 * Create a membership function chart 
	 * @param title : Title to show (if null => show membership function name)
	 * @param showIt : If true, plot is displayed
	 */
	public JFreeChart chart(String title, boolean showIt) {
		int numberOfPoints = size();
		if( title == null ) title = getName();

		// Evaluate membership function and add points to dataset
		XYSeries series = new XYSeries(title);
		for( int i = 0; i < numberOfPoints; i++ )
			series.add(valueX(i), membership(i));
		XYDataset xyDataset = new XYSeriesCollection(series);

		// Create plot and show it
		JFreeChart chart = ChartFactory.createScatterPlot(title, "x", "Membership", xyDataset, PlotOrientation.VERTICAL, false, true, false);
		if( showIt ) PlotWindow.showIt(title, chart);

		return chart;
	}

	/**
	 * Create an iterator for every discrete value (x values)
	 * @return An iterator
	 */
	public abstract Iterator<Double> iterator();

	/** Memebership function for point number 'index' */
	public abstract double membership(int index);

	/** Number of points in this function */
	public abstract int size();

	/** Value 'x' for point number 'index' */
	public abstract double valueX(int index);

}
