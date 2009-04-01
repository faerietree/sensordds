/**
 * 
 * JUnit test for jFuzzyLogic
 * 
 * 
 * @author pcingola@users.sourceforge.net
 */
package net.sourceforge.jFuzzyLogic;

import static org.junit.Assert.fail;

import org.antlr.runtime.RecognitionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Main testing class 
 * 
 * @author pcingola@users.sourceforge.net
 *
 */
public class JFuzzyTestCase {

	// A small number
	static double EPSILON = 0.000001;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	/**
	 * Round a double to an integer (time 100)
	 * @param d
	 * @return
	 */
	int doubleToInt100(double d) {
		return ((int) Math.round(d * 100));
	}

	double int100ToDOuble(int i) {
		return (i) / 100.0;
	}

	/**
	 * Read a table of numbers as an array of integers
	 * @param fileName
	 * @return
	 */
	int[][] loadMembershipFile(String fileName) {
		String file = Gpr.readFile(fileName);
		String lines[] = file.split("\n");
		int numCols = lines[0].split("\t").length;
		int vals[][] = new int[lines.length - 1][numCols];

		// Parse all lines
		// Skip first line (title)
		for( int lineNum = 1; lineNum < lines.length; lineNum++ ) {
			String valStr[] = lines[lineNum].split("\t");
			// Parse each line
			for( int col = 0; col < numCols; col++ )
				vals[lineNum - 1][col] = Integer.parseInt(valStr[col]);
		}

		return vals;
	}

