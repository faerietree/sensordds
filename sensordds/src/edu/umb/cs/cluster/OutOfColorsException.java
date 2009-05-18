/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.cluster;

/**
 *
 * @author francesco
 */
class OutOfColorsException extends Exception {

    public OutOfColorsException(){
        super();
    }

    /**
     * Constructs an instance of <code>notClusterHeadException</code> with
     * the specified detailed message.
     *
     * @param msg a detailed message.
     */
    public OutOfColorsException(String msg) {
        super(msg);
    }
}
