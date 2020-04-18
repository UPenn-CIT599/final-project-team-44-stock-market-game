import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class PositionFileIOTest {

	@Test
	void testReadpositionCSV() {
		String fileName = "DummyStockPortfolioTest.csv";
		HashMap <String, Position> testPortfolio = new HashMap <String, Position>();
		PositionFileIO testFileIO = new PositionFileIO();
		try {
		testPortfolio = testFileIO.readpositionCSV(fileName);
		} catch (FileNotFoundException e) {
			System.out.println ("Error reading in test file");
		}
		
		assertEquals(testPortfolio.containsKey("AAPL"), true);
		assertEquals(testPortfolio.get("AAPL").getShares(), 1000);
		assertEquals(testPortfolio.get("AAPL").getAverageCost(), 120.73);
	}

}
