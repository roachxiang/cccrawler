package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class GateioExchangeProcess extends ExchangeProcessBase {
    @Override
    public String processTrade(String exchange, String symbol, String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObjectTmp = (JSONObject)iterator.next();
            Long id = jsonObjectTmp.getLong("tradeID");
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
