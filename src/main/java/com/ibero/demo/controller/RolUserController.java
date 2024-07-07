package com.ibero.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.Rol;
import com.ibero.demo.service.IRolUserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/rol")
@SessionAttributes("rol")
public class RolUserController {

	@Autowired
	private IRolUserService rolUserService;
	
	@RequestMapping(value = "/listRol", method = RequestMethod.GET)
	public String ListPeople(Model model) {
		model.addAttribute("titlepage", "Roles del Sistema");
		model.addAttribute("rol", rolUserService.findAllRol());
		return "/pages/allRol";
	}
	
	@RequestMapping(value = "/formRol", method = RequestMethod.GET)
	public String showForm(Map<String, Object> model) {
	Rol rol = new Rol();
	model.put("titlepage", "Formulario de Registro de Roles del Sistema");
	model.put("titleform", "Registro de Datos");
	model.put("rol",rol);
	return "/pages/formRol";
	}
	
	@RequestMapping(value = "/formRol", method = RequestMethod.POST)
	public String processForm(@Valid Rol rol,BindingResult result,Model model,RedirectAttributes flash, SessionStatus status) {
	if(result.hasErrors()) {
		model.addAttribute("titlepage", "Formulario de Registro de Roles del Sistema");
		model.addAttribute("titleform", "Registro de Datos");
		return "/pages/formRol";
	}
	flash.addFlashAttribute("success", "Registro con Exito");
	rolUserService.saveRol(rol);
	status.setComplete();
	return "redirect:/rol/listRol";
	}

	@RequestMapping(value = "/formRol/{id}", method = RequestMethod.GET)
	public String editForm(@PathVariable(value = "id") int id,Map<String, Object> model,RedirectAttributes flash) {
		Rol rol = null;
		if(id > 0) {
			rol = rolUserService.findOneRol(id);
			flash.addFlashAttribute("success", "Datos actualizados con Exito");
			if(rol == null) {
				flash.addFlashAttribute("error", "El ID del Rol no existe en la BBDD");
				return "redirect:/rol/listRol";
			}
		}else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/rol/listRol";	
		}
		model.put("titlepage", "Formulario de Registro de Roles");
		model.put("titleform", "Actualizar Datos");
		model.put("rol",rol);
		return "/pages/formRol";	
	}
	
	@RequestMapping(value = "/deleteByIdRol/{id}", method = RequestMethod.GET)
	public String deleteIdRol(@PathVariable(value = "id") Integer id,RedirectAttributes flash) {
		if(id > 0) {			
			rolUserService.deleteIdRol(id);
			flash.addFlashAttribute("success", "Rol eliminado con éxito");				
		}
		return "redirect:/rol/listRol";
	}

}
