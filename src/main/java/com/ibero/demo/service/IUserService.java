package com.ibero.demo.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.ibero.demo.entity.User;

public interface IUserService {
	
	/*Metodo para listar todos los usuarios*/
	public List<User> findAllUsers();
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void saveUser(User user);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public User findOneUser(Integer id);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdUser(Integer id);

	/*Metodo para el filtro por nombre*/
	//public User findByUsername(String username);

}
