package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class HuobiproExchangeProcess extends ExchangeProcessBase {
    /*
     {
        "id": 成交id,
        "price": 成交价钱,
        "amount": 成交量,
        "direction": 主动成交方向,
        "ts": 成交时间
      }
      {
        "id": 600848670,
        "price": 7962.62,
        "amount": 0.0122,
        "direction": "buy",
        "ts": 1489464451000
      }
     */
    @Override
    public String processTrade(String exchange, String symbol, String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObjectTmp = (JSONObject)iterator.next();
            Long id = jsonObjectTmp.getLong("id");
            String key = exchange + symbol + id;
            if(!inCache(key)){
                jsonArrayResult.add(jsonObjectTmp);
            }
        }
        return jsonArrayResult.toJSONString();
    }

    /*
    {
    "id": K线id,
    "amount": 成交量,
    "count": 成交笔数,
    "open": 开盘价,
    "close": 收盘价,当K线为最晚的一根时，是最新成交价
    "low": 最低价,
    "high": 最高价,
    "vol": 成交额, 即 sum(每一笔成交价 * 该笔的成交量)
    }

    {
        "id": 1499184000,
        "amount": 37593.0266,
        "count": 0,
        "open": 1935.2000,
        "close": 1879.0000,
        "low": 1856.0000,
        "high": 1940.0000,
        "vol": 71031537.97866500
      }
     */

    @Override
    public String processKline(String exchange, String symbol, String content) {
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObjectTmp = (JSONObject)iterator.next();
            Long id = jsonObjectTmp.getLong("id");
            String key = exchange + symbol + id;
            if(!inCache(key)){
                jsonArrayResult.add(jsonObjectTmp);
            }
        }
        return jsonArrayResult.toJSONString();
    }
}
