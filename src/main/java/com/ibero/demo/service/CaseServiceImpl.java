package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.CaseDao;
import com.ibero.demo.entity.Case;

@Service
public class CaseServiceImpl implements CaseService{
	
	@Autowired
	private CaseDao casosdao;
	
	@Override
	@Transactional
	public Case saveCase(Case cass) {
		return casosdao.save(cass);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Case> findAllCass(Pageable page) {
		return (Page<Case>)casosdao.findAll(page);
	}

	@Override
	@Transactional(readOnly = true)
	public Case findCaseById(String id) {
		return casosdao.findById(id).orElse(null);
	}

}
