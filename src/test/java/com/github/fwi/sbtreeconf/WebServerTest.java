package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.fwi.sbtreeconf.weberror.WebErrorDTO;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = WebServerConfig.class, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Slf4j
class WebServerTest extends WebTest {

	@Test
	@SneakyThrows
	void webRoot() {

		log.debug("Testing web-server up at {}", getServerUrl());
		var response = get(getServerUrl() + "/bla-not-here").then().assertThat()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract().as(WebErrorDTO.class);
		log.debug("Response: {}", response);
		assertThat(response.getError()).contains("Not Found");
	}

}
