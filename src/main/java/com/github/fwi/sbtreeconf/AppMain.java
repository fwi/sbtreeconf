package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.fwi.sbtreeconf.db.DbConfig;

@Configuration
@Import({
	AppBootConfig.class,
	WebServerConfig.class, 
	DbConfig.class,
	IceCreamConfig.class,
})
// For testing and viewing any auto-configuration that Spring Boot pulls in as reported via "AcReport":
// @org.springframework.boot.autoconfigure.EnableAutoConfiguration
public class AppMain {

	public static void main(String[] args) {
		SpringApplication.run(AppMain.class, args);
	}
	
	@Bean
	AcReport acReport() {
		return new AcReport();
	}

}
