package com.github.fwi.sbtreeconf;

import java.util.List;
import java.util.Map;

import org.springframework.http.ProblemDetail;

import lombok.Getter;
import lombok.Setter;

public class WebValidationError extends ProblemDetail {

    @Getter
    @Setter
    private List<Map<String, String>> errors;

}
