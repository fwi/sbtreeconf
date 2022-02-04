package com.github.fwi.sbtreeconf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class FlavorCountTest {

	@Test
	void countFlavor() {
		
		var iceCreams = List.of(
				IceCreamDTO.builder().flavor("x").build(),
				IceCreamDTO.builder().flavor("y").build(),
				IceCreamDTO.builder().flavor("z").build(),
				IceCreamDTO.builder().flavor("x").build(),
				IceCreamDTO.builder().flavor("z").build(),
				IceCreamDTO.builder().flavor("z").build()
				);
		var service = new IceCreamService(null);
		assertEquals(service.countFlavor(iceCreams, "a"), 0);
		assertEquals(service.countFlavor(iceCreams, "x"), 2);
		assertEquals(service.countFlavor(iceCreams, "y"), 1);
		assertEquals(service.countFlavor(iceCreams, "z"), 3);
	}
}
