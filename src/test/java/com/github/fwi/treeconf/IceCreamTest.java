package com.github.fwi.treeconf;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
	initializers = ConfigDataApplicationContextInitializer.class,
	classes = IceCreamTest.Config.class
)
@ActiveProfiles("test")
public class IceCreamTest {
	
	@Import(AppMain.class)
	static class Config {}

	@Autowired
	IceCream iceCream;
	
	@Test
	void testFind() {
		Assertions.assertThat(iceCream.find())
			.as("Find ice-cream")
			.isEqualTo(IceCream.DEFAULT_FLAVOR);
	}
}
