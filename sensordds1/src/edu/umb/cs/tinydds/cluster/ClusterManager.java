/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umb.cs.tinydds.cluster;

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
import edu.umb.cs.tinydds.io.LED;
import edu.umb.cs.tinydds.fuzzyAggregation.FuzzyEngine;

/**
 *
 * @author francesco
 */
public class ClusterManager implements GlobalConfiguration, Runnable {

    private static ClusterManager clusterManager;
    private static boolean isBaseStation;
    private static Sender defaultMailer;
    private static boolean isClusterHead;

    private ClusterStrategy strategy;
    private GPS gps;
    protected TimerTask task1, task2, task3;
    protected Timer clusterTimer;
    protected Logger logger;

    // These variables for BS use only
    private Hashtable networkMembers; // Node IDs in the network and their last ping
    private Hashtable clusters; // CH (NODEs) in the network and their vectors of CMs
    
    // These variables for CH and CM
    private Vector clusterMembers;
    private long clusterHead;  // Holds value of the CH for this CM (if this is a CM)
    private int clusterColorPosition;


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
        strategy = new SimpleClusterStrategy();
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
            if(DEBUG && DBUG_LVL >= MEDIUM)
                logger.logInfo("run:Base Station");
            
            creatBaseStationTasks();
            clusterTimer.scheduleAtFixedRate(task1, PING_DELAY, PING_INTERVAL * ONE_SECOND);
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("run:task1: collect network info every "
                    + PING_INTERVAL + "s: start in " + PING_DELAY + "s.");

            if(DEBUG && DBUG_LVL >= LIGHT){
                clusterTimer.scheduleAtFixedRate(task2, DISP_DELAY, PING_INTERVAL * ONE_SECOND);
                logger.logInfo("run:task2: show nodes in network every "
                        + PING_INTERVAL + "s: start in " + DISP_DELAY + "s.");
            }

