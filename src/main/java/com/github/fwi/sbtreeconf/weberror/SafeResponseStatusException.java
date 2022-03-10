package com.github.fwi.sbtreeconf.weberror;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * The (reason) message included in this exception will be shown to the user
 * and not the entire stack-trace.
 */
public class SafeResponseStatusException extends ResponseStatusException {

	private static final long serialVersionUID = -8474621446754713944L;

	/**
	 * The reason message will be shown to the user.
	 * @param status 4xx status code
	 * @param reason the message to show to the user.
	 */
	public SafeResponseStatusException(HttpStatus status, String reason) {
		super(status, reason);
	}

}
