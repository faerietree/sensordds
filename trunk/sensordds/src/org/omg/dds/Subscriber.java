package org.omg.dds;

import edu.umb.cs.tinydds.OERP.OERP;

public interface Subscriber extends org.omg.dds.Entity {

    org.omg.dds.DataReader create_datareader(String a_topic, org.omg.dds.DataReaderListener a_listener);
    //int delete_datareader(org.omg.dds.DataReader a_datareader);
    //int delete_contained_entities();
    //org.omg.dds.DataReader lookup_datareader(String topic_name);
    //int get_datareaders (org.omg.dds.DataReaderSeq readers, int sample_states, int view_states, int instance_states);
    void notify_datareaders();
    int set_listener(org.omg.dds.SubscriberListener a_listener);
    org.omg.dds.SubscriberListener get_listener();
    org.omg.dds.DomainParticipant get_participant();
    //FIXME: should move to somewhere else
    void setOERP(OERP oerp);
} // interface Subscriber
