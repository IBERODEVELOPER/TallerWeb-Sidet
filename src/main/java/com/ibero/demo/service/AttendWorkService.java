package com.ibero.demo.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.AttendWork;
import com.ibero.demo.util.DaysWeek;

public interface AttendWorkService {
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void saveTarding(AttendWork tardingrecord);
	/*Metodo para listar todos los registros de asistencia*/
	public List<AttendWork> findAllReports();
	/*Metodo para listar las Personas registrado en el Sistema*/
	public Page<AttendWork> findAllTardingreport(Pageable page);
	//metodo para guardar la tardanza del empleado segun el día y su hora de ingreso programado
	public void savetarding(Employee employee, DaysWeek diaEnum, LocalTime horaEntrada, LocalTime horaActual);
	//para validar si existe un registro
	public boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
	//buscar por id de tardins
	public AttendWork findbyIdtardinesRecord(Integer id);
	/*Metodo para eliminar registro de asistencia*/
	public void deleteAsistenciaById(Integer id);
	// fintarding 
	Page<AttendWork> findTardinessByEmployee(Employee employee, Pageable pageable);
	// Definimos el método para buscar por empleado
    List<AttendWork> findByEmployee(Employee employee);
    // Definimos el método para buscar por empleado y mes
    List<AttendWork> findTardinessByEmployeeAndMonth(Employee employee,int month);
    //busqueda del empleado y mes
    public Page<AttendWork> findTardinessByEmployeeAndMonth(Employee employee, int month, Pageable pageable);
    //buscar asistencia del día
    Optional<AttendWork> findByEmployeeAndDate(Employee employee, LocalDate date);
    
}
