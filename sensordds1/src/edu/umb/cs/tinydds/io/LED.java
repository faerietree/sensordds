/*$Id: LED.java,v 1.2 2008/08/29 20:26:44 pruet Exp $

Copyright (c) 2008 University of Massachusetts, Boston 
All rights reserved. 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
Neither the name of the University of Massachusetts, Boston  nor 
the names of its contributors may be used to endorse or promote products 
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OF
MASSACHUSETTS, BOSTON OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
 */

package edu.umb.cs.tinydds.io;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.sensorboard.peripheral.TriColorLED;
import edu.umb.cs.tinydds.utils.Logger;

/**
 *
 * @author pruet
 */
public class LED {

    protected static ITriColorLED realLED[] = null;
    protected static int r[] = null,  g[] = null,  b[] = null;
    protected static boolean on[] = null;
    Logger logger;

    public LED() {
        if (realLED != null) {
            return;
        }
        ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
        realLED = new TriColorLED[leds.length];
        r = new int[leds.length];
        g = new int[leds.length];
        b = new int[leds.length];
        on = new boolean[leds.length];
        for (int i = 0; i != leds.length; i++) {
            realLED[i] = leds[i];
            r[i] = 0;
            g[i] = 0;
            b[i] = 0;
            on[i] = false;
        }

    }

    public void setRGB(int i, int redRGB, int greenRGB, int blueRGB) {
        r[i] = redRGB;
        g[i] = greenRGB;
        b[i] = blueRGB;
        setOn(i, on[i]);
    }

    public void setColor(int i, LEDColor clr) {
        r[i] = clr.red();
        g[i] = clr.green();
        b[i] = clr.blue();
        setOn(i, on[i]);
    }

    public LEDColor getColor(int i) {
        return new LEDColor(r[i], g[i], b[i]);
    }

    public int getRed(int i) {
        return r[i];
    }

    public int getGreen(int i) {
        return g[i];
    }

    public int getBlue(int i) {
        return b[i];
    }

    public void setOn(int i) {
        setOn(i, true);
    }

    public void setOff(int i) {
        setOn(i, false);
    }

    public void setOn(int i, boolean on) {
        LED.on[i] = on;
        if (LED.on[i]) {
            realLED[i].setRGB(r[i], g[i], b[i]);
            realLED[i].setOn();
        } else {
            realLED[i].setOff();
        }
    }

    public boolean isOn(int i) {
        return on[i];
    }
}
