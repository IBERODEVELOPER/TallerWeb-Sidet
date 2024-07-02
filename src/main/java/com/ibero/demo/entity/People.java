package com.ibero.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

@Entity
@Table(name = "peoples")
public class People implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPeople;
	
	@NotEmpty
	@Column(length = 20)
	private String namePeople;
	
	@NotEmpty
	@Column(length = 20)
	private String firstLastNamePeople;
	
	@NotEmpty
	@Column(length = 20)
	private String secondLastNamePeople;
	
	@NotNull
    @Min(18)
	@Column(length = 2)
	private int agePeople;
	
	@NotEmpty
	@Column(length = 20, unique = true)
	private String identityPeople;
	
	@NotEmpty
	@Email
	@Column(length = 40, unique = true)
	private String emailPeople;
	
	public People() {
		super();
	}

	public People(Integer idPeople, String namePeople, 
			String firstLastNamePeople, 
			String secondLastNamePeople,
			int agePeople, String identityPeople, 
			String emailPeople) {
		super();
		this.idPeople = idPeople;
		this.namePeople = namePeople;
		this.firstLastNamePeople = firstLastNamePeople;
		this.secondLastNamePeople = secondLastNamePeople;
		this.agePeople = agePeople;
		this.identityPeople = identityPeople;
		this.emailPeople = emailPeople;
	}

	public int getIdPeople() {
		return idPeople;
	}

	public void setIdPeople(Integer idPeople) {
		this.idPeople = idPeople;
	}

	public String getNamePeople() {
		return namePeople;
	}

	public void setNamePeople(String namePeople) {
		this.namePeople = namePeople;
	}

	public String getFirstLastNamePeople() {
		return firstLastNamePeople;
	}

	public void setFirstLastNamePeople(String firstLastNamePeople) {
		this.firstLastNamePeople = firstLastNamePeople;
	}

	public String getSecondLastNamePeople() {
		return secondLastNamePeople;
	}

	public void setSecondLastNamePeople(String secondLastNamePeople) {
		this.secondLastNamePeople = secondLastNamePeople;
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
	
	private static final long serialVersionUID = 1L;
}
