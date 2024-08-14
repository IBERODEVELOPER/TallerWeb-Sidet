package com.ibero.demo.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
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
