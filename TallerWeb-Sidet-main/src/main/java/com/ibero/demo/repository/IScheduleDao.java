package com.ibero.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.ibero.demo.entity.EntityEmployee;
import com.ibero.demo.entity.Schedule;

public interface IScheduleDao extends CrudRepository<Schedule, Integer>{

	// Método para encontrar el Schedule por empleado
    Schedule findByEmployee(EntityEmployee employee);
}
