package com.github.fwi.sbtreeconf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
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
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
public class IceCreamMockTest {
	
	@Configuration
	@Import({
		WebServerConfig.class,
		IceCreamConfig.class
	})
	static class Config {
		
		@MockBean
		IceCreamRepo icRepo;
	}

	@MockBean
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
		// but MockMvc picks up on the Secured annotation anyway.
		RestAssuredMockMvc.get(IceCreamController.BASE_PATH + "/1")
			.then().statusCode(HttpStatus.UNAUTHORIZED.value());
		
		var iceCream = IceCreamResponse.builder().flavor("mock").shape("mvc").build();
		Mockito.when(icService.findOne(1)).thenReturn(iceCream);
		var user = SecurityMockMvcRequestPostProcessors.user("reader").roles("READ");
		var iceCream1 = RestAssuredMockMvc
			.given().auth().with(user)
			.get(IceCreamController.BASE_PATH + "/1")
			.then().statusCode(HttpStatus.OK.value())
			.extract().as(IceCreamResponse.class);
		
		assertThat(iceCream1).isEqualTo(iceCream);
	}

}
