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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.*;

import linaje.LocalizedStrings;
import linaje.statics.Constants;

/**
 * 
 * y   = year   (yy or yyyy)
 * M   = month  (MM)
 * d   = day in month (dd)
 * h   = hour (0-12)  (hh)
 * H   = hour (0-23)  (HH)
 * m   = minute in hour (mm)
 * s   = seconds (ss)
 * S   = milliseconds (SSS)
 * E   = day of Week
 * z   = time zone  text        (e.g. Pacific Standard Time...)
 * Z   = time zone, time offset (e.g. -0800)
 * 
 * */
public final class Dates {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String periodDaily;
		public String periodWeekly;
		public String periodMonthly;
		public String periodBiMonthly;
		public String periodQuarterly;
		public String periodEveryFourMonths;
		public String periodSemiannual;
		public String periodAnnual;
		public String periodBiannual;
		
		public String periodDailyElement;
		public String periodWeeklyElement;
		public String periodMonthlyElement;
		public String periodBiMonthlyElement;
		public String periodQuarterlyElement;
		public String periodEveryFourMonthsElement;
		public String periodSemiannualElement;
		public String periodAnnualElement;
		public String periodBiannualElement;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final int PERIOD_DAILY = 0;
	public static final int PERIOD_WEEKLY = 1;
	public static final int PERIOD_MONTHLY = 2;
	public static final int PERIOD_BIMONTHLY = 3;
	public static final int PERIOD_QUARTERLY = 4;
	public static final int PERIOD_EVERY_FOUR_MONTHS = 5;
	public static final int PERIOD_SEMIANNUAL = 6;
	public static final int PERIOD_ANNUAL = 7;
	public static final int PERIOD_BIANNUAL = 8;
	
	public static final String PERIOD_DAILY_NAME = TEXTS.periodDaily;
	public static final String PERIOD_WEEKLY_NAME = TEXTS.periodWeekly;
	public static final String PERIOD_MONTHLY_NAME = TEXTS.periodMonthly;
	public static final String PERIOD_BIMONTHLY_NAME = TEXTS.periodBiMonthly;
	public static final String PERIOD_QUARTERLY_NAME = TEXTS.periodQuarterly;
	public static final String PERIOD_EVERY_FOUR_MONTHS_NAME = TEXTS.periodEveryFourMonths;
	public static final String PERIOD_SEMIANNUAL_NAME = TEXTS.periodSemiannual;
	public static final String PERIOD_ANNUAL_NAME = TEXTS.periodAnnual;
	public static final String PERIOD_BIANNUAL_NAME = TEXTS.periodBiannual;
	
	public static final String PERIOD_DAILY_NAME_ELEMENT = TEXTS.periodDailyElement;
	public static final String PERIOD_WEEKLY_NAME_ELEMENT = TEXTS.periodWeeklyElement;
	public static final String PERIOD_MONTHLY_NAME_ELEMENT = TEXTS.periodMonthlyElement;
	public static final String PERIOD_BIMONTHLY_NAME_ELEMENT = TEXTS.periodBiMonthlyElement;
	public static final String PERIOD_QUARTERLY_NAME_ELEMENT = TEXTS.periodQuarterlyElement;
	public static final String PERIOD_EVERY_FOUR_MONTHS_NAME_ELEMENT = TEXTS.periodEveryFourMonthsElement;
	public static final String PERIOD_SEMIANNUAL_NAME_ELEMENT = TEXTS.periodSemiannualElement;
	public static final String PERIOD_ANNUAL_NAME_ELEMENT = TEXTS.periodAnnualElement;
	public static final String PERIOD_BIANNUAL_NAME_ELEMENT = TEXTS.periodBiannualElement;
	
	public static final int[] PERIODS = {PERIOD_DAILY,
										PERIOD_WEEKLY,
										PERIOD_MONTHLY,
										PERIOD_BIMONTHLY,
										PERIOD_QUARTERLY,
										PERIOD_EVERY_FOUR_MONTHS,
										PERIOD_SEMIANNUAL,
										PERIOD_ANNUAL,
										PERIOD_BIANNUAL};
	
