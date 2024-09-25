package com.ibero.demo.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.AttendWork;
import com.ibero.demo.entity.Employee;

public interface ActivityDao extends CrudRepository<ActivityEntity,Integer>{
	//consultar si el usuario tiene un evento sin cerrar
	@Query("SELECT a FROM ActivityEntity a WHERE a.username = :username AND a.endTime IS NULL")
	Optional<ActivityEntity> findOngoingActivityByEventAndUser(@Param("username") String username);
	//Consultar eventos sin cerrar
	List<ActivityEntity> findByEndTimeIsNull();
	//buscar actividades desde :01/01/2024 hasta:30/01/2024
	@Query(value = "SELECT * FROM activities a WHERE STR_TO_DATE(a.start_time, '%d/%m/%Y') >= STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(a.end_time, '%d/%m/%Y') <= STR_TO_DATE(:endDate, '%d/%m/%Y')", nativeQuery = true)
    List<ActivityEntity> findActivitiesBetweenDatesNative(@Param("startDate") String startDate, @Param("endDate") String endDate);
	//buscar actividades desde :01/01/2024 hasta:30/01/2024 MÁS El ID DEL EVENTO.
	@Query(value = "SELECT * FROM activities a WHERE STR_TO_DATE(a.start_time, '%d/%m/%Y') >= STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(a.end_time, '%d/%m/%Y') <= STR_TO_DATE(:endDate, '%d/%m/%Y') AND a.event_id = :eventId", nativeQuery = true)
	List<ActivityEntity> findActivitiesBetweenDatesAndEventId(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("eventId") Integer eventId);
	//Buscar la asistencia de Hoy
	@Query(value = "SELECT * FROM activities a WHERE a.username = :username AND DATE(STR_TO_DATE(a.end_time, '%d/%m/%Y')) = :endTime", nativeQuery = true)
	List<ActivityEntity> findByUserAndDate(@Param("username") String username,@Param("endTime") LocalDate endTime);
}
