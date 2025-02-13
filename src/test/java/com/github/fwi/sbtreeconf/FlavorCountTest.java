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
				IceCreamResponse.builder().flavor("x").build(),
				IceCreamResponse.builder().flavor("y").build(),
				IceCreamResponse.builder().flavor("z").build(),
				IceCreamResponse.builder().flavor("x").build(),
				IceCreamResponse.builder().flavor("z").build(),
				IceCreamResponse.builder().flavor("z").build()
				);
		var service = new IceCreamService(null, null);
		assertEquals(0, service.countFlavor(iceCreams, "a"));
		assertEquals(2, service.countFlavor(iceCreams, "x"));
		assertEquals(1, service.countFlavor(iceCreams, "y"));
		assertEquals(3, service.countFlavor(iceCreams, "z"));
	}
}
