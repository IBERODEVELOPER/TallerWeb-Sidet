package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IAddressDao;
import com.ibero.demo.entity.Address;
import com.ibero.demo.entity.Employee;

@Service
public class IAddressServiceImpl implements IAddressService {

	@Autowired
	private IAddressDao addressdao;

	@Override 
	@Transactional(readOnly = true)
	public List<Address> findAllAddress() {
		return (List<Address>) addressdao.findAll();
	}
	

	@Override
	@Transactional
	public Address saveAddres(Address address) {
		return addressdao.save(address);
	}  

	@Override
	@Transactional
	public void saveAddress(Address address) {
		addressdao.save(address); 
	}

	@Override
	public Address findOneAddress(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteIdAddress(Integer id) {
		// TODO Auto-generated method stub
		
	}
	
	

}
