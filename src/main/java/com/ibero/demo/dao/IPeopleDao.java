package com.ibero.demo.dao;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface IPeopleDao extends CrudRepository<Employee, Integer>, PagingAndSortingRepository<Employee, Integer>{
	
	public Employee findByUserEntity(UserEntity userEntity);
	
	Employee findByIdentityPeople(String identityPeople);
	
	Optional<Employee> findByEmailPeople(String emailPeople);
	
	@Query("SELECT e FROM Employee e " +
		       "LEFT JOIN FETCH e.schedule s " +
		       "WHERE e.userEntity = :userEntity")
	Employee findByUserEntityWithSchedules(@Param("userEntity") UserEntity userEntity);
	
	@Modifying
    @Query("UPDATE Employee e SET e.foto = :foto WHERE e.id = :id")
    public void updateFoto(@Param("id") Integer id, @Param("foto") String foto);
	
	//Cargar los datos del mepleado junto con los de tardines record
	@Query("SELECT e FROM Employee e " +
	           "JOIN e.userEntity u " +
	           "JOIN u.roles r " +
	           "LEFT JOIN FETCH e.attendWorks " +
	           "WHERE r.authority IN ('ROLE_SUPPORT', 'ROLE_EMPLOYEE')")
	Page<Employee> findAllPeopleWithTardinessRecordsAndRoles(Pageable pageable);
    
}
