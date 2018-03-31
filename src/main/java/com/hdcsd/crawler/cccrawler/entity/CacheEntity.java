package com.hdcsd.crawler.cccrawler.entity;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "tradeinfo")
public class CacheEntity {
    @Cacheable
    public String test(String key){
        return null;
    }

    @CachePut
    public String update(String key){
        return "exist";
    }
}
