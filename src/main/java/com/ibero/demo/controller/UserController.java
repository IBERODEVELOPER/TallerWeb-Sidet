package com.ibero.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.EmailService;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IScheduleService;
import com.ibero.demo.service.IUserService;
import com.ibero.demo.util.EmailValuesDTO;
import com.ibero.demo.util.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IUserService userService;
	
	//ruta externa
	private String rootC = "C://TallerWeb-Fotos";

	// configurar el emisor
	@Value("${spring.mail.username}")
	private String mailFrom;
	// asunto del email
	private static final String subject = "Alta de acceso al sistema";
	@Autowired
	private EmailService emailservice;
	@Autowired
	private IPeopleService peopleService;
	@Autowired
	private IScheduleService schuduleservice;

	@Secured({"ROLE_ADMIN" })
	@GetMapping("/listUsers")
	public String showUsers(@RequestParam(name = "page",defaultValue = "0") int page,Model model) {
		Pageable pageRequest = PageRequest.of(page, 10);
		Page<UserEntity> userentity = userService.findAllUsers(pageRequest);
		PageRender<UserEntity> pageRender = new PageRender<UserEntity>("/user/listUsers", userentity);
		model.addAttribute("titlepage", "Lista de usuarios");
		model.addAttribute("user", userentity);
		model.addAttribute("page", pageRender);
		return "/pages/allUsers";
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping("/changekey")
	public String changePassword(Model model) {
		// Obtener el usuario autenticado
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Nombre del usuario autenticado
		// Recuperar los datos del usuario y del empleado relacionado desde el servicio
		UserEntity user = userService.findByUserName(username);
		model.addAttribute("titlepage", "Cambiar contraseña");
		model.addAttribute("user", new UserEntity(user.getId()));
		logger.info("id : " + user.getId());
		return "/pages/formChangePass"; // Nombre de tu archivo HTML de registro
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@PostMapping("/changekey")
	public String processFormchangePassword(@RequestParam("id") Integer id,
			@RequestParam("userPassword") String userPassword, Model model, RedirectAttributes flash) {
		try {
			boolean isPasswordValid = userService.checkPassword(id, userPassword);

			if (isPasswordValid) {
				flash.addFlashAttribute("error", "La contraseña nueva no tiene que ser igual que la anterior");
				logger.info("Resultado : " + isPasswordValid);
				return "redirect:/user/changekey";
			} else {
				logger.info("Resultado : " + isPasswordValid);
				String encodePass = passwordEncoder.encode(userPassword);
				// Actualizar la contraseña y el estado de la contraseña temporal a false "0"
				userService.updatePass(id, encodePass, false);
				flash.addFlashAttribute("success", "Contraseña actualizada exitosamente.");
			}
		} catch (Exception e) {
			logger.error("Error actualizando la contraseña", e);
			flash.addFlashAttribute("error", "Hubo un problema al actualizar la contraseña. Inténtalo de nuevo.");
			return "redirect:/user/changekey";
		}

		return "redirect:/user/changekey";// return "redirect:/user/perfil";
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@CrossOrigin(origins = "http://20.197.224.167:8080")
	@GetMapping(value = "/password/{username}")
	public ResponseEntity<String> getPassword(@PathVariable String username, @RequestParam String currentPassword) {
		String password = userService.getPasswordByUsername(username);
		// Verifica si la nueva contraseña coincide con la almacenada
		if (password != null && passwordEncoder.matches(currentPassword, password)) {
			return ResponseEntity.ok("Contraseña correcta");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
		}
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping("/perfil")
	public String showProfile(Model model) {
		// Obtener el usuario autenticado
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Nombre del usuario autenticado
		// Recuperar los datos del usuario y del empleado relacionado desde el servicio
		UserEntity user = userService.findByUserName(username);
		Employee employee = peopleService.findByUserEntity(user);
		// Recuperar el horario asociado al empleado
	    //Schedule schedule = schuduleservice.findByEmployee(employee);
	    // Obtenemos mediante el empleado su referencia con el horario
	 	List<Schedule> schedules = employee.getSchedule();
		// Pasar los datos al modelo
		model.addAttribute("user", user);
		model.addAttribute("employee", employee);
		model.addAttribute("schedules", schedules);
		return "/pages/profile";
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("/formUser")
	public String showFormUser(Model model) {
		UserEntity user = new UserEntity();
		// Paso 1: Obtener la lista completa de empleados
		List<Employee> allPeople = peopleService.findAllPeople();
		// Crear un conjunto para almacenar los IDs de los empleados que ya tienen un
		// usuario
		Set<Integer> employeeIdsWithUsers = new HashSet<>();
		// Paso 3: Rellenar el conjunto con los IDs de empleados asociados a un usuario
		for (Employee empl : allPeople) {
			if (empl.getUserEntity() != null) {
				employeeIdsWithUsers.add(empl.getId());
			}
		}
		// Filtrar la lista de empleados para remover aquellos que ya tienen un usuario
		List<Employee> availablePeople = new ArrayList<>();
		for (Employee person : allPeople) {
			if (!employeeIdsWithUsers.contains(person.getId())) {
				availablePeople.add(person);
			}
		}
		model.addAttribute("titlepage", "Creación de Cuenta");
		model.addAttribute("people", availablePeople);
		model.addAttribute("user", user);
		return "/pages/formUser"; // Nombre de tu archivo HTML de registro
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping(value = "/formUser")
	public String processForm(@Valid UserEntity user, BindingResult result, Model model, RedirectAttributes flash,
			@RequestParam(value = "Authority", required = false) String[] roleNames) {
		if (result.hasErrors()) {
			logger.info("Sucedio un error hasErrors()");
			model.addAttribute("titlepage", "Creación de usuarios");
			model.addAttribute("people", peopleService.findAllPeople());
			model.addAttribute("user", user);
			return "/pages/formUser";
		}

		// Procesar los roles seleccionados
		if (roleNames != null) {
			List<Role> roles = new ArrayList<>();
			for (String roleName : roleNames) {
				logger.info("Role " + roleName);
				roles.add(new Role(roleName));
			}
			user.setRoles(roles);
		} else {
			user.setRoles(new ArrayList<>()); // O manejar el caso de no tener roles seleccionados
		}

		if (user.getId() == null) {
			logger.info("Creando nuevo usuario");
			// Anotar el De Para Asunto del Correo
			EmailValuesDTO dto = new EmailValuesDTO();
			dto.setMailFrom(mailFrom);//DE
			dto.setMailTo(user.getEmployee().getEmailPeople());//PARA
			dto.setSubject(subject);//Asunto
			dto.setEmployeename(user.getEmployee().getFullName());//Nombre del empleado
			dto.setUserName(user.getUserName());
			dto.setNewuserpass(user.getUserPassword());
			//enviar al sevice
			emailservice.sendEmailNewUser(dto);
			//guardar el nuevo usuario
			userService.saveUser(user);
			flash.addFlashAttribute("success", "Usuario creado correctamente");
			
		} else {
			logger.info("Actualizando datos de usuario");
			flash.addFlashAttribute("success", "Usuario actualizado correctamente");
			userService.updateUser(user.getId(), user.getUserPassword(), user.getUserestado(), user.getRoles());
		}
		return "redirect:/user/listUsers";
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/formUser/{id}")
	public String ListPeopleconcredenciales(@PathVariable(value = "id") int id, Model model, RedirectAttributes flash) {
		UserEntity user = userService.findOneUser(id);
		if (user == null) {
			flash.addFlashAttribute("error", "El usuario no existe en la base de datos");
			return "redirect:/peoples/listPeople";
		}
		model.addAttribute("titlepage", "Actualizar Credenciales");
		model.addAttribute("user", user);
		return "/pages/formUser";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/deleteByIdUser/{id}")
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

}
