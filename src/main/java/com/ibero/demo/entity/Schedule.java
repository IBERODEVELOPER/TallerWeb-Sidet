package com.ibero.demo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ibero.demo.util.DaysWeek;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")
public class Schedule implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DaySchedule> days;
    
    public Schedule() {
        this.days = new ArrayList<>();
    }
    
    public void addDay(DaySchedule day) {
    	day.setSchedule(this);
        this.days.add(day);
    }
    
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

	public List<DaySchedule> getDays() {
		return days;
	}

	public void setDays(List<DaySchedule> days) {
		this.days = days;
	}

	private static final long serialVersionUID = 1L;
}
