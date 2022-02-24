package com.github.fwi.sbtreeconf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.fwi.sbtreeconf.db.DbConfig;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = {DbConfig.class, DbTest.Config.class}
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
// Replaced with "drop all objects" in schema-h2.sql
// @org.springframework.test.annotation.DirtiesContext
@ActiveProfiles("test")
@Slf4j
class DbTest {
	
	// Custom test-configuration for this test-class.
	@TestConfiguration
	@ImportAutoConfiguration(JdbcTemplateAutoConfiguration.class)
	static class Config {}
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Test
	void testDb() {
		
		log.debug("Querying ice-creams.");
		var iceCreams = jdbcTemplate.queryForList("select * from ice_cream");
		assertThat(iceCreams).as("3 ice-creams loaded via data-h2.sql").hasSize(3);
		
		// Ensure Hibernate is faster with provider_disables_autocommit=true when auto-commit is off.
		// Test copied from
		// https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/test/java/org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfigurationTests.java#L235
		
		var jpaProperties = context.getBean(LocalContainerEntityManagerFactoryBean.class).getJpaPropertyMap();
		assertThat(jpaProperties)
				.contains(entry("hibernate.connection.provider_disables_autocommit", "true"));
	}

}
