package com.hdcsd.crawler.cccrawler.entity;

public class ExchangeEntity {
    private String name;
    private String btcusdt;
    private String ethusdt;
    private String klineurl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBtcusdt() {
        return btcusdt;
    }

    public void setBtcusdt(String btcusdt) {
        this.btcusdt = btcusdt;
    }

    public String getEthusdt() {
        return ethusdt;
    }

    public void setEthusdt(String ethusdt) {
        this.ethusdt = ethusdt;
    }

    public String getKlineurl() {
        return klineurl;
    }

    public void setKlineurl(String klineurl) {
        this.klineurl = klineurl;
    }
}
