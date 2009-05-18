/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umb.cs.cluster;

/**
 *
 * @author francesco
 */
class NotClusterHeadException extends Exception {

    /**
     * Creates a new instance of <code>notClusterHeadException</code>
     * without detail message.
     */
    public NotClusterHeadException() {
        super( );
    }

    /**
     * Constructs an instance of <code>notClusterHeadException</code> with
     * the specified detailed message.
     *
     * @param msg a detailed message.
     */
    public NotClusterHeadException(String msg) {
        super(msg);
    }

}
