package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.CommentCaseDao;
import com.ibero.demo.entity.Case;
import com.ibero.demo.entity.CommentCase;

@Service
public class CommentCaseServiceImpl implements ICommentCaseService{

	@Autowired
	private CommentCaseDao commentcaso;
	
	@Override
	@Transactional(readOnly = true)
	public List<CommentCase> findCommentByCase(Case cas) {
		return commentcaso.findByCas(cas);
	}

}
