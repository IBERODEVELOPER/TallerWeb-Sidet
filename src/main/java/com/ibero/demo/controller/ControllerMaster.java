package com.ibero.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControllerMaster {

	@GetMapping({"/login","/"})
	public String showLoginForm(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,Model model,Principal principal) {
		model.addAttribute("titlepage", "ⓇRegistrex");

		if (error != null) {
			model.addAttribute("error",
					"¡Error al intentar iniciar sesión : Revise el nombre de usuario y contraseña, sean correctas, por favor vuelva a intentarlo!");
		}
		
		if (logout != null) {
			model.addAttribute("error","¡Cerro sesión con exito!");
		}
		
		if (principal != null) {
			return "redirect:/peoples/listPeople";
		}
		return "/login";
	}
		 
}
