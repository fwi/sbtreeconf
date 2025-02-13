package com.github.fwi.sbtreeconf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.github.fwi.sbtreeconf.db.IceCreamRepo;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.extern.slf4j.Slf4j;

/**
 * Just a demo-test showing usage of "MockMvc".
 * Requires dependency io.rest-assured:spring-mock-mvc
 * and org.springframework.security:spring-security-test
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = IceCreamMockTest.Config.class 
)
// @org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
// Will import the following (from spring.factories in spring-boot-test-autoconfigure):
/*
# AutoConfigureMockMvc auto-configuration imports
org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc=\
org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration,\
org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebClientAutoConfiguration,\
org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebDriverAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityConfiguration,\
org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration
 */
@ActiveProfiles("test")
@Slf4j
@MockitoBean(types = {IceCreamRepo.class})
class IceCreamMockTest {
	
	@Configuration
	@Import({
		WebServerConfig.class,
		IceCreamConfig.class
	})
	@ImportAutoConfiguration({
		MockMvcAutoConfiguration.class,
		SecurityAutoConfiguration.class,
	})
	static class Config {}

	@MockitoBean
	IceCreamService icService;

	@Autowired
	MockMvc mockMvc;
	
	@BeforeEach
	void setup() {
		RestAssuredMockMvc.mockMvc(mockMvc);
	}
	
	@Test
	void mockMyDay() {
		
		log.debug("Starting mock test.");
		
		// WebSecurityConfig was not loaded by Config above,
		// but SecurityAutoConfiguration was and that sets a minimum security.
		
		RestAssuredMockMvc.get(IceCreamController.BASE_PATH + "/1")
			.then().statusCode(HttpStatus.UNAUTHORIZED.value());
		
		var iceCream = IceCreamResponse.builder().flavor("mock").shape("mvc").build();
		Mockito.when(icService.findOne(1)).thenReturn(iceCream);
		
		// Actual values do not matter, as long as there is a user.
		var user = SecurityMockMvcRequestPostProcessors.user("reader").roles("READ");
		
		var iceCream1 = RestAssuredMockMvc
			.given().auth().with(user)
			.get(IceCreamController.BASE_PATH + "/1")
			.then().statusCode(HttpStatus.OK.value())
			.extract().as(IceCreamResponse.class);
		
		assertThat(iceCream1).isEqualTo(iceCream);
	}

}
