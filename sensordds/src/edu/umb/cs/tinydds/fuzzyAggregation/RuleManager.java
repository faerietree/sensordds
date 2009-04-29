/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 */
public interface RuleManager {
    public short getOperation(boolean neighbors, byte node1_val, byte node2_val, byte scenario);
    
    /*uses default scenario*/
    public short getOperation(boolean neighbors, byte node1_val, byte node2_val);
}
