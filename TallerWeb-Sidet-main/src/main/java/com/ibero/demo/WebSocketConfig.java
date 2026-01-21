package com.ibero.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");  // Donde se envían los mensajes
		registry.setApplicationDestinationPrefixes("/app");  // Donde se reciben los mensajes desde el cliente
		WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/eventprogress").withSockJS();// Ruta del WebSocket
		WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
	}
	
}
