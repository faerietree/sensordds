/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds;

/**
 *
 * @author matt
 */
public class MessageFactory {
    
    public final static byte PubSubMessage = 0;
    public final static byte ClusterMessage = 1;
    
    public static AbstractMessage create(byte[] data){
        
        byte type = data[0];
        AbstractMessage message = null;
        
        if(type == PubSubMessage){
            message = new PubSubMessage(new MessagePayloadBytes(new byte[data.length]));
        }
        else if(type == ClusterMessage){
            //do work here
        }
        
        return message;
    }
    
}
