package com.ibero.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ibero.demo.entity.UserEntity;

@Repository
public interface IUserDao extends CrudRepository<UserEntity, Integer> {

   //@Query(value = "Select u from user u left join u.people f where u.idUser=?1")
    public UserEntity findByUserName(String userName);
    
}
