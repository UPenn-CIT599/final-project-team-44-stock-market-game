import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class makes an API call to https://query1.finance.yahoo.com/v10/finance/ and returns the last price for
 * the desired symbol.
 */
public class YahooQuote {
	
	//Using two different end points for this class. The endpoint and module below are for chart data..
	//example url to view the data: https://query1.finance.yahoo.com/v8/finance/chart/AAPL?range=0d&interval=1d
	String chartEndPoint = "https://query1.finance.yahoo.com/v8/finance/chart/";
	String chartModule = "?range=0d&interval=1d";
	
	
	
	//Using two different end points for this class. The endpoint and module below are for chart quote data.
	//example url to view the data: https://query1.finance.yahoo.com/v10/finance/quoteSummary/AAPL?modules=price%2CDefaultKeyStatistics
	String quoteEndPoint = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/";
	String quoteModule = "?modules=price%2CDefaultKeyStatistics";
	
	/**
	 * Checks to confirm the symbol is traded, is an Equity or ETF and the currency is USD.
	 * @param json
	 * @return true if the symbol is traded, is an Equity or ETF and has a currency of USD.
	 * Otherwise, returns false.
	 * @throws IllegalStateException
	 */
	public boolean isValidSymbol(String symbol) throws IllegalStateException, IOException {
		String data = this.getData(symbol, "chart");
		
		//call the getField method to gather volume, currency and instrumentType data point. These
		this.getField("volume\":\\[(.+?),", data);
		String currency = this.getField("\"currency\":\"(.+?)\"", data);
		String instrumentType = this.getField("instrumentType\":\"(.+?)\"", data);
		
		
		//Validating that the symbol is valid.
		if ((instrumentType.equals("EQUITY") || instrumentType.equals("ETF")) && (currency.equals("USD"))) {
			return true;
		}
		System.out.println("Quote Type: " + instrumentType);
		System.out.println("Currency: " + currency);
		System.out.println("Quote Type must be ETF or EQUITY and currency must be USD. Please input a symbol that satisfies these requirements.");
		return false;
	}
	
