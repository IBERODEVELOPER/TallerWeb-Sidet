package com.ibero.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.ActivityDao;
import com.ibero.demo.entity.ActivityEntity;
import com.ibero.demo.entity.Event;

@Service
public class ActivityServiceImpl implements ActivityService{

	@Autowired
	private ActivityDao activitydao;
	
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityEntity> findActivityByUsernameAndEndTimeIsNull(String username) {
		return activitydao.findOngoingActivityByEventAndUser(username);
	}

	@Override
	@Transactional
	public ActivityEntity saveActivity(ActivityEntity activity) {
		return activitydao.save(activity);
	}

	@Override
	@Transactional(readOnly = true)
	public ActivityEntity findActivityById(Integer id) {
		return activitydao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityEntity> findByEndTimeIsNull() {
		return activitydao.findByEndTimeIsNull();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityEntity> findActivitiesBetweenDates(String startDate, String endDate) {
		return activitydao.findActivitiesBetweenDatesNative(startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityEntity> findActivitiesBetweenDatesAndEventId(String startDate, String endDate,
			Integer eventId) {
		return activitydao.findActivitiesBetweenDatesAndEventId(startDate, endDate, eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityEntity> findByUserAndDate(String username, LocalDate endTime) {
		return activitydao.findByUserAndDate(username, endTime);
	}

}
