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
import com.ibero.demo.view.xlsx.ReporteAsistenciaXlsxView;

import jakarta.servlet.http.HttpServletRequest;
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

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/general_report")
	public String showStadistics(Model model) {
		model.addAttribute("titlepage", "©Registrex");
		return "/pages/general_report";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/reports_export")
	public String showReportsExp(Model model) {
		model.addAttribute("titlepage", "©Registrex");
		return "/pages/reports_exports";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/asistence_general")
	public String showReportsAsi(@RequestParam(name = "page", defaultValue = "0") int page
			, Model model, HttpServletRequest request) {
		Pageable pageRequest = PageRequest.of(page, 8);
		//Para manejar la url
		String currentUri = request.getRequestURI();
	    boolean isReportPage = "/reports/asistence_general".equals(currentUri);
	    // Obtiene la paginación de los registros
		Page<TardinessRecord> tardins = tardinesservice.findAllTardingreport(pageRequest);
		// Calcula el total de registros
	    long totalRecords = tardins.getTotalElements();
	    // Prepara la paginación para la vista
		PageRender<TardinessRecord> pageRender = new PageRender<TardinessRecord>("/reports/asistence_general", tardins);
		// Agrega atributos al modelo para ser utilizados en la vista
		model.addAttribute("titlepage", "Reporte de Asistencia con tardanzas");
		model.addAttribute("tardins", tardins);
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		model.addAttribute("isReportPage", isReportPage);
		return "/pages/go_works";
	}

	// buscar empleado por su dni
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/seachby")
	public String findEmployeeByDocumento(
			@RequestParam(name = "documentNumber", required = false) String documentNumber,
			@RequestParam(name = "month", required = false) Integer month,
			@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		if (documentNumber != null && !documentNumber.isEmpty()) {
			Employee employee = peopleService.findByIdentityPeople(documentNumber);
			if (employee != null) {
				Pageable pageRequest = PageRequest.of(page, 8);
				Page<TardinessRecord> tardinessRecords;
				PageRender<TardinessRecord> pageRender;
				//Para mostrar el total de los registros
				long totalRecords=0;
				if (month != null) {
	                tardinessRecords = tardinesservice.findTardinessByEmployeeAndMonth(employee, month, pageRequest);
	             // Calcula el total de registros
	        	 totalRecords = tardinessRecords.getTotalElements();
	        	 // Prepara la paginación para la vista
	                pageRender = new PageRender<>("/reports/seachby?documentNumber=" + documentNumber + "&month=" + month, tardinessRecords);
	            } else {
	                tardinessRecords = tardinesservice.findTardinessByEmployee(employee, pageRequest);
	             // Calcula el total de registros
	                totalRecords = tardinessRecords.getTotalElements();
	             // Prepara la paginación para la vista
	                pageRender = new PageRender<>("/reports/seachby?documentNumber=" + documentNumber, tardinessRecords);
	            }
				// Agrega atributos al modelo para ser utilizados en la vista
	            model.addAttribute("tardins", tardinessRecords);
	            model.addAttribute("page", pageRender);
	            model.addAttribute("documentNumber", documentNumber);
	            model.addAttribute("selectedMonth", month);
	            model.addAttribute("totalRecords", totalRecords);
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

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/seachbydpf", produces = "application/pdf")
	public ModelAndView exportPDF(@RequestParam(name = "documentNumber", required = false) String documentNumber,
								  @RequestParam(name = "month", required = false) String monthStr,
	                              @RequestParam(name = "page", defaultValue = "0") int page,
	                              Model model) {
	    ModelAndView modelAndView = new ModelAndView("/pages/go_works");
	    Integer month = null;
	    if (monthStr != null && !monthStr.isEmpty() && !"null".equals(monthStr)) {
	        try {
	            month = Integer.parseInt(monthStr);
	        } catch (NumberFormatException e) {
	            // Manejar el error de formato si es necesario
	        }
	    }
	    if (documentNumber != null && !documentNumber.isEmpty()) {
	        Employee employee = peopleService.findByIdentityPeople(documentNumber);
	        if (employee != null) {
	        	List<TardinessRecord> allTardinessRecords;
	        	if (month != null && month > 0) {
	        		//obtenemos todos los registros de tardanza con el mes
		            allTardinessRecords = tardinesservice.findTardinessByEmployeeAndMonth(employee, month);
	        	}else {
	        		//obtenemos todos los registros de tardanza sin el mes
	        		allTardinessRecords = tardinesservice.findByEmployee(employee);
	        	}
	            // Pasamos todos los registros al modelo
	            model.addAttribute("tardins", allTardinessRecords); 
	        }
	    }
	    return modelAndView;
	}
	//Reporte General XLSX
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/asistence_generalxlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ModelAndView exportExcelGen(Model model) {
		ModelAndView modelAndView = new ModelAndView(new ReporteAsistenciaXlsxView());
		model.addAttribute("tardins", tardinesservice.findAllReports());
		return modelAndView;
	}
	//Reporte EXCEL por número de documento y/o mes
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/seachbyexcel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ModelAndView exportExcel(@RequestParam(name = "documentNumber", required = false) String documentNumber,
									@RequestParam(name = "month", required = false) String monthStr,
	                                @RequestParam(name = "page", defaultValue = "0") int page,
	                                Model model) {

	    ModelAndView modelAndView = new ModelAndView(new ReporteAsistenciaXlsxView());
	    Integer month = null;
	    if (monthStr != null && !monthStr.isEmpty() && !"null".equals(monthStr)) {
	        try {
	            month = Integer.parseInt(monthStr);
	        } catch (NumberFormatException e) {
	            // Manejar el error de formato si es necesario
	        }
	    }
	    if (documentNumber != null && !documentNumber.isEmpty()) {
	        Employee employee = peopleService.findByIdentityPeople(documentNumber);
	        if (employee != null) {
	        	List<TardinessRecord> allTardinessRecords;
	        	if (month != null && month > 0) {
	        		//obtenemos todos los registros de tardanza con el mes
		            allTardinessRecords = tardinesservice.findTardinessByEmployeeAndMonth(employee, month);
	        	}else {
	        		//obtenemos todos los registros de tardanza sin el mes
	        		allTardinessRecords = tardinesservice.findByEmployee(employee);
	        	}
	            // Pasar los registros al modelo
	            model.addAttribute("tardins", allTardinessRecords);
	        }
	    }

	    return modelAndView;
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN"})
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

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN"})
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
	
	@Secured({"ROLE_ADMIN"})
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
