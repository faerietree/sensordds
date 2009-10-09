/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author samathareddy
 */

import com.sun.spot.peripheral.Spot;
import edu.umb.cs.tinydds.utils.GlobalConfiguration;
import edu.umb.cs.tinydds.utils.Logger;
import edu.umb.cs.tinydds.MessagePayloadFuzzy;
import edu.umb.cs.tinydds.MessagePayload;
import edu.umb.cs.tinydds.cluster.ClusterMessage;
import edu.umb.cs.tinydds.cluster.ClusterManager;
import edu.umb.cs.tinydds.L3.L3;
import edu.umb.cs.tinydds.L3.OneHop;
import java.util.Timer;
import java.util.TimerTask;
import edu.umb.cs.tinydds.io.TempSensor;

import edu.umb.cs.tinydds.MessageFactory;

public class FuzzyEngine implements GlobalConfiguration
{
private static Logger log;
private static FuzzyEngine instance;
private static MessagePayloadFuzzy FuzzyPayload;
private TimerTask task;
private Timer ReaderInterval;
private TempSensor temp;

private FuzzyEngine()
{
  if(FuzzyPayload == null)
        FuzzyPayload = new MessagePayloadFuzzy();
  if(log == null)
      log  = new Logger("Fuzzy Engine");
  if(temp == null)
      temp = new TempSensor();
  task = new TimerTask() {
                  public void run()
                  {
                     try {  processTemp(temp.getValue()); }
                     catch(Exception ie){ log.logError("Error while reading temperature sensor");}
                  }
                };

     if(ReaderInterval != null)
            {
            ReaderInterval = new Timer();
            ReaderInterval.scheduleAtFixedRate(task, PING_DELAY, PING_INTERVAL * ONE_SECOND);
            }
  }
/** Returns an instance of FuzzyEngine. As you can see, FuzzyEngine is a singleton. */

public static FuzzyEngine getInstance()
{
    if(instance == null)
        instance = new FuzzyEngine();
    return instance;
}

/** Returns a double value denoting how much the temprature belongs to freezing.
 *
 * @param x - Temperature reading by TempSensor
 * @return A value accourding to the formulaw in FuzzysetEquations.
 */

private float Freezing(float x)
{
    if(x <= -2)
        return 1;
    if(x <= -1)
        return  (x*x + 4*x +3)*(-1);
    else
        return 0;
}

/** Returns a value denoting how much temperature belongs to hot.
 *
 * @param x - Temperature reading by TempSensor
 * @return A value accourding to the formulaw in FuzzysetEquations.
 */

private float Hot(float x)
{
    if(x >= 4 &&  x <= 7)
        return (-1*(x*x + 40)+14*x)/9;
    else
        if(x > 7)
            return 1;
        else
            return 0;
}

private float Cold(float x)
{
    if(x >= -2 && x <= 0)
        return (-1*(x*x/4) + 1);
    else
        if(x > 0 && x <= 3 )
            return 1;
        else
           if(x >= 3 && x <= 7)
                return (-1*(x*x) + 6*x + 7)/16;
           else
               return 0;
    
}

private float Unsafe(float x)
{
    if(x >= 2 && x <= 4)
            return (-1*(x*x/4+ 2*x-3));
    else
        if ( x >= 4  && x <=5)
                 return 1;
        else
            if(x < 2)
                return 0;
            else
                return 1;
}

private float Safe(float x)
{
    if(x <= 2)
        return 1;
    else
        if(x > 2 && x<=4)
            return (-1*((x*x)/4)+x);
        else
            return 0;
}

private float Low(float x)
{
    if(x<=0 && x<=1)
        return (-1*(x*x) + 1);
    else
        return 0;
}

private float Ideal(float x)
{
    if(x>= 0.25 && x <=1)
        return (-16*x + 32*x - 7)/9;
    else
        if(x<=1 && x<=3)
            return 1;
        else
            if(x >=3 && x <=5)
                return (-1*(x*x)+6*x -5)/4;
            else
                return 0;
}

private float High(float x)
{
    if(x>=3 && x<=5)
        return (-1*(x*x)+ 10*x -21)/4;
    else
        if(x<=5 && x<=8)
            return 1;
        else
            return 0;
}

private float Decrease(float x)
{
    if(x>=-4 && x<=0 )
        return (-1*(x*x/4)-x);
    else
        return 0;
}

private float Increase(float x)
{
    if(x>=-2 && x < 2)
        return (-1*(x*x/4)+1);
    else
        return 0;
}

private float NoChange(float x)
{
    if(x>0 && x<=4)
        return (-1*(x*x/4)+x);
    else
        return 0;
}

/**     
 * Following method sets the fields of Fuzzymessage pay load accourding to following offsets. 
 * these are Mu values.
 *      OFFSET_FREEZING = 0;
	OFFSET_COLD = 1;
	OFFSET_WARM = 2;
	OFFSET_LOW = 3;
	OFFSET_IDEAL = 4;
	OFFSET_HIGH = 5;
	OFFSET_SAFE = 6;
	OFFSET_UNSAFE = 7;
	OFFSET_CRITICAL = 8;
	OFFSET_NONCRITICAL = 9;
**/

public void processTemp(float x)
{
  log.logInfo("FUZZY: Processing Temperature "+ x);

   FuzzyPayload.setValue(0, Freezing(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Freezing "+ FuzzyPayload.getValue(0));
   FuzzyPayload.setValue(1, Cold(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Cold "+ FuzzyPayload.getValue(1));
   FuzzyPayload.setValue(2, Hot(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Warm "+ FuzzyPayload.getValue(2));
   FuzzyPayload.setValue(3, Low(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Airflow Low "+ FuzzyPayload.getValue(3));
   FuzzyPayload.setValue(4, Ideal(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Airflow Ideal"+ FuzzyPayload.getValue(4));
   FuzzyPayload.setValue(5, High(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing High"+ FuzzyPayload.getValue(5));
   FuzzyPayload.setValue(6, Safe(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Safety from bacteria "+ FuzzyPayload.getValue(6));
   FuzzyPayload.setValue(7, Unsafe(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Unsafe "+ FuzzyPayload.getValue(7));
   FuzzyPayload.setValue(8, Decrease(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Action to take Decrease"+ FuzzyPayload.getValue(8));
   FuzzyPayload.setValue(9, Increase(x));
   if(DEBUG & DBUG_LVL >= LIGHT)
        log.logInfo("FUZZY: Processing Increase"+ FuzzyPayload.getValue(9));
   ClusterMessage msg = new ClusterMessage();
   msg.setMsgCode(ClusterMessage.FUZZY);
   msg.setOriginator(L3.getAddress());
   msg.setReceiver(ClusterManager.getNextMember().longValue());
   ClusterManager.getInstance().loadMessage(msg,new OneHop());
 processCritical();
}

public void processPayload(MessagePayload payload)
{
    if(DEBUG && DBUG_LVL >= MEDIUM)
        log.logInfo("Received Fuzzy payload.Trying to process.");

}

public void processCritical()
{
    // not being used at present as we have decided to process actions rather than Critical and non-critical states.
    // to be used when we decide where to send control messages.
}

}
