/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umb.cs.cluster;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.util.IEEEAddress;
import edu.umb.cs.tinydds.L3.L3;
import edu.umb.cs.tinydds.Sender;
import edu.umb.cs.tinydds.io.GPS;
import edu.umb.cs.tinydds.io.SimulatedGPS;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Scenario 1)
 * Static Clustering:
 * a) BS requests information from all nodes in the network.
 *
 * @author francesco
 */
public class ClusterManager implements GlobalConfiguration, Runnable {

    private static ClusterManager clusterManager;
    private static boolean isClusterHead;
    private static boolean isBaseStation;
    private static Sender defaultMailer;

    private ClusterStrategy strategy;
    private GPS gps;
    protected TimerTask task1, task2, task3;
    protected Timer clusterTimer;
    protected Logger logger;

    // These variables for BS use only
    private Hashtable networkMembers; // Nodes in the network and their last ping
    private Hashtable clusters; // CH in the network and their vectors of CMs
    
    // These variables for CH and CM
    private Vector clusterMembers;
    private long clusterHead;  // Holds value of the CH for this CM (if this is a CM)

    public static synchronized ClusterManager getInstance() {
        if (clusterManager == null) {
            clusterManager = new ClusterManager();
        }
        return clusterManager;
    }

    private ClusterManager() {
        logger = new Logger("ClusterManager");

        clusterMembers = new Vector();
        isClusterHead = false;
        clusterHead = NOT_ASSIGNED;
        if(isBaseStation){
            networkMembers = new Hashtable();
            clusters = new Hashtable();
        }
        strategy = new simpleClusterStrategy();
        gps = SimulatedGPS.getInstance();
        clusterTimer = new Timer();
        logger.logInfo("initiated");
    }

    /**
     * Start differnt clustering services based on the clustering paradigm
     * <code>STATIC or DYNAMIC</code> and or clustering role <code>BS/CH/CM</code>
     */
    public void run(){
        if(isBaseStation){
            logger.logInfo("run:Base Station");
            creatBaseStationTasks();
            clusterTimer.scheduleAtFixedRate(task1, 2, PING_INTERVAL * ONE_SECOND);
            logger.logInfo("run:task1: collect network info every "
                    + PING_INTERVAL + "s: start in 2s.");
            if(DEBUG && DBUG_LVL >= LIGHT){
                clusterTimer.scheduleAtFixedRate(task2, 4, PING_INTERVAL * ONE_SECOND);
                logger.logInfo("run:task2: show nodes in network every "
                        + PING_INTERVAL + "s: start in 4s.");
            }
        }
    }

    /**
     * Set the strategy for this Cluster Manager.
     * 
     * @param newStrategy   The new strategy we wish the Cluster Manager 
     *                      to implement
     * @return  The old strategy
     */
    public ClusterStrategy setStrategy(ClusterStrategy newStrategy){
        ClusterStrategy oldStrategy = this.strategy;
        this.strategy = newStrategy;
        logger.logInfo("Replacing old strategy: \"" + oldStrategy.describe() +
                "\" with :\"" + newStrategy.describe());
        return oldStrategy;
    }

    public static void setDefaultMailer(Sender defaultMailer) {
        ClusterManager.defaultMailer = defaultMailer;
    }

    /**
     * Add a cluster members (CM) to this cluster head.
     *
     * @param ieeeAddress   A <code>IEEEAddress</code> string
     * @param lat   Latitude of the aspiring member
     * @param lon   Longitude of the aspiring member
     * @param elev  Elevation of the aspiring member
     * @throws notClusterHeadException  if this node is not a CH
     */
    private void addMember(String ieeeAddress, double lat, double lon, double elev)
            throws notClusterHeadException{
        if (!ClusterManager.isClusterHead)
            throw new notClusterHeadException("I am not a clusterHead");
        if(strategy.acceptMember(gps, lat, lon, elev, clusterMembers.size()))
            if (!this.clusterMembers.contains(ieeeAddress)){
                this.clusterMembers.addElement(ieeeAddress);
                logger.logInfo("Adding CM " + ieeeAddress + "to CH (this) " +
                        IEEEAddress.toDottedHex(Spot.getInstance().
                        getRadioPolicyManager().getIEEEAddress()));
            }
    }

