package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.AttachCaseDao;
import com.ibero.demo.entity.AttachCase;
import com.ibero.demo.entity.Case;

@Service
public class AttachCaseServiceImpl implements IAttachCaseService{
	
	@Autowired
	private AttachCaseDao attachdao;
	
	@Override
	@Transactional(readOnly = true)
	public List<AttachCase> findAttachmentsByCase(Case cas) {
		return attachdao.findByCas(cas);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttachCase> findByCasId(String caseId) {
		return attachdao.findByCasId(caseId);
	}
	
	

}
