package com.ibero.demo.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.entity.EntityEmployee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.repository.IEmployeeRepository;

@Service
public class IEmployeeServiceImpl implements IEmployeeService {

	@Autowired
	private IEmployeeRepository peopleDao;

	@Override
	@Transactional(readOnly = true)
	public List<EntityEmployee> findAllPeople() {
		return (List<EntityEmployee>) peopleDao.findAll();
	}

	@Override
	@Transactional
	public void SavePeople(EntityEmployee employee) {
		peopleDao.save(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public EntityEmployee findOnePerson(Integer id) {
		return peopleDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdPerson(Integer id) {
		peopleDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public EntityEmployee findByUserEntity(UserEntity userEntity) {
		return peopleDao.findByUserEntity(userEntity);
	}

	@Override
	@Transactional
	public void updateFoto(Integer id, String foto) {
		peopleDao.updateFoto(id, foto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<EntityEmployee> findByEmailPeople(String emailPeople) {
		return peopleDao.findByEmailPeople(emailPeople);
	}

	@Override
	@Transactional
	public EntityEmployee getEmployeeWithFullSchedule(UserEntity userEntity) {
		EntityEmployee employee = peopleDao.findByUserEntityWithSchedules(userEntity);
		for (Schedule schedule : employee.getSchedule()) {
			Hibernate.initialize(schedule.getDaySchedules());
		}
		return employee;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EntityEmployee> findAllPeople(Pageable page) {
		return peopleDao.findAll(page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EntityEmployee> findAllPeopleWithTardinessRecordsAndRoles(Pageable pageable) {
		return peopleDao.findAllPeopleWithTardinessRecordsAndRoles(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public EntityEmployee findByIdentityPeople(String identityPeople) {
		return peopleDao.findByIdentityPeople(identityPeople);
	}
}
