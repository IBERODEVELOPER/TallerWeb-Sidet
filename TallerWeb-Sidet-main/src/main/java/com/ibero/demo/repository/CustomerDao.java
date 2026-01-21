package com.ibero.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ibero.demo.entity.Customer;

public interface CustomerDao extends CrudRepository<Customer, Integer>,PagingAndSortingRepository<Customer, Integer>{

}
