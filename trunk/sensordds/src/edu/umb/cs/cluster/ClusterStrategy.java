/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.cluster;

import edu.umb.cs.tinydds.io.GPS;

/**
 *
 * @author francesco
 */
public interface ClusterStrategy {
    /**
     *
     * @param chGPS     GPS of clusterhead
     * @param cmLat     latitude of cluster member candidate
     * @param cmLon     longitude of cluster member candidate
     * @param cmElev    elevation of cluster member candidate
     * @param memberCnt number of cluster members candidate
     * @return          true if this candidate can join this cluster
     */
    public boolean acceptMember(GPS chGPS,
                                double cmLat,
                                double cmLon,
                                double cmElev,
                                int memberCnt);

    /**
     *
     * @return  A brief description of the strategy
     */
    public String describe();
}
