package com.ibero.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.Customer;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.CustomerService;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailservice;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IPeopleService peopleService;
	
	@Autowired
	private CustomerService CustomerService;
	
	//ruta externa
	private String rootC = "C://TallerWeb-Fotos";
	// configurar el emisor
	@Value("${spring.mail.username}")
	private String mailFrom;
	// asunto del email
	private static final String subject = "Alta de acceso al sistema";

	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@GetMapping("/listUsers")
	public String showUsers(@RequestParam(name = "page",defaultValue = "0") int page,Model model) {
		Pageable pageRequest = PageRequest.of(page, 8);
		Page<UserEntity> userentity = userService.findAllUsers(pageRequest);
		PageRender<UserEntity> pageRender = new PageRender<UserEntity>("/user/listUsers", userentity);
		// Calcula el total de registros
	    long totalRecords = userentity.getTotalElements();
		model.addAttribute("titlepage", "Lista de usuarios");
		model.addAttribute("user", userentity);
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		return "/pages/allUsers";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping("/changekey")
	public String changePassword(Model model) {
		// Obtener el usuario autenticado
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Nombre del usuario autenticado
		// Recuperar los datos del usuario y del empleado relacionado desde el servicio
		UserEntity user = userService.findByUserName(username);
		model.addAttribute("titlepage", "Cambiar contraseña");
		model.addAttribute("user", new UserEntity(user.getId()));
		return "/pages/formChangePass"; // Nombre de tu archivo HTML de registro
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@PostMapping("/changekey")
	public String processFormchangePassword(@RequestParam("id") Integer id,
			@RequestParam("userPassword") String userPassword, Model model, RedirectAttributes flash) {
		try {
			boolean isPasswordValid = userService.checkPassword(id, userPassword);
			if (isPasswordValid) {
				flash.addFlashAttribute("error", "La contraseña nueva no tiene que ser igual que la anterior");
				return "redirect:/user/changekey";
			} else {
				String encodePass = passwordEncoder.encode(userPassword);
				// Actualizar la contraseña y el estado de la contraseña temporal a false "0"
				userService.updatePass(id, encodePass, false);
				flash.addFlashAttribute("success", "Contraseña actualizada exitosamente.");
			}
		} catch (Exception e) {
			flash.addFlashAttribute("error", "Hubo un problema al actualizar la contraseña. Inténtalo de nuevo.");
			return "redirect:/user/changekey";
		}
		return "redirect:/user/changekey";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@CrossOrigin(origins = "http://20.197.229.70:80")
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

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping("/profileuser")
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
		return "/pages/profileuser";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@PostMapping("/updatePhoto")
	public String updatePicture(@RequestParam("file") MultipartFile foto, @RequestParam("id") Integer id,
			RedirectAttributes flash) {
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
		return "redirect:/user/profileuser";
	}

	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@GetMapping("/formUser")
	public String showFormUser(Model model) {
		UserEntity user = new UserEntity();
		//Obtener la lista completa de empleados
		List<Employee> allPeople = peopleService.findAllPeople();
		//Empleados que ya tienen un usuario
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
		// Obtener la lista completa de clientes
	    List<Customer> allCustomers = CustomerService.findAllCustomer();
	    //IDs de los clientes que ya tienen un usuario
	    Set<Integer> customerIdsWithUsers = new HashSet<>();
	    //IDs de clientes asociados a un usuario
	    for (Customer cust : allCustomers) {
	        if (cust.getUser() != null) {
	            customerIdsWithUsers.add(cust.getId());
	        }
	    }
	    // Filtrar la lista de clientes para remover aquellos que ya tienen un usuario
	    List<Customer> availableCustomers = new ArrayList<>();
	    for (Customer customer : allCustomers) {
	        if (!customerIdsWithUsers.contains(customer.getId())) {
	            availableCustomers.add(customer);
	        }
	    }
		model.addAttribute("titlepage", "Creación de Cuenta");
		model.addAttribute("people", availablePeople);
		model.addAttribute("customers", availableCustomers);
		model.addAttribute("user", user);
		return "/pages/formUser"; // Nombre de tu archivo HTML de registro
	}

	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
	@PostMapping(value = "/formUser")
	public String processForm(@Valid UserEntity user, BindingResult result, Model model, RedirectAttributes flash,
			@RequestParam(value = "Authority", required = false) String[] roleNames) {
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Creación de usuarios");
			model.addAttribute("people", peopleService.findAllPeople());
			model.addAttribute("user", user);
			return "/pages/formUser";
		}

		// Procesar los roles seleccionados
		if (roleNames != null) {
			List<Role> roles = new ArrayList<>();
			for (String roleName : roleNames) {
				roles.add(new Role(roleName));
			}
			user.setRoles(roles);
		} else {
			user.setRoles(new ArrayList<>()); // O manejar el caso de no tener roles seleccionados
		}
		if (user.getId() == null) {
			String emailpara = null;
			String nombreremitente = null;
			if(user.getEmployee() != null) {
				emailpara = user.getEmployee().getEmailPeople();
				nombreremitente = user.getEmployee().getFullName();
			}else {
				emailpara = user.getCustomer().getEmailCustomer();
				nombreremitente = user.getCustomer().getNameComplete();
			}
			// Anotar el De Para Asunto del Correo
			EmailValuesDTO dto = new EmailValuesDTO();
			dto.setMailFrom(mailFrom);//DE
			dto.setMailTo(emailpara);//PARA
			dto.setSubject(subject);//Asunto
			dto.setEmployeename(nombreremitente);//Nombre del empleado
			dto.setUserName(user.getUserName());
			dto.setNewuserpass(user.getUserPassword());
			//enviar al service
			emailservice.sendEmailNewUser(dto);
			//guardar el nuevo usuario
			userService.saveUser(user);
			flash.addFlashAttribute("success", "Usuario creado correctamente");
		} else {
			flash.addFlashAttribute("success", "Usuario actualizado correctamente");
			userService.updateUser(user.getId(), user.getUserPassword(), user.getUserestado(), user.getRoles());
		}
		return "redirect:/user/listUsers";
	}

	@Secured({"ROLE_ADMIN","ROLE_MANAGER"})
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
