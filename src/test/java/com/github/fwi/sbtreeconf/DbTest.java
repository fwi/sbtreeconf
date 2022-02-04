package com.github.fwi.sbtreeconf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = {DbConfig.class, JdbcTemplateAutoConfiguration.class}
)
/*
 * When running "mvn test" (i.e. all tests in one go), 
 * each test using the database will run "schema-h2.sql"
 * which gives rise to errors like "table already exists".
 * One work-around is to use annotation @AutoConfigureTestDatabase
 * but that ignores our configured "database.url" and constructs a Spring-Boot default one.
 * Instead use @DirtiesContext to ensure database tests do not interfere with each other.
 * It is a little bit slower, but does give each test a clean work-environment.
 */
@DirtiesContext
@ActiveProfiles("test")
class DbTest {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Test
	void testDb() {
		
		var iceCreams = jdbcTemplate.queryForList("select * from ice_cream");
		assertThat(iceCreams).as("3 ice-creams loaded via data-h2.sql").hasSize(3);
	}

}
