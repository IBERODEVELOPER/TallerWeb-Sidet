package com.ibero.demo.view.xlsx;

import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.ibero.demo.entity.TardinessRecord;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("/pages/reporte.xlsx")
public class ReporteAsistenciaXlsxView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"reporte_asistencia.xlsx\"");

		@SuppressWarnings("unchecked")
		List<TardinessRecord> tardinessRecords = (List<TardinessRecord>) model.get("tardins");

		// Crear la hoja
		Sheet sheet = workbook.createSheet("Reporte de Asistencia");
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);

		// Agregar el logo
		addLogo(workbook, sheet);

		// Crear el título
		createTitleRow(workbook, sheet);

		// Agregar los detalles del reporte
		createReportDetails(workbook, sheet, tardinessRecords);

		// Crear el encabezado de la tabla
		createTableHeader(workbook, sheet);

		// Agregar los datos a la tabla
		addTableRows(workbook, sheet, tardinessRecords);
	}

	private void addLogo(Workbook workbook, Sheet sheet) throws Exception {
		Drawing<?> drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
		anchor.setCol1(6); // Columna F
		anchor.setRow1(0); // Fila 1
		anchor.setCol2(7); // Columna G
		anchor.setRow2(2); // Fila 2

		// Ruta del logo en el classpath
		URL logoUrl = getClass().getClassLoader().getResource("static/images/iconlogo.png");
		if (logoUrl == null) {
			throw new IOException("No se encontró el logo en la ruta especificada.");
		}

		byte[] logoBytes = logoUrl.openStream().readAllBytes();
		int pictureIndex = workbook.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
		drawing.createPicture(anchor, pictureIndex);
	}

	private void createTitleRow(Workbook workbook, Sheet sheet) {
		Row titleRow = sheet.createRow(3);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("Reporte de Asistencia");
		titleCell.setCellStyle(createTitleCellStyle(workbook));

		// Unir celdas para que el título ocupe varias columnas
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
	}

	private CellStyle createTitleCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Helvetica");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	private void createReportDetails(Workbook workbook, Sheet sheet, List<TardinessRecord> tardinessRecords) {
		LocalDate startDate = tardinessRecords.stream().min(Comparator.comparing(TardinessRecord::getDate)).get()
				.getDate();
		LocalDate endDate = tardinessRecords.stream().max(Comparator.comparing(TardinessRecord::getDate)).get()
				.getDate();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedStartDate = startDate.format(formatter);
		String formattedEndDate = endDate.format(formatter);

		long attendanceDays = tardinessRecords.stream().map(TardinessRecord::getDate).distinct().count();
		long totalTardinessMinutes = tardinessRecords.stream().mapToLong(TardinessRecord::getTardinessMinutes).sum();

		// Fila 5: Reporte de Asistencia del Empleado
		Row row1 = sheet.createRow(5);
		row1.createCell(0).setCellValue(
				"Reporte de Asistencia del Empleado: " + tardinessRecords.get(0).getEmployee().getFullName());

		// Fila 6: Desde - Hasta
		Row row2 = sheet.createRow(6);
		row2.createCell(0).setCellValue("Desde: " + formattedStartDate);
		row2.createCell(1).setCellValue("Hasta: " + formattedEndDate);

		// Fila 7: Días de asistencia
		Row row4 = sheet.createRow(7);
		row4.createCell(0).setCellValue("Días de asistencia: " + attendanceDays);

		// Fila 9: Minutos totales de tardanza
		Row row5 = sheet.createRow(8);
		row5.createCell(0).setCellValue("Minutos totales de tardanza: " + totalTardinessMinutes + " minutos");

		// Fila 10: Tolerancia de Tardanza
		Row row6 = sheet.createRow(9);
		row6.createCell(0).setCellValue("Tolerancia de Tardanza por día 10 min, Tolerancia al mes 1 hora.");
	}

	private void createTableHeader(Workbook workbook, Sheet sheet) {
		Row headerRow = sheet.createRow(12);
		String[] columns = { "Nombre Completo", "Día", "Fecha", "H. Ingreso", "Tardanza"};

		CellStyle headerStyle = workbook.createCellStyle();
		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Helvetica");
		font.setBold(true);
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);

		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerStyle);
		}
		// Crear la celda "Justificación" y fusionar dos columnas (5 y 6)
	    Cell justifyHeaderCell = headerRow.createCell(5);
	    justifyHeaderCell.setCellValue("Justificación");
	    justifyHeaderCell.setCellStyle(headerStyle);
	    sheet.addMergedRegion(new CellRangeAddress(12, 12, 5, 6));
	}

	private void addTableRows(Workbook workbook, Sheet sheet, List<TardinessRecord> tardinessRecords) {
		int rowCount = 13;
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);

		for (TardinessRecord record : tardinessRecords) {
			Row row = sheet.createRow(rowCount);
			row.createCell(0).setCellValue(record.getEmployee().getFullName());
			row.createCell(1).setCellValue(record.getDay().toString());
			row.createCell(2).setCellValue(record.getDate().toString());
			row.createCell(3).setCellValue(record.getActualEntryTime().toString());
			row.createCell(4).setCellValue(String.valueOf(record.getTardinessMinutes()));
			// Celda de Justificación que ocupa 2 columnas
			Cell justifyCell = row.createCell(5);
			justifyCell.setCellValue(record.getJustifytard());
			justifyCell.setCellStyle(style);

			// Fusionar 2 columnas (5 y 6) en la misma fila (rowCount)
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 5, 6));

			rowCount++; // Aumentar el contador de filas en 1 para la siguiente fila
		}
		// Autoajustar el ancho de las columnas después de llenar los datos
	    for (int i = 0; i <= 6; i++) {
	        sheet.autoSizeColumn(i);
	    }
	}
}
