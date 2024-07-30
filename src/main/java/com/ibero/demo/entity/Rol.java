package com.ibero.demo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "rols")
public class Rol implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	@NotEmpty
	@Column(name="authority",length = 20)
	private String authority;
	
	@NotEmpty
	@Column(length = 80)
	private String descripRolUser;
	
	public Rol() {
	}
	
	public Rol(User user, @NotEmpty String authority, @NotEmpty String descripRolUser) {
		super();
		this.user = user;
		this.authority = authority;
		this.descripRolUser = descripRolUser;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getDescripRolUser() {
		return descripRolUser;
	}

	public void setDescripRolUser(String descripRolUser) {
		this.descripRolUser = descripRolUser;
	}
	
	private static final long serialVersionUID = 1L;
}
