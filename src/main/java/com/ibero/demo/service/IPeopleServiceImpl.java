package com.ibero.demo.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IPeopleDao;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.UserEntity;

@Service
public class IPeopleServiceImpl implements IPeopleService {

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

	@Override
	@Transactional
	public Employee getEmployeeWithFullSchedule(UserEntity userEntity) {
		Employee employee = peopleDao.findByUserEntityWithSchedules(userEntity);
		for (Schedule schedule : employee.getSchedule()) {
			Hibernate.initialize(schedule.getDaySchedules());
		}
		return employee;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Employee> findAllPeople(Pageable page) {
		return peopleDao.findAll(page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Employee> findAllPeopleWithTardinessRecordsAndRoles(Pageable pageable) {
		return peopleDao.findAllPeopleWithTardinessRecordsAndRoles(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findByIdentityPeople(String identityPeople) {
		return peopleDao.findByIdentityPeople(identityPeople);
	}
}
