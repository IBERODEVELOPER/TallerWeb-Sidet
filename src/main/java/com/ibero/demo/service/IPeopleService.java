package com.ibero.demo.service;

import java.util.List;
import com.ibero.demo.entity.People;

public interface IPeopleService {

	/*Metodo para listar las Personas registrado en el Sistema*/
	public List<People> findAllPeople();
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void SavePeople(People people);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public People findOnePerson(Integer id);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdPerson(Integer id);
}
