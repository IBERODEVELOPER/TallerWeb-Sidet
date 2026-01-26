package com.ibero.demo.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class EventHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final SimpMessagingTemplate messagingTemplate;

	public EventHandler(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	// Método para enviar un mensaje estructurado en formato JSON
	public void sendMessageToClients(String username, String eventName, String startTime, String id) {
		logger.debug("username : " + username + " Evento : " + eventName + " Fecha y Hora de Inicio: " +startTime+"ID "+ id);
		// Crear un objeto que represente el mensaje
		Map<String, String> messageData = new HashMap<>();
		messageData.put("username", username);
		messageData.put("eventName", eventName);
		messageData.put("startTime", startTime);
		messageData.put("id", id);
		try {
			// Convertir el objeto a JSON
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonMessage = objectMapper.writeValueAsString(messageData);
			// Enviar el mensaje a todos los clientes suscritos a "/topic/events"
			messagingTemplate.convertAndSend("/topic/events", jsonMessage);
		} catch (Exception e) {
			logger.error("Error convirtiendo el mensaje a JSON", e);
		}
	}
	
	// Método para enviar un mensaje estructurado en formato JSON indicando finalizando el evento.
		public void sendMessageClosed(String username, String id, boolean isClosed) {
			logger.debug("username : " + username + " ID: "+ id+ " is Closed: " + isClosed);
			// Crear un objeto que represente el mensaje
			Map<String, String> messageData = new HashMap<>();
			messageData.put("username", username);
			messageData.put("id", id);
			messageData.put("isClosed", Boolean.toString(isClosed));
			try {
				// Convertir el objeto a JSON
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonMessage = objectMapper.writeValueAsString(messageData);
				// Enviar el mensaje a todos los clientes suscritos a "/topic/events"
				messagingTemplate.convertAndSend("/topic/events", jsonMessage);
			} catch (Exception e) {
				logger.error("Error convirtiendo el mensaje a JSON", e);
			}
		}
}
