package com.ibero.demo.service;

import java.util.List;

import com.ibero.demo.entity.AttachCase;
import com.ibero.demo.entity.Case;

public interface IAttachCaseService {
	// Método para buscar adjuntos por objeto Case
    List<AttachCase> findAttachmentsByCase(Case cas);
 // Método para buscar todos los archivos adjuntos por el ID del caso
    List<AttachCase> findByCasId(String caseId);
}
