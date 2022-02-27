package com.github.fwi.sbtreeconf;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.fwi.sbtreeconf.weberror.SafeResponseStatusException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = IceCreamController.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class IceCreamController {

	public static final String BASE_PATH = "/api/v1/icecream"; 
	public static final String COUNT_PATH = "/count"; 
	
	final IceCreamService service;
	
	@GetMapping
	public List<IceCreamResponse> findAll() {
		return service.findAll();
	}

	@GetMapping(path = "/{id}")
	public IceCreamResponse findOne(@PathVariable long id) {
		return service.findOne(id);
	}

	@GetMapping(path = COUNT_PATH)
	public long flavorCount(@RequestParam(required = false) String flavor) {
		
		if (flavor == null) {
			return service.count();
		}
		if (StringUtils.isBlank(flavor)) {
			throw new SafeResponseStatusException(HttpStatus.BAD_REQUEST, "Flavor must have a value.");
		}
		return service.countFlavor(flavor);
	}
	
	@PutMapping
	public IceCreamResponse upsert(@Valid @RequestBody IceCreamRequest iceCream) {
		return service.upsert(iceCream);
	}
	
}
