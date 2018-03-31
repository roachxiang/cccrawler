package com.hdcsd.crawler.cccrawler.service;

import com.alibaba.fastjson.JSONObject;
import com.hdcsd.crawler.cccrawler.common.ExchangeProperties;
import com.hdcsd.crawler.cccrawler.common.MessageType;
import com.hdcsd.crawler.cccrawler.entity.CommonMessageEntity;
import com.hdcsd.crawler.cccrawler.entity.ExchangeEntity;
import com.hdcsd.crawler.cccrawler.exchange.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageConvertor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Map<String,String>> urlToInfo = new HashMap<>();

    @Autowired
    private ExchangeProperties properties;

    @Autowired
    private BinanceExchangeProcess biance;

    @Autowired
    private ZbExchangeProcess zb;

    @Autowired
    private HuobiproExchangeProcess huobipro;

    @Autowired
    private OkexExchangeProcess okex;

    @Autowired
    private OkcoinExchangeProcess okcoin;

    @Autowired
    private BittrexExchangeProcess bittrex;

    @Autowired
    private BitstampExchangeProcess bitstamp;

    @Autowired
    private GateioExchangeProcess gateio;

    @PostConstruct
    public void init(){
        List<ExchangeEntity> exchangeEntities = properties.getList();
        for(ExchangeEntity entity : exchangeEntities){
            if(StringUtils.isNotEmpty(entity.getSymbols())) {
                List<String> symbolList = Arrays.asList(entity.getSymbols().split(","));
                if (StringUtils.isNotEmpty(entity.getTradeurl())) {
                    for(String symbol : symbolList) {
                        String[] tmp = symbol.split("\\|");
                        String tradeurl = String.format(entity.getTradeurl(), tmp[0]);
                        logger.info("Add new trade url:" + tradeurl);
                        Map<String, String> infoMap = new HashMap<>();
                        infoMap.put("exchange", entity.getName());
                        infoMap.put("symbol", tmp[1]);
                        infoMap.put("type", MessageType.TRADE.getType());
                        urlToInfo.put(tradeurl, infoMap);
                    }
                }

                if (StringUtils.isNotEmpty(entity.getKlineurl())) {
                    for(String symbol : symbolList) {
                        String[] tmp = symbol.split("\\|");
                        String klineurl = String.format(entity.getKlineurl(), tmp[0]);
                        logger.info("Add new kline url:" + klineurl);
                        Map<String, String> infoMap = new HashMap<>();
                        infoMap.put("exchange", entity.getName());
                        infoMap.put("symbol", tmp[1]);
                        infoMap.put("type", MessageType.KLINE.getType());
                        urlToInfo.put(klineurl, infoMap);
                    }
                }
            }
        }
    }

    public CommonMessageEntity convert(String uri, String content){
        if(urlToInfo.containsKey(uri)) {
            Map<String, String> infoMap = urlToInfo.get(uri);
            return new CommonMessageEntity(infoMap.get("exchange"), infoMap.get("type"),
                     infoMap.get("symbol"),
                    convertByExchange(content, infoMap.get("exchange"), infoMap.get("type"),
                            infoMap.get("symbol")));
        }
        else
            logger.error("Bad uri:" + uri);
        return null;
    }

    private String convertByExchange(String content, String exchange, String type, String symbol){
        String key = exchange + type;
        String result = null;
        switch (key){
            case "binancetrade":
                result = biance.processTrade(exchange, symbol, content);
                break;
            case "binancekline":
                result = biance.processKline(exchange, symbol, content);
                break;
            case "zbtrade":
                result = zb.processTrade(exchange, symbol, content);
                break;
            case "zbkline":
                result = zb.processKline(exchange,symbol,content);
                break;
            case "huobiprotrade":
                result = huobipro.processTrade(exchange, symbol, content);
                break;
            case "huobiprokline":
                result = huobipro.processKline(exchange, symbol, content);
                break;
            case "okextrade":
                result = okex.processTrade(exchange, symbol, content);
                break;
            case "okexkline":
                result = okex.processKline(exchange, symbol, content);
                break;
            case "okcoinusdtrade":
                result = okcoin.processTrade(exchange, symbol, content);
                break;
            case "okcoinusdkline":
                result = okcoin.processKline(exchange, symbol, content);
                break;
            case "bittrextrade":
                result = bittrex.processTrade(exchange, symbol, content);
                break;
            case "bitstamptrade":
                result = bitstamp.processTrade(exchange, symbol, content);
                break;
            case "gateiotrade":
                result = gateio.processTrade(exchange, symbol, content);
                break;
            default:
                logger.error(String.format("bad %s %s", exchange, type));
                break;
        }

        logger.debug("result:" + exchange + ":" + type + ":" + symbol + ":" + result);
        return result;
    }
}
