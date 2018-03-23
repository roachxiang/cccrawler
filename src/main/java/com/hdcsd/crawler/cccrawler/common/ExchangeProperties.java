package com.hdcsd.crawler.cccrawler.common;

import com.hdcsd.crawler.cccrawler.entity.ExchangeEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("exchange")
public class ExchangeProperties {
    private final List<ExchangeEntity> list = new ArrayList<>();

    public List<ExchangeEntity> getList() {
        return this.list;
    }
}
