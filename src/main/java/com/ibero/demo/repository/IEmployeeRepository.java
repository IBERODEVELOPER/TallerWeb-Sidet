package com.ibero.demo.repository;

import com.ibero.demo.entity.EntityEmployee;
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
public interface IEmployeeRepository extends CrudRepository<EntityEmployee, Integer>, PagingAndSortingRepository<EntityEmployee, Integer>{
	
	public EntityEmployee findByUserEntity(UserEntity userEntity);
	
	EntityEmployee findByIdentityPeople(String identityPeople);
	
	Optional<EntityEmployee> findByEmailPeople(String emailPeople);
	
	@Query("SELECT e FROM EntityEmployee e " +
		       "LEFT JOIN FETCH e.schedule s " +
		       "WHERE e.userEntity = :userEntity")
	EntityEmployee findByUserEntityWithSchedules(@Param("userEntity") UserEntity userEntity);
	
	@Modifying
    @Query("UPDATE EntityEmployee e SET e.foto = :foto WHERE e.id = :id")
    public void updateFoto(@Param("id") Integer id, @Param("foto") String foto);
	
	//Cargar los datos del mepleado junto con los de tardines record
	@Query("SELECT e FROM EntityEmployee e " +
	           "JOIN e.userEntity u " +
	           "JOIN u.roles r " +
	           "LEFT JOIN FETCH e.attendWorks " +
	           "WHERE r.authority IN ('ROLE_SUPPORT', 'ROLE_EMPLOYEE')")
	Page<EntityEmployee> findAllPeopleWithTardinessRecordsAndRoles(Pageable pageable);
    
}
