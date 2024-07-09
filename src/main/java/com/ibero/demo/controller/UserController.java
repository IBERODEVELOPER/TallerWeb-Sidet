package com.ibero.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.User;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IRolUserService;
import com.ibero.demo.service.IUserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
@SessionAttributes("user")
public class UserController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IPeopleService peopleService;
	
	@Autowired
	private IRolUserService rolUserService;

	@GetMapping("/userReg")
	public String showFormUser(Model model) {
		User user = new User();
		model.addAttribute("titlepage", "Creación de Cuenta");
		model.addAttribute("people", peopleService.findAllPeople());
		model.addAttribute("rol", rolUserService.findAllRol());
		model.addAttribute("user", user);
		return "/pages/formUser"; // Nombre de tu archivo HTML de registro
	}

	@RequestMapping(value = "/userReg", method = RequestMethod.POST)
	public String processForm(@Valid User user,BindingResult result,Model model,RedirectAttributes flash, SessionStatus status) {
	if(result.hasErrors()) {
		model.addAttribute("titlepage", "Formulario de Registro de Usuarios");
		model.addAttribute("titleform", "Creación de usuarios");
		return "/pages/formPeople";
	}
	flash.addFlashAttribute("success", "Usuario creado correctamente");
	userService.saveUser(user);
	status.setComplete();
	return "redirect:/Peoples/listPeople";
	}

}
