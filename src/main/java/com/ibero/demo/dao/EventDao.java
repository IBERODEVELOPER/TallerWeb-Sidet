package com.ibero.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.ibero.demo.entity.Event;

@Repository
public interface EventDao extends CrudRepository<Event, Integer>,PagingAndSortingRepository<Event, Integer>{

}
