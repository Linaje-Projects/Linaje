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
package linaje.gui.renderers;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;

import linaje.gui.cells.LabelCell;
import linaje.statics.Constants;
import linaje.utils.Dates;
import linaje.utils.CalendarDates;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class CellRendererDates extends LCellRenderer<Object> {

	public static final String FORMAT_MONTH_SHORT = "MMM";
	public static final String FORMAT_MONTH_LONG = "MMMM";
	public static final String FORMAT_WEEK_DAY_LETTER = "E";
	public static final String FORMAT_WEEK_DAY_SHORT = "EEE";
	public static final String FORMAT_WEEK_DAY_LONG = "EEEE";
	
	private String format = null;
	private boolean capitalizedText = true;
		
	public CellRendererDates(String format) {
		super();
		setFormat(format);
	}
	public CellRendererDates(String format, boolean capitalizedText) {
		super();
		setFormat(format);
		setCapitalizedText(capitalizedText);
	}
	
	private String getFormattedDate(Object value) {
		
		String formattedDate = null;
		if (value != null) {
			
			if (value instanceof Integer) {	
				int dateInt = ((Integer) value).intValue();
				if (getFormat().equals(FORMAT_MONTH_LONG))
					formattedDate = CalendarDates.getMonthName(dateInt);
				else if (getFormat().equals(FORMAT_MONTH_SHORT))
					formattedDate = CalendarDates.getMonthNameShort(dateInt);
				else if (getFormat().equals(FORMAT_WEEK_DAY_LONG))
					formattedDate = CalendarDates.getDayOfWeekName(dateInt);
				else if (getFormat().equals(FORMAT_WEEK_DAY_SHORT))
					formattedDate = CalendarDates.getDayOfWeekNameShort(dateInt);
				else if (getFormat().equals(FORMAT_WEEK_DAY_LETTER))
					formattedDate = CalendarDates.getDayOfWeekLetter(dateInt);
				
				//Los nombres de meses y semanas vienen capitalizados por defecto
				if (!isCapitalizedText() && formattedDate != null)
					formattedDate = formattedDate.toUpperCase();
			}
			else {
				Date date = null;
				Calendar cal = null;
				if (value instanceof Date)
					date = (Date) value;
				else if (value instanceof Calendar) {
					cal = (Calendar) value;
					date = cal.getTime();
				}
				
				if (date != null) {
					try {
						if (getFormat().equals(FORMAT_WEEK_DAY_LETTER)) {
							cal = cal != null ? cal : Dates.getCalendarInstance(date);
							formattedDate = CalendarDates.getDayOfWeekLetter(cal);
						}
						else {
							formattedDate = Dates.getFormattedDate(date, getFormat());
						}
					}
					catch (Exception e) {
						//Si el format no es correcto, formateamos la fecha con el format por defecto
						formattedDate = Dates.getFormattedDate(date);
					}
					//Los formatos de fechas por vienen en mayusculas por defecto
					if (isCapitalizedText())
						formattedDate = Strings.capitalizeAllWords(formattedDate);
				}
			}
			
			if (formattedDate == null) {
				formattedDate = value.toString();
				if (isCapitalizedText())
					formattedDate = Strings.capitalizeAllWords(formattedDate);
			}
		}
		else {
			formattedDate = Constants.VOID;
		}
		
		return formattedDate;
	}
	
	@Override
	public LabelCell getListCellRendererComponent(JList<? extends Object> list,	Object value, int index, boolean isSelected, boolean cellHasFocus) {
		String formattedDate = getFormattedDate(value);
		return super.getListCellRendererComponent(list, formattedDate, index, isSelected,	cellHasFocus);
	}

	@Override
	public LabelCell getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String formattedDate = getFormattedDate(value);
		return super.getTableCellRendererComponent(table, formattedDate, isSelected, hasFocus, row, column);
	}

	@Override
	public LabelCell getTreeCellRendererComponent(JTree tree, Object value,	boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		String formattedDate = getFormattedDate(value);
		return super.getTreeCellRendererComponent(tree, formattedDate, isSelected, expanded, leaf, row, hasFocus);
	}

	public String getFormat() {
		if (format == null)
			format = Dates.FORMAT_DEFAULT;
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isCapitalizedText() {
		return capitalizedText;
	}
	public void setCapitalizedText(boolean capitalizedText) {
		this.capitalizedText = capitalizedText;
	}
}
