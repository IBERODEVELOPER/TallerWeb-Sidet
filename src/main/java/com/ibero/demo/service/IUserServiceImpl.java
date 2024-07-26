package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IUserDao;

import com.ibero.demo.entity.User;

@Service
public class IUserServiceImpl implements IUserService {

	@Autowired
	public IUserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public List<User> findAllUsers() {
		return (List<User>) userDao.findAll();
	}

	@Override
	@Transactional
	public void saveUser(User user) {
		userDao.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public User findOneUser(Integer id) {
		return userDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdUser(Integer id) {
		userDao.deleteById(id);
	}

	@Override
	@Transactional
	public User save(User user) {
		return userDao.save(user);
	}
}
