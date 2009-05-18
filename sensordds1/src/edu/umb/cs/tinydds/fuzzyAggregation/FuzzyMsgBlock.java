/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;

/**
 *
 * @author tjones
 * section of fuzzy data to be put into  a message
 * | Type | F | Mu |
 */
public class FuzzyMsgBlock {
    private byte type;
    private byte fuzzySet;
    private MiniFloat memGrade;

    public FuzzyMsgBlock(byte type, byte fuzzySet, MiniFloat memGrade) {
        this.type = type;
        this.fuzzySet = fuzzySet;
        this.memGrade = memGrade;
    }

    public FuzzyMsgBlock(byte type, byte fuzzySet, float memGrade) {
        this.type = type;
        this.fuzzySet = fuzzySet;
        this.memGrade = new MiniFloat(memGrade);
    }

    public byte getFuzzySet() {
        return fuzzySet;
    }

    public void setFuzzySet(byte fuzzySet) {
        this.fuzzySet = fuzzySet;
    }

    public MiniFloat getMemGrade() {
        return memGrade;
    }

    public void setMemGrade(MiniFloat memGrade) {
        this.memGrade = memGrade;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

}
