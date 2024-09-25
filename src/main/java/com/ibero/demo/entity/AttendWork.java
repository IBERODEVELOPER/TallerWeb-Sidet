package com.ibero.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.ibero.demo.util.DaysWeek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tardinessrecord")
public class AttendWork implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private DaysWeek day;

    private LocalTime scheduledEntryTime;
    
    private LocalTime actualEntryTime;
    
    private Long tardinessMinutes;

    private LocalDate date;
    
	@Column(name = "justifytard", length = 255)
	private String justifytard;
	
	private LocalTime exitTime; // Hora de salida del empleado

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public DaysWeek getDay() {
		return day;
	}

	public void setDay(DaysWeek day) {
		this.day = day;
	}

	public LocalTime getScheduledEntryTime() {
		return scheduledEntryTime;
	}

	public void setScheduledEntryTime(LocalTime scheduledEntryTime) {
		this.scheduledEntryTime = scheduledEntryTime;
	}

	public LocalTime getActualEntryTime() {
		return actualEntryTime;
	}

	public void setActualEntryTime(LocalTime actualEntryTime) {
		this.actualEntryTime = actualEntryTime;
	}

	public Long  getTardinessMinutes() {
		return tardinessMinutes;
	}

	public void setTardinessMinutes(Long  tardinessMinutes) {
		this.tardinessMinutes = tardinessMinutes;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
    
	public String getJustifytard() {
		return justifytard;
	}

	public void setJustifytard(String justifytard) {
		this.justifytard = justifytard;
	}

	public LocalTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalTime exitTime) {
		this.exitTime = exitTime;
	}

	private static final long serialVersionUID = 1L;
    
}
