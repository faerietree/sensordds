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
 * @author francesco Added Lat, Lon, Elev. of sender to the messages (this is to
 *                   needed to simulate radio distance)
 */
public abstract class AbstractMessage {
    
    protected byte messageType;
    protected long sender;
    protected long receiver;
    protected long originator;
    // The following adds overhead to the message - remember to eliminate if this
    // application needs to be deployed on a real network
    protected double senderLat;
    protected double senderLon;
    protected double senderElev;

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

        public double getSenderElev() {
        return senderElev;
    }

    public void setSenderElev(double elev) {
        this.senderElev = elev;
    }

    public double getSenderLat() {
        return senderLat;
    }

    public void setSenderLat(double lat) {
        this.senderLat = lat;
    }

    public double getSenderLon() {
        return senderLon;
    }

    public void setSenderLon(double lon) {
        this.senderLon = lon;
    }
    
    public abstract byte[] marshall();
    
    public abstract void demarshall(byte[] data);
    
}
