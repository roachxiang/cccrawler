package com.hdcsd.crawler.cccrawler.service;

import com.alibaba.fastjson.JSON;
import com.hdcsd.crawler.cccrawler.entity.CommonMessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;

@Component
public class KafkaProducer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.default.topic}")
    private String defaultTopic;

    @PostConstruct
    public void init(){
        logger.info("KafkaProducer default topic:" + defaultTopic);
    }

    public void send(CommonMessageEntity entity){
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(defaultTopic, JSON.toJSONString(entity));
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.debug("Send success");
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Send exception:" + ex.getMessage(), ex);
            }
        });
    }
}
