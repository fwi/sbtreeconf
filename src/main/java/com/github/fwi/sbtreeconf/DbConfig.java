package com.github.fwi.sbtreeconf;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ 
	DataSourceAutoConfiguration.class, 
	DataSourceTransactionManagerAutoConfiguration.class,
	SqlInitializationAutoConfiguration.class
})
public class DbConfig {

}
