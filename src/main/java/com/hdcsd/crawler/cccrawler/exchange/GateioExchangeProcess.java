package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class GateioExchangeProcess extends ExchangeProcessBase {

    /*
    {
                "tradeID": 3175762,
                "date": "2017-08-25 07:24:28",
                "type": "sell",
                "rate": 29011,
                "amount": 0.0019,
                "total": 55.1209
      }
        amount: 成交币种数量
        date: 订单时间
        rate: 币种单价
        total: 订单总额
        tradeID: tradeID
        type: 买卖类型, buy买 sell卖
     */
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
