/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.cluster;

/**
 *
 * @author francesco
 */
public class Node {
    private Long nodeID;
    private double latitude;
    private double longitude;
    private double elevation;

    public Node(Long nodeID, double latitude, double longitude, double elevation) {
        this.nodeID = nodeID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Long getNodeID() {
        return nodeID;
    }

//    public int hashCode(){
//        return 1;
//    }
//
//    public boolean equals(Object b){
//        return false;
//    }
}
