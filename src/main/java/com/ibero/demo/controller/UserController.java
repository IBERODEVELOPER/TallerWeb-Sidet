package com.ibero.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ibero.demo.entity.People;
import com.ibero.demo.entity.Rol;
import com.ibero.demo.entity.User;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IRolUserService;
import com.ibero.demo.service.IUserService;

@Controller
@RequestMapping("/user")
@SessionAttributes("user")
public class UserController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IRolUserService rolService;
	
	@Autowired
	private IPeopleService peopleService;

	

	@RequestMapping(value = "/login", method = RequestMethod.POST)
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
		}

	}

	@GetMapping("/userReg")
	public String showRegistrationForm() {
		return "/pages/registeruser"; // Nombre de tu archivo HTML de registro
	}

	@RequestMapping(value = "/userReg", method = RequestMethod.POST)
	public String registerUser(@RequestParam(required = true, value = "name") String name,
			@RequestParam(required = true, value = "firstLastName") String firstLastName,
			@RequestParam(required = true, value = "secondLastName") String secondLastName,
			@RequestParam(required = true, value = "age") int age,
			@RequestParam(required = true, value = "identity") String identity,
			@RequestParam(required = true, value = "email") String email,
			@RequestParam(required = true, value = "username") String username,
			@RequestParam(required = true, value = "password") String password, Model model) {
		// Crear y guardar People
		try {
			// Crear y guardar People
			People newPeople = new People();
			newPeople.setNamePeople(name);
			newPeople.setFirstLastNamePeople(firstLastName);
			newPeople.setSecondLastNamePeople(secondLastName);
			newPeople.setAgePeople(age);
			newPeople.setIdentityPeople(identity);
			newPeople.setEmailPeople(email);
			peopleService.SavePeople(newPeople);

			// Crear y guardar User
			User newUser = new User();
			newUser.setUserName(username);
			newUser.setUserPassword(password);
			newUser.setPeople(newPeople);
			newUser.setUserState('1');

			// Asignar Rol
			Rol rol = rolService.findRolByLevel("USER"); // Cambia según tu lógica de roles
			newUser.setRoles(rol);

			userService.saveUser(newUser);

			// Mostrar SweetAlert de éxito
			String successMessage = "Usuario registrado correctamente!";
			model.addAttribute("successMessage", successMessage);

			return "/pages/registeruser"; // Redirigir a la página de login después del registro exitoso
		} catch (Exception e) {
			// Mostrar SweetAlert de error
			String errorMessage = "Error al registrar usuario. Por favor, intenta nuevamente.";
			model.addAttribute("errorMessage", errorMessage);

			return "/pages/registeruser"; // Redirigir a la página de registro con mensaje de error
		}

		// Redirigir a la página de login después del registro exitoso
	}

}
