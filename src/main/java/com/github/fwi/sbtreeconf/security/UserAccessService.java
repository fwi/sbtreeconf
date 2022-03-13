package com.github.fwi.sbtreeconf.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Implement our own user-service.
 * The default one from Spring uses password encryption which is very slow 
 * (all requests take about 100ms just to authenticate).
 * 
 * If you do use encrypted passwords, make sure to cache "known good"
 * password hashes to prevent excessive CPU usage and delays.
 * A custom PasswordEncoder and/or AuthenticationManager will be required. 
 */
@Slf4j
public class UserAccessService implements UserDetailsService {
	
	/**
	 * Prefix for a permission that is a role.
	 */
	public static final String ROLE_PREFIX = "ROLE_";
	
	/**
	 * Do not do any password encryption / decryption.
	 */
	public static final String NOOP_ENCODER = "{noop}";
	
	final Map<String, AuthUser> users;
	final LoginFailureRegistry logins;
	
	public UserAccessService(
			Collection<IcreCreamAccessProperties.IceCreamUser> users, 
			LoginFailureRegistry loginFailureRegistry) {
		this.users = convert(users);
		this.logins = loginFailureRegistry;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		var user = users.get(username);
		if (user == null) {
			log.info("Unknown user [{}].", username);
			return null;
		}
		if (logins.isBlocked(username)) {
			log.debug("User {} blocked.", username);
			return null;
		}
		log.debug("Request from user {}.", username);
		// The password gets nullified after usage, so must create new User each time.
		return new User(user.getName(), user.getPassword(), user.getAuthorities());
	}

	protected Map<String, AuthUser> convert(Collection<IcreCreamAccessProperties.IceCreamUser> users) {
		
		var usersAuth = new HashMap<String, AuthUser>();
		if (users == null || users.isEmpty()) {
			log.warn("No users configured.");
			return usersAuth;
		}
		for (var user : users) {
			var authorities = toAuthorities(user.getRoles());
			if (authorities.isEmpty()) {
				log.warn("User {} has no roles.", user.getName());
			}
			usersAuth.put(user.getName(), 
					new AuthUser(user.getName(), NOOP_ENCODER + user.getPassword(), authorities));
		}
		return usersAuth;
	}
	
	Collection<SimpleGrantedAuthority> toAuthorities(Collection<String> roles) {
		
		var authorities = new HashSet<SimpleGrantedAuthority>();
		if (roles == null) {
			return authorities;
		}
		for (var authority: roles) {
			authority = StringUtils.trimToNull(authority);
			if (authority != null) {
				authority = ROLE_PREFIX + authority.toUpperCase(Locale.ENGLISH);
				authorities.add(new SimpleGrantedAuthority(authority));
			}
		}
		return authorities;
	}
	
	@Data
	@ToString
	@AllArgsConstructor
	public static class AuthUser {
		
		String name;
		@ToString.Exclude
		String password;
		Collection<SimpleGrantedAuthority> authorities;
	}

}
