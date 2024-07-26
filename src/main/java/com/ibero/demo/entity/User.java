package com.ibero.demo.entity;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "users")
public class User implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "people_id", referencedColumnName = "id")
	private People people;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="roles")
	private Rol roles;
	
	@NotEmpty
	@Column(length = 20)
    private String userName;
	
	@NotEmpty
	@Column(length = 20)
    private String userPassword;
	

	public User() {
		super();
	}

	public User(Integer id, People people, Rol roles, @NotEmpty String userName, @NotEmpty String userPassword) {
		super();
		this.id = id;
		this.people = people;
		this.roles = roles;
		this.userName = userName;
		this.userPassword = userPassword;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public People getPeople() {
		return people;
	}

	public void setPeople(People people) {
		this.people = people;
	}

	public Rol getRoles() {
		return roles;
	}

	public void setRoles(Rol roles) {
		this.roles = roles;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	private static final long serialVersionUID = 1L;
	
}
