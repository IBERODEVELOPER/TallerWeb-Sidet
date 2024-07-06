package com.ibero.demo.service;

import java.util.List;

import com.ibero.demo.entity.Rol;

public interface IRolUserService {
	
	/*Metodo para listar los roles del sistema*/
	public List<Rol> findAllRol();
	
	/*Metodo para registrar Roles*/
	public void saveRol(Rol rolUser);
	
	/*Metodo para Obtener datos de un Rol por si ID*/
	public Rol findOneRol(Integer id);
	
	/*Metodo para eliminar Roles por su ID*/
	public void deleteIdRol(Integer id);
	
	public Rol findRolByLevel(String level);

}
