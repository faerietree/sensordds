/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.utils;

import edu.umb.cs.tinydds.DDSimpl.ContentFilteredTopicImpl;
import org.omg.dds.ContentFilteredTopic;

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
    public boolean match(ContentFilteredTopic topic, int value){
        logger.logInfo("match");
        
        String expression = ((ContentFilteredTopicImpl)topic).get_filter_expression();
        String[] params = topic.get_expression_parameters();
        
        // example "light > 100"
        String type = topic.get_type_name();
        
        int endOfPhenom = expression.indexOf(" ", 0); 
        //logger.logInfo("endOfPhenom index: "+endOfPhenom);
        
        int endOfOperator = expression.indexOf(" ", endOfPhenom+1);
        //logger.logInfo("endOfOperator index: "+endOfOperator);
        
        String phenom = expression.substring(0, endOfPhenom-1);
        String op = expression.substring(endOfPhenom+1, endOfOperator);
        logger.logInfo("operator: "+op);
        
        //String constant = expression.substring(endOfOperator+1, expression.length()-1);
        double constant = Double.parseDouble(params[0]);
        
        if(op.equals("=")){
            return value == constant;
        }
        else if(op.equals(">")){
            return value > constant;
        }
        else if(op.equals("<")){
            return value < constant;
        }
        else if(op.equals("<>")){
            return value != constant;
        }
        else {
            logger.logInfo("no match");
            return false;
        }
     }
    
}
