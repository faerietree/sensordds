/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds;

import com.sun.spot.peripheral.Spot;
import edu.umb.cs.tinydds.L3.L3;

/**
 *
 * @author matt
 */
public abstract class AbstractMessage {
    
    protected byte messageType;
    protected long sender;
    protected long receiver;
    protected long originator;

    public AbstractMessage(byte messageType, long sender, long receiver, long originator) {
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.originator = originator;
    }

    public AbstractMessage() {
        this.sender = Spot.getInstance().getRadioPolicyManager().getIEEEAddress();
        this.receiver = L3.NO_ADDRESS;
        this.originator = L3.getAddress();
        this.messageType = 0;
    }

    public long getOriginator() {
        return originator;
    }

    public void setOriginator(long originator) {
        this.originator = originator;
    }

    public long getReceiver() {
        return receiver;
    }

    public void setReceiver(long receiver) {
        this.receiver = receiver;
    }

    public long getSender() {
        return sender;
    }

    public void setSender(long sender) {
        this.sender = sender;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }
    
    public abstract byte[] marshall();
    
    public abstract void demarshall(byte[] data);
    
}
