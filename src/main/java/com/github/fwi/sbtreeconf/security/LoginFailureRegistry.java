package com.github.fwi.sbtreeconf.security;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LoginFailureRegistry {
	
	private final int maxFailedAttempts;
	private final Duration maxFailedTimeout;
	private final Duration blockedTimeout;
	
	private final Map<String, AtomicInteger> failedLogins = new ConcurrentHashMap<>();
	private final Map<String, LocalDateTime> failedLoginTimes = new ConcurrentHashMap<>();
	private final Map<String, LocalDateTime> blockedAccounts = new ConcurrentHashMap<>();

	public boolean isBlocked(String username) {
		
		// least costly operation first.
		if (blockedAccounts.get(username) == null) {
			return false;
		}
		// account is blocked unless timeout has expired.
		if (blockedAccounts.get(username).isBefore(LocalDateTime.now())) {
			removeFails(username);
			var blockEnd = blockedAccounts.remove(username);
			log.info("User {} no longer blocked after {}.", username, blockEnd);
			return false;
		}
		return true;
	}
	
	public void successLogin(String username) {
		removeFails(username);
	}
	
	public void failedLogin(String username) {

		// keep this method atomic (use "put/computeIfAbsent") in case multiple failed-login requests 
		// for the same user are handled at the same time.
		
		final var now = LocalDateTime.now();
		var lastFailTime = failedLoginTimes.computeIfAbsent(username, v -> now);
		
		// reset failed attempts if it was far in the past.
		
		int failedAttempts = 1;
		if (lastFailTime.isBefore(now.minus(maxFailedTimeout))) {
			failedLoginTimes.put(username, now);
			failedLogins.put(username, new AtomicInteger(1));
		} else {
			failedAttempts = failedLogins.computeIfAbsent(username, 
					v -> new AtomicInteger()).incrementAndGet();
		}

		if (failedAttempts >= maxFailedAttempts
				&& blockedAccounts.putIfAbsent(username, LocalDateTime.now().plus(blockedTimeout)) == null) {
			log.warn("User {} blocked until {} after {} failed login attempts.", username, blockedAccounts.get(username), failedAttempts);
		} else {
			log.info("Failed login attempt {} out of {} for user {} within {}.", failedAttempts, maxFailedAttempts, username, maxFailedTimeout);
		}
	}
	
	private void removeFails(String username) {
		
		// least costly operation first.
		if (failedLoginTimes.containsKey(username)) {
			// removing is more expensive (requires locks).
			failedLogins.remove(username);
			failedLoginTimes.remove(username);
		}
	}

}