    /**
     * Add a cluster members (CM) to this cluster head.
     *
     * @see <code>addMember(String ieeeAddress)</code>
     * @param ieeeLong  long representing the member ID
     * @throws notClusterHeadException
     */
    private void addMember(long ieeeLong, double lat, double lon, double elev)
            throws notClusterHeadException{
        addMember(IEEEAddress.toDottedHex(ieeeLong), lat, lon, elev);
    }

    private void removeMember(String ieeeAddress){
        clusterMembers.removeElement(ieeeAddress);
    }

    private void removeMembers(Vector v, String ieeeAddress){
        v.removeElement(ieeeAddress);
    }

    private void removeMember(long ieeeLong){
        clusterMembers.removeElement(IEEEAddress.toDottedHex(ieeeLong));
    }

    public static void setAsBaseStation(){
        isBaseStation = true;
    }

    public boolean isBaseStation() {
        return isBaseStation;
    }

    public boolean isClusterHead() {
        return isClusterHead;
    }

    /**
     *
     * @param msg    Incoming message of clustering type
     * @param mailer An object that implements the Sender interface
     */
    public void loadMessage(ClusterMessage msg, Sender mailer){
        byte code = msg.getMsgCode();
        if (code == ClusterMessage.NEED_INFO){
            logger.logInfo("loadMessage:received message: NEED_INFO");
            if(!isBaseStation){
                ClusterMessage response = new ClusterMessage();
                response.setMsgCode(ClusterMessage.MY_INFO);
                response.setReceiver(msg.getOriginator()); // return to sender
                response.setOriginator(L3.getAddress());
                mailer.send(response);
            }
        }
        else if (code == ClusterMessage.MY_INFO){
            logger.logInfo("loadMessage:received message: MY_INFO");
            if(isBaseStation){
                Long newID = new Long(msg.getOriginator());
                if(!networkMembers.contains(newID)) { // New node in the network
                    networkMembers.put(newID, new Date());

                    // Determine if what this new node should be.
                    // 1) check to see if it can become a member of any of the existing clusters
                    // 1.1) if possible send message <YOU_HAVE_NEW_MEMBER> to CH and <YOUR_CH> to the new node
                    //      alternatively delegate adding as CM to the CH, which will send a <MY_CM> message to it
                    // 2) otherwise, make this node a new CH and create an (empty) vector of members
                    // 3) send message <YOU_ARE_CH> to the node
                }
            }
        }
        else if (code == ClusterMessage.NEED_CH){
            logger.logInfo("loadMessage:received message: NEED_CH");

        }
        else {
            logger.logInfo("loadMessage:received message: SOMETHING ELSE");
        }
    }

    private void creatBaseStationTasks(){
        /**
         * task1: ping all nodes in the network (all those that can hear this
         *        message) to send their ID <NEED_INFO>
         * conditions: this task is for BS only in the STATIC clustering scenario.
         * expected: nodes that hear this request will respond with <MY_INFO>
         */
        logger.logInfo("creatBaseStationTasks");
        task1 = new TimerTask(){
            public void run() {
                logger.logInfo("task1:Broadcast NEED_INFO");
                ClusterMessage msg = new ClusterMessage();
                msg.setMsgCode(ClusterMessage.NEED_INFO);
                msg.setReceiver(L3.BROADCAST_ADDRESS);
                msg.setOriginator(L3.getAddress());
                defaultMailer.send(msg);
            }
        };

        /**
         * task2: display to the users the status of the network and its clusters
         * conditions: this is task is fro BS only, probably moderated under a debug level
         */
        task2 = new TimerTask(){
            public void run() {
                logger.logInfo("task2:Show Network nodes");
                Enumeration members = networkMembers.keys();
                String s = "\nActive Network Members:\n";
                while(members.hasMoreElements()){
                    Long l = (Long) members.nextElement();
                    s += "nodeID: " + IEEEAddress.toDottedHex(l.longValue()) +
                         " ping: " + networkMembers.get(l) + "\n";
                }
                logger.logInfo(s);
            }
        };
    }

    private void createClusterHeadTasks(){

    }

    private void createClusterMemberTasks(){
        /**
         * task3: check to see if this node has a clusterhead, if not send a
         *        cluster message of type NEED_CH
         * conditions: this task is for CM only in the STATIC clustering scenario.
         */
        task3 = new TimerTask(){
            public void run() {
                logger.logInfo("task2: looking for CH");
            }
        };
    }
}
