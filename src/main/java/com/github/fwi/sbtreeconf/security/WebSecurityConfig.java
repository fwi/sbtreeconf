package com.github.fwi.sbtreeconf.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties(IcreCreamAccessProperties.class)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class WebSecurityConfig {
	
	/**
	 * Required user role for actuator endpoints except for "/actuator/health"
	 */
	public static final String ROLE_MANAGE = "MANAGE";
	
	/**
	 * Home ("/") redirects to apidocs at "/docs/index.html".
	 * Both will be accessible without authentication.
	 */
	protected static final String[] STATIC_RESOURCES = new String[] {
			"/", "/thread", "/docs/**"
	};

	@Bean
	SecurityFilterChain secure(HttpSecurity http) throws Exception {
		//@formatter:off
		return http
			.csrf(c -> c.disable())
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

}
