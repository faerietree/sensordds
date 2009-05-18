/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umb.cs.tinydds;

import edu.umb.cs.cluster.ClusterMessage;
import edu.umb.cs.cluster.MessagePayloadCluster;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;

/**
 *
 * @author matt
 */
public class MessageFactory implements GlobalConfiguration {
    
    public final static byte PUB_SUB_MESSAGE = 0;
    public final static byte CLUSTER_MESSAGE = 1;
    
    public static AbstractMessage create(byte[] data){
        
        Logger logger = new Logger("MessageFactory");

        byte type = data[0];
        AbstractMessage message = null;

        if(type == PUB_SUB_MESSAGE){
           if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("Creating message of type PUB_SUB_MESSAGE");
           message = new PubSubMessage(new MessagePayloadBytes(new byte[data.length]));
        }
        else if(type == CLUSTER_MESSAGE){
           if(DEBUG && DBUG_LVL >= MEDIUM)
               logger.logInfo("Creating message of type CLUSTER_MESSAGE");
           message = new ClusterMessage(new MessagePayloadCluster(new byte[data.length]));
        }
        return message;
    }
}
