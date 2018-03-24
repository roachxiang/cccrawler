package com.hdcsd.crawler.cccrawler.service;

import com.alibaba.fastjson.JSONObject;
import com.hdcsd.crawler.cccrawler.common.ExchangeProperties;
import com.hdcsd.crawler.cccrawler.common.MessageType;
import com.hdcsd.crawler.cccrawler.entity.CommonMessageEntity;
import com.hdcsd.crawler.cccrawler.entity.ExchangeEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageConvertor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> btcToExchange = new HashMap<>();
    private Map<String, String> ethToExchange = new HashMap<>();
    private Map<String, String> klineBtcToExchange = new HashMap<>();
    private Map<String, String> klineEthToExchange = new HashMap<>();

    @Autowired
    private ExchangeProperties properties;

    @PostConstruct
    public void init(){
        List<ExchangeEntity> exchangeEntities = properties.getList();
        for(ExchangeEntity entity : exchangeEntities){
            if(StringUtils.isNotEmpty(entity.getName())){
                if(StringUtils.isNotEmpty(entity.getBtcusdt())){
                    btcToExchange.put(entity.getBtcusdt(), entity.getName());
                }

                if(StringUtils.isNotEmpty(entity.getEthusdt())){
                    ethToExchange.put(entity.getEthusdt(), entity.getName());
                }

                if(StringUtils.isNotEmpty(entity.getKlinebtcusdt())){
                    klineBtcToExchange.put(entity.getKlinebtcusdt(), entity.getName());
                }

                if(StringUtils.isNotEmpty(entity.getKlineethusdt())){
                    klineEthToExchange.put(entity.getKlineethusdt(), entity.getName());
                }
            }
        }
    }

    public CommonMessageEntity convert(String uri, String content){
        if(btcToExchange.containsKey(uri)) {
            String exchange = btcToExchange.get(uri);
            return new CommonMessageEntity(exchange, MessageType.TRADE.getType(),
                     "BTCUSDT", convertByExchange(content, exchange));
        }
        else if(ethToExchange.containsKey(uri)) {
            String exchange = ethToExchange.get(uri);
            return new CommonMessageEntity(exchange, MessageType.TRADE.getType(),
                     "ETHUSDT", convertByExchange(content, exchange));
        }
        else if(klineBtcToExchange.containsKey(uri)) {
            String exchange = klineBtcToExchange.get(uri);
            return new CommonMessageEntity(exchange, MessageType.KLINE.getType(),
                     "BTCUSDT", convertByExchange(content, exchange));
        }
        else if(klineEthToExchange.containsKey(uri)) {
            String exchange = klineEthToExchange.get(uri);
            return new CommonMessageEntity(exchange, MessageType.KLINE.getType(),
                    "ETHUSDT", convertByExchange(content, exchange));
        }
        else
            logger.error("Bad uri:" + uri);
        return null;
    }

    private String convertByExchange(String content, String exchange){
        if(exchange.equals("huobipro")){
            JSONObject object = JSONObject.parseObject(content);
            return object.getString("data");
        }
        else{
            return  content;
        }
    }
}
