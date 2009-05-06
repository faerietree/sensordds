/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.io;

import edu.umb.cs.tinydds.utils.Geometry.Rectangle2D;
import edu.umb.cs.tinydds.utils.Geometry.Circle;
import edu.umb.cs.tinydds.utils.Geometry;
import edu.umb.cs.tinydds.utils.Geometry.Polygon;
import edu.umb.cs.tinydds.utils.Logger;
import java.util.Date;
import java.util.Random;

/**
 * Simulates a simple GPS sensor. 
 *
 * @author francesco
 */
public class SimulatedGPS implements GPS {

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
    private Rectangle2D rect2dSandbox;
    private Circle circle2dSandbox;
    private Polygon poly2dSandbox;
    private Velocity velocity;
    private static SimulatedGPS GPS;
    private Logger logger = new Logger("SimulatedGPS");


    private static Geometry BOX = new Geometry().new Rectangle2D(-70, 43, -69, 42);
    //gps = new SimulatedGPS(box, false); // false means node is fixed


    public static SimulatedGPS getInstance(){
        if(GPS == null){
            GPS = new SimulatedGPS(BOX, false);
        }
        return GPS;
    }

    public static void configure(Geometry sandbox, boolean adHoc){
        BOX = sandbox;
    }

    private SimulatedGPS(Geometry sandbox, boolean adHoc){
        this.adHoc = adHoc;
        startTime = new Date();
        seed = startTime.getTime();
        generator = new Random();
        generator.setSeed(seed);
        if(sandbox instanceof Rectangle2D)
            logger.log(Logger.INFO, "Sandbox is a Rectangle");
            this.rect2dSandbox = (Rectangle2D) sandbox; // No checking that the rectagle is legit.
        if(sandbox instanceof Circle)
            logger.log(Logger.INFO, "Sandbox is a Circle");
            this.circle2dSandbox = (Circle) sandbox; // No checking that the circle is legit.
        if(sandbox instanceof Polygon)
            logger.log(Logger.INFO, "Sandbox is a Polygon");
            this.poly2dSandbox = (Polygon) sandbox; // No checking that the polygon is legit.
        setRandomPosition();

        if(adHoc){
            velocity = new Velocity();
        }
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
        lon = rect2dSandbox.getTopLeftCorner().getX() +
                rect2dSandbox.width() * generator.nextDouble();
        lat = rect2dSandbox.getBottomRightCorner().getY() +
                rect2dSandbox.height() * generator.nextDouble();
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
           xVelocity = rect2dSandbox.width() * (generator.nextDouble()-0.5)/TICKS;
           yVelocity = rect2dSandbox.height() * (generator.nextDouble()-0.5)/TICKS;
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
           
           lat = ((lat + deltaY) >= rect2dSandbox.getBottomRightCorner().getY()) &&
                 ((lat + deltaY) <= rect2dSandbox.getTopLeftCorner().getY()) ?
                 lat + deltaY : lat - deltaY;
           lon = ((lon + deltaX) >= rect2dSandbox.getTopLeftCorner().getX()) &&
                 ((lon + deltaX)>= rect2dSandbox.getBottomRightCorner().getX()) ?
                 lon + deltaX : lon - deltaX;
       }
    }

    public static void main(String[] args){
        Geometry geom = new Geometry();
        Geometry.Rectangle2D box = geom.new Rectangle2D(-70, 43, -69, 42);

        SimulatedGPS gps = new SimulatedGPS(box, false);
        System.out.println("Top left (lat, lon) (" + box.getTopLeftCorner().getY() +
                           "," + box.getTopLeftCorner().getX() + ")");
        System.out.println("Bottom right (lat, lon)(" + box.getBottomRightCorner().getY() +
                           "," + box.getBottomRightCorner().getX() + ")");
        System.out.println("Random Latitude is: " + gps.getLatitude());
        System.out.println("Random Longitude is: " + gps.getLongitude());
        System.out.println("Random elevation is: " + gps.getElevation());
    }
}
