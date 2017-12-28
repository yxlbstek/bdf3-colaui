package com.bstek.cola;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ColaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ColaApplication.class, args);
	}
}
