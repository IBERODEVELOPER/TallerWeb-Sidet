package com.ibero.demo.entity;

import java.io.Serializable;

import com.ibero.demo.util.DaysWeek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "day_schedule")
public class DaySchedule implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_week")
    private DaysWeek dayWeek;

    @Column(name = "entry_time")
    private String 	entryTime;

    @Column(name = "leaving_work")
    private String leavWork;

    @Column(name = "hours_worked")
    private int hoursWorked;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private Schedule schedule;
    
    
    public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public DaysWeek getDayWeek() {
		return dayWeek;
	}


	public void setDayWeek(DaysWeek dayWeek) {
		this.dayWeek = dayWeek;
	}


	public String getEntryTime() {
		return entryTime;
	}


	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}


	public String getLeavWork() {
		return leavWork;
	}


	public void setLeavWork(String leavWork) {
		this.leavWork = leavWork;
	}


	public int getHoursWorked() {
		return hoursWorked;
	}


	public void setHoursWorked(int hoursWorked) {
		this.hoursWorked = hoursWorked;
	}


	public Schedule getSchedule() {
		return schedule;
	}


	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}


	private static final long serialVersionUID = 1L;
}
