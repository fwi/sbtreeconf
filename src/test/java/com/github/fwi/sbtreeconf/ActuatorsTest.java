package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.common.mapper.TypeRef;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = { WebServerConfig.class, ActuatorsConfig.class }, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
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
		
		assertThat(health.get("status")).isEqualTo("UP");

		var info = get(url() + "/info").then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(textMap);

		var buildInfo = fullTextMap(info.get("build")); 
		assertThat(buildInfo).isNotNull();
		assertThat(buildInfo.get("artifact")).isEqualTo("sbtreeconf");

		var metric = get(url() + "/metrics/system.cpu.count").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(textMap);
		
		var cpuCount = metric.get("name");
		assertThat(cpuCount).isNotNull();
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
