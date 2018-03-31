package com.hdcsd.crawler.cccrawler.entity;

public class ExchangeEntity {
    private String name;
    private String tradeurl;
    private String klineurl;
    private String symbols;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTradeurl() {
        return tradeurl;
    }

    public void setTradeurl(String tradeurl) {
        this.tradeurl = tradeurl;
    }

    public String getKlineurl() {
        return klineurl;
    }

    public void setKlineurl(String klineurl) {
        this.klineurl = klineurl;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }
}
