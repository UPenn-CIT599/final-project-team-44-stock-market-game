
public class Main {
	
	/**
	 * Prints out the portfolio in a formatted way
	 * for easy reading by the individual
	 * @param args
	 */
	public static void main(String[] args) {
		PositionFileIO posFile = new PositionFileIO();
		Portfolio port = new Portfolio(posFile.readpositionCSV("DummyStockPortfolio.csv"));
		
		System.out.println(String.format("%-10s","SYMBOL") 
				+ "\t" + String.format("%-10s", "SHARES") 
				+ "\t" + String.format("%-10s", "AVG COST") 
				+ "\t" + String.format("%-10s", "LAST PRICE") 
				+ "\t" + String.format("%-15s","COST BASIS") 
				+ "\t" + String.format("%-15s", "CURR VALUE") 
				+ "\t" + String.format("%-10s", "RETURN (%)"));
		for (String symbol : port.portfolio.keySet()) {
			System.out.println(port.portfolio.get(symbol));
		}
		
		//System.out.println(portfolio.get(0).getSymbol());
		
	}

}
