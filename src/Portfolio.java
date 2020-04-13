import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * constructs a Porfolio object from a file/initial cash poistion
 * and updates the porfolio when individual buys/sells
 * @author jarod
 *
 */
public class Portfolio {
	
	/**
	 * local variables to aid in the methods within the class
	 */
	HashMap<String, Position> portfolio = new HashMap<String, Position>();
	YahooQuote quote = new YahooQuote();
	
	/**
	 * Constructs the Portfolio object
	 * @param positions
	 */
	public Portfolio(HashMap<String, Position> positions) {
		this.portfolio = positions;
	}
	
	/**
	 * returns all Position objects read from csv
	 * @return
	 */
	public HashMap<String, Position> getPositions() {
		return portfolio;
	}
	
	/*
	 * Prints your positions line by line for review in a formatted
	 * output for easy reading by individual
	 */
	public void printPort() {
		System.out.println(String.format("%-10s","SYMBOL") 
		+ "\t" + String.format("%-10s", "SHARES") 
		+ "\t" + String.format("%-10s", "AVG COST") 
		+ "\t" + String.format("%-10s", "LAST PRICE") 
		+ "\t" + String.format("%-15s","COST BASIS") 
		+ "\t" + String.format("%-15s", "CURR VALUE") 
		+ "\t" + String.format("%-10s", "RETURN (%)"));
		for (String symbol : portfolio.keySet()) {
			System.out.println(portfolio.get(symbol));
		}
	}
	
	/**
	 * Checks the portfolio to make sure it holds sufficient shares of the stock to sell
	 * to prevent short selling the position.
	 * @param symbol
	 * @param double shares
	 * @return true if there are sufficient shares to sell, false if there are not
	 * sufficient shares to sell.
	 */
	public boolean hasSufficientShares(String symbol, double shares) {
		double currentShares = portfolio.get(symbol).getShares();
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
			double currCash = portfolio.get("USDCASH").getCurrentValue();
			if (currCash < cashToSpend) {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * adds positions to the existing portfolio when the
	 * individual wants to buy stock
	 * @param symbol
	 * @param shares
	 */
	public void buyStock(String symbol, double shares) {
		try {
			String stockSymbol = symbol.toUpperCase();
			double price = quote.getLastPrice(stockSymbol);
			double marketValue = price * shares;
			Position p = new Position(stockSymbol, shares, price);
			portfolio.put(symbol, p);
			this.updateCash(-marketValue);
			System.out.println("You bought " + shares + " shares of " + stockSymbol + " at $" + price);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * removes the entirety of a position from the portfolio
	 * if the individual wants to sell all their shares
	 * in an owned position
	 * @param symbol
	 */
	public void liquidateStock(String symbol) {
		String stockSymbol = symbol.toUpperCase();
		double price = portfolio.get(stockSymbol).getLastPrice();
		double marketValue = portfolio.get(stockSymbol).getCurrentValue();
		this.updateCash(marketValue);
		System.out.println("You liquidated your position of " + stockSymbol + " at $" + price);
		portfolio.remove(stockSymbol);
	}
	
	public void addToPosition(String symbol, double shares) {
		String stockSymbol = symbol.toUpperCase();
		Position origP = portfolio.get(stockSymbol);
		double price = origP.getLastPrice();
		double origShares = origP.getShares();
		double origCostBasis = origP.getCostBasis();
		double marketValue = price * shares;
		double newShares = origShares + shares;
		double newAvgCost = (origCostBasis + marketValue) / newShares;
		
		Position updatedPosition = new Position(stockSymbol, newShares, newAvgCost);
		portfolio.put(stockSymbol, updatedPosition);
		this.updateCash(-marketValue);
	}
	
	public void trimPosition(String symbol, double shares) {
		String stockSymbol = symbol.toUpperCase();
		Position origP = portfolio.get(stockSymbol);
		double price = origP.getLastPrice();
		double origShares = origP.getShares();
		double origCostBasis = origP.getCostBasis();
		double marketValue = price * shares;
		double newShares = origShares - shares;
		double newAvgCost = (origCostBasis - marketValue) / newShares;
		
		Position updatedPosition = new Position(stockSymbol, newShares, newAvgCost);
		portfolio.put(stockSymbol, updatedPosition);
		this.updateCash(marketValue);
	}
	
	/**
	 * updates the current total cash in the portfolio when 
	 * buys and sells happen
	 * buys will subtract from the cash while sells will add to the cash
	 * @param marketValue
	 */
	public void updateCash(double marketValue) {
		Position cash = portfolio.get("USDCASH");
		double currCash = cash.getShares();
		double newCashValue = currCash + marketValue;
		cash.setShares(newCashValue);
		cash.setCostBasis(newCashValue);
		cash.setCurrentValue(newCashValue);
	}
	
	/*
	 * Updates all portfolio positions except for cash, which is updated in its own methos.
	 * This will pull the latest price for each position and update all other position variables.	
	 */
	public void updatePortfolio() {
		for (String symbol : portfolio.keySet()) {
			if (!symbol.equals("USDCASH")) {
				double shares = portfolio.get(symbol).getShares();
				double avgCost = portfolio.get(symbol).getAverageCost();
				Position p = new Position(symbol, shares, avgCost);
				portfolio.put(symbol, p);
			}
		}
		this.printPort();
	}
	/**
	 * Updates the porfolio to include the most recent trade
	 * when an individual buys or sells
	 * updates cash balance and shares and adds new position
	 * if not already in the portfolio/deletes position if they
	 * sell all their shares in a specific stock
	 */
	public void updatePositions() {
		//may or may not be used based on above methods
		//need to update this code
		//need to think about how to add positions when they don't exist
		//as well as how to delete positions if the individual sells all
		//Cash position needs to update every time as well
	}
	

	/**
	 * main method for user interaction
	 * @param args
	 */
	
	//ideally this goes in a separate class, still toying with 
	//ideas here and don't have a final product on what this 
	//will look like in the separate class
	public static void main(String[] args) {
		PositionFileIO posFile = new PositionFileIO();
		//ArrayList<Position> portfolio = new ArrayList<Position>(); 
		Portfolio port;
		try {
			port = new Portfolio(posFile.readpositionCSV("DummyStockPortfolio.csv"));
			String continueTrading = "Y";
			Scanner in = new Scanner(System.in);
			port.updatePortfolio();
			//port.printPort();
			System.out.println("Do you want to continue trading? N to stop, anything else to keep trading.");
			continueTrading = in.next();
			while (!continueTrading.toUpperCase().equals("N")) {
				System.out.println("buy or sell?");
				String action = in.next();
				if (action.equals("buy")) {
					port.buyStock("ABT", 100);
					System.out.println();
					port.updatePortfolio();
					//port.printPort();
				}
				else if (action.equals("add")) {
					port.addToPosition("AAPL", 20);
					port.updatePortfolio();				
				}
				else if (action.equals("trim")) {
					port.trimPosition("AAPL", 20);
					port.updatePortfolio();				
				}
				else {
					System.out.println();
					port.liquidateStock("GE");
					System.out.println();
					port.updatePortfolio();
					//port.printPort();
				}
				System.out.println("Do you want to continue trading? N to stop, anything else to keep trading.");
				continueTrading = in.next();
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		//port.printPort();
		
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
