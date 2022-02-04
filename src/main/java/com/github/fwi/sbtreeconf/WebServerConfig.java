package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
	// Starts a Tomcat server.
	ServletWebServerFactoryAutoConfiguration.class,
	// Enables rest-controller functions.
	DispatcherServletAutoConfiguration.class,
	// Provides object to JSON vconversion for controller
	WebMvcAutoConfiguration.class,
})
public class WebServerConfig {
	
}
