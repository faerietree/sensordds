package edu.umb.cs.tinydds.fuzzyAggregation;

import edu.umb.cs.tinydds.utils.Geometry;
import java.util.Vector;
import java.io.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html
 * 
 * http://wiki.forum.nokia.com/index.php/How_to_create_a_high_score_database_in_Java_ME
 * 
 * @author matt
 */
public class AggregatorImpl implements Aggregator {

    private static Aggregator agg;
    private RecordStore store;
    
    public static Aggregator getInstance(){
        if(agg == null){
            agg = new AggregatorImpl();
        }
        return agg;
    }

    protected AggregatorImpl() {
        try {
            store = RecordStore.openRecordStore("measurements", true);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addData(String phenom, Geometry geom, long timestamp, double value) {
                    
        if(store == null)
        {            
            try 
            { 
                 store.addRecord(convertRecord(phenom,geom,timestamp,value),0,0);
            }
            catch (Exception e) 
            { 
                System.err.println("Opening RecordStore failed in Aggregator implementation");
                e.printStackTrace();
            } 
        } 
        else
        {
            try 
            { 
                store.addRecord(convertRecord(phenom,geom,timestamp,value),0,0);
                // put the code to add the record to record store
                // code to create the name of the particular record.
                
            }
            catch(RecordStoreNotFoundException error)
            {
                System.out.println("Record store not found. Creating a new record store.");
                store.openRecordStore("measurements", true)
                this.addData(phenom, geom, timestamp, value);
                store.addRecord(convertRecord(phenom,geom,timestamp,value),0,0);                
            }
            catch(RecordStoreFullException error)
            {
                System.out.println("RecordStore full. Removing oldest Record ");
                store.deleteRecord(1);
                store.addRecord(convertRecord(phenom,geom,timestamp,value),0,0);
            }
            catch(Exception e) { e.printStackTrace(); } 
        }
        // End samatha.
    }

    public Vector getPhenomAggregation(String function, String phenom) {
        // give the maximum element among the "phenom" values with function x. 
        // at present this is not valuable, how ever, we are still having it for the future functionality.
        
        if(function.equals(MAX))
        {
           // call max.
         
        }
        else if (function.equals(AVG))
        {
            // call average.
        }
        else if (function.equals(MIN))
        {
            //  call minimum.
        }
        return null;
        
    }

    public Vector getTemporalAggregation(String function, String phenom) 
    {
        if(function.equals(MAX))
        {
            // call maximum function.
         
        }
        else if (function.equals(AVG))
        {
            // call average.
        }
        else if (function.equals(MIN))
        {
            //  call minimum.
        }
        
        return null;
    }
    
    public void registerAggregation(String function, String phenom) {
               
    }

    public void unregisterAggregation(String function, String phenom) {
    }

    private byte[] convertRecord(String phenom, Geometry geom, long timestamp, double value)
    {
      byte[] outputRecord = null;
      try
      {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream outputDataStream = new DataOutputStream(outputStream);
        outputDataStream.writeUTF(phenom);
        /*
         * for some strange reason, following two lines are showing error. 
        outputDataStream.writeDouble(geom.getX());
        outputDataStream.writeDouble(geom.getY()); */
        outputDataStream.writeLong(timestamp);
        outputDataStream.writeDouble(value);
        outputDataStream.flush();               
        outputRecord = outputStream.toByteArray();
        outputStream.reset();   
        outputStream.close();
        outputDataStream.close();
      }
      catch ( Exception error)
      {
        System.err.println("Error while converting data in to ByteArray.");
        error.printStackTrace();
      }
      return outputRecord;
    }
    
    private Vector getRecord(byte record[])
    {
      Vector rec = new Vector();
      try
          {   
            String phenom = null;
            long timestamp = 0;
            double value;
            /* for geometry  // uncomment when geometry works.*/
            double x, y; 
            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(record);
            DataInputStream inputDataStream = new DataInputStream(inputStream);

             phenom = inputDataStream.readUTF();
             timestamp = inputDataStream.readLong();
             /* for geometry // uncomment when geometry works.  */
              x = inputDataStream.readDouble();
              y = inputDataStream.readDouble();
            
             value = inputDataStream.readDouble();
             
             inputStream.reset();
             inputStream.close();
             inputDataStream.close();
             
             rec.addElement(phenom);
             rec.addElement(new Long(timestamp));
             /** for geometry // uncomment when geometry works.
              rec.addElement(new Geometry(x,y));
              */
             rec.addElement(new Double(value));
             
          }
       catch (Exception error)
          {
            System.err.println("Error while converting record from ByteArray in to record.");
            error.printStackTrace();
          }
        return rec;
    }
 
    private double max(String phenom)
    {
     double maxValue = -1;
     Vector rec;
      try 
      {
            byte record[];
            for(int i = 0; i < store.getNumRecords(); i++)
            {
                record = store.getRecord(i);
                rec = getRecord(record);
              //  rec.lastElement().

            }
      }
      catch( Exception e)
      {
          
      }
      return maxValue;
    }
    
    private double min(String phenom)
    {
        double minValue = -1;
        
        return minValue;
        
    }
    
    private double avg(String phenom)
    {
        double avgValue = -1,sum = -1;
        int count = 0;
        
               
        return avgValue;
    }
}
