package com.github.fwi.sbtreeconf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	public static final String APIDOCS_PATH = "/docs/index.html"; 

	@GetMapping("/")
	public String homeToApiDocs() {
		return "redirect:" + APIDOCS_PATH; 
	}
}
