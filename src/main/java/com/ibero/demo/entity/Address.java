package com.ibero.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "addresses")
public class Address implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	@Column(name="pais", length = 20)
	private String pais;
	
	@NotEmpty
	@Column(name="region", length = 20)
	private String region;
	
	@NotEmpty
	@Column(name="distrito", length = 20)
	private String distrito;
	
	@NotEmpty
	@Column(name="direccion", length = 200)
	private String direccion;
	
	@NotEmpty
	@Column(name="referencia", length = 200)
	private String referencia;
	
	@OneToOne
	@JoinColumn(name = "people_id", referencedColumnName = "id")
	private Employee employe;

	public Address() {
	}
	
	public Address(Integer id, @NotEmpty String pais, @NotEmpty String region, @NotEmpty String distrito,
			@NotEmpty String direccion, @NotEmpty String referencia) {
		super();
		this.id = id;
		this.pais = pais;
		this.region = region;
		this.distrito = distrito;
		this.direccion = direccion;
		this.referencia = referencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public Employee getEmploye() {
		return employe;
	}

	public void setEmploye(Employee employe) {
		this.employe = employe;
		if (employe != null && !employe.getAddress().equals(this)) {
            employe.setAddress(this); // Establece la relación inversa si aún no está establecida
        }
	}
	
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	private static final long serialVersionUID = 1L;
}
