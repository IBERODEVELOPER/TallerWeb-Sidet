package com.ibero.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ibero.demo.entity.*;

public interface CustomerService {
	
	/*Lista de Clientes SIN PAGINACIÓN*/
	public List<Customer> findAllCustomer();
	
	/*Lista de Clientes CON PAGINACIÓN*/
	public Page<Customer> findAllCustomer(Pageable page);
	
	/*Guardar cliente*/
	public void saveCustomer(Customer customer);
	
	//Buscar Cliente por ID
	public Customer findCustomerById(Integer id);
	
	//Eliminar Cliente mediante su ID
	public void deleteCustomerById(Integer id);

}
