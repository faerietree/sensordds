/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.dds;

/**
 *
 * @author matt
 */
public interface Topic extends TopicDescription {
    
    Listener get_listener();
    
    void set_listener(Listener a_listener);
}
