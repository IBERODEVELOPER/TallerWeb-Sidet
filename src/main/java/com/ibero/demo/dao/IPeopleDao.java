package com.ibero.demo.dao;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPeopleDao extends CrudRepository<Employee, Integer>{
	
	public Employee findByUserEntity(UserEntity userEntity);
	
	Optional<Employee> findByEmailPeople(String emailPeople);
	
	@Modifying
    @Query("UPDATE Employee e SET e.foto = :foto WHERE e.id = :id")
    public void updateFoto(@Param("id") Integer id, @Param("foto") String foto);
	
    
}
