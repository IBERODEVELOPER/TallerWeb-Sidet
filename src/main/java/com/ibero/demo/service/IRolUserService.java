package com.ibero.demo.service;

import java.util.List;

import com.ibero.demo.entity.Role;

public interface IRolUserService {
	
	/*Metodo para listar los roles del sistema*/
	public List<Role> findAllRol();
	
	/*Metodo para registrar Roles*/
	public void saveRol(Role rolUser);
	
	/*Metodo para Obtener datos de un Rol por si ID*/
	public Role findOneRol(Integer id);
	
	/*Metodo para eliminar Roles por su ID*/
	public void deleteIdRol(Integer id);
	
	//public Rol findRolByLevel(String level);

}
