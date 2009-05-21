/*$Id: Application.java,v 1.2 2008/08/29 20:26:44 pruet Exp $

Copyright (c) 2008 University of Massachusetts, Boston 
All rights reserved. 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
Neither the name of the University of Massachusetts, Boston  nor 
the names of its contributors may be used to endorse or promote products 
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OF
MASSACHUSETTS, BOSTON OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umb.cs.tinydds;

import com.sun.spot.peripheral.Spot;
// import com.sun.spot.sensorboard.peripheral.TemperatureInput;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import edu.umb.cs.tinydds.cluster.ClusterManager;
import edu.umb.cs.tinydds.DDSimpl.DataReaderImpl;
import edu.umb.cs.tinydds.DDSimpl.DataReaderListenerImpl;
import edu.umb.cs.tinydds.DDSimpl.DomainParticipantImpl;
import edu.umb.cs.tinydds.io.GPS;
import edu.umb.cs.tinydds.io.SimulatedGPS;
import edu.umb.cs.tinydds.io.LED;
import edu.umb.cs.tinydds.io.LightSensor;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import edu.umb.cs.tinydds.utils.Observer;
import edu.umb.cs.tinydds.io.Switch;
import edu.umb.cs.tinydds.io.SwitchStatus;
import edu.umb.cs.tinydds.io.TempSensor;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.omg.dds.ContentFilteredTopic;
import org.omg.dds.DataReader;
import org.omg.dds.DataReaderListener;
import org.omg.dds.DataWriter;
import org.omg.dds.DomainParticipant;
import org.omg.dds.Publisher;
import org.omg.dds.Subscriber;
import org.omg.dds.Topic;

/**
 *
 * @author pruet
 * @author francesco    Added gps sensor support 04/25/09
 */

/* Testing application, press left hardware button for subscribing,
 * right hardware button for publishing 
 */
public class Application implements Observer, GlobalConfiguration{

    protected final long LIGHT_DELAY = 5 * 1000;
    protected final long TEMP_DELAY = 10 * 1000;
    
    protected DomainParticipant domainParticipant = null;
    protected Publisher publisher = null;
    
    protected DataWriter lightDataWriter = null;
    protected DataWriter tempDataWriter = null;
    
    protected Switch switchs = null;
    protected Logger logger = null;
    protected Subscriber subscriber = null;
    protected DataReader dataReader = null;
    protected DataReaderListener dataReaderListener = null;
    protected LED leds = null;
    protected LightSensor lightSensor = null;
    protected TempSensor tempSensor = null;
    protected GPS gps = null;  // Encapsulates real gps or simulates one
    protected boolean isPublishing = false;
    protected Timer measurementTimer;
    protected TimerTask lightTimerTask, tempTimerTask;
    
    public Application() {
        
        // Misc initialization
        logger = new Logger("Application");
        switchs = new Switch();
        ((Observable) switchs).addObserver(this);
        leds = new LED();
        
        lightSensor = new LightSensor();
        tempSensor = new TempSensor();
        
        // Hard coded box of 60x60 nautical miles near UMass for GPS simulation
        gps = SimulatedGPS.getInstance();

        // Create publisher
        domainParticipant = new DomainParticipantImpl();
        Topic lightTopic = domainParticipant.create_topic("LightSensor", "light"); 
        Topic tempTopic = domainParticipant.create_topic("TempSensor", "temp");
        
        publisher = domainParticipant.create_publisher(null);
        
        lightDataWriter = publisher.create_datawriter(lightTopic, null);  
        tempDataWriter = publisher.create_datawriter(tempTopic, null);
        
        measurementTimer = new Timer();
        
        createTimerTasks();

        if(CLUSTERING)
            ClusterManager.getInstance().run(); // Start all clustering tasks
        
        if(DEBUG && DBUG_LVL >= LIGHT) {
            logger.logInfo("initiate: NODE ID: " +
                    IEEEAddress.toDottedHex(Spot.getInstance().
                    getRadioPolicyManager().getIEEEAddress()));
            logger.logInfo("lat = " + gps.getLatitude() + "; lon = " +
                           gps.getLongitude() + "; elev = " + gps.getElevation());
        }
    }

    
    private void createTimerTasks(){
        lightTimerTask = new TimerTask(){
            public void run() {
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("publish data");
                byte data[] = new byte[4];
                try {
                    Utils.writeBigEndInt(data, 0, lightSensor.getValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                MessagePayloadBytes payload = new MessagePayloadBytes(data);
                lightDataWriter.write(payload);
            }
        };
        
        
        tempTimerTask = new TimerTask(){
            public void run() {
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("publish data");
                byte data[] = new byte[4];
                try {
                    Utils.writeBigEndInt(data, 0, tempSensor.getValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                MessagePayloadBytes payload = new MessagePayloadBytes(data);
                tempDataWriter.write(payload);
            }
        };
    }
    
    public void update(Observable obj, Object arg) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("update");
        if (obj.equals(switchs)) {
            SwitchStatus status = (SwitchStatus) arg;
            
            if (status.getChanged() == 1) {     // publish
                
                isPublishing = !isPublishing;
                
                if(isPublishing){ // turn on publishing
                    if(DEBUG && DBUG_LVL >= LIGHT)
                        logger.logInfo("turned on publishing");
                    measurementTimer.scheduleAtFixedRate(lightTimerTask, 0, LIGHT_DELAY);
                    measurementTimer.scheduleAtFixedRate(tempTimerTask, 0, TEMP_DELAY);
                }
                else {
                    if(DEBUG && DBUG_LVL >= LIGHT)
                        logger.logInfo("turned off publishing");
                 
                    //this is probably a threading issue
                    lightTimerTask.cancel();
                    tempTimerTask.cancel();
                    createTimerTasks();  
                }
                
            } 
            else if (status.getChanged() == 0) {
                // Create subscriber
                // FIXME: Some flag should be put here, we need to publish only once
                if(DEBUG && DBUG_LVL >= MEDIUM)
                    logger.logInfo("subscribe");
                
                subscriber = domainParticipant.create_subscriber(null);
                Topic topic = domainParticipant.create_topic("LightSensor", "light");
                
                String filter_expression = "Phenom > %n";
                String[] expression_parameters = {"100"};
                
                ContentFilteredTopic filteredTopic = domainParticipant.create_contentfilteredtopic("LightSensorZ", topic, filter_expression, expression_parameters);
        
                dataReaderListener = new DataReaderListenerImpl();           
                dataReader = subscriber.create_datareader(filteredTopic, dataReaderListener);
                
                ((DataReaderImpl) dataReader).addObserver(this);
            }
        } 
        else if (obj.equals(dataReader)) {
            // data from DataReader
            PubSubMessage msg = (PubSubMessage) arg;
            MessagePayloadBytes payload = (MessagePayloadBytes) msg.getPayload();
            int light = Utils.readBigEndInt(payload.get(), 0);
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("We got data from " + IEEEAddress.toDottedHex(msg.getOriginator())
                        + " value = " + light);
            leds.setRGB(6, 0, light, 0);
            leds.setOn(6);
        }
    }
}
