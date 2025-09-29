package com.github.fwi.sbtreeconf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = DbTest.Config.class
)
@ActiveProfiles("test")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class DbTest {
	
	@TestConfiguration
	@ImportAutoConfiguration({
		DataSourceAutoConfiguration.class, 
		SqlInitializationAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class,
		JdbcClientAutoConfiguration.class
	})
	static class Config {}
	
	final JdbcClient jdbcClient;
	
	@Test
	void testDb() {
		
		log.debug("Querying ice-creams.");
		var iceCreams = jdbcClient.sql("select * from ice_cream").query().listOfRows();
		assertThat(iceCreams).as("3 ice-creams loaded via data-h2.sql").hasSize(3);
	}

}
