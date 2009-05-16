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
import edu.umb.cs.tinydds.AbstractMessage;
import edu.umb.cs.tinydds.DDS;
import edu.umb.cs.tinydds.MessageFactory;
import edu.umb.cs.tinydds.utils.Logger;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 *
 * @author pruet
 */
public class OneHop extends L3 implements Runnable {

    // TODO: move to some configuration file
    AddressFiltering addressFiltering;
    Logger logger;
    boolean flag;

    public OneHop() {
        //this just doesn't seem to work..
        //myAddress = AddressFiltering.addressToLong(IEEEAddress.toDottedHex(Spot.getInstance().getRadioPolicyManager().getIEEEAddress()));
        myAddress = Spot.getInstance().getRadioPolicyManager().getIEEEAddress();
        //myAddress = new IEEEAddress(foo).asLong();
        
        logger = new Logger("OneHop");
        logger.logInfo("initiated:");
        logger.logInfo("initiated:my address is " + IEEEAddress.toDottedHex(L3.getAddress()));
        //addressFiltering = new AddressFiltering(L3.getAddress());
        logger.logInfo("initiated:start receiver thread");
        flag = false;
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
        if (msg.getReceiver() != L3.BROADCAST_ADDRESS) {
            url = "radiogram://" + AddressFiltering.longToAddress(msg.getReceiver()) + ":123";
        } else {
            url = "radiogram://broadcast:123";
        }
        logger.logInfo("send:to:" + url);
        //logger.logInfo("send:subject:" + msg.getSubject());
        //logger.logInfo("send:topic:" + msg.getTopic());
        try {
            rgc_tx = (RadiogramConnection) Connector.open(url);
            dg = (Radiogram) rgc_tx.newDatagram(rgc_tx.getMaximumLength());
        } catch (IOException ex) {
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
                    logger.logError("send:message to large max=" + rgc_tx.getMaximumLength() + " msg size= " + size);
                    return DDS.FAIL;
                }
                dg.write(msg.marshall());
                rgc_tx.send(dg);
                rgc_tx.close();
                flag = false;
            } catch (IOException ex) {
                logger.logError("send:can't send message");
                ex.printStackTrace();
                if (rgc_tx != null) {
                    try {
                        rgc_tx.close();
                        flag = false;
                    } catch (IOException ex1) {
                        logger.logError("send:can't close connection");
                        ex1.printStackTrace();
                    }
                }
                return DDS.FAIL;
            }
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
            logger.logInfo("run:open receiver connection");
        } catch (IOException e) {
            logger.logError("run:Could not open radiogram receiver connection");
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                byte[] b = new byte[rgc_rx.getMaximumLength()];
                
                dg.reset();
                rgc_rx.receive(dg);
                b = dg.getData();
                
                AbstractMessage mesg = MessageFactory.create(b);
                mesg.demarshall(b);
                
                logger.logInfo("run:receive a connection from " + dg.getAddress());
                //FIXME: This is a dirty hack, but I can't see how to fix better than this =_==
                //Gotta find a way to fix the address of base station...
//                if (mesg.getSubject() == Message.SUBJECT_SUBSCRIBE) {
//                    MessagePayloadBytes payload = (MessagePayloadBytes) mesg.getPayload();
//                    byte weight = payload.get()[0];
//                    logger.logInfo("run:check if it's subscription " + weight);
//                    if (weight == 1) { // This guy must be the base station
//                        AddressFiltering.setBaseStation(dg.getAddress());
//                    }
//                }
//                if (!addressFiltering.isMyNeighbor(dg.getAddress())) {
//                    logger.logInfo("run:drop by address filtering");
//                    continue;
//                }
                //dg.readFully(b);
                logger.logInfo("run:notify observer");
                this.notifyObservers((Object) mesg);
            } catch (IOException e) {
                e.printStackTrace();
                logger.logWarning("run:not receive data");

            }
        }
    }
}
