package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class ZbExchangeProcess extends ExchangeProcessBase {
    /*
    {
        "amount": 0.541,
        "date": 1472711925,
        "price": 81.87,
        "tid": 16497097,
        "trade_type": "ask",
        "type": "sell"
    }
    date : 交易时间(时间戳)
    price : 交易价格
    amount : 交易数量
    tid : 交易生成ID
    type : 交易类型，buy(买)/sell(卖)
    trade_type : 委托类型，ask(卖)/bid(买)
     */
    @Override
    public String processTrade(String exchange, String symbol, String content) {
        JSONArray jsonArray = JSONArray.parseArray(content);
        JSONArray jsonArrayResult = new JSONArray();
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            Long id = jsonObject.getLong("tid");
            String key = exchange + symbol + id;
            if(!inCache(key)){
                jsonArrayResult.add(jsonObject);
            }
        }

        return jsonArrayResult.toJSONString();
    }

    /*
        [
            1417536000000, 时间戳
            2370.16, 开
            2380, 高
            2352, 低
            2367.37, 收
            17259.83 交易量
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
