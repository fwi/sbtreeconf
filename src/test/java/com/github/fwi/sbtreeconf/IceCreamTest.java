package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.common.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@DirtiesContext
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class IceCreamTest extends WebTest {

	static final TypeRef<List<IceCreamResponse>> icList = new TypeRef<>() {};

	final ModelMapper mapper;
	
	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }

	RequestSpecification givenUser() {
		return given().auth()
		  .preemptive()
		  .basic(USER, USER_PASS);
	}

	@Test
	@Order(1)
	@SneakyThrows
	void readIceCream() {
		
		log.debug("Testing icecream web-access.");
		var response = givenUser().get(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(icList);

		assertThat(response).as("All ice-creams.").hasSize(3);
		assertThat(response).as("Have special flavor")
			.filteredOn(i -> i.getFlavor().equals("Neapolitan")).isNotEmpty();
		
		var count = givenUser()
				.queryParam("flavor", "vanilla")
				.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(Long.class);

		assertThat(count).isEqualTo(2);

		var errorDto = givenUser()
				.queryParam("flavor", "")
				.get(url() + IceCreamController.COUNT_PATH).then()
				//.log().all().and()
				.assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract().as(ProblemDetail.class);

		assertThat(errorDto.getDetail()).contains("Flavor must have a value");
		
		var none = givenUser().get(url() + "/0").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();

		assertThat(none).isEmpty();

		var one = givenUser().get(url() + "/1").then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(IceCreamResponse.class);
		log.debug("One: {}", one);

		assertThat(one.getId()).isEqualTo(1);
	}
	
	@Test
	@Order(2)
	@SneakyThrows
	void writeIceCream() {

		log.debug("Testing icecream storage.");
		
		final var oldDate = Instant.parse("2022-02-05T21:00:00Z");
		// Providing a modified date does not give an error,
		// the value is just ignored.
		var newIceCream = IceCreamRequest.builder()
				.id(null).flavor("Neapolitan").shape("waffle").build();
		var inserted = givenUser().contentType(JSON).body(newIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(IceCreamResponse.class);

		log.debug("Created ice-cream {}", inserted);
		assertThat(inserted.getId()).isPositive();
		assertThat(inserted).extracting(
				IceCreamResponse::getFlavor,
				IceCreamResponse::getShape
				).containsExactly("Neapolitan", "waffle");
		assertThat(inserted).extracting(
				IceCreamResponse::getCreated,
				IceCreamResponse::getModified
				).doesNotContainNull();
		assertThat(oldDate).isBefore(inserted.getModified());
		
		var updateIceCream = IceCreamRequest.builder().id(1L).flavor("vanilla").shape("sandwich").build();
		log.debug("Update ice-cream {}", updateIceCream);
		var updated = givenUser().contentType(JSON).body(updateIceCream)
				.put(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(IceCreamResponse.class);
			
		assertThat(updated).extracting(
				IceCreamResponse::getId, 
				IceCreamResponse::getFlavor,
				IceCreamResponse::getShape
				).containsExactly(1l, "vanilla", "sandwich");

		var negative = inserted.toBuilder().id(-1L).build();
		var none = givenUser().contentType(JSON).body(negative)
				.put(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();
		
		assertThat(none).isEmpty();
		
		var response = givenUser().get(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(icList);

		assertThat(response).as("One more ice-cream stored.").hasSize(4);
	}

	@Test
	@Order(3)
	@SneakyThrows
	void deleteIceCream() {

		log.debug("Testing delete function.");
		
		var oneDeleted = givenUser().delete(url() + "/1").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(IceCreamResponse.class);
		log.debug("Deleted one: {}", oneDeleted);

		assertThat(oneDeleted.getId()).isEqualTo(1);

		var noneDeleted = givenUser().get(url() + "/1").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();

		assertThat(noneDeleted).isEmpty();
	}

	@Test
	@Order(4)
	@SneakyThrows
	void validateIceCream() {

		log.debug("Testing error codes");
		var validationError = givenUser().contentType(JSON).body("{\"test\": \"invalid\"}")
			.put(url()).then()
			//.log().all().and()
			.assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract().as(WebValidationError.class);
		
		assertThat(validationError.getErrors())
			.containsAnyOf(Map.of("parameter", "flavor", "error", "must not be blank"));
	}

}
