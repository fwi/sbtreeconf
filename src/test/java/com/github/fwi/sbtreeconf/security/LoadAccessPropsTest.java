package com.github.fwi.sbtreeconf.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = LoadAccessPropsTest.Config.class
)
@ActiveProfiles("test")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class LoadAccessPropsTest {
	
	@EnableConfigurationProperties(IcreCreamAccessProperties.class)
	static class Config {}
	
	final IcreCreamAccessProperties props;
	
	@Test
	void users() {
		log.debug("Users: {}", props.getUsers());
		assertThat(props.getUsers()).isNotEmpty();
		log.debug("Logins: {}", props.getLogin());
		assertThat(props.getLogin()).isNotNull();
		assertThat(props.getLogin().getMaxFailedAttempts()).isGreaterThan(0);
		assertThat(props.getLogin().getBlockedTimeout()).isNotNull();
	}

}
