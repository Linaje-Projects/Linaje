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
package linaje.gui.tests;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;

import linaje.LocalizedStrings;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.LLabel;
import linaje.gui.components.CalendarComponent;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.CalendarDates;
import linaje.utils.Dates;
import linaje.utils.Numbers;

@SuppressWarnings("serial")
public class TestCalendarComponent extends JPanel {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.tests.localization.linaje_gui_tests.properties
		
		public String name;
		public String showCalendar;
		public String selectedDate;
		public String cancelled;
		public String zoom;
		public String filter;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_TESTS_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private CalendarComponent calendarComponent = null;
	private LCheckBox checkBoxZoom = null;
	private LCheckBox checkDatesFilter = null;
	private CalendarDates filterDates = null;
	
	public TestCalendarComponent() {
		super();
		initialize();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		//Truco para obtener la clase actual de forma estática y copiar y pegar éste main en otras clases
		Class currentClass = new Object() { }.getClass().getEnclosingClass();
		UtilsGUI.quickMain(currentClass);
	}
	
	private void initialize() {
		
		setName(TEXTS.name);
		setLayout(new GridBagLayout());
		final LLabel labelInfo = new LLabel(Constants.SPACE);
		labelInfo.setOpaque(true);
		labelInfo.setBackground(ColorsGUI.getColorPanelsBright());
		
		LButton button = new LButton(TEXTS.showCalendar);
		button.setIcon(Icons.CALENDAR);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int respuesta = getCalendarComponent().showInDialog();
				String message;
				if (respuesta == ButtonsPanel.RESPONSE_ACCEPT_YES)
					message = TEXTS.selectedDate + Dates.getFormattedDate(calendarComponent.getSelectedDate());
				else
					message = TEXTS.cancelled;
				labelInfo.setText(message);
				Console.println(message);
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets.top = 20;
		gbc.insets.left = 20;
		gbc.insets.right = 20;
		gbc.insets.bottom = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		add(button, gbc);
		
		gbc.gridy = 2;
		gbc.insets.top = 0;
		add(getCheckBoxZoom(), gbc);
		
		gbc.gridy = 3;
		gbc.insets.top = 0;
		add(getCheckDatesFilter(), gbc);
		
		gbc.gridy = 4;
		gbc.insets.bottom = 20;
		add(labelInfo, gbc);
	}
	
	private CalendarComponent getCalendarComponent() {
		
		if (calendarComponent == null) {
			calendarComponent = new CalendarComponent();
			//calendarComponent.setFont(calendarComponent.getFont().deriveFont(20f));
			
			/*calendarComponent.setBackground(Color.yellow);
			calendarComponent.setForeground(Color.magenta);
			calendarComponent.setSelectedBackground(Color.blue);
			calendarComponent.setOpaque(false);
			calendarComponent.getDefaultDialog().setTransparency(0.5f);
			*/
			Calendar cal = Dates.getCalendarInstance(null);
			CalendarDates filterDates = new CalendarDates();
			for (int i = 0; i < 50; i++) {
				cal.set(Calendar.DATE, Numbers.getRandomNumberInt(1, 31));
				cal.set(Calendar.MONTH, Numbers.getRandomNumberInt(0, 11));
				cal.set(Calendar.YEAR, Numbers.getRandomNumberInt(2015, 2018));
				filterDates.addDate(cal.getTime());
			}
			Date today = new Date();
			filterDates.addDate(today);//Hoy
			filterDates.addDate(Dates.calculateNextPeriodsDate(today, Dates.PERIOD_DAILY, 1));//Mañana
			filterDates.addDate(Dates.calculateNextPeriodsDate(today, Dates.PERIOD_DAILY, -1));//Ayer
			
			calendarComponent.setFilterDates(filterDates);
			
			//Cambiamos la fuente y el tamaño del calendario con la rueda del ratón
			MouseWheelListener mouseWheelListener = new MouseWheelListener() {
				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					
					if (getCheckBoxZoom().isSelected() && e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
						
						int wheelRotation = e.getWheelRotation();
						Font font = calendarComponent.getFont();
						if (wheelRotation < 0) {
							calendarComponent.setFont(UtilsGUI.getFontWithSize(font, font.getSize()+1));
						}
						else {
							calendarComponent.setFont(UtilsGUI.getFontWithSize(font, font.getSize()-1));
						}
						Console.println("Font: "+font.getName() + " "+font.getSize());
					}
				}
			};
			calendarComponent.addMouseWheelListener(mouseWheelListener);
		}
		return calendarComponent;
	}
	
	public LCheckBox getCheckBoxZoom() {
		if (checkBoxZoom == null) {
			checkBoxZoom = new LCheckBox(TEXTS.zoom, true);
		}
		return checkBoxZoom;
	}
	
	public LCheckBox getCheckDatesFilter() {
		if (checkDatesFilter == null) {
			checkDatesFilter = new LCheckBox(TEXTS.filter, true);
			checkDatesFilter.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					getCalendarComponent().setFilterDates(e.getStateChange() == ItemEvent.SELECTED ? getFilterDates() : null);
				}
			});
		}
		return checkDatesFilter;
	}
	
	public CalendarDates getFilterDates() {
		if (filterDates == null) {
			
			filterDates = new CalendarDates();
			Calendar cal = Dates.getCalendarInstance(null);
			for (int i = 0; i < 50; i++) {
				cal.set(Calendar.DATE, Numbers.getRandomNumberInt(1, 31));
				cal.set(Calendar.MONTH, Numbers.getRandomNumberInt(0, 11));
				cal.set(Calendar.YEAR, Numbers.getRandomNumberInt(2015, 2018));
				filterDates.addDate(cal.getTime());
			}
			Date today = new Date();
			filterDates.addDate(today);//Hoy
			filterDates.addDate(Dates.calculateNextPeriodsDate(today, Dates.PERIOD_DAILY, 1));//Mañana
			filterDates.addDate(Dates.calculateNextPeriodsDate(today, Dates.PERIOD_DAILY, -1));//Ayer
		}
		return filterDates;
	}
}