	public static final String[] PERIOD_NAMES = {PERIOD_DAILY_NAME,
												PERIOD_WEEKLY_NAME,
												PERIOD_MONTHLY_NAME,
												PERIOD_BIMONTHLY_NAME,
												PERIOD_QUARTERLY_NAME,
												PERIOD_EVERY_FOUR_MONTHS_NAME,
												PERIOD_SEMIANNUAL_NAME,
												PERIOD_ANNUAL_NAME,
												PERIOD_BIANNUAL_NAME};
	
	public static final String[] PERIOD_ELEMENT_NAMES = {PERIOD_DAILY_NAME_ELEMENT,
														PERIOD_WEEKLY_NAME_ELEMENT,
														PERIOD_MONTHLY_NAME_ELEMENT,
														PERIOD_BIMONTHLY_NAME_ELEMENT,
														PERIOD_QUARTERLY_NAME_ELEMENT,
														PERIOD_EVERY_FOUR_MONTHS_NAME_ELEMENT,
														PERIOD_SEMIANNUAL_NAME_ELEMENT,
														PERIOD_ANNUAL_NAME_ELEMENT,
														PERIOD_BIANNUAL_NAME_ELEMENT};
	
	public static final String FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
	public static final String FORMAT_DD_MMM_YYYY = "dd/MMM/yyyy";
	public static final String FORMAT_DD_MMMM_YYYY = "dd MMMM yyyy";
	public static final String FORMAT_EEEE_DD_MMMM_YYYY = "EEEE, " + FORMAT_DD_MMMM_YYYY;
	public static final String FORMAT_HH_MM = "HH:mm";
	public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
	public static final String FORMAT_YYYY = "yyyy";
	public static final String FORMAT_DD_MM_YYYY_HH_MM_SS = FORMAT_DD_MM_YYYY + " " + FORMAT_HH_MM_SS;
	public static final String FORMAT_MM_YYYY = "MM/yyyy";
	public static final String FORMAT_MMM_YYYY = "MMM yyyy";
	public static final String FORMAT_MMMM_YYYY = "MMMM yyyy";
	public static final String FORMAT_JAVA_DATE = "EEE MMM d h:mm:ss z yyyy";
	
	public static final String FORMAT_DEFAULT = FORMAT_DD_MM_YYYY;
	
	public static final String[] FORMATS_SEARCH_POSIBLE_DATE = {FORMAT_DD_MM_YYYY,
																FORMAT_DD_MMM_YYYY,
																FORMAT_DD_MMMM_YYYY,
																FORMAT_MM_YYYY,
																FORMAT_MMM_YYYY,
																FORMAT_MMMM_YYYY,
																FORMAT_YYYY,
																FORMAT_EEEE_DD_MMMM_YYYY,
																FORMAT_DD_MM_YYYY_HH_MM_SS};
	
	public static final Date MIN_DATE = new GregorianCalendar(0001, 0, 1).getTime();
	public static final Date MAX_DATE = new GregorianCalendar(9999, 11, 31).getTime();

	//Zonas horarias
	public static final String TIME_ZONE_UTC  = "UTC";	//Coordinated Universal Time
	public static final String TIME_ZONE_GMT  = "GMT";	//Greenwich Mean Time			UTC
	public static final String TIME_ZONE_CET  = "CET";	//Central European Time			UTC + 1 hour
	public static final String TIME_ZONE_CEST = "CEST";	//Central European Summer Time	UTC + 2 hours
	public static final String TIME_ZONE_WET  = "WET";	//Western European Time			UTC
	public static final String TIME_ZONE_WEST = "WEST";	//Western European Summer Time	UTC + 1 hour
	public static final String TIME_ZONE_EET  = "EET";	//Eastern European Time			UTC + 2 hours
	public static final String TIME_ZONE_EEST = "EEST";	//Eastern European Summer Time	UTC + 3 hours
	public static final String TIME_ZONE_BST  = "BST";	//British Summer Time			UTC + 1 hour
	public static final String TIME_ZONE_IST  = "IST";	//Irish Summer Time				UTC + 1 hour	

