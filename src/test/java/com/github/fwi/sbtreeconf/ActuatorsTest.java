package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.common.mapper.TypeRef;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(
	classes = {WebServerConfig.class, ActuatorsConfig.class}, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@EnableAutoConfiguration(exclude = {
	DataSourceAutoConfiguration.class,
	SecurityAutoConfiguration.class,
	ManagementWebSecurityAutoConfiguration.class
})
@ActiveProfiles("test")
@Slf4j
class ActuatorsTest {
	
	@LocalManagementPort
	int port;

	String url() {
		return "http://localhost:" + port + "/actuator";
	}

	@Test
	void health() {
		
		log.debug("Testing actuator health");
		final var textMap = new TypeRef<Map<String, Object>>() {};

		var actuators = get(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();
		log.trace("Actuators online:\n{}", actuators);

		var health = get(url() + "/health").then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(textMap);
		
		assertThat(health).contains(Map.entry("status", "UP"));

		var info = get(url() + "/info").then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(textMap);

		var buildInfo = fullTextMap(info.get("build")); 
		assertThat(buildInfo).contains(Map.entry("artifact", "sbtreeconf"));

		var metric = get(url() + "/metrics/system.cpu.count").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(textMap);
		
		var cpuCount = metric.get("name");
		assertThat(cpuCount).isEqualTo("system.cpu.count");
		
		// Prometheus is not activated in test environment.
		get(url() + "/prometheus").then().assertThat()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.extract().asString();	
	}
	
	@SuppressWarnings("unchecked")
	Map<String, String> fullTextMap(Object o) {
		return (Map<String, String>) o;
	}
	
}
