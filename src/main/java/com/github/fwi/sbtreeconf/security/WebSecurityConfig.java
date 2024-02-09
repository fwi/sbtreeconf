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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
@EnableMethodSecurity(securedEnabled = true)
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
			"/", "/thread", "/docs/**"
	};

	@Bean
	SecurityFilterChain secure(HttpSecurity http) throws Exception {
		//@formatter:off
		return http
			.csrf(c -> c.disable())
			// TODO: cors gives a warning log about caching, disable the warn-log?
			// Cache miss for REQUEST dispatch to '/api/v1/icecream/1' (previous null). Performing CorsConfiguration lookup. This is logged once only at WARN level, and every time at TRACE
			// Disabling cors does not look like a good idea.
			//.cors(c -> c.disable())
			.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(c -> c
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers(STATIC_RESOURCES).permitAll()
				
				.requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
				.requestMatchers(EndpointRequest.toAnyEndpoint()
						.excluding(HealthEndpoint.class)).hasRole(ROLE_MANAGE)
				
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults())
			.build();
		//@formatter:on
	}
	
	@Bean
	UserAccessService userAccessService(IcreCreamAccessProperties access, LoginFailureRegistry loginFailureRegistry) {
		return new UserAccessService(access.getUsers(), loginFailureRegistry);
	}

	@Bean
	LoginFailureRegistry loginFailureRegistry(IcreCreamAccessProperties access) {
		return new LoginFailureRegistry(
				access.getLogin().getMaxFailedAttempts(),
				access.getLogin().getMaxFailedTimeout(),
				access.getLogin().getBlockedTimeout()
				);
	}

	@Bean
	AuthenticationFailureListener authenticationFailureListener(LoginFailureRegistry loginFailureRegistry) {
		return new AuthenticationFailureListener(loginFailureRegistry);
	}

	@Bean
	AuthenticationSuccessListener authenticationSuccessListener(LoginFailureRegistry loginFailureRegistry) {
		return new AuthenticationSuccessListener(loginFailureRegistry);
	}

	@Bean
	HttpFirewall httpFirewall() {
		
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
