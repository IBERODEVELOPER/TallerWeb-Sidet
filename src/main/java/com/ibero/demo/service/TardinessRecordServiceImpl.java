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

import com.ibero.demo.dao.TardinessRecordDao;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.TardinessRecord;
import com.ibero.demo.util.DaysWeek;

@Service
public class TardinessRecordServiceImpl implements TardinessRecordService{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TardinessRecordDao rectardao;
	
	@Override
	@Transactional
	public void savetarding(Employee employee, DaysWeek diaEnum, LocalTime horaEntrada, LocalTime horaActual) {
		TardinessRecord tardrec = new TardinessRecord();
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
	public Page<TardinessRecord> findAllTardingreport(Pageable page) {
		return rectardao.findAll(page);
	}

	@Override
	@Transactional(readOnly = true)
	public TardinessRecord findbyIdtardinesRecord(Integer id) {
		return rectardao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void saveTarding(TardinessRecord tardingrecord) {
		rectardao.save(tardingrecord);
	}

	@Override
	@Transactional
	public void deleteAsistenciaById(Integer id) {
		rectardao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TardinessRecord> findTardinessByEmployee(Employee employee, Pageable pageable) {
	    return rectardao.findByEmployee(employee, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TardinessRecord> findByEmployee(Employee employee) {
		return rectardao.findByEmployee(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TardinessRecord> findTardinessByEmployeeAndMonth(Employee employee, int month, Pageable pageable) {
		return rectardao.findByEmployeeAndMonth(employee, month, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TardinessRecord> findTardinessByEmployeeAndMonth(Employee employee, int month) {
		return rectardao.findByEmployeeAndMonthL(employee, month);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TardinessRecord> findAllReports() {
		return (List<TardinessRecord>) rectardao.findAll();
	}

}
