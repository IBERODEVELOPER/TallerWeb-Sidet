package com.ibero.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.IScheduleDao;
import com.ibero.demo.dao.IDayScheduleDao;
import com.ibero.demo.entity.DaySchedule;
import com.ibero.demo.entity.Employee;
import com.ibero.demo.entity.Schedule;

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
	public Schedule findByEmployee(Employee employee) {
		return schuduledao.findByEmployee(employee);
	}

}
