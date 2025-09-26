package com.github.fwi.sbtreeconf.docs;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.fwi.sbtreeconf.IceCreamController;
import com.github.fwi.sbtreeconf.IceCreamRequest;
import com.github.fwi.sbtreeconf.WebTest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class IceCreamApiDocsTest extends WebTest {

	// https://docs.spring.io/spring-restdocs/docs/current/reference/html5/
    private RequestSpecification spec;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {

		spec = new RequestSpecBuilder().addFilter(
				documentationConfiguration(restDocumentation)
					.snippets().withAdditionalDefaults(new RequestPathSnippet()).and()
					.operationPreprocessors()
					.withRequestDefaults(
							Preprocessors.modifyUris().removePort().host("ice-cream-api.fwi.github.com"),
							Preprocessors.prettyPrint())
					.withResponseDefaults(Preprocessors.prettyPrint())
				).build();
	}
	
	RequestSpecification givenUser() {
		return given(spec).auth()
		  .preemptive()
		  .basic(USER, USER_PASS);
	}

	@Test
	@SneakyThrows
	void iceCreamDocs() {
		
		givenUser().filter(document("one-ice-cream", pathParameters( 
				parameterWithName("id").description("Identifier"))))
			.get(url() + "/{id}", 1).then().assertThat()
			.statusCode(HttpStatus.OK.value());
		
		givenUser().filter(document("count-ice-cream"))
			.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		givenUser().filter(document("count-ice-cream-flavor", queryParameters(
				parameterWithName("flavor").description("Flavor"))))
			.queryParam("flavor", "vanilla")
			.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		givenUser().filter(document("bad-ice-cream-request"))
			.queryParam("flavor", " ")
			.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value());
		
		var createIceCream = IceCreamRequest.builder()
				.id(null).flavor("Neapolitan").shape("waffle").build();
		givenUser().filter(document("new-ice-cream"))
			.contentType(JSON).body(createIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());
		
		var updateIceCream = IceCreamRequest.builder()
				.id(1L).flavor("vanillaa").shape("sandwich").build();
		givenUser().filter(document("update-ice-cream"))
			.contentType(JSON).body(updateIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		givenUser().filter(document("bad-ice-cream-update-request"))
			.contentType(JSON).body("{\"test\": \"invalid\"}")
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value());
		
		givenUser().filter(document("delete-ice-cream", pathParameters( 
				parameterWithName("id").description("Identifier"))))
			.delete(url() + "/{id}", 1).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		givenUser().filter(document("all-ice-cream"))
			.get(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());
	}
	
	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }

}
