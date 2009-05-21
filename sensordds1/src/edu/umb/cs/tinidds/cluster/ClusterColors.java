/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umb.cs.tinidds.cluster;

import com.sun.spot.sensorboard.peripheral.LEDColor;

/**
 *
 * @author francesco
 */
public final class ClusterColors {

    public static final LEDColor[] colors = {LEDColor.GREEN, 
                                             LEDColor.BLUE,
                                             LEDColor.ORANGE,
                                             LEDColor.TURQUOISE,
                                             LEDColor.CHARTREUSE,
                                             LEDColor.CYAN,
                                             LEDColor.MAGENTA,
                                             LEDColor.MAUVE,
                                             LEDColor.PUCE,
                                             LEDColor.RED,
                                             LEDColor.WHITE,
                                             LEDColor.YELLOW};

    private static int nextColor = 2;  // Green and Blue are reserved for BS and Nodes

    public static synchronized LEDColor nextColor() throws OutOfColorsException{
        if(nextColor == colors.length)
            throw new OutOfColorsException("No more colors");
        return colors[nextColor++];
    }

    /**
     * If the position is out of bound it will cause a runtime exception
     *
     * @param position
     * @return
     */
    public static LEDColor getColor(int position){
        return colors[position];
    }

    public static int getPosition(){
        return nextColor;
    }
}
