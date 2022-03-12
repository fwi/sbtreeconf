package com.github.fwi.sbtreeconf.security;

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
	
	@Data
	@ToString
	public static class IceCreamUser {
		
		String name;
		@ToString.Exclude
		String password;
		Collection<String> roles = new HashSet<>();
	}

}
