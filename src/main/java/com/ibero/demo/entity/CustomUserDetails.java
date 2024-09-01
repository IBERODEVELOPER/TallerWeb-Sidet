package com.ibero.demo.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

	private String employeeFoto;
	private UserEntity userEntity;
	
	public CustomUserDetails(UserEntity userEntity,String username,String password, boolean enabled,
			boolean accountNonExpired,boolean credentialsNonExpired,boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,String employeeFoto){
		 super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		 this.employeeFoto = employeeFoto;
		 this.userEntity = userEntity;
	}
	
	// Validar Role
	public boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return false;
		}
		Authentication aut = context.getAuthentication();
		if (aut == null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = aut.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority(role));
	}

	public String getEmployeeFoto() {
        return employeeFoto;
    }
	
	public UserEntity getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public void setEmployeeFoto(String employeeFoto) {
		this.employeeFoto = employeeFoto;
	}



	private static final long serialVersionUID = 1L;

}
