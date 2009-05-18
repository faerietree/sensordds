/*$Id: SubscriberImpl.java,v 1.1 2008/08/26 19:35:07 pruet Exp $
 
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

package edu.umb.cs.tinydds.DDSimpl;

import edu.umb.cs.tinydds.DDS;
import edu.umb.cs.tinydds.PubSubMessage;
import edu.umb.cs.tinydds.OERP.OERP;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import edu.umb.cs.tinydds.utils.Observer;
import java.util.Hashtable;
import org.omg.dds.DataReader;
import org.omg.dds.DataReaderListener;
import org.omg.dds.DomainParticipant;
import org.omg.dds.Subscriber;
import org.omg.dds.SubscriberListener;
import org.omg.dds.TopicDescription;

/**
 *
 * @author pruet
 */
public class SubscriberImpl extends Observable 
                            implements Subscriber, Observer, GlobalConfiguration {

    protected static OERP oerp;
    Logger logger;
    Hashtable dataReaderTable;
    private SubscriberListener publisherListener;

    public SubscriberImpl() {
        super();
        logger = new Logger("SubscriberImpl");
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("initiate");
        dataReaderTable = new Hashtable();
    }

    public DataReader create_datareader(TopicDescription topic, DataReaderListener a_listener) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("create_datareader");
        if(dataReaderTable.get(topic) == null) {
            DataReader dataReader = new DataReaderImpl(this, topic);
            dataReader.set_listener(a_listener);
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("create_datareader:set observer");
            this.addObserver((DataReaderImpl) dataReader);
            dataReaderTable.put(topic, dataReader);
            oerp.subscribe(topic);
        }
        return (DataReader) dataReaderTable.get(topic);
    }

    public void notify_datareaders() {
    }

    public int set_listener(SubscriberListener a_listener) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("set_listener");
        publisherListener = a_listener;
        return DDS.SUCCESS;
    }

    public SubscriberListener get_listener() {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("get_listener");
        return publisherListener;
    }

    public DomainParticipant get_participant() {
        return null;
    }

    public void update(Observable obj, Object arg) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("update");
        
        //only care about getting subscription data ... maybe
        if(obj.equals(oerp) && (arg instanceof PubSubMessage) && (((PubSubMessage)arg).getSubject() == PubSubMessage.SUBJECT_DATA) ) {
            if(DEBUG && DBUG_LVL >= LIGHT)
                logger.logInfo("push up");
            notifyObservers(arg);
        }
    }

    public void setOERP(OERP oerp) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("setOERP");
        SubscriberImpl.oerp = oerp;
    }
}
