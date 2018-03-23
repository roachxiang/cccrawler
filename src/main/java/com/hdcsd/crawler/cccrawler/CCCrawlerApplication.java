package com.hdcsd.crawler.cccrawler;

import com.hdcsd.crawler.cccrawler.common.ExchangeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(value = { ExchangeProperties.class })
@EnableKafka
public class CCCrawlerApplication {
	public static void main(String[] args) {
		SpringApplication.run(CCCrawlerApplication.class, args);
	}
}
