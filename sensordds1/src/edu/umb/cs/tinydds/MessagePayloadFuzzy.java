/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Encodes the Mu's using big-endian byte ordering (fixed size 10bytes).
 * Node IDs are appended as needed.  The order of the node IDs is guarantied
 * FIFO.
 *
 * @author francesco
 */
public class MessagePayloadFuzzy implements MessagePayload {
/* The following is the content of the payload:
 * MANDATORY PART (byte 0 to byte 9)
 * 0: MF1-1 : "Mu value for temperature data -- Freezing"
 * 1: MF1-2 : "Mu value for temperature data -- Cold"
 * 2: MF1-3 : "Mu value for temperature data -- Hot"
 * 3: MF2-1 : "Mu value for airflow data -- Low"
 * 4: MF2-2 : "Mu value for airflow data -- Ideal"
 * 5: MF2-3 : "Mu value for airflow data -- High"
 * 6: MF3-1 : "Mu value for bacteria growth rate data -- Safe"
 * 7: MF3-2 : "Mu value for bacteria growth rate data -- Unsafe"
 * 8: MF4-1 : "Mu value for Criticality data -- Critical"
 * 9: MF4-2 : "Mu value for Criticality data -- NonCritical"
 * OPTIONAL PART (byte 10 to 10 + multiple of 8 bytes)
 * 10-17: First Node ID of node of interest
 * 18-25: SEcond Node ID of node of interest
 * ....
 */

	private static final int OFFSET_FREEZING = 0;
	private static final int OFFSET_COLD = 1;
	private static final int OFFSET_WARM = 2;
	private static final int OFFSET_LOW = 3;
	private static final int OFFSET_IDEAL = 4;
	private static final int OFFSET_HIGH = 5;
	private static final int OFFSET_SAFE = 6;
	private static final int OFFSET_UNSAFE = 7;
	private static final int OFFSET_CRITICAL = 8;
	private static final int OFFSET_NONCRITICAL = 9;

    private static final int MU_BUFF_SIZE = 10;
    private static final int PRECISION = 100;
    private static final int NAN = -100;

	private byte[] load;
    private int numOfNodes;

    public MessagePayloadFuzzy(){
		load = new byte[MU_BUFF_SIZE];
        for(int i = 0; i < 10; i++)
            load[i] = (byte) NAN; // Initialize with NaN's
        numOfNodes = 0;
	}

	public void setValue(int offset, double value){
		load[offset] = encodeDouble(value);
	}

	public double getValue(int offset){
		return decodeDouble(load[offset]);
	}

	private byte encodeDouble(double value){
        return (byte) (value * PRECISION);
	}

	private double decodeDouble(byte value){
        return ((double) value)/PRECISION;
	}

    public void read(DataInputStream is) throws IOException {
        is.read(load);
    }

    public void write(DataOutputStream os) throws IOException {
        os.write(load, OFFSET_FREEZING, load.length);
    }

    public void addNodeID(long nodeID){
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        try {
            dout.write(load, 0, load.length);
            dout.writeLong(nodeID);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        load = bout.toByteArray();
        this.numOfNodes++;
    }

    public int getNodeIDCount(){
        return numOfNodes;
    }
    
    public long[] getNodeIds(){
        ByteArrayInputStream bin = new ByteArrayInputStream(load);
        DataInputStream din = new DataInputStream(bin);
        long[] nodeIDs = new long[numOfNodes];
        try {
            din.read(load, 0, 10);  // Skip Mu part
            for(int i = 0; i < numOfNodes; i++){
                nodeIDs[i] = din.readLong();   
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return nodeIDs;
    }

    public byte[] marshall() {
		return load;
    }

    public void demarshall(byte[] data) {
        load = data;
        numOfNodes = (load.length - MU_BUFF_SIZE)/8;
   }

    /**
     * Returns the lenght of this payload in bytes
     *
     * @return Length of the message in bytes
     */
    public int size() {
        return load.length;
    }

    // todo: comment out before real deployment
	public static void main(String[] args){
		MessagePayloadFuzzy encoder = new MessagePayloadFuzzy();
		encoder.setValue(MessagePayloadFuzzy.OFFSET_FREEZING, 0.345);
		encoder.setValue(MessagePayloadFuzzy.OFFSET_COLD, 0.655);
		encoder.setValue(MessagePayloadFuzzy.OFFSET_WARM, 0);
		encoder.setValue(MessagePayloadFuzzy.OFFSET_IDEAL, 0.2394);
		encoder.setValue(MessagePayloadFuzzy.OFFSET_NONCRITICAL, 0.444);

		System.out.println("Bytearray is: " + encoder.marshall());

		encoder.demarshall(encoder.marshall());

        encoder.addNodeID(12335678);
        encoder.addNodeID(12334448);
        encoder.addNodeID(10005678);

        System.out.println("The payload is " + encoder.size() + " bytes long");
        System.out.println("There are " + encoder.getNodeIDCount() + " nodes in the payload");
        System.out.println("The NodeIds lenght is " + encoder.getNodeIds().length);
        long[] ids = encoder.getNodeIds();
        for(int i = 0; i < ids.length; i++){
            System.out.println("Node " + i + ": " + ids[i]);
        }

        encoder.demarshall(encoder.marshall());
        System.out.println("There are " + encoder.getNodeIDCount() + " nodes in the payload");

		System.out.println("Freezing is: " + encoder.getValue(MessagePayloadFuzzy.OFFSET_FREEZING));
		System.out.println("Cold is: " + encoder.getValue(MessagePayloadFuzzy.OFFSET_COLD));
		System.out.println("Warm is: " + encoder.getValue(MessagePayloadFuzzy.OFFSET_WARM));
		System.out.println("Ideal is: " + encoder.getValue(MessagePayloadFuzzy.OFFSET_IDEAL));
		System.out.println("NonCritical is: " + encoder.getValue(MessagePayloadFuzzy.OFFSET_NONCRITICAL));
		System.out.println("Critical is: " + encoder.getValue(MessagePayloadFuzzy.OFFSET_CRITICAL));
	}
}
