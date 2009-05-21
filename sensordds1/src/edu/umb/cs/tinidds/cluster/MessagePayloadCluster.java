/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinidds.cluster;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.umb.cs.tinydds.MessagePayload;

/**
 *
 * @author francesco
 */
public class MessagePayloadCluster implements MessagePayload {

    private byte[] load;
    private int numOfNodes;

    public MessagePayloadCluster(){
   		load = null;
        numOfNodes = 0;
    }

    public MessagePayloadCluster(byte[] data){
        load = data;
        numOfNodes = (load.length)/32;
    }

    public void addNode(Node node){
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        try {
            if(load != null)
                dout.write(load, 0, load.length);
            dout.writeLong(node.getNodeID().longValue());
            dout.writeDouble(node.getLatitude());
            dout.writeDouble(node.getLongitude());
            dout.writeDouble(node.getElevation());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        load = bout.toByteArray();
        numOfNodes++;
    }

    public int getNodeCount(){
        return numOfNodes;
    }

    public Node[] getNodes(){
        ByteArrayInputStream bin = new ByteArrayInputStream(load);
        DataInputStream din = new DataInputStream(bin);
        Node[] nodes = new Node[numOfNodes];
        try {
            for(int i = 0; i < numOfNodes; i++){
                Node node = new Node(new Long(din.readLong()), din.readDouble(),
                                     din.readDouble(), din.readDouble());
                nodes[i] = node;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return nodes;
    }

    public byte[] marshall() {
		return load;
    }

    public void demarshall(byte[] data) {
        load = data;
        numOfNodes = (load.length)/32;
   }

    /**
     * Returns the lenght of this payload in bytes
     *
     * @return Length of the message in bytes
     */
    public int size() {
        return load.length;
    }
}
