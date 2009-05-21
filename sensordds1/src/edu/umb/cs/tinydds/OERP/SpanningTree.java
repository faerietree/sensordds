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

// import com.sun.spot.sensorboard.peripheral.LEDColor;
import edu.umb.cs.tinidds.cluster.ClusterManager;
import edu.umb.cs.tinydds.AbstractMessage;
import edu.umb.cs.tinydds.DDS;
import edu.umb.cs.tinydds.L3.AddressFiltering;
import edu.umb.cs.tinydds.L3.L3;
import edu.umb.cs.tinydds.MessageFactory;
import edu.umb.cs.tinydds.PubSubMessage;
import edu.umb.cs.tinydds.MessagePayload;
import edu.umb.cs.tinydds.MessagePayloadBytes;
import edu.umb.cs.tinydds.TopicManager;
import edu.umb.cs.tinydds.io.LED;
import edu.umb.cs.tinydds.tinygiop.TinyGIOPObserver;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import java.util.Vector;
import org.omg.dds.TopicDescription;

/**
 *
 * @author pruet
 * @author francesco    Added ClusterManager member
 */
public class SpanningTree extends OERP 
                          implements TinyGIOPObserver, Runnable, GlobalConfiguration {

    Logger logger;
    //Hashtable topicWeight;
    //Hashtable subscriptionAddresses;  //replaced with TopicManager
    LED leds;
    Vector subscribedTopic;
    TopicManager topicManager;
    protected boolean isBaseStation;
    ClusterManager clusterManager;

    private SpanningTree() {
         super();
        leds = new LED();
//        leds.setColor(0, LEDColor.RED);
//        leds.setColor(1, LEDColor.RED);
//        leds.setColor(2, LEDColor.RED);
//        leds.setColor(3, LEDColor.RED);
//        leds.setOn(0);
//        leds.setOn(1);
//        leds.setOn(2);

        logger = new Logger("SpanningTree");
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("initiate");

        //topicWeight = new Hashtable();
        subscribedTopic = new Vector();

        //subscriptionAddresses = new Hashtable();
        topicManager = TopicManager.getInstance();
   }

    public SpanningTree(boolean isBaseStation){
        this();
        this.isBaseStation = isBaseStation;
        // Initialize CLuster Manager
        if (this.isBaseStation) {
            ClusterManager.setAsBaseStation();
        }
        ClusterManager.setDefaultMailer(this);
        clusterManager = ClusterManager.getInstance(); // Instantiate (1st time)
    }

    public void update(Observable obj, Object arg) {
        PubSubMessage msg = (PubSubMessage) arg;
        
        if(DEBUG && DBUG_LVL >= LIGHT)
            logger.logInfo("update:receive message subject=" + msg.getSubject() +
                       " topic=" + msg.getTopic() + " orig=" +
                       AddressFiltering.longToAddress(msg.getOriginator()) +
                       " from=" + AddressFiltering.longToAddress(msg.getSender()));
        
        if (msg.getSubject() == PubSubMessage.SUBJECT_SUBSCRIBE) {  //publisher gets here
            
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("update:subscribe message");
            TopicDescription topic = msg.getTopic();
            topicManager.addAddressForTopic(topic, msg.getSender());
        }
        if (msg.getSubject() == PubSubMessage.SUBJECT_DATA) {  
            
            if (subscribedTopic.contains(msg.getTopic())) {   //subscriber gets here
                if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("update:we're intrested in this topic, push up");
                notifyObservers(arg);
            }
            
            Vector subscriptionAddresses = topicManager.getAddressesForTopic(msg.getTopic());
            if (subscriptionAddresses != null) { 
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("update:forward data message");
                
                Long address = (Long)subscriptionAddresses.firstElement();
                msg.setReceiver(address.longValue());
                tinygiop.send(msg);
            }
        } 
        else {
            notifyObservers(arg);
        }
    }

    public int send(AbstractMessage msg) {
        if(msg.getMessageType() == MessageFactory.CLUSTER_MESSAGE){
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("send:ClusterMSG");
            return tinygiop.send(msg);
        } else {
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("send:PubSubMSG");
            if (((PubSubMessage) msg).getSubject() == PubSubMessage.SUBJECT_SUBSCRIBE) {
                msg.setReceiver(L3.BROADCAST_ADDRESS);
                return tinygiop.send(msg);
            }
            else if (((PubSubMessage) msg).getSubject() == PubSubMessage.SUBJECT_DATA) {   //publishing this

                Vector addresses = topicManager.getAddressesForTopic(((PubSubMessage) msg).getTopic());
                if (addresses != null) {
                    //Long receiverAddress = (Long)subscriptionAddresses.get(msg.getTopic());
                    //we will fix this to support multiple receipents later
                    Long receiverAddress = (Long) addresses.firstElement();
                    msg.setReceiver(receiverAddress.longValue());
                    return tinygiop.send(msg);
                }
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("send:no subscriber, drop");
            }
        }
        return DDS.FAIL;
    }

    public boolean isBaseStation() {
        return isBaseStation;
    }
    
    public void run() {
    }

    public int subscribe(TopicDescription topic) {
        //TODO this limit the network radius to only 255 hops, but that should be large enough
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("subscribe");
        subscribedTopic.addElement(topic);
        
        byte[] weight = new byte[1];
        //topicWeight.put(topic, new Integer(0));
        weight[0] = 1;
        MessagePayload payload = new MessagePayloadBytes(weight);
        PubSubMessage msg = new PubSubMessage(payload);
        msg.setSubject(PubSubMessage.SUBJECT_SUBSCRIBE);
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
