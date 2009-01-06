/*$Id: DataReaderImpl.java,v 1.1 2008/08/26 19:35:07 pruet Exp $
 
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
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.utils.Observable;
import edu.umb.cs.tinydds.utils.Observer;
import org.omg.dds.DataReader;
import org.omg.dds.DataReaderListener;
import org.omg.dds.Subscriber;

/**
 *
 * @author pruet
 */
public class DataReaderImpl extends Observable implements DataReader, Observer {
    Logger logger;
    Subscriber subscriber;
    DataReaderListener dataReaderListener;
    String topic;
    public DataReaderImpl(Subscriber subscriber, String topic) {
        logger = new Logger("DataReaderImpl");
        this.subscriber = subscriber;
        this.topic = topic;
        logger.logInfo("initiate:topic=" + topic);
    }
    public int set_listener(DataReaderListener a_listener) {
        logger.logInfo("set_listener");
        dataReaderListener = a_listener;
        return DDS.SUCCESS;
    }

    public DataReaderListener get_listener() {
        logger.logInfo("get_listener");
        return dataReaderListener;
    }

    public Subscriber get_subscriber() {
        logger.logInfo("get_subscriber");
        return subscriber;
    }

    public void update(Observable obj, Object arg) {
        logger.logInfo("update");
        if(obj.equals(subscriber)) {
            logger.logInfo("push up");
            notifyObservers(arg);
        }
    }

}
