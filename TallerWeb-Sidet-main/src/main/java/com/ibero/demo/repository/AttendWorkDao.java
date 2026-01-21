package com.ibero.demo.repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ibero.demo.entity.EntityEmployee;
import com.ibero.demo.entity.AttendWork;

public interface AttendWorkDao extends CrudRepository<AttendWork, Integer>, PagingAndSortingRepository<AttendWork, Integer>{

	// Definimos el método para buscar por empleado
    List<AttendWork> findByEmployee(EntityEmployee employee);
    //Buscar la asistencia de Hoy
  	Optional<AttendWork> findByEmployeeAndDate(EntityEmployee employee, LocalDate date);
    // Definimos el método para buscar por empleado y mes
    @Query("SELECT tr FROM AttendWork tr WHERE tr.employee = :employee AND MONTH(tr.date) = :month")
    List<AttendWork> findByEmployeeAndMonthL(@Param("employee") EntityEmployee employee, @Param("month") int month);
	Page<AttendWork> findByEmployee(EntityEmployee employee, Pageable pageable);
	boolean existsByEmployeeAndDate(EntityEmployee employee, LocalDate date);
	//para la busqueda de empleado con el mes
	 @Query("SELECT tr FROM AttendWork tr WHERE tr.employee = :employee AND MONTH(tr.date) = :month")
	 Page<AttendWork> findByEmployeeAndMonth(@Param("employee") EntityEmployee employee, @Param("month") int month, Pageable pageable);

}
