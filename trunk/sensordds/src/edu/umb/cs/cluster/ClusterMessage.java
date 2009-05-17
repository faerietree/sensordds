/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.cluster;

import edu.umb.cs.tinydds.AbstractMessage;
import edu.umb.cs.tinydds.MessageFactory;
import edu.umb.cs.tinydds.MessagePayload;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Vinita
 * @author francesco    Added GPS coordinates to the payload
 */
public class ClusterMessage extends AbstractMessage
{
    public static final byte NEED_INFO = 0;
    public static final byte MY_INFO = 1;

    public static final byte NEED_CH= 2;
    public static final byte MY_CM = 3;
    
    public static final byte CM_ACK = 4;
    public static final byte MY_CMS = 5;

    protected MessagePayload payload;
    protected byte msgCode;

    public ClusterMessage() {
        super();
        messageType = MessageFactory.CLUSTER_MESSAGE;
    }

    public ClusterMessage(MessagePayload payload) {
        this();
        this.payload = payload;
    }

    ClusterMessage(long sender, long receiver, long originator, MessagePayload payload) 
    {
        super((byte)MessageFactory.CLUSTER_MESSAGE, sender, receiver, originator);
        this.payload = payload;
    }
    
    public void setMsgCode(byte msgCode)
    {
        this.msgCode = msgCode;
    }
    
    public byte getMsgCode()
    {
        return msgCode;
    }
    
    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public byte[] marshall() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        try {
            dout.writeByte(messageType);    //identify this as a ClusterMessage
            
            dout.writeLong(getSender());
            dout.writeLong(getReceiver());
            dout.writeLong(getOriginator());
            // Lat/Lon/Elev payload
            dout.writeDouble(getSenderLat());
            dout.writeDouble(getSenderLon());
            dout.writeDouble(getSenderElev());

            
            dout.writeByte(msgCode);        //write what type of message it is
            if(carriesPayload(msgCode))
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
            // Lat/Lon/Elev payload
            setSenderLat(din.readDouble());
            setSenderLon(din.readDouble());
            setSenderElev(din.readDouble());

            setMsgCode(din.readByte());
            
            din.read(b);
            payload.demarshall(b);
            
       } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Some message codes requires additional payload.  This method helps the
     * marshalling method to determine if it should marshall a payload.
     *
     * @param msgCode   The code for this message
     * @return  true if this message requires a payload
     */
    private boolean carriesPayload(byte msgCode) {
//        return (msgCode == MY_CM) ||
//               (msgCode == MY_INFO);
        return false;
    }
}
