package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class BinanceExchangeProcess extends ExchangeProcessBase {
    /*
    {"id":32421789,"price":"6947.01000000","qty":"0.04078600",
    "time":1522557341020,"isBuyerMaker":true,"isBestMatch":true}
     */
    @Override
    public String processTrade(String exchange, String symbol, String content) {
        JSONArray jsonArray = JSONArray.parseArray(content);
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            Long id = jsonObject.getLong("id");
            String key = exchange + symbol + id;
            if(!inCache(key)){
                jsonArrayResult.add(jsonObject);
            }
        }

        return jsonArrayResult.toJSONString();
    }

    /*
    [
      [
        1499040000000,      // Open time
        "0.01634790",       // Open
        "0.80000000",       // High
        "0.01575800",       // Low
        "0.01577100",       // Close
        "148976.11427815",  // Volume
        1499644799999,      // Close time
        "2434.19055334",    // Quote asset volume
        308,                // Number of trades
        "1756.87402397",    // Taker buy base asset volume
        "28.46694368",      // Taker buy quote asset volume
        "17928899.62484339" // Ignore
      ]
    ]
     */
    @Override
    public String processKline(String exchange, String symbol, String content) {
        JSONArray jsonArray = JSONArray.parseArray(content);
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONArray jsonArrayTmp = (JSONArray) iterator.next();
            Long id = jsonArrayTmp.getLong(0);
            String key = exchange + symbol + id;
            if(!inCache(key)){
                jsonArrayResult.add(jsonArrayTmp);
            }
        }

        return jsonArrayResult.toJSONString();
    }
}
