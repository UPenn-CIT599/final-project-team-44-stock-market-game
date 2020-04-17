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
	private Portfolio portfolio = new Portfolio(readPortfolio);  // may need to take this out and put in the loop
	YahooQuote quote = new YahooQuote();
	
	/**
	 * helper method to be able to loop through the switch statement
	 * and get through all options in an efficient manner
	 * provides the individual with 6 options: buy stock, sell stock, get a stock quote,
	 * deposit cash, withdraw cash, or exit the trading session
	 */
	private void options() {
		Scanner optionScanner = new Scanner(System.in);
		int optionSelection = 0;
		
		while (optionSelection < 6) {	
			System.out.println();
			System.out.println("What would you like to do next?  Please choose and enter a number from the following options");
			System.out.println("   1. buy stock");
			System.out.println("   2. sell stock");
			System.out.println("   3. get a stock quote");
			System.out.println("   4. deposit cash");
			System.out.println("   5. withdraw cash");
			System.out.println("   6. exit trading session");
			optionSelection = optionScanner.nextInt();
			switch (optionSelection) {
				case 1:
					try {
						// ask the user for a stock symbol and check to make sure it is valid
						// then gives the user a quote with the available cash in their portfolio
						System.out.println("Please enter the stock symbol you would like to buy.");
						stockSymbol = optionScanner.next();
						quote.isValidSymbol(stockSymbol); 
						System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
						
// need to check current portfolio for cash position
// need some help on this one
						System.out.println("You currently have " + "" + " available to trade.  How many shares would you like to buy?");
						int shares = optionScanner.nextInt();
						
// code breaks here
						
// can probably add the throws exception to the method so we don't need the if statement
						if (portfolio.hasSufficientShares("USDCASH", shares) == true) {
							//give the user the options to execute or cancel the trade
							System.out.println("Please choose and enter a number from the following:");
							System.out.println("   1. execute trade");
							System.out.println("   2. cancel trade");
							optionSelection = optionScanner.nextInt();
							// divide the response into two separate cases
							switch (optionSelection) {
								case 1:
									portfolio.tradeStock(stockSymbol, shares);
// do we have the trade price stored globally?
									System.out.println("You bought " + shares + " of " + stockSymbol + " at " + "");
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
						System.out.println("You currently have " + "" + " available to trade.  How many shares would you like to sell?");
						int shares = optionScanner.nextInt();
						// can probably add the throws exception to the method so we don't need the if statement
						
// code breaks here						
						
						if (portfolio.hasSufficientShares("USDCASH", shares) == true) {
							//give the user the options to execute or cancel the trade
							System.out.println("Please choose and enter a number from the following:");
							System.out.println("   1. execute trade");
							System.out.println("   2. cancel trade");
							optionSelection = optionScanner.nextInt();
							//divide the response into two separate cases
							switch (optionSelection) {
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
				case 3:
					System.out.println("Please enter the symbol of the stock you would like a quote on.");
					stockSymbol = optionScanner.next();
					try {
						quote.isValidSymbol(stockSymbol);
						//System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
						quote.returnStockQuote(stockSymbol);
						// after getting the first quote it enters into the options helper method
						this.options();
					} catch (IllegalStateException e1) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					} catch (IOException e1) {
						System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
						stockSymbol = optionScanner.next();
					}
					break;
				case 4:
					System.out.println("How much would you like to deposit?");
// need error handling here
					amount = optionScanner.nextDouble();
					
// code breaks here					
					
					portfolio.updateCash(amount); // updates the cash in the new portfolio to what the user input
					break;
				case 5:
					System.out.println("How much would you like to withdraw?");
					amount = optionScanner.nextDouble();
					
// code breaks here					
					
					// again, should we have the throws exception in the method?
					if(portfolio.hasSufficientShares("USDCASH", amount) == true) {
						portfolio.tradeStock("USDCASH", -amount);
					} else System.out.println("You do not have enough cash to withdraw.  Please choose choose and enter a number from the following:");
					System.out.println("   1. sell stock");
					System.out.println("   2. cancel transaction");
					optionSelection = optionScanner.nextInt();
					switch (optionSelection) {
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
									optionSelection = optionScanner.nextInt();
									//divide the response into two separate cases
									switch (optionSelection) {
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
		System.out.println("To start your trading session, please choose and enter a number from the following:");
		System.out.println("   1. Upload an existing portfolio from an outside file");
		System.out.println("   2. Continue without an existing portfolio");
		
		// instance variable to store values 
		PositionFileIO file = new PositionFileIO();
		Scanner s = new Scanner(System.in);
		int userSelection = s.nextInt();
		String fileName = null;
		
		while(userSelection < 6) {
			switch (userSelection) {
				case 1:
					try {
						System.out.println("Please enter your file path and/or name.");
						fileName = s.next();
						readPortfolio = file.readpositionCSV(fileName);
						Portfolio portfolio = new Portfolio(readPortfolio);
						portfolio.printPort();
						
						// make sure this makes sense after helper method is established
						
// need code in here to make sure that it doesn't skip step or maybe in the error handling
						
						this.options();
						break;
					} catch (FileNotFoundException e) {
						System.out.println("Could not find the file with the provided path and/or name.");
						System.out.println("Please enter a valid path and/or file name.");
					}
				case 2:
					/**
					 * option if the do not have a file to read from/initial portfolio
					 * they would like to trade from
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
						case 1:
							/**
							 * this option deposits cash so we enter into a buy order
							 * where the individual is buying 1 share of cash per dollar valued at
							 * 1 dollar per share, then enters the options helper method
							 */
							System.out.println("How much would you like to deposit?");
							// need error handling here
							amount = s.nextDouble();
					
// Code Breaks here							
							
							portfolio.updateCash(amount); //updates the cash in the new portfolio to what the user input
							// after updating the cash it enters into the helper options method
							this.options();
							break;
						case 2:
							/**
							 * this option allows the individual to get a stock quote at first
							 * before entering the options helper method
							 */
							System.out.println("Please enter the symbol of the stock you would like a quote on.");
							stockSymbol = s.next();
							try {
								quote.isValidSymbol(stockSymbol);
								System.out.println(stockSymbol + " is currently trading at $" + Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart")));
								
								// after getting the first quote it enters into the options helper method
								this.options();
							} catch (IllegalStateException e1) {
								System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
								stockSymbol = s.next();
							} catch (IOException e1) {
								System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
								stockSymbol = s.next();
							}
							break;
					}
					// make sure this makes sense after helper method is established
					this.options();
					break;
			}
		}
	
// code broke here		
		
		System.out.println("You have exited the trading session.  Below is your current portfolio.");
		portfolio.printPort();
		try {
			file.writePositionCSV(fileName, portfolio);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}
}
