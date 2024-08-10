package com.ibero.demo.service;

import java.util.List;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;

public interface IPeopleService {

	/*Metodo para listar las Personas registrado en el Sistema*/
	public List<Employee> findAllPeople();
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void SavePeople(Employee employee);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public Employee findOnePerson(Integer id);
	
	/*Metodo para Obtener datos del usuario*/
	public Employee findByUserEntity(UserEntity userEntity);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdPerson(Integer id);
}
