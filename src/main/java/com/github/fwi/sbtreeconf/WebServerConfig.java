package com.github.fwi.sbtreeconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.fwi.sbtreeconf.weberror.WebErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fwi.sbtreeconf.weberror.WebErrorController;

// TODO: the actuator endpoints still use platform threads?
// Replace error controller with problemdetails.
@Configuration
public class WebServerConfig {

	@Bean
	WebErrorResponse webErrorResponse() {
		return new WebErrorResponse();
	}

	@Bean
	WebErrorController webErrorController(ObjectMapper mapper) {
		return new WebErrorController(mapper);
	}

	@Bean
	HomeController homeController() {
		return new HomeController();
	}

}
