/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 */
public class FuzzyTypeException extends Exception {

    /**
     * Creates a new instance of <code>FuzzyTypeException</code> without detail message.
     */
    public FuzzyTypeException() {
    }


    /**
     * Constructs an instance of <code>FuzzyTypeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FuzzyTypeException(String msg) {
        super(msg);
    }
}
