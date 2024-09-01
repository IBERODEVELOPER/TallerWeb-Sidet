package com.ibero.demo.dao;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import com.ibero.demo.entity.UserEntity;

public interface IUserDao extends CrudRepository<UserEntity, Integer>,PagingAndSortingRepository<UserEntity, Integer> {
	
    public UserEntity findByUserName(String userName);
    
    //para actualizar la contraseña con JPQL
    @Modifying
    @Query("UPDATE UserEntity u SET u.userPassword = :userPassword WHERE u.id = :id")
    public void updatePass(@Param("id") Integer id, @Param("userPassword") String userPassword);
  
}
