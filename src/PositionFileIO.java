import java.io.File;
import java.util.*;
//import javax.swing.text.Position;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Reads in data from the positions csv file
 * Creates a new file if one does not exist
 * Writes data to the file
 * 
 */
public class PositionFileIO {
	/**
	 * Reads in position data from a file for a given user's portfolio
	 * @param positionsFile: name of file to read positions from
	 * return array list of positions that are read in from the user's portfolio
	 */
	public ArrayList<Position> readpositionCSV(String fileName) {
	ArrayList <Position> positions = new ArrayList<Position>();
	File positionsFile = new File(fileName);
	YahooQuote getPrice = new YahooQuote();
	//overall try catch to read in the elements on the .csv file
	try {
		Scanner fileReader = new Scanner(positionsFile);
		fileReader.nextLine(); // skip title row
		while (fileReader.hasNextLine()) {
			String line = fileReader.nextLine();
			String[] lineComponents = line.split(",");
			
			//System.out.println(Arrays.toString(lineComponents));
			
			String symbol = lineComponents[0];
			double shares = 0;
			double avgCost = 1.0;
			double lastPrice = 1.0;
			
			try {
				shares = Double.parseDouble(lineComponents[1]);
			} catch (NumberFormatException e) {System.out.println("Wrong format");}
			try {
				avgCost = Double.parseDouble(lineComponents[2]);
			} catch (NumberFormatException e) {System.out.println("Wrong format");}

			Position position = new Position(symbol, shares, avgCost);
							
			positions.add(position);
		}
		fileReader.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace(); 
	}
	return positions;
}

	/**
	 * Reads in position data from a file for a given user's portfolio
	 * @param fileName
	 * @return
	 * return HashMap of positions that are read in from the user's portfolio
	 * using the symbol as the key
	 */
	//this is a duplicate of the above, just using a different object
	//seeing which one will be best in the overall program and will 
	//delete the non-used one upon final turn-in
	public HashMap<String, Double[]> readpositionCSVHM(String fileName) {
		HashMap <String, Double[]> userPortfolio = new HashMap <String, Double[]>();
		File positionsFile = new File(fileName);
		YahooQuote getPrice = new YahooQuote();
		//overall try catch to read in the elements on the .csv file
		try {
			Scanner fileReader = new Scanner(positionsFile);
			fileReader.nextLine();//skip title row
			while (fileReader.hasNextLine()) {
				String line = fileReader.nextLine();
				String[] lineComponents = line.split(",");
				
				String symbol = lineComponents[0];
				double shares = 0;
				double avgCost = 1.0;
				double lastPrice = 1.0;
				
				
				try {
					shares = Double.parseDouble(lineComponents[1]);
				} catch (NumberFormatException e) {System.out.println("Wrong format");}
				try {
					avgCost = Double.parseDouble(lineComponents[2]);
				} catch (NumberFormatException e) {System.out.println("Wrong format");}
				
				Double[] positionValues = {shares, avgCost};
				userPortfolio.put(symbol, positionValues);
			}
			fileReader.close();
		} catch (FileNotFoundException e) {e.printStackTrace();}
		return userPortfolio;
	}
	/**
	 *method to write data to a file 
	 *@param fileName: name of file to write new positions to the positions file
	 *@param portfolio: contains the portfolio information that will be written to the file
	 */
	public void writePositionCSV (String fileName, Portfolio portfolio) {
		File out = new File(fileName);
		try (PrintWriter pw = new PrintWriter(out)) {
			pw.println("Symbol" + "," + "Shares" + "," + "AverageCost" + "," + "LastPrice" + "," + "CostBasis" + "," + "CurrentValue" + "," + "Return");
		
			for (Position position : portfolio) {
				String symbolOutput = position.getSymbol();
				String sharesOutput = Double.toString(position.getShares());
				String avgCostOutput = Double.toString(position.getAverageCost());
				String lastPriceOutput = Double.toString(position.getLastPrice());
				String costBasisOutput = Double.toString(position.getCostBasis());
				String currentValueOutput = Double.toString(position.getCurrentValue());
				String returnValueOutput = Double.toString(position.getPositionReturn()); 
		
				// Prints the position to the file
				pw.println(symbolOutput + "," + sharesOutput + "," + avgCostOutput + "," + lastPriceOutput + "," + costBasisOutput + "," + currentValueOutput + "," + returnValueOutput);
			}		
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not write the File out.");
		}
	}
	
	
	/**
	 * method to create a file name
	 */
	public static String createFileName() {
		String fileName = null;
		//method to create the filename. We can update the naming structure as needed.
		fileName = "StockPorfolio.csv";
		return fileName;
	}
	
	/**
	 * tester main method to make sure reading file correctly
	 * @param args
	 */
	public static void main(String[] args) {
		PositionFileIO test = new PositionFileIO();
		ArrayList<Position> portfolio = test.readpositionCSV("DummyStockPortfolio.csv");
		System.out.println(portfolio);
	}
	
}
	
	
	
		

