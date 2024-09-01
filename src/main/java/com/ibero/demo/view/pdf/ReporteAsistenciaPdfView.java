package com.ibero.demo.view.pdf;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.Comparator;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.ibero.demo.entity.TardinessRecord;
import com.lowagie.text.BadElementException;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("/pages/go_works")
public class ReporteAsistenciaPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		List<TardinessRecord> tardinessRecords = (List<TardinessRecord>) model.get("tardins");
		// Agregar el logo al documento
        addLogo(document);
		// Crear una fuente con color
		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(51,96,216));
		// Mostrar título del documento
		Paragraph title = new Paragraph("Reporte de Asistencia", titleFont);
		title.setAlignment(Element.ALIGN_CENTER); // Centrando el título
		title.setSpacingBefore(10f); // Espacio antes del título
		title.setSpacingAfter(10f);  // Espacio después del título
		document.add(title);
		// Obtener la fecha mínima (inicio) y máxima (fin)
		LocalDate startDate = tardinessRecords.stream().min(Comparator.comparing(TardinessRecord::getDate)).get()
				.getDate();

		LocalDate endDate = tardinessRecords.stream().max(Comparator.comparing(TardinessRecord::getDate)).get()
				.getDate();
		// Formato de la fecha
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedStartDate = startDate.format(formatter);
		String formattedEndDate = endDate.format(formatter);
		// Mostrar detalle del reporte
		long attendanceDays = tardinessRecords.stream().map(TardinessRecord::getDate).distinct().count();
		// Calcular la suma total de minutos de tardanza usando Long
		long totalTardinessMinutes = tardinessRecords.stream().mapToLong(TardinessRecord::getTardinessMinutes).sum();
		// Agregar resumen al PDF
		Paragraph summary = new Paragraph();
		summary.add("Reporte de Asistencia del Empleado: " + tardinessRecords.get(0).getEmployee().getFullName() + "\n");
		summary.add("Desde: " + formattedStartDate + " Hasta: " + formattedEndDate+ "\n");
		summary.add("Días de asistencia: " + attendanceDays + "\n");
		summary.add("Minutos totales de tardanza: " + totalTardinessMinutes + " minutos" + "\n");
		summary.add("Tolerancia de Tardanza por día 10 min Tolerancia al mes 1 hora.");
		document.add(summary);
		
		PdfPTable table = new PdfPTable(6); // ajusta el número de columnas según la tabla
		table.setWidthPercentage(100);
		table.setWidths(new float [] {5f,2,2,2,2,5f});
		table.setSpacingBefore(10f);
		table.setSpacingAfter(10f);
		// Agregar encabezados
		addTableHeader(table);
		// Agregar datos
		addRows(table, tardinessRecords);
		document.add(table);
	}

	private void addTableHeader(PdfPTable table) {
		Stream.of("Nombre Completo", "Día", "Fecha", "H. Ingreso", "Tardanza", "Justificación").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setPhrase(new Phrase(columnTitle));
			header.setBackgroundColor(new Color(51,96,216));
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		});
	}

	private void addRows(PdfPTable table, List<TardinessRecord> tardinessRecords) {
		for (TardinessRecord record : tardinessRecords) {
			table.addCell(record.getEmployee().getFullName());
			table.addCell(record.getDay().toString());
			table.addCell(record.getDate().toString());
			table.addCell(record.getActualEntryTime().toString());
			table.addCell(String.valueOf(record.getTardinessMinutes()));
			table.addCell(record.getJustifytard());
		}
	}
	
	 private void addLogo(Document document) throws IOException, BadElementException {
	        // Ruta del logo en el classpath
	        URL logoUrl = getClass().getClassLoader().getResource("static/images/iconlogo.png");
	        if (logoUrl == null) {
	            throw new IOException("No se encontró el logo en la ruta especificada.");
	        }
	        Image logo = Image.getInstance(logoUrl);
	        // Ajustar el tamaño del logo
	        logo.scaleToFit(45f, 450f); // Ancho máximo de 150 píxeles y altura máxima de 75 píxeles
	        logo.setAlignment(Image.ALIGN_RIGHT);
	        document.add(logo);
	    }
}
