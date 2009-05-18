/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.dds;

/**
 *
 * @author matt
 */
public interface TopicDescription {
    
    DomainParticipant get_participant();
    
    void set_participant(DomainParticipant dp);
    
    String get_type_name();
    
    String get_name();
    
}
