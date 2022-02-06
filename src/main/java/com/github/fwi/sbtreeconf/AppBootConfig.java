package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Import auto-configurations reported by AcReport as
 * "Beans used without conditions".
 */
@Configuration
@ImportAutoConfiguration({
	
	// Provides ApplicationAvailabilityBean which might be used by actuators and such.
	org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration.class,
	
	// Use EnableConfigurationProperties directly with class-argument.
	// org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
	
	// spring.lifecycle configure shutdown time-out.
	org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration.class,
	
	// replaces ${..} in for example $Value annotations.
	org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration.class,
	
	// For actuators, loads ${spring.info.build.location:classpath:META-INF/build-info.properties} and Git info.
	// use "build-info" goal in spring-boot-maven-plugin
	org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration.class
})
public class AppBootConfig {

}
