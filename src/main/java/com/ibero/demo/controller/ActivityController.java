package com.ibero.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.ibero.demo.dao.ActivityRepository;
import com.ibero.demo.dao.EventDao;
import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.Event;
import com.ibero.demo.util.EventHandler;
import com.ibero.demo.util.FinishActivityDTO;
import com.ibero.demo.util.StartActivityDTO;

@RestController
@RequestMapping("/api/events")
public class ActivityController {


	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EventDao eventRepository;
    
    @Autowired
    private EventHandler eventHandler;
    
    @Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR", "ROLE_EMPLOYEE", "ROLE_SUPPORT" })
    @PostMapping("/start-activity")
    public ResponseEntity<?> startActivity(@RequestBody StartActivityDTO request) {
        // Obtén el evento desde el repositorio usando el ID del evento
    	Event event = eventRepository.findById(Integer.parseInt(request.getActivityId()))
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    	// Verifica si hay alguna actividad en curso para este usuario y evento
        Optional<ActivityEntity> ongoingActivity = activityRepository.findOngoingActivityByEventAndUser(request.getUsername());
        if (ongoingActivity.isPresent()) {
            // Si hay una actividad en curso, devuelve una respuesta con un mensaje de error
            return ResponseEntity.ok(Map.of("success", false, "message", "Ya tienes una actividad en curso. Debes finalizarla antes de iniciar una nueva."));
        }
        // Si no hay una actividad en curso, crea una nueva
        ActivityEntity activity = new ActivityEntity();
        activity.setEvent(event);
        activity.setUsername(request.getUsername());
        activity.setStartTime(request.getStartTime()); 
        // Guarda la nueva actividad en la base de datos
        ActivityEntity savedActivity = activityRepository.save(activity);
        // Envía un mensaje a través del WebSocket para notificar sobre la nueva actividad
        String username = request.getUsername();
        String eventName = event.getNameEvent();
        String startTime = request.getStartTime();
        Integer id = savedActivity.getId(); 
        logger.info("ID: " + id);
        eventHandler.sendMessageToClients(username,eventName,startTime,id.toString());
        // Devuelve una respuesta JSON exitosa con el ID de la actividad
        return ResponseEntity.ok(Map.of("success", true, "activityId", savedActivity.getId()));
    }
    
    @Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR", "ROLE_EMPLOYEE", "ROLE_SUPPORT" })
    @PostMapping("/finish-activity")
    public ResponseEntity<?> finishActivity(@RequestBody FinishActivityDTO request) {
        // Buscar la actividad en la base de datos usando el ID de la actividad
        ActivityEntity activity = activityRepository.findById(Integer.parseInt(request.getActivityId()))
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        // Establece los datos de finalización
        activity.setEndTime(request.getEndTime());
        activity.setDetailsevent(request.getDetailsevent());
        // Calcular el tiempo total
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(activity.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), formatter);
        long totalTime = ChronoUnit.MINUTES.between(startTime, endTime);
        activity.setTotalTime(totalTime); // Guarda el tiempo total en minutos
        // Actualizar la actividad en la base de datos
        activityRepository.save(activity);
        // Enviar mensaje de cierre de la actividad al WebSocket
        String username = activity.getUsername();  // Usuario relacionado con la actividad
        String activityId = request.getActivityId();  // ID de la actividad
        boolean isClosed = true;  // Indica que el evento está cerrado
        // Enviar el mensaje al WebSocket
        eventHandler.sendMessageClosed(username,activityId, isClosed);
        // Devolver una respuesta JSON exitosa
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }
}
