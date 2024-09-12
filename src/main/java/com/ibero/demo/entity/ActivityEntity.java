package com.ibero.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activities")
public class ActivityEntity implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ID de la actividad administrativa
	
    private String username; // Nombre del usuario que inicia la actividad
    
    @ManyToOne
    @JoinColumn(name = "event_id") // Foreign key column
    private Event event; // Relación con la entidad Event
    
    private String startTime; // Fecha y hora de inicio
    private String endTime; // Fecha y hora de finalización
    private long totalTime; // Tiempo total transcurrido en formato "HH:mm:ss"
    private String detailsevent;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
    
    public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	public String getDetailsevent() {
		return detailsevent;
	}

	public void setDetailsevent(String detailsevent) {
		this.detailsevent = detailsevent;
	}


	private static final long serialVersionUID = 1L;
}

