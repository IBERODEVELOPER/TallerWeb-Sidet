package com.ibero.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IUserService userService;

	@Autowired
	private IPeopleService peopleService;

	@GetMapping("/listUsers")
	public String showUsers(Model model) {
		model.addAttribute("titlepage", "Lista de usuarios");
		model.addAttribute("user", userService.findAllUsers());
		return "/pages/allUsers"; // Nombre de tu archivo HTML de registro
	}
	
	@GetMapping("/perfil")
    public String showProfile(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Nombre del usuario autenticado
        // Recuperar los datos del usuario y del empleado relacionado desde el servicio
        UserEntity user = userService.findByUserName(username);
        Employee employee = peopleService.findByUserEntity(user);
        // Pasar los datos al modelo
        model.addAttribute("user", user);
        model.addAttribute("employee", employee);
        return "/pages/profile";
    }

	@GetMapping("/formUser")
	public String showFormUser(Model model) {
		UserEntity user = new UserEntity();
		// Paso 1: Obtener la lista completa de empleados
		List<Employee> allPeople = peopleService.findAllPeople();
		// Crear un conjunto para almacenar los IDs de los empleados que ya tienen un usuario
		Set<Integer> employeeIdsWithUsers = new HashSet<>();
		// Paso 3: Rellenar el conjunto con los IDs de empleados asociados a un usuario
		for (Employee empl : allPeople) {
		    if (empl.getUserEntity() != null) {
		        employeeIdsWithUsers.add(empl.getUserEntity().getId());
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
			flash.addFlashAttribute("success", "Usuario creado correctamente");
			userService.saveUser(user);
		} else {
			logger.info("Actualizando datos de usuario");
			flash.addFlashAttribute("success", "Usuario actualizado correctamente");
			userService.updateUser(user.getId(), user.getUserPassword(), user.getUserestado(),user.getRoles());
		}
		return "redirect:/user/listUsers";
	}

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
