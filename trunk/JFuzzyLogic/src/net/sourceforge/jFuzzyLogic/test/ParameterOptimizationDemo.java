package net.sourceforge.jFuzzyLogic.test;

import java.util.ArrayList;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.optimization.OptimizationDeltaJump;
import net.sourceforge.jFuzzyLogic.optimization.Parameter;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;

/**
 * Fuzzy rule set parameter optimization example
 * 
 * @author pcingola@users.sourceforge.net
 */
public class ParameterOptimizationDemo {

	//-------------------------------------------------------------------------
	// Main
	//-------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		System.out.println("ParameterOptimizationDemo: Begin");

		//---
		// Load FIS (Fuzzy Inference System)
		//---
		FIS fis = FIS.load("fcl/qualify.fcl");
		RuleBlock ruleBlock = fis.getFunctionBlock(null).getFuzzyRuleBlock(null);

		//---
		// Create a list of parameter to optimize
		//---
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		// Add variables. 
		// Note: Fuzzy sets' parameters for these (scoring and credLimMul) variables will be optimized
		Parameter.parameterListAddVariable(parameterList, ruleBlock.getVariable("scoring"));
		Parameter.parameterListAddVariable(parameterList, ruleBlock.getVariable("credLimMul"));

		// Add every rule's weight
		for( Rule rule : ruleBlock.getRules() ) {
			Parameter.parameterListAddRule(parameterList, rule);
		}

		//---
		// Create an error function to be optimized (i.e. minimized)
		//---
		ErrorFunctionQualify errorFunction = new ErrorFunctionQualify();

		//---
		// Optimize (using 'Delta jump optimization')
		//---
		OptimizationDeltaJump optimizationDeltaJump = new OptimizationDeltaJump(ruleBlock, errorFunction, parameterList);
		optimizationDeltaJump.setMaxIterations(20); // Number optimization of iterations
		optimizationDeltaJump.optimize(true);

		//---
		// Save optimized fuzzyRuleSet to file
		//---
		System.out.println(ruleBlock.toString());
		Gpr.toFile("fcl/qualify_optimized.fcl", ruleBlock.toString());

		System.out.println("ParameterOptimizationDemo: End");
	}
}
