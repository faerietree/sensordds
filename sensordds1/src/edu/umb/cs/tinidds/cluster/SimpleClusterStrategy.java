/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinidds.cluster;

import edu.umb.cs.tinydds.io.GPS;

/**
 * A very simple strategy: a cluster head accepts only up to 
 * <code>MAXMEMBERS</code> members.
 *
 * @author francesco
 */
public class SimpleClusterStrategy implements ClusterStrategy {
    private final int MAXMEMBERS = 2;

    public boolean acceptMember(GPS chGPS, double cmLat, double cmLon, double cmElev,
                                int memberCnt) {
        return memberCnt < MAXMEMBERS;
    }

    public boolean acceptMember(double chLat, double chLon, double chElev,
                                double cmLat, double cmLon, double cmElev,
                                int memberCnt) {
        return memberCnt < MAXMEMBERS;
    }

    public String describe() {
       return "Accepts members up to " + MAXMEMBERS;
    }

}
