package com.ibero.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IPeopleDao;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.UserEntity;

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

	@Override
	@Transactional(readOnly = true)
	public Employee findByUserEntity(UserEntity userEntity) {
		return peopleDao.findByUserEntity(userEntity);
	}

	@Override
	@Transactional
	public void updateFoto(Integer id, String foto) {
		peopleDao.updateFoto(id, foto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Employee> findByEmailPeople(String emailPeople) {
		return peopleDao.findByEmailPeople(emailPeople);
	}
	}
