import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		PositionFileIO posFile = new PositionFileIO();
		ArrayList<Position> portfolio = new ArrayList<Position>(); 
		portfolio = posFile.readpositionCSV("DummyStockPortfolio.csv");
		
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
		
		//System.out.println(portfolio.get(0).getSymbol());
		
	}

}
