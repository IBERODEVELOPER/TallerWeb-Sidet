package com.ibero.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IScheduleService;
import com.ibero.demo.util.DaysWeek;

@Controller
@RequestMapping("schedule")
@SessionAttributes("schedule")
public class ScheduleController {

	@Autowired
	private IScheduleService schuduleservice;
	
	@Autowired
	private IPeopleService peopleService;
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/schuduleweek/{id}")
	public String showForm(@PathVariable("id") Integer id,Model model) {
		// Obtener el empleado mediante su ID
		Employee employee =peopleService.findOnePerson(id);
		if (employee == null) {
	        // Manejar el caso en que el empleado no exista
	        model.addAttribute("error", "El empleado no existe en la base de datos.");
	        return "redirect:/peoples/listPeople";
	    }
		Schedule schedule = new Schedule();
		model.addAttribute("titleform", "Crear turno de Trabajo"); 
		model.addAttribute("FullName", "Empleado : "+employee.getFullName()); 
		model.addAttribute("emploID", employee.getId()); 
		model.addAttribute("schedule", schedule);
		return "/pages/formschuduleAll";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@PostMapping(value = "/schuduleweek/{emploID}")
	public String processFormAll(@PathVariable("emploID") Integer id,@ModelAttribute Schedule schedule, BindingResult result,Model model,RedirectAttributes flash) {
		Employee employee = peopleService.findOnePerson(id);
		if (result.hasErrors()) {
			model.addAttribute("titleform", "Crear turno de Trabajo");
			flash.addFlashAttribute("error", "Sucedio un error al intentar guardar los cambios");
			return "/pages/formschudule";
		}
		schedule.setEmployee(employee); // Establecer la relación con el empleado
		// Asegurar que cada DaySchedule esté asociado con el Schedule y tenga el día asignado
	    DaysWeek[] days = DaysWeek.values();
	    for (int i = 0; i < schedule.getDaySchedules().size(); i++) {
	        DaySchedule daySchedule = schedule.getDaySchedules().get(i);
	        daySchedule.setDayWeek(days[i]);
	        daySchedule.setSchedule(schedule);
	    }
		schuduleservice.saveschudule(schedule);
		flash.addFlashAttribute("success", "Horario creado correctamente correctamente");
		return "redirect:/peoples/listPeople";
	}
}
