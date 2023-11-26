package com.github.fwi.sbtreeconf;

import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.fwi.sbtreeconf.weberror.WebErrorResponse;
import com.github.fwi.sbtreeconf.weberror.WebErrorController;

// Option 1: webmvc

@Configuration
@ImportAutoConfiguration({
		// Starts a Tomcat server.
		ServletWebServerFactoryAutoConfiguration.class,
		// Enables rest-controller functions.
		DispatcherServletAutoConfiguration.class,
		// Setup JSON conversions.
		JacksonAutoConfiguration.class,
		// Glue JSON configuration to RestController.
		// Among other things, converts LocalDateTime to String in web-response.
		HttpMessageConvertersAutoConfiguration.class,
		// For WebSockets:
		// CodecsAutoConfiguration.class,
		ErrorMvcAutoConfiguration.class,
		// Enable generic web-functions.
		WebMvcAutoConfiguration.class,
})
public class WebServerConfig {

	/**
	 * To enable virtual threads usage within Tomcat,
	 * update the "protocol handler" to use virtual threads.
	 * TODO: the actuator endpoints still use platform threads.
	 */
	@Bean
	public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
		return protocolHandler -> {
			protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
		};
	}

	@Bean
	WebErrorResponse webErrorResponse() {
		return new WebErrorResponse();
	}

	@Bean
	WebErrorController webErrorController() {
		return new WebErrorController();
	}

	@Bean
	HomeController homeController() {
		return new HomeController();
	}

}