	public static String getPeriodName(int period) {
		return PERIOD_NAMES[period];
	}
	public static String getPeriodElementName(int period) {
		return PERIOD_ELEMENT_NAMES[period];
	}

	public static GregorianCalendar getCalendarInstance(Date date) {
		return getCalendarInstance(date, false);
	}
	
	public static GregorianCalendar getCalendarInstance(Date date, boolean hourZero) {
		
		GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
		if (date != null)
			gc.setTime(date);
		
		if (hourZero) {
			initializeHour(gc);
		}
		
		return gc;
	}
	
	public static GregorianCalendar getCalendarInstance(int day, int month, int year) {
		
		GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
		gc.set(Calendar.DATE, day);
		gc.set(Calendar.MONTH, month);
		gc.set(Calendar.YEAR, year);
		
		initializeHour(gc);
		
		return gc;
	}
	
	public static void initializeHour(GregorianCalendar gc) {
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
	}
	
	public static Date getJavaDate(String formattedDate) {
		try {
			return new SimpleDateFormat(FORMAT_JAVA_DATE, Locale.US).parse(formattedDate);
		}
		catch (Exception e) {
			return null;
		}
	}
	public static Date getDate(String formattedDate) throws ParseException {
		return getDate(formattedDate, (String[]) null);
	}
	public static Date getDate(String formattedDate, String... format) throws ParseException {
		
		Date date = null;
		if (formattedDate != null) {
			if (format == null) {
				//Si no especificamos formato, primero miramos si es una fecha Java en formato String
				date = getJavaDate(formattedDate);
				if (date != null)
					return date;
				else //No es una fecha java, por lo que miramos si es una fecha de los típicos formatos de fecha
					format = FORMATS_SEARCH_POSIBLE_DATE;
			}
			for (int i = 0; i < format.length && date == null; i++) {
				try {
					date = new SimpleDateFormat(format[i]).parse(formattedDate);
				}catch (ParseException ex) {
				}
			}
		}
		return date;
	}
	
	public static String getFormattedDate(Date date) {
		return getFormattedDate(date, null);
	}
	public static String getFormattedDate(Date date, int period) {
		String format = getDateFormat(period);
		return getFormattedDate(date, format);
	}
	public static String getFormattedDate(Date date, String format) {
		return getFormattedDate(date, format, true);
	}
	public static String getFormattedDate(Date date, String format, boolean toUpperCase) {
		if (format == null)
			format = FORMAT_DEFAULT;
		String formattedDate = date != null ? new SimpleDateFormat(format).format(date) : Constants.VOID;
		return toUpperCase ? formattedDate.toUpperCase() : Strings.capitalize(formattedDate);
	}
	
	public static String getDateShortFormatted(Date date, int period) {

		String format = getDateFormatShort(period);
		return getFormattedDate(date, format);
	}
	public static String getDateLongFormatted(Date date, int period) {

		String format = getDateFormatLong(period);
		return getFormattedDate(date, format);
	}
	
	public static final String getDateFormat(int period) {
		if (period == PERIOD_DAILY || period == PERIOD_WEEKLY)
			return getDateFormatShort(period);
		else
			return getDateFormatLong(period);
	}
	public static final String getDateFormatShort(int period) {
		
		if (period == PERIOD_DAILY || period == PERIOD_WEEKLY)
			return FORMAT_DD_MMM_YYYY;
		else if (period == PERIOD_ANNUAL || period == PERIOD_BIANNUAL)
			return FORMAT_YYYY;
		else
			return FORMAT_MMM_YYYY;
	}
	public static final String getDateFormatLong(int period) {
		
		if (period == PERIOD_DAILY || period == PERIOD_WEEKLY)
			return FORMAT_DD_MMMM_YYYY;
		else if (period == PERIOD_ANNUAL || period == PERIOD_BIANNUAL)
			return FORMAT_YYYY;
		else
			return FORMAT_MMMM_YYYY;
	}
	
