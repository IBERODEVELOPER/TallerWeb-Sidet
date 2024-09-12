package com.ibero.demo.controller;

import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.service.IPeopleService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ibero.demo.util.PageRender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("peoples")
@SessionAttributes("employee")
public class PeopleController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IPeopleService peopleService;

	// ruta externa
	private String rootC = "C://TallerWeb-Fotos";

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/listPeople")
	public String ListPeople(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 8);
		Page<Employee> employee = peopleService.findAllPeople(pageRequest);
		// Calcula el total de registros
	    long totalRecords = employee.getTotalElements();
		PageRender<Employee> pageRender = new PageRender<Employee>("/peoples/listPeople", employee);
		model.addAttribute("titlepage", "Empleados registrados en el sistema");
		model.addAttribute("employee", employee);
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		return "/pages/allPeople";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/verdata/{id}")
	public String verDatosCompleto(@PathVariable(value = "id") int id, Model model, RedirectAttributes flash) {
		Employee employee = null;
		if (id > 0) {
			employee = peopleService.findOnePerson(id);
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/peoples/listPeople";
		}
		if (employee == null) {
			flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
			return "redirect:/peoples/listPeople";
		}
		// Obtenemos mediante el empleado su referencia con el horario
		List<Schedule> schedules = employee.getSchedule();
		// Calcular la suma total de horas trabajadas
		int totalHoursWorked = 0;
		for (Schedule schedule : schedules) {
			for (DaySchedule daySchedule : schedule.getDaySchedules()) {
				totalHoursWorked += daySchedule.getHoursWorked();
			}
		}
		model.addAttribute("titlepage", "Ficha de datos");
		model.addAttribute("employee", employee);
		model.addAttribute("schedules", schedules);
		model.addAttribute("totalHoursWorked", totalHoursWorked);
		return "/pages/profiles";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/formPeople")
	public String showForm(Map<String, Object> model) {
		Employee employee = new Employee();
		employee.setAgePeople(18);
		model.put("titlepage", "Registro de datos del Empleado");
		model.put("titleform", "Ficha de datos");
		model.put("employee", employee);
		return "/pages/formPeople";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@PostMapping("/updatePicture")
	public String updatePicture(@RequestParam("file") MultipartFile foto, @RequestParam("id") Integer id,
			RedirectAttributes flash) {
		logger.info("Valor obtenido: "+foto.getName());
		// obtenermos el empleado
		Employee employee = peopleService.findOnePerson(id);
		if (!foto.isEmpty()) {
			// Obtener el nombre del archivo original y la extensión
			String originalFilename = foto.getOriginalFilename();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			// Crear el nuevo nombre de archivo utilizando el nombre del empleado
			String nuevoNombreArchivo = employee.getName().replace(" ", "_") + "_" + System.currentTimeMillis()
					+ extension;
			// ruta relativa
			Path rootPath = Paths.get(rootC).resolve(nuevoNombreArchivo);
			// ruta absoluta
			Path rootAbsPath = rootPath.toAbsolutePath();
			if (employee.getFoto() != null && !employee.getFoto().isEmpty()) {
				Path pathFotoAnterior = Paths.get(rootC).resolve(employee.getFoto()).toAbsolutePath();
				File archivoFotoAnterior = pathFotoAnterior.toFile();
				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
			try {
				byte[] bytes = foto.getBytes();
				Files.write(rootPath, bytes);
				flash.addFlashAttribute("success", "Has subido correctamente '" + nuevoNombreArchivo + "'");
				flash.addFlashAttribute("mensage", "Al actualizar su foto cierre sesión para visualizar los cambios");
				employee.setFoto(nuevoNombreArchivo);
				peopleService.updateFoto(id, employee.getFoto());
			} catch (IOException e) {
				e.printStackTrace();
				flash.addFlashAttribute("error", "Sucedio un error al intentar actualizar " + e);
			}
		}else {
			flash.addFlashAttribute("error", "No hay ninguna foto seleccionado");
		}
		return "redirect:/peoples/verdata/"+id;
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@PostMapping(value = "/formPeople")
	public String processForm(@Valid Employee employee,BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Formulario de Registro de Clientes");
			model.addAttribute("titleform", "Registro de Datos");
			return "/pages/formPeople";
		}
		// Procesar foto
		if (!foto.isEmpty()) {
			// Obtener el nombre del archivo original y la extensión
			String originalFilename = foto.getOriginalFilename();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			// Crear el nuevo nombre de archivo utilizando el nombre del empleado
			String nuevoNombreArchivo = employee.getName().replace(" ", "_") + extension;
			// ruta relativa
			Path rootPath = Paths.get(rootC).resolve(nuevoNombreArchivo);
			// ruta absoluta
			Path rootAbsPath = rootPath.toAbsolutePath();
			if (employee.getId() != null && employee.getId() > 0 && employee.getFoto() != null
					&& employee.getFoto().length() > 0) {
				// Obtenemos el archivo
				File archivo = rootAbsPath.toFile();
				if (archivo.exists() && archivo.canRead()) {
					archivo.delete();
				}
			}

			try {
				byte[] bytes = foto.getBytes();
				Files.write(rootAbsPath, bytes);
				flash.addFlashAttribute("info", "Has subido correctamente '" + nuevoNombreArchivo + "'");
				employee.setFoto(nuevoNombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (employee.getId() == null) {
			peopleService.SavePeople(employee);
			flash.addFlashAttribute("success", "Datos registrados correctamente");

		} else {
			// Actualizar solo los campos específicos si es necesario
			peopleService.SavePeople(employee);
			flash.addFlashAttribute("success", "Datos Actualizados correctamente");
		}
		status.setComplete();
		return "redirect:/peoples/listPeople";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
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

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/verschedule/{id}")
	public String showFormSchudule(@PathVariable("id") Integer id, Model model, RedirectAttributes flash) {
		if (id <= 0) {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
			return "redirect:/peoples/listPeople";
		} // obtenemos el empleado mediante su id
		Employee employee = peopleService.findOnePerson(id);
		if (employee == null) {
			flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD");
			return "redirect:/peoples/listPeople";
		}
		// Obtenemos mediante el empleado su referencia con el horario
		List<Schedule> schedules = employee.getSchedule();
		// Calcular la suma total de horas trabajadas
		int totalHoursWorked = 0;
		for (Schedule schedule : schedules) {
			for (DaySchedule daySchedule : schedule.getDaySchedules()) {
				totalHoursWorked += daySchedule.getHoursWorked();
			}
		}
		model.addAttribute("titleform", "Horario");
		model.addAttribute("employee", employee);
		model.addAttribute("schedules", schedules);
		model.addAttribute("totalHoursWorked", totalHoursWorked);
		return "pages/allSchudule";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/deleteByIdPerson/{id}")
	public ResponseEntity<Map<String, String>> deleteIdPerson(@PathVariable(value = "id") Integer id) {
		Map<String, String> response = new HashMap<>();
		if (id > 0) {
			// obtenermos el empleado
			Employee employee = peopleService.findOnePerson(id);
			// eliminamos al empleado segun su id.
			peopleService.deleteIdPerson(id);
			response.put("message", "¡Empleado " + employee.getName() + " eliminado con éxito!");
			// ruta relativa
			Path rootPath = Paths.get("TallerWeb-Fotos").resolve(employee.getFoto());
			// ruta absoluta
			Path rootAbsPath = rootPath.toAbsolutePath();
			// Obtenemos el archivo
			File archivo = rootAbsPath.toFile();
			if (archivo.exists() && archivo.canRead()) {
				if (archivo.delete()) {
					response.put("info", "¡Foto " + employee.getFoto() + "eliminado con éxito!");
				}
			}
			return ResponseEntity.ok(response);
		} else {
			response.put("message", "Error al intentar eliminar al Empleado");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
	
}
