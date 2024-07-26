package com.ibero.demo.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.ibero.demo.entity.User;


public interface IUserDao extends CrudRepository<User, Integer> {

   /*@Query(value = "Select u from user u left join u.people f where u.idUser=?1")
    public User findByUserForId(Integer id);*/
    
}
