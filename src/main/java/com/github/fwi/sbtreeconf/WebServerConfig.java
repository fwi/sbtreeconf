package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({
	// Starts a Tomcat server.
	ServletWebServerFactoryAutoConfiguration.class,
	// Enables rest-controller functions.
	DispatcherServletAutoConfiguration.class,
	// Setup JSON conversions
	JacksonAutoConfiguration.class,
	ErrorMvcAutoConfiguration.class,
	// Provides object to JSON conversion for controller
	WebMvcAutoConfiguration.class,
})
public class WebServerConfig {
	
}
