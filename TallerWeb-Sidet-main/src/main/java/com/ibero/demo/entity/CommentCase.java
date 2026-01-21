package com.ibero.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name = "comments")
public class CommentCase implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(length = 10)
	private LocalDateTime dateComment;
	
	@Column(length = 500)
	private String Comment;
	
	@Column(length = 10)
	private String statusComment;
	
	@ManyToOne
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    private Case cas;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity commentedBy;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getDateComment() {
		return dateComment;
	}

	public void setDateComment(LocalDateTime dateComment) {
		this.dateComment = dateComment;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public Case getCas() {
		return cas;
	}

	public void setCas(Case cas) {
		this.cas = cas;
	}

	public UserEntity getCommentedBy() {
		return commentedBy;
	}

	public void setCommentedBy(UserEntity commentedBy) {
		this.commentedBy = commentedBy;
	}

	public String getStatusComment() {
		return statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	private static final long serialVersionUID = 1L;

}
