package com.github.fwi.sbtreeconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IceCreamConfig {

	@Bean
	IceCreamService iceCreamService() {
		return new IceCreamService();
	}

	@Bean
	IceCreamController iceCreamController() {
		return new IceCreamController(iceCreamService());
	}
}
