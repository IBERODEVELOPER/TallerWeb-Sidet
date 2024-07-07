package com.ibero.demo.controller;

import com.ibero.demo.entity.People;
import com.ibero.demo.service.IPeopleService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Peoples")
@SessionAttributes("people")
public class PeopleController {

	@Autowired
	private IPeopleService peopleService;

	@RequestMapping(value = "/listPeople", method = RequestMethod.GET)
	public String ListPeople(Model model) {
		model.addAttribute("titlepage", "Clientes registrados en el Sistema");
		model.addAttribute("people", peopleService.findAllPeople());
		return "/pages/allUser";
	}
	
	@RequestMapping(value = "/formPeople", method = RequestMethod.GET)
	public String showForm(Map<String, Object> model) {
	People people = new People();
	model.put("titlepage", "Formulario de Registro de Clientes");
	model.put("titleform", "Registro de Datos");
	model.put("people",people);
	return "/pages/formPeople";
	}
	
	@RequestMapping(value = "/formPeople", method = RequestMethod.POST)
	public String processForm(@Valid People people,BindingResult result,Model model,RedirectAttributes flash, SessionStatus status) {
	if(result.hasErrors()) {
		model.addAttribute("titlepage", "Formulario de Registro de Clientes");
		model.addAttribute("titleform", "Registro de Datos");
		return "/pages/formPeople";
	}
	flash.addFlashAttribute("success", "Registro con Exito");
	peopleService.SavePeople(people);
	status.setComplete();
	return "redirect:/Peoples/listPeople";
	}

	@RequestMapping(value = "/formPeople/{id}", method = RequestMethod.GET)
	public String editForm(@PathVariable(value = "id") int id,Map<String, Object> model,RedirectAttributes flash) {
		People people = null;
		if(id > 0) {
			people = peopleService.findOnePerson(id);
			flash.addFlashAttribute("success", "Datos actualizados con Exito");
			if(people == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
				return "redirect:/Peoples/listPeople";
			}
		}else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/Peoples/listPeople";		
		}
		model.put("titlepage", "Formulario de Registro de Clientes");
		model.put("titleform", "Actualizar Datos");
		model.put("people",people);
		return "/pages/formPeople";	
	}
	
	@RequestMapping(value = "/deleteByIdPerson/{id}", method = RequestMethod.GET)
	public String deleteIdPerson(@PathVariable(value = "id") Integer id,RedirectAttributes flash) {
		if(id > 0) {			
			peopleService.deleteIdPerson(id);
			flash.addFlashAttribute("success", "¡Cliente eliminado con éxito!");
					
		}
		return "redirect:/Peoples/listPeople";
	}


}
