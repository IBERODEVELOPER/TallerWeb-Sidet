package com.ibero.demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Event;
import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.AttendWork;
import com.ibero.demo.service.IPeopleService;
import com.ibero.demo.service.ActivityService;
import com.ibero.demo.service.AttendWorkService;
import com.ibero.demo.service.EventService;
import com.ibero.demo.util.PageRender;
import com.ibero.demo.view.xlsx.ReporteAsistenciaXlsxView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("reports")
@SessionAttributes("tardinessrecord")
public class ControllerReports {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AttendWorkService tardinesservice;
	
	@Autowired
	private ActivityService activityservice;
	
	@Autowired
	private EventService eventservice;

	@Autowired
	private IPeopleService peopleService;

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping(value = "/general_report")
	public String showStadistics(Model model) {
		// Lista para almacenar los nombres de eventos administrativos
	    List<Event> gestionTickets = new ArrayList<>();
	    // Obtener todos los eventos
		List<Event> events = eventservice.findAllEvents();
		// Recorrer la lista de eventos y filtrar por tipo
	    for (Event e : events) {
	        if ("Gestión de Ticket".equals(e.getTipoEvento())) {
	        	// Agregar el evento a la lista filtrada
	        	gestionTickets.add(e);
	        }
	    }
		model.addAttribute("titlepage", "©Registrex");
		model.addAttribute("gestionTickets", gestionTickets);
		return "/pages/general_report";
	}
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping("/download-report")
    public ResponseEntity<Object> downloadReport(
    		@RequestParam String startDate, 
    		@RequestParam String endDate) throws ParseException, IOException {
        // Obtener las actividades dentro del rango de fechas
        List<ActivityEntity> activities = activityservice.findActivitiesBetweenDates(startDate, endDate);
        if(activities.isEmpty()) {
        	// Si la lista está vacía, retornamos un mensaje de advertencia
            return ResponseEntity.badRequest().body("No se encontraron actividades en el rango de fechas.");
        }
        // Crear el archivo Excel
        ByteArrayInputStream in = createExcelReport(activities);
        // Preparar la respuesta para descargar el archivo
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_eventos.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_SUPPORT", "ROLE_EMPLOYEE"})
	@GetMapping("/download-reportbyevent")
    public ResponseEntity<Object> downloadReportByEvent(
    		@RequestParam String startDate, 
    		@RequestParam String endDate,
            @RequestParam("eventId") Integer eventId) throws ParseException, IOException {
        try {
        	// Consultar las actividades basadas en las fechas y el ID del evento
            List<ActivityEntity> activities = activityservice.findActivitiesBetweenDatesAndEventId(startDate, endDate, eventId);
         // Si no se encuentran actividades, devolver un mensaje de error
            if (activities.isEmpty()) {
            	// Si la lista está vacía, retornamos un mensaje de advertencia
                return ResponseEntity.badRequest().body("No se encontraron actividades para las fechas y evento seleccionados.");
            }
            // Crear el archivo Excel
            ByteArrayInputStream in = createExcelReport(activities);
         // Preparar la respuesta para descargar el archivo
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_eventos.xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(in.readAllBytes());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("error: " + e);
		}
    }
    
    private ByteArrayInputStream createExcelReport(List<ActivityEntity> activities) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Eventos");
         // Crear estilo para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);  // Negrita
            headerFont.setColor(IndexedColors.WHITE.getIndex()); // Texto blanco
            headerStyle.setFont(headerFont);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex()); // Fondo azul
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Crear fila de encabezados
            Row header = sheet.createRow(0);
            String[] headerTitles = {"ID", "USUARIO", "ESTADO", "CÓDIGO", "PROCESO", "SUB-RPOCESO", "INICIO", "FIN", "DURACIÓN", "OBSERVACIONES"};
            for (int i = 0; i < headerTitles.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headerTitles[i]);
                cell.setCellStyle(headerStyle); // Aplicar estilo
            }
            // Crear el estilo para las celdas de datos
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(false); 
            // Crear filas de datos
            int rowIndex = 1;
            for (ActivityEntity activity : activities) {
                Row row = sheet.createRow(rowIndex++);
                // Crear las celdas y aplicar el estilo de envoltura de texto
                Cell cellId = row.createCell(0);
                cellId.setCellValue(activity.getId());
                cellId.setCellStyle(cellStyle);
                
                Cell cellUser = row.createCell(1);
                cellUser.setCellValue(activity.getUsername());
                cellUser.setCellStyle(cellStyle);
                
                Cell status = row.createCell(2);
                status.setCellValue(activity.getStatus());
                status.setCellStyle(cellStyle);
                
                Cell codeAct = row.createCell(3);
                codeAct.setCellValue(activity.getCodeActivity());
                codeAct.setCellStyle(cellStyle);
                
                Cell cellTipoEvento = row.createCell(4);
                cellTipoEvento.setCellValue(activity.getEvent().getTipoEvento());
                cellTipoEvento.setCellStyle(cellStyle);
                
                Cell cellNameEvent = row.createCell(5);
                cellNameEvent.setCellValue(activity.getEvent().getNameEvent());
                cellNameEvent.setCellStyle(cellStyle);

                Cell cellStartTime = row.createCell(6);
                cellStartTime.setCellValue(activity.getStartTime().toString());
                cellStartTime.setCellStyle(cellStyle);

                Cell cellEndTime = row.createCell(7);
                cellEndTime.setCellValue(activity.getEndTime().toString());
                cellEndTime.setCellStyle(cellStyle);
                
                Cell cellTotalTime = row.createCell(8);
                cellTotalTime.setCellValue(activity.getTotalTime());
                cellTotalTime.setCellStyle(cellStyle);

                Cell cellDetails = row.createCell(9);
                cellDetails.setCellValue(activity.getDetailsevent());
                cellDetails.setCellStyle(cellStyle);
            }
            // Ajustar el tamaño de las columnas automáticamente después de agregar los datos
            for (int i = 0; i < headerTitles.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Escribir el archivo a un ByteArrayOutputStream
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            }
        }
    }
	
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/asistence_general")
	public String showReportsAsi(@RequestParam(name = "page", defaultValue = "0") int page
			, Model model, HttpServletRequest request) {
		Pageable pageRequest = PageRequest.of(page, 8);
		//Para manejar la url
		String currentUri = request.getRequestURI();
	    boolean isReportPage = "/reports/asistence_general".equals(currentUri);
	    // Obtiene la paginación de los registros
		Page<AttendWork> tardins = tardinesservice.findAllTardingreport(pageRequest);
		// Calcula el total de registros
	    long totalRecords = tardins.getTotalElements();
	    // Prepara la paginación para la vista
		PageRender<AttendWork> pageRender = new PageRender<AttendWork>("/reports/asistence_general", tardins);
		// Agrega atributos al modelo para ser utilizados en la vista
		model.addAttribute("titlepage", "Reporte de Asistencia con tardanzas");
		model.addAttribute("tardins", tardins);
		model.addAttribute("page", pageRender);
		model.addAttribute("totalRecords", totalRecords);
		model.addAttribute("isReportPage", isReportPage);
		return "/pages/go_works";
	}

	// buscar empleado por su dni
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/seachby")
	public String findEmployeeByDocumento(
			@RequestParam(name = "documentNumber", required = false) String documentNumber,
			@RequestParam(name = "month", required = false) Integer month,
			@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		if (documentNumber != null && !documentNumber.isEmpty()) {
			Employee employee = peopleService.findByIdentityPeople(documentNumber);
			if (employee != null) {
				Pageable pageRequest = PageRequest.of(page, 8);
				Page<AttendWork> attendWorks;
				PageRender<AttendWork> pageRender;
				//Para mostrar el total de los registros
				long totalRecords=0;
				if (month != null) {
	                attendWorks = tardinesservice.findTardinessByEmployeeAndMonth(employee, month, pageRequest);
	             // Calcula el total de registros
	        	 totalRecords = attendWorks.getTotalElements();
	        	 // Prepara la paginación para la vista
	                pageRender = new PageRender<>("/reports/seachby?documentNumber=" + documentNumber + "&month=" + month, attendWorks);
	            } else {
	                attendWorks = tardinesservice.findTardinessByEmployee(employee, pageRequest);
	             // Calcula el total de registros
	                totalRecords = attendWorks.getTotalElements();
	             // Prepara la paginación para la vista
	                pageRender = new PageRender<>("/reports/seachby?documentNumber=" + documentNumber, attendWorks);
	            }
				// Agrega atributos al modelo para ser utilizados en la vista
	            model.addAttribute("tardins", attendWorks);
	            model.addAttribute("page", pageRender);
	            model.addAttribute("documentNumber", documentNumber);
	            model.addAttribute("selectedMonth", month);
	            model.addAttribute("totalRecords", totalRecords);
			} else {
				logger.info("No se encontró un empleado con el documento: " + documentNumber);
				model.addAttribute("tardins", new ArrayList<>());
				// Enviamos una lista vacía si no hay registros
			}
		} else {
			logger.info("Número de documento no proporcionado");
			model.addAttribute("tardins", new ArrayList<>());
			// Enviamos una lista vacía si no se proporcionó el documento
		}
		return "/pages/go_works"; // Redirige a la página de asistencia general
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/seachbydpf", produces = "application/pdf")
	public ModelAndView exportPDF(@RequestParam(name = "documentNumber", required = false) String documentNumber,
								  @RequestParam(name = "month", required = false) String monthStr,
	                              @RequestParam(name = "page", defaultValue = "0") int page,
	                              Model model) {
	    ModelAndView modelAndView = new ModelAndView("/pages/go_works");
	    Integer month = null;
	    if (monthStr != null && !monthStr.isEmpty() && !"null".equals(monthStr)) {
	        try {
	            month = Integer.parseInt(monthStr);
	        } catch (NumberFormatException e) {
	            // Manejar el error de formato si es necesario
	        }
	    }
	    if (documentNumber != null && !documentNumber.isEmpty()) {
	        Employee employee = peopleService.findByIdentityPeople(documentNumber);
	        if (employee != null) {
	        	List<AttendWork> allTardinessRecords;
	        	if (month != null && month > 0) {
	        		//obtenemos todos los registros de tardanza con el mes
		            allTardinessRecords = tardinesservice.findTardinessByEmployeeAndMonth(employee, month);
	        	}else {
	        		//obtenemos todos los registros de tardanza sin el mes
	        		allTardinessRecords = tardinesservice.findByEmployee(employee);
	        	}
	            // Pasamos todos los registros al modelo
	            model.addAttribute("tardins", allTardinessRecords); 
	        }
	    }
	    return modelAndView;
	}
	//Reporte General XLSX
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/asistence_generalxlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ModelAndView exportExcelGen(Model model) {
		ModelAndView modelAndView = new ModelAndView(new ReporteAsistenciaXlsxView());
		model.addAttribute("tardins", tardinesservice.findAllReports());
		return modelAndView;
	}
	//Reporte EXCEL por número de documento y/o mes
	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN","ROLE_EMPLOYEE"})
	@GetMapping(value = "/seachbyexcel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ModelAndView exportExcel(@RequestParam(name = "documentNumber", required = false) String documentNumber,
									@RequestParam(name = "month", required = false) String monthStr,
	                                @RequestParam(name = "page", defaultValue = "0") int page,
	                                Model model) {

	    ModelAndView modelAndView = new ModelAndView(new ReporteAsistenciaXlsxView());
	    Integer month = null;
	    if (monthStr != null && !monthStr.isEmpty() && !"null".equals(monthStr)) {
	        try {
	            month = Integer.parseInt(monthStr);
	        } catch (NumberFormatException e) {
	            // Manejar el error de formato si es necesario
	        }
	    }
	    if (documentNumber != null && !documentNumber.isEmpty()) {
	        Employee employee = peopleService.findByIdentityPeople(documentNumber);
	        if (employee != null) {
	        	List<AttendWork> allTardinessRecords;
	        	if (month != null && month > 0) {
	        		//obtenemos todos los registros de tardanza con el mes
		            allTardinessRecords = tardinesservice.findTardinessByEmployeeAndMonth(employee, month);
	        	}else {
	        		//obtenemos todos los registros de tardanza sin el mes
	        		allTardinessRecords = tardinesservice.findByEmployee(employee);
	        	}
	            // Pasar los registros al modelo
	            model.addAttribute("tardins", allTardinessRecords);
	        }
	    }

	    return modelAndView;
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN"})
	@GetMapping(value = "/changetarding/{id}")
	public String showReportsAsi(@PathVariable(value = "id") Integer id, Model model, RedirectAttributes flash) {
		AttendWork tardins = null;
		if (id > 0) {
			tardins = tardinesservice.findbyIdtardinesRecord(id);
		}
		if (tardins == null) {
			flash.addFlashAttribute("error", "El ID del reporte no existe en la BBDD");
			return "redirect:/reports/asistence_general";
		}
		model.addAttribute("titlepage", "Actualizar tardanza");
		model.addAttribute("titleform", "Justificar tardanza");
		model.addAttribute("tardins", tardins);
		return "/pages/formtardiness";
	}

	@Secured({ "ROLE_MANAGER", "ROLE_ADMIN"})
	@PostMapping(value = "/changetarding")
	public String processForm(@Valid AttendWork tarding, BindingResult result, Model model,
			RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titlepage", "Formulario de Registro de Clientes");
			model.addAttribute("titleform", "Registro de Datos");
			return "redirect:/reports/changetarding/" + tarding.getId();
		}
		if (tarding.getId() == null) {
			tardinesservice.saveTarding(tarding);
			flash.addFlashAttribute("success", "Datos registrados correctamente");

		} else {
			// Actualizar solo los campos específicos si es necesario
			tardinesservice.saveTarding(tarding);
			flash.addFlashAttribute("success", "Datos Actualizados correctamente");
		}
		status.setComplete();
		return "redirect:/reports/asistence_general";
	}
	
	@Secured({"ROLE_ADMIN"})
	@GetMapping(value = "/deleteBy/{id}")
	public String eliminarCliente(@PathVariable(value = "id") Integer id, RedirectAttributes flash) {
		AttendWork tardins = null;
		if (id > 0) {
			tardins = tardinesservice.findbyIdtardinesRecord(id);
			tardinesservice.deleteAsistenciaById(tardins.getId());
			flash.addFlashAttribute("success", "Registro de Asistencia eliminado");
		}
		return "redirect:/reports/asistence_general";
	}

}
