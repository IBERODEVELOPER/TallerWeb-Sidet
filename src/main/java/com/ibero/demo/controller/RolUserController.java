package com.ibero.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.Role;
import com.ibero.demo.service.IRolUserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/role")
@SessionAttributes("role")
public class RolUserController {

	@Autowired
	private IRolUserService rolUserService;
	
	@GetMapping(value = "/list")
	public String ListPeople(Model model) {
		model.addAttribute("titlepage", "Roles del Sistema");
		model.addAttribute("role", rolUserService.findAllRol());
		return "/pages/allRol";
	}
	
	@GetMapping(value = "/formrol")
	public String showForm(Map<String, Object> model) {
	Role role = new Role();
	model.put("titlepage", "Formulario de Registro de Roles del Sistema");
	model.put("titleform", "Registro de Datos");
	model.put("role",role);
	return "/pages/formRol";
	}
	
	@PostMapping(value = "/formrol")
	public String processForm(@Valid Role rol,BindingResult result,Model model,RedirectAttributes flash, SessionStatus status) {
	if(result.hasErrors()) {
		model.addAttribute("titlepage", "Formulario de Registro de Roles del Sistema");
		model.addAttribute("titleform", "Registro de Datos");
		return "/pages/formRol";
	}
	flash.addFlashAttribute("success", "Registro con Exito");
	rolUserService.saveRol(rol);
	status.setComplete();
	return "redirect:/role/list";
	}

	@GetMapping(value = "/formrol/{id}")
	public String editForm(@PathVariable(value = "id") int id,Map<String, Object> model,RedirectAttributes flash) {
		Role role = null;
		if(id > 0) {
			role = rolUserService.findOneRol(id);
			flash.addFlashAttribute("success", "Datos actualizados con Exito");
			if(role == null) {
				flash.addFlashAttribute("error", "El ID del Rol no existe en la BBDD");
				return "redirect:/rol/list";
			}
		}else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/rol/listRol";	
		}
		model.put("titlepage", "Formulario de Registro de Roles");
		model.put("titleform", "Actualizar Datos");
		model.put("role",role);
		return "/pages/formRol";	
	}
	
	@GetMapping(value = "/deleteByIdRol/{id}")
	public String deleteIdRol(@PathVariable(value = "id") Integer id,RedirectAttributes flash) {
		if(id > 0) {			
			rolUserService.deleteIdRol(id);
			flash.addFlashAttribute("success", "Rol eliminado con éxito");				
		}
		return "redirect:/role/list";
	}

}
