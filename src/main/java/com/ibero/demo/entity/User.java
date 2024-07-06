package com.ibero.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "users")
public class User implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idUser;
	
	@OneToOne
	@JoinColumn(name="idpeople")
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
	
	@NotEmpty
	@Column(length = 20)
    private char userState;
    
	public User() {
		super();
	}

	public User(Integer idUser, People people, Rol roles, @NotEmpty String userName, @NotEmpty String userPassword,
			@NotEmpty char userState) {
		super();
		this.idUser = idUser;
		this.people = people;
		this.roles = roles;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userState = userState;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", people=" + people + ", roles=" + roles + ", userName=" + userName
				+ ", userPassword=" + userPassword + ", userState=" + userState + "]";
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
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

	public char getUserState() {
		return userState;
	}

	public void setUserState(char userState) {
		this.userState = userState;
	}

	private static final long serialVersionUID = 1L;
}
