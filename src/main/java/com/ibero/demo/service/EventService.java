package com.ibero.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibero.demo.entity.Event;

public interface EventService {
		/*Metodo para listar las eventos registrado en el Sistema*/
		public List<Event> findAllEvents();
		/*Metodo para listar las eventos registrado en el Sistema con paginación*/
		public Page<Event> allEvents(Pageable page);
		/*Metodo para guardar los datos del formulario Persona*/
		public Event saveEvent(Event event);
		/*Metodo para Obtener datos de una Persona por si ID*/
		public Event findEventById(Integer id);
		/*Metodo para eliminar Persona por su ID*/
		public void deleteEventById(Integer id);


}
