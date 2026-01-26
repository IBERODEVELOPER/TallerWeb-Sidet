package com.ibero.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "attachs")
public class AttachCase implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(length = 20)
	private String nameAttach;
	
	@Column(name = "upload_date")
	private LocalDateTime dateAttach;
	
	@ManyToOne
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    private Case cas;
	
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity uploadedBy;
	
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getNameAttach() {
		return nameAttach;
	}


	public void setNameAttach(String nameAttach) {
		this.nameAttach = nameAttach;
	}


	public LocalDateTime getDateAttach() {
		return dateAttach;
	}


	public void setDateAttach(LocalDateTime dateAttach) {
		this.dateAttach = dateAttach;
	}


	public Case getCas() {
		return cas;
	}


	public void setCas(Case cas) {
		this.cas = cas;
	}


	public UserEntity getUploadedBy() {
		return uploadedBy;
	}


	public void setUploadedBy(UserEntity uploadedBy) {
		this.uploadedBy = uploadedBy;
	}


	private static final long serialVersionUID = 1L;

}
