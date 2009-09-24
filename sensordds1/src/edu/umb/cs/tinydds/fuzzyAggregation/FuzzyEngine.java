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

public class FuzzyEngine
{
private Logger log = new Logger();
private FuzzyEngine instance;

private FuzzyEngine()
{
    if(instance == null)
        instance = new FuzzyEngine();
}

public FuzzyEngine getInstance()
{
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

}
