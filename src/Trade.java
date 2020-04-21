import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
/**
 * This class houses the user interaction and the loops
 * that make the game run
 * @author jarod
 *
 */
public class Trade {
	
	// instance variables for Trade class
	private String stockSymbol = null;
	private double amount = 0.0;
	private HashMap<String, Position> readPortfolio = new HashMap<String, Position>();
	YahooQuote quote = new YahooQuote();
	private int userSelection = 0;
	
	/**
	 * helper method to be able to loop through the switch statement
	 * and get through all options in an efficient manner
	 * provides the individual with 6 options: buy stock, sell stock, get a stock quote,
	 * deposit cash, withdraw cash, or exit the trading session
	 */
	private void options(Portfolio portfolio) {
		Scanner optionScanner = new Scanner(System.in);
		
		
		while (userSelection < 6) {	
			System.out.println("What would you like to do next?  Please choose and enter a number from the following options");
			System.out.println("   1. buy stock");
			System.out.println("   2. sell stock");
			System.out.println("   3. get a stock quote");
			System.out.println("   4. deposit cash");
			System.out.println("   5. withdraw cash");
			System.out.println("   6. exit trading session");
			userSelection = optionScanner.nextInt();
			switch (userSelection) {
// Chad handling				
				case 1:
					try {
						// ask the user for a stock symbol and check to make sure it is valid
						// then gives the user a quote with the available cash in their portfolio
						System.out.println("Please enter the stock symbol you would like to buy.");
						stockSymbol = optionScanner.next().toUpperCase();
						quote.isValidSymbol(stockSymbol); 
						System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
						
// need to check current portfolio for cash position
// need some help on this one, portfolio.get("USDCASH").getShares.... then pass into the
// Jarod to handle
						System.out.println("You currently have " + "" + " available to trade.  How many shares would you like to buy?");
						int shares = optionScanner.nextInt();
						
// calculate net money and pass in where the shares are passed						
// can probably add the throws exception to the method so we don't need the if statement
						if (portfolio.hasSufficientShares("USDCASH", shares) == true) {
							//give the user the options to execute or cancel the trade
							System.out.println("Please choose and enter a number from the following:");
							System.out.println("   1. execute trade");
							System.out.println("   2. cancel trade");
							userSelection = optionScanner.nextInt();
							// divide the response into two separate cases
							switch (userSelection) {
								case 1:
									portfolio.tradeStock(stockSymbol, shares);
									portfolio.updatePortfolio();
									break;
								case 2: 
									break;
							}
						} 
						
						else System.out.println("You don't have enough cash to purchase " + shares + "of " + stockSymbol);
						System.out.println();
					} catch (IllegalStateException e) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					} catch (IOException e) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					}
					break;
// Chad handling				
				case 2:
					try {
						// ask the user for a stock symbol and first check to make sure it's a valid symbol
						// then check to make sure they possess it in their portfolio
						// then gives the user a quote with the available shares in their portfolio
						System.out.println("Please enter the stock symbol you would like to sell.");
						stockSymbol = optionScanner.next();
						quote.isValidSymbol(stockSymbol); 
						System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
						
// need to check current portfolio for shares of the stock
// need some help on this one
						System.out.println("You currently have " + "" + " available to trade.  How many shares would you like to sell?");
						int shares = optionScanner.nextInt();
						// can probably add the throws exception to the method so we don't need the if statement
						
// code breaks here						
						
						if (portfolio.hasSufficientShares("USDCASH", shares) == true) {
							//give the user the options to execute or cancel the trade
							System.out.println("Please choose and enter a number from the following:");
							System.out.println("   1. execute trade");
							System.out.println("   2. cancel trade");
							userSelection = optionScanner.nextInt();
							//divide the response into two separate cases
							switch (userSelection) {
								case 1:
									portfolio.tradeStock(stockSymbol, -shares);
//do we have the trade price stored globally?
									System.out.println("You sold " + shares + " of " + stockSymbol + " at " + "");
									portfolio.updatePortfolio();
									break;
								case 2: 
									break;
							}
						}
						System.out.println();
					} catch (IllegalStateException e) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					} catch (IOException e) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					}
					break;
				// option 3 from the selection
				// allows individual to get a single stock quote and re-enter the option method
				case 3:
					System.out.println("Please enter the symbol of the stock you would like a quote on.");
					stockSymbol = optionScanner.next().toUpperCase();
					try {
						quote.isValidSymbol(stockSymbol);
						//System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
						quote.returnStockQuote(stockSymbol);
						// after getting the first quote it enters into the options helper method
						this.options(portfolio);
					} catch (IllegalStateException e1) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					} catch (IOException e1) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					}
					break;
