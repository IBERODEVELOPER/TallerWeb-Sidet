package com.ibero.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibero.demo.dao.EventDao;
import com.ibero.demo.entity.Event;

@Service
public class EventServiceImpl implements EventService{

	@Autowired
	private EventDao eventado;
	
	@Override
	@Transactional(readOnly = true)
	public List<Event> findAllEvents() {
		return (List<Event>)eventado.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Event> allEvents(Pageable page) {
		return eventado.findAll(page);
	}

	@Override
	@Transactional
	public Event saveEvent(Event event) {
		return eventado.save(event);
	}

	@Override
	@Transactional(readOnly = true)
	public Event findEventById(Integer id) {
		return eventado.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteEventById(Integer id) {
		eventado.deleteById(id);
	}
}
