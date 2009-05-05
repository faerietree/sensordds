/*$Id: DDS.java,v 1.3 2008/08/29 20:26:44 pruet Exp $
 
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

package edu.umb.cs.tinydds;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import edu.umb.cs.tinydds.DDSimpl.DomainParticipantImpl;
import edu.umb.cs.tinydds.DDSimpl.SubscriberImpl;
import edu.umb.cs.tinydds.L3.L3;
import edu.umb.cs.tinydds.L3.OneHop;
import edu.umb.cs.tinydds.L4.L4;
import edu.umb.cs.tinydds.L4.L4AL;
import edu.umb.cs.tinydds.OERP.OERP;
import edu.umb.cs.tinydds.OERP.SpanningTree;
import edu.umb.cs.tinydds.io.LED;
import edu.umb.cs.tinydds.tinygiop.TinyGIOP;
import edu.umb.cs.tinydds.tinygiop.TinyGIOPimpl;
import edu.umb.cs.tinydds.utils.Logger;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.omg.dds.DomainParticipant;

/**
 *
 * @author pruet
 */
public class DDS_BS extends MIDlet {

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    protected Logger logger;
    protected long PREFIX = 0;
    protected LED leds;
    L3 l3;
    L4 l4;
    TinyGIOP tinygiop;
    OERP oerp;

    protected void startApp() throws MIDletStateChangeException {
        //misc initialization
        leds = new LED();
        logger = new Logger("DDS_BS");
        logger.setLogLevel(Logger.INFO);
        logger.logInfo("initiated");
        
        // TinyDDS Stack
        l3 = new OneHop();
        l4 = new L4AL();
        l4.setL3(l3);
        tinygiop = new TinyGIOPimpl();
        tinygiop.setL4(l4);
        oerp = new SpanningTree();
        oerp.setTinyGIOP(tinygiop);
        
        
        DomainParticipant domainParticipant = new DomainParticipantImpl();
        domainParticipant.create_publisher(null).setOERP(oerp);
        domainParticipant.create_subscriber(null).setOERP(oerp);
        oerp.addObserver((SubscriberImpl) domainParticipant.create_subscriber(null));
        
        
        //Start application
        new ApplicationBS();
        //Show LED status
        leds.setColor(7, LEDColor.GREEN);
        leds.setOn(7);
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

}
