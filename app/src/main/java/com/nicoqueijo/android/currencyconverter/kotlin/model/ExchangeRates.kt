package com.nicoqueijo.android.currencyconverter.kotlin.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ExchangeRates {

    val currencies: List<Currency>
        get() {
            val currencies = mutableListOf<Currency>()
            val declaredFields = this.javaClass.declaredFields
            for (property in declaredFields) {
                val currencyCode = property.name
                val exchangeRate = property.get(this) as Double
                currencies.add(Currency(currencyCode, exchangeRate))
            }
            return currencies
        }

    override fun toString() = currencies.toString()

    @Json(name = "AED")
    internal var USD_AED = 0.0

    @Json(name = "AFN")
    internal var USD_AFN = 0.0

    @Json(name = "ALL")
    internal var USD_ALL = 0.0

    @Json(name = "AMD")
    internal var USD_AMD = 0.0

    @Json(name = "ANG")
    internal var USD_ANG = 0.0

    @Json(name = "AOA")
    internal var USD_AOA = 0.0

    @Json(name = "ARS")
    internal var USD_ARS = 0.0

    @Json(name = "AUD")
    internal var USD_AUD = 0.0

    @Json(name = "AWG")
    internal var USD_AWG = 0.0

    @Json(name = "AZN")
    internal var USD_AZN = 0.0

    @Json(name = "BAM")
    internal var USD_BAM = 0.0

    @Json(name = "BBD")
    internal var USD_BBD = 0.0

    @Json(name = "BDT")
    internal var USD_BDT = 0.0

    @Json(name = "BGN")
    internal var USD_BGN = 0.0

    @Json(name = "BHD")
    internal var USD_BHD = 0.0

    @Json(name = "BIF")
    internal var USD_BIF = 0.0

    @Json(name = "BMD")
    internal var USD_BMD = 0.0

    @Json(name = "BND")
    internal var USD_BND = 0.0

    @Json(name = "BOB")
    internal var USD_BOB = 0.0

    @Json(name = "BRL")
    internal var USD_BRL = 0.0

    @Json(name = "BSD")
    internal var USD_BSD = 0.0

    @Json(name = "BTC")
    internal var USD_BTC = 0.0

    @Json(name = "BTN")
    internal var USD_BTN = 0.0

    @Json(name = "BWP")
    internal var USD_BWP = 0.0

    @Json(name = "BYN")
    internal var USD_BYN = 0.0

    @Json(name = "BZD")
    internal var USD_BZD = 0.0

    @Json(name = "CAD")
    internal var USD_CAD = 0.0

    @Json(name = "CDF")
    internal var USD_CDF = 0.0

    @Json(name = "CHF")
    internal var USD_CHF = 0.0

    @Json(name = "CLP")
    internal var USD_CLP = 0.0

    @Json(name = "CNY")
    internal var USD_CNY = 0.0

    @Json(name = "COP")
    internal var USD_COP = 0.0

    @Json(name = "CRC")
    internal var USD_CRC = 0.0

    @Json(name = "CUP")
    internal var USD_CUP = 0.0

    @Json(name = "CVE")
    internal var USD_CVE = 0.0

    @Json(name = "CZK")
    internal var USD_CZK = 0.0

    @Json(name = "DJF")
    internal var USD_DJF = 0.0

    @Json(name = "DKK")
    internal var USD_DKK = 0.0

    @Json(name = "DOP")
    internal var USD_DOP = 0.0

    @Json(name = "DZD")
    internal var USD_DZD = 0.0

    @Json(name = "EGP")
    internal var USD_EGP = 0.0

    @Json(name = "ERN")
    internal var USD_ERN = 0.0

    @Json(name = "ETB")
    internal var USD_ETB = 0.0

    @Json(name = "EUR")
    internal var USD_EUR = 0.0

    @Json(name = "FJD")
    internal var USD_FJD = 0.0

    @Json(name = "FKP")
    internal var USD_FKP = 0.0

    @Json(name = "GBP")
    internal var USD_GBP = 0.0

    @Json(name = "GEL")
    internal var USD_GEL = 0.0

    @Json(name = "GGP")
    internal var USD_GGP = 0.0

    @Json(name = "GHS")
    internal var USD_GHS = 0.0

    @Json(name = "GIP")
    internal var USD_GIP = 0.0

    @Json(name = "GMD")
    internal var USD_GMD = 0.0

    @Json(name = "GNF")
    internal var USD_GNF = 0.0

    @Json(name = "GTQ")
    internal var USD_GTQ = 0.0

    @Json(name = "GYD")
    internal var USD_GYD = 0.0

    @Json(name = "HKD")
    internal var USD_HKD = 0.0

    @Json(name = "HNL")
    internal var USD_HNL = 0.0

    @Json(name = "HRK")
    internal var USD_HRK = 0.0

    @Json(name = "HTG")
    internal var USD_HTG = 0.0

    @Json(name = "HUF")
    internal var USD_HUF = 0.0

    @Json(name = "IDR")
    internal var USD_IDR = 0.0

    @Json(name = "ILS")
    internal var USD_ILS = 0.0

    @Json(name = "IMP")
    internal var USD_IMP = 0.0

    @Json(name = "INR")
    internal var USD_INR = 0.0

    @Json(name = "IQD")
    internal var USD_IQD = 0.0

    @Json(name = "IRR")
    internal var USD_IRR = 0.0

    @Json(name = "ISK")
    internal var USD_ISK = 0.0

    @Json(name = "JEP")
    internal var USD_JEP = 0.0

    @Json(name = "JMD")
    internal var USD_JMD = 0.0

    @Json(name = "JOD")
    internal var USD_JOD = 0.0

    @Json(name = "JPY")
    internal var USD_JPY = 0.0

    @Json(name = "KES")
    internal var USD_KES = 0.0

    @Json(name = "KGS")
    internal var USD_KGS = 0.0

    @Json(name = "KHR")
    internal var USD_KHR = 0.0

    @Json(name = "KMF")
    internal var USD_KMF = 0.0

    @Json(name = "KPW")
    internal var USD_KPW = 0.0

    @Json(name = "KRW")
    internal var USD_KRW = 0.0

    @Json(name = "KWD")
    internal var USD_KWD = 0.0

    @Json(name = "KYD")
    internal var USD_KYD = 0.0

    @Json(name = "KZT")
    internal var USD_KZT = 0.0

    @Json(name = "LAK")
    internal var USD_LAK = 0.0

    @Json(name = "LBP")
    internal var USD_LBP = 0.0

    @Json(name = "LKR")
    internal var USD_LKR = 0.0

    @Json(name = "LRD")
    internal var USD_LRD = 0.0

    @Json(name = "LSL")
    internal var USD_LSL = 0.0

    @Json(name = "LYD")
    internal var USD_LYD = 0.0

    @Json(name = "MAD")
    internal var USD_MAD = 0.0

    @Json(name = "MDL")
    internal var USD_MDL = 0.0

    @Json(name = "MGA")
    internal var USD_MGA = 0.0

    @Json(name = "MKD")
    internal var USD_MKD = 0.0

    @Json(name = "MMK")
    internal var USD_MMK = 0.0

    @Json(name = "MNT")
    internal var USD_MNT = 0.0

    @Json(name = "MOP")
    internal var USD_MOP = 0.0

    @Json(name = "MRO")
    internal var USD_MRO = 0.0

    @Json(name = "MUR")
    internal var USD_MUR = 0.0

    @Json(name = "MVR")
    internal var USD_MVR = 0.0

    @Json(name = "MWK")
    internal var USD_MWK = 0.0

    @Json(name = "MXN")
    internal var USD_MXN = 0.0

    @Json(name = "MYR")
    internal var USD_MYR = 0.0

    @Json(name = "MZN")
    internal var USD_MZN = 0.0

    @Json(name = "NAD")
    internal var USD_NAD = 0.0

    @Json(name = "NGN")
    internal var USD_NGN = 0.0

    @Json(name = "NIO")
    internal var USD_NIO = 0.0

    @Json(name = "NOK")
    internal var USD_NOK = 0.0

    @Json(name = "NPR")
    internal var USD_NPR = 0.0

    @Json(name = "NZD")
    internal var USD_NZD = 0.0

    @Json(name = "OMR")
    internal var USD_OMR = 0.0

    @Json(name = "PAB")
    internal var USD_PAB = 0.0

    @Json(name = "PEN")
    internal var USD_PEN = 0.0

    @Json(name = "PGK")
    internal var USD_PGK = 0.0

    @Json(name = "PHP")
    internal var USD_PHP = 0.0

    @Json(name = "PKR")
    internal var USD_PKR = 0.0

    @Json(name = "PLN")
    internal var USD_PLN = 0.0

    @Json(name = "PYG")
    internal var USD_PYG = 0.0

    @Json(name = "QAR")
    internal var USD_QAR = 0.0

    @Json(name = "RON")
    internal var USD_RON = 0.0

    @Json(name = "RSD")
    internal var USD_RSD = 0.0

    @Json(name = "RUB")
    internal var USD_RUB = 0.0

    @Json(name = "RWF")
    internal var USD_RWF = 0.0

    @Json(name = "SAR")
    internal var USD_SAR = 0.0

    @Json(name = "SBD")
    internal var USD_SBD = 0.0

    @Json(name = "SCR")
    internal var USD_SCR = 0.0

    @Json(name = "SDG")
    internal var USD_SDG = 0.0

    @Json(name = "SEK")
    internal var USD_SEK = 0.0

    @Json(name = "SGD")
    internal var USD_SGD = 0.0

    @Json(name = "SHP")
    internal var USD_SHP = 0.0

    @Json(name = "SLL")
    internal var USD_SLL = 0.0

    @Json(name = "SOS")
    internal var USD_SOS = 0.0

    @Json(name = "SRD")
    internal var USD_SRD = 0.0

    @Json(name = "SSP")
    internal var USD_SSP = 0.0

    @Json(name = "STN")
    internal var USD_STN = 0.0

    @Json(name = "SVC")
    internal var USD_SVC = 0.0

    @Json(name = "SYP")
    internal var USD_SYP = 0.0

    @Json(name = "SZL")
    internal var USD_SZL = 0.0

    @Json(name = "THB")
    internal var USD_THB = 0.0

    @Json(name = "TJS")
    internal var USD_TJS = 0.0

    @Json(name = "TMT")
    internal var USD_TMT = 0.0

    @Json(name = "TND")
    internal var USD_TND = 0.0

    @Json(name = "TOP")
    internal var USD_TOP = 0.0

    @Json(name = "TRY")
    internal var USD_TRY = 0.0

    @Json(name = "TTD")
    internal var USD_TTD = 0.0

    @Json(name = "TWD")
    internal var USD_TWD = 0.0

    @Json(name = "TZS")
    internal var USD_TZS = 0.0

    @Json(name = "UAH")
    internal var USD_UAH = 0.0

    @Json(name = "UGX")
    internal var USD_UGX = 0.0

    @Json(name = "USD")
    internal var USD_USD = 0.0

    @Json(name = "UYU")
    internal var USD_UYU = 0.0

    @Json(name = "UZS")
    internal var USD_UZS = 0.0

    @Json(name = "VEF")
    internal var USD_VEF = 0.0

    @Json(name = "VND")
    internal var USD_VND = 0.0

    @Json(name = "VUV")
    internal var USD_VUV = 0.0

    @Json(name = "WST")
    internal var USD_WST = 0.0

    @Json(name = "XAF")
    internal var USD_XAF = 0.0

    @Json(name = "XAG")
    internal var USD_XAG = 0.0

    @Json(name = "XAU")
    internal var USD_XAU = 0.0

    @Json(name = "XCD")
    internal var USD_XCD = 0.0

    @Json(name = "XOF")
    internal var USD_XOF = 0.0

    @Json(name = "XPF")
    internal var USD_XPF = 0.0

    @Json(name = "YER")
    internal var USD_YER = 0.0

    @Json(name = "ZAR")
    internal var USD_ZAR = 0.0

    @Json(name = "ZMW")
    internal var USD_ZMW = 0.0

    @Json(name = "ZWL")
    internal var USD_ZWL = 0.0
}
