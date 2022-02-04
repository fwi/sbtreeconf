package com.github.fwi.sbtreeconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IceCreamConfig {

	@Bean
	IceCream iceCream() {
		return new IceCream();
	}
}
