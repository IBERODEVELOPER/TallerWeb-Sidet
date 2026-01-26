package com.ibero.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ibero.demo.entity.Case;

public interface CaseDao extends CrudRepository<Case,String>, PagingAndSortingRepository<Case,String>{

}
