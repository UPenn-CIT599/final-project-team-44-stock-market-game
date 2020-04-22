import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
/**
 * This class houses the user interaction and the loops
 * that make the game run
 * @author jarod
 *
 */
public class ChadTrade {
	
	// instance variables for Trade class
	private String stockSymbol = null;
	private double amount = 0.0;
	private HashMap<String, Position> readPortfolio = new HashMap<String, Position>();
	YahooQuote quote = new YahooQuote();
	private int userSelection = 0;
	private String fileName = null;
	
	/**
	 * Local helper method to complete buys and sells when the individual selects either option
	 * 1 or 2 from the option method
	 * @param port
	 * @param selectedOption
	 * @param s
	 * @return
	 */
	private Portfolio optionsOneAndTwo(Portfolio port, int selectedOption, Scanner s) {
		String tradeAction = "buy";
		if (selectedOption == 2) {
			tradeAction = "sell";
		}
		System.out.println("Please enter the stock symbol you would like to " + tradeAction + ".");
		String stockSymbol = s.next().toUpperCase();
		stockSymbol = this.getValidSymbol(s, stockSymbol);
		
		double availableShares = 0;
		String availSharesString = "";
		
		//if trying to sell shares of an un-owned position, break out of the method and go back
		//to caller.
		if (selectedOption == 2 && !port.portfolio.containsKey(stockSymbol)) {
			System.out.println("You do not own " + stockSymbol + " and cannot sell any shares.");
			return port;
		}
		if (selectedOption == 2 && port.portfolio.containsKey(stockSymbol)) {
			availableShares = port.portfolio.get(stockSymbol).getShares();
			availSharesString = String.format("%,.0f", availableShares) + " shares ";
		}
		else {
			availableShares = port.portfolio.get("USDCASH").getShares();
			availSharesString = "$" + String.format("%,.2f", availableShares);
		}
		if (!stockSymbol.equals("EXIT")) {
			double price;
			try {
				price = Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart"));
				System.out.println(stockSymbol + " is currently trading at $" + String.format("%,.2f", price));
				System.out.println("You currently have " + availSharesString 
				+ " available to " + tradeAction + ".  How many shares would you like to " + tradeAction + "?");
				
				int shares = getValidInt(s);

				boolean sufficientShares = false;
				
				//if selling, confirm we have enough shares to sell.
				if (selectedOption == 2) {
					sufficientShares = port.hasSufficientShares(stockSymbol, shares);
					shares = -shares;
				}
				//if buying, confirm there is enough cash to purchase the shares.
				else {
					double netMoney = shares * price;
					sufficientShares = port.hasSufficientShares("USDCASH", netMoney);
				}
				
				//continue with trade options if we have enough shares/cash.
				if (sufficientShares) {
					//give the user the options to execute or cancel the trade
					System.out.println("Please choose and enter a number from the following:");
					System.out.println("   1. execute trade");
					System.out.println("   2. cancel trade");
					int optionSelection = s.nextInt();
					// divide the response into two separate cases
					switch (optionSelection) {
					case 1:
						port.tradeStock(stockSymbol, shares);
						port.updatePortfolio();
						break;
					case 2:
						break;
					}
				}
				else {
					//set default for a sell
					String sharesOrCash = "shares";
					String action = "sell";
					String currShares = "";
					double currentShares = 0;
					if (port.portfolio.containsKey(stockSymbol) && selectedOption == 2) {
						currentShares = port.portfolio.get(stockSymbol).getShares();	
						currShares = String.format("%,.0f", currentShares);
					}
					
					//if buying, flip the verbiage to cash.
					if (selectedOption == 1) {
						sharesOrCash = "cash";
						action = "buy";
						currentShares = port.portfolio.get("USDCASH").getShares();
						currShares = "$" + String.format("%,.2f", currentShares);
					}
					//sharesOrCash is passed in to the string.
					System.out.println("You do not have sufficient " + sharesOrCash + " to " + action + " " + 
							Math.abs(shares) + " shares of " + stockSymbol + ". You only have " 
							+ currShares  + " " + sharesOrCash + ".");
				}

		} catch (NumberFormatException e) {
			//	e.printStackTrace();
			} catch (IllegalStateException e) {
			//	e.printStackTrace();
			} catch (IOException e) {
			//	e.printStackTrace();
			}
		}
		//if the user exited the getValidSymbol method by typing "EXIT" as their symbol.
		else {
			System.out.println("You exited the stock selection without providing a valid symbol.");
		}
		return port;
	}		
	
