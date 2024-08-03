package com.ibero.demo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "people_id", referencedColumnName = "id")
	private Employee employee;
	
	@NotEmpty
	@Column(name="username", length = 40)
    private String userName;
	
	@NotEmpty
	@Column(name="userpassword",length = 70)
    private String userPassword;
	
	@Column(name="userenabled")
    private Boolean userestado;
	
	//@OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="user_id")
	private List<Role> roles;
	
	public UserEntity(Integer id, @NotEmpty String userName, 
			@NotEmpty String userPassword, Boolean userestado,
			List<Role> roles) {
		super();
		this.id = id;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userestado = userestado;
		this.roles = roles;
	}

	public UserEntity() {
		roles = new ArrayList<Role>();
	}
	
	public void addRole(Role role) {
		roles.add(role);
	}
	
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Boolean getUserestado() {
		return userestado;
	}

	public void setUserestado(Boolean userestado) {
		this.userestado = userestado;
	}
	
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	private static final long serialVersionUID = 1L;
	
}
