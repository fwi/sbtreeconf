package com.github.fwi.sbtreeconf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
	
	public static final String APIDOCS_PATH = "/docs/index.html"; 

	@GetMapping("/")
	public String homeToApiDocs() {
		return "redirect:" + APIDOCS_PATH; 
	}

	/**
	 * This should show a name like
	 * <code>VirtualThread[#60]/runnable@ForkJoinPool-1-worker-1</code>
	 * when virtual threads are used.
	 */
	@GetMapping("/thread")
	@ResponseBody
	public String threadName() {
		return Thread.currentThread().toString(); 
	}

}