	/**
	 * <b>Descripción:</b><br>
	 * Creado por: Pablo Linaje (04/01/2006 17:00:18)
	 * 
	 * @return int
	 * @param operand1 java.util.Date
	 * @param operand2 java.util.Date
	 *
	 * Devuelve:
	 *		 0 si las fechas son iguales
	 *		 1 si operand1 es mayor que operand2
	 *		-1 si operand1 es menor que operand2
	 */
	public static int compare(Date operand1, Date operand2) {
	
		int resultado = compareIgnoringTimeOfDay(operand1, operand2);
		if (resultado == 0) {
			
			Calendar cal;
			int hour1, minute1, second1, hour2, minute2, second2;
	
			cal = Calendar.getInstance();
	
			cal.setTime(operand1);
			hour1 = cal.get(Calendar.HOUR_OF_DAY);
			minute1 = cal.get(Calendar.MINUTE);
			second1 = cal.get(Calendar.SECOND);
	
			cal.setTime(operand2);
			hour2 = cal.get(Calendar.HOUR_OF_DAY);
			minute2 = cal.get(Calendar.MINUTE);
			second2 = cal.get(Calendar.SECOND);
	
			if (hour1 > hour2) {
				return 1;
			}
			else if (hour1 < hour2) {
				return -1;
			}
			else {
	
				if (minute1 > minute2) {
					return 1;
				}
				else if (minute1 < minute2) {
					return -1;
				}
				else {
	
					if (second1 > second2) {
						return 1;
					}
					else if (second1 < second2) {
						return -1;
					}
					else
						return 0;
				}
			}
		}
		else return resultado;
	}
	/**
	 * <b>Descripción:</b><br>
	 * Creado por: Pablo Linaje (04/01/2006 17:00:18)
	 * 
	 * @return int
	 * @param operand1 java.util.Date
	 * @param operand2 java.util.Date
	 *
	 * Devuelve:
	 *		 0 si las fechas son iguales
	 *		 1 si operand1 es mayor que operand2
	 *		-1 si operand1 es menor que operand2
	 */
	public static int compareIgnoringTimeOfDay(Date operand1, Date operand2) {
	
		Calendar cal;
		int day1, month1, year1, day2, month2, year2;
		
		cal = Calendar.getInstance();
		
		cal.setTime(operand1);
		day1 = cal.get(Calendar.DATE);
		month1 = cal.get(Calendar.MONTH);
		year1 = cal.get(Calendar.YEAR);
	
		cal.setTime(operand2);
		day2 = cal.get(Calendar.DATE);
		month2 = cal.get(Calendar.MONTH);
		year2 = cal.get(Calendar.YEAR);
	
		if (year1 > year2) {
			return 1;
		}
		else if (year1 < year2) {
			return -1;
		}
		else {
	
			if (month1 > month2) {
				return 1;
			}
			else if (month1 < month2) {
				return -1;
			}
			else {
	
				if (day1 > day2) {
					return 1;
				}
				else if (day1 < day2) {
					return -1;
				}
				else return 0;
			}
		}	
	}
	
	/**
	 * Con este método nos aseguramos de que cuando cambiamos de año el 29 de febrero, seguiremos estando en el último día de febrero
	 *  y no en el 1 de marzo como pasaría haciendo Calendar.set(Calendar.YEAR, nuevoAno);
	 */
	public static boolean setYear(Calendar cal, int newYear) {
		
		boolean dayChanged = false;
		int year = cal != null ? cal.get(GregorianCalendar.YEAR) : newYear;
		if (newYear != year) {
			
			int day = cal.get(Calendar.DATE);
			cal.set(GregorianCalendar.YEAR, newYear);
			int newDay = cal.get(Calendar.DATE);
			
			//Excepciones: Si el dia inicial y final no coinciden es que la fecha inicial es el 29 de febrero y tendremos que recalcular la fecha
			if (day != newDay) {
				cal.set(GregorianCalendar.DATE, 28);
				cal.set(GregorianCalendar.MONTH, Calendar.FEBRUARY);
				dayChanged = true;				
			}
		}
		
		return dayChanged;
	}
	
