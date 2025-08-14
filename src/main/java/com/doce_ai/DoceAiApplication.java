package com.doce_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoceAiApplication {
	public static void main(String[] args) {

		SpringApplication.run(DoceAiApplication.class, args);
	}
}
