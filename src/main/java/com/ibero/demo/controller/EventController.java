package com.ibero.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Event;
import com.ibero.demo.service.EventService;
import com.ibero.demo.util.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("event")
@SessionAttributes("event")
public class EventController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EventService eventservice;
	
	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@GetMapping(value = "/online")
	public String showStadistics(Model model) {
		model.addAttribute("titlepage", "Eventos en Tiempo Real");
		return "/pages/onlineEvent";
	}
	
	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@GetMapping(value = "/formevent")
	public String showEvents(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		//Mando objetos vacios
		Event event = new Event();
		Pageable pageRequest = PageRequest.of(page, 8);
		//Lista de eventos
		Page<Event> events =eventservice.allEvents(pageRequest);
		// Calcula el total de registros
	    long totalRecords = events.getTotalElements();
	    PageRender<Event> pageRender = new PageRender<Event>("/event/formevent", events);
		model.addAttribute("titlepage", "Crear nuevo evento");
		model.addAttribute("titlelist", "Eventos del Día");
		model.addAttribute("event", event);//Para el formulario
		model.addAttribute("events",events);//Listar los eventos
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		return "/pages/formevent";
	}
	
	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@PostMapping(value = "/formevent")
	public String processEvents(@Valid Event event,BindingResult result , Model model,RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Crear nuevo evento");
			model.addAttribute("event", event);
			return "/pages/formevent";
		}
		if(event.getId()==null) {
			eventservice.saveEvent(event);
			flash.addFlashAttribute("success", "Evento creado correctamente");
		}else {
			eventservice.saveEvent(event);
			flash.addFlashAttribute("success", "Evento Actualizados correctamente");
		}
		status.setComplete();
		return "redirect:/event/formevent";
	}
	
	@Secured({"ROLE_ADMIN","ROLE_EMPLOYEE", "ROLE_SUPPORT","ROLE_CUSTOMER"})
	@GetMapping(value = "/listevents", produces = "application/json")
	@ResponseBody
	public List<Event> listEventJson() {
		// Lista para almacenar los nombres de eventos administrativos
	    List<Event> eventosAdministrativos = new ArrayList<>();
	 // Obtener todos los eventos
		List<Event> events = eventservice.findAllEvents();
		// Recorrer la lista de eventos y filtrar por tipo
	    for (Event e : events) {
	        if ("Actividades Administrativas".equals(e.getTipoEvento())) {
	        	// Agregar el evento a la lista filtrada
	            eventosAdministrativos.add(e);
	        }
	    }
		return eventosAdministrativos;
	}
	
	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@GetMapping(value = "/eventEditBy/{id}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Event> editForm(@PathVariable(value = "id") int id) {
	    if (id <= 0) {
	        return ResponseEntity.badRequest().build();
	    }
	    Event event = eventservice.findEventById(id);
	    if (event == null) {
	        return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.ok(event); 
	    }
	
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/deleteById/{id}")
	public String deleteById(@PathVariable(value = "id") Integer id,RedirectAttributes flash) {
		if(id > 0) {
			Event event = eventservice.findEventById(id);
			eventservice.findEventById(event.getId());
			flash.addFlashAttribute("success", "Evento "+ event.getId()+" eliminado con éxito");			
		}
		return "redirect:/event/formevent";
	}
}
