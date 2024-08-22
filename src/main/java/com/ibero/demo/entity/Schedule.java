package com.ibero.demo.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedules")
public class Schedule implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "peoples_id", referencedColumnName = "id")
	private Employee employee;
	
	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<DaySchedule> daySchedules;
	
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
	
	public List<DaySchedule> getDaySchedules() {
		return daySchedules;
	}

	public void setDaySchedules(List<DaySchedule> daySchedules) {
		this.daySchedules = daySchedules;
	}
	
	private static final long serialVersionUID = 1L;
}
