package com.github.fwi.sbtreeconf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Demonstration of a simple test for complex methods:
 * move methods that do something complicated into a separate class if needed,
 * ensure arguments are simple and do not require additional classes / structures to function.
 * Test without using any kind of beans, repositories etc..
 * (i.e. leave Spring (Boot) out if it as much as possible).
 */
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
		var service = new IceCreamService(null, null);
		assertEquals(service.countFlavor(iceCreams, "a"), 0);
		assertEquals(service.countFlavor(iceCreams, "x"), 2);
		assertEquals(service.countFlavor(iceCreams, "y"), 1);
		assertEquals(service.countFlavor(iceCreams, "z"), 3);
	}
}
