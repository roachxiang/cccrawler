package com.hdcsd.crawler.cccrawler.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.hdcsd.crawler.cccrawler.common.ExchangeProperties;
import com.hdcsd.crawler.cccrawler.entity.CommonMessageEntity;
import com.hdcsd.crawler.cccrawler.entity.ExchangeEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class Crawler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final List<HttpGet> httpGetList10S = new ArrayList<>();
    private final List<HttpGet> httpGetList20S = new ArrayList<>();
    private final List<HttpGet> httpGetList30S = new ArrayList<>();
    private final List<HttpGet> httpKlineGetList = new ArrayList<>();

    private int totalTrade10S = 0;
    private int totalTrade20S = 0;
    private int totalTrade30S = 0;
    private int totalKline = 0;
    private int success = 0;
    private int failed = 0;
    private int cancelled = 0;

    @Autowired
    private ExchangeProperties properties;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private MessageConvertor messageConvertor;

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
                        switch (entity.getPeriod()) {
                            case "10":
                                httpGetList10S.add(new HttpGet(tradeurl));
                                break;
                            case "20":
                                httpGetList20S.add(new HttpGet(tradeurl));
                                break;
                            case "30":
                                httpGetList30S.add(new HttpGet(tradeurl));
                                break;
                            default:
                                httpGetList30S.add(new HttpGet(tradeurl));
                                break;
                        }
                    }
                }

                if (StringUtils.isNotEmpty(entity.getKlineurl())) {
                    for(String symbol : symbolList) {
                        String[] tmp = symbol.split("\\|");
                        String klineurl = String.format(entity.getKlineurl(), tmp[0]);
                        logger.info("Add new kline url:" + klineurl);
                        httpKlineGetList.add(new HttpGet(klineurl));
                    }
                }
            }
        }

        totalTrade10S = httpGetList10S.size();
        totalTrade20S = httpGetList20S.size();
        totalTrade30S = httpGetList30S.size();
        totalKline = httpKlineGetList.size();
    }

    /*
     * 频率限制
     * 币安：1分钟1200次
     * zb: 单个IP限制每分钟1000次访问,K线接口每秒只能请求一次数据。
     * huobipro:行情api不限制
     * okex:一个IP五分钟之内，最多发送3000个https请求。相当于1秒钟10个
     * okcoinusd:一个ip五分钟之内，最多发送3000个https请求。
     * bittrex:对交易有限制，对行情没看到限制
     * bitstamp:10分钟600次,1s钟1次
     * gateio:没有说明
     */

    @Scheduled(fixedRate = 10000)
    public void run() throws InterruptedException, IOException {
        logger.info("start crawler");
        logger.debug(JSON.toJSONString(properties.getList()));
        Stopwatch sw = Stopwatch.createStarted();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        try {
            httpclient.start();
            final CountDownLatch latch = new CountDownLatch(httpGetList10S.size());
            success = 0;
            failed = 0;
            cancelled = 0;
            for (final HttpGet request : httpGetList10S) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                        success++;
                        try {
                            String uri = request.getURI().toASCIIString();
                            String content = new String(IOUtils.toByteArray(response.getEntity().getContent()));
                            logger.debug(uri + ":" + content);
                            CommonMessageEntity entity = messageConvertor.convert(uri, content);
                            kafkaProducer.send(entity);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        failed++;
                        logger.info(request.getRequestLine() + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        cancelled++;
                        logger.info(request.getRequestLine() + " cancelled");
                    }

                });
            }
            latch.await();
            logger.info("Shutting down");
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        finally {
            httpclient.close();
        }
        logger.info("Done");
        sw.stop();
        long millis = sw.elapsed(MILLISECONDS);
        logger.info("time: " + millis);
        logger.info(String.format("Total %d success %d failed %d cancelled %d",
                totalTrade10S, success, failed, cancelled));
    }

    @Scheduled(fixedRate = 20000)
    public void run20S() throws InterruptedException, IOException {
        logger.info("start crawler");
        logger.debug(JSON.toJSONString(properties.getList()));
        Stopwatch sw = Stopwatch.createStarted();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        try {
            httpclient.start();
            final CountDownLatch latch = new CountDownLatch(httpGetList20S.size());
            success = 0;
            failed = 0;
            cancelled = 0;
            for (final HttpGet request : httpGetList20S) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                        success++;
                        try {
                            String uri = request.getURI().toASCIIString();
                            String content = new String(IOUtils.toByteArray(response.getEntity().getContent()));
                            logger.debug(uri + ":" + content);
                            CommonMessageEntity entity = messageConvertor.convert(uri, content);
                            kafkaProducer.send(entity);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        failed++;
                        logger.info(request.getRequestLine() + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        cancelled++;
                        logger.info(request.getRequestLine() + " cancelled");
                    }

                });
            }
            latch.await();
            logger.info("Shutting down");
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        finally {
            httpclient.close();
        }
        logger.info("Done");
        sw.stop();
        long millis = sw.elapsed(MILLISECONDS);
        logger.info("time: " + millis);
        logger.info(String.format("Total %d success %d failed %d cancelled %d",
                totalTrade20S, success, failed, cancelled));
    }

    @Scheduled(fixedRate = 20000)
    public void run30S() throws InterruptedException, IOException {
        logger.info("start crawler");
        logger.debug(JSON.toJSONString(properties.getList()));
        Stopwatch sw = Stopwatch.createStarted();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        try {
            httpclient.start();
            final CountDownLatch latch = new CountDownLatch(httpGetList30S.size());
            success = 0;
            failed = 0;
            cancelled = 0;
            for (final HttpGet request : httpGetList30S) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                        success++;
                        try {
                            String uri = request.getURI().toASCIIString();
                            String content = new String(IOUtils.toByteArray(response.getEntity().getContent()));
                            logger.debug(uri + ":" + content);
                            CommonMessageEntity entity = messageConvertor.convert(uri, content);
                            kafkaProducer.send(entity);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        failed++;
                        logger.info(request.getRequestLine() + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        cancelled++;
                        logger.info(request.getRequestLine() + " cancelled");
                    }

                });
            }
            latch.await();
            logger.info("Shutting down");
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        finally {
            httpclient.close();
        }
        logger.info("Done");
        sw.stop();
        long millis = sw.elapsed(MILLISECONDS);
        logger.info("time: " + millis);
        logger.info(String.format("Total %d success %d failed %d cancelled %d",
                totalTrade30S, success, failed, cancelled));
    }

    @Scheduled(fixedRate = 60000)
    public void kline() throws InterruptedException, IOException {
        logger.info("start crawler");
        logger.debug(JSON.toJSONString(properties.getList()));
        Stopwatch sw = Stopwatch.createStarted();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        try {
            httpclient.start();
            final CountDownLatch latch = new CountDownLatch(httpKlineGetList.size());
            success = 0;
            failed = 0;
            cancelled = 0;
            for (final HttpGet request : httpKlineGetList) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                        success++;
                        try {
                            String uri = request.getURI().toASCIIString();
                            String content = new String(IOUtils.toByteArray(response.getEntity().getContent()));
                            logger.debug(uri + ":" + content);
                            CommonMessageEntity entity = messageConvertor.convert(uri, content);
                            kafkaProducer.send(entity);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        failed++;
                        logger.info(request.getRequestLine() + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        cancelled++;
                        logger.info(request.getRequestLine() + " cancelled");
                    }

                });
            }
            latch.await();
            logger.info("Shutting down");
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        finally {
            httpclient.close();
        }
        logger.info("Done");
        sw.stop();
        long millis = sw.elapsed(MILLISECONDS);
        logger.info("time: " + millis);
        logger.info(String.format("Kline total %d success %d failed %d cancelled %d",
                totalKline, success, failed, cancelled));
    }
}
