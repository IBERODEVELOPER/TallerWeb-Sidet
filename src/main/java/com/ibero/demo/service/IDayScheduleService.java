package com.ibero.demo.service;

import com.ibero.demo.entity.DaySchedule;

public interface IDayScheduleService {
	
	/*Metodo para guardar los datos del formulario*/
	public void savesdaySchedule(DaySchedule daySchedule);
	
	/*Metodo para Obtener datos mediante el ID*/
	public DaySchedule findOneSchuduleDay(Integer id);
	
	/*Metodo para eliminar mediante el ID*/
	public void deleteId(Integer id);

}
