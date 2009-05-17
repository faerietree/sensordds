/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.io;

/**
 *
 * @author francesco
 */
public interface GPS {

    double getElevation();

    double getLatitude();

    double getLongitude();

    public double getEuclidianDistFrom(double lat, double lon, double elev);
}
