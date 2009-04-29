/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.fuzzyAggregation;
import edu.umb.cs.tinydds.fuzzyAggregation.PhenomType.*;

/**
 *
 * @author tjones
 */
public class TempRules implements RuleManager{
    
    byte[][] states =    // table for rule decisions. Alternate for chain of IFs
                    //IGNORE for now
	{
            //              N1     N2      OP
                {           0,     0,      0},
	/* Rule 1*/	{PhenomType.COLD,	2,	3},
	/* Rule 2*/	{1,	2,	6},
	/* 3*/	{3,	4,	3},
	/* 4*/	{3,	4,	8},
	/* 5*/	{5,	6,	7},
	/* 6*/	{6,	5,	6},
	/* 7*/	{7,	8,	7},
	/* 8*/	{7,	8,	8},

	};

    public short getOperation(boolean neighbors, byte node1_val, byte node2_val, byte scenario) {

        if ((node1_val !=PhenomType.COLD) || (node2_val!=PhenomType.COLD) && (neighbors))
            return FuzzyConstants.AGGREGATE;
        else
            if ((node1_val !=PhenomType.COLD) || (node2_val!=PhenomType.COLD) && (!neighbors))
               return FuzzyConstants.RELAY;
        switch (scenario){
            case PhenomType.DATA_COLLECTION:
                  if ((node1_val ==PhenomType.COLD) && (node2_val==PhenomType.COLD) && (neighbors))
                    return FuzzyConstants.AGGREGATE;
            case PhenomType.EVENT_COLLECTION:
                  if ((node1_val ==PhenomType.COLD) && (node2_val==PhenomType.COLD) && (!neighbors))
                    return FuzzyConstants.AGGREGATE;
        }
         //Should not get here:
         return FuzzyConstants.UNKNOWN;

        }



    public short getOperation(boolean neighbors, byte node1_val, byte node2_val) {
        //Assume PhenomType.DATA_COLLECTION
        return getOperation(neighbors, node1_val, node2_val, PhenomType.DATA_COLLECTION);
    }




}
