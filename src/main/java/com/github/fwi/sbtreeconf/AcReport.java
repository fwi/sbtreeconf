package com.github.fwi.sbtreeconf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class AcReport implements ApplicationListener<ContextRefreshedEvent> {

	// Code in this class is very defensive - the code here should never fail to start an application.

	public static final String AUTO_CONF = "AutoConf";
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		var report = filterAcReport(
				event.getApplicationContext().getBean(ConditionEvaluationReport.class));
		logAcReport(report, log.isDebugEnabled());
	}
	
	List<List<String>> filterAcReport(ConditionEvaluationReport report) {
		
		if (report == null) {
			log.info("No ConditionEvaluationReport available in application context.");
			return null;
		}
		var outcomes = report.getConditionAndOutcomesBySource();
		if (outcomes == null || outcomes.isEmpty()) {
			log.info("No conditions by source available in evaluation report.");
			return null;
		}
		var unmatched = new HashSet<String>();
		var matched = new HashSet<String>();
		var exclusions = report.getExclusions();
		if (exclusions == null) {
			exclusions = new ArrayList<String>();
		} else {
			// get rid of unmodifiable list.
			exclusions = new ArrayList<String>(exclusions);
		}
		var ucSet = report.getUnconditionalClasses();
		var unconditionals = new ArrayList<String>();
		if (ucSet != null) {
			unconditionals.addAll(ucSet);
		}
		for (var className: outcomes.keySet()) {
			if (className == null || !className.contains(AUTO_CONF)) {
				continue;
			}
			var conditions = outcomes.get(className);
			if (conditions == null) {
				continue; // should be part of exclusions or unconditionals
			}
			if (conditions.isFullMatch()) {
				matched.add(className);
			} else {
				unmatched.add(className);
			}
		}
		unmatched.removeAll(overlappingEntries(unmatched));
		matched.removeAll(overlappingEntries(matched));
		unmatched.removeAll(doubleEntries(unmatched, matched));
		// unmatched.removeAll(doubleEntries(unmatched, new HashSet<String>(unconditionals)));
		var unmatchedList = new ArrayList<String>(unmatched); 
		var matchedList = new ArrayList<String>(matched); 
		Collections.sort(unmatchedList);
		Collections.sort(matchedList);
		Collections.sort(exclusions);
		Collections.sort(unconditionals);
		return List.of(unmatchedList, matchedList, unconditionals, exclusions);
	}
	
	HashSet<String> overlappingEntries(HashSet<String> classes) {
		
		var overlaps = new HashSet<String>();
		for (var className : classes) {
			var mainName = baseName(className);
			if (className.equals(mainName)) {
				continue;
			}
			if (classes.contains(mainName)) {
				overlaps.add(className);
			}
		}
		return overlaps;
	}

	/**
	 * Remove auto-config classes in "unmatched" that are already reported in "matched".
	 */
	HashSet<String> doubleEntries(HashSet<String> unmatched, HashSet<String> matched) {
		
		var overlaps = new HashSet<String>();
		for (var className : unmatched) {
			var mainName = baseName(className);
			if (matched.contains(mainName)) {
				overlaps.add(className);
			}
		}
		return overlaps;
	}

	String baseName(String className) {

		int i = className.indexOf('$');
		if (i < 1) {
			i = className.indexOf('#');
		}
		if (i > 1) {
			return className.substring(0, i);
		}
		return className;
	}

	void logAcReport(List<List<String>> report, boolean debug) {

		if (report == null || report.size() != 4) {
			return;
		}
		if (!report.get(0).isEmpty()) {
			log.info("Unused auto-configuration classes:\n{}", String.join("\n", report.get(0)));
		}
		if (debug) {
			if (!report.get(1).isEmpty()) {
				log.debug("Used auto-configuration classes:\n{}", String.join("\n", report.get(1)));
			}
			if (!report.get(2).isEmpty()) {
				log.debug("Beans used without conditions:\n{}", String.join("\n", report.get(2)));
			}
			if (!report.get(3).isEmpty()) {
				log.debug("Beans used that are excluded from condition evaluation:\n{}", String.join("\n", report.get(3)));
			}
		}
	}
	
}
