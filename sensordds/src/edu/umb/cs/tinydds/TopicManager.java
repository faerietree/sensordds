/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds;

import edu.umb.cs.tinydds.DDSimpl.ContentFilteredTopicImpl;
import edu.umb.cs.tinydds.DDSimpl.SensorContentFilteredTopic;
import edu.umb.cs.tinydds.utils.Logger;

import edu.umb.cs.tinydds.utils.TopicConstraintMatcher;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.omg.dds.ContentFilteredTopic;
import org.omg.dds.TopicDescription;

/**
 *
 * @author matt
 */
public class TopicManager {
    
    protected static TopicManager topicManager;
    
    protected Hashtable topics;
    protected Logger logger;
    protected TopicConstraintMatcher matcher;
    
    protected TopicManager(){
        topics = new Hashtable();
        matcher = new TopicConstraintMatcher();
        logger = new Logger("TopicManager");
        logger.logInfo("initiate");
    }
    
    public static synchronized TopicManager getInstance(){
    
        if (topicManager == null) {
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
        
        return subscriberAddresses;
    }
    
    /**
     * 
     * @param topic
     * @param value
     * @return
     */
    public Vector findFilteredTopics(TopicDescription topic, int value){
        logger.logInfo("findFilteredTopics");
        
        Vector matchedFilteredTopics = new Vector();
        
        Enumeration filteredTopics = getContentFilteredTopics(topic);
        
        while(filteredTopics.hasMoreElements()){
            SensorContentFilteredTopic filteredTopic = (SensorContentFilteredTopic)filteredTopics.nextElement();
            
            if(matcher.match(filteredTopic, value, System.currentTimeMillis(), null)){
                matchedFilteredTopics.addElement(filteredTopic);
            }
        }
        
        return matchedFilteredTopics;
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
        if(subscriberAddresses == null){
            subscriberAddresses = new Vector();
            
            if(topicDescription instanceof ContentFilteredTopic){
                topics.put(new SensorContentFilteredTopic((ContentFilteredTopicImpl)topicDescription), subscriberAddresses);
            }
            else {    
                topics.put(topicDescription, subscriberAddresses);
            }
        }
        
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
    
    /**
     * Returns an Enumeration of TopicDescription objects
     * 
     * @return 
     */
    public Enumeration getAllTopics(){
        logger.logInfo("getAllTopics");
        return topics.keys();
    }
    
    /**
     * Finds filtered topics of topic with same type.
     * 
     * @param topic
     * @return
     */
    public Enumeration getContentFilteredTopics(TopicDescription topic){
        logger.logInfo("getcontentFilteredTopics");
        
        Vector foo = new Vector();
        
        Enumeration allTopics = getAllTopics();
        while(allTopics.hasMoreElements()){
            TopicDescription topicFiltered = (TopicDescription)allTopics.nextElement();
            
            if(topicFiltered instanceof SensorContentFilteredTopic && 
               topic.get_type_name().equals(topicFiltered.get_type_name())){
                
                foo.addElement(topicFiltered);
            }
        }
        
        return foo.elements();
    }
}
