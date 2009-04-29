/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 * Interface for the creation, manipulation, parsing of messages
 */
public interface GenericMessage {
    public String getContent();

    public int getLength();

    public int getMessageType() ;

    int getNodeID();

}
