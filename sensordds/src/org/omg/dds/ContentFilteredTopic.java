/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.dds;

/**
 *
 * @author matt
 */
public interface ContentFilteredTopic extends TopicDescription {
    
    Topic get_related_topic();
    
    String[] get_expression_parameters();
    
    void set_expression_parameters(String[] expression_parameters);
}
