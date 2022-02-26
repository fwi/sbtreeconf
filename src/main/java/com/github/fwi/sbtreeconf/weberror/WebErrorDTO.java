package com.github.fwi.sbtreeconf.weberror;

import org.springframework.http.HttpStatus;
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

	public WebErrorDTO(WebRequest request, HttpStatus status) {
		updateFrom(request, status);
	}
	
	public void updateFrom(WebRequest request, HttpStatus status) {
		
		var pathAttribute = request.getAttribute("org.springframework.web.util.UrlPathHelper.PATH", 0);
		String path = null;
		if (pathAttribute != null) {
			path = String.valueOf(pathAttribute);
		}
		if (path == null) {
			path = request.getDescription(false);
		}
		if (path != null && path.startsWith(URI_PREFIX)) {
			path = path.substring(URI_PREFIX.length());
		}
		setPath(path);
		setStatus(status.value());
		setError(status.getReasonPhrase());
	}

}
