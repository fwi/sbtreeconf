package com.github.fwi.sbtreeconf.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

/**
 * Test the (un)blocking of users after failed logins.
 */
class LoginFailureTest {
	
	/*
	 * This test uses small wait-times which in turn could fail the test
	 * on slower build-machines.
	 */
	
	LoginFailureRegistry testInstance() {
		return new LoginFailureRegistry(3, Duration.ofMillis(150), Duration.ofMillis(260));
	}
	
	@Test
	void failedLogins() {
		
		var registry = testInstance();
		final String user = "name";
		
		// user gets blocked after 3 attempts
		registry.failedLogin(user);
		registry.failedLogin(user);
		assertThat(registry.isBlocked(user)).isFalse();
		registry.failedLogin(user);
		assertThat(registry.isBlocked(user)).isTrue();
		sleep(261);
		assertThat(registry.isBlocked(user)).isFalse();
		
		// failed login resets after failed-timeout.
		registry.failedLogin(user);
		registry.failedLogin(user);
		sleep(151);
		registry.failedLogin(user);
		registry.failedLogin(user);
		assertThat(registry.isBlocked(user)).isFalse();
	}
	
	void sleep(long timeMs) {
		try { Thread.sleep(timeMs); } catch (Exception e) { // NOSONAR
			// ignored
		}
	}

}
