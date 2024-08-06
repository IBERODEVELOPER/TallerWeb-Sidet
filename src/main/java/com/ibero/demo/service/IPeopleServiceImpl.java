package com.ibero.demo.service;

import java.util.List;

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
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override 
	@Transactional(readOnly = true)
	public List<Employee> findAllPeople() {
		return (List<Employee>) peopleDao.findAll();
	}

	@Override
	@Transactional
	public void SavePeople(Employee employee) {
		// Encriptar la contraseña del usuario asociado al empleado
		UserEntity user = employee.getUserEntity();
		List<Role> roles = user.getRoles();
        if (user != null && user.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }
        //Gestionar la referencia relación con Roles de usuario
        if (employee.getUserEntity() != null) {
            user.setEmployee(employee);
            roles.add(new Role("ROLE_USER"));
            user.setRoles(roles);
        }
        
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
