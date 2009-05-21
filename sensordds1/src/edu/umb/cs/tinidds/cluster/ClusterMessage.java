/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinidds.cluster;

import edu.umb.cs.tinydds.AbstractMessage;
import edu.umb.cs.tinydds.MessageFactory;
import edu.umb.cs.tinydds.MessagePayload;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
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
public class ClusterMessage extends AbstractMessage implements GlobalConfiguration
{
    public static final byte NEED_INFO = 0;
    public static final byte MY_INFO = 1;
    public static final byte TAKE_CMS = 2;
    public static final byte YOU_ARE_CH = 3;
    public static final byte MY_CMS = 4;
    public static final byte YOUR_CH = 5;

    // public static final byte NEED_CH= 4;
    // public static final byte MY_CM = 4;
    
   //  public static final byte CM_ACK = 6;

    protected MessagePayload payload;
    protected byte msgCode;
    private int colorIndex;
    private Logger logger;

    public ClusterMessage() {
        super();
        messageType = MessageFactory.CLUSTER_MESSAGE;
        logger = new Logger("ClusterMessage");
    }

    public ClusterMessage(MessagePayload payload) {
        this();
        this.payload = payload;
    }

    ClusterMessage(long sender, long receiver, long originator, MessagePayload payload) 
    {
        super((byte)MessageFactory.CLUSTER_MESSAGE, sender, receiver, originator);
        this.payload = payload;
        logger = new Logger("ClusterMessage");
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

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
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
            if((msgCode == YOU_ARE_CH) || msgCode == YOUR_CH)
                dout.writeInt(colorIndex);
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
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("demarshall: size of data is " + data.length + " bytes" );
        byte[] b;
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
            if((msgCode == YOU_ARE_CH) || msgCode == YOUR_CH){
                setColorIndex(din.readInt());
                b = new byte[data.length - 54];
            }
            else{
                b = new byte[data.length - 50];
            }
            if(carriesPayload(msgCode)){
                din.read(b);
                payload = new MessagePayloadCluster(b);
            }
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
        return msgCode == TAKE_CMS;
//        return (msgCode == MY_CM) ||
//               (msgCode == MY_INFO);
    }
}
