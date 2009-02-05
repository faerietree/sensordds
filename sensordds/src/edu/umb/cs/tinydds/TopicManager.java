/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds;

import edu.umb.cs.tinydds.utils.Logger;
import java.util.Hashtable;
import java.util.Vector;
import org.omg.dds.TopicDescription;

/**
 *
 * @author matt
 */
public class TopicManager {
    
    protected static TopicManager topicManager;
    
    protected Hashtable topics;
    protected Logger logger;
    
    protected TopicManager(){
        topics = new Hashtable();
        logger = new Logger("TopicManager");
        logger.logInfo("initiate");
    }
    
    public static TopicManager getInstance(){
        
        if(topicManager == null){
            topicManager = new TopicManager();
        }
        
        return topicManager;
    }
    
    /**
     * Returns a Vector of Long objects
     * 
     * 
     * @param topicDescription
     * @return
     */
    public Vector getAddressesForTopic(TopicDescription topicDescription){
        logger.logInfo("getAddressesForTopic");
        
        Vector subscriberAddresses = (Vector)topics.get(topicDescription);
        
        if(subscriberAddresses == null){  //we don't have anything for this topic yet
            subscriberAddresses = new Vector();
            topics.put(topicDescription, subscriberAddresses);   
        }
        
        return subscriberAddresses;
    }
    
    /**
     * 
     * @param topicDescription
     * @param subscriberAddress
     */
    public void addAddressForTopic(TopicDescription topicDescription, long subscriberAddress){
        logger.logInfo("addAddressForTopic");
        
        //get the list of subscriber addresses associated with this topic
        Vector subscriberAddresses = getAddressesForTopic(topicDescription);
        
        //this needs to be a Long to add to a Collection.  Fix this awful variable name.
        Long subscriberAddressLong = new Long(subscriberAddress); 
        
        //check to see if this address is already subscribing to this topic
        if(!subscriberAddresses.contains(subscriberAddressLong)) {
            subscriberAddresses.addElement(subscriberAddressLong);
            logger.logInfo("added address for topic: "+topicDescription);
        }
        else {
            logger.logInfo("dropped subscriber address");
        }
    }
    
}
