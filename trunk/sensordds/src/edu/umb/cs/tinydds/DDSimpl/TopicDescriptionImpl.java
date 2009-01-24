/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.DDSimpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.omg.dds.TopicDescription;

/**
 *
 * @author matt
 */
public abstract class TopicDescriptionImpl implements TopicDescription {
    
    protected String name;
    protected String type_name;

    public TopicDescriptionImpl(String name, String type_name) {
        this.name = name;
        this.type_name = type_name;
    }

    public TopicDescriptionImpl() {      
    }
    
    public String get_type_name(){
        return type_name;
    }
    
    public String get_name(){
        return name;
    }
    
    public String toString() {
        return get_name();
    }
    
    public abstract void read(DataInputStream is) throws IOException;
    
    public abstract void write(DataOutputStream os) throws IOException;
}
