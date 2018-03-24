package com.hdcsd.crawler.cccrawler.service;

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
    //private Map<String, String> klineUrlToExchange = new HashMap<>();

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

                //if(StringUtils.isNotEmpty(entity.getKlineurl())){
                //    klineUrlToExchange.put(entity.getKlineurl(), entity.getName());
                //}
            }
        }
    }

    public CommonMessageEntity convert(String uri, String content){
        if(btcToExchange.containsKey(uri))
            return new CommonMessageEntity(btcToExchange.get(uri),
                    MessageType.TRADE.getType(), "BTCUSDT", content);
        else if(ethToExchange.containsKey(uri))
            return new CommonMessageEntity(ethToExchange.get(uri),
                    MessageType.TRADE.getType(), "ETHUSDT", content);
        //else if(klineUrlToExchange.containsKey(uri))
        //    return new CommonMessageEntity(klineUrlToExchange.get(uri),
        //            MessageType.KLINE.getType(), content);
        else
            logger.error("Bad uri:" + uri);
        return null;
    }
}
