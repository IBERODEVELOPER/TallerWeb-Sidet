package com.ibero.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibero.demo.entity.User;

@Controller
public class ControllerMaster {

	@GetMapping("/")
	public String showLoginForm(Model model) {
		model.addAttribute("titlepage", "ⓇRegistrex");
		return "/login";
	}
	
/*	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String showLoginForme(@RequestParam(required = true, value = "username") String username,
			@RequestParam(required = true, value = "password") String password, Model model) {
		User usuario = userService.findByUsername(username);

		if (usuario != null && usuario.getUserPassword().equals(password)) {
			model.addAttribute("usuario",
					(usuario.getPeople().getNamePeople() + " " + usuario.getPeople().getFirstLastNamePeople() + " "
							+ usuario.getPeople().getSecondLastNamePeople()).toUpperCase());
			model.addAttribute("rol", usuario.getRoles().getLevelUser());

			return "/pages/home";
			// return "redirect:/home"; // Página de inicio después del login exitoso
		} else {
			model.addAttribute("error", true);
			// return "/pages/login?error=true";
			return "/pages/login";// Manejo de error de login
}}*/
}
