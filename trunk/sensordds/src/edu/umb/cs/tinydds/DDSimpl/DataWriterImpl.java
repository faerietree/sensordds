/*$Id: DataWriterImpl.java,v 1.1 2008/08/26 19:35:07 pruet Exp $
 
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
import edu.umb.cs.tinydds.MessagePayload;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import org.omg.dds.DataWriter;
import org.omg.dds.DataWriterListener;
import org.omg.dds.Publisher;
import org.omg.dds.Topic;

/**
 *
 * @author pruet
 */
public class DataWriterImpl extends Observable implements DataWriter{
    Logger logger;
    Publisher publisher;
    DataWriterListener dataWriterListener;
    Topic topic;
    
    public DataWriterImpl(Publisher publisher, Topic topic) {
        logger = new Logger("DataWriterImpl");
        this.publisher = publisher;
        this.topic = topic;
        logger.logInfo("initiate:topic=" + topic);
    }
    public DataWriterListener get_listener() {
        logger.logInfo("get_listener");
        return dataWriterListener;
    }

    public Topic get_topic() {
        logger.logInfo("get_topic");
        return topic;
    }

    public Publisher get_publisher() {
        logger.logInfo("get_topic");
        return publisher;
    }

    public void write(MessagePayload msg) {
        logger.logInfo("write");
        publisher.publish(this, msg);
    }

    public int set_listener(DataWriterListener a_listener) {
        logger.logInfo("set_listener");
        dataWriterListener = a_listener;
        return DDS.SUCCESS;
    }

}
