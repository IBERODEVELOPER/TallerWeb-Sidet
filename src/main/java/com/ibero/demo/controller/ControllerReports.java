package com.ibero.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.TardinessRecord;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.TardinessRecordService;
import com.ibero.demo.util.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("reports")
@SessionAttributes("tardinessrecord")
public class ControllerReports {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TardinessRecordService tardinesservice;

	@Autowired
	private IPeopleService peopleService;

	@GetMapping(value = "/general_report")
	public String showStadistics(Model model) {
		model.addAttribute("titlepage", "©Registrex");
		return "/pages/general_report";
	}

	@GetMapping(value = "/reports_export")
	public String showReportsExp(Model model) {
		model.addAttribute("titlepage", "©Registrex");
		return "/pages/reports_exports";
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/asistence_general")
	public String showReportsAsi(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 10);
		Page<TardinessRecord> tardins = tardinesservice.findAllTardingreport(pageRequest);
		PageRender<TardinessRecord> pageRender = new PageRender<TardinessRecord>("/reports/asistence_general", tardins);
		model.addAttribute("titlepage", "Reporte de Asistencia con tardanzas");
		model.addAttribute("tardins", tardins);
		model.addAttribute("page", pageRender);
		return "/pages/go_works";
	}

	// buscar empleado por su dni

	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/seachby")
	public String findEmployeeByDocumento(
			@RequestParam(name = "documentNumber", required = false) String documentNumber,
			@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		if (documentNumber != null && !documentNumber.isEmpty()) {
			Employee employee = peopleService.findByIdentityPeople(documentNumber);
			if (employee != null) {
				logger.info("Empleado: " + employee.getName() + " N°: " + employee.getIdentityPeople());
				Pageable pageRequest = PageRequest.of(page, 10);
				Page<TardinessRecord> tardinessRecords = tardinesservice.findTardinessByEmployee(employee, pageRequest);
				PageRender<TardinessRecord> pageRender = new PageRender<>(
						"/reports/seachby?documentNumber=" + documentNumber, tardinessRecords);
				model.addAttribute("tardins", tardinessRecords);
				// Añadimos los registros de tardanza al modelo
				model.addAttribute("page", pageRender);// mandamos la pagina
				model.addAttribute("documentNumber", documentNumber);// mandamos la pagina
			} else {
				logger.info("No se encontró un empleado con el documento: " + documentNumber);
				model.addAttribute("tardins", new ArrayList<>());
				// Enviamos una lista vacía si no hay registros
			}
		} else {
			logger.info("Número de documento no proporcionado");
			model.addAttribute("tardins", new ArrayList<>());
			// Enviamos una lista vacía si no se proporcionó el documento
		}
		return "/pages/go_works"; // Redirige a la página de asistencia general
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/seachbydpf", produces = "application/pdf")
	public ModelAndView exportPDF(@RequestParam(name = "documentNumber", required = false) String documentNumber,
	                              @RequestParam(name = "page", defaultValue = "0") int page,
	                              Model model) {
	    ModelAndView modelAndView = new ModelAndView("/pages/go_works");

	    if (documentNumber != null && !documentNumber.isEmpty()) {
	        Employee employee = peopleService.findByIdentityPeople(documentNumber);
	        if (employee != null) {
	        	// Aquí obtenemos todos los registros de tardanza sin paginación
	            List<TardinessRecord> allTardinessRecords = tardinesservice.findByEmployee(employee);
	            // Pasamos todos los registros al modelo
	            model.addAttribute("tardins", allTardinessRecords); 
	        }
	    }
	    return modelAndView;
	}


	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/changetarding/{id}")
	public String showReportsAsi(@PathVariable(value = "id") Integer id, Model model, RedirectAttributes flash) {
		TardinessRecord tardins = null;
		if (id > 0) {
			tardins = tardinesservice.findbyIdtardinesRecord(id);
		}
		if (tardins == null) {
			flash.addFlashAttribute("error", "El ID del reporte no existe en la BBDD");
			return "redirect:/reports/asistence_general";
		}
		model.addAttribute("titlepage", "Actualizar tardanza");
		model.addAttribute("titleform", "Justificar tardanza");
		model.addAttribute("tardins", tardins);
		return "/pages/formtardiness";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping(value = "/changetarding")
	public String processForm(@Valid TardinessRecord tarding, BindingResult result, Model model,
			RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Formulario de Registro de Clientes");
			model.addAttribute("titleform", "Registro de Datos");
			return "redirect:/reports/changetarding/" + tarding.getId();
		}
		if (tarding.getId() == null) {
			tardinesservice.saveTarding(tarding);
			flash.addFlashAttribute("success", "Datos registrados correctamente");

		} else {
			// Actualizar solo los campos específicos si es necesario
			tardinesservice.saveTarding(tarding);
			flash.addFlashAttribute("success", "Datos Actualizados correctamente");
		}
		status.setComplete();
		return "redirect:/reports/asistence_general";
	}

	@GetMapping(value = "/deleteBy/{id}")
	public String eliminarCliente(@PathVariable(value = "id") Integer id, RedirectAttributes flash) {
		TardinessRecord tardins = null;
		if (id > 0) {
			tardins = tardinesservice.findbyIdtardinesRecord(id);
			tardinesservice.deleteAsistenciaById(tardins.getId());
			flash.addFlashAttribute("success", "Registro de Asistencia eliminado");
		}
		return "redirect:/reports/asistence_general";
	}

}
