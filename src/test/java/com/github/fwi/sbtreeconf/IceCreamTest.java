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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = AppMain.class, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Slf4j
public class IceCreamTest extends WebTest {

	@Test
	@SneakyThrows
	void getIceCream() {
		
		log.debug("Testing icecream web-access.");
		var response = get(getServerUrl() + IceCream.BASE_PATH + IceCream.FIND_PATH).then().assertThat()
			.statusCode(HttpStatus.OK.value())
			.extract().asString();
		assertThat(response).isEqualTo("vanilla");
	}

}
