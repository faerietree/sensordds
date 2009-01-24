/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.DDSimpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.omg.dds.DomainParticipant;
import org.omg.dds.Listener;
import org.omg.dds.Topic;

/**
 *
 * @author matt
 */
public class TopicImpl extends TopicDescriptionImpl implements Topic {

    private DomainParticipant dp;
    private Listener listener;

    public TopicImpl(DomainParticipant dp, String name, String type_name) {
        super(name, type_name);
        this.dp = dp;
    }

    public TopicImpl() {
    }
   
    public DomainParticipant get_participant() {
        return dp;
    }

    public void set_participant(DomainParticipant dp) {
        this.dp = dp;
    }

    public Listener get_listener() {
        return listener;
    }

    public void set_listener(Listener a_listener) {
        this.listener = a_listener;
    }

    public void read(DataInputStream is) throws IOException {
        
        this.name = is.readUTF();
        this.type_name = is.readUTF();
    }

    public void write(DataOutputStream os) throws IOException {
        os.writeUTF(get_name());
        os.writeUTF(get_type_name());
    }
    
}
