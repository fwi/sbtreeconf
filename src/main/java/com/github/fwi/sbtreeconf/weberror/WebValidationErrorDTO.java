package com.github.fwi.sbtreeconf.weberror;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebValidationErrorDTO extends WebErrorDTO {

	private String timestamp;
	private String status;
	private String error;
	private String path;
	private Object validations;

	public WebValidationErrorDTO() {
		setValidationReason();
	}

	public WebValidationErrorDTO(WebRequest request, HttpStatus status) {
		updateFrom(request, status);
	}
	
	@Override
	public void updateFrom(WebRequest request, HttpStatus status) {
		super.updateFrom(request, status);
		setValidationReason();
	}

	public void setValidationReason() {
		setReason("Invalid value(s).");
	}

}
