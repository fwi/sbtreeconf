package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.fwi.sbtreeconf.db.DbConfig;

@Configuration
@Import({
	AppBootConfig.class,
	WebServerConfig.class, 
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
	
	/**
	 * Add some information to actuator info-endpoint.
	 */
	
	@Bean
	public InfoContributor infoContributor() {
		return new InfoContributor() {
			@Override
			public void contribute(Builder builder) {
				builder.withDetail("spring-boot.version", SpringBootVersion.getVersion());
			}
		};
	}
	

}
