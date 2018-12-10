package com.nicoqueijo.android.currencyconverter.models;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRates {

    private static final int AMOUNT_OF_CURRENCIES = 160;
    private List<Currency> currencies = new ArrayList<>(AMOUNT_OF_CURRENCIES);

    private double USDAED;
    private double USDAFN;
    private double USDALL;
    private double USDAMD;
    private double USDANG;
    private double USDAOA;
    private double USDARS;
    private double USDAUD;
    private double USDAWG;
    private double USDAZN;
    private double USDBAM;
    private double USDBBD;
    private double USDBDT;
    private double USDBGN;
    private double USDBHD;
    private double USDBIF;
    private double USDBMD;
    private double USDBND;
    private double USDBOB;
    private double USDBRL;
    private double USDBSD;
    private double USDBTN;
    private double USDBWP;
    private double USDBYN;
    private double USDBZD;
    private double USDCAD;
    private double USDCDF;
    private double USDCHF;
    private double USDCLP;
    private double USDCNY;
    private double USDCOP;
    private double USDCRC;
    private double USDCUP;
    private double USDCVE;
    private double USDCZK;
    private double USDDJF;
    private double USDDKK;
    private double USDDOP;
    private double USDDZD;
    private double USDEGP;
    private double USDERN;
    private double USDETB;
    private double USDEUR;
    private double USDFJD;
    private double USDFKP;
    private double USDGBP;
    private double USDGEL;
    private double USDGGP;
    private double USDGHS;
    private double USDGIP;
    private double USDGMD;
    private double USDGNF;
    private double USDGTQ;
    private double USDGYD;
    private double USDHKD;
    private double USDHNL;
    private double USDHRK;
    private double USDHTG;
    private double USDHUF;
    private double USDIDR;
    private double USDILS;
    private double USDIMP;
    private double USDINR;
    private double USDIQD;
    private double USDIRR;
    private double USDISK;
    private double USDJEP;
    private double USDJMD;
    private double USDJOD;
    private double USDJPY;
    private double USDKES;
    private double USDKGS;
    private double USDKHR;
    private double USDKMF;
    private double USDKPW;
    private double USDKRW;
    private double USDKWD;
    private double USDKYD;
    private double USDKZT;
    private double USDLAK;
    private double USDLBP;
    private double USDLKR;
    private double USDLRD;
    private double USDLSL;
    private double USDLTL;
    private double USDLVL;
    private double USDLYD;
    private double USDMAD;
    private double USDMDL;
    private double USDMGA;
    private double USDMKD;
    private double USDMMK;
    private double USDMNT;
    private double USDMOP;
    private double USDMRO;
    private double USDMUR;
    private double USDMVR;
    private double USDMWK;
    private double USDMXN;
    private double USDMYR;
    private double USDMZN;
    private double USDNAD;
    private double USDNGN;
    private double USDNIO;
    private double USDNOK;
    private double USDNPR;
    private double USDNZD;
    private double USDOMR;
    private double USDPAB;
    private double USDPEN;
    private double USDPGK;
    private double USDPHP;
    private double USDPKR;
    private double USDPLN;
    private double USDPYG;
    private double USDQAR;
    private double USDRON;
    private double USDRSD;
    private double USDRUB;
    private double USDRWF;
    private double USDSAR;
    private double USDSBD;
    private double USDSCR;
    private double USDSDG;
    private double USDSEK;
    private double USDSGD;
    private double USDSHP;
    private double USDSLL;
    private double USDSOS;
    private double USDSRD;
    private double USDSTD;
    private double USDSVC;
    private double USDSYP;
    private double USDSZL;
    private double USDTHB;
    private double USDTJS;
    private double USDTMT;
    private double USDTND;
    private double USDTOP;
    private double USDTRY;
    private double USDTTD;
    private double USDTWD;
    private double USDTZS;
    private double USDUAH;
    private double USDUGX;
    private double USDUSD;
    private double USDUYU;
    private double USDUZS;
    private double USDVEF;
    private double USDVND;
    private double USDVUV;
    private double USDWST;
    private double USDXAF;
    private double USDXCD;
    private double USDXOF;
    private double USDXPF;
    private double USDYER;
    private double USDZAR;
    private double USDZMW;
    private double USDZWL;

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

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
