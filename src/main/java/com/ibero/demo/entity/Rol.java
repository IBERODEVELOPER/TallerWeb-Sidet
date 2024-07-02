package com.ibero.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "rolusers")
public class Rol {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRolUser;
	
	@NotEmpty
	@Column(length = 20)
	private String levelUser;
	
	@NotEmpty
	@Column(length = 80)
	private String descripRolUser;

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

}
