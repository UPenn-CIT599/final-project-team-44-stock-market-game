import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

class PositionTest {

	Position position;
	Position cash;
	
	public PositionTest() {
		position = new Position("IBM", 100, 10.73);
		cash = new Position("USDCASH", 10000, 1.0);
	}
	
	@Test
	public void testGetPositionCostBasis() {
		assertEquals(1073.0, position.getCostBasis());
		assertEquals(10000, cash.getCostBasis());
	}
	
	
	@Test
	public void testPositionMarketValue() {
		assertEquals(100 * position.getLastPrice(), position.getCurrentValue());
		assertEquals(10000, cash.getCurrentValue());
	}
	
	@Test
	public void testGetSymbol() {
		assertEquals("IBM", position.getSymbol());
		assertEquals("USDCASH", cash.getSymbol());
	}
	
	@Test
	public void testGetShares() {
		assertEquals(100, position.getShares());
		assertEquals(10000, cash.getShares());
	}

	@Test
	public void testGetAverageCost() {
		assertEquals(10.73, position.getAverageCost());
		assertEquals(1, cash.getAverageCost());
	}
	
	@Test
	public void testGetPositionReturn() {
		assertEquals(Math.round((position.getCurrentValue() / position.getCostBasis() - 1) * 10000) / 100.0, Math.round(position.getPositionReturn() * 100) / 100.0);
		assertEquals(0, cash.getPositionReturn());
	}
	
	@Test
	public void testCashLastPrice() {
		assertEquals(1, cash.getLastPrice());
	}

	@Test
	public void testToString() {
		String objectString = position.toString();
		String[] stringArray = objectString.split("\t");
		for (int i = 0; i < stringArray.length; i++) {
			String val = stringArray[i].trim();
			stringArray[i] = val;
		}

		assertEquals("IBM", stringArray[0]); //symbol
		assertEquals("100", stringArray[1]); //shares
		assertEquals("$10.73", stringArray[2]); //formatted average cost
		assertEquals("$" + String.format("%.2f", position.getLastPrice()), stringArray[3]); //formatted last price
		assertEquals("$1,073.00", stringArray[4]); //formatted costBasis
		assertEquals("$" + String.format("%,.2f", position.getShares() * position.getLastPrice()), stringArray[5]); //formatted current value
		assertEquals(String.format("%,.2f", position.getPositionReturn()), stringArray[6]); //formatted return
	}
	
	
	//Tests the exception thrown in Position.class line 46
	public final ExpectedException exception = ExpectedException.none();
	Position fakeTicker;
	@Test
	public void exceptionTest() {
		exception.expect(IOException.class);
		fakeTicker = new Position("fakeTicker", 1, 1);
	}
	
}
