package com.ibero.demo.dao;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPeopleDao extends CrudRepository<Employee, Integer>{
	
	public Employee findByUserEntity(UserEntity userEntity);
}
