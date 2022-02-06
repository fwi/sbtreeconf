package com.github.fwi.sbtreeconf.weberror;

import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class WebErrorResponse extends ResponseEntityExceptionHandler {

	@ExceptionHandler(SafeResponseStatusException.class)
	public ResponseEntity<Object> controllerError(SafeResponseStatusException ex, WebRequest request) {
		
		log.debug("Controller error for {}", request);
		var error = new WebErrorDTO(request, ex.getStatus());
		error.setReason(ex.getReason());
		return new ResponseEntity<>(error, ex.getStatus());
	}

	/**
	 * This is a catch-all, but some are still handled by "handleExceptionInternal"
	 * (e.g. Jackson serialization errors to JSON).
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> controllerError(Exception ex, WebRequest request) {
		
		var error = new WebErrorDTO(request, HttpStatus.INTERNAL_SERVER_ERROR);
		error.setReason("Unexpected runtime error.");
		log.error("Internal server error at {}", error, ex);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Provide feedback for all validations that failed.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		log.debug("Validation failed for {}", request);
		var error = new WebValidationErrorDTO(request, status);
		error.setValidations(
				ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(f -> new String[] {f.getField(), f.getDefaultMessage()})
				.collect(Collectors.toList())
				);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Limit the information from all other errors (e.g. 500 internal server error).
	 * Does not work for all errors (e.g. NPE - still gets logged by another method) 
	 * but does work for conversion errors (e.g. Jackson conversion to JSON that fails).
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

		var error = new WebErrorDTO(request, status);
		error.setReason("Unkown cause");
		// Copied from parent method.
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
			log.error("Internal server error at {}", error, ex);
		} else {
			log.warn("Unexpected error at {}", error, ex);
		}
		return new ResponseEntity<>(error, status);
	}

}
