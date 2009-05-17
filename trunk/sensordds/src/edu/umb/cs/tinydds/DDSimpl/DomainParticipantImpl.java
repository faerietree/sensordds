/*$Id: DomainParticipantImpl.java,v 1.2 2008/08/29 20:26:44 pruet Exp $

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

import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
import java.util.Hashtable;
import org.omg.dds.ContentFilteredTopic;
import org.omg.dds.DomainParticipant;
import org.omg.dds.Publisher;
import org.omg.dds.PublisherListener;
import org.omg.dds.Subscriber;
import org.omg.dds.SubscriberListener;
import org.omg.dds.Topic;

/**
 *
 * @author pruet
 */
public class DomainParticipantImpl implements DomainParticipant, GlobalConfiguration {

    protected static Publisher publisher = null;
    protected static Subscriber subscriber = null;
    protected Hashtable topics;
    protected Logger logger;

    public DomainParticipantImpl() {
        logger = new Logger("DomainParticipantImpl");
        topics = new Hashtable();
    }

    public Publisher create_publisher(PublisherListener a_listener) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("create_publisher");
        if (publisher == null) {
            publisher = new PublisherImpl();
        }
        publisher.set_listener(a_listener);
        return publisher;
    }

    public Subscriber create_subscriber(SubscriberListener a_listener) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("create_subscriber");
        if (subscriber == null) {
            subscriber = new SubscriberImpl();
        }
        subscriber.set_listener(a_listener);
        return subscriber;
    }

    public ContentFilteredTopic create_contentfilteredtopic(String name, Topic related_topic, String filter_expression, String[] expression_parameters) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("create_contentfilteredtopic");
        if(topics.contains(name)){
            //TODO this could be an ClassCastException if there is a Topic that exists with this name
            return (ContentFilteredTopic)topics.get(name);
        }
        
        ContentFilteredTopicImpl cft = new ContentFilteredTopicImpl(this, related_topic, name, filter_expression, expression_parameters);
        
        topics.put(name, cft);
        
        return cft;
    }

    
    public Topic create_topic(String topic_name, String type_name) {
        if(DEBUG && DBUG_LVL >= MEDIUM)
            logger.logInfo("create_topic");
        
        if(topics.contains(topic_name)){
            return (Topic)topics.get(topic_name);
        }
        
        TopicImpl topic = new TopicImpl(this, topic_name, type_name);
        
        return topic;
    }
}
