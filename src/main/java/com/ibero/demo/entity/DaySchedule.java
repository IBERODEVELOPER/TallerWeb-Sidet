package com.ibero.demo.entity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "dayschedules")
public class DaySchedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "day_week")
	private DaysWeek  dayWeek;

	@NotEmpty
	@Column(name = "entry_time")
	private String entryTime;

	@NotEmpty
	@Column(name = "leaving_work")
	private String leavWork;

	@Column(name = "hours_worked")
	private int hoursWorked;
	
	// Gestionar tiempo de trabajo inicio hasta fin salida y que calcule el timpo de
		// trabajo
		@PreUpdate
		@PrePersist
		public void preUpdate() {
			calculateHoursWorked();
		}
		
		private void calculateHoursWorked() {
		    if (this.entryTime != null && this.leavWork != null) {
		    	// Formato para hora en 24 horas
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		        
		        // Parsear el tiempo de entrada y salida usando el formato de 24 horas
		        LocalTime start = LocalTime.parse(this.entryTime, formatter);
		        LocalTime end = LocalTime.parse(this.leavWork, formatter);
		        
		     // Calcular la diferencia en horas
		        long hours = ChronoUnit.HOURS.between(start, end);
		        
		        // Manejo de casos donde la hora de salida es al día siguiente
		        if (hours < 0) {
		            hours += 24;
		        }

		        this.hoursWorked = (int) hours;
		    }
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Schedule getSchedule() {
			return schedule;
		}

		public void setSchedule(Schedule schedule) {
			this.schedule = schedule;
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
		
}
