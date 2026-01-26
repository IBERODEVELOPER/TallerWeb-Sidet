package com.ibero.demo.interceptors;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.UserEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TemporaryPasswordFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 // Verifica si es una solicitud para un recurso estático
        String path = request.getRequestURI();
		if(path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") || path.startsWith("/TallerWeb-Fotos/")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            UserEntity userentity = customUserDetails.getUserEntity();
            if (userentity.isTemporaryPassword() && !request.getRequestURI().equals("/user/changekey")) {
                response.sendRedirect("/user/changekey");
                return;
            }
        }
        filterChain.doFilter(request, response);
	}
}
