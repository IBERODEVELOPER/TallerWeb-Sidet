package com.ibero.demo.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.TardinessRecord;
import com.ibero.demo.util.DaysWeek;

public interface TardinessRecordService {
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void saveTarding(TardinessRecord tardingrecord);
	/*Metodo para listar las Personas registrado en el Sistema*/
	public Page<TardinessRecord> findAllTardingreport(Pageable page);
	//metodo para guardar la tardanza del empleado segun el día y su hora de ingreso programado
	public void savetarding(Employee employee, DaysWeek diaEnum, LocalTime horaEntrada, LocalTime horaActual);
	//para validar si existe un registro
	public boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
	//buscar por id de tardins
	public TardinessRecord findbyIdtardinesRecord(Integer id);
	/*Metodo para eliminar registro de asistencia*/
	public void deleteAsistenciaById(Integer id);
	// fintarding 
	Page<TardinessRecord> findTardinessByEmployee(Employee employee, Pageable pageable);
	// Definimos el método para buscar por empleado
    List<TardinessRecord> findByEmployee(Employee employee);
}
