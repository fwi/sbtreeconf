package com.github.fwi.sbtreeconf;

import org.springframework.boot.test.web.server.LocalServerPort;

public class WebTest { // NOSONAR
	
	public static final String JSON = "application/json";
	
	public static final String USER = "operator"; 
	public static final String USER_PASS = "operates"; 
	
	@LocalServerPort
	int port;

	public String getServerUrl() {
		return "http://localhost:" + port;
	}
}
