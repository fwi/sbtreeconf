package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.fwi.sbtreeconf.db.DbConfig;
import com.github.fwi.sbtreeconf.security.WebSecurityConfig;

import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = {WebServerConfig.class, WebSecurityConfig.class, DbConfig.class, IceCreamConfig.class}, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Slf4j
public class IceCreamSecurityTest extends WebTest {

	public static final String JSON = "application/json";
	
	@Autowired
	ModelMapper mapper;
	
	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }
	
	RequestSpecification authRead() {
		return given().auth()
		  .preemptive()
		  .basic("reader", "reads");
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

	@Test
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
		given().auth()
		  .preemptive()
		  .basic("reader", "1234")
		  .get(url()).then().assertThat()
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

}
