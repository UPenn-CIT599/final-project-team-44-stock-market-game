import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class YahooQuoteTest {
	
	YahooQuote quote = new YahooQuote();

	//tests the validSymbol method for true and false returns
	@Test 
	void validSymbol() throws IllegalStateException, IOException {
		assertFalse(quote.isValidSymbol("vod.l"));
		assertTrue(quote.isValidSymbol("aapl"));
		assertTrue(quote.isValidSymbol("ITOT")); //tests for ETF instrument type
		assertFalse(quote.isValidSymbol("^dji")); //tests for non etf or equity
	}
	
	//test that IllegalStateExcpetion (i.e. the symbol no longer trades) is thrown by isValidSymbol. 
	@Test
	public void validSymbolTickerNoLongerTradesException() {
		Exception exception = assertThrows(IllegalStateException.class, () -> {
	        quote.isValidSymbol("mdco");
	    });
		
		String expectedMessgae = "IllegalStateException";
		String actualMessage = exception.toString();
		
		assertTrue(actualMessage.contains(expectedMessgae));
	}
	
	//test for IllegalStateExcpetion (i.e. the symbol no longer trades) is thrown by isValidSymbol. 
	@Test
	public void validSymbolTickerDoesntExistException() {
		Exception exception = assertThrows(IOException.class, () -> {
	        quote.isValidSymbol("faketicker");
	    });
		
		String expectedMessgae = "FileNotFoundException";
		String actualMessage = exception.toString();
		
		assertTrue(actualMessage.contains(expectedMessgae));
	}
	
	//Test pulling a field from the quote url
	@Test
	public void getFieldTest() throws IllegalStateException, IOException {
		String currency = quote.getField("AAPL", "currency\":\"(.+?)\"", "quote");
		assertEquals("USD", currency);
	}
	
	//run to confirm returnStockQuote adn indicesData run properly. Void methods so just testing that they don't fail on attempt.
	@Test
	public void stockQuoteTest() throws IllegalStateException, IOException {
		quote.returnStockQuote("aapl");
		quote.indicesData();
	}
	
}