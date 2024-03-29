package org.omg.dds;

import edu.umb.cs.tinydds.MessagePayload;


/**
* org/omg/dds/DataWriter.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dds_dcps.idl
* Monday, August 18, 2008 12:58:40 AM EDT
*/

public interface DataWriter extends  org.omg.dds.Entity 
{
  org.omg.dds.DataWriterListener get_listener ();
  Topic get_topic ();
  org.omg.dds.Publisher get_publisher ();
  void write(MessagePayload msg);
  int set_listener (org.omg.dds.DataWriterListener a_listener);
} // interface DataWriter
