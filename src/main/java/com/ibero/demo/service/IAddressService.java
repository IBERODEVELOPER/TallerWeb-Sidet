package com.ibero.demo.service;

import java.util.List;

import com.ibero.demo.entity.Address;

public interface IAddressService {
	
	/*Metodo para listar las Personas registrado en el Sistema*/
	public List<Address> findAllAddress();
	
	/*Metodo para guardar los datos del formulario Persona*/
	public Address saveAddres(Address address);
	
	/*Metodo para guardar los datos del formulario Persona*/
	public void saveAddress(Address address);
	
	/*Metodo para Obtener datos de una Persona por si ID*/
	public Address findOneAddress(Integer id);
	
	/*Metodo para eliminar Persona por su ID*/
	public void deleteIdAddress(Integer id);

}
