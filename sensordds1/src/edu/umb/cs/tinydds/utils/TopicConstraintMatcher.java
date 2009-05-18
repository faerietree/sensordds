/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.utils;

import edu.umb.cs.tinydds.DDSimpl.SensorContentFilteredTopic;
import edu.umb.cs.tinydds.fuzzyAggregation.Aggregator;
import edu.umb.cs.tinydds.fuzzyAggregation.AggregatorImpl;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author matt
 */
public class TopicConstraintMatcher implements GlobalConfiguration {

    private Logger logger;
    private Aggregator aggregator;
    
    public TopicConstraintMatcher() {
        logger = new Logger("TopicConstraintMatcher");
        aggregator = AggregatorImpl.getInstance();
    }
    
    //change this value to some object like SensorConstraints
    public boolean match(SensorContentFilteredTopic topic, int value, long timestamp, Geometry geom){
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("match");
        
        Hashtable selectors = topic.getSelectors();        
        Hashtable vars = new Hashtable();

        /* all the commented stuff below will be used once the 
           aggregation class is finished */
//        String phenomSelector = (String)selectors.get("Phenom");
//        String temporalSelector = (String)selectors.get("Temporal");
//        
//        Vector record = null;
//        
//        if(phenomSelector != null){
//            record = aggregator.getPhenomAggregation(phenomSelector, topic.getPhenomenon());
//        }
//        else {
//            record = aggregator.getTemporalAggregation(temporalSelector, topic.getPhenomenon());
//        }
//        
//        Double v = (Double)record.elementAt(0); //value
//        Long ts = (Long)record.elementAt(1); //timestamp
//        Geometry g = (Geometry)record.elementAt(2); //geometry
//        
//        vars.put("Phenom", Double.toString(v.doubleValue()));
//        vars.put("Temporal", Long.toString(ts.longValue()));
        
        vars.put("Phenom", Integer.toString(value));
        vars.put("Temporal", Long.toString(timestamp));
        
        boolean result = topic.eval(vars);
        
        if(result){
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("got match for "+topic.get_name());
        }
        
        return result;
    }
    
}
