package com.github.fwi.sbtreeconf.weberror;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;

@Data
public class WebErrorDTO {

	public static final String URI_PREFIX = "uri=";

	private String timestamp;
	private String status;
	private String error;
	private String path;
	private String reason;

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
		setTimeStampNow();
		setStatus(String.valueOf(status.value()));
		setError(status.getReasonPhrase());
	}

	public void setTimeStampNow() {
		setTimestamp(String.valueOf(System.currentTimeMillis()));
	}

}
