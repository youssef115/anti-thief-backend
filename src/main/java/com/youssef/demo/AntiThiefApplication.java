package com.youssef.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AntiThiefApplication {

	public static void main(String[] args) {
		SpringApplication.run(AntiThiefApplication.class, args);
	}

}
