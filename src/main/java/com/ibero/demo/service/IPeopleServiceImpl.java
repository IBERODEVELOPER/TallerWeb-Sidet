package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IPeopleDao;
import com.ibero.demo.entity.People;

@Service
public class IPeopleServiceImpl implements IPeopleService{

	@Autowired
	private IPeopleDao peopleDao;
	
	@Override 
	@Transactional(readOnly = true)
	public List<People> findAllPeople() {
		return (List<People>) peopleDao.findAll();
	}

	@Override
	@Transactional
	public void SavePeople(People people) {
		peopleDao.save(people);
	}

	@Override
	@Transactional(readOnly = true)
	public People findOnePerson(Integer id) {
		return peopleDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdPerson(Integer id) {
		peopleDao.deleteById(id);	
	}
	
	

}
