package com.ibero.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.ibero.demo.entity.DaySchedule;

public interface IDayScheduleDao extends CrudRepository<DaySchedule, Integer>{

}
