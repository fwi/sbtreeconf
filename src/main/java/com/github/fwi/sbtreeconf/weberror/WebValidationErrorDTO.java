package com.github.fwi.sbtreeconf.weberror;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_EMPTY)
public class WebValidationErrorDTO extends WebErrorDTO {

	private Map<String, List<String>> validations;

	public WebValidationErrorDTO() {
		setValidationReason();
	}

	public WebValidationErrorDTO(WebRequest request, HttpStatusCode status) {
		updateFrom(request, status);
	}
	
	@Override
	public void updateFrom(WebRequest request, HttpStatusCode status) {
		super.updateFrom(request, status);
		setValidationReason();
	}

	public void setValidationReason() {
		setReason("Invalid value(s).");
	}

}
