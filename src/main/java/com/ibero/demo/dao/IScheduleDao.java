package com.ibero.demo.dao;

import org.springframework.data.repository.CrudRepository;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;

public interface IScheduleDao extends CrudRepository<Schedule, Integer>{

	// Método para encontrar el Schedule por empleado
    Schedule findByEmployee(Employee employee);
}
