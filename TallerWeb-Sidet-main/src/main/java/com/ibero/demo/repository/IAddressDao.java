package com.ibero.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ibero.demo.entity.Address;

@Repository
public interface IAddressDao extends CrudRepository<Address, Integer>{

}
