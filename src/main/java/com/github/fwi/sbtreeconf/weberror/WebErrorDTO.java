package com.github.fwi.sbtreeconf.weberror;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class WebErrorDTO {

	public static final String URI_PREFIX = "uri=";

	private int status; // 404 
	private String error; // not found
	private String path; // /path/with/tupo
	private String reason; // additional information from controller

	public WebErrorDTO() {
		// no-op
	}

	public WebErrorDTO(WebRequest request, HttpStatusCode status) {
		updateFrom(request, status);
	}
	
	public void updateFrom(WebRequest request, HttpStatusCode status) {
		
		var pathAttribute = request.getAttribute("org.springframework.web.util.UrlPathHelper.PATH", 0);
		String requestPath = null;
		if (pathAttribute != null) {
			requestPath = String.valueOf(pathAttribute);
		}
		if (requestPath == null) {
			requestPath = request.getDescription(false);
		}
		if (requestPath != null && requestPath.startsWith(URI_PREFIX)) {
			requestPath = requestPath.substring(URI_PREFIX.length());
		}
		setPath(requestPath);
		setStatus(status.value());
		setError(statusCodeReasonPhrase(status));
	}

	String statusCodeReasonPhrase(HttpStatusCode status) {
		
		try {
			return HttpStatus.valueOf(status.value()).getReasonPhrase();
		} catch (Exception _) {
			// ignored
		}
		return null;
	}

}
