/*$Id: PublisherImpl.java,v 1.2 2008/08/29 20:26:44 pruet Exp $

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
import edu.umb.cs.tinydds.L3.L3;
import edu.umb.cs.tinydds.Message;
import edu.umb.cs.tinydds.MessagePayload;
import edu.umb.cs.tinydds.OERP.OERP;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import java.util.Hashtable;
import org.omg.dds.DataWriter;
import org.omg.dds.DataWriterListener;
import org.omg.dds.Publisher;
import org.omg.dds.PublisherListener;

/**
 *
 * @author pruet
 */
public class PublisherImpl extends Observable implements Publisher {

    Logger logger;
    Hashtable dataWriterTable;
    PublisherListener publisherListener = null;
    static OERP oerp = null;

    public PublisherImpl() {
        super();
        logger = new Logger("PublisherImpl");
        logger.logInfo("initiate");
        dataWriterTable = new Hashtable();
    }

    public DataWriter create_datawriter(String topic, DataWriterListener a_listener) {
        logger.logInfo("create_datawriter");
        if (dataWriterTable.get(topic) == null) {
            DataWriter dataWriter = new DataWriterImpl(this, topic);
            dataWriter.set_listener(a_listener);

            dataWriterTable.put(topic, dataWriter);
        }
        return (DataWriter) dataWriterTable.get(topic);
    }

    public int set_listener(PublisherListener a_listener) {
        logger.logInfo("set_listener");
        publisherListener = a_listener;
        return DDS.SUCCESS;
    }

    public PublisherListener get_listener() {
        logger.logInfo("get_listener");
        return publisherListener;
    }

    public void publish(DataWriter dataWriter, MessagePayload payload) {
        logger.logInfo("publish:topic " + dataWriter.get_topic());
        Message msg = new Message(payload);
        msg.setSubject(Message.SUBJECT_DATA);
        msg.setTopic(dataWriter.get_topic());
        msg.setOriginator(L3.getAddress());
        if (oerp == null) {
            logger.logError("publish:OERP is not connected");
        }
        oerp.send(msg);
    }

    public void setOERP(OERP oerp) {
        PublisherImpl.oerp = oerp;
    }
}
