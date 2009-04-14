/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.utils;

import edu.umb.cs.tinydds.DDSimpl.SensorContentFilteredTopic;
import java.util.Hashtable;

/**
 *
 * @author matt
 */
public class TopicConstraintMatcher {

    private Logger logger;
    
    public TopicConstraintMatcher() {
        logger = new Logger("TopicConstraintMatcher");
    }
    
    //change this value to some object like SensorConstraints
    public boolean match(SensorContentFilteredTopic topic, int value){
        logger.logInfo("match");
        
        Hashtable vars = new Hashtable();
        vars.put("Phenom:"+topic.get_type_name(), Integer.toString(value));
    
        boolean result = topic.eval(vars);
        
        if(result){
            logger.logInfo("got match for "+topic.get_name());
        }
        
        return result;
    }
    
}
