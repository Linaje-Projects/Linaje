/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import linaje.LocalizedStrings;

/**
 * Clase para manejar las fechas de un componente calendario y movernos por las diferentes fechas
 * Nos facilita los nombres de los días y meses
 * Nos da los días del mes de una año y nos facilita los incrementos típicos de calendarios de sumar o restar un mes o un año
 * Permite filtrar fechas con addDate y removeDate de forma que luego podamos por ejemplo resaltar o habilitar esas fechas en el calendario
 * 
 * @see linaje.gui.components.Calendar
 *  
 **/
@SuppressWarnings("serial")
public class CalendarDates extends HashMap<Integer, HashMap<Integer, List<Integer>>> {

	//Meses y días de la semana (una letra)
	public static final int[] DAYS_OF_WEEK_MONDAY_TO_SUNDAY = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY}; 
	public static final int[] DAYS_OF_WEEK_SUNDAY_TO_SATURDAY = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY}; 
	public static final int[] MONTHS = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER}; 
	
	private static HashMap<Integer, String> daysOfWeekLettersMap = null;
	private static HashMap<Integer, String> daysOfWeekNamesMap = null;
	private static HashMap<Integer, String> daysOfWeekNamesShortMap = null;
	private static HashMap<Integer, String> monthNamesMap = null;
	private static HashMap<Integer, String> monthNamesShortMap = null;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String mondayLetter;
		public String tuesdayLetter;
		public String wednesdayLetter;
		public String thursdayLetter;
		public String fridayLetter;
		public String saturdayLetter;
		public String sundayLetter;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static int[] getDaysOfWeek(Calendar calendar) {
		
		if (calendar == null)
			calendar = Calendar.getInstance();
		
		return calendar.getFirstDayOfWeek() == Calendar.MONDAY ? DAYS_OF_WEEK_MONDAY_TO_SUNDAY : DAYS_OF_WEEK_SUNDAY_TO_SATURDAY;
	}
	
	public static String[] getDaysOfWeekLetters(Calendar calendar) {
		
		if (calendar == null)
			calendar = Calendar.getInstance();
		
		int[] daysOfWeek = getDaysOfWeek(calendar);
		String[] daysOfWeekLetters = new String[daysOfWeek.length];
		for (int i = 0; i < daysOfWeekLetters.length; i++) {
			daysOfWeekLetters[i] = getDayOfWeekLetter(daysOfWeek[i]);
		}
		return daysOfWeekLetters;
	}
	
	public static String[] getDaysOfWeekNames(Calendar calendar) {
		
		if (calendar == null)
			calendar = Calendar.getInstance();
		
		int[] daysOfWeek = getDaysOfWeek(calendar);
		String[] daysOfWeekNames = new String[daysOfWeek.length];
		for (int i = 0; i < daysOfWeekNames.length; i++) {
			daysOfWeekNames[i] = getDayOfWeekName(daysOfWeek[i]);
		}
		return daysOfWeekNames;
	}
	public static String[] getDaysOfWeekNamesShort(Calendar calendar) {
		
		if (calendar == null)
			calendar = Calendar.getInstance();
		
		int[] daysOfWeek = getDaysOfWeek(calendar);
		String[] daysOfWeekNamesShort = new String[daysOfWeek.length];
		for (int i = 0; i < daysOfWeekNamesShort.length; i++) {
			daysOfWeekNamesShort[i] = getDayOfWeekNameShort(daysOfWeek[i]);
		}
		return daysOfWeekNamesShort;
	}
	
	public static String[] getMonthNames() {
		
		String[] monthNames = new String[MONTHS.length];
		for (int i = 0; i < MONTHS.length; i++) {
			monthNames[i] = getMonthName(MONTHS[i]);
		}
		return monthNames;
	}
	public static String[] getMonthNamesShort() {
		
		String[] monthNamesShort = new String[MONTHS.length];
		for (int i = 0; i < MONTHS.length; i++) {
			monthNamesShort[i] = getMonthNameShort(MONTHS[i]);
		}
		return monthNamesShort;
	}
	
	public static String getDayOfWeekLetter(Calendar calendar) {
		
		if (calendar == null)
			calendar = Calendar.getInstance();
		
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		return getDayOfWeekLetter(dayOfWeek);
	}
	
	public static String getDayOfWeekLetter(int dayOfWeek) {
		return getDaysOfWeekLettersMap().get(dayOfWeek);
	}
	public static String getDayOfWeekName(int dayOfWeek) {
		return getDaysOfWeekNamesMap().get(dayOfWeek);
	}
	public static String getDayOfWeekNameShort(int dayOfWeek) {
		return getDaysOfWeekNamesShortMap().get(dayOfWeek);
	}
	public static String getMonthName(int month) {
		return getMonthNamesMap().get(month);
	}
	public static String getMonthNameShort(int month) {
		return getMonthNamesShortMap().get(month);
	}
		
	public static HashMap<Integer, String> getDaysOfWeekLettersMap() {
		if (daysOfWeekLettersMap == null) {
			
			daysOfWeekLettersMap = new LinkedHashMap<Integer, String>();
			
			daysOfWeekLettersMap.put(Calendar.MONDAY, TEXTS.mondayLetter);
			daysOfWeekLettersMap.put(Calendar.TUESDAY, TEXTS.tuesdayLetter);
			daysOfWeekLettersMap.put(Calendar.WEDNESDAY, TEXTS.wednesdayLetter);
			daysOfWeekLettersMap.put(Calendar.THURSDAY, TEXTS.thursdayLetter);
			daysOfWeekLettersMap.put(Calendar.FRIDAY, TEXTS.fridayLetter);
			daysOfWeekLettersMap.put(Calendar.SATURDAY, TEXTS.saturdayLetter);
			daysOfWeekLettersMap.put(Calendar.SUNDAY, TEXTS.sundayLetter);
		}
		return daysOfWeekLettersMap;
	}
	public static HashMap<Integer, String> getDaysOfWeekNamesMap() {
		if (daysOfWeekNamesMap == null) {
			
			daysOfWeekNamesMap = new LinkedHashMap<Integer, String>();
			Calendar cal = Dates.getCalendarInstance(null);
			final String FORMAT_DAY_OF_WEEK = "EEEE";
			int[] daysOfWeek = getDaysOfWeek(cal);
			for (int i = 0; i < daysOfWeek.length; i++) {
				int day = daysOfWeek[i];
				cal.set(Calendar.DAY_OF_WEEK, day);
				String dayName = Dates.getFormattedDate(cal.getTime(), FORMAT_DAY_OF_WEEK);
				daysOfWeekNamesMap.put(day, Strings.capitalize(dayName));
			}
		}
		return daysOfWeekNamesMap;
	}
	public static HashMap<Integer, String> getDaysOfWeekNamesShortMap() {
		if (daysOfWeekNamesShortMap == null) {
			
			daysOfWeekNamesShortMap = new LinkedHashMap<Integer, String>();
			Calendar cal = Dates.getCalendarInstance(null);
			final String FORMAT_DAY_OF_WEEK_SHORT = "EEE";
			int[] daysOfWeek = getDaysOfWeek(cal);
			for (int i = 0; i < daysOfWeek.length; i++) {
				int day = daysOfWeek[i];
				cal.set(Calendar.DAY_OF_WEEK, day);
				String dayName = Dates.getFormattedDate(cal.getTime(), FORMAT_DAY_OF_WEEK_SHORT);
				daysOfWeekNamesShortMap.put(day, Strings.capitalize(dayName));
			}
		}
		return daysOfWeekNamesShortMap;
	}
	public static HashMap<Integer, String> getMonthNamesShortMap() {
		if (monthNamesShortMap == null) {
			
			monthNamesShortMap = new LinkedHashMap<Integer, String>();
			Calendar cal = Dates.getCalendarInstance(null);
			final String FORMAT_MONTH_SHORT = "MMM";
			for (int i = 0; i < MONTHS.length; i++) {
				int month = MONTHS[i];
				cal.set(Calendar.MONTH, month);
				String monthName = Dates.getFormattedDate(cal.getTime(), FORMAT_MONTH_SHORT);
				monthNamesShortMap.put(month, Strings.capitalize(monthName));
			}
		}
		return monthNamesShortMap;
	}
	public static HashMap<Integer, String> getMonthNamesMap() {
		if (monthNamesMap == null) {
			
			monthNamesMap = new LinkedHashMap<Integer, String>();
			Calendar cal = Dates.getCalendarInstance(null);
			final String FORMAT_MONTH = "MMMM";
			for (int i = 0; i < MONTHS.length; i++) {
				int month = MONTHS[i];
				cal.set(Calendar.MONTH, month);
				String monthName = Dates.getFormattedDate(cal.getTime(), FORMAT_MONTH);
				monthNamesMap.put(month, Strings.capitalize(monthName));
			}
		}
		return monthNamesMap;
	}
	
	public CalendarDates() {
		super();
	}
	public CalendarDates(List<Date> dates) {
		super();
		setDates(dates);
	}
	
	public boolean addDate(Date date) {
		if (date != null) {
			Calendar cal = Dates.getCalendarInstance(date, true);
			return addDate(cal.get(Calendar.DATE), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}
		return false;
	}
	public boolean addDate(int day, int month, int year) {
		
		boolean addedDate = true;
		
		HashMap<Integer, List<Integer>> daysMonthMap = get(year);
		if (daysMonthMap != null) {
			List<Integer> days = daysMonthMap.get(month);
			if (days != null) {
				if (!days.contains(day))
					days.add(day);
				else
					addedDate = false;
			}
			else {
				days = Lists.newList(day);
				daysMonthMap.put(month, days);
			}
		}
		else {
			
			daysMonthMap = new HashMap<Integer, List<Integer>>();
			List<Integer> days = Lists.newList(day);
			daysMonthMap.put(month, days);
			put(year, daysMonthMap);
		}
		
		return addedDate;
	}
	
	public boolean removeDate(Date date) {
		if (date != null) {
			Calendar cal = Dates.getCalendarInstance(date, true);
			return removeDate(cal.get(Calendar.DATE), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}
		return false;
	}
	public boolean removeDate(int day, int month, int year) {
		
		boolean removedDate = false;
		HashMap<Integer, List<Integer>> daysMonthMap = get(year);
		if (daysMonthMap != null) {
			List<Integer> days = daysMonthMap.get(month);
			if (days != null) {
				if (days.contains(day)) {
					days.remove(day);
					removedDate = true;
				}
			}
		}
		return removedDate;
	}
	
	public boolean containsDate(Date date) {
		if (date != null) {
			Calendar cal = Dates.getCalendarInstance(date, true);
			return containsDate(cal.get(Calendar.DATE), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}
		return false;
	}
	public boolean containsDate(int day, int month, int year) {
		
		boolean containsDate = false;
		HashMap<Integer, List<Integer>> daysMonthMap = get(year);
		if (daysMonthMap != null) {
			List<Integer> days = daysMonthMap.get(month);
			if (days != null) {
				if (days.contains(day)) {
					containsDate = true;
				}
			}
		}
		return containsDate;
	}
	
	public void setDates(List<Date> dates) {
		clear();
		if (dates != null) {
			for (int i = 0; i < dates.size(); i++) {
				addDate(dates.get(i));
			}
		}
	}
	
	public List<Integer> getDays(int year, int month) {
		
		List<Integer> days = null;
		HashMap<Integer, List<Integer>> daysMonthMap = get(year);
		if (daysMonthMap != null) 
			days = daysMonthMap.get(month);
		
		if (days == null)
			days = Lists.newList();
		
		Lists.sort(days);
		
		return days;
	}
	
	public List<Integer> getMonths(int year) {
		
		List<Integer> months = Lists.newList();
		HashMap<Integer, List<Integer>> daysMonthMap = get(year);
		
		if (daysMonthMap != null) {
			for (Integer month : daysMonthMap.keySet()) {
				months.add(month);
			}
		}
		
		Lists.sort(months);
		
		return months;
	}
	
	public List<Integer> getYears() {
		
		List<Integer> years = Lists.newList();
		for (Integer year : this.keySet()) {
			years.add(year);
		}
		
		Lists.sort(years);
		
		return years;
	}
	
	public Date getFirstDate() {
		
		List<Integer> years = getYears();
		int year = Lists.getFirstElement(years);
		List<Integer> months = getMonths(year);
		int month = Lists.getFirstElement(months);
		List<Integer> days = getDays(year, month);
		int day = Lists.getFirstElement(days);
		
		return Dates.getCalendarInstance(day, month, year).getTime();
	}
	
	public Date getLastDate() {
		
		List<Integer> years = getYears();
		int year = Lists.getLastElement(years);
		List<Integer> months = getMonths(year);
		int month = Lists.getLastElement(months);
		List<Integer> days = getDays(year, month);
		int day = Lists.getLastElement(days);
		
		return Dates.getCalendarInstance(day, month, year).getTime();
	}
	
	public Date getNextDate(Date currentDate) {
		
		Date nextDate = null;
		
		if (currentDate == null || !containsDate(currentDate)) {
			nextDate = getFirstDate();
		}
		else {
			
			Calendar cal = Dates.getCalendarInstance(currentDate);
			int currentYear = cal.get(Calendar.YEAR);
			int currentMonth = cal.get(Calendar.MONTH);
			int currentDay = cal.get(Calendar.DATE);
			
			List<Integer> years = getYears();
			int indexYear = years.indexOf(currentYear);
			int year = years.get(indexYear);
			
			List<Integer> months = getMonths(year);
			int indexMonth = months.indexOf(currentMonth);
			int month = months.get(indexMonth);
			
			List<Integer> days = getDays(year, month);
			int indexDay = days.indexOf(currentDay);
			int day = days.get(indexDay);
			
			if (indexDay < days.size()-1) {
				indexDay++;
				day = days.get(indexDay);
			}
			else {
				
				if (indexMonth < months.size()-1) {
					indexMonth++;
					month = months.get(indexMonth);
				}
				else {
					if (indexYear < years.size()-1)
						indexYear++;
					else
						indexYear = 0;
					year = years.get(indexYear);
					months = getMonths(year);
					month = Lists.getFirstElement(months);	
				}
				days = getDays(year, month);
				day = Lists.getFirstElement(days);
			}
			
			nextDate = Dates.getCalendarInstance(day, month, year).getTime();
		}
		return nextDate;
	}
	
	public Date getPreviousDate(Date currentDate) {
		
		Date previousDate = null;
		
		if (currentDate == null || !containsDate(currentDate)) {
			previousDate = getLastDate();
		}
		else {
			
			Calendar cal = Dates.getCalendarInstance(currentDate);
			int currentYear = cal.get(Calendar.YEAR);
			int currentMonth = cal.get(Calendar.MONTH);
			int currentDay = cal.get(Calendar.DATE);
			
			List<Integer> years = getYears();
			int indexYear = years.indexOf(currentYear);
			int year = years.get(indexYear);
			
			List<Integer> months = getMonths(year);
			int indexMonth = months.indexOf(currentMonth);
			int month = months.get(indexMonth);
			
			List<Integer> days = getDays(year, month);
			int indexDay = days.indexOf(currentDay);
			int day = days.get(indexDay);
			
			if (indexDay > 0) {
				indexDay--;
				day = days.get(indexDay);
			}
			else {
				
				if (indexMonth > 0) {
					indexMonth--;
					month = months.get(indexMonth);
				}
				else {
					if (indexYear > 0)
						indexYear--;
					else
						indexYear = years.size()-1;
					year = years.get(indexYear);
					months = getMonths(year);
					month = Lists.getLastElement(months);	
				}
				days = getDays(year, month);
				day = Lists.getLastElement(days);
			}
			
			previousDate = Dates.getCalendarInstance(day, month, year).getTime();
		}
		return previousDate;
	}
}
