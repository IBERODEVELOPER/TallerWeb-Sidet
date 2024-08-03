package com.ibero.demo.controller;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.entity.Address;
import com.ibero.demo.service.IAddressService;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("peoples")
@SessionAttributes("employee")
public class PeopleController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IPeopleService peopleService;

	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@GetMapping(value = "/listPeople")
	public String ListPeople(Model model, Authentication authentication, HttpSession session,
			HttpServletRequest request) {

		if (authentication != null && authentication.isAuthenticated()) {
			// Verificar si el mensaje ya ha sido mostrado en esta sesión
			Boolean isMessageShown = (Boolean) session.getAttribute("messageShown");
			if (isMessageShown == null || !isMessageShown) {
				// Obtener la hora actual
				LocalDateTime now = LocalDateTime.now();
				// Formatear la hora en el formato deseado
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				String formattedNow = now.format(formatter);
				model.addAttribute("success", "Hola " + authentication.getName()
						+ " ha iniciado sesión con éxito,".concat("Hora de ingreso: " + formattedNow));
				// Marcar el mensaje como mostrado en esta sesión
				session.setAttribute("messageShown", true);
			}
		}
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication aut = context.getAuthentication();
		if (request.isUserInRole("ROLE_ADMIN")) {
			logger.info("HttpServletRequest Hola ".concat(aut.getName()).concat("tienes acceso"));
		} else {
			logger.info("HttpServletRequest Hola ".concat(aut.getName()).concat("NO tienes acceso"));
		}

		model.addAttribute("titlepage", "Clientes registrados en el Sistema");
		model.addAttribute("employee", peopleService.findAllPeople());
		return "/pages/allPeople";
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@GetMapping(value = "/verdata/{id}")
	public String verDatosCompleto(@PathVariable(value = "id") int id, Model model, RedirectAttributes flash) {
		Employee employee = null;
		if (id > 0) {
			employee = peopleService.findOnePerson(id);
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/peoples/listPeople";
		}
		
		model.addAttribute("titlepage", "Ficha de Datos de la Persona");
		model.addAttribute("employee", employee);
		return "/pages/profile";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/formPeople")
	public String showForm(Map<String, Object> model) {
		Employee employee = new Employee();
		employee.setAgePeople(18);
		model.put("titlepage", "Formulario de Registro de Clientes");
		model.put("titleform", "Registro de Datos");
		model.put("employee", employee);
		return "/pages/formPeople";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping(value = "/formPeople")
	public String processForm(@Valid Employee employee, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {
		
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Formulario de Registro de Clientes");
			model.addAttribute("titleform", "Registro de Datos");
			return "/pages/formPeople";
		}

	    if(employee.getId()==null){
			logger.info("Creando nuevo empleado");
			peopleService.SavePeople(employee);
			flash.addFlashAttribute("success", "Datos registrados correctamente");	
			
		}else{
			logger.info("Actualizando sus datos");
			// Actualizar solo los campos específicos si es necesario
			peopleService.SavePeople(employee);
			flash.addFlashAttribute("success", "Datos Actualizados correctamente");		
		}
	    status.setComplete();
		return "redirect:/peoples/listPeople";
	}


	@GetMapping(value = "/formPeople/{id}")
	public String editForm(@PathVariable(value = "id") int id, Map<String, Object> model, RedirectAttributes flash) {
		if (id <= 0) {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/peoples/listPeople";
		}

		Employee employee = peopleService.findOnePerson(id);
		if (employee == null) {
			flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
			return "redirect:/peoples/listPeople";
		}

		model.put("titlepage", "Formulario de Registro de Clientes");
		model.put("titleform", "Actualizar Datos");
		model.put("employee", employee);
		return "/pages/formPeople";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/deleteByIdPerson/{id}")
	public ResponseEntity<Map<String, String>> deleteIdPerson(@PathVariable(value = "id") Integer id) {
		Map<String, String> response = new HashMap<>();
		if (id > 0) {
			peopleService.deleteIdPerson(id);
			response.put("message", "¡Cliente eliminado con éxito!");
			return ResponseEntity.ok(response);

		} else {
			response.put("message", "Error al intentar eliminar el Cliente");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return false;
		}

		Authentication aut = context.getAuthentication();

		if (aut == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = aut.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));

	}

}
