package com.github.fwi.sbtreeconf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.fwi.sbtreeconf.db.DbConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = {DbConfig.class, DbTest.Config.class}
)
@ActiveProfiles("test")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class DbTest {
	
	@TestConfiguration
	@ImportAutoConfiguration({
		DataSourceAutoConfiguration.class, 
		DataSourceTransactionManagerAutoConfiguration.class,
		SqlInitializationAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class
	})
	static class Config {}
	
	final ApplicationContext context;
	final JdbcTemplate jdbcTemplate;
	
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