	/**
	 * Con este método nos aseguramos de que cuando cambiamos a un mes con menos días que el anterior, seguiremos estando en el último día de mes
	 *  y no en el priemro del mes siguiente como pasaría haciendo Calendar.set(Calendar.MONTH, newMonth);
	 */
	public static boolean setMonth(Calendar cal, int newMonth) {
		
		boolean dayChanged = false;
		int month = cal != null ? cal.get(GregorianCalendar.MONTH) : newMonth;
		if (newMonth != month) {
			
			int day = cal.get(Calendar.DATE);
			cal.set(GregorianCalendar.MONTH, newMonth);
			int newDay = cal.get(Calendar.DATE);
			
			//Excepciones: Si el dia inicial y final no coinciden es que la fecha inicial es de un mes con mas días que el calculado y tendremos que recalcular la fecha
			if (day != newDay) {
				cal.set(GregorianCalendar.DATE, 1);
				cal.set(GregorianCalendar.MONTH, newMonth);
				cal.set(GregorianCalendar.DATE, cal.getActualMaximum(Calendar.DATE));
				dayChanged = true;
			}
		}
		return dayChanged;
	}
	
	public static Date calculaFechaAnterior(Date startDate, int period) {
		return calculateNextPeriodsDate(startDate, period, -1);
	}
	public static Date calculateNextPeriodsDate(Date startDate, int period, int periodsNumber) {
		
		if (startDate == null)
			startDate = new Date();
		
		if (periodsNumber == 0) {
			return startDate;
		}
		else {
			
			Calendar cal = getCalendarInstance(startDate);
			
			if (period == PERIOD_ANNUAL || period == PERIOD_BIANNUAL) {
				
				int year = cal.get(GregorianCalendar.YEAR);
				int newYear = period == PERIOD_ANNUAL ? year + periodsNumber : year + periodsNumber*2;
				setYear(cal, newYear);
			}
			else if (period == PERIOD_MONTHLY
					|| period == PERIOD_BIMONTHLY
					|| period == PERIOD_QUARTERLY
					|| period == PERIOD_EVERY_FOUR_MONTHS
					|| period == PERIOD_SEMIANNUAL) {
				
				int month = cal.get(GregorianCalendar.MONTH);
				int newMonth;
				if (period == PERIOD_SEMIANNUAL)
					newMonth = month + periodsNumber*6;
				else if (period == PERIOD_EVERY_FOUR_MONTHS)
					newMonth = month + periodsNumber*4;
				else if (period == PERIOD_QUARTERLY)
					newMonth = month + periodsNumber*3;
				else if (period == PERIOD_BIMONTHLY)
					newMonth = month + periodsNumber*2;
				else
					newMonth = month + periodsNumber;
				
				setMonth(cal, newMonth);
			}
			else if (period == PERIOD_WEEKLY) {
				//Si usamos WEEK_OF_YEAR no salen bien los calculos en alguna ocasiones
				int week = cal.get(GregorianCalendar.WEEK_OF_MONTH);
				int newWeek = week + periodsNumber;
				cal.set(GregorianCalendar.WEEK_OF_MONTH, newWeek);
			}
			else {
				int day = cal.get(GregorianCalendar.DATE);
				int newDay = day + periodsNumber;
				cal.set(GregorianCalendar.DATE, newDay);
			}
			
			return cal.getTime();
		}
	}
	
