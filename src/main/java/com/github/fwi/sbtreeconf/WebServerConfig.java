package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
	// Starts a Tomcat server.
	ServletWebServerFactoryAutoConfiguration.class,
	// Enables rest-controller functions.
	DispatcherServletAutoConfiguration.class,
})
public class WebServerConfig {
	
}
