package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IPeopleDao;
import com.ibero.demo.entity.Employee;

@Service
public class IPeopleServiceImpl implements IPeopleService{

	@Autowired
	private IPeopleDao peopleDao;
	
	@Override 
	@Transactional(readOnly = true)
	public List<Employee> findAllPeople() {
		return (List<Employee>) peopleDao.findAll();
	}

	@Override
	@Transactional
	public void SavePeople(Employee employee) {
		peopleDao.save(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findOnePerson(Integer id) {
		return peopleDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdPerson(Integer id) {
		peopleDao.deleteById(id);	
	}

}
