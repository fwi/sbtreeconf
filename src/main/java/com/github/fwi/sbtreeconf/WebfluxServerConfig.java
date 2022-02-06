package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.context.annotation.Configuration;

// Option 2: reactive
// Requires a different implementation for WebErrorResponse,
// so for now this cannot work.

@Configuration
@ImportAutoConfiguration({
	
	ReactiveWebServerFactoryAutoConfiguration.class,
	HttpHandlerAutoConfiguration.class,

	// Setup JSON conversions.
	JacksonAutoConfiguration.class,
	// For WebSockets:
	// CodecsAutoConfiguration.class,
	ErrorWebFluxAutoConfiguration.class,
	WebFluxAutoConfiguration.class,
})
public class WebfluxServerConfig {
	
}
