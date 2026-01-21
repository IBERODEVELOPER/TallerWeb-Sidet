package com.ibero.demo.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.Case;

public interface CaseService {
	//Crear Caso
	public Case saveCase(Case cass);
	//Listar Caso
	public Page<Case> findAllCass(Pageable page);
	//Buscar caso por id
	public Case findCaseById(String id);
}
