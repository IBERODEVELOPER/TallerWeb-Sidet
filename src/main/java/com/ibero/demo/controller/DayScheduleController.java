package com.ibero.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.service.IDayScheduleService;
import com.ibero.demo.service.IScheduleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("dayschedule")
@SessionAttributes("dayschedule")
public class DayScheduleController {

	
	@Autowired
	private IDayScheduleService dayservice;

	@Autowired
	private IScheduleService scheduleservice;

	// Editar horario por día
	@GetMapping(value = "/editschudule/{id}")
	public String editSchedule(@PathVariable("id") Integer id, Model model) {
		// Obtener el `Schedule` usando el servicio
		DaySchedule dayschedule = dayservice.findOneSchuduleDay(id);
		if (dayschedule == null) {
			model.addAttribute("error", "No se encontró el día en el horario.");
			return "redirect:/peoples/verschedule/" + dayschedule.getSchedule().getEmployee().getId();
		}
		Integer employeeId = dayschedule.getSchedule().getEmployee().getId(); // Obtener el ID del empleado
		String fullnameemploy = dayschedule.getSchedule().getEmployee().getFullName();// Obtener el Nombre completo del
																						// empleado
		model.addAttribute("fullnameemploy", fullnameemploy);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("dayschedule", dayschedule);
		return "/pages/formschudule";
	}

	@PostMapping(value = "/updateScheduleday")
	public String processForm(@Valid @ModelAttribute DaySchedule daySchedule, BindingResult result, Model model,
			RedirectAttributes flash) {
		if (result.hasErrors()) {
			model.addAttribute("titleform", "Registro de Datos");
			flash.addFlashAttribute("error", "Sucedio un error al intentar guardar los cambios");
			return "redirect:/dayschedule/editschudule/" + daySchedule.getId();
		}
		// Calcular horas trabajadas antes de guardar
	    daySchedule.calculateHoursWorked();
	    //guardar dayschedule
		dayservice.savesdaySchedule(daySchedule);
		flash.addFlashAttribute("success", "Datos actualizados correctamente");
		return "redirect:/peoples/verschedule/" + daySchedule.getSchedule().getEmployee().getId();
	}

	@GetMapping(value = "/deletediaby/{id}")
	public String eliminarCliente(@PathVariable(value = "id") Integer id, RedirectAttributes flash) {
		// Obtener el `Schedule` usando el servicio
		DaySchedule dayschedule = dayservice.findOneSchuduleDay(id);
		if (id > 0) {
			dayservice.deleteId(id);
			// Verificar si el `Schedule` ya no tiene más `DaySchedule`
			Schedule schedule = dayschedule.getSchedule();
			if (schedule.getDaySchedules().isEmpty()) {
				// Eliminar el `Schedule` si no tiene más `DaySchedule`
				scheduleservice.deleteId(schedule.getId());
				flash.addFlashAttribute("success", "Día de trabajo y su horario han sido eliminados con éxito");
				return "redirect:/peoples/listPeople";
			} 
		}
		flash.addFlashAttribute("success", "Día de trabajo eliminado con éxito");
		return "redirect:/peoples/verschedule/" + dayschedule.getSchedule().getEmployee().getId();
	}

}
