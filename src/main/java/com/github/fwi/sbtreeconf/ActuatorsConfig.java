package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

import io.micrometer.observation.ObservationPredicate;
import lombok.extern.slf4j.Slf4j;

// TODO: the actuator endpoints still use platform threads?
@Configuration
@Slf4j
public class ActuatorsConfig {

	public static final String HTTP_SERVER_REQUESTS = "http.server.requests";

	/**
	 * Disable observation (metrics) for actuator endpoints.
	 *
	 * NOTE: for actuator endpoints, prometheus reports no "http_server_requests" metrics
	 * when management.server.port is different from main app port.
	 * The issue also shows when Spring Boot autoconfiguration is fully enabled.
	 * 
	 * See also https://github.com/spring-projects/spring-boot/issues/34801
	 */
	@Bean
	ObservationPredicate actuatorServerContextPredicate(PathMappedEndpoints pme) {
		log.info("Disabling actuator observations for {} at {}", HTTP_SERVER_REQUESTS, pme.getBasePath());
		return (name, context) -> {
			if (name.equals(HTTP_SERVER_REQUESTS)
					&& context instanceof ServerRequestObservationContext serverContext) {
				return !serverContext.getCarrier().getRequestURI().startsWith(pme.getBasePath());
			}
			return true;
		};
	}

	/**
	 * Add some information to actuator info-endpoint.
	 * 
	 * When this is added to {@link AppMain}, the application fails to start.
	 */
	@Bean
	public InfoContributor infoContributor() {
		return builder -> builder.withDetail("spring-boot.version", SpringBootVersion.getVersion());
	}

}
