package com.github.fwi.sbtreeconf;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.common.mapper.TypeRef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = AppMain.class, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@DirtiesContext
@ActiveProfiles("test")
@Slf4j
class IceCreamTest extends WebTest {

	@Test
	@SneakyThrows
	void getIceCream() {
		
		log.debug("Testing icecream web-access.");
		var response = get(getServerUrl() + IceCreamController.BASE_PATH 
				+ IceCreamController.FIND_PATH).then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().as(new TypeRef<List<IceCreamDTO>>() {});
		assertThat(response).as("All ice-creams.").hasSize(3);
		assertThat(response).as("Have special flavor")
			.filteredOn(i -> i.getFlavor().equals("Neapolitan")).isNotEmpty();
		
		var count = get(getServerUrl() + IceCreamController.BASE_PATH 
				+ IceCreamController.COUNT_FLAVOR_PATH + "/vanilla").then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.extract().as(Integer.class);
		assertThat(count).isEqualTo(2);
	}

}
