package com.github.fwi.sbtreeconf.weberror;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Replace the (global) Whitelabel Error page.
 * Depends on setting app-value:
 * <br><tt>server.error.path: /web-error</tt>
 * <br>Controller errors are handled by {@link WebErrorResponse}.
 * <p>
 * A JSON response is always returned in case of a general error.
 * This is not user-friendly but since this app is all about REST APIs,
 * it is programmer friendly.
 * Also, the default error pages (either from Spring or Tomcat) reveal the (exact version of the) underlying software.
 * Probably best to try to hide it a little bit.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class WebErrorController implements ErrorController {
	
	public static final String SERVER_ERROR_PATH = "/web-error";
	
	private final ObjectMapper mapper;
	
	ObjectWriter writer;

	@PostConstruct
	private void init() {
		writer = mapper.writerWithDefaultPrettyPrinter();
	}

	@RequestMapping(path = SERVER_ERROR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public String handleError(HttpServletRequest request) {
		
		var error = new WebErrorDTO();
		HttpStatus status = null;
		try {
			status = HttpStatus.valueOf(
						Integer.valueOf(
							request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString())
					);
		} catch (Exception _) {
			// ignored
		}
		if (status == null) {
			error.setReason("Unknown");
		} else {
			error.setStatus(status.value());
			error.setError(status.getReasonPhrase());
		}
		String path = null;
		try {
			path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
		} catch (Exception _) {
			// ignored
		}
		error.setPath(path);
		String response = "error";
		try {
			response = writer.writeValueAsString(error);
		} catch (Exception _) {
			// ignored
		}
		log.trace("Returning web-error response {}", response);
		return response;
	}

}
