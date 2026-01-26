package com.ibero.demo.util;

import java.util.Calendar;

public enum DaysWeek {
	Lunes,Martes, Miercoles, Jueves,Viernes,Sabado, Domingo;
	
	public static DaysWeek fromCalendarDay(int calendarDay) {
        switch (calendarDay) {
	        case Calendar.MONDAY:
	            return Lunes;
            case Calendar.TUESDAY:
                return Martes;
            case Calendar.WEDNESDAY:
                return Miercoles;
            case Calendar.THURSDAY:
                return Jueves;
            case Calendar.FRIDAY:
                return Viernes;
            case Calendar.SATURDAY:
                return Sabado;
            case Calendar.SUNDAY:
                return Domingo;
            default:
                throw new IllegalArgumentException("Día invalido: " + calendarDay);
        }
    }

}
