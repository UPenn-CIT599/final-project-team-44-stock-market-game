import java.io.File;
import java.util.*;
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
	 * @param fileName
	 * @return
	 * return HashMap of positions that are read in from the user's portfolio
	 * using the symbol as the key
	 */
	//this is a duplicate of the above, just using a different object
	//seeing which one will be best in the overall program and will 
	//delete the non-used one upon final turn-in
	public HashMap<String, Position> readpositionCSV(String fileName) throws FileNotFoundException {
		HashMap <String, Position> userPortfolio = new HashMap <String, Position>();
		File positionsFile = new File(fileName);
		int numColumns = 7;
		boolean goodRow = true;
		int rowNum = 0;
		YahooQuote yahooIsAlive = new YahooQuote();
		//overall try catch to read in the elements on the .csv file
		//try {
			Scanner fileReader = new Scanner(positionsFile);
			fileReader.nextLine();//skip title row
			while (fileReader.hasNextLine()) {
				rowNum++;
				String line = fileReader.nextLine();
				String[] lineComponents = line.split(",");
				//check for empty cells
				for (int i=0; i < numColumns; i++) {
					String s = lineComponents[i];
					if (s.compareTo("")==0){
						goodRow = false;
						System.out.println("Blank entry in row " + rowNum + ". This row was ignored.");
					}					
				}
				try {
					if (lineComponents[0].compareTo("USDCASH")!=0) {
						if (!yahooIsAlive.isValidSymbol(lineComponents[0])) {
							goodRow = false;
							System.out.println("Wrong Stock Symbol in row " + rowNum + ". This row was ignored.");
						}}
						} catch (IOException e) {
							goodRow = false;
							System.out.println("Wrong Stock Symbol in row " + rowNum + ". This row was ignored.");
						}
					
				if (goodRow) {
					
					String symbol = lineComponents[0];
					double shares = 0;
					double avgCost = 0;			
					
					try {
						shares = Double.parseDouble(lineComponents[1]);
					} catch (NumberFormatException e) {
						goodRow = false;
						System.out.println("Shares is entered in the wrong format in row " + rowNum + ". This row was ignored.");
					}
					try {
						avgCost = Double.parseDouble(lineComponents[2]);
					} catch (NumberFormatException e) {
						goodRow = false;
						System.out.println("Average Cost is entered in the wrong format in row " + rowNum + ". This row was ignored.");
					}
					
					if (goodRow) {
						Position p = new Position(symbol, shares, avgCost);
						userPortfolio.put(symbol, p);
					}
				}
				goodRow=true;
			}
			fileReader.close();
		//} catch (FileNotFoundException e) {e.printStackTrace();}
		return userPortfolio;
	}
	/**
	 *method to write data to a file 
	 *@param fileName: name of file to write new positions to the positions file
	 *@param portfolio: contains the portfolio information that will be written to the file
	 */
	public void writePositionCSV (String fileName, Portfolio port) throws FileNotFoundException {
		File out = new File(fileName);
		PrintWriter pw = new PrintWriter(out);
			pw.println("Symbol" + "," + "Shares" + "," + "AverageCost" + "," + "LastPrice" + "," + "CostBasis" + "," + "CurrentValue" + "," + "Return");
			//pw.flush();
		//the following code tries to convert to dollar format where needed
			for (String symbol : port.portfolio.keySet()) {
				//if we ever wanted this--will probably delete when finalized
				/*NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(Locale.US);
		        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
				String sharesOutput = numberFormat.format(port.portfolio.get(symbol).getShares());
		        String avgCostOutput = dollarFormat.format(port.portfolio.get(symbol).getAverageCost());
		        String lastPriceOutput = dollarFormat.format(port.portfolio.get(symbol).getLastPrice());
		        String costBasisOutput = dollarFormat.format(port.portfolio.get(symbol).getCostBasis());
		        String currentValueOutput = dollarFormat.format(port.portfolio.get(symbol).getCurrentValue());
		        String returnValueOutput = Double.toString(port.portfolio.get(symbol).getPositionReturn());*/
				String symbolOutput = port.portfolio.get(symbol).getSymbol();
				String sharesOutput = Double.toString(port.portfolio.get(symbol).getShares());
				String avgCostOutput = Double.toString(port.portfolio.get(symbol).getAverageCost());
				String lastPriceOutput = Double.toString(port.portfolio.get(symbol).getLastPrice());
				String costBasisOutput = Double.toString(port.portfolio.get(symbol).getCostBasis());
				String currentValueOutput = Double.toString(port.portfolio.get(symbol).getCurrentValue());
				String returnValueOutput = Double.toString(port.portfolio.get(symbol).getPositionReturn());
		        //"\"" + currency + "\" ,"
				// Prints the position to the file. Need to format it in a way that we can print commas to a cell in a .csv file.
				pw.println(symbolOutput + "," + sharesOutput + "," + avgCostOutput + "," + lastPriceOutput + "," + costBasisOutput + "," + currentValueOutput + "," + returnValueOutput);
				pw.flush();
			}	
			pw.close();
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


}