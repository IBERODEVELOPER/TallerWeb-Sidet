package com.ibero.demo.auth.handler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.ibero.demo.service.AttendWorkService;
import com.ibero.demo.util.DaysWeek;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccesHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IPeopleService employeeService;

	@Autowired
	private AttendWorkService tardinesservice;

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
				flashmap.put("success", "Hola " + authentication.getName()
						+ " ha iniciado sesión con éxito,".concat("hora de ingreso: " + formattedNow));
				// Marcar el mensaje como mostrado en esta sesión
				session.setAttribute("messageShown", true);
			}

			try {
				// Valido el rol del usuario
				if (hasRole("ROLE_ADMIN")||hasRole("ROLE_MANAGER")) {
					logger.info("LoginSuccesHandler Estoy en ADMIN Y MANAGER");
					if (session.getAttribute("asistenciaRegistrada") == null) {
						Employee employee = employeeService.getEmployeeWithFullSchedule(userentity);
						if (employee != null) {
							// Llamar al método que realiza las inserciones
							registrarAsistencia(employee);
							// Marca la inserción como realizada
							session.setAttribute("asistenciaRegistrada", true);
						}
					}
					// Si es admin, no se valida el horario.
					if (userentity.isTemporaryPassword()) {
						response.sendRedirect(request.getContextPath() + "/user/changekey");
					} else {
						response.sendRedirect(request.getContextPath() + "/index");
					}
				} else if (hasRole("ROLE_EMPLOYEE") || hasRole("ROLE_SUPPORT")) {
					logger.info("LoginSuccesHandler Estoy en hasRole(\"ROLE_EMPLOYEE\") Y hasRole(\"ROLE_SUPPORT\")");
					// Si es empleado o soporte, se valida el horario.
					if (verificarHorario(request, response, authentication)) {
						logger.info("Entro en verificarHorario(request, response, authentication) horario");
						if (userentity.isTemporaryPassword()) {
							response.sendRedirect(request.getContextPath() + "/user/changekey");
						} else {
							response.sendRedirect(request.getContextPath() + "/index");
						}
					} else {
						logger.info("LoginSuccesHandler Entro al else de fuera de turno");
						response.sendRedirect(request.getContextPath() + "/outofturn");
					}
				}else {
					logger.info("LoginSuccesHandler Estoy en Cliente");
					if (userentity.isTemporaryPassword()) {
						response.sendRedirect(request.getContextPath() + "/user/changekey");
					} else {
						response.sendRedirect(request.getContextPath() + "/index");
					}
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
						int margenToleranciaMinutos = 10; // Permitir 10 minutos de tolerancia
						LocalTime horaEntrada = LocalTime.parse(daySchedule.getEntryTime(), formatter);
						LocalTime horaEntradaConTolerancia = horaEntrada.plusMinutes(margenToleranciaMinutos);
						LocalTime horaSalida = LocalTime.parse(daySchedule.getLeavWork(), formatter);
						// Verifica si el turno cruza la medianoche
						boolean cruzaMedianoche = horaSalida.isBefore(horaEntrada);
						logger.info("LoginSuccesHandler verificarHorario() Valor de horaEntrada: " + horaEntrada);
						logger.info("LoginSuccesHandler verificarHorario() Valor de horaEntradaConTolerancia: " + horaEntradaConTolerancia);
						logger.info("LoginSuccesHandler verificarHorario() Valor de horaSalida: " + horaSalida);
						logger.info("LoginSuccesHandler verificarHorario() Valor de horaActualTime: " + horaActualTime);
						logger.info("LoginSuccesHandler verificarHorario() Valor de cruzaMedianoche: " + cruzaMedianoche);
						if (cruzaMedianoche) {
						    if ((horaActualTime.isAfter(horaEntrada.minusMinutes(margenToleranciaMinutos)) 
						    		|| horaActualTime.equals(horaEntrada))
						            || horaActualTime.isBefore(horaSalida)) {
						        // Está dentro del turno
						        logger.info("Empleado está dentro del turno");
						        LocalDate today = LocalDate.now();
						        if (horaActualTime.isAfter(horaEntradaConTolerancia)) {
									if (!tardinesservice.existsByEmployeeAndDate(employee, today)) {
										// Registrar la tardanza
										tardinesservice.savetarding(employee, diaEnum, horaEntrada, horaActualTime);
									} else {
										logger.info("Empleado inicia sesión por segunda vez");
									}
								} else {
									if(!tardinesservice.existsByEmployeeAndDate(employee, today)) {
										// registrar asistencia
										tardinesservice.savetarding(employee, diaEnum, horaEntrada, horaActualTime);
										logger.info("El empleado ingresó a tiempo.");
									}else {
										logger.info("El empleado ingresó a tiempo. Existe su asistencia registrado");
									}
								}
						        
						        
						    } 
						    return true;// Está dentro del horario permitido
						}else {
							if (horaActualTime.isAfter(horaEntrada.minusMinutes(margenToleranciaMinutos))
									&& horaActualTime.isBefore(horaSalida)) {
								LocalDate today = LocalDate.now();
								if (horaActualTime.isAfter(horaEntradaConTolerancia)) {
									if (!tardinesservice.existsByEmployeeAndDate(employee, today)) {
										// Registrar la tardanza
										tardinesservice.savetarding(employee, diaEnum, horaEntrada, horaActualTime);
									} else {
										logger.info("Empleado inicia sesión por segunda vez");
									}
								}
								else {
									// registrar asistencia
									if(!tardinesservice.existsByEmployeeAndDate(employee, today)) {
										// registrar asistencia
										tardinesservice.savetarding(employee, diaEnum, horaEntrada, horaActualTime);
										logger.info("El empleado ingresó a tiempo.");
									}else {
										logger.info("El empleado ingresó a tiempo. Existe su asistencia registrado");
									}
								}

								return true; // Está dentro del horario permitido
							}
						}
						
					}
				}
			}
		}
		return false; // Está fuera del horario permitido
	}

	// registrar asistencia.
	public void registrarAsistencia(Employee employee) {
		Calendar calendar = Calendar.getInstance();
		int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
		int minutosActuales = calendar.get(Calendar.MINUTE);
		int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
		DaysWeek diaEnum = DaysWeek.fromCalendarDay(diaSemana);
		LocalTime horaActualTime = LocalTime.of(horaActual, minutosActuales);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		for (Schedule schedule : employee.getSchedule()) {
			for (DaySchedule daySchedule : schedule.getDaySchedules()) {
				// Verificar si el día del horario coincide con el día actual
				if (daySchedule.getDayWeek().equals(diaEnum)) {
					LocalDate today = LocalDate.now();
					LocalTime horaEntrada = LocalTime.parse(daySchedule.getEntryTime(), formatter);
					if (!tardinesservice.existsByEmployeeAndDate(employee, today)) {
						// Registrar la tardanza
						tardinesservice.savetarding(employee, diaEnum, horaEntrada, horaActualTime);
						return;
					} else {
						logger.info("Empleado inicia sesión por segunda vez");
					}
				}
			}
		}
	}

	// Validar Role
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return false;
		}
		Authentication aut = context.getAuthentication();
		if (aut == null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = aut.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority(role));
	}

}
