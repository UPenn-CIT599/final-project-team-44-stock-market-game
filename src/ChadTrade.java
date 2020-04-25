import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
/**
 * This class houses the user interaction and the loops
 * that make the game run
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
	private Scanner s = new Scanner(System.in);
	
	/**
	 * Local helper method to complete buys and sells when the individual selects either option
	 * 1 or 2 from the option method
	 * @param port
	 * @param selectedOption
	 */
	private Portfolio optionsOneAndTwo(Portfolio port, int selectedOption) {
		String tradeAction = "buy";
		if (selectedOption == 2) {
			tradeAction = "sell";
		}
		System.out.println("Please enter the stock symbol you would like to " + tradeAction + ".");
		s.nextLine();
		String stockSymbol = s.nextLine().toUpperCase();
		stockSymbol = this.getValidSymbol(stockSymbol);
		
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
		if (!stockSymbol.equals("*")) {
			double price;
			try {
				price = Double.parseDouble(quote.getField(stockSymbol, "regularMarketPrice\":(.+?),", "chart"));
				System.out.println(stockSymbol + " is currently trading at $" + String.format("%,.2f", price));
				System.out.println("You currently have " + availSharesString 
				+ " available to " + tradeAction + ".  How many shares would you like to " + tradeAction + "?");
				
				int shares = getValidInt();

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
					System.out.println("You have indicated you want to " + tradeAction + " " + Math.abs(shares) + " shares of " + stockSymbol);
					System.out.println("Please choose and enter a number from the following:");
					System.out.println("   1. execute trade");
					System.out.println("   2. cancel trade");
					int optionSelection = s.nextInt();
					// divide the response into two separate cases
					switch (optionSelection) {
					case 1:
						port.buySell(stockSymbol, shares);
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
					//convert shares to string and format
					String shsString = String.format("%,.0f",  (double) Math.abs(shares));
					
					//sharesOrCash is passed in to the string.
					System.out.println("You do not have sufficient " + sharesOrCash + " to " + action + " " + 
							shsString + " shares of " + stockSymbol + ". You only have " 
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
	 *  Local helper method to complete deposits and withdraws when the individual selects either option
	 * 3 or 4 from the option method
	 * @param port
	 * @param selectedOption
	 * @param s
	 * @return
	 */
	private Portfolio optionsFourAndFive(Portfolio port, int selectedOption) {
		double amount = 0;
		if (selectedOption == 4) {
			System.out.println("How much would you like to deposit?");
			//need error handling here
			amount = getValidDouble(0.01, Double.MAX_VALUE);
			if(port == null) {
				// ask individual for a file name so we can write the file out
				port = this.initialCashDeposit();
				port.updatePortfolio();
				
			} else {
				port.updateCash(amount); // updates the cash in the new portfolio to what the user input
				port.updatePortfolio();
			}
			
		}
		if (selectedOption == 5) {
			System.out.println("How much would you like to withdraw?");
			amount = getValidDouble(0.01, Double.MAX_VALUE);
			if(port.hasSufficientShares("USDCASH", amount) == true) {
				port.updateCash(-amount);
				port.updatePortfolio();
			} else {
				System.out.println("You do not have enough cash to withdraw.");
			}
		}
		return port;
	}
	
	/**
	 * Does error handling and user interaction for when an invalid or nonexistent s is input by the user.
	 * locally used helper method
	 * @return String with either a valid symbol or "*" which will exit the loop for the option that we are passing
	 * back to.
	 */
	private String getValidSymbol(String symbol) {
		//enters while loop with validSymbol = false. Once the user enters a valid symbol, we will \
		//exit the loop and return the valid symbol to the caller.
		boolean validSymbol = false;
		while (!validSymbol && !symbol.toUpperCase().equals("*")) {
			try {
				validSymbol = quote.isValidSymbol(symbol.toUpperCase());
				if (!validSymbol) {
					s.nextLine();
					symbol = s.nextLine().toUpperCase();
					System.out.println(symbol);
				}
				//if the user typed in exit, it allows them to exit this loop and return to the options menu.
				if (symbol.toUpperCase().equals("*")) {
					return "*";
				}
			} catch (IOException e) {
				System.out.println("IOException. The symbol you input does not exist. "
						+ "Please input a valid symbol to continue or type \"*\" to return to the options menu.");
				symbol = s.nextLine().toUpperCase();
			} catch (IllegalStateException e) {
				System.out.println("IllegalStateException. The symbol you input does not trade. "
						+ "Please input a valid symbol to continue or type \"*\" to return to the options menu.");
				symbol = s.nextLine().toUpperCase();
			}
		}
		return symbol;
	}

	/**
	 * local helper method to handle errors when individual enters a file to load
	 * @param fileName
	 * @return
	 */
	private Portfolio getValidFile(String fileName) {
		PositionFileIO readFile = new PositionFileIO();
		Portfolio portfolio = null;
		File file = new File(fileName);
		boolean exists = file.exists();
		while (exists == false && !fileName.equals("exit")) {
			System.out.println("Could not find your file. Please enter valid file path and/or name or type \"exit\" to continue without an existing file.");
			fileName = s.nextLine().toLowerCase();
			file = new File(fileName);
			exists = file.exists();
		}
		if(fileName.equals("exit")) {
			portfolio = this.initialCashDeposit();
		} else {
			try {
				readPortfolio = readFile.readpositionCSV(fileName);
				portfolio = new Portfolio(readPortfolio);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return portfolio;
	}
	
	/**
	 * local helper method to make sure individual is entering in a valid file name
	 * that is able to be printed at the end of the trading session
	 * @return
	 */
	private String getValidFileName(String fileName) {
			boolean goodFile = false;
		    while (!goodFile) {
		    	try {
		    		fileName = s.nextLine();
		    		File f = new File(fileName);
		    		f.getCanonicalPath();
		    		goodFile = true;
		    	}
		    	catch (IOException e) {
		    		System.out.println("You have entered an invalid file name. Please enter a valid file name.");
		    	}
		    }
		    return fileName;
		  }
		
	/**
	 * local helper method for error handling when buying and selling shares of stock
	 * @return
	 */
	private int getValidInt() {
		int shares = -1;
		boolean validShares = false;
		while (!validShares || shares <= 0) {
			try {
				shares = s.nextInt();
				s.nextLine();
				if (shares <= 0) {
					System.out.println("Invalid shares. Please input an integer greater than 0.");
				}
				else {
					validShares = true;
				}
			} catch (InputMismatchException e) {
				validShares = false;
				System.out.println("InputMismatchException. Please input a valid positive integer.");
				s.nextLine();
				// e.printStackTrace();
			}
		}
		return shares;
	}
	
	/**
	 * Handles errors and invalid input for parameter selection.
	 * only used locally as a helper method
	 * @param lowOption
	 * @param highOption
	 * @return
	 */
	private int getValidInt(int lowOption, int highOption) {
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
	 * Handles errors and invalid input when dealing with cash
	 * only used locally as a helper method
	 * @param lowOption
	 * @param highOption
	 * @return
	 */
	private double getValidDouble(double lowOption, double highOption) {
		double option = 0;
		while (option < lowOption || option > highOption) {
			try {
				option = s.nextDouble();
				if (option < lowOption || option > highOption) {
					System.out.println("Invalid selection. Please enter a valid double greater than 0.");
					s.nextLine();					
				}
			} catch (InputMismatchException e) {
				System.out.println("InputMismatchException. Please input a double greater than 0.");
				s.nextLine();
			}  
		}
		return option;
	}
	
	/**
	 * creates an initial cash portfolio if the individual doesn't have a file to read in
	 * @param amount
	 */
	private Portfolio initialCashDeposit() {
		// first asks the user to enter in a filename and stores the value 
		// so that we can properly print it out later
		System.out.println("What would you like to name your output file?");
		fileName = s.nextLine();
		this.getValidFileName(fileName);
		
		System.out.println("How much would you like to deposit?");
		amount = this.getValidDouble(0.01, Double.MAX_VALUE);
		// create  portfolio object made of cash
		Position cash = new Position("USDCASH", amount, 1);
		HashMap<String, Position> newPortfolio = new HashMap<String, Position>();
		newPortfolio.put("USDCASH", cash);
		Portfolio portfolio = new Portfolio(newPortfolio);
		return portfolio;
	}
	
	
	/**
	 * local helper method to continue to get quotes if the individual has no initial file
	 * to load in and then to deposit cash to establish a portfolio object
	 * @return
	 */
	private void getStockQuote() {
		System.out.println("Please enter the symbol of the stock you would like a quote on.");
		s.nextLine();
		stockSymbol = s.nextLine().toUpperCase();
		try {
			quote.returnStockQuote(this.getValidSymbol(stockSymbol));
		} catch (IllegalStateException e) {
			System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
		} catch (IOException e) {
			System.out.println("The stock symbol entered does not exist.  Please enter a new stock symbol");
		}
	}
	
	/**
	 * Local helper method to be able to loop through the switch statement
	 * and get through all options in an efficient manner
	 * provides the individual with 6 options: buy stock, sell stock, get a stock quote,
	 * deposit cash, withdraw cash, or exit the trading session
	 */
	private void options(Portfolio portfolio) {
		//Scanner s = new Scanner(System.in);
		
		while (userSelection != 6) {	
			System.out.println("What would you like to do next?  Please choose and enter a number from the following options");
			System.out.println("   1. buy stock");
			System.out.println("   2. sell stock");
			System.out.println("   3. get a stock quote");
			System.out.println("   4. deposit cash");
			System.out.println("   5. withdraw cash");
			System.out.println("   6. exit trading session");
			userSelection = this.getValidInt(1, 6);
			switch (userSelection) {
				// case 1 is option one, buy stock, from the given choices
				case 1:
					portfolio = this.optionsOneAndTwo(portfolio, 1);
					break;
				// case 2 is option 2, sell stock, from the given choices
				case 2:
					portfolio = this.optionsOneAndTwo(portfolio, 2);
					break;
				// case 3 is option 3, get a stock quote, from the given choices
				// allows individual to get a single stock quote and re-enter the option method
				case 3:
					this.getStockQuote();
					break;
				// case 4 is option 4, deposit cash, from the given choices
				case 4:
					portfolio = this.optionsFourAndFive(portfolio, 4);
					break;
				// case 5 is choice 5, withdraw cash, from the given choices
				case 5:
					portfolio = this.optionsFourAndFive(portfolio, 5);
					break;
			}
		}
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
		userSelection = this.getValidInt(1, 2);
		Portfolio portfolio = null;
		
			switch (userSelection) {
				case 1:				
					// option to read in a file if the individual already has a portfolio they would like to upload
					System.out.println("Please enter your file path and/or name. If you selected 1 erroneaously please type \"exit\" to continue without loading a file.");
					s.nextLine();
					fileName = s.nextLine().toLowerCase();
					
					if (!fileName.toUpperCase().contentEquals("EXIT")) {
						portfolio = this.getValidFile(fileName);
						portfolio.updatePortfolio();
						this.options(portfolio);
						break;
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
					userSelection = this.getValidInt(1, 2);
					switch (userSelection) {
						// this option allows the individual to put cash into an empty portfolio
						// so that they can transact as they wish through the program
						case 1:
							portfolio = this.initialCashDeposit();
							portfolio.updatePortfolio();
							this.options(portfolio);
							break;
						case 2:
							/**
							 * this option allows the individual to get a stock quote at first
							 * and then continue to get quotes or deposit cash to establish a portfolio
							 */
							while (userSelection !=1) {
								this.getStockQuote();
								System.out.println("Please choose and enter a number from the following:");
								System.out.println("   1. deposit cash");
								System.out.println("   2. get a stock quote");
								userSelection = this.getValidInt(1, 2);
							}
							portfolio = this.initialCashDeposit();
							portfolio.updatePortfolio();
							this.options(portfolio);
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
			System.out.println("Could not print your file.");
		}
		s.close();	
		
	}
}