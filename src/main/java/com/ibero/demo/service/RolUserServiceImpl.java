package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.ibero.demo.entity.Rol;
import com.ibero.demo.dao.IRolUserDao;

@Service
public class RolUserServiceImpl implements IRolUserService{

	@Autowired
	private IRolUserDao rolUserDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Rol> findAllRol() {		
		return (List<Rol>) rolUserDao.findAll();
	}

	@Override
	@Transactional
	public void saveRol(Rol rolUser) {
		rolUserDao.save(rolUser);
	}

	@Override
	@Transactional(readOnly = true)
	public Rol findOneRol(Integer id) {
		return rolUserDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdRol(Integer id) {
		rolUserDao.deleteById(id);
	}

}
