package com.ibero.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IDayScheduleDao;
import com.ibero.demo.entity.DaySchedule;

@Service
public class IDayScheduleServiceImpl implements IDayScheduleService{


	@Autowired
	private IDayScheduleDao dayschuday;
	
	@Override
	@Transactional
	public void savesdaySchedule(DaySchedule daySchedule) {
		dayschuday.save(daySchedule);
	}

	@Override
	@Transactional(readOnly = true)
	public DaySchedule findOneSchuduleDay(Integer id) {
		return dayschuday.findById(id).orElse(null);}

	@Override
	@Transactional
	public void deleteId(Integer id) {
		dayschuday.deleteById(id);
	}

}
