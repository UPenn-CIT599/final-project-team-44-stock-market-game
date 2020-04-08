import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PortfolioTest {
	
	PositionFileIO posFile = new PositionFileIO();
	//ArrayList<Position> portfolio = new ArrayList<Position>(); 
	Portfolio port = new Portfolio(posFile.readpositionCSV("DummyStockPortfolio.csv"));
	
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
		port.liquidateStock("GE");
		assertEquals(false, port.portfolio.containsKey("GE"));
	}
	
	@Test
	void getPositionsTest() {
		assertEquals(4, port.portfolio.size());
	}
	
}
