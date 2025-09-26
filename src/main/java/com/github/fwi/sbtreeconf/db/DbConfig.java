package com.github.fwi.sbtreeconf.db;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = DbPackage.class)
@EnableJpaRepositories(basePackageClasses = DbPackage.class)
public class DbConfig {

}
