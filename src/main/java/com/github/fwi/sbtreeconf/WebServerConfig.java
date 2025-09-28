package com.github.fwi.sbtreeconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfig {

	@Bean
	HomeController homeController() {
		return new HomeController();
	}

	@Bean
	WebErrorResponse webErrorResponse() {
		return new WebErrorResponse();
	}

}
