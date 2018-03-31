package com.hdcsd.crawler.cccrawler.exchange;

import com.hdcsd.crawler.cccrawler.entity.CacheEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class ExchangeProcessBase {
    @Autowired
    private CacheEntity cacheEntity;

    public abstract String processTrade(String exchange, String symbol, String content);

    public abstract String processKline(String exchange, String symbol, String content);

    boolean inCache(String key){
        if(cacheEntity.test(key) == null){
            cacheEntity.update(key);
            return false;
        }
        else
            return true;
    }
}
