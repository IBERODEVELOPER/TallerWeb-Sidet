package com.ibero.demo.controller;

import com.ibero.demo.entity.People;
import com.ibero.demo.entity.User;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("peoples")
@SessionAttributes("people")
public class PeopleController {

	@Autowired
	private IUserService userService;

	@Autowired
	private IPeopleService peopleService;

	@GetMapping(value = "/listPeople")
	public String ListPeople(Model model) {
		model.addAttribute("titlepage", "Clientes registrados en el Sistema");
		model.addAttribute("people", peopleService.findAllPeople());
		return "/pages/allPeople";
	}
	
	@GetMapping(value = "/verdata/{id}")
	public String verDatosCompleto(@PathVariable(value = "id") int id,Model model, RedirectAttributes flash) {
		People people = null;
		if(id > 0) {
			people = peopleService.findOnePerson(id);
		}else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/peoples/listPeople";
		}
		model.addAttribute("titlepage", "Ficha de Datos de la Persona");
		model.addAttribute("titleform", "Actualizar Datos");
		model.addAttribute("people", people);
		return "/pages/profile";
	}

	@GetMapping(value = "/formPeople")
	public String showForm(Map<String, Object> model) {
		People people = new People();
		people.setAgePeople(18);
		model.put("titlepage", "Formulario de Registro de Clientes");
		model.put("titleform", "Registro de Datos");
		model.put("people", people);
		return "/pages/formPeople";
	}

	@PostMapping(value = "/formPeople")
	public String processForm(@Valid People people, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {
		User user = people.getUser();
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Formulario de Registro de Clientes");
			model.addAttribute("titleform", "Registro de Datos");
			return "/pages/formPeople";
		}
		flash.addFlashAttribute("success", "Registro con Exito");
		
		if(people.getId()==null) {
			peopleService.SavePeople(people);			
		}
		
		if(people.getId() > 0){
			user.setUserName(people.getUser().getUserName());
			user.setUserPassword(people.getUser().getUserPassword());
			user.setPeople(people);
			user= userService.save(user);
		}
		status.setComplete();
		return "redirect:/peoples/listPeople";
	}

	@GetMapping(value = "/formPeople/{id}")
	public String editForm(@PathVariable(value = "id") int id, Map<String, Object> model, RedirectAttributes flash) {
		People people = null;
		if (id > 0) {
			people = peopleService.findOnePerson(id);
			flash.addFlashAttribute("success", "Datos actualizados con Exito");
			if (people == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
				return "redirect:/peoples/listPeople";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/peoples/listPeople";
		}
		model.put("titlepage", "Formulario de Registro de Clientes");
		model.put("titleform", "Actualizar Datos");
		model.put("people", people);
		return "/pages/formPeople";
	}

	@GetMapping(value = "/deleteByIdPerson/{id}")
	public String deleteIdPerson(@PathVariable(value = "id") Integer id, RedirectAttributes flash) {
		if (id > 0) {
			peopleService.deleteIdPerson(id);
			flash.addFlashAttribute("success", "¡Cliente eliminado con éxito!");

		}
		return "redirect:/peoples/listPeople";
	}
}
