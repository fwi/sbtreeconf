package com.github.fwi.sbtreeconf;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.fwi.sbtreeconf.db.IceCreamRepo;

@Configuration
public class IceCreamConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	IceCreamService iceCreamService(IceCreamRepo iceCreamRepo, ModelMapper modelMapper) {
		return new IceCreamService(iceCreamRepo, modelMapper);
	}

	@Bean
	IceCreamController iceCreamController(IceCreamService iceCreamService) {
		return new IceCreamController(iceCreamService);
	}
	
}
