import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class PositionTest {

	Position position;
	Position cash;
	
	public PositionTest() {
		position = new Position("IBM", 100, 10.73);
		cash = new Position("USDCASH", 10000, 1.0);
	}
	
	@Test
	public void testPositionCostBasis() {
		assertEquals(1073.0, position.getCostBasis());
	}
	
	
	@Test
	public void testPositionMarketValue() {
		assertEquals(position.getShares() * position.getLastPrice(), position.getCurrentValue());
	}
	
	@Test
	public void testGetSymbol() {
		assertEquals("IBM", position.getSymbol());
	}
	
	@Test
	public void testGetShares() {
		assertEquals(100, position.getShares());
	}

	@Test
	public void testGetAverageCost() {
		assertEquals(10.73, position.getAverageCost());
	}
	
	@Test
	public void testGetPositionReturn() {
		assertEquals(Math.round((position.getCurrentValue() / position.getCostBasis() - 1) * 10000) / 100.0, position.getPositionReturn());
	}
	
	
	@Test
	public void testCashAverageCost() {
		assertEquals(1, cash.getAverageCost());
	}
	
	@Test
	public void testCashLastPrice() {
		assertEquals(1, cash.getLastPrice());
	}
	
	@Test
	public void testSetShares() {
		position.setShares(10);
		assertEquals(10, position.getShares());
	}
	
	@Test
	public void testSetAverageCost() {
		position.setAverageCost(7.0);
		assertEquals(7, position.getAverageCost());
	}
	
	@Test
	public void testToString() {
		assertEquals(String.format("%-10s",position.getSymbol()) 
				+ "\t"  + String.format("%-10s",position.getShares()) 
				+ "\t" + String.format("%-10s",position.getAverageCost()) 
				+ "\t"+ String.format("%-10s",position.getLastPrice()) 
				+ "\t" + String.format("%-15s", position.getCostBasis()) 
				+ "\t" + String.format("%-15s", position.getCurrentValue()) 
				+ "\t" + String.format("%-10s",position.getPositionReturn()), position.toString());
	}
	
}
