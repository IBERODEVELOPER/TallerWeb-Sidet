package com.ibero.demo.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ibero.demo.entity.Case;
import com.ibero.demo.entity.CommentCase;

public interface CommentCaseDao extends CrudRepository<CommentCase,Integer>{
	// Método para encontrar los comentarios
    List<CommentCase> findByCas(Case cas);
}
