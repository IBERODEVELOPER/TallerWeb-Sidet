package com.ibero.demo.service;

import java.util.List;

import com.ibero.demo.entity.CommentCase;
import com.ibero.demo.entity.Case;

public interface ICommentCaseService {
	// Método para buscar comentario
    List<CommentCase> findCommentByCase(Case cas);
}
