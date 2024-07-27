package com.ibero.demo.controller;

import com.ibero.demo.entity.People;
import com.ibero.demo.entity.User;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;

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
@SessionAttributes("people")
public class PeopleController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IUserService userService;

	@Autowired
	private IPeopleService peopleService;

	@GetMapping(value = "/listPeople")
	public String ListPeople(Model model,Authentication authentication,HttpSession session) {
		
		if(authentication != null && authentication.isAuthenticated()) {
			// Verificar si el mensaje ya ha sido mostrado en esta sesión
            Boolean isMessageShown = (Boolean) session.getAttribute("messageShown");
            if (isMessageShown == null || !isMessageShown) {
			 // Obtener la hora actual
	        LocalDateTime now = LocalDateTime.now();
	        // Formatear la hora en el formato deseado
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	        String formattedNow = now.format(formatter);
			model.addAttribute("success","Hola "+authentication.getName()+" ha iniciado sesión con éxito,".concat("Hora de ingreso: "+formattedNow));
			// Marcar el mensaje como mostrado en esta sesión
            session.setAttribute("messageShown", true);
            }
		}
		
		/*Authentication aut = SecurityContextHolder.getContext().getAuthentication();
		 * if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola".concat(aut.getName()).concat("tienes acceso"));
		}else {
			logger.info("Hola".concat(aut.getName()).concat("NO tienes acceso"));
		}Para obtener usuario logeado		if(aut != null) {mensaje con logger .getName()}*/
		
		
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
		
		if(people.getId()==null) {
			peopleService.SavePeople(people);
			flash.addFlashAttribute("success", "Datos registrados correctamente");			
		}else {
			peopleService.SavePeople(people);
			flash.addFlashAttribute("success", "Datos Actualizados correctamente");
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
	public ResponseEntity<Map<String, String>> deleteIdPerson(@PathVariable(value = "id") Integer id) {
		Map<String, String> response = new HashMap<>();
		if (id > 0) {
			peopleService.deleteIdPerson(id);
			response.put("message", "¡Cliente eliminado con éxito!");
			return ResponseEntity.ok(response);

		}else {
			response.put("message", "Error al intentar eliminar el Cliente");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);	
			}
	}
	
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if(context == null) {
			return false;
		}
		
		Authentication aut = context.getAuthentication();
		
		if(aut == null) {
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = aut.getAuthorities();
		
		return authorities.contains(new SimpleGrantedAuthority(role));
		/*for(GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				logger.info("Hola".concat(aut.getName()).concat("tu role es:").concat(authority.getAuthority()));
				return true;
			}
		}
		return false;	*/
		
		}
	
	
}
