package com.ibero.demo.controller;

import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.ibero.demo.dao.EventDao;
import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.AttendWork;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Event;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.ActivityService;
import com.ibero.demo.service.AttendWorkService;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.IUserService;
import com.ibero.demo.util.ActivityDTO;
import com.ibero.demo.util.EventHandler;
import com.ibero.demo.util.FinishActivityDTO;
import com.ibero.demo.util.StartActivityDTO;

@RestController
@RequestMapping("/api/events")
public class ActivityController {

	@Autowired
	private ActivityService activityservice;

	@Autowired
	private EventDao eventRepository;

	@Autowired
	private EventHandler eventHandler;

	@Autowired
	private IUserService userService;

	@Autowired
	private IPeopleService peopleService;

	@Autowired
	private AttendWorkService tardinesservice;

	// Obtener todas las actividades sin cerrar
	@Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR", "ROLE_EMPLOYEE", "ROLE_SUPPORT" })
	@GetMapping(value = "/actives", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ActivityDTO> getOpenActivities() {
		List<ActivityEntity> activities = activityservice.findByEndTimeIsNull();
		return activities.stream().map(activity -> {
			ActivityDTO dto = new ActivityDTO();
			dto.setId(activity.getId());
			dto.setUsername(activity.getUsername());
			dto.setEventName(activity.getEvent().getNameEvent()); // Nombre del evento
			dto.setStartTime(activity.getStartTime());
			return dto;
		}).collect(Collectors.toList());
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR", "ROLE_EMPLOYEE", "ROLE_SUPPORT" })
	@PostMapping("/start-activity")
	public ResponseEntity<?> startActivity(@RequestBody StartActivityDTO request) {
		// Obtén el evento desde el repositorio usando el ID del evento
		Event event = eventRepository.findById(Integer.parseInt(request.getActivityId()))
				.orElseThrow(() -> new RuntimeException("Evento no encontrado"));
		// Verifica si hay alguna actividad en curso para este usuario y evento
		Optional<ActivityEntity> ongoingActivity = activityservice.findActivityByUsernameAndEndTimeIsNull(request.getUsername());
		if (ongoingActivity.isPresent()) {
			// Si hay una actividad en curso, devuelve una respuesta con un mensaje de error
			return ResponseEntity.ok(Map.of("success", false, "message",
					"Ya tienes una actividad en curso. Debes finalizarla antes de iniciar una nueva."));
		}
		// Si no hay una actividad en curso, crea una nueva
		ActivityEntity activity = new ActivityEntity();
		activity.setEvent(event);
		activity.setUsername(request.getUsername());
		activity.setStartTime(request.getStartTime());
		// Guarda la nueva actividad en la base de datos
		ActivityEntity savedActivity = activityservice.saveActivity(activity);
		// Envía un mensaje a través del WebSocket para notificar sobre la nueva
		// actividad
		String username = request.getUsername();
		String eventName = event.getNameEvent();
		String startTime = request.getStartTime();
		Integer id = savedActivity.getId();
		eventHandler.sendMessageToClients(username, eventName, startTime, id.toString());
		// Devuelve una respuesta JSON exitosa con el ID de la actividad
		return ResponseEntity.ok(Map.of("success", true, "activityId", savedActivity.getId()));
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR", "ROLE_EMPLOYEE", "ROLE_SUPPORT" })
	@PostMapping("/finish-activity")
	public ResponseEntity<?> finishActivity(@RequestBody FinishActivityDTO request) {
		// Buscar la actividad en la base de datos usando el ID de la actividad
		ActivityEntity activity = activityservice.findActivityById(Integer.parseInt(request.getActivityId()));
		// Establece los datos de finalización
		activity.setEndTime(request.getEndTime());
		activity.setDetailsevent(request.getDetailsevent());
		if (request.getStatus() != null && request.getCodeAct() != null) {
			activity.setStatus(request.getStatus());
			activity.setCodeActivity(request.getCodeAct());
		} else {
			activity.setStatus("Closed");
			activity.setCodeActivity(generateUniqueId());
		}
		// Calcular el tiempo total
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm:ss");
		LocalDateTime startTime = LocalDateTime.parse(activity.getStartTime(), formatter);
		LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), formatter);
		long totalTime = ChronoUnit.MINUTES.between(startTime, endTime);
		activity.setTotalTime(totalTime); // Guarda el tiempo total en minutos
		// Enviar mensaje de cierre de la actividad al WebSocket
		String username = activity.getUsername(); // Usuario relacionado con la actividad
		String activityId = request.getActivityId(); // ID de la actividad
		boolean isClosed = true; // Indica que el evento está cerrado
		// Enviar el mensaje al WebSocket
		eventHandler.sendMessageClosed(username, activityId, isClosed);
		// Actualizar la actividad en la base de datos
		activityservice.saveActivity(activity);
		// Devolver una respuesta JSON exitosa
		return ResponseEntity.ok(Collections.singletonMap("success", true));
	}

	// Generar el codigo del evento
	private String generateUniqueId() {
		// Generar una parte numérica aleatoria
		int randomNum = (int) (Math.random() * 10000); // Genera un número de 4 dígitos
		// Generar una letra aleatoria
		char randomLetter = (char) ('A' + Math.random() * 26); // Genera una letra mayúscula aleatoria
		// Combinar "Caso-", la letra aleatoria y el número de 4 dígitos
		return String.format("Evento-%c%08d", randomLetter, randomNum);
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR", "ROLE_EMPLOYEE", "ROLE_SUPPORT" })
	@PostMapping("/register-outwork")
	public ResponseEntity<Map<String, Object>> registerExit() {
		// Obtener el usuario autenticado
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // Nombre del usuario autenticado
		// Verifica si hay alguna actividad en curso para este usuario y evento
		Optional<ActivityEntity> ongoingActivity = activityservice.findActivityByUsernameAndEndTimeIsNull(username);
		if (ongoingActivity.isPresent()) {
			// Si hay una actividad en curso, devuelve una respuesta con un mensaje de error
			Map<String, Object> response = Map.of("success", false, "message",
					"Ya tienes una actividad en curso. Debes finalizarla antes de iniciar una nueva.");
			return ResponseEntity.ok(response);
		}
		// Recuperar los datos del usuario y del empleado relacionado desde el servicio
		UserEntity user = userService.findByUserName(username);
		Employee employee = peopleService.findByUserEntity(user);
		// Recoge la fecha del día
		LocalDate today = LocalDate.now();
		// Buscar el registro de asistencia del día actual
		Optional<AttendWork> attendanceOpt = tardinesservice.findByEmployeeAndDate(employee, today);
		if (attendanceOpt.isPresent()) {
			AttendWork attendance = attendanceOpt.get();
			// Registrar la hora de salida
			attendance.setExitTime(LocalTime.now());
			// Guardar el registro actualizado
			tardinesservice.saveTarding(attendance);
			Map<String, Object> response = Map.of("success", true, "message",
					"Hora de salida registrada correctamente.");
			return ResponseEntity.ok(response);
		} else {
			Map<String, Object> response = Map.of("success", false, "message",
					"No se encontró el registro de asistencia para el día de hoy.");
			return ResponseEntity.badRequest().body(response);
		}
	}

}