// Chris Handling				
				case 4:
					System.out.println("How much would you like to deposit?");
// need error handling here
					amount = optionScanner.nextDouble();
					
// code breaks here					
					
					portfolio.updateCash(amount); // updates the cash in the new portfolio to what the user input
					portfolio.updatePortfolio();
					break;
// Chris Handling				
				case 5:
					System.out.println("How much would you like to withdraw?");
					amount = optionScanner.nextDouble();
					
// code breaks here					
					
// again, should we have the throws exception in the method?
					if(portfolio.hasSufficientShares("USDCASH", amount) == true) {
						portfolio.tradeStock("USDCASH", -amount);
						portfolio.updatePortfolio();
					} else System.out.println("You do not have enough cash to withdraw.  Please choose choose and enter a number from the following:");
					System.out.println("   1. sell stock");
					System.out.println("   2. cancel transaction");
					userSelection = optionScanner.nextInt();
					switch (userSelection) {
						case 1:
							try {
								// ask the user for a stock symbol and first check to make sure it's a valid symbol
								// then check to make sure they possess it in their portfolio
								// then gives the user a quote with the available shares in their portfolio
								System.out.println("Please enter the stock symbol you would like to sell.");
								stockSymbol = optionScanner.next();
								quote.isValidSymbol(stockSymbol); 
								// do we need a checkPositions method in Portfolio class to make sure they have the stock?
								System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
								
								// need to check current portfolio for shares of the stock
								// need some help on this one
								System.out.println("You currently have " + "" + " available to trade");
								
								// ask the user how many shares they would like to sell and check to make sure they have enough
								System.out.println("How many shares would you like to sell?");
								int shares = optionScanner.nextInt();
								// can probably add the throws exception to the method so we don't need the if statement
								if (portfolio.hasSufficientShares("USDCASH", shares) == true) {
									//give the user the options to execute or cancel the trade
									System.out.println("Please choose and enter a number from the following:");
									System.out.println("   1. execute trade");
									System.out.println("   2. cancel trade");
									userSelection = optionScanner.nextInt();
									//divide the response into two separate cases
									switch (userSelection) {
										case 1:
											portfolio.tradeStock(stockSymbol, -shares);
											//do we have the trade price stored globally?
											System.out.println("You sold " + shares + " of " + stockSymbol + " at " + "");
											break;
										case 2: 
											break;
									}
								}
								System.out.println();
							} catch (IllegalStateException e) {
								System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
								stockSymbol = optionScanner.next();
							} catch (IOException e) {
								System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
								stockSymbol = optionScanner.next();
							}
							break;
						case 2:
							break;
					}
					break;
			}
		}
		optionScanner.close();
	}
	
	/**
	 * single method to run the trading session for the game
	 */
	public void runTradingSession() {
		// Intro explaining the game and setting rules
		System.out.println("In this game we will simulate a brokerage account and trading stocks.");
		System.out.println("You will be able to buy and sell stocks, deposit and withdraw funds, or simply get a stock quote.");
		System.out.println("A few ground rules before we start the trading session:");
		System.out.println("   I. Stocks are limited to those that are currently trading on U.S. exchanges");
		System.out.println("   II. You will not be able to sell more shares than what you currently own, a.k.a. no short-selling.");
		System.out.println("   III. You must have the available cash to buy any stock, a.k.a. no margin.");
		System.out.println("   IV. You cannot buy or sell partial shares.  The minimum buy or sell must be 1.");
		System.out.println("   V. In order to buy or sell a stock you will need to know its symbol.  This can be found via any online search engine.");
		System.out.println();
		quote.indicesData();
		System.out.println();
		System.out.println("To start your trading session, please choose and enter a number from the following:");
		System.out.println("   1. Upload an existing portfolio from an outside file");
		System.out.println("   2. Continue without an existing portfolio");
		
		// instance variable to store values 
		PositionFileIO file = new PositionFileIO();
		Scanner s = new Scanner(System.in);
// error handling here for userSelection;  
// Chris to handle this
		userSelection = s.nextInt();
		String fileName = null;
		Portfolio portfolio = null;
		
			//need to make the portfolio here outside the 
			switch (userSelection) {
				case 1:				
					try {
						System.out.println("Please enter your file path and/or name.");
						fileName = s.next();
						readPortfolio = file.readpositionCSV(fileName);
						portfolio = new Portfolio(readPortfolio);
						portfolio.updatePortfolio();
						
						this.options(portfolio);
						break;
					} catch (FileNotFoundException e) {
// need code in here to make sure that it doesn't skip step or maybe in the error handling
// Jarod Handling
						System.out.println("Could not find the file with the provided path and/or name.");
						System.out.println("Please enter a valid path and/or file name.");
//						if ()
					} 
		
				case 2:
					/**
					 * option if individual does not have a file to read from/initial portfolio
					 * splits into another two options asking if the individual
					 * wants to first deposit cash or get a stock quote before entering into
					 * the options helper method 
					 */
					System.out.println("Please choose and enter a number from the following:");
					System.out.println("   1. deposit cash");
					System.out.println("   2. get a stock quote");
					// need exception handling here
					userSelection = s.nextInt();
					switch (userSelection) {
						// this option allows the individual to put cash into an empty portfolio
						// so that they can transact as they wish through the program
						case 1:
							// first asks the user to enter in a filename and stores the value 
							// so that we can properly print it out later
							System.out.println("What would you like to name your output file?  Please do not include file extension. It will be written as a CSV file");
							fileName = s.next() + ".csv";
							
							System.out.println("How much would you like to deposit?");
// need error handling here
							amount = s.nextDouble();
							// creates a new cash position and portfolio where all the
							// individual initially has is cash so that we can pass it
							// through the rest of the program
							Position cash = new Position("USDCASH", amount, 1);
							HashMap<String, Position> newPortfolio = new HashMap<String, Position>();
							newPortfolio.put("USDCASH", cash);
							// pass the cash into the portfolio
							portfolio = new Portfolio(newPortfolio);
							// skip a line for easy readability
							System.out.println();
							portfolio.updatePortfolio();
							// skip a line for easy readability
							System.out.println();
							
							this.options(portfolio);
							break;
						case 2:
							/**
							 * this option allows the individual to get a stock quote at first
							 * before entering the options helper method
							 */
							System.out.println("Please enter the symbol of the stock you would like a quote on.");
							stockSymbol = s.next();
							try {
// need the while loop to not leave the exception handling erroneously
// Chad to look at exception throwing/creating method to handle
								quote.isValidSymbol(stockSymbol);
								quote.returnStockQuote(stockSymbol);

								// after getting the first quote it enters into the options helper method
								this.options(portfolio);
							} catch (IllegalStateException e1) {
								System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
								stockSymbol = s.next();
							} catch (IOException e1) {
								System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
								stockSymbol = s.next();
							}
							break;
					}
			}
		// after individual enters 6 they see their portfolio and exit the program
		// the new portfolio is written over the old one in the same file they gave earlier
		System.out.println("You have exited the trading session.  Below is your current portfolio.");
		portfolio.updatePortfolio();
		try {
			file.writePositionCSV(fileName, portfolio);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();	
		
	}
}
