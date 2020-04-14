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
	
	//instance variables for Trade class
	private String stockSymbol = null;
	private double amount = 0.0;
	PositionFileIO file = new PositionFileIO();
	Portfolio portfolio = new Portfolio();
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
						//ask the user for a stock symbol and check to make sure it is valid
						//then gives the user a quote with the available cash in their portfolio
						System.out.println("Please enter the stock symbol you would like to buy.");
						stockSymbol = optionScanner.next();
						quote.isValidSymbol(stockSymbol); 
						System.out.println(stockSymbol + " is currently trading at $" + tradePrice);
						
						//need to check current portfolio for cash position
						System.out.println("You currently have " + "" + " available to trade");
						
						//ask the user how many shares they would like to buy and check to make sure they have enough cash
						System.out.println("How many shares would you like to buy?");
						int shares = optionScanner.nextInt();
						portfolio.hasSufficientShares("USDCASH", shares);
						
						
						
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
				case 3:
					
					break;
				case 4:
					
					break;
				case 5:
					
					break;
			}
		}
		optionScanner.close();
	}
	
	/**
	 * single method to run the trading session for the game
	 */
	public void runTradingSession() {
		//Into explaining the game and setting rules
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
		
		//instance variable to store values 
		Scanner s = new Scanner(System.in);
		int userSelection = s.nextInt();
		String fileName = null;
		
		switch (userSelection) {
			case 1:
				try {
					System.out.println("Please enter your file path and/or name.");
					fileName = s.next();
					//We need to talk about this in the next meeting and how to resolve
					portfolio = file.readpositionCSV(fileName);
					//make sure this makes sense after helper method is established
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
				//need exception handling here
				userSelection = s.nextInt();
				switch (userSelection) {
					case 1:
						/**
						 * this option deposits cash so we enter into a buy order
						 * where the individual is buying 1 share of cash per dollar valued at
						 * 1 dollar per share, then enters the options helper method
						 */
						System.out.println("How much would you like to deposit?");
						//need error handling here
						amount = s.nextDouble();
						portfolio.updateCash(amount); //updates the cash in the new portfolio to what the user input
						//after updating the cash it enters into the helper options method
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
							System.out.println(stockSymbol + "is currently trading at $" +quote.getLastPrice(stockSymbol));
							
							//after getting the first quote it enters into the options helper method
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
				//make sure this makes sense after helper method is established
				this.options();
				break;
		}
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
