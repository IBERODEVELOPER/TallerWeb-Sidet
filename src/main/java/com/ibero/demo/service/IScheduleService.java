package com.ibero.demo.service;


import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;

public interface IScheduleService {
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void saveschudule(Schedule schedule);
	/*Metodo para Obtener datos de una Persona por si ID*/
	public Schedule findOneSchudule(Integer id);
	/*Metodo para eliminar mediante el ID*/
	public void deleteId(Integer id);
	// Método para encontrar el Schedule por empleado
    Schedule findByEmployee(Employee employee);
	
}
