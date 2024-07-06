package com.ibero.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerMaster {

	@GetMapping("/")
	public String showLoginForm() {
		return "/login";
	}
}
