import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class makes an API call to https://query1.finance.yahoo.com/v10/finance/ and returns the last price for
 * the desired symbol.
 */
public class YahooQuote {
	
	String endPoint = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/";
	String module = "?modules=price";

	/**
	 * Makes the API call and gets the JSON result as a String. Then, uses regex to find the lastPrice 
	 * of the stock.
	 * @param symbol
	 * @return stock's last price
	 * @throws IOException
	 */
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
	
	public static void main(String[] args) {
		YahooQuote quote = new YahooQuote();
		try {
			System.out.println(quote.getLastPrice("aapl"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
