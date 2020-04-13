import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

class PortfolioTest {
	
	Portfolio port;
	
	public PortfolioTest() {
		PositionFileIO posFile = new PositionFileIO();
		//ArrayList<Position> portfolio = new ArrayList<Position>(); 
		try {
			port = new Portfolio(posFile.readpositionCSV("DummyStockPortfolio.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			e.printStackTrace();
		}
	}

	
	@Test
	void testSufficientSharesTrue() {
		assertEquals(true, port.hasSufficientShares("GE", 7));
	}
	
	@Test
	void testSufficientSharesFalse() {
		assertEquals(false, port.hasSufficientShares("GE", 11));
	}
	
	@Test
	void testHasSufficientCashTrue() {
		assertEquals(true, port.hasSufficientCash("ABT", 100));
	}
	
	@Test
	void testHasSufficientCashFalse() {
		assertEquals(false, port.hasSufficientCash("AAPL", 1500));
	}
	
	@Test
	void buyStock() {
		port.buyStock("ZVZZT", 10);
		assertEquals(true, port.portfolio.containsKey("ZVZZT"));
	}
	
	@Test
	void liquidateStock() {
		port.sellStock("GE", 10);
		assertEquals(false, port.portfolio.containsKey("GE"));
	}
	
	@Test void addStock() {
		double currShares = port.portfolio.get("AAPL").getShares();
		port.buyStock("AAPL", 1);
		assertEquals(currShares + 1, port.portfolio.get("AAPL").getShares());
	}
	
	@Test void trimStock() {
		double currShares = port.portfolio.get("AAPL").getShares();
		port.sellStock("AAPL", 1);
		assertEquals(currShares - 1, port.portfolio.get("AAPL").getShares());
	}
	
	@Test
	void getPositionsTest() {
		assertEquals(5, port.portfolio.size());
	}
	
}
