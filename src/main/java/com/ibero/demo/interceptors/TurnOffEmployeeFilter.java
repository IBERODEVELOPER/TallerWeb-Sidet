package com.ibero.demo.interceptors;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.util.DaysWeek;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TurnOffEmployeeFilter extends OncePerRequestFilter {

    private IPeopleService employeeService;
	
	@Autowired
    public TurnOffEmployeeFilter(IPeopleService employeeService) {
        this.employeeService = employeeService;
    }
		 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Verifica si es una solicitud para un recurso estático
		String path = request.getRequestURI();
		if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
				|| path.startsWith("/TallerWeb-Fotos/")) {
			filterChain.doFilter(request, response);
			return;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
			if (userDetails.hasRole("ROLE_EMPLOYEE") || userDetails.hasRole("ROLE_SUPPORT")) {
				try {
					if(!verificarHorario(request, response, auth)) {
						// Si no está intentando acceder a /outofturn, redirigir
	                    if (!request.getRequestURI().equals("/outofturn")) {
	                        response.sendRedirect(request.getContextPath() + "/outofturn");
	                        return;
	                    }
					}
				} catch (Exception e) {
					logger.error(e);
				}					
			}
		}
		filterChain.doFilter(request, response);
	}
	
	// Método para verificar el horario
		private boolean verificarHorario(HttpServletRequest request, HttpServletResponse response,
		        Authentication authentication) throws Exception {
			logger.info("Validando horario TurnOffEmployeeFilter");
		    Calendar calendar = Calendar.getInstance();
		    int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
		    int minutosActuales = calendar.get(Calendar.MINUTE);
		    int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
		    DaysWeek diaEnum = DaysWeek.fromCalendarDay(diaSemana);
		    LocalTime horaActualTime = LocalTime.of(horaActual, minutosActuales);
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		    // Obtener el usuario autenticado
		    CustomUserDetails usercustom = (CustomUserDetails) authentication.getPrincipal();
		    UserEntity usere = usercustom.getUserEntity();
		 // Carga completa del empleado con sus horarios
		    Employee employee = employeeService.getEmployeeWithFullSchedule(usere);
		    if (employee != null) {
		        for (Schedule schedule : employee.getSchedule()) {
		            for (DaySchedule daySchedule : schedule.getDaySchedules()) {
		            	 if (daySchedule.getDayWeek().equals(diaEnum)) {
		            		 int margenToleranciaMinutos = 10; // Permitir 10 minutos de tolerancia
			                    LocalTime horaEntrada = LocalTime.parse(daySchedule.getEntryTime(), formatter);
			                    LocalTime horaSalida = LocalTime.parse(daySchedule.getLeavWork(), formatter);
			                    logger.info("TurnOffEmployeeFilter Entrada : " + horaEntrada);
			                    logger.info("TurnOffEmployeeFilter horaActualTime : " + horaActualTime);
			                    logger.info("TurnOffEmployeeFilter Dia : " + diaEnum);
			                    // Verifica si el turno cruza la medianoche
			                    boolean cruzaMedianoche = horaSalida.isBefore(horaEntrada);
			                    if (cruzaMedianoche) {
			                        // Si cruza la medianoche, la hora actual debe estar entre la hora de entrada y medianoche
			                        // o entre medianoche y la hora de salida
			                        if ((horaActualTime.isAfter(horaEntrada.minusMinutes(margenToleranciaMinutos)) || horaActualTime.equals(horaEntrada))
			                                || horaActualTime.isBefore(horaSalida)) {
			                            return true; // Está dentro del horario permitido
			                        }
			                    }else {
				                    if (horaActualTime.isAfter(horaEntrada.minusMinutes(margenToleranciaMinutos))&& horaActualTime.isBefore(horaSalida)) {
				                    	 return true; // Está dentro del horario permitido
				                    }
			                    }
			                }
		            	 }
		        }
		    }
		    return false; // Está fuera del horario permitido
		}

}
