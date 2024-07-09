package com.ibero.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.ibero.demo.entity.User;

@Repository
public interface IUserDao extends CrudRepository<User, Integer> {

   /* @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
    User findByUsername(@Param("username") String username);*/
    
}
