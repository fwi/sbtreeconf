package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.fwi.sbtreeconf.db.DbConfig;

@Configuration
@Import({
	WebServerConfig.class, 
	DbConfig.class,
	IceCreamConfig.class,
})
public class AppMain {

	public static void main(String[] args) {
		SpringApplication.run(AppMain.class, args);
	}

}
