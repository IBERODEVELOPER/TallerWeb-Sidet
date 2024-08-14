package com.ibero.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.ibero.demo.dao.IUserDao;
import com.ibero.demo.entity.CustomUserDetails;
import com.ibero.demo.entity.Role;
import com.ibero.demo.entity.UserEntity;
import com.ibero.demo.util.EmailValuesDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service("iUserServiceImpl")
public class IUserServiceImpl implements IUserService, UserDetailsService {

	@Autowired
	public IUserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> findAllUsers() {
		logger.info("Listar usuarios");
		return (List<UserEntity>) userDao.findAll();
	}

	@Override
	@Transactional
	public void saveUser(UserEntity user) {
		// Encriptar la contraseña del usuario
		if (user != null && user.getUserPassword() != null) {
			user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
		}
		logger.info("Guardando usuario void");
		userDao.save(user);
	}

	@Override
	@Transactional
	public UserEntity save(UserEntity user) {
		logger.info("Guardando usuario con retorno user");
		return userDao.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserEntity findOneUser(Integer id) {
		logger.info("Buscar usuario por su id: " + id);
		return userDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteIdUser(Integer id) {
		logger.info("Eliminando usuario por su id: " + id);
		userDao.deleteById(id);
	}

	// para actualizar solo los datos estado o contraseña
	@Override
	@Transactional
	public void updateUser(Integer id, String userPassword, Boolean userestado, List<Role> roles) {
		UserEntity user = userDao.findById(id).orElseThrow();
		user.setUserPassword(passwordEncoder.encode(userPassword));
		user.setUserestado(userestado);
		// Comparar y actualizar los roles
		List<Role> currentRoles = user.getRoles();
		// Eliminar roles que ya no están en newRoles
		currentRoles.removeIf(role -> !roles.contains(role));
		// Agregar roles que están en newRoles y no en currentRoles
		for (Role newRole : roles) {
			if (!currentRoles.contains(newRole)) {
				currentRoles.add(newRole);
			}
		}
		//lanzar un email 
		
		
		userDao.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userDao.findByUserName(username);
		if (user == null) {
			logger.error("Error en el login: no existe el usuario '" + username + "'");
			throw new UsernameNotFoundException("Username " + username + " no existe en el sistema");
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		if (authorities.isEmpty()) {
			logger.error("Error en el login: usuario '" + username + "' no tiene roles asignados");
			throw new UsernameNotFoundException(
					"Error en el login: usuario '" + username + "' no tiene roles asignados");
		}
		// Obtener la foto del empleado asociado al usuario
	    String employeeFoto = user.getEmployee().getFoto();
		
		return new CustomUserDetails(user, user.getUserName(), user.getUserPassword(), user.getUserestado(),
	            true, true, true, authorities, employeeFoto);
	}

	@Transactional(readOnly = true)
	@Override
	public UserEntity findByUserName(String userName) {
		return userDao.findByUserName(userName);
	}

	@Transactional(readOnly = true)
	@Override
	public String getPasswordByUsername(String userName) {
		UserEntity user = userDao.findByUserName(userName);
		return user != null ? user.getUserPassword() : null;
	}

	
	@Override
	@Transactional(readOnly = true)
	public boolean checkPassword(Integer id, String userPassword) {
		UserEntity user = userDao.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		// Comparar la contraseña ingresada con la contraseña cifrada almacenada
        return passwordEncoder.matches(userPassword, user.getUserPassword());
	}

	@Transactional
	@Override
	public void updatePass(Integer id, String userPassword,boolean status) {
		UserEntity user = userDao.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		user.setTemporaryPassword(status);
		user.setUserPassword(userPassword);
		userDao.save(user);
	}
	
}