            clusterTimer.scheduleAtFixedRate(task3, EXP_DELAY, EXPIRE_INTERVAL * ONE_SECOND);
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("run:task3: remove unresponsive nodes every "
                    + EXPIRE_INTERVAL + "s: start in " + EXP_DELAY + "s.");
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
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("Replacing old strategy: \"" + oldStrategy.describe()
                    + "\" with :\"" + newStrategy.describe());
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
            throws NotClusterHeadException{
        if (!ClusterManager.isClusterHead)
            throw new NotClusterHeadException("I am not a clusterHead");
        if(strategy.acceptMember(gps, lat, lon, elev, clusterMembers.size()))
            if (!this.clusterMembers.contains(ieeeAddress)){
                this.clusterMembers.addElement(ieeeAddress);
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("Adding CM " + ieeeAddress + "to CH (this) "
                            + IEEEAddress.toDottedHex(Spot.getInstance().
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
            throws NotClusterHeadException{
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
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("loadMessage:received message: NEED_INFO");
            if(!isBaseStation){
                messageNeedInfoHandler(msg, mailer);
            }
        }

        else if (code == ClusterMessage.MY_INFO){
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("loadMessage:received message: MY_INFO");
            if(isBaseStation) {
                messageMyInfoHandler(msg, mailer);
            }
        }

        else if (code == ClusterMessage.TAKE_CMS){
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("loadMessage:received message: TAKE_CMS");
            // The BS has already determined that these nodes should be CM of this CH
            if(isClusterHead){  // Just to make sure, but this message is only sent to CHs
                messageTakeCmsHandler(msg, mailer);
            }
        }

        else if (code == ClusterMessage.YOU_ARE_CH){
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("loadMessage:received message: YOU_ARE_CH");
            isClusterHead = true; // Now this node is a clusterhead
            LED leds = new LED();
            clusterColorPosition = msg.getColorIndex();
            leds.setColor(0, ClusterColors.getColor(clusterColorPosition));
            leds.setColor(1, ClusterColors.getColor(clusterColorPosition));
            leds.setOn(0);
            leds.setOn(1);
         }

        else if (code == ClusterMessage.YOUR_CH){
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("loadMessage:received message: YOUR_CH");
            if(!isClusterHead){
                messageYourchHandler(msg);
            }
        }
        else if (code == ClusterMessage.FUZZY){
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("loadMessage:received message: FUZZY");
            if(!isClusterHead){
                messageFuzzyHandler(msg);
            }
        }

        else
        {
            if(DEBUG && DBUG_LVL >= LIGHT)
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
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("creatBaseStationTasks");
        task1 = new TimerTask(){
            public void run() {
                if(DEBUG && DBUG_LVL >= MEDIUM)
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
         * conditions: this task is for BS only
         */
        task2 = new TimerTask(){
            public void run() {
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("task2:Show Network nodes");
                Enumeration members = networkMembers.keys();
                String s = "\nActive Network Members:\n";
                while(members.hasMoreElements()){
                    Long l = (Long) members.nextElement();
                    s += "nodeID: " + IEEEAddress.toDottedHex(l.longValue()) +
                         " ping: " + networkMembers.get(l) + "\n";
                }
                s += "\nActive Clusters\n";
                Enumeration chs = clusters.keys();
                Node ch, cm;
                Vector cMembers;
                while(chs.hasMoreElements()){
                    ch = (Node) chs.nextElement();
                    s += "CH ID: " + IEEEAddress.toDottedHex(ch.getNodeID().longValue()) + "\n";

                    cMembers = (Vector) clusters.get(ch);
                    Enumeration cms = cMembers.elements();
                    while(cms.hasMoreElements()){
                        cm = (Node) cms.nextElement();
                        s += ">>> CM ID: ";
                        s += IEEEAddress.toDottedHex(cm.getNodeID().longValue()) + "\n";;
                    }
                }
                logger.logInfo(s);
            }
        };

        /**
         * task3: Remove unresponsive nodes from the list of nodes and the active
         *        clusters.
         * conditions: this task is for base stations only.  If the node to be
         *             removed is a CH, its CM will be removed too (at the next
         *             ping cycle, they will be re-added to another CH);
         */
        task3 = new TimerTask(){
            public void run() {
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("task3:Remove unresponsive nodes");
                Enumeration members = networkMembers.keys();
                // String s = "\nActive Network Members:\n";
                Date now = new Date();
                while(members.hasMoreElements()){
                    Long l = (Long) members.nextElement();
                    if(DEBUG && DBUG_LVL >= LIGHT)
                          logger.logInfo("Now is: " + now.getTime() +
                                  " Last ping is: " + ((Date) networkMembers.get(l)).getTime());
                    if((now.getTime() - ((Date) networkMembers.get(l)).getTime())
                            > UNRESPONSIVE_TIME){
                        removeNode(l);
                    }
                }
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
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("task2: looking for CH");
            }
        };
    }

    /**
     * Does the dirty work in the case the message is <code>MY_INFO</code>
     * @param msg
     * @param mailer
     */
    private void messageMyInfoHandler(ClusterMessage msg, Sender mailer){
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("messageMyInfoHandler");
        Long newID = new Long(msg.getOriginator());
        if(DEBUG && DBUG_LVL >= LIGHT)
            logger.logInfo("Incoming message from: " +
                    IEEEAddress.toDottedHex(msg.getOriginator()));
        if(!networkMembers.containsKey(newID)) { // New node not in the network
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("New node ID not in the network");
            networkMembers.put(newID, new Date());
            Node newNode = new Node(newID, msg.getSenderLat(), msg.getSenderLon(),
                                    msg.getSenderElev());
            boolean placed = false;
            // Determine if what this new node should be.
            // 1) check to see if it can become a member of any of the existing clusters
            if(!clusters.isEmpty()){
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("There are clusters");
                Enumeration clusterHeads = clusters.keys();
                Vector cms = null;
                Node ch = null;
                int cmCount;
                while(clusterHeads.hasMoreElements() && !placed){
                    ch = (Node) clusterHeads.nextElement();
                    cms = (Vector) clusters.get(ch);
                    cmCount = cms.size();
                    if(strategy.acceptMember(ch.getLatitude(), ch.getLongitude(),
                                             ch.getElevation(), msg.getSenderLat(),
                                             msg.getSenderLon(), msg.getSenderElev(),
                                             cmCount)){
                        placed = true;
                        break;
                    }
                }
                if (placed){ // This cluster can accept the new node
                    cms.addElement(new Node(newID, msg.getSenderLat(),
                        msg.getSenderLon(), msg.getSenderElev()));
                    // Notify clusterhead of the new cluster member
                    ClusterMessage response = new ClusterMessage();
                    MessagePayloadCluster payload = new MessagePayloadCluster();
                    payload.addNode(newNode);
                    response.setPayload(payload);
                    response.setMsgCode(ClusterMessage.TAKE_CMS);
                    response.setReceiver(ch.getNodeID().longValue()); // send to CH
                    response.setOriginator(L3.getAddress());
                    mailer.send(response); // CH will ask New node to be her CM
                    return;
                }
             }
            // new node needs to become a new cluster (i.e. a CH)
            Vector newMembers = new Vector();
            this.clusters.put(newNode, newMembers);
            ClusterMessage response = new ClusterMessage();
            response.setMsgCode(ClusterMessage.YOU_ARE_CH);
            int colorIndex = 0;  // Corresponds to Green - an error flag here
            try {
                colorIndex = ClusterColors.getPosition();
                ClusterColors.nextColor(); // Increment the color
            } catch (OutOfColorsException ex) {
                ex.printStackTrace();
            }
            response.setColorIndex(colorIndex);
            response.setReceiver(msg.getOriginator());
            response.setOriginator(L3.getAddress());
            mailer.send(response);
        }
        else { // Not a new node, just update the timestamp
            networkMembers.put(newID, new Date());
        }
    }

    private void messageNeedInfoHandler(ClusterMessage msg, Sender mailer) {
        ClusterMessage response = new ClusterMessage();
        response.setMsgCode(ClusterMessage.MY_INFO);
        response.setReceiver(msg.getOriginator()); // return to sender
        response.setOriginator(L3.getAddress());
        mailer.send(response);
    }

        private void messageTakeCmsHandler(ClusterMessage msg, Sender mailer) {
        // Just to make sure, but this message is only sent to CHs
        // The BS has already determined that these nodes should be CM of this CH
        MessagePayloadCluster payload = (MessagePayloadCluster) msg.getPayload();
        Node[] nodes = payload.getNodes();
        int size = nodes.length;
        if (DEBUG && DBUG_LVL >= LIGHT) {
            logger.logInfo("There are " + size + " records in the payload");
        }
        for (int i = 0; i < size; i++) {
            // Set each node as a cluster member
            ClusterMessage response = new ClusterMessage();
            response.setMsgCode(ClusterMessage.YOUR_CH);
            response.setColorIndex(clusterColorPosition);
            response.setReceiver(nodes[i].getNodeID().longValue());
            response.setOriginator(L3.getAddress());
            mailer.send(response);
            if (DEBUG && DBUG_LVL >= LIGHT) {
                logger.logInfo("Sending TAKE_CMS to " +
                        IEEEAddress.toDottedHex(nodes[i].getNodeID().longValue())
                        + " color index is " + clusterColorPosition);
            }
        }
    }
        

    private void messageYourchHandler(ClusterMessage msg) {

        LED leds = new LED();
        clusterColorPosition = msg.getColorIndex();
        leds.setColor(0, ClusterColors.getColor(clusterColorPosition));
        leds.setOn(0);
    }

    private void messageFuzzyHandler(ClusterMessage msg) {

        FuzzyEngine.getInstance().processPayload(msg.getPayload());
    }


    
    protected void removeNode(Long nodeID){
        networkMembers.remove(nodeID);

        Enumeration chs = clusters.keys();
        Node ch;
        Vector cMembers;
        while(chs.hasMoreElements()){
            ch = (Node) chs.nextElement();
            cMembers = (Vector) clusters.get(ch);
            if (ch.getNodeID() == nodeID){ // Node is a CH
                // Remove her CMs (also remove from the networkMembers)
                Enumeration cms = cMembers.elements();
                Node cm;
                while(cms.hasMoreElements()){
                    networkMembers.remove(((Node) cms.nextElement()).getNodeID());
                }
                clusters.remove(ch);
            }
            else {  // Node must be a CM of some CH, need to dig into the CMs
                Enumeration cms = cMembers.elements();
                Node cm;
                while(cms.hasMoreElements()){
                    cm = (Node) cms.nextElement();
                    if(cm.getNodeID() == nodeID){
                        cMembers.removeElement(cm);
                    }
                }
            }
        }
        if(DEBUG && DBUG_LVL >= LIGHT)
            logger.logInfo("Removed unresponsive node " +
                    IEEEAddress.toDottedHex(nodeID.longValue()) + " from network");
    }
}