	/**
	 * Does error handling and user interaction for when an invalid or nonexistent s is input by the user.
	 * locally used helper method
	 * @return String with either a valid symbol or "EXIT" which will exit the loop for the option that we are passing
	 * back to.
	 */
	private String getValidSymbol(Scanner s, String symbol) {
		//enters while loop with validSymbol = false. Once the user enters a valid symbol, we will \
		//exit the loop and return the valid symbol to the caller.
		boolean validSymbol = false;
		while (!validSymbol && !symbol.toUpperCase().equals("EXIT")) {
			try {
				validSymbol = quote.isValidSymbol(symbol);
				if (!validSymbol) {
					symbol = s.next().toUpperCase();
					System.out.println(symbol);
				}
				//if the user typed in exit, it allows them to exit this loop and return to the options menu.
				if (symbol.toUpperCase().equals("EXIT")) {
					return "EXIT";
				}
			} catch (IOException e) {
				System.out.println("IOException. The symbol you input does not exist. "
						+ "Please input a valid symbol to continue or type \"exit\" to return to the options menu.");
				// e.printStackTrace();
				symbol = s.next().toUpperCase();
			} catch (IllegalStateException e) {
				System.out.println("IllegalStateException. The symbol you input does not trade. "
						+ "Please input a valid symbol to continue or type \"exit\" to return to the options menu.");
				// e.printStackTrace();
				symbol = s.next().toUpperCase();
			}
		}
		return symbol;
	}


	public int getValidInt(Scanner s) {
		int shares = -1;
		boolean validShares = false;
		while (!validShares || shares <= 0) {
			try {
				shares = s.nextInt();
				if (shares <= 0) {
					System.out.println("Invalid shares. Please input an integer greater than 0.");
				}
				else {
					validShares = true;
				}
			} catch (InputMismatchException e) {
				validShares = false;
				System.out.println("InputMismatchException. Please input a valid positive integer.");
//				shares = s.nextInt();
				s.nextLine();
				// e.printStackTrace();
			}
		}
		return shares;
	}
	
// Make this double and all inputs for amounts and selections doubles to make easier?	
	/**
	 * Handles errors and invalid input for parameter selection.
	 * only used locally as a helper method
	 * @param s
	 * @param lowOption
	 * @param highOption
	 * @return
	 */
	private int getValidInt(Scanner s, int lowOption, int highOption) {
		int option = 0;
		while (option < lowOption || option > highOption) {
			try {
				option = s.nextInt();
				if (option < lowOption || option > highOption) {
					System.out.println("Invalid option selection. Please input an integer "
							+ "between " + lowOption + " and " + highOption + ".");
					s.nextLine();					
				}
			} catch (InputMismatchException e) {
				System.out.println("InputMismatchException. Please input an integer " 
						+ "between " + lowOption + " and " + highOption + ".");
				// e.printStackTrace();
				s.nextLine();
			}  
		}
		return option;
	}
	
	/**
	 * Local helper method to be able to loop through the switch statement
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
			userSelection = this.getValidInt(optionScanner, 1, 6);
			switch (userSelection) {
// Updated				
				case 1:
					portfolio = this.optionsOneAndTwo(portfolio, 1, optionScanner);
					break;
// Updated				
				case 2:
					portfolio = this.optionsOneAndTwo(portfolio, 2, optionScanner);
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
					if(portfolio == null) {
						// ask individual for a file name so we can write the file out
						System.out.println("What would you like to name your output file?  Please do not include file extension. It will be written as a CSV file");
						fileName = optionScanner.next() + ".csv";
						portfolio = this.initialCashDeposit(amount);
						portfolio.updatePortfolio();
						this.options(portfolio);
					} else {
						portfolio.updateCash(amount); // updates the cash in the new portfolio to what the user input
						portfolio.updatePortfolio();
					}
					break;
// Chris Handling				
				case 5:
					System.out.println("How much would you like to withdraw?");
					amount = optionScanner.nextDouble();
					if(portfolio.hasSufficientShares("USDCASH", amount) == true) {
						portfolio.updateCash(-amount);
						portfolio.updatePortfolio();
					} else {
						System.out.println("You do not have enough cash to withdraw.  Please choose choose and enter a number from the following:");
						System.out.println("   1. sell stock");
						System.out.println("   2. cancel transaction");
						userSelection = this.getValidInt(optionScanner, 1, 2);
						
						switch (userSelection) {
							case 1:
								// maybe 
								portfolio = this.optionsOneAndTwo(portfolio, 1, optionScanner);
								break;
							case 2:
								break;
						}
					}
					break;
			}
		}
		optionScanner.close();
	}
	
	/**
	 * creates an initial cash portfolio if the individual doesn't have a file to read in
	 * @param amount
	 */
	private Portfolio initialCashDeposit(double amount) {
		Position cash = new Position("USDCASH", amount, 1);
		HashMap<String, Position> newPortfolio = new HashMap<String, Position>();
		newPortfolio.put("USDCASH", cash);
		Portfolio portfolio = new Portfolio(newPortfolio);
		
		return portfolio;
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
		ChadPositionFileIO file = new ChadPositionFileIO();
		Scanner s = new Scanner(System.in);
// updated		
		userSelection = this.getValidInt(s, 1, 2);
		Portfolio portfolio = null;
		
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
					 * option if individual does not have a file to read from/initial portfolio;
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
// need error handling here; possibly make getValidInt an interface?
// create similar method to getValidInt
							amount = s.nextDouble();
							// pass the cash deposit into the portfolio
							portfolio = this.initialCashDeposit(amount);
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

// loop back into the option 2 to get quote or deposit
// Jarod Handling
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