	/**
	 * Takes in a symbol and gets the data from the URL endpoint. This string is passed to other 
	 * methods in the class.
	 * @param symbol, dataType. dataType determines which URL is hit. "chart" will return data from the
	 * chart end point, anything else will return data from the quote end point.
	 * @return data from the URL.
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public String getData(String symbol, String dataType) throws IOException, IllegalStateException {
		String stockSymbol = symbol.toUpperCase();
		String url;
		if (dataType.toLowerCase().equals("chart")) {
			url = chartEndPoint + stockSymbol + chartModule;	
		}
		else {
			url = quoteEndPoint + stockSymbol + quoteModule;
		}

		URL yfinance;
		URLConnection yc;
		BufferedReader in;

		//hit the URL and get the response from it.
		yfinance = new URL(url);
		yc = yfinance.openConnection();
		in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;

		StringBuffer response = new StringBuffer();
		//BufferedReader does not have a "hasNext" type method so this is how to check for 
		//more input
		//if it has more input append to the StringBuffer
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
				
		return response.toString();
	}
	
	public String getField(String symbol, String regex, String dataType) throws IllegalStateException, IOException {
		String data = this.getData(symbol, dataType);
		Pattern fieldPattern = Pattern.compile(regex);
		Matcher matcher1 = fieldPattern.matcher(data);
		matcher1.find();
		
		String fieldReturn = matcher1.group(1);
		return fieldReturn;
	}
	
	
	/**
	 * getField is overloaded as a second helper method for instances where you want to get multiple data
	 * points from the same endpoint url. This method should be used in conjunction with the getData method.
	 * The string from the getData method should be fed in as the data parameter and this method can be
	 * called to get each data point. This prevents the need to make multiple calls to the same url 
	 * end point when you need to get multiple data points e.g. returnStockQuote and isValidSymbol methods. 
	 * @param regex is the regex needed to get the data point you want to return
	 * @param data is the string returned from the getData method.
	 * @return
	 */
	public String getField(String regex, String data) {
		//use regex to find the regularMarketPrice from the returned response. This will then be used to find the 
		//latest market price.
		//Pattern pricePattern = Pattern.compile("regularMarketPrice\":(.+?),");
		Pattern fieldPattern = Pattern.compile(regex);
		Matcher matcher1 = fieldPattern.matcher(data);
		matcher1.find();

		String fieldReturn = matcher1.group(1);
		return fieldReturn;
	}
	
	
	/**
	 * This method prints a standard stock quote for the symbol the user inputs. It is assumed that the
	 * stock is valid. This method utilizes getData and getField methods in this class to get the needed
	 * data points and then displays standard quote information to the user.
	 * @param symbol is the symbol the user is requesting the quote for.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	
	public void returnStockQuote(String symbol) throws IllegalStateException, IOException {
		
		//gets the data from the url end point one time.
		String quoteData = this.getData(symbol.toUpperCase(), "quote");
		//formatting numbers for currency below.
		NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
		//simplifying the regex calls when using getField below to gather data points for this method.
		String regexPrefix = "\"";
		String regexSuffix = "\":\\{\"raw\":(.+?),";

		//leverages the getField method to gather all the required data points to print the quote
		//information for the symbol.
		String price = "Current Price: " + numberFormat.format(Double.parseDouble(this.getField(regexPrefix + "regularMarketPrice" + regexSuffix, quoteData)));
		String close = "Close: " + numberFormat.format(Double.parseDouble(this.getField(regexPrefix + "regularMarketPreviousClose" + regexSuffix, quoteData)));
		String open = "Open: " + numberFormat.format(Double.parseDouble(this.getField(regexPrefix + "regularMarketOpen" + regexSuffix, quoteData)));
		double high = Double.parseDouble(this.getField(regexPrefix + "regularMarketDayHigh" + regexSuffix, quoteData));
		double low = Double.parseDouble(this.getField(regexPrefix + "regularMarketDayLow" + regexSuffix, quoteData));
		String dayRange = "Today's Trading Range: " + numberFormat.format(low) + " - " + numberFormat.format(high);
		String name = this.getField("shortName\":\"(.+?)\"", quoteData);
		String yearChangeString = "52 Week Change: " + String.format("%.2f", Double.parseDouble(this.getField(regexPrefix + "52WeekChange" + regexSuffix, quoteData)) * 100) + "%";
		String beta = "Beta: " + String.format("%.2f", Double.parseDouble(this.getField(regexPrefix + "beta" + regexSuffix, quoteData)));
		String forwardPE = "Forward PE Ratio: " + String.format("%.2f", Double.parseDouble(this.getField(regexPrefix + "forwardPE" + regexSuffix, quoteData)));
		//			String forwardEPS = "Forward EPS: " + String.format("%.2f", Double.parseDouble(this.getField(regexPrefix + "forwardEps" + regexSuffix, quoteData)));

		//prints the standard quote information to display to the user.
		System.out.println(symbol.toUpperCase() + ": " + name);
		System.out.println();
		System.out.println(dayRange);
		System.out.println();
		System.out.println(String.format("%-25s", price) + "\t" + String.format("%-20s", yearChangeString));
		System.out.println(String.format("%-25s", open) + "\t" + String.format("%-20s", beta));
		System.out.println(String.format("%-25s", close) + "\t" + String.format("%-20s", forwardPE));
		
	}
	
	/**
	 * Prints standard index data for US indices to begin the trading session. 	
	 */
	public void indicesData() {
		//create a hashmap to map the yahoo symbols to the index names.
		HashMap<String, String> indices = new HashMap<String, String>();
		indices.put("^dji", "Dow");
		indices.put("^gspc", "S&P 500");
		indices.put("^ixic", "Nasdaq");
		
		//formatting the numbers 
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		
		//Loop to print the data points for each index (current price, change from yesterday's close.
		for (String index : indices.keySet()) {
			try {
				String data = this.getData(index, "chart");
				double price = Double.parseDouble(this.getField("regularMarketPrice\":(.+?),", data));
				String priceString = numberFormat.format(price);
				double prevClose = Double.parseDouble(this.getField("chartPreviousClose\":(.+?),", data));
				String pctChangeString = String.format("%.2f", ((price / prevClose) - 1) * 100) + "%";
				System.out.println("The " + indices.get(index) + " is currently trading at " + priceString + ", a " +
						pctChangeString + " difference from yeseterday's close.");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
}
