package com.nicoqueijo.android.currencyconverter.models;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Model class for the nested "rates" object in the API endpoint containing all the exchange rates.
 * Needs to have the same fields as the "rates" object in order to map it using GSON hence the
 * individual double fields.
 */
public class ExchangeRates {

    public static final String TAG = ExchangeRates.class.getSimpleName();

    private static final int AMOUNT_OF_CURRENCIES = 159;
    private List<Currency> currencies = Lists.newArrayListWithCapacity(AMOUNT_OF_CURRENCIES);

    @SerializedName("AED")
    private double USD_AED;
    @SerializedName("AFN")
    private double USD_AFN;
    @SerializedName("ALL")
    private double USD_ALL;
    @SerializedName("AMD")
    private double USD_AMD;
    @SerializedName("ANG")
    private double USD_ANG;
    @SerializedName("AOA")
    private double USD_AOA;
    @SerializedName("ARS")
    private double USD_ARS;
    @SerializedName("AUD")
    private double USD_AUD;
    @SerializedName("AWG")
    private double USD_AWG;
    @SerializedName("AZN")
    private double USD_AZN;
    @SerializedName("BAM")
    private double USD_BAM;
    @SerializedName("BBD")
    private double USD_BBD;
    @SerializedName("BDT")
    private double USD_BDT;
    @SerializedName("BGN")
    private double USD_BGN;
    @SerializedName("BHD")
    private double USD_BHD;
    @SerializedName("BIF")
    private double USD_BIF;
    @SerializedName("BMD")
    private double USD_BMD;
    @SerializedName("BND")
    private double USD_BND;
    @SerializedName("BOB")
    private double USD_BOB;
    @SerializedName("BRL")
    private double USD_BRL;
    @SerializedName("BSD")
    private double USD_BSD;
    @SerializedName("BTN")
    private double USD_BTN;
    @SerializedName("BWP")
    private double USD_BWP;
    @SerializedName("BYN")
    private double USD_BYN;
    @SerializedName("BZD")
    private double USD_BZD;
    @SerializedName("CAD")
    private double USD_CAD;
    @SerializedName("CDF")
    private double USD_CDF;
    @SerializedName("CHF")
    private double USD_CHF;
    @SerializedName("CLP")
    private double USD_CLP;
    @SerializedName("CNY")
    private double USD_CNY;
    @SerializedName("COP")
    private double USD_COP;
    @SerializedName("CRC")
    private double USD_CRC;
    @SerializedName("CUP")
    private double USD_CUP;
    @SerializedName("CVE")
    private double USD_CVE;
    @SerializedName("CZK")
    private double USD_CZK;
    @SerializedName("DJF")
    private double USD_DJF;
    @SerializedName("DKK")
    private double USD_DKK;
    @SerializedName("DOP")
    private double USD_DOP;
    @SerializedName("DZD")
    private double USD_DZD;
    @SerializedName("EGP")
    private double USD_EGP;
    @SerializedName("ERN")
    private double USD_ERN;
    @SerializedName("ETB")
    private double USD_ETB;
    @SerializedName("EUR")
    private double USD_EUR;
    @SerializedName("FJD")
    private double USD_FJD;
    @SerializedName("FKP")
    private double USD_FKP;
    @SerializedName("GBP")
    private double USD_GBP;
    @SerializedName("GEL")
    private double USD_GEL;
    @SerializedName("GGP")
    private double USD_GGP;
    @SerializedName("GHS")
    private double USD_GHS;
    @SerializedName("GIP")
    private double USD_GIP;
    @SerializedName("GMD")
    private double USD_GMD;
    @SerializedName("GNF")
    private double USD_GNF;
    @SerializedName("GTQ")
    private double USD_GTQ;
    @SerializedName("GYD")
    private double USD_GYD;
    @SerializedName("HKD")
    private double USD_HKD;
    @SerializedName("HNL")
    private double USD_HNL;
    @SerializedName("HRK")
    private double USD_HRK;
    @SerializedName("HTG")
    private double USD_HTG;
    @SerializedName("HUF")
    private double USD_HUF;
    @SerializedName("IDR")
    private double USD_IDR;
    @SerializedName("ILS")
    private double USD_ILS;
    @SerializedName("IMP")
    private double USD_IMP;
    @SerializedName("INR")
    private double USD_INR;
    @SerializedName("IQD")
    private double USD_IQD;
    @SerializedName("IRR")
    private double USD_IRR;
    @SerializedName("ISK")
    private double USD_ISK;
    @SerializedName("JEP")
    private double USD_JEP;
    @SerializedName("JMD")
    private double USD_JMD;
    @SerializedName("JOD")
    private double USD_JOD;
    @SerializedName("JPY")
    private double USD_JPY;
    @SerializedName("KES")
    private double USD_KES;
    @SerializedName("KGS")
    private double USD_KGS;
    @SerializedName("KHR")
    private double USD_KHR;
    @SerializedName("KMF")
    private double USD_KMF;
    @SerializedName("KPW")
    private double USD_KPW;
    @SerializedName("KRW")
    private double USD_KRW;
    @SerializedName("KWD")
    private double USD_KWD;
    @SerializedName("KYD")
    private double USD_KYD;
    @SerializedName("KZT")
    private double USD_KZT;
    @SerializedName("LAK")
    private double USD_LAK;
    @SerializedName("LBP")
    private double USD_LBP;
    @SerializedName("LKR")
    private double USD_LKR;
    @SerializedName("LRD")
    private double USD_LRD;
    @SerializedName("LSL")
    private double USD_LSL;
    @SerializedName("LYD")
    private double USD_LYD;
    @SerializedName("MAD")
    private double USD_MAD;
    @SerializedName("MDL")
    private double USD_MDL;
    @SerializedName("MGA")
    private double USD_MGA;
    @SerializedName("MKD")
    private double USD_MKD;
    @SerializedName("MMK")
    private double USD_MMK;
    @SerializedName("MNT")
    private double USD_MNT;
    @SerializedName("MOP")
    private double USD_MOP;
    @SerializedName("MRO")
    private double USD_MRO;
    @SerializedName("MUR")
    private double USD_MUR;
    @SerializedName("MVR")
    private double USD_MVR;
    @SerializedName("MWK")
    private double USD_MWK;
    @SerializedName("MXN")
    private double USD_MXN;
    @SerializedName("MYR")
    private double USD_MYR;
    @SerializedName("MZN")
    private double USD_MZN;
    @SerializedName("NAD")
    private double USD_NAD;
    @SerializedName("NGN")
    private double USD_NGN;
    @SerializedName("NIO")
    private double USD_NIO;
    @SerializedName("NOK")
    private double USD_NOK;
    @SerializedName("NPR")
    private double USD_NPR;
    @SerializedName("NZD")
    private double USD_NZD;
    @SerializedName("OMR")
    private double USD_OMR;
    @SerializedName("PAB")
    private double USD_PAB;
    @SerializedName("PEN")
    private double USD_PEN;
    @SerializedName("PGK")
    private double USD_PGK;
    @SerializedName("PHP")
    private double USD_PHP;
    @SerializedName("PKR")
    private double USD_PKR;
    @SerializedName("PLN")
    private double USD_PLN;
    @SerializedName("PYG")
    private double USD_PYG;
    @SerializedName("QAR")
    private double USD_QAR;
    @SerializedName("RON")
    private double USD_RON;
    @SerializedName("RSD")
    private double USD_RSD;
    @SerializedName("RUB")
    private double USD_RUB;
    @SerializedName("RWF")
    private double USD_RWF;
    @SerializedName("SAR")
    private double USD_SAR;
    @SerializedName("SBD")
    private double USD_SBD;
    @SerializedName("SCR")
    private double USD_SCR;
    @SerializedName("SDG")
    private double USD_SDG;
    @SerializedName("SEK")
    private double USD_SEK;
    @SerializedName("SGD")
    private double USD_SGD;
    @SerializedName("SHP")
    private double USD_SHP;
    @SerializedName("SLL")
    private double USD_SLL;
    @SerializedName("SOS")
    private double USD_SOS;
    @SerializedName("SRD")
    private double USD_SRD;
    @SerializedName("SSP")
    private double USD_SSP;
    @SerializedName("STN")
    private double USD_STN;
    @SerializedName("SVC")
    private double USD_SVC;
    @SerializedName("SYP")
    private double USD_SYP;
    @SerializedName("SZL")
    private double USD_SZL;
    @SerializedName("THB")
    private double USD_THB;
    @SerializedName("TJS")
    private double USD_TJS;
    @SerializedName("TMT")
    private double USD_TMT;
    @SerializedName("TND")
    private double USD_TND;
    @SerializedName("TOP")
    private double USD_TOP;
    @SerializedName("TRY")
    private double USD_TRY;
    @SerializedName("TTD")
    private double USD_TTD;
    @SerializedName("TWD")
    private double USD_TWD;
    @SerializedName("TZS")
    private double USD_TZS;
    @SerializedName("UAH")
    private double USD_UAH;
    @SerializedName("UGX")
    private double USD_UGX;
    @SerializedName("USD")
    private double USD_USD;
    @SerializedName("UYU")
    private double USD_UYU;
    @SerializedName("UZS")
    private double USD_UZS;
    @SerializedName("VEF")
    private double USD_VEF;
    @SerializedName("VND")
    private double USD_VND;
    @SerializedName("VUV")
    private double USD_VUV;
    @SerializedName("WST")
    private double USD_WST;
    @SerializedName("XAF")
    private double USD_XAF;
    @SerializedName("XCD")
    private double USD_XCD;
    @SerializedName("XOF")
    private double USD_XOF;
    @SerializedName("XPF")
    private double USD_XPF;
    @SerializedName("YER")
    private double USD_YER;
    @SerializedName("ZAR")
    private double USD_ZAR;
    @SerializedName("ZMW")
    private double USD_ZMW;
    @SerializedName("ZWL")
    private double USD_ZWL;

    // Getters and setters are defined below.
    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    /**
     * Takes all the double fields in this object and adds them to a list of Currency objects.
     * Does this by using Java's Reflection API to obtain each field's name and value.
     */
    public void currenciesToList() {
        Field[] fields = ExchangeRates.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() != double.class) {
                continue;
            }
            String currencyCode = field.getName();
            double exchangeRate = 0.0;
            try {
                exchangeRate = (Double) field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Currency currency = new Currency(currencyCode, exchangeRate);
            currencies.add(currency);
        }
    }
}