	public static Date calculateFirstDateOfPeriod(Date startDate, int period) {
		return calculateFirstLastDateOfPeriod(startDate, period, true);
	}
	public static Date calculateLastDateOfPeriod(Date startDate, int period) {
		return calculateFirstLastDateOfPeriod(startDate, period, false);
	}
	private static Date calculateFirstLastDateOfPeriod(Date startDate, int period, boolean useFirstPeriodDate) {
		
		GregorianCalendar calendar = getCalendarInstance(startDate, true);
		
		if (period != PERIOD_DAILY) {
			if (period == PERIOD_WEEKLY) {
				calendar.set(Calendar.DAY_OF_WEEK, useFirstPeriodDate ? calendar.getFirstDayOfWeek() : getLastDayOfWeek(calendar));
			}
			else {
				if (period == PERIOD_ANNUAL || period == PERIOD_BIANNUAL) {
					//Si la periodicidad es anual el mes de inicio será enero y el final será diciembre
					calendar.set(Calendar.MONTH, useFirstPeriodDate ? Calendar.JANUARY : Calendar.DECEMBER);
				}
				else {
					int month = calendar.get(Calendar.MONTH);
					int firstLastMonth;
					if (period == PERIOD_BIMONTHLY) {
						if (month < Calendar.MARCH)
							firstLastMonth = useFirstPeriodDate ? Calendar.JANUARY : Calendar.FEBRUARY;
						else if (month < Calendar.MAY)
							firstLastMonth = useFirstPeriodDate ? Calendar.MARCH : Calendar.APRIL;
						else if (month < Calendar.JULY)
							firstLastMonth = useFirstPeriodDate ? Calendar.MAY : Calendar.JUNE;
						else if (month < Calendar.SEPTEMBER)
							firstLastMonth = useFirstPeriodDate ? Calendar.JULY : Calendar.AUGUST;
						else if (month < Calendar.NOVEMBER)
							firstLastMonth = useFirstPeriodDate ? Calendar.SEPTEMBER : Calendar.OCTOBER;
						else 
							firstLastMonth = useFirstPeriodDate ? Calendar.NOVEMBER : Calendar.DECEMBER;
					}
					else if (period == PERIOD_QUARTERLY) {
						if (month < Calendar.APRIL)
							firstLastMonth = useFirstPeriodDate ? Calendar.JANUARY : Calendar.MARCH;
						else if (month < Calendar.JULY)
							firstLastMonth = useFirstPeriodDate ? Calendar.APRIL : Calendar.JUNE;
						else if (month < Calendar.OCTOBER)
							firstLastMonth = useFirstPeriodDate ? Calendar.JULY : Calendar.SEPTEMBER;
						else 
							firstLastMonth = useFirstPeriodDate ? Calendar.OCTOBER : Calendar.DECEMBER;
					}
					else if (period == PERIOD_EVERY_FOUR_MONTHS) {
						if (month < Calendar.MAY)
							firstLastMonth = useFirstPeriodDate ? Calendar.JANUARY : Calendar.APRIL;
						else if (month < Calendar.SEPTEMBER)
							firstLastMonth = useFirstPeriodDate ? Calendar.MAY : Calendar.AUGUST;
						else 
							firstLastMonth = useFirstPeriodDate ? Calendar.SEPTEMBER : Calendar.DECEMBER;
					}
					else if (period == PERIOD_SEMIANNUAL) {
						if (month < Calendar.JULY)
							firstLastMonth = useFirstPeriodDate ? Calendar.JANUARY : Calendar.JUNE;
						else 
							firstLastMonth = useFirstPeriodDate ? Calendar.JULY : Calendar.DECEMBER;
					}
					else {
						firstLastMonth = month;
					}
					
					calendar.set(Calendar.MONTH, firstLastMonth);
				}
				//Si la periodicidad no es diaria ni semanal ponemos como fecha de inicio el dia 1 de mes y como fecha fin el último día del mes
				calendar.set(Calendar.DATE, useFirstPeriodDate ? 1 : calendar.getActualMaximum(Calendar.DATE));
			}
		}
		
		return calendar.getTime();
	}
	
