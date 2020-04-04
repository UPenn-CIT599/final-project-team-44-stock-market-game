import java.io.IOException;
import java.util.*;

/**
 * constructs a Porfolio object from a file/initial cash poistion
 * and updates the porfolio when individual buys/sells
 * @author jarod
 *
 */
public class Portfolio {

	ArrayList<Position> portfolio = new ArrayList<Position>();
	YahooQuote quote = new YahooQuote();
	
	/**
	 * Constructs the Portfolio object
	 * @param positions
	 */
	public Portfolio(ArrayList<Position> positions) {
		this.portfolio = positions;
	}
	
	/**
	 * returns all Position objects read from csv
	 * @return
	 */
	public ArrayList<Position> getPositions() {
		return portfolio;
	}
	
	/*
	 * Prints your positions line by line for review.
	 */
	public void printPort() {
		System.out.println(String.format("%-10s","SYMBOL") 
		+ "\t" + String.format("%-10s", "SHARES") 
		+ "\t" + String.format("%-10s", "AVG COST") 
		+ "\t" + String.format("%-10s", "LAST PRICE") 
		+ "\t" + String.format("%-15s","COST BASIS") 
		+ "\t" + String.format("%-15s", "CURR VALUE") 
		+ "\t" + String.format("%-10s", "RETURN (%)"));
		for (Position pos : portfolio) {
			System.out.println(pos);
		}
	}
	
	/**
	 * Checks the portfolio to make sure it holds sufficient shares of the stock to sell
	 * to prevent over-selling the position.
	 * @param symbol
	 * @param double shares
	 * @return true if there are sufficient shares to sell, false if there are not
	 * sufficient shares to sell.
	 */
	public boolean hasSufficientShares(String symbol, double shares) {
		double currentShares = 0.0;
		for (Position position : portfolio) {
			if(position.getSymbol().toUpperCase().equals(symbol.toUpperCase())) {
				currentShares = position.getShares();
			}
		}
		if (currentShares >= shares) {
			return true;
		} else return false;
	}
	
