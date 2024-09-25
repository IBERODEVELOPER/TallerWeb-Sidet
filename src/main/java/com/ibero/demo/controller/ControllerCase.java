package com.ibero.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.AttachCase;
import com.ibero.demo.entity.Case;
import com.ibero.demo.entity.CommentCase;
import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.CaseService;
import com.ibero.demo.service.IAttachCaseService;
import com.ibero.demo.service.ICommentCaseService;
import com.ibero.demo.service.IUserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("case")
@SessionAttributes("case")
public class ControllerCase {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CaseService caseservice;

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IAttachCaseService attachcaseservice;
	
	@Autowired
	private ICommentCaseService commentcaseservice;

	// ruta externa
	private String rootC = "C://TallerWeb-Adjuntos";

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_SUPPORT", "ROLE_EMPLOYEE", "ROLE_CUSTOMER" })
	@PostMapping(value = "/create-case")
	public String processForm(@Valid Case cass, BindingResult result, Model model,
			@RequestParam("files") MultipartFile[] files, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			flash.addFlashAttribute("error", "Ups Sucedió algo, no se creó el caso.");
			return "/pages/ticketsAll";
		}
		// obtener usuario autenticado
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Nombre del usuario autenticado
		UserEntity user = userService.findByUserName(username);
		if (cass.getId()== null) {
			cass.setEntryDate(LocalDateTime.now());
			cass.setStatusCase("Open");
			cass.setUser(user);
			caseservice.saveCase(cass);
			flash.addFlashAttribute("success", "caso creado correctamente");

		}
		// Pocesar archivos subidos
		List<AttachCase> attachments = new ArrayList<>();
		// Verifica si los archivos están vacíos
	    if (files == null || files.length == 0) {
	        flash.addFlashAttribute("error", "No se seleccionó ningún archivo.");
	        return "/pages/ticketsAll";  // Asegúrate de tener un formulario
	    }
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				try {
				// Crear una instancia de Random para generar un número aleatorio entre 1 y 9
				Random random = new Random();
				int randomNum = random.nextInt(9) + 1;
				// Obtener el nombre del archivo original y la extensión
				String originalFilename = file.getOriginalFilename();
				String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
				// Crear el nuevo nombre de archivo utilizando el nombre del empleado
				String nuevoNombreArchivo = cass.getId().replace(" ", "_") + "_" + randomNum + extension;
				// ruta relativa
				Path rootPath = Paths.get(rootC).resolve(nuevoNombreArchivo);
				// ruta absoluta
				Path rootAbsPath = rootPath.toAbsolutePath();
				// Crear el directorio si no existe
                File directorio = new File(rootC);
                if (!directorio.exists()) {
                    directorio.mkdirs(); // Crear los directorios
                }
                // Guardar el archivo en el sistema de archivos
                Files.copy(file.getInputStream(), rootAbsPath, StandardCopyOption.REPLACE_EXISTING);
				AttachCase attachment = new AttachCase();
				// Guardar información del archivo en la entidad AttachCase
				attachment.setNameAttach(nuevoNombreArchivo);
				attachment.setDateAttach(LocalDateTime.now());
				attachment.setUploadedBy(user);
				attachment.setCas(cass);
				attachments.add(attachment);
				}catch (IOException e) {
	                flash.addFlashAttribute("error", "Error al subir el archivo " + file.getOriginalFilename());
	            }
			}
		}
		// Asignar los archivos adjuntos al caso y guardarlo
	    cass.setAttachments(attachments);
	    if (!cass.getId().isEmpty()) {
			caseservice.saveCase(cass);
		}
		status.setComplete();
		return "redirect:/incidents";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE","ROLE_CUSTOMER"})
	@GetMapping(value = "/vercasebyId/{id}")
	public String verDatosCompleto(@PathVariable(value = "id") String id, Model model, RedirectAttributes flash) {
		Case cass = null;
		if (!id.isEmpty()) {
			cass = caseservice.findCaseById(id);
		} else {
			flash.addFlashAttribute("error", "El ID del caso no puede ser 0");
			return "redirect:/incidents";
		}
		//adjuntos
		List<AttachCase> attachs = attachcaseservice.findAttachmentsByCase(cass);
		//Comentarios
		List<CommentCase> commentcass = commentcaseservice.findCommentByCase(cass);
		model.addAttribute("caso", cass);
		model.addAttribute("attachs", attachs);
		model.addAttribute("commentcass", commentcass);
		return "/pages/formCase";
	}

}
