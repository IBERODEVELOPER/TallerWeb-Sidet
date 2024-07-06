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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "roles")
public class Rol implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRolUser;
	
	@NotEmpty
	@Column(length = 20)
	private String levelUser;
	
	@NotEmpty
	@Column(length = 80)
	private String descripRolUser;
	
	@JsonIgnore
    @OneToMany(mappedBy = "roles",cascade =CascadeType.ALL,fetch = FetchType.EAGER)
    private List<User> users=new ArrayList<>();

	public Rol(Integer idRolUser, @NotEmpty String levelUser, @NotEmpty String descripRolUser, List<User> users) {
		super();
		this.idRolUser = idRolUser;
		this.levelUser = levelUser;
		this.descripRolUser = descripRolUser;
		this.users = users;
	}

	public Rol() {
	}

	public Rol(Integer idRolUser, @NotEmpty String levelUser, @NotEmpty String descripRolUser) {
		super();
		this.idRolUser = idRolUser;
		this.levelUser = levelUser;
		this.descripRolUser = descripRolUser;
	}

	public Integer getIdRolUser() {
		return idRolUser;
	}

	public void setIdRolUser(Integer idRolUser) {
		this.idRolUser = idRolUser;
	}

	public String getLevelUser() {
		return levelUser;
	}

	public void setLevelUser(String levelUser) {
		this.levelUser = levelUser;
	}

	public String getDescripRolUser() {
		return descripRolUser;
	}

	public void setDescripRolUser(String descripRolUser) {
		this.descripRolUser = descripRolUser;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	@Override
	public String toString() {
		return "Rol [idRolUser=" + idRolUser + ", levelUser=" + levelUser + ", descripRolUser=" + descripRolUser
				+ ", users=" + users + "]";
	}
	
	private static final long serialVersionUID = 1L;
}
