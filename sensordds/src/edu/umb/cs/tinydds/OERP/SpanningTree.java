/*$Id: SpanningTree.java,v 1.3 2008/08/29 20:26:44 pruet Exp $

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
package edu.umb.cs.tinydds.OERP;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import edu.umb.cs.tinydds.DDS;
import edu.umb.cs.tinydds.L3.AddressFiltering;
import edu.umb.cs.tinydds.L3.L3;
import edu.umb.cs.tinydds.Message;
import edu.umb.cs.tinydds.MessagePayload;
import edu.umb.cs.tinydds.MessagePayloadBytes;
import edu.umb.cs.tinydds.io.LED;
import edu.umb.cs.tinydds.tinygiop.TinyGIOPObserver;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author pruet
 */
public class SpanningTree extends OERP implements TinyGIOPObserver, Runnable {

    Logger logger;
    //Hashtable topicWeight;
    Hashtable parent;
    LED leds;
    Vector subscribedTopic;

    public SpanningTree() {
        super();
        leds = new LED();
        leds.setColor(0, LEDColor.RED);
        leds.setColor(1, LEDColor.RED);
        leds.setColor(2, LEDColor.RED);
        leds.setColor(3, LEDColor.RED);
        logger = new Logger("SpanningTree");
        logger.logInfo("initiate");
        //topicWeight = new Hashtable();
        subscribedTopic = new Vector();
        parent = new Hashtable();
    }

    public void update(Observable obj, Object arg) {
        Message msg = (Message) arg;
        logger.logInfo("update:receive message subject=" + msg.getSubject() + " topic=" + msg.getTopic() + " orig=" + AddressFiltering.longToAddress(msg.getOriginator()) + " from=" + AddressFiltering.longToAddress(msg.getSender()));
        if (msg.getSubject() == Message.SUBJECT_SUBSCRIBE) {
            logger.logInfo("update:subscribe message");
            //MessagePayloadBytes payload = (MessagePayloadBytes) msg.getPayload();
            //byte weight = payload.get()[0];
            String topic = msg.getTopic();
            
            //logger.logInfo("incomming weight=" + weight + " current weight=" + topicWeight.get(topic));
            
            parent.put(topic, new Long(msg.getSender()));
            
//            if (topicWeight.get(topic) == null || (((Integer) topicWeight.get(topic)).intValue()) > weight) {
//                logger.logInfo("update:weight need to be updated");
//                // set new parent
////              parent.put(topic, new Long(msg.getSender()));
//                // update the weight
//                topicWeight.put(topic, new Integer(weight));
//                updateLed(weight);
//                // set the new weight
//                weight++;
//                byte[] weights = new byte[1];
//                weights[0] = (byte) (weight);
//                // resent message
//                payload.set(weights);
//                msg.setPayload(payload);
//                send(msg);
//            } else {
//                logger.logInfo("update:drop");
//            }
        }
        if (msg.getSubject() == Message.SUBJECT_DATA) {
            if (subscribedTopic.contains(msg.getTopic())) {
                logger.logInfo("update:we're intrested in this topic, push up");
                notifyObservers(arg);
            }
            if (parent.get(msg.getTopic()) != null) {
                logger.logInfo("update:forward data message");
                msg.setReceiver(((Long) parent.get(msg.getTopic())).longValue());
                tinygiop.send(msg);
            }
        } else {
            notifyObservers(arg);
        }
    }

    public int send(Message msg) {
        logger.logInfo("send:msg");
        if (msg.getSubject() == Message.SUBJECT_SUBSCRIBE) {
            msg.setReceiver(L3.BROADCAST_ADDRESS);

            return tinygiop.send(msg);
        } else if (msg.getSubject() == Message.SUBJECT_DATA) {
            // msg.setReceiver(AddressFiltering.longToAddress((Long)parent.get(msg.getTopic()))); 
            if (parent.get(msg.getTopic()) != null) {
                Long receiverAddress = (Long)parent.get(msg.getTopic());
                msg.setReceiver(receiverAddress.longValue());
                return tinygiop.send(msg);
            }
            logger.logInfo("send:no subscriber, drop");
        }
        return DDS.FAIL;
    }

    public void run() {
    }

    public int subscribe(String topic) {
        //TODO this limit the network radius to only 255 hops, but that should be large enough
        logger.logInfo("subscribe");
        subscribedTopic.addElement(topic);
        
        byte[] weight = new byte[1];
        //topicWeight.put(topic, new Integer(0));
        weight[0] = 1;
        MessagePayload payload = new MessagePayloadBytes(weight);
        Message msg = new Message(payload);
        msg.setSubject(Message.SUBJECT_SUBSCRIBE);
        msg.setTopic(topic);
        msg.setReceiver(L3.BROADCAST_ADDRESS);
        msg.setOriginator(L3.getAddress());
        send(msg);
        return DDS.SUCCESS;
    }

    protected void updateLed(int hop) {
        int[][] pattern = {{0, 0, 0}, {0, 0, 1}, {0, 1, 0}, {0, 1, 1}, {1, 0, 0}, {1, 0, 1}, {1, 1, 0}, {1, 1, 1}};
        for (int i = 0; i != 3; i++) {
            if (pattern[hop][i] == 0) {
                leds.setOff(i);
            } else {
                leds.setOn(i);
            }
        }
    }
}
