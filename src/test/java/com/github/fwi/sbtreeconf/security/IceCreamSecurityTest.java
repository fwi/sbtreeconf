package com.github.fwi.sbtreeconf.security;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.fwi.sbtreeconf.IceCreamController;
import com.github.fwi.sbtreeconf.IceCreamRequest;
import com.github.fwi.sbtreeconf.WebTest;

import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = WebTest.Config.class, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class IceCreamSecurityTest extends WebTest {

	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }
	
	static final String READER = "reader";
	
	RequestSpecification authRead() {
		return given().auth()
		  .preemptive()
		  .basic(READER, "reads");
	}
	
	RequestSpecification authWrite() {
		return given().auth()
		  .preemptive()
		  .basic("writer", "writes");
	}

	RequestSpecification authDelete() {
		return given().auth()
		  .preemptive()
		  .basic("operator", "operates");
	}

	RequestSpecification authBadCred() {
		return given().auth()
		  .preemptive()
		  .basic(READER, "read");
	}
	
	@Autowired
	IcreCreamAccessProperties accessProps;

	@Autowired
	LoginFailureRegistry logins;

	@Test
	@Order(1)
	@SneakyThrows
	void iceCreamSecurity() {
		
		log.debug("Testing icecream web-security.");
		
		// READ
		// Security for READ access is set at class-level.
		// No need to test all the methods.
		get(url()).then().assertThat()
			.statusCode(HttpStatus.UNAUTHORIZED.value());
		authRead().get(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());
		authWrite().get(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());
		// Invalid password test
		authBadCred().get(url()).then().assertThat()
			.statusCode(HttpStatus.UNAUTHORIZED.value());

		// WRITE
		
		var newIceCream = IceCreamRequest.builder()
				.id(null).flavor("Neapolitan").shape("waffle").build();

		authRead().contentType(JSON).body(newIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.UNAUTHORIZED.value());
		authWrite().contentType(JSON).body(newIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		// DELETE
		
		authWrite().delete(url() + "/1").then().assertThat()
			.statusCode(HttpStatus.UNAUTHORIZED.value());
		authDelete().delete(url() + "/1").then().assertThat()
			.statusCode(HttpStatus.OK.value());
	}
	
	@Test
	@Order(1000)
	/* Test blocked user, do as last test. */
	void blockSecurity() {
		
		IntStream.range(0, accessProps.getLogin().getMaxFailedAttempts()).forEach(i ->
			authBadCred().get(url()).then().assertThat()
				.statusCode(HttpStatus.UNAUTHORIZED.value())
		);
		assertThat(logins.isBlocked(READER)).isTrue();
	}

}