	/**
	 * Show a 'separator' line
	 */
	public void separator() {
		System.out.println("-------------------------------------------------------------------------------");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {}

	/**
	 * Test method for {@link net.sourceforge.jFuzzyLogic.FIS#load(java.lang.String)}.
	 */
	@Test
	public void testFileParsing1() {
		FIS fis = FIS.load("./junit/junit1.fcl", true);
		System.out.println(fis);
		separator();
	}

	/**
	 * Test method for {@link net.sourceforge.jFuzzyLogic.FIS#load(java.lang.String)}.
	 */
	@Test
	public void testFileParsing2() {
		FIS fis = FIS.load("./junit/junit2.fcl", true);
		System.out.println(fis);
		separator();
	}

	/**
	 * Test method for {@link net.sourceforge.jFuzzyLogic.FIS#load(java.lang.String)}.
	 */
	@Test
	public void testFileParsing3() {
		FIS fis = FIS.load("./junit/junit3.fcl", true);
		System.out.println(fis);
		separator();
	}

	/**
	 * Test method for {@link net.sourceforge.jFuzzyLogic.FIS#load(java.lang.String)}.
	 */
	@Test
	public void testFileParsing4() {
		FIS fis = FIS.load("./junit/junit4.fcl", true);
		System.out.println(fis);
		separator();
	}

	/**
	 * Test method a fuzzy system that uses functions
	 */
	@Test
	public void testFunctions() {
		// Load tipper fuzzy system
		FIS fis = FIS.load("./junit/junit_functions.fcl", true);
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Load stored results
		int mem[][] = loadMembershipFile("./junit/junit_functions.txt");

		// Compare running the system vs. stored results 
		for( int ind = 0; ind < mem.length; ind++ ) {
			// Get input variables from stores results
			double inVar = int100ToDOuble(mem[ind][0]);

			// Set variables and run the system
			fb.setVariable("inVar", inVar);
			fb.evaluate();

			// Get output variable
			double outVar = fb.getVariable("outVar").getLatestDefuzzifiedValue();

			// Compare output variable to stored result
			if( doubleToInt100(outVar) != mem[ind][4] ) fail("Tipper output outVar(inVar=" + inVar + ") should be " + int100ToDOuble(mem[ind][4]) + ", but it is " + outVar);
		}
	}

	/**
	 * Test method for membership function
	 */
	public void testMembershipFunction(String title, String fclFile, String membershipFunctionFile) {
		int mem[][] = loadMembershipFile(membershipFunctionFile);

		FIS fis = FIS.load(fclFile);
		System.out.println(fis);
		FunctionBlock fb = fis.getFunctionBlock(null);

		for( int ind = 1; ind < mem.length; ind++ ) {
			double value = int100ToDOuble(mem[ind][0]);

			fb.setVariable("inVar", value);

			int poor = doubleToInt100(fb.getVariable("inVar").getMembership("poor"));
			int good = doubleToInt100(fb.getVariable("inVar").getMembership("good"));
			int excellent = doubleToInt100(fb.getVariable("inVar").getMembership("excellent"));

			if( poor != mem[ind][1] ) fail("Membership function " + title + ", poor(" + value + ") should be " + int100ToDOuble(mem[ind][1]) + ", but it is " + int100ToDOuble(poor));
			if( good != mem[ind][2] ) fail("Membership function " + title + ", good(" + value + ") should be " + int100ToDOuble(mem[ind][2]) + ", but it is " + int100ToDOuble(good));
			if( excellent != mem[ind][3] ) fail("Membership function " + title + ", excellent(" + value + ") should be " + int100ToDOuble(mem[ind][3]) + ", but it is " + int100ToDOuble(excellent));
		}
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionCosine() {
		testMembershipFunction("Cosine", "./junit/junit_cosine.fcl", "./junit/junit_cosine.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionDsigm() {
		testMembershipFunction("Dsigm", "./junit/junit_dsigm.fcl", "./junit/junit_dsigm.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionGauss() {
		testMembershipFunction("Gauss", "./junit/junit_gauss.fcl", "./junit/junit_gauss.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionGbell() {
		testMembershipFunction("Gbell", "./junit/junit_gbell.fcl", "./junit/junit_gbell.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionPiecewiseLinear() {
		testMembershipFunction("Piecewise_linear", "./junit/junit_piecewise_linear.fcl", "./junit/junit_piecewise_linear.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionSigmoid() {
		testMembershipFunction("Sigmoid", "./junit/junit_sigmoid.fcl", "./junit/junit_sigmoid.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionSingletons() {
		testMembershipFunction("Singleton", "./junit/junit_singletons.fcl", "./junit/junit_singletons.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionTrapezoid() {
		testMembershipFunction("Trapezoid", "./junit/junit_trape.fcl", "./junit/junit_trape.txt");
	}

	/**
	 * Test method for membership function
	 */
	@Test
	public void testMembershipFunctionTriangular() {
		testMembershipFunction("Triangular", "./junit/junit_triang.fcl", "./junit/junit_triang.txt");
	}

	/**
	 * Test method a fuzzy system that showed NA values due to 'Triangle' membership function bug
	 * Bug report and FCL code by Shashankrao Wankhede
	 */
	@Test
	public void testNAmembership() {
		// FCL.debug = true;
		FIS fis = FIS.load("./junit/junit_shashankrao.fcl", true);
		System.out.println(fis);

		// This set of values used to produce a 'NaN' output
		double ra = 0.5;
		double ad = 0.0;
		fis.setVariable("ra", ra);
		fis.setVariable("ad", ad);
		fis.evaluate();

		// Right output should be 0.5
		double ta = fis.getVariable("ta").getValue();
		if( Double.isNaN(ta) || Double.isInfinite(ta) || (Math.abs(ta - 0.5) > EPSILON) ) fail("System's output should be 0.5, but it's " + ta + "\n" + fis.getVariable("ta"));
	}

	/**
	 * Test method for {@link net.sourceforge.jFuzzyLogic.FIS#createFromString(java.lang.String, boolean)}.
	 */
	@Test
	public void testStringParsing() {
		String fcl = "FUNCTION_BLOCK tipper_parsed_from_string  // Block definition (there may be more than one block per file)\n" + //
		"\n" + //
		"VAR_INPUT              // Define input variables\n" + //
		"   service : REAL;\n" + //
		"   food : REAL;\n" + //
		"END_VAR\n" + //
		"\n" + //
		"VAR_OUTPUT             // Define output variable\n" + //
		"   tip : REAL;\n" + //
		"END_VAR\n" + //
		"\n" + //
		"FUZZIFY service            // Fuzzify input variable 'service': {'poor', 'good' , 'excellent'}\n" + //
		"   TERM poor := (0, 1) (4, 0) ; \n" + //
		"   TERM good := (1, 0) (4,1) (6,1) (9,0);\n" + //
		"   TERM excellent := (6, 0) (9, 1);\n" + //
		"END_FUZZIFY\n" + //
		"\n" + //
		"FUZZIFY food           // Fuzzify input variable 'food': { 'rancid', 'delicious' }\n" + //
		"   TERM rancid := (0, 1) (1, 1) (3,0) ;\n" + //
		"   TERM delicious := (7,0) (9,1);\n" + //
		"END_FUZZIFY\n" + //
		"\n" + //
		"DEFUZZIFY tip          // Defzzzify output variable 'tip' : {'cheap', 'average', 'generous' }\n" + //
		"   TERM cheap := (0,0) (5,1) (10,0);\n" + //
		"   TERM average := (10,0) (15,1) (20,0);\n" + //
		"   TERM generous := (20,0) (25,1) (30,0);\n" + //
		"   METHOD : COG;       // Use 'Center Of Gravity' defuzzification method\n" + //
		"   DEFAULT := 0;       // Default value is 0 (if no rule activates defuzzifier)\n" + //
		"END_DEFUZZIFY\n" + //
		"\n" + //
		"RULEBLOCK No1\n" + //
		"   AND : MIN;          // Use 'min' for 'and' (also implicit use 'max' for 'or' to fulfill DeMorgan's Law)\n" + //
		"   ACT : MIN;          // Use 'min' activation method\n" + //
		"   ACCU : MAX;         // Use 'max' accumulation method\n" + //
		"\n" + //
		"   RULE 1 : IF service IS poor OR food is rancid THEN tip IS cheap;\n" + //
		"   RULE 2 : IF service IS good THEN tip IS average; \n" + //
		"   RULE 3 : IF service IS excellent AND food IS delicious THEN tip is generous;\n" + //
		"END_RULEBLOCK\n" + //
		"\n" + //
		"END_FUNCTION_BLOCK\n";

		try {
			FIS fis = FIS.createFromString(fcl, true);
			System.out.println(fis);
		} catch(RecognitionException e) {
			fail("RecognitionException:" + e);
		}
	}

	/**
	 * Test method 'tipper' fuzzy system
	 */
	@Test
	public void testTipper() {
		// Load tipper fuzzy system
		FIS fis = FIS.load("./junit/junit_tipper.fcl", true);
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Load stored results
		int mem[][] = loadMembershipFile("./junit/junit_tipper.txt");

		// Compare running the system vs. stored results 
		for( int ind = 1; ind < mem.length; ind++ ) {
			// Get input variables from stores results
			double service = int100ToDOuble(mem[ind][0]);
			double food = int100ToDOuble(mem[ind][1]);

			// Set variables and run the system
			fb.setVariable("service", service);
			fb.setVariable("food", food);
			fb.evaluate();

			// Get output variable
			double tip = fb.getVariable("tip").getLatestDefuzzifiedValue();

			// Compare output variable to stored result
			if( doubleToInt100(tip) != mem[ind][2] ) fail("Tipper output tip(service=" + service + ", food=" + food + ") should be " + int100ToDOuble(mem[ind][2]) + ", but it is " + tip);
		}
	}

	/**
	 * Test method generating a string and parsing it
	 */
	@Test
	public void testToString() {
		// Load system
		String fileName = "junit/tipper.fcl";
		FIS fis = FIS.load(fileName, true);

		// Parse FCL code generated by fis.toString()
		FIS fis2;
		try {
			fis2 = FIS.createFromString(fis.toString(), false);
		} catch(RecognitionException e) {
			throw new RuntimeException("Could not parse FCL code generated by fis.toString(). This should never happen!!!");
		}

		// Compare both fis (should be identical)
		boolean ok = fis.toString().equals(fis2.toString());
		System.out.println("Are both fis equal?: " + ok);
		if( !ok ) throw new RuntimeException("FCL code for both fis is not the same.");

	}
}
