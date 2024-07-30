package com.ibero.demo.auth.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccesHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		
		FlashMap flashmap = new FlashMap();
		
		flashmap.put("success","Hola "+authentication.getName()+" Ha iniciado sesión con éxito");
		Logger log= LoggerFactory.getLogger(getClass());
		log.info("Mensaje"+flashmap);
		flashMapManager.saveOutputFlashMap(flashmap, request, response);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
