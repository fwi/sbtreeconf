package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.web.filter.ServerHttpObservationFilter;

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
})
public class AppBootConfig {

	ServerHttpObservationFilter x;

	/**
	 * To enable virtual threads usage for async Spring Boot tasks,
	 * use a virtual thread per task.
	 * TODO: demonstrate this bean and virtual tasks are used.
	 */
	@Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	public AsyncTaskExecutor asyncTaskExecutor() {
		return new VirtualThreadTaskExecutor("vt");
	}

}
