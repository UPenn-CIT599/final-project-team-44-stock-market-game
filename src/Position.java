import java.io.IOException;

public class Position {

	private String symbol;
	private double shares;
	private double averageCost;
	private double lastPrice;
	private double costBasis;
	private double currentValue;
	private double positionReturn;
	
	/**
	 * Constructs a position object to be used in other classes
	 * a position is equivalent to a single stock
	 * @param currSymbol
	 * @param currShares
	 * @param currAverageCost
	 */
	public Position(String currSymbol, double currShares, double currAverageCost) {
		YahooQuote getPrice = new YahooQuote();
		symbol = currSymbol.toUpperCase();
		shares = currShares;
		//USDCASH is the actual cash position, checking and setting cost to 1
		//which equal one US dollar
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
	
	/**
	 * returns the stock symbol of the position object
	 * @return
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * returns the number of shares of a stock from the position object
	 * @return
	 */
	public double getShares() {
		return shares;
	}
	
	/**
	 * returns the average cost of a stock from the position object
	 * @return
	 */
	public double getAverageCost() {
		return averageCost;
	}

	/**
	 * returns the last price of a stock from the position object
	 * @return
	 */
	public double getLastPrice() {
		return lastPrice;
	}

	/**
	 * returns the cost basis of a stock from the position object
	 * cost basis = total price you paid for all shares of a particular stock
	 * @return
	 */
	public double getCostBasis() {
		return costBasis;
	}

	/**
	 * returns the current value of a stock from the position object
	 * current value = last price * number of shares
	 * @return
	 */
	public double getCurrentValue() {
		return currentValue;
	}

	/**
	 * returns the return on a stock from the position object
	 * return = (current value / cost basis) minus one
	 * @return
	 */
	public double getPositionReturn() {
		return positionReturn;
	}

	/**
	 * helper method to format the position object 
	 * when printing to the console
	 */
	public String toString() {
		return (String.format("%-10s",symbol) 
				+ "\t"  + String.format("%-10s",shares) 
				+ "\t" + String.format("%-10s",averageCost) 
				+ "\t"+ String.format("%-10s",lastPrice) 
				+ "\t" + String.format("%-15s", costBasis) 
				+ "\t" + String.format("%-15s", currentValue) 
				+ "\t" + String.format("%-10s",positionReturn));
	}

	/**
	 * sets the number of shares for a stock from the position object
	 * @param d
	 */
	public void setShares(double d) {
		this.shares = d;
	}

	/**
	 * sets the average cost of a single share of a particular stock
	 * from the position object
	 * @param averageCost
	 */
	public void setAverageCost(double averageCost) {
		this.averageCost = averageCost;
	}

	/**
	 * sets the last price per share of a stock from the position object
	 * @param lastPrice
	 */
	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}

	/**
	 * sets the cost basis of a particular stock from the position object
	 * @param costBasis
	 */
	public void setCostBasis(double costBasis) {
		this.costBasis = costBasis;
	}

	/**
	 * sets the current value of a stock from the position object
	 * @param currentValue
	 */
	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * sets the return of a stock from the position object
	 * @param positionReturn
	 */
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
