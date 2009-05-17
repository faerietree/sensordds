/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.cluster;

import edu.umb.cs.tinydds.io.GPS;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;

/**
 * A distance based strategy: If a node is in range, then it can join the cluster.
 *
 * @author francesco
 */
public class distClusterStrategy implements ClusterStrategy, GlobalConfiguration {

    public boolean acceptMember(GPS chGPS, double cmLat, double cmLon, double cmElev,
                                int memberCnt) {
       return chGPS.getEuclidianDistFrom(cmLat, cmLon, cmElev) < RANGE;
    }

    public String describe() {
        return "Accepts a member if within range.";
    }

}
