package com.ibero.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControllerMaster {

	@GetMapping(value = "/login")
	public String showLoginForm(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,Model model,Principal principal) {
		model.addAttribute("titlepage", "©Registrex");

		if (error != null) {
			model.addAttribute("error","¡Error al intentar iniciar sesión!");
		}
		
		if (logout != null) {
			model.addAttribute("success","¡Cerro sesión con exito!");
		}
		
		if (principal != null) {
			return "redirect:/index";
		}
		return "/login";
	}
	
	@GetMapping(value = "/")
	public String showIndex(Model model) {
		model.addAttribute("titlepage", "©Registrex");
		return "/index";
	}
	 
}
