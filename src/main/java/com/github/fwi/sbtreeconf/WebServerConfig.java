package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
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
	// Glue JSON configuration to RestController
	// Among other things, converts LocalDateTime to String in web-response.
	HttpMessageConvertersAutoConfiguration.class,
	// For WebSockets:
	// CodecsAutoConfiguration.class,
	ErrorMvcAutoConfiguration.class,
	// Enable generic web-functions.
	WebMvcAutoConfiguration.class,
})
public class WebServerConfig {
	
}
