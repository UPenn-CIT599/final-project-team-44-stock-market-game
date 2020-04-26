import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
/**
 * 
 * @author Chris
 * Unit tests to test the ability to read in data from a test file in the PositionFileIO class
 * 
 *
 */
class PositionFileIOTest {
	/**
	 * Unit test to validate that data is being read in at all.
	 */
	@Test
	void testConfirmFileLoaded() {
		String fileName = "StockPortfolio.csv"; //note-created a separate test file to not bother the one being used by others
		HashMap <String, Position> testPortfolio = new HashMap <String, Position>();
		PositionFileIO testFileIO = new PositionFileIO();
		try {
			testPortfolio = testFileIO.readpositionCSV(fileName);
		} catch (FileNotFoundException e) {
			System.out.println ("Error reading in test file");
		}
		assertTrue(testPortfolio.size()>0);
	}
	
	/**
	 * Unit test to confirm the data being read in is as anticipated.
	 */
	@Test
	void testReadpositionCSV() {
		String fileName = "StockPortfolioTest.csv"; //note-created a separate test file to not bother the one being used by others
		HashMap <String, Position> testPortfolio = new HashMap <String, Position>();
		PositionFileIO testFileIO = new PositionFileIO();
		try {
		testPortfolio = testFileIO.readpositionCSV(fileName);
		} catch (FileNotFoundException e) {
			System.out.println ("Error reading in test file");
		}
		
		assertEquals(testPortfolio.containsKey("AAPL"), true);
		assertEquals(testPortfolio.get("AAPL").getShares(), 200);
		assertEquals(testPortfolio.get("AAPL").getAverageCost(), 282.97);
		assertEquals(testPortfolio.containsKey("GOOGL"), true);
		assertEquals(testPortfolio.get("GOOGL").getShares(), 100);
		assertEquals(testPortfolio.get("GOOGL").getAverageCost(), 1276.6);
		assertEquals(testPortfolio.containsKey("USDCASH"), true);
		assertEquals(testPortfolio.get("USDCASH").getShares(), 693357);
		assertEquals(testPortfolio.get("USDCASH").getAverageCost(), 1);
		assertEquals(testPortfolio.containsKey("AMZN"), true);
		assertEquals(testPortfolio.get("AMZN").getShares(), 50);
		assertEquals(testPortfolio.get("AMZN").getAverageCost(), 2410.22);
		assertEquals(testPortfolio.containsKey("GE"), true);
		assertEquals(testPortfolio.get("GE").getShares(), 300);
		assertEquals(testPortfolio.get("GE").getAverageCost(), 6.26);
	}
}
	