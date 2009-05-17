/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umb.cs.tinydds.utils;

/**
 *
 * @author francesco
 */
public interface GlobalConfiguration {
    // Select your configuration here, other variables will modify accordingly
    // NOTE: ONLY ONE CONFIGURATION AT A TIME CAN BE TRUE
    public final static boolean IS_STATIC_CLUSTERING = true;
    public final static boolean IS_DYNAMIC_CLUSTERING = false;
    public final static boolean IS_DEFAULT = false;


    // Also select the debugging settings
    public static final boolean DEBUG = true;
    public static final int LIGHT = 1;
    public static final int MEDIUM = 2;
    public static final int FULL = 3;

    public static final int DBUG_LVL = LIGHT;

    // No changes below this line
    //------------------------------------------------------------------------
    public final static boolean CLUSTERING = IS_STATIC_CLUSTERING ||
                                             IS_DYNAMIC_CLUSTERING;
    public final static boolean DIST_ENFORCED = IS_STATIC_CLUSTERING ||
                                                IS_DYNAMIC_CLUSTERING;
    public final static double RANGE = IS_STATIC_CLUSTERING ? 0.8 :
                                       IS_DYNAMIC_CLUSTERING? 0.5 : 0;
    public final static boolean CH_CH_COMM_ENFORCED = IS_STATIC_CLUSTERING ||
                                                      IS_DYNAMIC_CLUSTERING;
    public final static Geometry DEFAULT_GEOMETRY = new Geometry().
                                 new Rectangle2D(-70, 43, -69, 42);

    public final static int PING_INTERVAL = 15;
    public final static int EXPIRE_INTERVAL = 60;
    public final static int PING_DELAY = 1;
    public final static int DISP_DELAY = PING_DELAY + 4;
    public final static int EXP_DELAY = DISP_DELAY + 2;
    public final static int ONE_SECOND = 1000;
    public final static long NOT_ASSIGNED = -1; // For node addresses

}
