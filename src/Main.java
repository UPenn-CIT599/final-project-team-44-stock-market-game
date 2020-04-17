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

}