package com.ibero.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ibero.demo.entity.Role;

@Repository
public interface IRolUserDao extends CrudRepository<Role, Integer>{

	//@Query(value = "SELECT * FROM roles WHERE level_user = :level", nativeQuery = true)
    //Rol findByLevelUser(@Param("level") String level);
}
