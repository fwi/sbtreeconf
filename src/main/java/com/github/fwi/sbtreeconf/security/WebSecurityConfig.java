package com.github.fwi.sbtreeconf.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableConfigurationProperties(IcreCreamAccessProperties.class)
@ImportAutoConfiguration({
	SecurityAutoConfiguration.class,
})
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
	
	/**
	 * Required user role for actuator endpoints except for "/actuator/health"
	 */
	public static final String ROLE_MANAGE = "MANAGE";
	
	/**
	 * Home ("/") redirects to apidocs at "/docs/index.html".
	 * Both will be accessible without authentication.
	 */
	public static final String[] STATIC_RESOURCES = new String[] {
			"/", "/docs/**"
	};

	// Security was updated with Spring Boot 2.7:
	// https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
	
	@Bean
	SecurityFilterChain secure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.authorizeRequests((requests) -> {
			requests.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
			requests.antMatchers(STATIC_RESOURCES).permitAll();
			
			requests.requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll();
			requests.requestMatchers(EndpointRequest.toAnyEndpoint()
					.excluding(HealthEndpoint.class)).hasRole(ROLE_MANAGE);
			
			requests.anyRequest().authenticated();
		});
		http.httpBasic(Customizer.withDefaults());
		return http.build();
	}
	
	@Bean
	UserDetailsService access(IcreCreamAccessProperties access) {
		return new UserAccessService(access.getUsers());
	}
	
	@Bean
	public HttpFirewall httpFirewall() {
		
		/*
		 * Values often contains special characters, these need to be allowed.
		 */
		var firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true);
		firewall.setAllowUrlEncodedPeriod(true);
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}
	
	/**
	 * Prevent errors in log from bad requests (e.g. containing "//") as detected by the firewall.
	 */
	@Bean
	RequestRejectedHandler requestRejectedHandler() {
	   // sends an error response with a configurable status code (default is 400 BAD_REQUEST)
	   // we can pass a different value in the constructor
	   return new HttpStatusRequestRejectedHandler();
	}

}
