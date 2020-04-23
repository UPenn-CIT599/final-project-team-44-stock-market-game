import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * acts as a runner class to run a trading session
 * @author jarod
 *
 */
public class ChadMain2 {
	/**
	 * single main method that runs the entire trading session
	 * @param args
	 */
	public static void main(String[] args) {
		ChadTrade2 tradingSession = new ChadTrade2();
		tradingSession.runTradingSession();
	}

}