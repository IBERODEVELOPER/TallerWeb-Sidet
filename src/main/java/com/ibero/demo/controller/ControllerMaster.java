package com.ibero.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerMaster {

	@GetMapping("/")
	public String showLoginForm(Model model) {
		model.addAttribute("titlepage", "ⓇRegistrex");
		return "/login";
	}
}
