package com.doce_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;

//@SpringBootApplication(
//		exclude = {
//				MongoAutoConfiguration.class,
//				MongoDataAutoConfiguration.class
//		}
//)
@SpringBootApplication
public class DoceAiApplication {
	public static void main(String[] args) {
		SpringApplication.run(DoceAiApplication.class, args);
	}
}