	/**
	 * Checks the portfolio to make sure it has sufficient cash to buy the desired
	 * shares of the stock..
	 * @param symbol
	 * @param shares
	 * @return true if there is enough cash to buy the stock, false if there is not enough cash.
	 */
	public boolean hasSufficientCash(String symbol, double shares) {
		try {
			double price = quote.getLastPrice(symbol);
			double cashToSpend = price * shares;
			for (Position position : portfolio) {
				if(position.getSymbol().toUpperCase().equals("USDCASH")) {
					Position cash = position;
					if(cash.getCurrentValue() < cashToSpend) {
						return false;
					}
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean positionExists(String symbol) {
		for (Position pos : portfolio) {
			if (pos.getSymbol().toUpperCase().equals(symbol.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	public void buyStock(String symbol, double shares) {
		try {
			String stockSymbol = symbol.toUpperCase();
			double price = quote.getLastPrice(stockSymbol);
			double marketValue = price * shares;
			Position p = new Position(stockSymbol, shares, price);
			portfolio.add(p);
			this.updateCash(-marketValue);
			System.out.println("You bought " + shares + " shares of " + stockSymbol + " at $" + price);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void liquidateStocks(String symbol) {
		String stockSymbol = symbol.toUpperCase();
		int myPosition = 0;
		for (int i = 0; i < portfolio.size(); i++) {
			if (portfolio.get(i).getSymbol().equals(stockSymbol)) {
				//storing position in portfolio to remove after iterating over ArrayList
				//to avoid causing concurrent modification exception.
				myPosition = i;
				Position p = portfolio.get(i);
				double shares = p.getShares();
				double price = p.getLastPrice();
				double marketValue = p.getCurrentValue();
				this.updateCash(marketValue);
				System.out.println("You sold " + shares + " shares of " + stockSymbol + " at $" + price);
			}
		}
		portfolio.remove(myPosition);
	}
	
	public void liquidateStock(String symbol) {
		String stockSymbol = symbol.toUpperCase();
		int i = 0;
		//searching through portfolio to find the index for symbol.
		while (!portfolio.get(i).getSymbol().equals(stockSymbol)) {
			i++;
		}
		Position p = portfolio.get(i);
		double shares = p.getShares();
		double price = p.getLastPrice();
		double marketValue = p.getCurrentValue();
		this.updateCash(marketValue);
		System.out.println("You sold " + shares + " shares of " + stockSymbol + " at $" + price);
		portfolio.remove(p);
	}
	
	public void updateCash(double marketValue) {
		for (Position cash : portfolio) {
			if (cash.getSymbol().toUpperCase().equals("USDCASH")) {
				double currCash = cash.getShares();
				double newCashValue = currCash + marketValue;
				cash.setShares(newCashValue);
				cash.setCostBasis(newCashValue);
				cash.setCurrentValue(newCashValue);
			}
		}
	}
	
	/**
	 * Updates the porfolio to include the most recent trade
	 * when an individual buys or sells
	 * updates cash balance and shares and adds new position
	 * if not already in the portfolio/deletes position if they
	 * sell all their shares in a specific stock
	 */
	public void updatePositions() {
		//need to update this code
		//need to think about how to add positions when they don't exist
		//as well as how to delete positions if the individual sells all
		//Cash position needs to update every time as well
	}
	
	
	/**
	 * Returns the current market value of each stock in the current
	 * portfolio as well as cash using the current quoted price
	 * and the symbol as the key 
	 * @return
	 */
	public double getMarketValue(String symbol) {
		double currentValue = 0;
		for (Position position : portfolio) {
			if(position.getSymbol().equals(symbol)) {
				currentValue = position.getShares() * position.getLastPrice();
			}
		}
		return currentValue;
	}
	
	
	/**
	 * returns the percentage return the stock has had
	 * since owning it
	 * percent return = (current market value minus cost basis)/cost basis
	 * @param symbol
	 * @return
	 */
	public double getReturn(String symbol) {
		double currentReturn = 0;
		for (Position position : portfolio) {
			if (position.getSymbol().equals(symbol)) {
				double currentPrice = position.getLastPrice();
				double currentMarketValue = position.getShares() * currentPrice;
				currentReturn = (currentMarketValue - position.getCostBasis()) / position.getCostBasis();
			}
		}
		return currentReturn;
	}
	
	
	/**
	 * returns the average cost per share given a symbol
	 * @param symbol
	 * @return
	 */
	public double getAvgCost(String symbol) {
		double avgCostPerShare = 0;
		for (Position position : portfolio) {
			if (position.getSymbol().equals(symbol)) {
				double shares = position.getShares();
				double cost = position.getCostBasis();
				avgCostPerShare = cost / shares;
			}
		}
		return avgCostPerShare;
	}
	
	public static void main(String[] args) {
		PositionFileIO posFile = new PositionFileIO();
		//ArrayList<Position> portfolio = new ArrayList<Position>(); 
		Portfolio port = new Portfolio(posFile.readpositionCSV("DummyStockPortfolio.csv"));
		
		String continueTrading = "Y";
		Scanner in = new Scanner(System.in);
		port.printPort();
		while (continueTrading.toUpperCase() != "N") {
			System.out.println("Do you want to continue trading? N to stop, anything else to keep trading.");
			continueTrading = in.next();
			System.out.println("buy or sell?");
			String action = in.next();
			if (action.equals("buy")) {
				port.buyStock("ABT", 100);
				System.out.println();
				port.printPort();
			}
			else {
				System.out.println();
				port.liquidateStock("GE");
				System.out.println();
				port.printPort();
			}
		}
		
		/*
		System.out.println();
		port.printPort();
		System.out.println();
		port.buyStock("ABT", 100);
		System.out.println();
		port.printPort();
		System.out.println();
		port.liquidateStock("GE");
		System.out.println();
		port.printPort();
		*/
	/*
		System.out.println(port.positionExists("APL"));
		//test - should return false
		System.out.println("Expect false: " + port.hasSufficientCash("AAPL", 1040));
		//test - should return true
		System.out.println("Expect true: " + port.hasSufficientCash("AAPL", 1030));
		//test - should return true
		System.out.println("Expect true: " + port.hasSufficientShares("GE", 10));
		//test - should return fales
		System.out.println("Expect false: " + port.hasSufficientShares("GE", 11));
	*/
	}
	
}
