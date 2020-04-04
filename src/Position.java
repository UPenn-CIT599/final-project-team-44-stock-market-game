import java.io.IOException;

public class Position {

	private String symbol;
	private double shares;
	private double averageCost;
	private double lastPrice;
	private double costBasis;
	private double currentValue;
	private double positionReturn;
	
	public Position(String currSymbol, double currShares, double currAverageCost) {
		YahooQuote getPrice = new YahooQuote();
		symbol = currSymbol.toUpperCase();
		shares = currShares;
		
		if (symbol.equals("USDCASH")) {
			averageCost = 1.0;
		}
		else {
			averageCost = currAverageCost;
		}
		
		//Last price is grabbed from the StockAPICall class to the API based on the position's symbol.
		//The last price grabbed is used to calculate current value and position return.
		
		if (symbol.equals("USDCASH")) {
			lastPrice = 1.0;
		}
		else {
			try {
				lastPrice = getPrice.getLastPrice(symbol);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		costBasis = Math.round(shares * averageCost * 100) / 100.00;
		currentValue = Math.round(shares * lastPrice * 100) / 100.00;
		//position return will be calculated to the nearest one hundredth of a percentage point.
		positionReturn = Math.round((currentValue / costBasis - 1) * 10000) / 100.0;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public double getShares() {
		return shares;
	}

	public double getAverageCost() {
		return averageCost;
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public double getCostBasis() {
		return costBasis;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public double getPositionReturn() {
		return positionReturn;
	}

	public String toString() {
		return (String.format("%-10s",symbol) 
				+ "\t"  + String.format("%-10s",shares) 
				+ "\t" + String.format("%-10s",averageCost) 
				+ "\t"+ String.format("%-10s",lastPrice) 
				+ "\t" + String.format("%-15s", costBasis) 
				+ "\t" + String.format("%-15s", currentValue) 
				+ "\t" + String.format("%-10s",positionReturn));
	}

	public void setShares(double d) {
		this.shares = d;
	}

	public void setAverageCost(double averageCost) {
		this.averageCost = averageCost;
	}

	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setCostBasis(double costBasis) {
		this.costBasis = costBasis;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public void setPositionReturn(double positionReturn) {
		this.positionReturn = positionReturn;
	}

/*
	public static void main(String[] args) {
		Position test = new Position("AAPL", 1, 127.40);
		//System.out.println(test.getLastPrice());
		//System.out.println(test.getShares());
		//System.out.println(test.getCostBasis());
		//System.out.println(test.getCurrentValue());
		//System.out.println(test.getPositionReturn());
		
		//System.out.println(test);
	}
*/	
}
