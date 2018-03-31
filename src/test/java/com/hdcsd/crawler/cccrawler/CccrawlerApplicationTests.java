package com.hdcsd.crawler.cccrawler;

import com.alibaba.fastjson.JSONArray;
import com.hdcsd.crawler.cccrawler.entity.CacheEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CccrawlerApplicationTests {
    @Autowired
    CacheEntity cacheEntity;


	@Test
	public void contextLoads() {
	}

	@Test
    public void testCache(){
        String result = cacheEntity.test("aa");
        System.out.println("result:" + result);
        result = cacheEntity.update("aa");
        System.out.println("result:" + result);
        result = cacheEntity.test("aa");
        System.out.println("result:" + result);
    }

    @Test
    public void testJson(){
	    String json = "[[1522497000000,\"7105.00000000\",\"7109.00000000\",\"7100.00000000\",\"7107.94000000\",\"4.96577600\",1522497059999,\"35286.70608775\",106,\"2.33179100\",\"16574.49039419\",\"0\"],[1522497060000,\"7107.93000000\",\"7107.93000000\",\"7100.00000000\",\"7100.11000000\",\"4.31957100\",1522497119999,\"30681.60669422\",79,\"1.64875200\",\"11712.28990929\",\"0\"],[1522497120000,\"7100.11000000\",\"7102.98000000\",\"7100.10000000\",\"7100.99000000\",\"5.07734300\",1522497179999,\"36052.73043532\",91,\"2.70378700\",\"19199.99646354\",\"0\"],[1522497180000,\"7100.99000000\",\"7108.79000000\",\"7100.10000000\",\"7101.00000000\",\"7.44769800\",1522497239999,\"52889.44369227\",128,\"3.36053300\",\"23867.68141098\",\"0\"],[1522497240000,\"7100.89000000\",\"7100.89000000\",\"7090.06000000\",\"7090.06000000\",\"23.37714100\",1522497299999,\"165964.48577962\",57,\"1.98455700\",\"14082.38735201\",\"0\"]]";
        JSONArray jsonArray = JSONArray.parseArray(json);
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            System.out.println(((JSONArray)iterator.next()).getLong(0));
        }
    }

}