	public static int getLastDayOfWeek(Calendar calendar) {
		
		int firstDayOfWeek = calendar.getFirstDayOfWeek();
		int lastDayOfWeek = firstDayOfWeek == Calendar.MONDAY ? Calendar.SUNDAY : Calendar.SATURDAY;
		
		return lastDayOfWeek;	
	}
	
	public static ArrayList<Date> getLastDatesOfPeriod(Date startDate, int period, boolean includeCurrentDate, boolean useFirstPeriodDate, int numDates) {
		
		ArrayList<Date> lastDatesOfPeriod = new ArrayList<Date>();
		
		if (startDate == null)
			startDate = new Date();
		if (period == -1)
			period = PERIOD_MONTHLY;
		
		startDate = calculateFirstLastDateOfPeriod(startDate, period, useFirstPeriodDate);
		
		Date date = includeCurrentDate ? startDate : calculaFechaAnterior(startDate, period);
		if (numDates < 1)
			numDates = getDefaultNumDatesOfPeriod(date, period);
		
		if (numDates > 0) {
			
			lastDatesOfPeriod.add(date);
			numDates--;
		
			for (int i = 0; i < numDates; i++) {
				date = calculaFechaAnterior(date, period);
				lastDatesOfPeriod.add(date);
			}
		}
		
		return lastDatesOfPeriod;
	}
	
	private static int getDefaultNumDatesOfPeriod(Date startDate, int period) {
		
		if (startDate == null)
			startDate = new Date();
		
		Date endDate;
		if (period == PERIOD_DAILY || period == PERIOD_WEEKLY)
			endDate = calculaFechaAnterior(startDate, PERIOD_MONTHLY);//Hasta el mismo dia del mes anterior
		else if (period == PERIOD_ANNUAL || period == PERIOD_BIANNUAL)
			endDate = calculateNextPeriodsDate(startDate, PERIOD_ANNUAL, -4);//Hasta el mismo dia de 4 años antes
		else
			endDate = calculaFechaAnterior(startDate, PERIOD_ANNUAL);//Hasta el mismo dia del año anterior
		
		int numDates = (int) getDifference(startDate, endDate, period);
				
		//Si la periodicidad no es anual ni bienal, añadimos uno para devolver el mismo dia/semana/mes/etc. del mes/año anterior
		if (period != PERIOD_ANNUAL && period != PERIOD_BIANNUAL)
			numDates++;
		
		return numDates;
	}
	
