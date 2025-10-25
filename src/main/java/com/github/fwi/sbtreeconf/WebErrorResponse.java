package com.github.fwi.sbtreeconf;

import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles validation errors thrown by Controllers.
 */
@ControllerAdvice
@Slf4j
public class WebErrorResponse extends ResponseEntityExceptionHandler {

	/**
	 * Called when one parameter / validation failed.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		log.debug("Method argument validation failed for {}", request);
		var errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> Map.of("parameter", error.getField(), "error", error.getDefaultMessage()))
				.toList();
		ex.getBody().setProperty("errors", errors);
		return super.handleMethodArgumentNotValid(ex, headers, status, request);
	}

	/**
	 * Called when a parameter that is a collection fails.
	 * <p>
	 * Note: this implementation may not be complete.
	 * It has been tested for a list of items but not, for example, a list of maps with items.
	 */
	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(
			HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		log.debug("Method validation failed for {}", request);
		// Copied from https://github.com/spring-projects/spring-framework/issues/31887 and updated to include a field-name.
		var errors = ex.getParameterValidationResults().stream()
				.flatMap(result -> result.getResolvableErrors().stream()
					.map(error -> {
						var param = switch(error) {
							case ObjectError o -> o.getObjectName();
							default -> ((MessageSourceResolvable) error.getArguments()[0]).getDefaultMessage();
						};
						if (result.getContainerIndex() != null) {
							param += "[" + result.getContainerIndex() + "]";
							if (error instanceof FieldError fieldError) {
								param += "." + fieldError.getField();
							}
						}
						return Map.of("parameter", param, "error", error.getDefaultMessage());
					})
				).toList();
		ex.getBody().setProperty("errors", errors);
		return super.handleHandlerMethodValidationException(ex, headers, status, request);
	}

}
