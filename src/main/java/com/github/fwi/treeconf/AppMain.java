package com.github.fwi.treeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppMain {

	public static void main(String[] args) {
		SpringApplication.run(AppMain.class);
	}

	@Bean
	IceCream iceCream() {
		return new IceCream();
	}
}
