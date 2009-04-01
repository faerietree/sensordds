package net.sourceforge.jFuzzyLogic;

import net.sourceforge.jFuzzyLogic.plot.JDialogFis;

public class Zzz {

	/**
	 * Round a double to an integer (time 100)
	 * @param d
	 * @return
	 */
	static int doubleToInt100(double d) {
		return ((int) Math.round(d * 100));
	}

	static double int100ToDOuble(int i) {
		return (i) / 100.0;
	}

	public static void main(String args[]) throws Exception {
		System.out.println("Begin: Zzz");

		// Load system
		String fileName = "fcl/z.fcl";
		FIS fis = FIS.load(fileName, true);

		System.out.println("\n\n------------------------------------FIS1------------------------------------\n");
		System.out.println(fis);
		FIS fis2 = FIS.createFromString(fis.toString(), false);
		System.out.println("\n\n------------------------------------FIS2------------------------------------\n");
		System.out.println(fis2.toString());
		System.out.println("COMPARE: " + fis.toString().equals(fis2.toString()));

		if( false ) {
			// Create a plot
			JDialogFis jdf = new JDialogFis(fis, 800, 600);

			for( double service = 1, food = 1; service <= 10; service += 0.1 ) {
				food = service;
				// Evaluate system using these parameters
				fis.getVariable("service").setValue(service);
				fis.getVariable("food").setValue(food);
				fis.evaluate();

				// Print result & update plot
				System.out.println(String.format("Service: %2.2f\tfood:%2.2f\t=> tip: %2.2f %%", service, food, fis.getVariable("tip").getValue()));
				jdf.repaint();

				// Small delay
				Thread.sleep(100);
			}
		}

		System.out.println("End: Zzz");
	}

	/**
	 * Show values for 'inVar' (membership for sets 'poor', 'good' and 'excellent')
	 *  
	 * @param fclFile
	 * @param evaluate
	 */
	public static void showMembershipTable(String fclFile, boolean evaluate) {
		FIS fis = FIS.load(fclFile, true);
		System.out.println(fis);

		System.out.println(fis.getVariable("inVar"));
		System.out.println(fis.getVariable("outVar"));

		int MAX = 100;
		// Generate table
		System.out.println("inVar\tpoor\tgood\texcellent");
		double var = 0.1;
		for( int count = 0; count < MAX; count++, var += .1 ) {
			double inVar = int100ToDOuble(doubleToInt100(var));
			fis.setVariable("inVar", inVar);

			int poor = doubleToInt100(fis.getVariable("inVar").getMembership("poor"));
			int good = doubleToInt100(fis.getVariable("inVar").getMembership("good"));
			int excellent = doubleToInt100(fis.getVariable("inVar").getMembership("excellent"));

			if( evaluate ) {
				// Evaluate & get output variable
				fis.evaluate();
				double outVar = fis.getVariable("outVar").getLatestDefuzzifiedValue();
				System.out.println(doubleToInt100(inVar) + "\t" + poor + "\t" + good + "\t" + excellent + "\t" + doubleToInt100(outVar));
			} else {
				System.out.println(doubleToInt100(inVar) + "\t" + poor + "\t" + good + "\t" + excellent);
			}
		}
	}
}
