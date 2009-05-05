/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds;

import edu.umb.cs.tinydds.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Vinita
 */

public class ClusterMessage extends AbstractMessage
{
    public static final byte NEED_CH= 0;
    public static final byte CH_REPLY = 1;
    public static final byte CM_ACK = 2;
    public static final byte MY_CMS = 3;

    
    protected MessagePayload payload;
    protected byte msgType;

    public ClusterMessage() {
        super();
    }

    ClusterMessage(MessagePayload payload) {
        this();
        this.payload = payload;
    }

    ClusterMessage(long sender, long receiver, long originator,  MessagePayload payload) 
    {
        super((byte)0, sender, receiver, originator);
        this.payload = payload;
    }
    
    public void setMsgType(byte msgType)
    {
        this.msgType = msgType;
    }
    
    public byte getMsgType()
    {
        return msgType;
    }
    
     public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public byte[] marshall() {
        if(payload == null) 
            return null;
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        try {
            dout.writeByte(messageType);    //identify this as a ClusterMessage
            
            dout.writeLong(getSender());
            dout.writeLong(getReceiver());
            dout.writeLong(getOriginator());
            
            dout.writeByte(msgType);        //write what type of message it is
            
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
            
            setMsgType(din.readByte());
            
            din.read(b);
            payload.demarshall(b);
            
       } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
