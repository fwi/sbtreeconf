package com.github.fwi.sbtreeconf.weberror;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
@Controller
@Slf4j
public class WebErrorController implements ErrorController, InitializingBean {
	
	public static final String SERVER_ERROR_PATH = "/web-error";
	
	@Autowired
	ObjectMapper mapper;
	
	ObjectWriter writer;

	@Override
	public void afterPropertiesSet() throws Exception {
		writer = mapper.writerWithDefaultPrettyPrinter();
	}

	@RequestMapping(path = SERVER_ERROR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String handleError(HttpServletRequest request) {
		
		var error = new WebErrorDTO();
		HttpStatus status = null;
		try {
			status = HttpStatus.valueOf(
						Integer.valueOf(
							request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString())
					);
		} catch (Exception ignored) {}
		if (status == null) {
			error.setReason("Unknown");
		} else {
			error.setStatus(status.value());
			error.setError(status.getReasonPhrase());
		}
		String path = null;
		try {
			path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
		} catch (Exception ignored) {}
		error.setPath(path);
		String response = "error";
		try {
			response = writer.writeValueAsString(error);
		} catch (Exception ignored) {}
		log.trace("Returning web-error response {}", response);
		return response;
	}

}
