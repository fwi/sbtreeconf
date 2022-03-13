package com.github.fwi.sbtreeconf.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent>{
	
	final LoginFailureRegistry accessRegistry;

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		accessRegistry.failedLogin(event.getAuthentication().getName());
	}

}
