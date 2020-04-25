import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

class PortfolioTest {

	HashMap<String, Position> testPrt;
	Portfolio prt;
	YahooQuote quote = new YahooQuote();
	
	public PortfolioTest() {
		testPrt = new HashMap<String, Position>();
		Position cash = new Position("USDCASH", 250000, 1);
		Position aapl = new Position("AAPL", 1000, 220.73);
		Position ibm = new Position("IBM", 1000, 117.52);
		Position ge = new Position("GE", 1000, 17.28);
		
		testPrt.put("USDCASH", cash);
		testPrt.put("AAPL", aapl);
		testPrt.put("IBM", ibm);
		testPrt.put("GE", ge);
		
		prt = new Portfolio(testPrt);
	}

	
	//Tests hasSufficientShares for the following cases:
	//you have sufficient shares: assertEquals true
	//you don't have sufficient shares of a position you own: assertEquals false
	//you check sufficient shares for a position you do not own: assertEquals false
	@Test
	void testSufficientShares() {
		assertTrue(prt.hasSufficientShares("GE", 1000));
		assertFalse(prt.hasSufficientShares("AAPL", 1001));
		assertFalse(prt.hasSufficientShares("INTC", 1));
	}
	
	//testing buying a new position and confirming the position is now in the portfolio.
	@Test
	void buyStock() {
		prt.buySell("ZVZZT", 10.0);
		assertTrue(prt.portfolio.containsKey("ZVZZT"));
	}
	
	//testing liquidating a position and confirming the postiion is no longer in the portfolio.
	@Test
	void liquidateStock() {
		prt.buySell("GE", -1000);
		assertFalse(prt.portfolio.containsKey("GE"));
	}
	
	//testing adding shares to an existing position and confirming the new share amount for the position.
	@Test void addStock() {
		prt.buySell("AAPL", 1);
		assertEquals(1001, prt.portfolio.get("AAPL").getShares());
	}
	
	//testing trimming shares from an existing position and confirming the new share amount for the position.
	@Test void trimStock() {
		prt.buySell("IBM", -1);
		assertEquals(999, prt.portfolio.get("IBM").getShares());
	}
	
	//Testing getting positions
	@Test
	void getPositionsTest() {
		HashMap<String, Position> tst = prt.getPositions();
		assertEquals(4, tst.size());
	}
	
	//Testing cash Deposit
	@Test
	void cashDepositTest() {
		prt.updateCash(1);
		assertEquals(250001, prt.portfolio.get("USDCASH").getShares());
	}
	
	//Testing cash withdrawal
	@Test
	void cashWithdrawalTest() {
		prt.updateCash(-1);
		assertEquals(249999, prt.portfolio.get("USDCASH").getShares());
	}
	

	//Testing updatePort / printPort. Will test that the stock's last price
	//matches what is printed by the port in updatePortfolio.
	@Test
	void updatePortTest() {
		prt.updatePortfolio();
		try {
			assertEquals(Double.parseDouble(quote.getField("AAPL", "regularMarketPrice\":(.+?),", "chart")), prt.portfolio.get("AAPL").getLastPrice());
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Tests the exception thrown in Position.class line 46
	public final ExpectedException exception = ExpectedException.none();
	@Test
	public void exceptionTest() {
		exception.expect(IOException.class);
		prt.buySell("fake ticker", 1);
	}
	
}
