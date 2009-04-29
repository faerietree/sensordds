/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 */
public class FreshMonsterMessage implements GenericMessage {
static final short MAXBLOCKS = 4;
FuzzyMsgBlock msgBlocks[]; // array[3];
//int nodeID; //not used, currently using nodid from Sunspot header
//timestamp  // not used, currenty using timestamp in SunSpot header
    public String getContent() {
        //TODO convert message block array to string
        //Just crude method for now
        return msgBlocks.toString();
    }

    public int getLength() {
        return -1;
    }

    public int getNumBlocks() {
        return msgBlocks.length;
    }
    public int getMessageType() {
        if( !isMixedType())
                return (int) msgBlocks[0].getType();
        else;
        return -1;
    }

    public int getNodeID() {
        return -1;
    }
 /**
 *
 *
 */public boolean isMixedType(){
     //TODO check all messge blocks for equality
     return false;

    }
 /**
 *
 *
 */public void addMsgBlock(FuzzyMsgBlock block){
     //puts block on the end
     //will overwrite if full
     msgBlocks[msgBlocks.length -1] = block;

    }

}
