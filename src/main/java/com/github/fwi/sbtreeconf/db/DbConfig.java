package com.github.fwi.sbtreeconf.db;

import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = DbPackage.class)
@EnableJpaRepositories(basePackageClasses = DbPackage.class)
@ImportAutoConfiguration({ 
	DataSourceAutoConfiguration.class, 
	DataSourceTransactionManagerAutoConfiguration.class,
	SqlInitializationAutoConfiguration.class,
	HibernateJpaAutoConfiguration.class,
	// The datasource health contributor has to be imported after the datasource autoconfiguration.
	DataSourceHealthContributorAutoConfiguration.class,
})
public class DbConfig {

}
