package edu.umb.cs.tinydds.fuzzyAggregation;

import edu.umb.cs.tinydds.utils.Geometry;
import java.util.Vector;
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
        
    }

    public Vector getPhenomAggregation(String function, String phenom) {
        return null;
    }

    public Vector getTemporalAggregation(String function, String phenom) {
        return null;
    }
    
    public void registerAggregation(String function, String phenom) {
    }

    public void unregisterAggregation(String function, String phenom) {
    }

    
}
