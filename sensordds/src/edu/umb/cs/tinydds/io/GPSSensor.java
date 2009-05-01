/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.io;

import edu.umb.cs.tinydds.aggregate.Geometry.Rectangle2D;
import edu.umb.cs.tinydds.aggregate.Geometry.Circle;
import edu.umb.cs.tinydds.aggregate.Geometry;
import java.util.Date;
import java.util.Random;

/**
 * Simulates a simple GPS sensor.  There is no such off the shelf board in
 * sunspot, but if and when a GPS board is added it can be encapsulated in this
 * class.
 *
 * @author francesco
 */
public class GPSSensor {

    private double lat;
    private double lon;
    private double elev;
    private Date startTime;
    private Random generator;


    public static final double MIN_LAT = -90;  // 90Deg South of Equator
    public  static final double MAX_LAT = 90;   // 90Deg North of Equator
    public  static final double MIN_LON = -180; // 180Deg West of Greenwich
    public  static final double MAX_LON = 180;  // 180Deg East of Greenwich
    private final int TICKS = 100;


    private long seed;    // Seed for random number generator
    private boolean adHoc; // True if sensor is allowed to move, else false
    private Rectangle2D sandbox;
    private Velocity velocity;

    public GPSSensor(){
        startTime = new Date();
        // This constructor to encapsulate the real GPS in the future
    }

    public GPSSensor(Rectangle2D sandbox, boolean adHoc){
        this.adHoc = adHoc;
        startTime = new Date();
        seed = startTime.getTime();
        generator = new Random();
        generator.setSeed(seed);
        this.sandbox = sandbox; // No checking that the rectagle is legit.
        setRandomPosition();

        if(adHoc){
            velocity = new Velocity();
        }
    }

    public GPSSensor(Circle sandbox) {
        // todo: implement circular constraint
    }

    public double getLatitude(){
        return lat;
    }

    public double getLongitude(){
        return lon;
    }

    public double getElevation(){
        return 0;
    }
    
    private void setRandomPosition(){
        elev = 0; // 2D only for now
        lon = sandbox.getTopLeftCorner().getX() +
                sandbox.width() * generator.nextDouble();
        lat = sandbox.getBottomRightCorner().getY() +
                sandbox.height() * generator.nextDouble();
    }

    /**
     *
     * @author francesco
     */
    private class Velocity{
       public static final int SECOND = 1000;
       public static final int MINUTE = 60* SECOND;
       public static final int HOUR = 60* MINUTE;


       private double xVelocity; // in decimal degrees per hour
       private double yVelocity; // in decimal degrees per hour

       protected Velocity(){
           setRandomVelocity();
       }

       protected Velocity(double xVelocity, double yVelocity){
           this.xVelocity = xVelocity;
           this.yVelocity = yVelocity;
       }

       protected void setRandomVelocity(){
           xVelocity = sandbox.width() * (generator.nextDouble()-0.5)/TICKS;
           yVelocity = sandbox.height() * (generator.nextDouble()-0.5)/TICKS;
       }

       /**
        * Calculate the position of the node based on the initial position and
        * the velocity.
        */
       protected void advance(){
           Date now = new Date();
           long milliDiff = now.getTime() - startTime.getTime();
           double hours = milliDiff / HOUR;
           
           // implement bouncing - don't let nodes exit the sandbox
           double deltaX = hours * yVelocity;
           double deltaY = hours * xVelocity;
           
           lat = ((lat + deltaY) >= sandbox.getBottomRightCorner().getY()) &&
                 ((lat + deltaY) <= sandbox.getTopLeftCorner().getY()) ?
                 lat + deltaY : lat - deltaY;
           lon = ((lon + deltaX) >= sandbox.getTopLeftCorner().getX()) &&
                 ((lon + deltaX)>= sandbox.getBottomRightCorner().getX()) ?
                 lon + deltaX : lon - deltaX;
       }
    }

    public static void main(String[] args){
        Geometry geom = new Geometry();
        Geometry.Rectangle2D box = geom.new Rectangle2D(-70, 43, -69, 42);

        GPSSensor gps = new GPSSensor(box, false);
        System.out.println("Top left (lat, lon) (" + box.getTopLeftCorner().getY() +
                           "," + box.getTopLeftCorner().getX() + ")");
        System.out.println("Bottom right (lat, lon)(" + box.getBottomRightCorner().getY() +
                           "," + box.getBottomRightCorner().getX() + ")");
        System.out.println("Random Latitude is: " + gps.getLatitude());
        System.out.println("Random Longitude is: " + gps.getLongitude());
        System.out.println("Random elevation is: " + gps.getElevation());
    }
}
