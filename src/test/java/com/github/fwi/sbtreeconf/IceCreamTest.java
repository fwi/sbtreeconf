package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
import com.github.fwi.sbtreeconf.weberror.WebErrorDTO;
import com.github.fwi.sbtreeconf.weberror.WebValidationErrorDTO;

import io.restassured.common.mapper.TypeRef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = {WebServerConfig.class, DbConfig.class, IceCreamConfig.class}, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Slf4j
public class IceCreamTest extends WebTest {

	public static final String JSON = "application/json";
	
	@Autowired
	ModelMapper mapper;
	
	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }
	
	@Test
	@SneakyThrows
	void getIceCream() {
		
		final var icList = new TypeRef<List<IceCreamResponse>>() {};
		
		log.debug("Testing icecream web-access.");
		var response = get(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(icList);

		assertThat(response).as("All ice-creams.").hasSize(3);
		assertThat(response).as("Have special flavor")
			.filteredOn(i -> i.getFlavor().equals("Neapolitan")).isNotEmpty();
		
		var count = given()
				.queryParam("flavor", "vanilla")
				.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(Long.class);

		assertThat(count).isEqualTo(2);

		var errorDto = given()
				.queryParam("flavor", "")
				.get(url() + IceCreamController.COUNT_PATH).then()
				.log().all().and()
				.assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract().as(WebErrorDTO.class);

		assertThat(errorDto.getReason()).contains("Flavor must have a value");
		
		var none = get(url() + "/0").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();

		assertThat(none).isEmpty();

		var one = get(url() + "/1").then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(IceCreamResponse.class);
		log.debug("One: {}", one);

		assertThat(one.getId()).isEqualTo(1);
		
		log.debug("Testing icecream storage.");
		
		final var oldDate = OffsetDateTime.parse("2022-02-05T21:00:00+01");
		// Providing a modified date does not give an error,
		// the value is just ignored.
		var newIceCream = IceCreamRequest.builder()
				.id(null).flavor("Neapolitan").shape("waffle").build();
		var inserted = given().contentType(JSON).body(newIceCream)
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
		
		var updateIceCream = mapper.map(one, IceCreamRequest.class);
		updateIceCream.setShape("sandwich");
		log.debug("Update ice-cream {}", updateIceCream);
		var updated = given().contentType(JSON).body(updateIceCream)
				.put(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(IceCreamResponse.class);
			
		assertThat(updated).extracting(
				IceCreamResponse::getId, 
				IceCreamResponse::getFlavor,
				IceCreamResponse::getShape
				).containsExactly(1l, "vanilla", "sandwich");

		var negative = inserted.toBuilder().id(-1L).build();
		none = given().contentType(JSON).body(negative)
				.put(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();
		
		assertThat(none).isEmpty();
		
		response = get(url()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(icList);

		assertThat(response).as("One more ice-cream stored.").hasSize(4);
		
		log.debug("Testing delete function.");
		
		var oneDeleted = delete(url() + "/1").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(IceCreamResponse.class);
		log.debug("Deleted one: {}", one);

		assertThat(oneDeleted.getId()).isEqualTo(1);

		var noneDeleted = get(url() + "/1").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().asString();

		assertThat(noneDeleted).isEmpty();
		
		log.debug("Testing error codes");
		var validationError = given().contentType(JSON).body("{\"test\": \"invalid\"}")
			.put(url()).then()
			.log().all().and()
			.assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract().as(WebValidationErrorDTO.class);
		
		assertThat(validationError.getValidations())
			.isInstanceOf(Map.class)
			.extracting("flavor").asList().containsExactly("must not be blank");
	}

}
