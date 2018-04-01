package com.hdcsd.crawler.cccrawler.exchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class OkcoinExchangeProcess extends ExchangeProcessBase {
    /*
    {
        "date": "1367130137",
        "date_ms": "1367130137000",
        "price": 787.5,
        "amount": 0.091,
        "tid": "230435",
        "type": "sell"
    }
    date:交易时间
    date_ms:交易时间(ms)
    price: 交易价格
    amount: 交易数量
    tid: 交易生成ID
    type: buy/sell
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
	1417536000000,	时间戳
	2370.16,	开
	2380,	        高
	2352,	        低
	2367.37,	收
	17259.83	交易量
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
