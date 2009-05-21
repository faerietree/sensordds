/*$Id: OneHop.java,v 1.3 2008/08/29 20:26:44 pruet Exp $

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
package edu.umb.cs.tinydds.L3;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.util.IEEEAddress;
import edu.umb.cs.tinydds.cluster.ClusterManager;
import edu.umb.cs.tinydds.AbstractMessage;
import edu.umb.cs.tinydds.cluster.ClusterMessage;
import edu.umb.cs.tinydds.DDS;
import edu.umb.cs.tinydds.MessageFactory;
import edu.umb.cs.tinydds.Sender;
import edu.umb.cs.tinydds.io.GPS;
import edu.umb.cs.tinydds.io.SimulatedGPS;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 *
 * @author pruet
 * @author francesco    Messages are augmented with GPS position before sending
 */
public class OneHop extends L3 implements Runnable, GlobalConfiguration, Sender {

    // TODO: move to some configuration file
    AddressFiltering addressFiltering;
    Logger logger;
    boolean flag;
    private GPS gps;

    public OneHop() {
        //this just doesn't seem to work..
        //myAddress = AddressFiltering.addressToLong(IEEEAddress.toDottedHex(Spot.getInstance().getRadioPolicyManager().getIEEEAddress()));
        myAddress = Spot.getInstance().getRadioPolicyManager().getIEEEAddress();
        //myAddress = new IEEEAddress(foo).asLong();
        
        logger = new Logger("OneHop");
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("initiated:");
        if(DEBUG && DBUG_LVL >= LIGHT)
            logger.logInfo("initiated:my address is " + IEEEAddress.toDottedHex(L3.getAddress()));
         if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("initiated:start receiver thread");
        flag = false;
        gps = SimulatedGPS.getInstance(); // Get GPS instance
        new Thread(this).start();
    }

    public int send(AbstractMessage msg) {
        
        RadiogramConnection rgc_tx = null;
        Radiogram dg = null;
        String url = null;

        //TODO: Test this flag, but theoretically, it should work well
        if (flag) {
            return DDS.FAIL;
        }
        flag = true;

        // Augment message with GPS position
        msg.setSenderLat(gps.getLatitude());
        msg.setSenderLon(gps.getLongitude());
        msg.setSenderElev(gps.getElevation());

        if (msg.getReceiver() != L3.BROADCAST_ADDRESS) {
            url = "radiogram://" + AddressFiltering.longToAddress(msg.getReceiver()) + ":123";
        } else {
            url = "radiogram://broadcast:123";
        }
        if(DEBUG && DBUG_LVL >= LIGHT)
            logger.logInfo("send:to:" + url);

        try {
            rgc_tx = (RadiogramConnection) Connector.open(url);
            dg = (Radiogram) rgc_tx.newDatagram(rgc_tx.getMaximumLength());
        } catch (IOException ex) {
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logError("send:can't open connection");
            ex.printStackTrace();
            return DDS.FAIL;
        }
        if (rgc_tx != null) {
            try {
                msg.setSender(L3.getAddress());
                dg.reset();
                int size = msg.marshall().length;
                if (size > rgc_tx.getMaximumLength()) {
                    if(DEBUG && DBUG_LVL >= LIGHT)
                        logger.logError("send:message to large max=" +
                              rgc_tx.getMaximumLength() + " msg size= " + size);
                    return DDS.FAIL;
                }
                if(msg.getMessageType() == MessageFactory.CLUSTER_MESSAGE)
                if(DEBUG && DBUG_LVL >= FULL)
                    logger.logInfo("Message code is: " + ((ClusterMessage)msg).getMsgCode());
                dg.write(msg.marshall());
                rgc_tx.send(dg);
                rgc_tx.close();
                flag = false;
            } catch (IOException ex) {
                if(DEBUG && DBUG_LVL >= LIGHT)
                    logger.logError("send:can't send message");
                ex.printStackTrace();
                if (rgc_tx != null) {
                    try {
                        rgc_tx.close();
                        flag = false;
                    } catch (IOException ex1) {
                        if(DEBUG && DBUG_LVL >= MEDIUM)
                            logger.logError("send:can't close connection");
                        ex1.printStackTrace();
                    }
                }
                return DDS.FAIL;
            }
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("send:done");
            return DDS.SUCCESS;
        }
        return DDS.FAIL;
    }

    public void run() {
        String tmp = null;
        RadiogramConnection rgc_rx = null;
        Radiogram dg = null;

        try {
            rgc_rx = (RadiogramConnection) Connector.open("radiogram://:123");
            dg = (Radiogram) rgc_rx.newDatagram(rgc_rx.getMaximumLength());
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("run:open receiver connection");
        } catch (IOException e) {
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logError("run:Could not open radiogram receiver connection");
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                byte[] b = new byte[rgc_rx.getMaximumLength()];
                AbstractMessage mesg;
                
                dg.reset();
                rgc_rx.receive(dg);
                b = dg.getData();

                mesg = MessageFactory.create(b);
                mesg.demarshall(b);
                if(DEBUG && DBUG_LVL >= LIGHT)
                    logger.logInfo("run:received message from " + dg.getAddress());
                if(DEBUG && DBUG_LVL >= LIGHT)
                    logger.logInfo("Message from lat=" + mesg.getSenderLat() + " lon="
                        + mesg.getSenderLon() + " elev=" + mesg.getSenderElev() +
                        " dist="  + gps.getEuclidianDistFrom(mesg.getSenderLat(),
                        mesg.getSenderLon(), mesg.getSenderElev()));

                // logger.logInfo("Originator: " + AddressFiltering.longToAddress(mesg.getOriginator()));
                // logger.logInfo("Sender: " + AddressFiltering.longToAddress(mesg.getSender()));
                if(DIST_ENFORCED && RANGE <
                        gps.getEuclidianDistFrom(mesg.getSenderLat(), mesg.getSenderLon(), mesg.getSenderElev())){
                    if(DEBUG && DBUG_LVL >= LIGHT)
                        logger.logInfo("Too far away, could not hear message");
                    continue;
                }
                if(mesg.getMessageType() == MessageFactory.CLUSTER_MESSAGE){
                    if(DEBUG && DBUG_LVL >= MEDIUM)
                        logger.logInfo("run:Cluster message: pass to ClusterManager");
                    ClusterManager.getInstance().loadMessage((ClusterMessage) mesg, this);
                }
                if(mesg.getMessageType() == MessageFactory.PUB_SUB_MESSAGE){
                    if(DEBUG && DBUG_LVL >= MEDIUM)
                        logger.logInfo("run:Pub Sub message: notify observers");
                    this.notifyObservers((Object) mesg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logWarning("run: not receive data");
            }
        }
    }
}
