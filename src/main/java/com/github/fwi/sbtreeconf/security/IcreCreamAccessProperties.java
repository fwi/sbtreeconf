package com.github.fwi.sbtreeconf.security;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.ToString;

@ConfigurationProperties(prefix = IcreCreamAccessProperties.USERS_PREFIX)
@Data
public class IcreCreamAccessProperties {
	
	public static final String USERS_PREFIX = "icecream.access";
	
	Collection<IceCreamUser> users;
	LoginFailure login;

	@Data
	@ToString
	public static class IceCreamUser {
		
		String name;
		@ToString.Exclude
		String password;
		Collection<String> roles = new HashSet<>();
	}

	@Data
	@ToString
	public static class LoginFailure {
		
		int maxFailedAttempts;
		Duration maxFailedTimeout;
		Duration blockedTimeout;
	}

}
