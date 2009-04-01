package net.sourceforge.jFuzzyLogic.test;

import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.optimization.ErrorFunction;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;

/**
 * Error function for qualify.fcl fuzzy rule set
 *
 * @author pcingola@users.sourceforge.net
 */
public class ErrorFunctionQualify extends ErrorFunction {

	// Desired output for credit limit (see Qualify_demo.xls spreadsheet)
	private static double credLimXL[][] = { { 0, 100, 100, 100, 75, 60 }, { 73, 119, 150, 150, 138, 110 }, { 80, 130, 190, 200, 200, 160 }, { 86, 141, 206, 245, 250, 210 }, { 93, 152, 221, 264, 286, 300 }, { 99, 163, 237, 283, 300, 300 } };
	// Debug mode?
	public static boolean debug = false;
	// net income column (see Qualify_demo.xls spreadsheet)
	private static double incomeXL[] = { 1000, 1300, 2000, 3000, 4000, 5000 };
	// score row (see Qualify_demo.xls spreadsheet)
	private static double scoreXL[] = { 400, 500, 600, 700, 800, 900 };

	@Override
	public double evaluate(RuleBlock fuzzyRuleSet) {
		double error = 0;

		fuzzyRuleSet.setVariable("city", 1); // Cap. Fed.
		// fuzzyRuleSet.setVariable("city", 2000); // Interior
		fuzzyRuleSet.setVariable("occupation_type", 10); // Good ocupation type
		// fuzzyRuleSet.setVariable("occupation_type", 1); // Bad ocupation type
		fuzzyRuleSet.setVariable("scoring_partner", -1); // No partner

		// Caclualte for each table's element (score and income)
		for( int scoreInd = 0; scoreInd < scoreXL.length; scoreInd++ ) {
			double score = scoreXL[scoreInd];
			fuzzyRuleSet.setVariable("scoring", score);
			for( int incomeInd = 0; incomeInd < incomeXL.length; incomeInd++ ) {
				double income = incomeXL[incomeInd];
				fuzzyRuleSet.setVariable("sel", income);

				// Evaluate fuzzy system
				fuzzyRuleSet.evaluate();

				// get output
				double credLimMul = fuzzyRuleSet.getVariable("credLimMul").getLatestDefuzzifiedValue();

				// Calc error (mean square)
				double desiredCredLim = credLimXL[scoreInd][incomeInd] / 100;
				error += (credLimMul - desiredCredLim) * (credLimMul - desiredCredLim);
				if( debug ) Gpr.debug(score + ", " + income + "\t" + credLimMul + "\t" + desiredCredLim + "\t" + Math.sqrt(error));
			}
		}

		return Math.sqrt(error);
	}

}
