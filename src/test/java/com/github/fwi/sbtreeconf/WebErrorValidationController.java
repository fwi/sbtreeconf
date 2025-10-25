package com.github.fwi.sbtreeconf;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = IceCreamController.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class WebErrorValidationController {

	@PutMapping("/one")
	int validateOne(@Valid @RequestBody IceCreamRequest iceCream) {
		log.info("IceCream: {}", iceCream);
		return 1;
	}

	@PutMapping("/many")
	int validateMany(@Valid @RequestBody List<IceCreamRequest> iceCreams) {
		log.info("IceCreams: {}", iceCreams.size());
		return iceCreams.size();
	}

}
