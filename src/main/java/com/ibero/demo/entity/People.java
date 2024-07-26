package com.ibero.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "peoples")
public class People implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	@Column(length = 20)
	private String name;
	
	@Column(name = "ful_name")
	private String fullName;

	@NotNull
	@Column(name="happy_day")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date happyDay;
	
	@NotEmpty
	@Column(length = 20)
	private String firstLastName;
	
	@NotEmpty
	@Column(length = 20)
	private String secondLastName;
	
	@NotNull
    @Min(18)
	@Column(length = 2)
	private int agePeople;
	
	@NotEmpty
	@Column(unique = true)
	private String identityPeople;
	
	@NotEmpty
	@Email
	@Column(length = 40, unique = true)
	private String emailPeople;
	
	
	@OneToOne(mappedBy = "people",cascade = CascadeType.ALL)
	private User user;
	
	/* Gestionar el nombre completo  y el ID*/
	@PreUpdate
	public void preUpdate ()
	{
		this.fullName = this.name + " "+ this.firstLastName + " "+ this.secondLastName ;
	}

	@PrePersist
	public void prePersist ()
	{

		this.fullName =  this.name + " "+ this.firstLastName + " "+ this.secondLastName ;
	}
	
	public People() {
	}

	public People(@NotEmpty String name, String fullName, Date happyDay, @NotEmpty String firstLastName,
			@NotEmpty String secondLastName, @NotNull @Min(18) int agePeople, @NotEmpty String identityPeople,
			@NotEmpty @Email String emailPeople) {
		super();
		this.name = name;
		this.fullName = fullName;
		this.happyDay = happyDay;
		this.firstLastName = firstLastName;
		this.secondLastName = secondLastName;
		this.agePeople = agePeople;
		this.identityPeople = identityPeople;
		this.emailPeople = emailPeople;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getHappyDay() {
		return happyDay;
	}

	public void setHappyDay(Date happyDay) {
		this.happyDay = happyDay;
	}

	public String getFirstLastName() {
		return firstLastName;
	}

	public void setFirstLastName(String firstLastName) {
		this.firstLastName = firstLastName;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	public int getAgePeople() {
		return agePeople;
	}

	public void setAgePeople(int agePeople) {
		this.agePeople = agePeople;
	}

	public String getIdentityPeople() {
		return identityPeople;
	}

	public void setIdentityPeople(String identityPeople) {
		this.identityPeople = identityPeople;
	}

	public String getEmailPeople() {
		return emailPeople;
	}

	public void setEmailPeople(String emailPeople) {
		this.emailPeople = emailPeople;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	private static final long serialVersionUID = 1L;
}
