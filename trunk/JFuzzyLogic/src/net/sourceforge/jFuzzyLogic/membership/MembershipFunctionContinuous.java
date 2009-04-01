package net.sourceforge.jFuzzyLogic.membership;

import net.sourceforge.jFuzzyLogic.plot.PlotWindow;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * Base continuous membership function
 * @author pcingola@users.sourceforge.net
 */
public abstract class MembershipFunctionContinuous extends MembershipFunction {

	/**
	 * Default constructor 
	 */
	public MembershipFunctionContinuous() {
		super();
		discrete = false;
	}

	/**
	 * Create a membership function chart 
	 * @param title : Title to show (if null => show membership function name)
	 * @param showIt : If true, plot is displayed
	 */
	public JFreeChart chart(String title, boolean showIt) {
		int numberOfPoints = PlotWindow.DEFAULT_CHART_NUMBER_OF_POINTS;

		if( title == null ) title = getName();
		
		// Sanity check
		if( Double.isNaN(universeMin) || Double.isInfinite(universeMax) ) estimateUniverse();

		// Evaluate membership function and add points to dataset
		XYSeries series = new XYSeries(title);
		double xx = universeMin;
		double step = (universeMax - universeMin) / ((double) numberOfPoints);
		for( int i = 0; i < numberOfPoints; i++, xx += step ) {
			series.add(xx, membership(xx));
		}
		XYDataset xyDataset = new XYSeriesCollection(series);

		// Create plot and show it
		JFreeChart chart = ChartFactory.createXYLineChart(title, "x", "Membership", xyDataset, PlotOrientation.VERTICAL, false, true, false );
		if( showIt ) PlotWindow.showIt(title, chart);
		
		return chart;
	}
}
