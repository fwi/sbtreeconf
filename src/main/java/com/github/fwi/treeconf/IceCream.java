package com.github.fwi.treeconf;

import org.springframework.stereotype.Component;

@Component
public class IceCream {

	public static final String DEFAULT_FLAVOR = "vanilla"; 
	
	public String find() {
		return DEFAULT_FLAVOR;
	}
}
