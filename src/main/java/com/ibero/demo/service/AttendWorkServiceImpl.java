package com.ibero.demo.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.time.Duration;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.AttendWorkDao;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.AttendWork;
import com.ibero.demo.util.DaysWeek;

@Service
public class AttendWorkServiceImpl implements AttendWorkService{
	
	@Autowired
	private AttendWorkDao rectardao;
	
	@Override
	@Transactional
	public void savetarding(Employee employee, DaysWeek diaEnum, LocalTime horaEntrada, LocalTime horaActual) {
		AttendWork tardrec = new AttendWork();
		tardrec.setEmployee(employee);
		tardrec.setDay(diaEnum);
		tardrec.setScheduledEntryTime(horaEntrada);
		tardrec.setActualEntryTime(horaActual);
		// Calcular minutos de tardanza y asegurarse de que no sea negativo
	    long tardinessMinutes = Duration.between(horaEntrada, horaActual).toMinutes();
	    if (tardinessMinutes < 0) {
	        tardinessMinutes = 0; // Si es negativo, asignar 0
	    }
		tardrec.setTardinessMinutes(tardinessMinutes);
		tardrec.setDate(LocalDate.now());//registrar la hora actual
		rectardao.save(tardrec);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmployeeAndDate(Employee employee, LocalDate date) {
		return rectardao.existsByEmployeeAndDate(employee, date);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AttendWork> findAllTardingreport(Pageable page) {
		return rectardao.findAll(page);
	}

	@Override
	@Transactional(readOnly = true)
	public AttendWork findbyIdtardinesRecord(Integer id) {
		return rectardao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void saveTarding(AttendWork tardingrecord) {
		rectardao.save(tardingrecord);
	}

	@Override
	@Transactional
	public void deleteAsistenciaById(Integer id) {
		rectardao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AttendWork> findTardinessByEmployee(Employee employee, Pageable pageable) {
	    return rectardao.findByEmployee(employee, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendWork> findByEmployee(Employee employee) {
		return rectardao.findByEmployee(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AttendWork> findTardinessByEmployeeAndMonth(Employee employee, int month, Pageable pageable) {
		return rectardao.findByEmployeeAndMonth(employee, month, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendWork> findTardinessByEmployeeAndMonth(Employee employee, int month) {
		return rectardao.findByEmployeeAndMonthL(employee, month);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendWork> findAllReports() {
		return (List<AttendWork>) rectardao.findAll();
	}

	@Override
	public Optional<AttendWork> findByEmployeeAndDate(Employee employee, LocalDate date) {
		return rectardao.findByEmployeeAndDate(employee, date);
	}
}
