package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.github.fwi.sbtreeconf.db.DbConfig;
import com.github.fwi.sbtreeconf.security.WebSecurityConfig;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({
	WebServerConfig.class,
	WebSecurityConfig.class,
	DbConfig.class,
	IceCreamConfig.class,
	ActuatorsConfig.class,
})
public class AppMain {

	public static void main(String[] args) {
		SpringApplication.run(AppMain.class, args);
	}

}
