package com.ibero.demo.controller;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.dao.ActivityDao;
import com.ibero.demo.dao.AttendWorkDao;
import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Case;
import com.ibero.demo.entity.Event;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.AttendWork;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.ActivityService;
import com.ibero.demo.service.CaseService;
import com.ibero.demo.service.EmailService;
import com.ibero.demo.service.EventService;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;
import com.ibero.demo.util.EmailValuesDTO;
import com.ibero.demo.util.PageRender;

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
	private EventService eventservice;
	
	@Autowired
    private AttendWorkDao tardinesrecords;
	
	@Autowired
    private ActivityService activityservice;
	
	@Autowired
	private CaseService caseservice;
	
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
		List<Event> events = eventservice.findAllEvents();
		// Variables para almacenar eventos específicos
	    Event eventoSSHH = null;
	    Event eventoCoffeeBreak = null;
	    Event eventoAlmuerzo = null;
		// Recorrer la lista de eventos y filtrar por tipo
	    for (Event e : events) {
	        if ("Pausas Activas".equals(e.getTipoEvento())) {
	        	 if ("SS.HH".equals(e.getNameEvent())) {
	        		 eventoSSHH = e;
	             } else if ("Coffee Break".equals(e.getNameEvent())) {
	            	 eventoCoffeeBreak = e;
	             } else if ("Almuerzo".equals(e.getNameEvent())) {
	            	 eventoAlmuerzo = e;
	             }
	        }
	    }
	    //pasar a la vista
		model.addAttribute("titlepage", "©Registrex");
		model.addAttribute("eventoSSHH", eventoSSHH);
	    model.addAttribute("eventoCoffeeBreak", eventoCoffeeBreak);
	    model.addAttribute("eventoAlmuerzo", eventoAlmuerzo);
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
        Optional<ActivityEntity> ongoingActivity = activityservice.findActivityByUsernameAndEndTimeIsNull(usercustom.getUsername());
        // Si hay una actividad en curso, devuelve los detalles
        if (ongoingActivity.isPresent()) {
        	ActivityEntity activity = ongoingActivity.get();
        	if(!activity.getEvent().getTipoEvento().equals("Gestión de Ticket")) {
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
         
        }
        if(hasRole("ROLE_SUPPORT")||hasRole("ROLE_EMPLOYEE")||hasRole("ROLE_MANAGER")||hasRole("ROLE_ADMIN")) {
            // Buscar el empleado
    	    Employee employee = peopleService.getEmployeeWithFullSchedule(usere);
    	    // Obtener el registro de tardanza del día actual
            Optional<AttendWork> tardinessRecordOpt = tardinesrecords.findByEmployeeAndDate(employee, LocalDate.now());
            Long tardinessMinutes = tardinessRecordOpt.map(AttendWork::getTardinessMinutes).orElse(0L);
            //Obtener los eventos del día del usuario
            List<ActivityEntity> actday= activityservice.findByUserAndDate(usercustom.getUsername(),LocalDate.now());
            int totalCoffeeBreakTime = 0;
            int totalserviciosTime = 0;
            int totalalmuerzoTime = 0;
            //PRODUCTIVIDAD
            int totaladminstrativasTime = 0;
            int totalgestionesTime = 0;
            //OCUPACIÓN
            int totalocupacionTime = 0;
            double totalProductividad=0.0;
            int totalProductividadRedondeada=0;
            int totalOcupacionRedondeada=0;
            //EFECTIVIDAD
            int totalTareasCompletadas = 0;
            int tareasEsperadas = 15;
            double efectividad=0.0;
            int efectividadredondeado=0;
            for(ActivityEntity activity:actday) {
            	if("Coffee Break".equals(activity.getEvent().getNameEvent())) {
            		totalCoffeeBreakTime += activity.getTotalTime();
            	}
            	if("SS.HH".equals(activity.getEvent().getNameEvent())) {
            		totalserviciosTime += activity.getTotalTime();
            	}
            	if("Almuerzo".equals(activity.getEvent().getNameEvent())) {
            		totalalmuerzoTime += activity.getTotalTime();
            	}
            	//Para calcular su productividad efectividad y ocupación
            	if("Pausas Activas".equals(activity.getEvent().getTipoEvento())) {
            		totalocupacionTime += activity.getTotalTime();
            	}
            	if("Actividades Administrativas".equals(activity.getEvent().getTipoEvento())) {
            		totaladminstrativasTime += activity.getTotalTime();
            	}
            	if("Gestión de Ticket".equals(activity.getEvent().getTipoEvento())) {
            		totalgestionesTime += activity.getTotalTime();
            	}
            	if("Gestión de Ticket".equals(activity.getEvent().getTipoEvento()) ||
            			"Actividades Administrativas".equals(activity.getEvent().getTipoEvento())) {
            		totalTareasCompletadas++; // Incrementar las tareas completadas
            	}
            	//calculo de productividad
            	double porcentajeAdministrativas = 0.005; // 0.5%
            	double porcentajeGestiones = 0.30; // 30%
            	double porcentajePausas = 0.0; // 0%
            	//Calculamos la productividad
            	double productividadAdministrativas = totaladminstrativasTime * porcentajeAdministrativas;
            	double productividadGestiones = totalgestionesTime * porcentajeGestiones;
            	//calcular el procentaje total
            	double tiempoTotal = 510;// de 9 horas laborables 30 minutos son destinados al baño y coffee break 
            	totalProductividad = (productividadAdministrativas + productividadGestiones) / tiempoTotal * 100;
            	// Redondear el valor de productividad
            	totalProductividadRedondeada = (int) Math.round(totalProductividad);
            	//calcular ocupación
            	int totalocupacion = totaladminstrativasTime + totalgestionesTime + totalocupacionTime;
            	double ocupacion = (totalocupacion/tiempoTotal)*100;
            	totalOcupacionRedondeada=(int) Math.round(ocupacion);
            	//calcular efectividad
            	if (tareasEsperadas > 0) {
                    efectividad = (double) totalTareasCompletadas / tareasEsperadas * 100;
                    efectividadredondeado = (int) Math.round(efectividad);
                } 
            }
            // Pasar los minutos de tardanza al modelo
            model.addAttribute("tardinessMinutes", tardinessMinutes);
            model.addAttribute("totalCoffeeBreakTime", totalCoffeeBreakTime);
            model.addAttribute("totalserviciosTime", totalserviciosTime);
            model.addAttribute("totalalmuerzoTime", totalalmuerzoTime);
            model.addAttribute("productividad", totalProductividadRedondeada);
            model.addAttribute("ocupacion", totalOcupacionRedondeada);
            model.addAttribute("efectividad", efectividadredondeado);
            model.addAttribute("titlepage", "©Registrex");
    		return "/index";
        }else {
        	model.addAttribute("titlepage", "©Registrex");
        	return "redirect:/incidents";
        }
        
        
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
	public String showTrayIncidents(@RequestParam(name = "page", defaultValue = "0") int page,Model model) {
		Pageable pageRequest = PageRequest.of(page, 2);
		Page<Case> cass = caseservice.findAllCass(pageRequest);
		// Calcula el total de registros
	    long totalRecords = cass.getTotalElements();
		PageRender<Case> pageRender = new PageRender<Case>("/incidents", cass);
		// Lista para almacenar los nombres de eventos
	    List<Event> listGestiones = new ArrayList<>();
	    // Obtener todos los eventos
		List<Event> events = eventservice.findAllEvents();
		// Recorrer la lista de eventos y filtrar por tipo
	    for (Event e : events) {
	        if ("Gestión de Ticket".equals(e.getTipoEvento())) {
	        	// Agregar el evento a la lista filtrada
	        	listGestiones.add(e);
	        }
	    }
	    // Agregar la lista de tipos de eventos al modelo
	    model.addAttribute("listGestiones", listGestiones);
		model.addAttribute("caso", new Case());
		model.addAttribute("listacasos", cass);
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		return "/pages/ticketsAll";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping(value="/efforts")
	public String markEfforts(Model model, Authentication authentication) {
		//Obtiene el usuario autenticado
		CustomUserDetails usercustom = (CustomUserDetails) authentication.getPrincipal();
		UserEntity usere = usercustom.getUserEntity();
		// Verifica si hay alguna actividad en curso para este usuario y evento
		Optional<ActivityEntity> ongoingActivity = activityservice.findActivityByUsernameAndEndTimeIsNull(usercustom.getUsername());
		if (ongoingActivity.isPresent()) {
        	ActivityEntity activity = ongoingActivity.get();
        	if(activity.getEvent().getTipoEvento().equals("Gestión de Ticket")) {
        		// Cambia el patrón para coincidir con '10/9/2024, 20:05:21'
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm:ss");
             // Convierte el String a LocalDateTime
                LocalDateTime startTime = LocalDateTime.parse(activity.getStartTime(), formatter);
                // Formatea el LocalDateTime
                String startTimeISO = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                // Añadir el tiempo de inicio al modelo
                model.addAttribute("startTime", startTimeISO);
                model.addAttribute("activityId", activity.getId());
                model.addAttribute("ID",  activity.getEvent().getId());
                model.addAttribute("selectedTipoEvento", activity.getEvent().getTipoEvento());
                model.addAttribute("eventName", activity.getEvent().getNameEvent());
        	}
         
        }
		// Lista para almacenar los nombres de eventos
	    List<Event> listGestiones = new ArrayList<>();
	 // Obtener todos los eventos
		List<Event> events = eventservice.findAllEvents();
		// Recorrer la lista de eventos y filtrar por tipo
	    for (Event e : events) {
	        if ("Gestión de Ticket".equals(e.getTipoEvento())) {
	        	// Agregar el evento a la lista filtrada
	        	listGestiones.add(e);
	        }
	    }
	 // Agregar la lista de tipos de eventos al modelo
	    model.addAttribute("listGestiones", listGestiones);
		return "/pages/markEfforts";
	}
	
	// Validar Role
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
