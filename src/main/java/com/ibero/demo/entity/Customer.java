package com.ibero.demo.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "customers")
public class Customer implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotEmpty
	@Column(name = "name", length = 30)
	private String name;
	
	@NotEmpty
	@Column(name = "last_names", length = 30)
	private String lastNames;
	
	@Column(name = "name_complete", length = 80)
	private String nameComplete;
	
	@NotEmpty
	@Column(name = "tdocumento", length = 30)
	private String tipoDocumento;

	@NotEmpty
	@Column(unique = true, length = 20)
	private String identityCustomer;

	@NotEmpty
	@Column(name = "ruc", length = 30)
	private String RUC;
	
	@NotEmpty
	@Column(name = "razon_social", length = 30)
	private String razonsocial;
	
	//para manejar el nombre completo
	@PreUpdate
	@PrePersist
	public void preUpdate() {
		this.nameComplete = this.name + " " + this.lastNames;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastNames() {
		return lastNames;
	}

	public void setLastNames(String lastNames) {
		this.lastNames = lastNames;
	}

	public String getNameComplete() {
		return nameComplete;
	}

	public void setNameComplete(String nameComplete) {
		this.nameComplete = nameComplete;
	}

	public String getRUC() {
		return RUC;
	}

	public void setRUC(String rUC) {
		RUC = rUC;
	}

	public String getRazonsocial() {
		return razonsocial;
	}

	public void setRazonsocial(String razonsocial) {
		this.razonsocial = razonsocial;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getIdentityCustomer() {
		return identityCustomer;
	}

	public void setIdentityCustomer(String identityCustomer) {
		this.identityCustomer = identityCustomer;
	}


	private static final long serialVersionUID = 1L;
}
