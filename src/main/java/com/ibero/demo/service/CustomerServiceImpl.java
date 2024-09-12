package com.ibero.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.CustomerDao;
import com.ibero.demo.entity.Customer;

@Service
public class CustomerServiceImpl implements  CustomerService{

	@Autowired
	private CustomerDao customerdao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Customer> findAllCustomer(Pageable page) {
		return customerdao.findAll(page);
	}

	@Override
	@Transactional
	public void saveCustomer(Customer customer) {
		customerdao.save(customer);
	}

	@Override
	@Transactional(readOnly = true)
	public Customer findCustomerById(Integer id) {
		return customerdao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteCustomerById(Integer id) {
		customerdao.deleteById(id);
	}

}
