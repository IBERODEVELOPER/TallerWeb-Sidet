package com.ibero.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.Id;
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
public class Employee implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "full_name")
	private String fullName;
	
	@NotEmpty
	@Column(name = "employeename",length = 20)
	private String name;
	
	@NotEmpty
	@Column(length = 20)
	private String firstLastName;
	
	@NotEmpty
	@Column(length = 20)
	private String secondLastName;
	
	@NotNull
	@Column(name="happy_day")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date happyDay;
	
	@NotNull
	@Column(name="ingreso")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechingreso;
	
	@NotNull
    @Min(18)
	@Column(length = 2)
	private int agePeople;
	
	@NotEmpty
	@Column(name="tdocumento",length = 30)
	private String tipoDocumento;
	
	@NotEmpty
	@Column(unique = true , length = 20)
	private String identityPeople;
	
	@NotEmpty
	@Column(name="genero",length = 20)
	private String genero;
	
	@NotEmpty
	@Column(name="estadocivil",length = 10)
	private String estadoCivil;
	
	@NotEmpty
	@Column(name="cargo",length = 100)
	private String cargo;
	
	@NotEmpty
	@Email
	@Column(length = 40)
	private String emailPeople;
	
	@OneToOne(mappedBy = "employee",cascade = CascadeType.ALL)
	private UserEntity userEntity;
	
	@OneToOne(mappedBy = "employe",cascade = CascadeType.ALL)
	private Address address;
	
	public Employee() {	}
	
	/* Gestionar el nombre completo*/
	@PreUpdate
	public void preUpdate ()
	{		this.fullName = this.name + " "+ this.firstLastName + " "+ this.secondLastName ;	}

	@PrePersist
	public void prePersist ()
	{		this.fullName =  this.name + " "+ this.firstLastName + " "+ this.secondLastName ;	}
	
	//Manejo de relaciones inversas
	public UserEntity getUser() {
		return userEntity;
	}

	public void setUser(UserEntity userEntity) {
		this.userEntity = userEntity;
		if(userEntity != null) {
			userEntity.setEmployee(this);
		}
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
        if (address != null) {
            address.setEmploye(this); // Establece la relación inversa
        }
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Date getHappyDay() {
		return happyDay;
	}

	public void setHappyDay(Date happyDay) {
		this.happyDay = happyDay;
	}

	public int getAgePeople() {
		return agePeople;
	}

	public void setAgePeople(int agePeople) {
		this.agePeople = agePeople;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getIdentityPeople() {
		return identityPeople;
	}

	public void setIdentityPeople(String identityPeople) {
		this.identityPeople = identityPeople;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getEmailPeople() {
		return emailPeople;
	}

	public void setEmailPeople(String emailPeople) {
		this.emailPeople = emailPeople;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Date getFechingreso() {
		return fechingreso;
	}

	public void setFechingreso(Date fechingreso) {
		this.fechingreso = fechingreso;
	}	
	
	private static final long serialVersionUID = 1L;
}
