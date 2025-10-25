package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
class WebErrorValidationTest extends WebTest {

	@Configuration
	@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		SecurityAutoConfiguration.class,
		ManagementContextAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
	})
	static class Config{
		@Bean WebErrorValidationController webErrorValidationController() { return new WebErrorValidationController(); }
		@Bean WebErrorResponse webErrorResponse() { return new WebErrorResponse(); }
	}

	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }

	@Test
	@SneakyThrows
	void validateOne() {

		log.debug("Testing validation");
		var ic1 = IceCreamRequest.builder().id(null).flavor("   ").shape("t").build();
		var validationError = given().contentType(JSON).body(ic1)
			.put(url() + "/one").then()
			//.log().all().and()
			.assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract().as(WebValidationError.class);
		
		assertThat(validationError.getErrors())
			.containsAnyOf(Map.of("parameter", "shape", "error", "size must be between 2 and 128"))
			.containsAnyOf(Map.of("parameter", "flavor", "error", "must not be blank"));
	}

	@Test
	@SneakyThrows
	void validateList() {

		log.debug("Testing validation");
		var ic1 = IceCreamRequest.builder().id(null).flavor("flavor").shape("t").build();
		var ic2 = IceCreamRequest.builder().id(null).flavor("    ").shape("test").build();
		var validationError = given().contentType(JSON).body(List.of(ic1, ic2))
			.put(url() + "/many").then()
			//.log().all().and()
			.assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract().as(WebValidationError.class);
		
		assertThat(validationError.getErrors())
			.containsAnyOf(Map.of("parameter", "iceCreamRequestList[0].shape", "error", "size must be between 2 and 128"))
			.containsAnyOf(Map.of("parameter", "iceCreamRequestList[1].flavor", "error", "must not be blank"));
	}

}
