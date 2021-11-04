package xrate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.json.*;

/**
 * Provide access to basic currency exchange rate services.
 */
public class ExchangeRateReader {

    private String accessKey;
    private String baseURL;

    /**
     * Construct an exchange rate reader using the given base URL. All requests will
     * then be relative to that URL. If, for example, your source is Xavier Finance,
     * the base URL is http://api.finance.xaviermedia.com/api/ Rates for specific
     * days will be constructed from that URL by appending the year, month, and day;
     * the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL the base URL for requests
     */
    public ExchangeRateReader(String baseURL) {
    	
    	this.baseURL = baseURL;

        // Reads the Fixer.io API access key from the appropriate
        // environment variable.
        readAccessKey();
    }

    /**
     * This reads the `fixer_io` access key from from the system environment and
     * assigns it to the field `accessKey`.
     * 
     * You don't have to change anything here.
     */
    private void readAccessKey() {
        // Read the desired environment variable.
        accessKey = System.getenv("FIXER_IO_ACCESS_KEY");
        // If that environment variable isn't defined, then
        // `getenv()` returns `null`. We'll throw a (custom)
        // exception if that happens since the program can't
        // really run if we don't have an access key.
        if (accessKey == null) {
            throw new MissingAccessKeyException();
        }
    }

    /**
     * Get the exchange rate for the specified currency against the base currency
     * (the Euro) on the specified date.
     * 
     * @param currencyCode the currency code for the desired currency
     * @param year         the year as a four digit integer
     * @param month        the month as an integer (1=Jan, 12=Dec)
     * @param day          the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException if there are problems reading from the server
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day) throws IOException {
    	return getExchangeRate(currencyCode, "EUR", year, month, day);
    }

    /**
     * Get the exchange rate of the first specified currency against the second on
     * the specified date.
     * 
     * @param fromCurrency the currency code we're exchanging *from*
     * @param toCurrency   the currency code we're exchanging *to*
     * @param year         the year as a four digit integer
     * @param month        the month as an integer (1=Jan, 12=Dec)
     * @param day          the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException if there are problems reading from the server
     */
    public float getExchangeRate(String fromCurrency, String toCurrency, int year, int month, int day)
            throws IOException {
    	
    	String fullURL = String.format(
    			"%s/%04d-%02d-%02d?access_key=%s&symbols=%s,%s",
    			baseURL,
    			year,
    			month,
    			day,
    			URLEncoder.encode(accessKey, "UTF-8"),
    			URLEncoder.encode(fromCurrency, "UTF-8"),
    			URLEncoder.encode(toCurrency, "UTF-8"));
    	URL url = new URL(fullURL);
    	InputStream inputStream = url.openStream();
    	
    	JSONObject json = new JSONObject(new JSONTokener(inputStream));
    	
    	return json.getJSONObject("rates").getFloat(fromCurrency) / json.getJSONObject("rates").getFloat(toCurrency);
    }
}