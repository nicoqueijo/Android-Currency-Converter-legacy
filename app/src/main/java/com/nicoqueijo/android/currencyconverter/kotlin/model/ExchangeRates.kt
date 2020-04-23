package com.nicoqueijo.android.currencyconverter.kotlin.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ExchangeRates {

    /**
     * Creates a list of Currency objects from the declared fields of this class using reflection
     * to instantiate each object's currency code with the declared field's name.
     */
    val currencies: List<Currency>
        get() {
            val currencies = mutableListOf<Currency>()
            val declaredFields = javaClass.declaredFields
            for (property in declaredFields) {
                val currencyCode = property.name
                val exchangeRate = property.get(this) as Double
                currencies.add(Currency(currencyCode, exchangeRate))
            }
            return currencies
        }

    override fun toString() = currencies.toString()

    @Json(name = "AED")
    var USD_AED = 0.0

    @Json(name = "AFN")
    var USD_AFN = 0.0

    @Json(name = "ALL")
    var USD_ALL = 0.0

    @Json(name = "AMD")
    var USD_AMD = 0.0

    @Json(name = "ANG")
    var USD_ANG = 0.0

    @Json(name = "AOA")
    var USD_AOA = 0.0

    @Json(name = "ARS")
    var USD_ARS = 0.0

    @Json(name = "AUD")
    var USD_AUD = 0.0

    @Json(name = "AWG")
    var USD_AWG = 0.0

    @Json(name = "AZN")
    var USD_AZN = 0.0

    @Json(name = "BAM")
    var USD_BAM = 0.0

    @Json(name = "BBD")
    var USD_BBD = 0.0

    @Json(name = "BDT")
    var USD_BDT = 0.0

    @Json(name = "BGN")
    var USD_BGN = 0.0

    @Json(name = "BHD")
    var USD_BHD = 0.0

    @Json(name = "BIF")
    var USD_BIF = 0.0

    @Json(name = "BMD")
    var USD_BMD = 0.0

    @Json(name = "BND")
    var USD_BND = 0.0

    @Json(name = "BOB")
    var USD_BOB = 0.0

    @Json(name = "BRL")
    var USD_BRL = 0.0

    @Json(name = "BSD")
    var USD_BSD = 0.0

    @Json(name = "BTC")
    var USD_BTC = 0.0

    @Json(name = "BTN")
    var USD_BTN = 0.0

    @Json(name = "BWP")
    var USD_BWP = 0.0

    @Json(name = "BYN")
    var USD_BYN = 0.0

    @Json(name = "BZD")
    var USD_BZD = 0.0

    @Json(name = "CAD")
    var USD_CAD = 0.0

    @Json(name = "CDF")
    var USD_CDF = 0.0

    @Json(name = "CHF")
    var USD_CHF = 0.0

    @Json(name = "CLP")
    var USD_CLP = 0.0

    @Json(name = "CNY")
    var USD_CNY = 0.0

    @Json(name = "COP")
    var USD_COP = 0.0

    @Json(name = "CRC")
    var USD_CRC = 0.0

    @Json(name = "CUP")
    var USD_CUP = 0.0

    @Json(name = "CVE")
    var USD_CVE = 0.0

    @Json(name = "CZK")
    var USD_CZK = 0.0

    @Json(name = "DJF")
    var USD_DJF = 0.0

    @Json(name = "DKK")
    var USD_DKK = 0.0

    @Json(name = "DOP")
    var USD_DOP = 0.0

    @Json(name = "DZD")
    var USD_DZD = 0.0

    @Json(name = "EGP")
    var USD_EGP = 0.0

    @Json(name = "ERN")
    var USD_ERN = 0.0

    @Json(name = "ETB")
    var USD_ETB = 0.0

    @Json(name = "EUR")
    var USD_EUR = 0.0

    @Json(name = "FJD")
    var USD_FJD = 0.0

    @Json(name = "FKP")
    var USD_FKP = 0.0

    @Json(name = "GBP")
    var USD_GBP = 0.0

    @Json(name = "GEL")
    var USD_GEL = 0.0

    @Json(name = "GGP")
    var USD_GGP = 0.0

    @Json(name = "GHS")
    var USD_GHS = 0.0

    @Json(name = "GIP")
    var USD_GIP = 0.0

    @Json(name = "GMD")
    var USD_GMD = 0.0

    @Json(name = "GNF")
    var USD_GNF = 0.0

    @Json(name = "GTQ")
    var USD_GTQ = 0.0

    @Json(name = "GYD")
    var USD_GYD = 0.0

    @Json(name = "HKD")
    var USD_HKD = 0.0

    @Json(name = "HNL")
    var USD_HNL = 0.0

    @Json(name = "HRK")
    var USD_HRK = 0.0

    @Json(name = "HTG")
    var USD_HTG = 0.0

    @Json(name = "HUF")
    var USD_HUF = 0.0

    @Json(name = "IDR")
    var USD_IDR = 0.0

    @Json(name = "ILS")
    var USD_ILS = 0.0

    @Json(name = "IMP")
    var USD_IMP = 0.0

    @Json(name = "INR")
    var USD_INR = 0.0

    @Json(name = "IQD")
    var USD_IQD = 0.0

    @Json(name = "IRR")
    var USD_IRR = 0.0

    @Json(name = "ISK")
    var USD_ISK = 0.0

    @Json(name = "JEP")
    var USD_JEP = 0.0

    @Json(name = "JMD")
    var USD_JMD = 0.0

    @Json(name = "JOD")
    var USD_JOD = 0.0

    @Json(name = "JPY")
    var USD_JPY = 0.0

    @Json(name = "KES")
    var USD_KES = 0.0

    @Json(name = "KGS")
    var USD_KGS = 0.0

    @Json(name = "KHR")
    var USD_KHR = 0.0

    @Json(name = "KMF")
    var USD_KMF = 0.0

    @Json(name = "KPW")
    var USD_KPW = 0.0

    @Json(name = "KRW")
    var USD_KRW = 0.0

    @Json(name = "KWD")
    var USD_KWD = 0.0

    @Json(name = "KYD")
    var USD_KYD = 0.0

    @Json(name = "KZT")
    var USD_KZT = 0.0

    @Json(name = "LAK")
    var USD_LAK = 0.0

    @Json(name = "LBP")
    var USD_LBP = 0.0

    @Json(name = "LKR")
    var USD_LKR = 0.0

    @Json(name = "LRD")
    var USD_LRD = 0.0

    @Json(name = "LSL")
    var USD_LSL = 0.0

    @Json(name = "LYD")
    var USD_LYD = 0.0

    @Json(name = "MAD")
    var USD_MAD = 0.0

    @Json(name = "MDL")
    var USD_MDL = 0.0

    @Json(name = "MGA")
    var USD_MGA = 0.0

    @Json(name = "MKD")
    var USD_MKD = 0.0

    @Json(name = "MMK")
    var USD_MMK = 0.0

    @Json(name = "MNT")
    var USD_MNT = 0.0

    @Json(name = "MOP")
    var USD_MOP = 0.0

    @Json(name = "MRO")
    var USD_MRO = 0.0

    @Json(name = "MUR")
    var USD_MUR = 0.0

    @Json(name = "MVR")
    var USD_MVR = 0.0

    @Json(name = "MWK")
    var USD_MWK = 0.0

    @Json(name = "MXN")
    var USD_MXN = 0.0

    @Json(name = "MYR")
    var USD_MYR = 0.0

    @Json(name = "MZN")
    var USD_MZN = 0.0

    @Json(name = "NAD")
    var USD_NAD = 0.0

    @Json(name = "NGN")
    var USD_NGN = 0.0

    @Json(name = "NIO")
    var USD_NIO = 0.0

    @Json(name = "NOK")
    var USD_NOK = 0.0

    @Json(name = "NPR")
    var USD_NPR = 0.0

    @Json(name = "NZD")
    var USD_NZD = 0.0

    @Json(name = "OMR")
    var USD_OMR = 0.0

    @Json(name = "PAB")
    var USD_PAB = 0.0

    @Json(name = "PEN")
    var USD_PEN = 0.0

    @Json(name = "PGK")
    var USD_PGK = 0.0

    @Json(name = "PHP")
    var USD_PHP = 0.0

    @Json(name = "PKR")
    var USD_PKR = 0.0

    @Json(name = "PLN")
    var USD_PLN = 0.0

    @Json(name = "PYG")
    var USD_PYG = 0.0

    @Json(name = "QAR")
    var USD_QAR = 0.0

    @Json(name = "RON")
    var USD_RON = 0.0

    @Json(name = "RSD")
    var USD_RSD = 0.0

    @Json(name = "RUB")
    var USD_RUB = 0.0

    @Json(name = "RWF")
    var USD_RWF = 0.0

    @Json(name = "SAR")
    var USD_SAR = 0.0

    @Json(name = "SBD")
    var USD_SBD = 0.0

    @Json(name = "SCR")
    var USD_SCR = 0.0

    @Json(name = "SDG")
    var USD_SDG = 0.0

    @Json(name = "SEK")
    var USD_SEK = 0.0

    @Json(name = "SGD")
    var USD_SGD = 0.0

    @Json(name = "SHP")
    var USD_SHP = 0.0

    @Json(name = "SLL")
    var USD_SLL = 0.0

    @Json(name = "SOS")
    var USD_SOS = 0.0

    @Json(name = "SRD")
    var USD_SRD = 0.0

    @Json(name = "SSP")
    var USD_SSP = 0.0

    @Json(name = "STN")
    var USD_STN = 0.0

    @Json(name = "SVC")
    var USD_SVC = 0.0

    @Json(name = "SYP")
    var USD_SYP = 0.0

    @Json(name = "SZL")
    var USD_SZL = 0.0

    @Json(name = "THB")
    var USD_THB = 0.0

    @Json(name = "TJS")
    var USD_TJS = 0.0

    @Json(name = "TMT")
    var USD_TMT = 0.0

    @Json(name = "TND")
    var USD_TND = 0.0

    @Json(name = "TOP")
    var USD_TOP = 0.0

    @Json(name = "TRY")
    var USD_TRY = 0.0

    @Json(name = "TTD")
    var USD_TTD = 0.0

    @Json(name = "TWD")
    var USD_TWD = 0.0

    @Json(name = "TZS")
    var USD_TZS = 0.0

    @Json(name = "UAH")
    var USD_UAH = 0.0

    @Json(name = "UGX")
    var USD_UGX = 0.0

    @Json(name = "USD")
    var USD_USD = 0.0

    @Json(name = "UYU")
    var USD_UYU = 0.0

    @Json(name = "UZS")
    var USD_UZS = 0.0

    @Json(name = "VEF")
    var USD_VEF = 0.0

    @Json(name = "VND")
    var USD_VND = 0.0

    @Json(name = "VUV")
    var USD_VUV = 0.0

    @Json(name = "WST")
    var USD_WST = 0.0

    @Json(name = "XAF")
    var USD_XAF = 0.0

    @Json(name = "XAG")
    var USD_XAG = 0.0

    @Json(name = "XAU")
    var USD_XAU = 0.0

    @Json(name = "XCD")
    var USD_XCD = 0.0

    @Json(name = "XOF")
    var USD_XOF = 0.0

    @Json(name = "XPF")
    var USD_XPF = 0.0

    @Json(name = "YER")
    var USD_YER = 0.0

    @Json(name = "ZAR")
    var USD_ZAR = 0.0

    @Json(name = "ZMW")
    var USD_ZMW = 0.0

    @Json(name = "ZWL")
    var USD_ZWL = 0.0
}
