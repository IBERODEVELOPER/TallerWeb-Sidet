package com.ibero.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.EntityEmployee;
import com.ibero.demo.entity.Schedule;
import com.ibero.demo.repository.IDayScheduleDao;
import com.ibero.demo.repository.IScheduleDao;

@Service
public class IScheduleServiceImpl implements IScheduleService {

	@Autowired
	private IScheduleDao schuduledao;
	

	@Override
	@Transactional
	public void saveschudule(Schedule schedule) {
		schuduledao.save(schedule);
	}

	@Override
	@Transactional(readOnly = true)
	public Schedule findOneSchudule(Integer id) {
		return schuduledao.findById(id).orElse(null);
	}

	@Override
	public void deleteId(Integer id) {
		schuduledao.deleteById(id);
	}

	@Override
	public Schedule findByEmployee(EntityEmployee employee) {
		return schuduledao.findByEmployee(employee);
	}

}
