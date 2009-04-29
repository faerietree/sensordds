/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 */
public class MiniFloat {
    float floatValue;
    byte byteValue;
    //Float num2;

    public MiniFloat(float floatValue) {
        this.floatValue = floatValue;

        //TODO:  Convert to byte
        //This method may be lossy. need to test
        //Fix :  use Bahr method
        this.byteValue = new Float(floatValue).byteValue();
    }

    public MiniFloat(byte byteValue) {
        this.byteValue = byteValue;
        //TODO:  Convert to float
    }

    public byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }


}
