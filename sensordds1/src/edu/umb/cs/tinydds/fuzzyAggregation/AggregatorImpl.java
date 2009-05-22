package edu.umb.cs.tinydds.fuzzyAggregation;

import edu.umb.cs.tinydds.utils.Geometry;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
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
                try 
                {
                System.err.println("Record store not found. Creating a new record store.");
                store.openRecordStore("measurements", true);
                this.addData(phenom, geom, timestamp, value);
                store.addRecord(convertRecord(phenom,geom,timestamp,value),0,0);                
                }
            catch (Exception e)  { e.printStackTrace(); } 
             }
            catch(RecordStoreFullException error)
            {
            try{
                System.err.println("RecordStore full. Removing oldest Record ");
                store.deleteRecord(1);
                this.addData(phenom,geom,timestamp,value);
                }
            catch (Exception e)  { e.printStackTrace(); } 
            }
            catch(RecordStoreException error)
            {
                System.err.println("Exception while trying to add a record");
                error.printStackTrace();
            }
            catch(Exception e) { e.printStackTrace(); } 
        }
        // End samatha.
    }

    public Vector getPhenomAggregation(String function, String phenom) {
        // give the maximum element among the "phenom" values with function x. 
        // at present this is not valuable, how ever, we are still having it for the future functionality.
        
        Vector ret = new Vector();
        Date now = new Date();
        Geometry geom = null; 
       
        if(function.equals(MAX))
        {
           double d = max(phenom);
           
           ret.addElement(new Double(d));
           ret.addElement(new Long(now.getTime()));        
           ret.addElement(geom);
        }
        else if (function.equals(AVG))
        {
            double d = avg(phenom);
           
           ret.addElement(new Double(d));
           ret.addElement(new Long(now.getTime()));        
           ret.addElement(geom);
        }
        else if (function.equals(MIN))
        {
            double d = min(phenom);
           
           ret.addElement(new Double(d));
           ret.addElement(new Long(now.getTime()));        
           ret.addElement(geom);
        }
        return ret;
        
    }

    public Vector getTemporalAggregation(String function, String phenom) 
    {
        // give the maximum element among the "phenom" values with function x. 
        // at present this is not valuable, how ever, we are still having it for the future functionality.
        
        Vector ret = new Vector();
        Date now = new Date();
        Geometry geom = null; 
       
        if(function.equals(MAX))
        {
           double d = max(phenom);
           
           ret.addElement(new Double(d));
           ret.addElement(new Long(now.getTime()));        
           ret.addElement(geom);
        }
        else if (function.equals(AVG))
        {
            double d = avg(phenom);
           
           ret.addElement(new Double(d));
           ret.addElement(new Long(now.getTime()));        
           ret.addElement(geom);
        }
        else if (function.equals(MIN))
        {
            double d = min(phenom);
           
           ret.addElement(new Double(d));
           ret.addElement(new Long(now.getTime()));        
           ret.addElement(geom);
        }
        return ret;
     
    }
    
    public void registerAggregation(String function, String phenom) {
               
    }

    public void unregisterAggregation(String function, String phenom) {
    }

    private class record { 
      public  String phenom;
      public  double value; 
      public long timestamp;
      //public Geometry geom; 
    };
    
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
             /* for geometry // uncomment when geometry works.  
              x = inputDataStream.readDouble();
              y = inputDataStream.readDouble();
            */
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
    
    private record getLocalRecord(byte[] record)
    {
       record rec = null;
       try
          {   
            String phenom = null;
            long timestamp = 0;
            
            /* for geometry  // uncomment when geometry works.*/
          //  double x, y; 
            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(record);
            DataInputStream inputDataStream = new DataInputStream(inputStream);

             rec.phenom = inputDataStream.readUTF();
             rec.timestamp = inputDataStream.readLong();
             /* for geometry // uncomment when geometry is integrated by customization. 
             x = inputDataStream.readDouble();
             y = inputDataStream.readDouble();
            */
             rec.value = inputDataStream.readDouble();
             inputStream.reset();
             inputStream.close();
             inputDataStream.close();
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
     record rec;
      try 
      {
            byte byteRecord[];
            for(int i = 0; i < store.getNumRecords(); i++)
            {
                byteRecord = store.getRecord(i);
                rec = getLocalRecord(byteRecord);
                if(phenom.equals(rec.phenom))
                {
                    if(maxValue < rec.value)
                        maxValue = rec.value;
                }
           }
      }
      catch( Exception e)
      {
          System.err.println("Error while getting maximum record");
          e.printStackTrace();
      }
      return maxValue;
    }
    
    private double min(String phenom)
    {
     double minValue = 999999;
     record rec;
      try 
      {
            byte byteRecord[];
            for(int i = 0; i < store.getNumRecords(); i++)
            {
                byteRecord = store.getRecord(i);
                rec = getLocalRecord(byteRecord);
                if(phenom.equals(rec.phenom))
                {
                    if(minValue > rec.value)
                        minValue = rec.value;
                }
           }
      }
      catch( Exception e)
      {
          System.err.println("Error while getting maximum record");
          e.printStackTrace();
      }
      return minValue;
        
    }
   
    private double avg(String phenom)
    {
      double avgValue = -1,sum = 0;
      int count = 0;
      record rec;
      try 
      {
            byte byteRecord[];
            count =  store.getNumRecords();
            for(int i = 0; i < count; i++)
            {
                byteRecord = store.getRecord(i);
                rec = getLocalRecord(byteRecord);
                if(phenom.equals(rec.phenom))
                {
                    sum += rec.value;
                }
           }
            
           avgValue = sum/count;
      }
      catch( Exception e)
      {
          System.err.println("Error while getting maximum record");
          e.printStackTrace();
      }
      return avgValue;
    }
}
