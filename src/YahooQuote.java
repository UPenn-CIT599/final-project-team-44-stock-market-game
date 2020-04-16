import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

/**
 * This class makes an API call to https://query1.finance.yahoo.com/v10/finance/ and returns the last price for
 * the desired symbol.
 */
public class YahooQuote {
	
	//String endPoint = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/";
	//String module = "?modules=price";

	String endPoint = "https://query1.finance.yahoo.com/v8/finance/chart/";
	String module = "?range=0d&interval=1d";
	
	//new endpoint to test: https://query1.finance.yahoo.com/v8/finance/chart/itot?range=0d&interval=1d
	
	/**
	 * Makes the API call and gets the JSON result as a String. Then, uses regex to find the lastPrice 
	 * of the stock.
	 * @param symbol
	 * @return stock's last price
	 * @throws IOException
	 */
	/*
	public double getLastPrice(String symbol) throws IOException {
		
		String stockSymbol = symbol.toUpperCase();
		String url = endPoint + stockSymbol + module;
		
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
		
		//use regex to find the regularMarketPrice from the returned response. This will then be used to find the 
		//latest market price.
		Pattern regMarketPrice = Pattern.compile("regularMarketPrice(.+?),");
		Matcher matcher1 = regMarketPrice.matcher(response);
		matcher1.find();
		String matchedExpression = matcher1.group();
		
		//uses the matchedExpression to filter down further to extract the stock's last price.
		Pattern pattern2 = Pattern.compile("(?<=\\{\"raw\":)(.*?)(?=\\,)");
		Matcher matcher2 = pattern2.matcher(matchedExpression);
		
		matcher2.find();
		String price = matcher2.group();
		
		double lastPrice = Double.parseDouble(price);

		return lastPrice;
		
	}
	*/
	
	/**
	 * Checks to confirm the symbol is traded, is an Equity or ETF and the currency is USD.
	 * @param json
	 * @return true if the symbol is traded, is an Equity or ETF and has a currency of USD.
	 * Otherwise, returns false.
	 * @throws IllegalStateException
	 */
	public boolean isValidSymbol(String symbol) throws IllegalStateException, IOException {
		String json = this.getJSON(symbol);
		
		//Uses regex to find the volume for the symbol. If there is no volume, 
		//the symbol either does not exist or no longer trades and throws IllegalStateException.
		Pattern volumePattern = Pattern.compile("volume\":\\[(.+?),");
		Matcher matcher3 = volumePattern.matcher(json);
		matcher3.find();
		
		//Uses regex to find the instrumentType. 
		//valid types are "ETF" and "EQUITY"
		Pattern instrumentTypePattern = Pattern.compile("instrumentType\":\"(.+?)\"");
		Matcher matcher1 = instrumentTypePattern.matcher(json);
		matcher1.find();
		String instrumentType = matcher1.group(1);
		
		//Uses regex to find the currency.
		//USD is the only valid currency fat this time.
		Pattern currencyPattern = Pattern.compile("\"currency\":\"(.+?)\"");
		Matcher matcher2 = currencyPattern.matcher(json);
		matcher2.find();
		String currency = matcher2.group(1);
		
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
	 * @param symbol
	 * @return data from the URL.
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public String getJSON(String symbol) throws IOException, IllegalStateException {
		String stockSymbol = symbol.toUpperCase();
		String url = endPoint + stockSymbol + module;

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
	
	/**
	 * returns the last price 
	 * @param json
	 * @return the last price of the stock.
	 * @throws IllegalStateException
	 */
	public double getLastPrice(String symbol) throws IllegalStateException, IOException {
		
		String json = this.getJSON(symbol);
		//use regex to find the regularMarketPrice from the returned response. This will then be used to find the 
		//latest market price.
		Pattern pricePattern = Pattern.compile("regularMarketPrice\":(.+?),");
		Matcher matcher1 = pricePattern.matcher(json);
		matcher1.find();
		double price = Double.parseDouble(matcher1.group(1));
		
		//double lastPrice = Double.parseDouble(price);

		return price;
	}
	
	public String getField(String symbol, String field) throws IllegalStateException, IOException {
		String json = this.getJSON(symbol);
		//use regex to find the regularMarketPrice from the returned response. This will then be used to find the 
		//latest market price.
		//Pattern pricePattern = Pattern.compile("regularMarketPrice\":(.+?),");
		Pattern fieldPattern = Pattern.compile(field + "\":(.+?),");
		Matcher matcher1 = fieldPattern.matcher(json);
		matcher1.find();
		
		String fieldReturn = matcher1.group(1);
		return fieldReturn;
	}
	
	public void indicesData() {
		HashMap<String, String> indices = new HashMap<String, String>();
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		indices.put("^dji", "Dow");
		indices.put("^gspc", "S&P 500");
		indices.put("^ixic", "Nasdaq");
		
		for (String index : indices.keySet()) {
			try {
				double price = Double.parseDouble(this.getField(index, "regularMarketPrice"));
				String priceString = numberFormat.format(price);
				double prevClose = Double.parseDouble(this.getField(index, "chartPreviousClose"));
				double pctChange = ((price / prevClose) - 1) * 100;
				String pctChangeString = String.format("%.2f", pctChange) + "%";
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
	
	/**
	 * tester main to make sure the data is being read in correctly and usable
	 * throughout the rest of the program
	 * @param args
	 */
	public static void main(String[] args) {
		YahooQuote quote = new YahooQuote();
		Scanner in = new Scanner(System.in);
		/*
		System.out.println("please put in a symbol");
		String symbol = in.next().toUpperCase();
		
		boolean validSymbol = false;
		while (validSymbol == false) {
			try {
				validSymbol = quote.isValidSymbol(symbol);
				System.out.println(validSymbol);
				if (validSymbol) {
					System.out.println(quote.getLastPrice(symbol));
				}
				else {
					symbol = in.next();
				}
			} catch (IOException e) {
				System.out.println("IOException. The symbol you input does not exist. Please input a valid symbol.");
				e.printStackTrace();
				symbol = in.next();
			} catch (IllegalStateException e) {
				System.out.println("IllegalStateException. The symbol you input does not trade. Please input a valid symbol.");
				symbol = in.next();
			}
		}

		*/

		quote.indicesData();
		in.close();
	}
}
