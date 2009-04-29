/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 * Static class to hold constants
 * //changed fromm int to byte for space storing elswhere
 */
public class PhenomType {

    /* Phenomena codes */
    public static byte TEMP = 0x00;
    static byte AIRFLOW = 0x01;
    static byte BACTERIA = 0x02;

    /*Fuzzy set Codes*/
    public static byte COLD = 0x10;
    static byte WARM = 0x11;
    static byte HOT = 0x12;
    static byte LOW = 0x13;
    static byte IDEAL = 0x14;
    static byte HIGH = 0x15;
    static byte SAFE = 0x16;
    static byte UNSAFE = 0x17;

    /* Scenario Types*/
    static final byte DATA_COLLECTION = 0x7F;
    static final byte EVENT_COLLECTION = 0x70;



}
