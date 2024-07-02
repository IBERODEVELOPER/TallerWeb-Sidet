package com.ibero.demo.dao;

import com.ibero.demo.entity.People;
import org.springframework.data.repository.CrudRepository;


public interface IPeopleDao extends CrudRepository<People, Integer>{

}
