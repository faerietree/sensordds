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
import edu.umb.cs.cluster.ClusterManager;
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
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
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
 * @author francesco    This is now a Base Station
 */

/* Testing application, press left hardware button for subscribing,
 * right hardware button for publishing 
 */
public class ApplicationBS implements Observer, GlobalConfiguration {

    protected DomainParticipant domainParticipant = null;
    protected Publisher publisher = null;
    protected DataWriter dataWriter = null;
    protected Switch switchs = null;
    protected Logger logger = null;
    protected Subscriber subscriber = null;
    protected DataReader dataReader = null;
    protected DataReaderListener dataReaderListener = null;
    protected LED leds = null;
    protected LightSensor lightSensor = null;
    protected GPS gps = null;  // Encapsulates real gps or simulates one

    public ApplicationBS() {
        // Misc initialization
        logger = new Logger("Application Base");
        switchs = new Switch();
        ((Observable) switchs).addObserver(this);
        leds = new LED();
        
        // Hard coded box of 60x60 nautical miles near UMass for GPS simulation
        gps = SimulatedGPS.getInstance();

        // Create publisher
        domainParticipant = new DomainParticipantImpl();

        if(CLUSTERING)
            ClusterManager.getInstance().run(); // Start all clustering tasks
        
        if(DEBUG && DBUG_LVL >= LIGHT){
            logger.logInfo("initiate BASE STATION ID: " +
                    IEEEAddress.toDottedHex(Spot.getInstance().
                    getRadioPolicyManager().getIEEEAddress()));
            logger.logInfo("lat = " + gps.getLatitude() + "; lon = " +
                           gps.getLongitude() + "; elev = " + gps.getElevation());
        }
    }

    public void update(Observable obj, Object arg) {
        logger.logInfo("update");
        if (obj.equals(switchs)) {
            SwitchStatus status = (SwitchStatus) arg;
            if (status.getChanged() == 0) {
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
