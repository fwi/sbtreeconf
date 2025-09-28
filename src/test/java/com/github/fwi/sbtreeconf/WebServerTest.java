package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Example of a test that starts the application without the database and security layer
 * by excluding the auto-configuration for both.
 */
@SpringBootTest(
	classes = WebServerConfig.class, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@EnableAutoConfiguration(exclude = {
	DataSourceAutoConfiguration.class,
	SecurityAutoConfiguration.class,
	ManagementContextAutoConfiguration.class,
	ManagementWebSecurityAutoConfiguration.class
})
@ActiveProfiles("test")
@Slf4j
class WebServerTest extends WebTest {

	@Test
	@SneakyThrows
	void webRoot() {

		log.debug("Testing web-server up at {}", getServerUrl());
		var response = get(getServerUrl() + "/bla-not-here").then().assertThat()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract().as(ProblemDetail.class);
		log.debug("Response: {}", response);
		assertThat(response.getTitle()).isEqualTo("Not Found");
	}

}
