/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

import edu.umb.cs.tinydds.utils.Geometry;

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
     *
	 * @param function
	 * @param phenom
	 * @param geom
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public double getAggregation(String function, String phenom, Geometry geom,
            long startTime, long endTime);

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
