package com.github.fwi.sbtreeconf;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import com.github.fwi.sbtreeconf.db.DbConfig;
import com.github.fwi.sbtreeconf.security.WebSecurityConfig;

public class WebTest {
	
	public static final String JSON = "application/json";
	
	public final String USER = "operator"; 
	public final String USER_PASS = "operates"; 
	
	@Import({
		WebServerConfig.class, 
		WebSecurityConfig.class, 
		DbConfig.class, 
		IceCreamConfig.class
	})
	public static class Config {
		
	}

	@LocalServerPort
	int port;

	public String getServerUrl() {
		return "http://localhost:" + port;
	}
}
