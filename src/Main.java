import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * acts as a runner class to run a trading session
 * @author jarod
 *
 */
public class Main {
	/**
	 * single main method that runs the entire trading session
	 * @param args
	 */
	public static void main(String[] args) {
		Trade tradingSession = new Trade();
		tradingSession.runTradingSession();
	}
	
	
	//Do we still need the below?  I believe it's the same as the printPort method in the Portfolio class
	/**
	 * Prints out the portfolio in a formatted way
	 * for easy reading by the individual
	 * @param args
	 */
	/**
	public static void main(String[] args) {
		PositionFileIO posFile = new PositionFileIO();
		try {
		Portfolio port = new Portfolio(posFile.readpositionCSV("DummyStockPortfolio.csv"));
		
		System.out.println(String.format("%10s","SYMBOL") 
				+ "\t" + String.format("%10s", "SHARES") 
				+ "\t" + String.format("%10s", "AVG COST") 
				+ "\t" + String.format("%10s", "LAST PRICE") 
				+ "\t" + String.format("%10s","COST BASIS") 
				+ "\t" + String.format("%15s", "CURR VALUE") 
				+ "\t" + String.format("%15s", "RETURN (%)"));
		for (String symbol : port.portfolio.keySet()) {
			System.out.println(port.portfolio.get(symbol));
		}
		
		posFile.writePositionCSV("DummyStockPortfoliov2.csv", port);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException - that is not a valid file name.");
			System.out.println("Please try entering a new name.");
		}
		
		//System.out.println(portfolio.get(0).getSymbol());
		
	}
	*/
}
