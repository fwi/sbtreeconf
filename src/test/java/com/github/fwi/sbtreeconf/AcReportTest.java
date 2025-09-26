package com.github.fwi.sbtreeconf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = AcReportTest.Config.class
)
@ActiveProfiles("test")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class AcReportTest {
	
	@TestConfiguration
	@ImportAutoConfiguration({
		// For further testing:
		// com.github.fwi.sbtreeconf.db.DbConfig.class,
		DataSourceAutoConfiguration.class, // should be a dependency of project
		CassandraAutoConfiguration.class   // should not be a dependency of project
	})
	static class Config {
		
		@Bean
		AcReport acReport() {
			return new AcReport();
		}
	}

	final ApplicationContext ctx;
	final AcReport acReport;
	
	@Test
	void verifyReport() {
		
		log.debug("Verifying AC report");
		var report = acReport.filterAcReport(ctx.getBean(ConditionEvaluationReport.class));
		// unmatched
		assertThat(report.get(0)).contains(CassandraAutoConfiguration.class.getName());
		// matched
		assertThat(report.get(1)).contains(DataSourceAutoConfiguration.class.getName());
	}
	
}
