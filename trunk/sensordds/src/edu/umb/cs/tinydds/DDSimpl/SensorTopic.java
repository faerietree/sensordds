/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.DDSimpl;

import org.omg.dds.DomainParticipant;
import org.omg.dds.Listener;
import org.omg.dds.Topic;

/**
 *
 * @author matt
 */
public class SensorTopic implements Topic {

    protected Topic topic;
    
    public SensorTopic(Topic topic) {
        this.topic = topic;
    }
    
    public String getPhenomenon(){
        return get_type_name();
    }

    public Listener get_listener() {
        return topic.get_listener();
    }

    public void set_participant(DomainParticipant dp) {
        topic.set_participant(dp);
    }

    public String get_name() {
        return topic.get_name();
    }

    public DomainParticipant get_participant() {
        return topic.get_participant();
    }

    public String get_type_name() {
        return topic.get_type_name();
    }

    public void set_listener(Listener a_listener) {
        topic.set_listener(a_listener);
    }    
    
}
