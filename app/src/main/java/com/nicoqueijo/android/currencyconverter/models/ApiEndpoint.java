package com.nicoqueijo.android.currencyconverter.models;

/**
 * Model class for the API endpoint used to retrieve the exchange rates. Needs to have the same
 * fields as the endpoint JSON object in order to map it using GSON.
 */
public class ApiEndpoint {

    public static final String TAG = ApiEndpoint.class.getSimpleName();

    private boolean success;
    private String terms;
    private String privacy;
    private long timestamp;
    private String source;
    private ExchangeRates quotes;

    /**
     * This constructor is never actually used since objects of this type are created automatically
     * using GSON. Have included it just for completion.
     *
     * @param success   true or false depending on whether or not your query succeeds.
     * @param terms     a link to the currencylayer Terms & Conditions.
     * @param privacy   a link to the currencylayer Privacy Policy.
     * @param timestamp the exact date and time (UNIX) the exchange rates were collected.
     * @param source    the currency to which all exchange rates are relative. (default: USD)
     * @param quotes    contains all exchange rate values, consisting of the currency pairs and
     *                  their respective conversion rates.
     */
    public ApiEndpoint(boolean success, String terms, String privacy, long timestamp, String source,
                       ExchangeRates quotes) {
        this.success = success;
        this.terms = terms;
        this.privacy = privacy;
        this.timestamp = timestamp;
        this.source = source;
        this.quotes = quotes;
    }

    // Getters and setters are defined below.
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ExchangeRates getQuotes() {
        return quotes;
    }

    public void setQuotes(ExchangeRates quotes) {
        this.quotes = quotes;
    }
}
