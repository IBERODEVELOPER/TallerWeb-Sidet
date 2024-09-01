package com.ibero.demo.dao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.TardinessRecord;

public interface TardinessRecordDao extends CrudRepository<TardinessRecord, Integer>, PagingAndSortingRepository<TardinessRecord, Integer>{

	// Definimos el método para buscar por empleado
    List<TardinessRecord> findByEmployee(Employee employee);
	Page<TardinessRecord> findByEmployee(Employee employee, Pageable pageable);
	Optional<TardinessRecord> findByEmployeeAndDate(Employee employee, LocalDate date);
	boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
}
