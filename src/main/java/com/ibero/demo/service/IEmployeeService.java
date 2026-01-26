package com.ibero.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibero.demo.entity.EntityEmployee;
import com.ibero.demo.entity.UserEntity;

public interface IEmployeeService {

	/*Metodo para listar las Personas registrado en el Sistema*/
	public List<EntityEmployee> findAllPeople();
	
	/*Metodo para listar las Personas registrado en el Sistema*/
	public Page<EntityEmployee> findAllPeople(Pageable page);
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void SavePeople(EntityEmployee employee);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public EntityEmployee findOnePerson(Integer id);
	
	//Metodo para actualizar solo foto
	public void updateFoto(Integer id, String foto);
	
	/*Metodo para Obtener datos del usuario*/
	public EntityEmployee findByUserEntity(UserEntity userEntity);
	
	//Metodo para buscar empleado por numero de documento
	EntityEmployee findByIdentityPeople(String identityPeople);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdPerson(Integer id);
	
	//Obtener el Email de la persona
	public Optional<EntityEmployee> findByEmailPeople(String emailPeople);
	
	//carga de schudule consulta 
	public EntityEmployee getEmployeeWithFullSchedule(UserEntity userEntity);
	
	//carga los datos del empleado junto con tardanzas
	public Page<EntityEmployee> findAllPeopleWithTardinessRecordsAndRoles(Pageable pageable);
}
