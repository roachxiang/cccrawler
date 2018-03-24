package com.hdcsd.crawler.cccrawler.entity;

public class CommonMessageEntity {
    private String exchange;
    private String type;//trade, kline
    private String symbol;//BTCUSDT,ETHUSDT
    private String content;

    public CommonMessageEntity(String exchange, String type, String symbol, String content){
        this.exchange = exchange;
        this.type = type;
        this.symbol = symbol;
        this.content = content;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
