package com.ibero.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cases")
public class Case implements Serializable {

	@Id
	@Column(length = 20)
	private String id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "event_id", referencedColumnName = "id")
	private Event event;

	@OneToMany(mappedBy = "cas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AttachCase> attachments = new ArrayList<>();

	@OneToMany(mappedBy = "cas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<CommentCase> comments = new ArrayList<>();

	private LocalDateTime entryDate;

	@Column(length = 10)
	private String priority;

	@Column(length = 200)
	private String affairCase;

	@Column(length = 500)
	private String descriptionCase;

	@Column(length = 10)
	private String statusCase;

	@PrePersist
	public void prePersist() {
		if (this.id == null || this.id.isEmpty()) {
			this.id = generateUniqueId();
		}
	}

	private String generateUniqueId() {
		// Generar una parte numérica aleatoria
		int randomNum = (int) (Math.random() * 10000); // Genera un número de 4 dígitos
		// Generar una letra aleatoria
		char randomLetter = (char) ('A' + Math.random() * 26); // Genera una letra mayúscula aleatoria
		// Combinar "Caso-", la letra aleatoria y el número de 8 dígitos
		return String.format("Caso-%c%08d", randomLetter, randomNum);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public List<AttachCase> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<AttachCase> attachments) {
		this.attachments = attachments;
	}

	public List<CommentCase> getComments() {
		return comments;
	}

	public void setComments(List<CommentCase> comments) {
		this.comments = comments;
	}

	public LocalDateTime  getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDateTime entryDate) {
		this.entryDate = entryDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getAffairCase() {
		return affairCase;
	}

	public void setAffairCase(String affairCase) {
		this.affairCase = affairCase;
	}

	public String getDescriptionCase() {
		return descriptionCase;
	}

	public void setDescriptionCase(String descriptionCase) {
		this.descriptionCase = descriptionCase;
	}

	public String getStatusCase() {
		return statusCase;
	}

	public void setStatusCase(String statusCase) {
		this.statusCase = statusCase;
	}

	private static final long serialVersionUID = 1L;
}
