/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.DDSimpl;

/**
 * Syncronized model of 
 * 
 * @author matt
 */
public class SensorContentFilteredTopic extends ContentFilteredTopicImpl {

    public SensorContentFilteredTopic() {
        
    }
    
    public void addPhenomenonFilter(String phenom, String operator, double value){
        
    }

    public void setExpression_parameters(String[] expression_parameters) {
        this.expression_parameters = expression_parameters;
    }

    public void setFilter_expression(String filter_expression) {
        this.filter_expression = filter_expression;
    }  
}
