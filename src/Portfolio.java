import java.io.IOException;
import java.util.*;

/**
 * constructs a Porfolio object from a file/initial cash poistion
 * and updates the porfolio when individual buys/sells
 * @author jarod
 *
 */

public class Portfolio implements FinancialActivity{
	
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
	 * Helper method. Prints your positions line by line for review in a formatted
	 * output for easy reading by individual. This should never be called directly.
	 * It should only be accessed via the updatePortfolio method as that will print the
	 * portfolio with updated prices.
	 */
	
	public void printPort() {
		System.out.println(String.format("%10s","SYMBOL") 
		+ "\t" + String.format("%13s", "SHARES") 
		+ "\t" + String.format("%13s", "AVG COST") 
		+ "\t" + String.format("%13s", "LAST PRICE") 
		+ "\t" + String.format("%18s","COST BASIS") 
		+ "\t" + String.format("%18s", "CURR VALUE") 
		+ "\t" + String.format("%10s", "RETURN (%)"));
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
		if (!portfolio.containsKey(symbol.toUpperCase())) {
			return false;
		}
		else {
			double currentShares = portfolio.get(symbol).getShares();
			if (currentShares >= shares) {
				return true;
			} else return false;
		}
	}

	/**
	 * trades stocks in the portfolio. If the position does not exist, it adds it to the portfolio.
	 * If the position already exists, it recalculates shares and average cost and updates the
	 * position in the portfolio. IF the position is being liquidated, it reomves the position
	 * from the portfolio.
	 * @param symbol
	 * @param shares
	 */
	@Override
	public void buySell(String symbol, double shares) {
		try {
			Position p;
			String stockSymbol = symbol.toUpperCase();
			//initializes tradeAction to bought. Will be changed to sold later if shares < 0.
			String tradeAction = "bought";
			int absShares = (int) Math.abs(shares);
			double price = Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart"));;
			double netMoney = price * shares;
			
			//Liquidating a position
			if (portfolio.containsKey(stockSymbol) && shares + portfolio.get(stockSymbol).getShares() == 0) {
				portfolio.remove(stockSymbol);
			}
			else {
				//New purchase of a position
				if (!portfolio.containsKey(stockSymbol) && shares > 0) {
					p = new Position(stockSymbol, shares, price);
				}
				//add or trim an existing position
				else {
					double newShares = portfolio.get(stockSymbol).getShares() + shares;
					//calculate average cost on sell (avg cost does not change) vs. buys (need
					//to recalculate the new average cost).
					double newAvgCost = portfolio.get(stockSymbol).getAverageCost();
					if (shares > 0) {
						newAvgCost = (portfolio.get(stockSymbol).getCostBasis() + netMoney) / newShares;	
					}
					p = new Position(stockSymbol, newShares, newAvgCost);
				}
				//putting the position in to the portfolio on a new buy or 
				//updating the position on add or trim
				portfolio.put(stockSymbol, p);
			}

			//update cash balance in the portfolio
			this.updateCash(-netMoney);
			
			//original tradeAction set to bought. If shares < 0, this flips the tradeAction to sold.
			if (shares < 0) {
				tradeAction = "sold";
			}
			
			System.out.println("You " + tradeAction + " " + absShares + " shares of " + stockSymbol + " at $" + price);
					
			//Try Catch is here in this method as a requirement for the price call to quote; however,
			//the program already runs getValidSymbol in Trade class before calling tradeStock. The program
			//cannot trigger this IOException.
			} catch (IOException e) {
				System.out.println("The symbol you input cannot return a price.");
//				e.printStackTrace();
			}
	}

	
	/**
	 * updates the current total cash in the portfolio when 
	 * buys and sells happen
	 * buys will subtract from the cash while sells will add to the cash
	 * @param marketValue
	 */
	@Override
	public void updateCash(double marketValue) {
		Position cash = portfolio.get("USDCASH");
		double currCash = cash.getShares();
		double newCashValue = currCash + marketValue;
		cash = new Position("USDCASH", newCashValue, 1);
		portfolio.put("USDCASH", cash);
	}
	
	/*
	 * Updates all portfolio positions except for cash, which is updated in its own method.
	 * This will pull the latest price for each position and update all other position variables.
	 * The portfolio is then displayed to the user.	
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
}
