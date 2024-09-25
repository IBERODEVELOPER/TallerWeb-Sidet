package com.ibero.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.Employee;

public interface ActivityService {
	//listar todos los eventos:
	List<ActivityEntity> findByUserAndDate(String username, LocalDate endTime);
	//Busca la actividad si esta cerrado
	Optional<ActivityEntity> findActivityByUsernameAndEndTimeIsNull(String username);
	//Guardar actividad
	public ActivityEntity saveActivity(ActivityEntity activity);
	
	//Buscar actividad por ID
	public ActivityEntity findActivityById(Integer id);
	
	//Consultar eventos sin cerrado
	List<ActivityEntity> findByEndTimeIsNull();

    //Para descargar reportes
    public List<ActivityEntity> findActivitiesBetweenDates(String startDate, String endDate);
    
    //Para descargar reportes FECHA INICIO FIN E  ID
    List<ActivityEntity> findActivitiesBetweenDatesAndEventId(String startDate,String endDate,Integer eventId);
}
