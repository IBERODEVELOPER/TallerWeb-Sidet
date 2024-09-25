package com.ibero.demo.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.ibero.demo.entity.AttachCase;
import com.ibero.demo.entity.Case;

public interface AttachCaseDao extends CrudRepository<AttachCase,Integer>{
	
	// Método para encontrar los AttachCase por Case
    List<AttachCase> findByCas(Case cas);
    // Método para encontrar todos los archivos adjuntos por el ID del caso
    List<AttachCase> findByCasId(String caseId);

}