	/**
	 * No calcularemos la diferencia exacta en tiempo, sino la diferencia respecto a cada unidad de tiempo
	 * Por ejemplo la diferencia en meses entre el 31/01/2019 y el 01/02/2019 será de un mes aunque solo haya un dia de diferencia
	 * De igual forma la diferencia en años entre el 31/12/2018 y el 01/01/2019 será de un año
	 * Inicializaremos las horas a cero para que no haya problemas con los días
	 */
	public static long getDifference(Date startDate, Date endDate, int period) {
		
		GregorianCalendar startCal = getCalendarInstance(startDate, true);
		GregorianCalendar endCal = getCalendarInstance(endDate, true);	
		
		long difference = 0;
		
		long startTimeInMillis = startCal.getTimeInMillis();
		long endTimeInMillis = endCal.getTimeInMillis();
		long differenceInMillis = Math.abs(startTimeInMillis - endTimeInMillis);
		
		if (differenceInMillis > 0) {
			
			long days = TimeUnit.MILLISECONDS.toDays(differenceInMillis);
			
			if (period == PERIOD_DAILY) {
				difference = days;
			}
			else if (period == PERIOD_WEEKLY) {
				difference = days/7;
			}
			else {
				
				boolean startIsGrater = startTimeInMillis > endTimeInMillis;
				Calendar calendarGrater = startIsGrater ? startCal : endCal;
				Calendar calendarSmaller = startIsGrater ? endCal : startCal;
				
				int years = calendarGrater.get(Calendar.YEAR) - calendarSmaller.get(Calendar.YEAR);
				if (period == PERIOD_ANNUAL) {
					difference = years;
				}
				else if (period == PERIOD_BIANNUAL) {
					//Un bienio puede ser relativo a cualquier año, por lo que el número de bienios será relativo al número de años
					difference = years/2;
				}
				else if (period == PERIOD_MONTHLY) {
					
					long months = calendarGrater.get(Calendar.MONTH) - calendarSmaller.get(Calendar.MONTH);
					int yearsExtra = years;
					if (months < 0) {
						months = months + 12;
						yearsExtra--;
					}
					if (yearsExtra > 0)
						months = months + yearsExtra*12;
						
					difference = months;
				}
				else if (period == PERIOD_BIMONTHLY) {
	
					long bimesters = getBimester(calendarGrater) - getBimester(calendarSmaller);
					int yearsExtra = years;
					if (bimesters < 0) {
						bimesters = bimesters + 6;
						yearsExtra--;
					}
					if (yearsExtra > 0)
						bimesters = bimesters + yearsExtra*6;
						
					difference = bimesters;
				}
				else if (period == PERIOD_QUARTERLY) {
	
					long trimesters = getTrimester(calendarGrater) - getTrimester(calendarSmaller);
					int yearsExtra = years;
					if (trimesters < 0) {
						trimesters = trimesters + 4;
						yearsExtra--;
					}
					if (yearsExtra > 0)
						trimesters = trimesters + yearsExtra*4;
						
					difference = trimesters;
				}
				else if (period == PERIOD_EVERY_FOUR_MONTHS) {
					
					long quarters = getQuarter(calendarGrater) - getQuarter(calendarSmaller);
					int yearsExtra = years;
					if (quarters < 0) {
						quarters = quarters + 3;
						yearsExtra--;
					}
					if (yearsExtra > 0)
						quarters = quarters + yearsExtra*3;
					
					difference = quarters;
				}
				else if (period == PERIOD_SEMIANNUAL) {
	
					long semesters = getSemester(calendarGrater) - getSemester(calendarSmaller);
					int yearsExtra = years;
					if (semesters < 0) {
						semesters = semesters + 2;
						yearsExtra--;
					}
					if (yearsExtra > 0)
						semesters = semesters + yearsExtra*2;
					
					difference = semesters;
				}
			}
		}
		
		return difference;
	}
	
	public static int getBimester(Calendar calendar) {
		
		int month = calendar.get(Calendar.MONTH);
				
		int bimester;
		if (month < Calendar.MARCH)
			bimester = 1;
		else if (month < Calendar.MAY)
			bimester = 2;
		else if (month < Calendar.JULY)
			bimester = 3;
		else if (month < Calendar.SEPTEMBER)
			bimester = 4;
		else if (month < Calendar.NOVEMBER)
			bimester = 5;
		else 
			bimester = 6;
		
		return bimester;
	}
	
	public static int getTrimester(Calendar calendar) {
		
		int month = calendar.get(Calendar.MONTH);
				
		int trimester;
		if (month < Calendar.APRIL)
			trimester = 1;
		else if (month < Calendar.JULY)
			trimester = 2;
		else if (month < Calendar.OCTOBER)
			trimester = 3;
		else 
			trimester = 4;
		
		return trimester;
	}
	
	public static int getQuarter(Calendar calendar) {
		
		int month = calendar.get(Calendar.MONTH);
				
		int quarter;
		if (month < Calendar.MAY)
			quarter = 1;
		else if (month < Calendar.SEPTEMBER)
			quarter = 2;
		else 
			quarter = 3;
		
		return quarter;
	}
	
	public static int getSemester(Calendar calendar) {
		
		int month = calendar.get(Calendar.MONTH);
				
		int semester;
		if (month < Calendar.JULY)
			semester = 1;
		else 
			semester = 2;
		
		return semester;
	}
}
