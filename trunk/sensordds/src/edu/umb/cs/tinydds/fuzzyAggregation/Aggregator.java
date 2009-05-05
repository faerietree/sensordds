/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

	
	public Vector getPhenomAggregation(String function, String phenom);

        
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
