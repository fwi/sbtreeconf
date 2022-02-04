package com.github.fwi.sbtreeconf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Import(JdbcTemplateAutoConfiguration.class)
public class IceCreamConfig {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Bean
	IceCreamService iceCreamService() {
		return new IceCreamService(jdbcTemplate);
	}

	@Bean
	IceCreamController iceCreamController() {
		return new IceCreamController(iceCreamService());
	}
}
