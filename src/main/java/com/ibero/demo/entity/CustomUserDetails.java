package com.ibero.demo.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

	private String employeeFoto;
	
	public CustomUserDetails(String username,String password, boolean enabled,
			boolean accountNonExpired,boolean credentialsNonExpired,boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,String employeeFoto){
		 super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		 this.employeeFoto = employeeFoto;
	}
	
	public String getEmployeeFoto() {
        return employeeFoto;
    }

	private static final long serialVersionUID = 1L;

}
