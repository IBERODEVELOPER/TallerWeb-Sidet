package com.ibero.demo.auth.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.util.DaysWeek;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccesHandler extends SimpleUrlAuthenticationSuccessHandler {


	@Autowired
    private IPeopleService employeeService;
		
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
			
			//verificar el horario
			try {
				if (verificarHorario(request, response, authentication)) {
	                if (userentity.isTemporaryPassword()) {
	                    response.sendRedirect(request.getContextPath() + "/user/changekey");
	                } else {
	                    response.sendRedirect(request.getContextPath() + "/");
	                }
	            } else {
	                response.sendRedirect(request.getContextPath() + "/outofturn");
	            }
	            return;
			} catch (Exception e) {
				logger.error("error : " + e.getMessage());
				response.sendRedirect(request.getContextPath() + "/error");
	            return;
			}
		}
		flashMapManager.saveOutputFlashMap(flashmap, request, response);
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	// Método para verificar el horario
	private boolean verificarHorario(HttpServletRequest request, HttpServletResponse response,
	        Authentication authentication) throws Exception {
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
	            		 logger.info("Día : " + diaEnum +" día del empleado : " + daySchedule.getDayWeek().toString());
		                    LocalTime horaEntrada = LocalTime.parse(daySchedule.getEntryTime(), formatter);
		                    LocalTime horaSalida = LocalTime.parse(daySchedule.getLeavWork(), formatter);
		                    logger.info("Hora de entrada : " + horaEntrada);
		                    logger.info("Hora de actual : " + horaActualTime);
		                    logger.info("Hora de salida : "  + horaSalida);
		                    if (horaActualTime.isAfter(horaEntrada) && horaActualTime.isBefore(horaSalida)) {
		                    	logger.info("Esta dentro del rango de trabajo");
		                        return true; // Está dentro del horario permitido
		                    }
		                }
	            	 }
	        }
	    }
	    return false; // Está fuera del horario permitido
	}

}
