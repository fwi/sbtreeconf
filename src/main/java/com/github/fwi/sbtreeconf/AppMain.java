package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.fwi.sbtreeconf.db.DbConfig;
import com.github.fwi.sbtreeconf.security.WebSecurityConfig;

@Configuration
@Import({
	AppBootConfig.class,
	WebServerConfig.class,
	WebSecurityConfig.class,
	// Does not work for now, see comments in config-class.
	// WebfluxServerConfig.class,
	DbConfig.class,
	IceCreamConfig.class,
	ActuatorsConfig.class,
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

	/*
	 * Bean definition for InfoContributor used to be here,
	 * but that woulld fail to start app from jar with exception
	 * "NoClassDefFoundError". Moved InfoContributor to the
	 * ActuatorsConfig class and error went away.
	 */

}
