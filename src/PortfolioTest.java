import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

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
	void positionExistsTrue() {
		assertEquals(true, port.positionExists("USDCASH"));
	}

	@Test
	void positionExistsFalse() {
		assertEquals(false, port.positionExists("MADEUPSTOCKSYMBOL"));
	}
	
	
	@Test
	void buyStock() {
		port.buyStock("ZVZZT", 10);
		assertEquals(true, port.positionExists("ZVZZT"));
	}
	
	@Test
	void liquidateStock() {
		port.liquidateStock("GE");
		assertEquals(false, port.positionExists("GE"));
	}
	
	@Test
	void getPositionsTest() {
		ArrayList<Position> pTest = port.getPositions();
		assertEquals(4, pTest.size());
	}
	
}
