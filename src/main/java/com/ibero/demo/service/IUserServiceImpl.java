package com.ibero.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IUserDao;
import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.UserEntity;

@Service("iUserServiceImpl")
public class IUserServiceImpl implements IUserService,UserDetailsService {

	@Autowired
	public IUserDao userDao;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> findAllUsers() {
		return (List<UserEntity>) userDao.findAll();
	}

	@Override
	@Transactional
	public void saveUser(UserEntity user) {
		userDao.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserEntity findOneUser(Integer id) {
		return userDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdUser(Integer id) {
		userDao.deleteById(id);
	}

	@Override
	@Transactional
	public UserEntity save(UserEntity user) {
		return userDao.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userDao.findByUserName(username);
		if(user == null) {
			logger.error("Error en el login: no existe el usuario '"+username+"'");
			throw new UsernameNotFoundException("Username "+ username+" no existe en el sistema");
		}
		List<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
		for(Role role:user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		if(authorities.isEmpty()) {
			logger.error("Error en el login: usuario '"+username+"' no tiene roles asignados");
			throw new UsernameNotFoundException("Error en el login: usuario '"+username+"' no tiene roles asignados");
		}
		return new User(user.getUserName(), user.getUserPassword(), user.getUserestado(), true, true, true, authorities);
	}
}
