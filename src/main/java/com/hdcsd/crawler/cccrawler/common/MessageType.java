package com.hdcsd.crawler.cccrawler.common;

public enum MessageType {
    TRADE("trade"),
    KLINE("kline");

    private String type;
    MessageType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
