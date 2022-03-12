package com.github.fwi.sbtreeconf.docs;

import static com.github.fwi.sbtreeconf.IceCreamTest.JSON;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import org.springframework.restdocs.operation.preprocess.Preprocessors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.fwi.sbtreeconf.IceCreamConfig;
import com.github.fwi.sbtreeconf.IceCreamController;
import com.github.fwi.sbtreeconf.IceCreamRequest;
import com.github.fwi.sbtreeconf.WebServerConfig;
import com.github.fwi.sbtreeconf.WebTest;
import com.github.fwi.sbtreeconf.db.DbConfig;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest(
	classes = {WebServerConfig.class, DbConfig.class, IceCreamConfig.class}, 
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class IceCreamApiDocs extends WebTest {

	// https://docs.spring.io/spring-restdocs/docs/current/reference/html5/
    private RequestSpecification spec;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {

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
	
	@Test
	@SneakyThrows
	void iceCreamDocs() {
		
		given(spec).filter(document("one-ice-cream", pathParameters( 
				parameterWithName("id").description("Identifier"))))
			.get(url() + "/{id}", 1).then().assertThat()
			.statusCode(HttpStatus.OK.value());
		
		given(spec).filter(document("count-ice-cream"))
			.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		given(spec).filter(document("count-ice-cream-flavor", requestParameters(
				parameterWithName("flavor").description("Flavor"))))
			.queryParam("flavor", "vanilla")
			.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		given(spec).filter(document("bad-ice-cream-request"))
			.queryParam("flavor", " ")
			.get(url() + IceCreamController.COUNT_PATH).then().assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value());
		
		var createIceCream = IceCreamRequest.builder()
				.id(null).flavor("Neapolitan").shape("waffle").build();
		given(spec).filter(document("new-ice-cream"))
			.contentType(JSON).body(createIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());
		
		var updateIceCream = IceCreamRequest.builder()
				.id(1L).flavor("vanillaa").shape("sandwich").build();
		given(spec).filter(document("update-ice-cream"))
			.contentType(JSON).body(updateIceCream)
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		given(spec).filter(document("bad-ice-cream-update-request"))
			.contentType(JSON).body("{\"test\": \"invalid\"}")
			.put(url()).then().assertThat()
			.statusCode(HttpStatus.BAD_REQUEST.value());
		
		given(spec).filter(document("delete-ice-cream", pathParameters( 
				parameterWithName("id").description("Identifier"))))
			.delete(url() + "/{id}", 1).then().assertThat()
			.statusCode(HttpStatus.OK.value());

		given(spec).filter(document("all-ice-cream"))
			.get(url()).then().assertThat()
			.statusCode(HttpStatus.OK.value());
	}
	
	String url() { return getServerUrl() + IceCreamController.BASE_PATH; }

}
