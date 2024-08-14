package com.ibero.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.util.EmailValuesDTO;

public interface IUserService {
	
	/*Metodo para listar todos los usuarios*/
	public List<UserEntity> findAllUsers();
	
	/*Metodo para guardar los datos del formulario Persona*/
	public UserEntity save(UserEntity userEntity);
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void saveUser(UserEntity userEntity);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public UserEntity findOneUser(Integer id);
	
	/*Metodo para Obtener datos del usuario*/
	public UserEntity findByUserName(String userName);
	
	/*Metodo para Obtener password del usuario*/
	public String getPasswordByUsername(String userName);
	
	//Metodo para actualizar solo password
	public void updatePass(Integer id, String userPassword,boolean status);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdUser(Integer id);
	
	/*Metodo para verificar la contraseña ingresada no sea la misma del sistema*/
	public boolean checkPassword(Integer id, String userPassword);

	/*Metodo para el filtro por nombre*/
	public void updateUser(Integer id, String userPassword, Boolean userestado,List<Role> roles);

}
