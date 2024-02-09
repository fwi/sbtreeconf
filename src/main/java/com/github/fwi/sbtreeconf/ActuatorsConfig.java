package com.github.fwi.sbtreeconf;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.autoconfigure.availability.AvailabilityHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.availability.AvailabilityProbesAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.JvmMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.LogbackMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.task.TaskExecutorMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.observation.web.servlet.WebMvcObservationAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.observation.ServerRequestObservationContext;

import io.micrometer.observation.ObservationPredicate;
import lombok.extern.slf4j.Slf4j;

/**
 * Enable actuator support with manual AutoConfiguration.
 * One of two webservers can be used:
 * <br> - Option 1: webmvc (Tomcat), dependency spring-boot-starter-web
 * <br> - Option 2: reactive (Netty), dependency spring-boot-starter-webflux
 * <br> Only one of the two options can be used and only one of the two dependencies can be used.
 * <p>
 * Related: https://stackoverflow.com/questions/58031056/problem-with-spring-actuator-metrics-without-enableautoconfiguration
 */

@Configuration
@Import({
	
	// Add some more application availability under actuator/health,
	// nice to have.
	ApplicationAvailabilityAutoConfiguration.class,
	AvailabilityProbesAutoConfiguration.class,
	AvailabilityHealthContributorAutoConfiguration.class,
	
	// Add the health/ping endpoint which always reports status up.
	HealthContributorAutoConfiguration.class,

	// Add some data to the info-endpoint.
	// ProjectInfo has to be before InfoContributor.
	// Loads ${spring.info.build.location:classpath:META-INF/build-info.properties} and Git info.
	// use "build-info" goal in spring-boot-maven-plugin
	ProjectInfoAutoConfiguration.class,
	InfoContributorAutoConfiguration.class,
	
	// Enabling and wiring metrics and endpoints.
	
	MetricsAutoConfiguration.class,
	
	// Note: the order IS important here, 
	// Prometheus must be configured before the other autoconfigures below.
	// If the proper order is not maintained all 
	// xyzMetricsAutoConfiguration classes further on will not initialize. 
	PrometheusMetricsExportAutoConfiguration.class,
	
	// Use as fallback, e.g. when PrometheusMetrics above is not used
	SimpleMetricsExportAutoConfiguration.class,
	
	// CompositeMeter appears to be not needed:
	// CompositeMeterRegistryAutoConfiguration.class,
	
	EndpointAutoConfiguration.class,
	WebEndpointAutoConfiguration.class,
	
	// in case management.server.port is set.
	ServletManagementContextAutoConfiguration.class,
	// For webflux:
	// ReactiveManagementContextAutoConfiguration.class,
	ManagementContextAutoConfiguration.class, 
	// Requires web-security dependency:
	// ManagementWebSecurityAutoConfiguration.class,
	
	// Metrics to collect 
	
	JvmMetricsAutoConfiguration.class,
	SystemMetricsAutoConfiguration.class,
	LogbackMetricsAutoConfiguration.class,
	TaskExecutorMetricsAutoConfiguration.class,
	
	TomcatMetricsAutoConfiguration.class,
	
	// Enable "http_server_requests_seconds" metrics in prometheus endpoint.
	ObservationAutoConfiguration.class,
	WebMvcObservationAutoConfiguration.class,
	// For webflux:
	// WebFluxObservationAutoConfiguration.class,
	
	// Actuator endpoints to enable.
	// Class names look like names from https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.exposing
	// except for Prometheus which is "special".
	HealthEndpointAutoConfiguration.class,
	InfoEndpointAutoConfiguration.class,
	MetricsEndpointAutoConfiguration.class,
	// this is a potentially dangerous one to use/expose.
	// EnvironmentEndpointAutoConfiguration.class, 
})
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
	 */
	@Bean
	public InfoContributor infoContributor() {
		return new InfoContributor() {
			@Override
			public void contribute(Builder builder) {
				builder.withDetail("spring-boot.version", SpringBootVersion.getVersion());
			}
		};
	}

}
