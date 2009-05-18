package edu.umb.cs.tinydds.fuzzyAggregation;

import edu.umb.cs.tinydds.utils.Geometry;
import java.util.Vector;

/**
 *
 * @author Matt Calder
 *
 */
public interface Aggregator {

	public final static String MAX = "MAX";
	public final static String MIN = "MIN";
	public final static String AVG = "AVG";
	public final static String STD_DEV = "STD_DEV";

	/**
	 * add a data point to the Aggregator.
	 *
	 *
	 * @param phenom
	 * @param geom
	 * @param timestamp
	 * @param value
	 */
	public void addData(String phenom, Geometry geom, long timestamp, double value);

	/**
         * This returns a Vector of size 3 with the elements in the following order
         * 0 - value of type Double or String (Notice the capitol 'D')
         * 1 - timestamp of type Long (Notice the capitol 'L')
         * 2 - geometry of type Geometry. (just make this null for now)
         * 
         * @param function
         * @param phenom
         * @return
         */
	public Vector getPhenomAggregation(String function, String phenom);

        /**
         * This returns a Vector of size 3 with the elements in the following order
         * 0 - value of type Double (Notice the capitol 'D')
         * 1 - timestamp of type Long (Notice the capitol 'L')
         * 2 - geometry of type Geometry. (just make this null for now)
         * 
         * @param function
         * @param phenom
         * @return
         */
        public Vector getTemporalAggregation(String function, String phenom);
	
        /**
	 *
	 * @param function
	 * @param phenom
	 */
	public void registerAggregation(String function, String phenom);

	/**
	 *
	 * @param function
	 * @param phenom
	 */
	public void unregisterAggregation(String function, String phenom);
}
