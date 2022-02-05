package com.github.fwi.sbtreeconf;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.fwi.sbtreeconf.db.IceCreamRepo;
import com.github.fwi.sbtreeconf.weberror.WebErrorResponse;

@Configuration
@ImportAutoConfiguration( {
	ValidationAutoConfiguration.class,
})
public class IceCreamConfig {

	@Autowired
	IceCreamRepo iceCreamRepo;
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	IceCreamService iceCreamService() {
		return new IceCreamService(iceCreamRepo, modelMapper());
	}

	@Bean
	WebErrorResponse webErrorResponse() { 
		return new WebErrorResponse(); 
	}
	
	@Bean
	IceCreamController iceCreamController() {
		return new IceCreamController(iceCreamService());
	}
	
}
