package com.github.fwi.sbtreeconf;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = IceCreamController.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IceCreamController {

	public static final String BASE_PATH = "/api/v1/icecream"; 
	public static final String FIND_PATH = "/find";
	public static final String COUNT_FLAVOR_PATH = "/count/flavor";
	
	final IceCreamService service;
	
	@GetMapping(path = FIND_PATH)
	public List<IceCreamDTO> find() {
		return service.findAll();
	}

	@GetMapping(path = COUNT_FLAVOR_PATH + "/{flavor}")
	public int flavorCount(@PathVariable String flavor) {
		return service.countFlavor(flavor);
	}

}
