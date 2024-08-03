package com.ibero.demo.dao;

import com.ibero.demo.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IPeopleDao extends CrudRepository<Employee, Integer>{
	

}
