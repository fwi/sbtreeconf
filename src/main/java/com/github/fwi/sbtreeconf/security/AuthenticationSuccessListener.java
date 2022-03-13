package com.github.fwi.sbtreeconf.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent>{
	
	final LoginFailureRegistry accessRegistry;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		accessRegistry.successLogin(event.getAuthentication().getName());
	}

}
