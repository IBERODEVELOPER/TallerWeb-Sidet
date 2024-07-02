package com.ibero.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ibero.demo.entity.Rol;

public interface IRolUserDao extends JpaRepository<Rol, Integer>{

}
