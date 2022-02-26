package com.github.fwi.sbtreeconf.docs;

import static org.springframework.restdocs.generate.RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

/**
 * Template for this snippet is stored at
 * <br><tt>${project.basedir}/src/test/resources/org/springframework/restdocs/templates/asciidoctor</tt>
 */
public class RequestPathSnippet extends TemplatedSnippet {

	// Full customization example:
	// https://github.com/ePages-de/restdocs-wiremock/blob/master/wiremock/src/main/java/com/epages/restdocs/WireMockJsonSnippet.java
	
    public RequestPathSnippet() {
        super("request-uri", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
    	
    	var attributes = new HashMap<String, Object>();
    	attributes.put("request-method", operation.getRequest().getMethod());
		var requestPath = (String) operation.getAttributes()
				.get(ATTRIBUTE_NAME_URL_TEMPLATE);
		var queryPath = operation.getRequest().getUri().getQuery();
		if (StringUtils.isNotBlank(queryPath)) {
			requestPath += "?" + queryPath;
		}
    	attributes.put("request-path", requestPath);
        return attributes;
    }

}
