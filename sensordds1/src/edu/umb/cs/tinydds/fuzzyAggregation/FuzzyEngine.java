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
import edu.umb.cs.tinydds.MessageFactory;

public class FuzzyEngine implements GlobalConfiguration
{
private static Logger log;
private static FuzzyEngine instance;
private static MessagePayloadFuzzy FuzzyPayload;

private FuzzyEngine()
{
  if(FuzzyPayload == null)
        FuzzyPayload = new MessagePayloadFuzzy();
  if(log == null)
      log  = new Logger("Fuzzy Engine");
}

public static FuzzyEngine getInstance()
{
    if(instance == null)
        instance = new FuzzyEngine();
    return instance;
}

private float Freezing(float x)
{
    if(x <= -2)
        return 1;
    if(x <= -1)
        return  (x*x + 4*x +3)*(-1);
    else
        return 0;
}

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
   FuzzyPayload.setValue(0, Freezing(x));
   FuzzyPayload.setValue(1, Cold(x));
   FuzzyPayload.setValue(2, Hot(x));
   FuzzyPayload.setValue(3, Low(x));
   FuzzyPayload.setValue(4, Ideal(x));
   FuzzyPayload.setValue(5, High(x));
   FuzzyPayload.setValue(6, Safe(x));
   FuzzyPayload.setValue(7, Unsafe(x));
   FuzzyPayload.setValue(8, Decrease(x));
   FuzzyPayload.setValue(9, NoChange(x));
   ClusterMessage msg = new ClusterMessage();
   msg.setMsgCode(ClusterMessage.FUZZY);
   msg.setOriginator(L3.getAddress());
   msg.setReceiver(ClusterManager.getNextMember().longValue());
   ClusterManager.getInstance().loadMessage(msg, new OneHop());

}

public void processPayload(MessagePayload payload)
{
    if(DEBUG && DBUG_LVL >= MEDIUM)
        log.logInfo("Received Fuzzy payload.Trying to process.");

}

}
