package com.github.fwi.sbtreeconf;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = IceCream.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class IceCream {

	public static final String BASE_PATH = "/v1/icecream"; 
	public static final String FIND_PATH = "/find"; 
	
	public static final String DEFAULT_FLAVOR = "vanilla"; 
	
	@GetMapping(path = FIND_PATH)
	public String find() {
		return DEFAULT_FLAVOR;
	}
}
