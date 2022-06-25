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
package linaje.gui.components;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import linaje.gui.LCombo;
import linaje.gui.renderers.CellRendererDates;
import linaje.logs.Console;
import linaje.utils.Dates;
import linaje.utils.CalendarDates;
import linaje.utils.Lists;
 
@SuppressWarnings("serial")
public class MultiComboDates extends JPanel implements ItemListener, ItemSelectable {
	
	private LCombo<Integer> comboYear = null;
	private LCombo<Integer> comboDay = null;
	private LCombo<Integer> comboMonth = null;
	
	private CalendarDates calendarDates = null;
	private int period = Dates.PERIOD_DAILY;
	
	public MultiComboDates() {
		super();
		initialize();
	}
	public MultiComboDates(int period) {
		super();
		this.period = period;
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new GridBagLayout());
		getComboDay().addItemListener(this);
		getComboMonth().addItemListener(this);
		getComboYear().addItemListener(this);
		
		addCombos();
		
		setSize(getPreferredSize());
	}
	
	private void updateDates() {
		
		List<Integer> years = getCalendarDates().getYears();
		updateComboElements(getComboYear(), years);
		
		updateMonths();
	}
	
	private void updateMonths() {
			
		List<Integer> months;
		if (getComboYear().getItemCount() == 0)
			months = Lists.newList();
		else
			months = getCalendarDates().getMonths(getComboYear().getSelectedItem());
		
		updateComboElements(getComboMonth(), months);
		
		updateDays();
	}
	
	private void updateDays() {
		
		List<Integer> days;
		if (getComboYear().getItemCount() == 0)
			days = Lists.newList();
		else
			days = getCalendarDates().getDays(getComboYear().getSelectedItem(), getComboMonth().getSelectedItem());
		
		updateComboElements(getComboDay(), days);
	}
	
	private void updateComboElements(LCombo<Integer> combo, List<Integer> elements) {
		
		combo.removeItemListener(this);
		
		Integer selectedItem = combo.getSelectedItem() != null ? combo.getSelectedItem() : null;
		if (!elements.isEmpty() && !elements.contains(selectedItem))
			selectedItem = elements.get(0);
			
		combo.removeAllItems();
		combo.addItems(elements);
		
		if (selectedItem != null)
			combo.setSelectedItem(selectedItem);
		
		combo.addItemListener(this);
	}
	
	public void addItem(Date fecha) {
		getCalendarDates().addDate(fecha);
		updateDates();
	}
	
	public void clearDates() {
		setCalendarDates(null);
	}
	
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			if (e.getSource() == getComboMonth()) 
				updateDays();
			else if (e.getSource() == getComboYear()) 
				updateMonths();
			
			fireItemStateChanged();
		}
	}
	
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}
	protected void fireItemStateChanged() {
		
		ItemEvent e = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, getSelectedDate(), ItemEvent.SELECTED);
		Object[] listeners = listenerList.getListenerList();
		// Procesamos los listeners desde el último al primero
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ItemListener.class) {
				((ItemListener)listeners[i+1]).itemStateChanged(e);
			}          
		}
	}
	
	private LCombo<Integer> getComboYear() {
		if (comboYear == null) {
			comboYear = new LCombo<Integer>();
		}
		return comboYear;
	}
	private LCombo<Integer> getComboDay() {
		if (comboDay == null) {
			comboDay = new LCombo<Integer>();
		}
		return comboDay;
	}
	private LCombo<Integer> getComboMonth() {
		if (comboMonth == null) {
			comboMonth = new LCombo<Integer>();
			comboMonth.setRenderer(new CellRendererDates(CellRendererDates.FORMAT_MONTH_SHORT));
		}
		return comboMonth;
	}
	
	public List<Component> getSelectableComponents() {	
		
		List<Component> selectableComponents = Lists.newList();
	
		selectableComponents.add(getComboYear());
		selectableComponents.add(getComboDay());
		selectableComponents.add(getComboMonth());
	
		return selectableComponents;
	}
	
	private void addCombos() {
		try {
	
			removeAll();
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.gridy = 1;
			gbc.gridx = 1;
			gbc.weighty = 0;
			
			//Pintamos los combos (Los de dia y año siempre tendrán el mismo ancho mientras que el de mes variará)
			if (getPeriod() == Dates.PERIOD_MONTHLY ||
				getPeriod() == Dates.PERIOD_BIMONTHLY ||
				getPeriod() == Dates.PERIOD_QUARTERLY ||
				getPeriod() == Dates.PERIOD_EVERY_FOUR_MONTHS ||
				getPeriod() == Dates.PERIOD_SEMIANNUAL) {

				gbc.weightx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				add(getComboMonth(), gbc);
				
				gbc.gridx = 2;
				gbc.weightx = 0;
				gbc.fill = GridBagConstraints.NONE;
				add(getComboYear(), gbc);
			}
			else if (getPeriod() == Dates.PERIOD_ANNUAL || getPeriod() == Dates.PERIOD_BIANNUAL) {

				gbc.weightx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				add(getComboYear(), gbc);
			}
			else   {
				
				add(getComboDay(), gbc);
				
				gbc.gridx = 2;
				gbc.weightx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				add(getComboMonth(), gbc);
				
				gbc.gridx = 3;
				gbc.weightx = 0;
				gbc.fill = GridBagConstraints.NONE;
				add(getComboYear(), gbc);
			}
			revalidate();
		}
		catch (Exception e) {
			Console.printException(e);
		}
	}
	
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
		CellRendererDates renderMonth = (CellRendererDates) getComboMonth().getRenderer();
		String format = period == Dates.PERIOD_DAILY || period == Dates.PERIOD_WEEKLY ?	CellRendererDates.FORMAT_MONTH_SHORT : CellRendererDates.FORMAT_MONTH_LONG;
		renderMonth.setFormat(format);
		addCombos();
	}
	
	public Date getSelectedDate() {
		
		Date selectedDate = null;
		if (!getCalendarDates().isEmpty()) {
			
			Calendar cal = Dates.getCalendarInstance(null, true);
			Dates.setYear(cal, getComboYear().getSelectedItem());
			Dates.setMonth(cal, getComboMonth().getSelectedItem());
			cal.set(Calendar.DATE, getComboDay().getSelectedItem());
			
			selectedDate = cal.getTime();
		}
		
		return selectedDate;
	}
	public void setSelectedDate(Date newDate) {
		
		Date oldDate = getSelectedDate();
		
		boolean datesEqual = oldDate == null && newDate == null || (oldDate != null && newDate != null && Dates.compareIgnoringTimeOfDay(oldDate, newDate) == 0);
		if (!datesEqual && newDate != null && getCalendarDates().containsDate(newDate)) {
			
			Calendar calSelec = Dates.getCalendarInstance(newDate, true);
			
			getComboYear().removeItemListener(this);
			getComboMonth().removeItemListener(this);
			getComboDay().removeItemListener(this);
			
			getComboYear().setSelectedItem(calSelec.get(Calendar.YEAR));
			getComboMonth().setSelectedItem(calSelec.get(Calendar.MONTH));
			getComboDay().setSelectedItem(calSelec.get(Calendar.DATE));
			
			getComboYear().addItemListener(this);
			getComboMonth().addItemListener(this);
			getComboDay().addItemListener(this);
			
			fireItemStateChanged();
		}
	}
		
	public CalendarDates getCalendarDates() {
		if (calendarDates == null)
			calendarDates = new CalendarDates();
		return calendarDates;
	}
	public void setCalendarDates(CalendarDates fechasCombo) {
		this.calendarDates = fechasCombo;
		updateDates();
	}
	
	@Override
	public Date[] getSelectedObjects() {
		return new Date[]{getSelectedDate()};
	}
}
