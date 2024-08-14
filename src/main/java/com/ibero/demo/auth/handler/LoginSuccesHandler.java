package com.ibero.demo.auth.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.UserEntity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccesHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// Obtener la sesión desde la solicitud
	    HttpSession session = request.getSession();
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		FlashMap flashmap = new FlashMap();
		CustomUserDetails usercustom = (CustomUserDetails) authentication.getPrincipal();
		UserEntity userentity = usercustom.getUserEntity();
		if (authentication != null && authentication.isAuthenticated()) {
			// Verificar si el mensaje ya ha sido mostrado en esta sesión
			Boolean isMessageShown = (Boolean) session.getAttribute("messageShown");
			if (isMessageShown == null || !isMessageShown) {
				// Obtener la hora actual
				LocalDateTime now = LocalDateTime.now();
				// Formatear la hora en el formato deseado
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				String formattedNow = now.format(formatter);
				flashmap.put("success", "Hola " + authentication.getName()+ " ha iniciado sesión con éxito,".concat("hora de ingreso: " + formattedNow));
				// Marcar el mensaje como mostrado en esta sesión
				session.setAttribute("messageShown", true);
			}
			
			if(userentity.isTemporaryPassword()) {
				response.sendRedirect("/user/changekey");
			}else {
				response.sendRedirect("/");
			}
			
			
		}
		flashMapManager.saveOutputFlashMap(flashmap, request, response);
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
