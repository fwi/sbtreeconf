package com.github.fwi.sbtreeconf;

import org.springframework.boot.web.server.LocalServerPort;

class WebTest {

	@LocalServerPort
	int port;

	String getServerUrl() {
		return "http://localhost:" + port;
	}
}
