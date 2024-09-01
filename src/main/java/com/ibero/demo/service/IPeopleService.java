package com.ibero.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.UserEntity;

public interface IPeopleService {

	/*Metodo para listar las Personas registrado en el Sistema*/
	public List<Employee> findAllPeople();
	
	/*Metodo para listar las Personas registrado en el Sistema*/
	public Page<Employee> findAllPeople(Pageable page);
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void SavePeople(Employee employee);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public Employee findOnePerson(Integer id);
	
	//Metodo para actualizar solo foto
	public void updateFoto(Integer id, String foto);
	
	/*Metodo para Obtener datos del usuario*/
	public Employee findByUserEntity(UserEntity userEntity);
	
	//Metodo para buscar empleado por numero de documento
	Employee findByIdentityPeople(String identityPeople);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdPerson(Integer id);
	
	//Obtener el Email de la persona
	public Optional<Employee> findByEmailPeople(String emailPeople);
	
	//carga de schudule consulta 
	public Employee getEmployeeWithFullSchedule(UserEntity userEntity);
	
	//carga los datos del empleado junto con tardanzas
	public Page<Employee> findAllPeopleWithTardinessRecordsAndRoles(Pageable pageable);
}
