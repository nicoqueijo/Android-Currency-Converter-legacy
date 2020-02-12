package com.nicoqueijo.android.currencyconverter.java.models;

/**
 * Model class for the API endpoint used to retrieve the exchange rates. Needs to have the same
 * fields as the endpoint JSON object in order to map it using GSON.
 */
public class ApiEndpoint {

    public static final String TAG = ApiEndpoint.class.getSimpleName();

    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private ExchangeRates rates;

    /**
     * This constructor is never actually used since objects of this type are created automatically
     * using GSON. Have included it just for completion.
     *
     * @param disclaimer a link to the service provider's disclaimer.
     * @param license    a link to the service provider's license.
     * @param timestamp  the exact date and time (UNIX) the exchange rates were collected.
     * @param base       the currency to which all exchange rates are based to. (default: USD)
     * @param rates      contains all exchange rate values, consisting of the currency pairs and
     *                   their respective conversion rates.
     */
    public ApiEndpoint(String disclaimer, String license, long timestamp, String base,
                       ExchangeRates rates) {
        this.disclaimer = disclaimer;
        this.license = license;
        this.timestamp = timestamp;
        this.base = base;
        this.rates = rates;
    }

    // Getters and setters are defined below.
    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public ExchangeRates getRates() {
        return rates;
    }

    public void setRates(ExchangeRates rates) {
        this.rates = rates;
    }
}
