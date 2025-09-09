package com.example.FoodWebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FoodWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodWebsiteApplication.class, args);
	}

}
