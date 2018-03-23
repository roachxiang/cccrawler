package com.hdcsd.crawler.cccrawler.entity;

public class CommonMessageEntity {
    private String exchange;
    private String type;//trade, kline
    private String content;

    public CommonMessageEntity(String exchange, String type, String content){
        this.exchange = exchange;
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
