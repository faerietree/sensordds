/*$Id: Message.java,v 1.3 2008/08/29 20:26:44 pruet Exp $
 
Copyright (c) 2008 University of Massachusetts, Boston 
All rights reserved. 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
Neither the name of the University of Massachusetts, Boston  nor 
the names of its contributors may be used to endorse or promote products 
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OF
MASSACHUSETTS, BOSTON OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
 */

package edu.umb.cs.tinydds;

import com.sun.spot.peripheral.Spot;
import edu.umb.cs.tinydds.DDSimpl.ContentFilteredTopicImpl;
import edu.umb.cs.tinydds.DDSimpl.TopicDescriptionImpl;
import edu.umb.cs.tinydds.DDSimpl.TopicImpl;
import edu.umb.cs.tinydds.L3.L3;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.omg.dds.ContentFilteredTopic;
import org.omg.dds.Topic;
import org.omg.dds.TopicDescription;

/**
 *
 * @author pruet
 */

// This is JavaBean style class, it should be self-contain.
public class PubSubMessage extends AbstractMessage {

    public static final short SUBJECT_NONE = 0;
    public static final short SUBJECT_SUBSCRIBE = 1;
    public static final short SUBJECT_DATA = 2;
    
    public static final byte TOPIC = 0;
    public static final byte CONTENT_FILTERED_TOPIC = 1;
    
    protected byte topicType;
    protected TopicDescription topic;
    protected short subject;
    protected MessagePayload payload;

    PubSubMessage(long sender, long receiver, long originator, TopicDescription topic, short subject, MessagePayload payload) {

        super((byte)0, sender, receiver, originator);
        
        setTopic(topic);
        
        this.subject = subject;
        this.payload = payload;
    }

    public PubSubMessage(MessagePayload payload) {
        //this(Spot.getInstance().getRadioPolicyManager().getIEEEAddress(), L3.NO_ADDRESS, L3.getAddress(), topic, Message.SUBJECT_NONE, payload);
        this();
        this.payload = payload;
    }
    
    public PubSubMessage() {
        super();
        this.subject = SUBJECT_NONE;
    }

    public TopicDescription getTopic() {
        return topic;
    }

    public void setTopic(TopicDescription topic) {
        this.topic = topic;
    
        //lolh4x
        if (topic instanceof Topic) {
            topicType = TOPIC;
        } else if (topic instanceof ContentFilteredTopic) {
            topicType = CONTENT_FILTERED_TOPIC;
        }
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(short subject) {
        this.subject = subject;
    }
    
    public byte getTopicType(){
        return topicType;
    }
    
    public byte[] marshall() {
        if(payload == null) 
            return null;
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        try {
            dout.writeByte(messageType);    //identify this as a PubSubMessage
            
            dout.writeLong(getSender());
            dout.writeLong(getReceiver());
            dout.writeLong(getOriginator());
            
            dout.writeByte(topicType);   //which type of topic this is
            
            ((TopicDescriptionImpl)topic).write(dout); //write topic
            
            dout.writeShort(getSubject());
            dout.write(payload.marshall());
            dout.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bout.toByteArray();

    }

    public void demarshall(byte[] data) {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(bin);
        byte[] b = new byte[data.length];
        try {
            din.readByte();   //eat the messageType flag
            
            setSender(din.readLong());
            setReceiver(din.readLong());
            setOriginator(din.readLong());
            
            TopicDescriptionImpl topic = null;
            topicType = din.readByte();     //set topic type
            
            if(topicType == TOPIC){
                topic = new TopicImpl();
            }
            else if(topicType == CONTENT_FILTERED_TOPIC){
                topic = new ContentFilteredTopicImpl();
            }
            topic.read(din);
            setTopic(topic);
            
            setSubject(din.readShort());
            
            din.read(b);
            payload.demarshall(b);
       } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
  
}
    
