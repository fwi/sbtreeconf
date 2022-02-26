package com.github.fwi.sbtreeconf;

import org.springframework.boot.web.server.LocalServerPort;

public class WebTest {

	@LocalServerPort
	int port;

	public String getServerUrl() {
		return "http://localhost:" + port;
	}
}
