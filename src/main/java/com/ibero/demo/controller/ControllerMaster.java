package com.ibero.demo.controller;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.dao.ActivityRepository;
import com.ibero.demo.dao.TardinessRecordDao;
import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Event;
import com.ibero.demo.entity.TardinessRecord;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.EmailService;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;
import com.ibero.demo.util.EmailValuesDTO;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class ControllerMaster {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IPeopleService peopleService;

	@Autowired
	private IUserService userService;

	@Autowired
	private EmailService emailservice;
	
	@Autowired
    private TardinessRecordDao tardinesrecords;
	
	@Autowired
    private ActivityRepository activityRepository;
	
	//configurar el emisor 
	@Value("${spring.mail.username}")
	private String mailFrom;
	//asunto del email
	private static final String subject = "Credencial Temporal de Acceso";
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping(value = "/events")
	public String showEvents(Model model) {
		model.addAttribute("titlepage", "©Registrex");
	     return "/pages/showevents";
	}

	
	@GetMapping(value = "/login")
	public String showLoginForm(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, Principal principal) {
		model.addAttribute("titlepage", "©Registrex");
		if (error != null) {model.addAttribute("error", "¡Error al intentar iniciar sesión!");}

		if (logout != null) {model.addAttribute("success", "¡Cerro sesión con exito!");	}
		
		if (principal != null) {return "redirect:/index";}
		return "/login";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping("/index")
	public String showIndex(Model model, Authentication authentication) {
		//Obtiene el usuario autenticado
		CustomUserDetails usercustom = (CustomUserDetails) authentication.getPrincipal();
	    UserEntity usere = usercustom.getUserEntity();
	    // Verifica si hay alguna actividad en curso para este usuario y evento
        Optional<ActivityEntity> ongoingActivity = activityRepository.findOngoingActivityByEventAndUser(usercustom.getUsername());
        // Si hay una actividad en curso, devuelve los detalles
        if (ongoingActivity.isPresent()) {
        	ActivityEntity activity = ongoingActivity.get();
         // Cambia el patrón para coincidir con '10/9/2024, 20:05:21'
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm:ss");
         // Convierte el String a LocalDateTime
            LocalDateTime startTime = LocalDateTime.parse(activity.getStartTime(), formatter);
            // Formatea el LocalDateTime
            String startTimeISO = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            // Añadir el tiempo de inicio al modelo
            model.addAttribute("startTime", startTimeISO);
            model.addAttribute("activityId", activity.getId());
            model.addAttribute("selectedTipoEvento", activity.getEvent().getTipoEvento());
            model.addAttribute("eventName", activity.getEvent().getNameEvent());
        }
        // Buscar el empleado
	    Employee employee = peopleService.getEmployeeWithFullSchedule(usere);
	    // Obtener el registro de tardanza del día actual
        Optional<TardinessRecord> tardinessRecordOpt = tardinesrecords.findByEmployeeAndDate(employee, LocalDate.now());
        Long tardinessMinutes = tardinessRecordOpt.map(TardinessRecord::getTardinessMinutes).orElse(0L);
        // Pasar los minutos de tardanza al modelo
        model.addAttribute("titlepage", "©Registrex");
        model.addAttribute("tardinessMinutes", tardinessMinutes);
		return "/index";
	}
	
	@Secured({"ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping("/outofturn")
	public String showoutofturn(Model model) {
		StringBuilder mensaje = new StringBuilder("Sr(a). Empleado");
		mensaje.append(" Usted se encuentra fuera del turno de trabajo");
		mensaje.append(" o no se encuentra programado para su ingreso en estas horas.");
		model.addAttribute("titlepage", "Fuera del horario de atención.");
		model.addAttribute("mensajeoutofturn", mensaje.toString());
		return "/outofturn";
	}


	@PostMapping("/send-email") //
	public String sendRestartPass(@RequestParam("mailTo") String mailTo, RedirectAttributes flash) {
		Optional<Employee> usuarioOpt = peopleService.findByEmailPeople(mailTo);
		if (usuarioOpt.isPresent()) {
			Employee empl = usuarioOpt.get();
			UserEntity user = userService.findOneUser(empl.getUserEntity().getId());
			if(user == null) {
			}else {
			// Genera una nueva contraseña aleatoria
			String newPassword = generateRandomPassword(8);
			//encriptación de la nueva contraseña
			user.setUserPassword(passwordEncoder.encode(newPassword));
			user.setTemporaryPassword(true);
			userService.save(user);
			//Anotar el De Para Asunto del Correo
			EmailValuesDTO dto = new EmailValuesDTO();
			dto.setMailFrom(mailFrom);//De
			dto.setMailTo(empl.getEmailPeople());//Para
			dto.setSubject(subject);//Asunto
			//Se necesita enviar el usuario propietario del correo
			dto.setUserName(user.getUserName());
			//Contraseña temporal 
			dto.setNewuserpass(newPassword);
			//para el nombre del empleado
			dto.setEmployeename(empl.getFullName());
			flash.addFlashAttribute("success", "Credenciales enviado a sus correo electronico");
			// Envía la nueva contraseña al correo electrónico
			emailservice.sendEmail(dto);
			return "redirect:/login";
			}
		} else {
			// Mensaje de error si no se encuentra el email
			flash.addFlashAttribute("error", "No existe ninguna coincidencia con el correo proporcionado");
			return "redirect:/login";
		}
		return "redirect:/login";
	}

	// generar contraseña aleatoria
	private String generateRandomPassword(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*(),.?\":{}|<>";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping(value="/incidents")
	public String showTrayIncidents() {
		return "/pages/ticketsAll";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping(value="/efforts")
	public String markEfforts() {
		return "/pages/markEfforts";
	}

}
