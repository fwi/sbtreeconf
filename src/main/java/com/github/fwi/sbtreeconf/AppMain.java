package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
	WebServerConfig.class, 
	IceCreamConfig.class,
})
public class AppMain {

	public static void main(String[] args) {
		SpringApplication.run(AppMain.class);
	}

}
