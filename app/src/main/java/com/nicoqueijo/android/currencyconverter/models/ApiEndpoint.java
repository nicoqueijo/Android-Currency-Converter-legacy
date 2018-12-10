package com.nicoqueijo.android.currencyconverter.models;

public class ApiEndpoint {

    public static final String TAG = ApiEndpoint.class.getSimpleName();

    private boolean success;
    private String terms;
    private String privacy;
    private long timestamp;
    private String source;
    private ExchangeRates quotes;

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

    public ApiEndpoint(boolean success, String terms, String privacy, long timestamp, String source, ExchangeRates quotes) {
        this.success = success;
        this.terms = terms;
        this.privacy = privacy;
        this.timestamp = timestamp;
        this.source = source;
        this.quotes = quotes;
    }
}
