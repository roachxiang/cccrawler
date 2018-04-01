package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class BittrexExchangeProcess extends ExchangeProcessBase {

    /*
    {
	"Id" : 319435,
	"TimeStamp" : "2014-07-09T03:21:20.08",
	"Quantity" : 0.30802438,
	"Price" : 0.01263400,
	"Total" : 0.00389158,
	"FillType" : "FILL",
	"OrderType" : "BUY"
    }

     */
    @Override
    public String processTrade(String exchange, String symbol, String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObjectTmp = (JSONObject)iterator.next();
            Long id = jsonObjectTmp.getLong("Id");
            String key = exchange + symbol + id;
            if(!inCache(key)){
                jsonArrayResult.add(jsonObjectTmp);
            }
        }
        return jsonArrayResult.toJSONString();
    }

    @Override
    public String processKline(String exchange, String symbol, String content) {
        return content;
    }
}
