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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import linaje.LocalizedStrings;
import linaje.gui.AppGUI;
import linaje.gui.LArrowButton;
import linaje.gui.LButton;
import linaje.gui.LCombo;
import linaje.gui.LLabel;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.LToggleButton;
import linaje.gui.RoundedBorder;
import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.statics.Constants;
import linaje.utils.Dates;
import linaje.utils.CalendarDates;
import linaje.utils.Lists;
import linaje.utils.Numbers;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class CalendarComponent extends LDialogContent {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String today;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private LToggleButton toggleButtonAux = null;
	
	private LLabel[] labelsDaysOfWeek = null;
	private LLabel[][] labelsDays = null;
	
	private Calendar selectedDateCalendar = null;
	private LCombo<LLabel> comboMonths = null;
	private LArrowButton btnDown = null;
	private LArrowButton btnUp = null;
	private LButton btnToday = null;
	private LButton btnAccept = null;
	private LTextField textFieldYear = null;
	private CalendarDates filterDates = null;
	
	private LPanel panelLabels = null;
	private LPanel panelTextField = null;
	
	private Color selectedBackground = null;
	private Color selectedForeground = null;
	private Color backgroundToday = null;
	private Color foregroundToday = null;
	
	private Border borderToday = null;
	private Border borderSelected = null;
	
	//Cuando cambiamos de mes/año usaremos este día para aproximarnos a el en caso de que el mes calculado no lo contenga
	//Se asignará en setSelectedDate o al pinchar un dia
	private int selectedDayExplicitly = 0;
	
	private ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				monthChanged();
			}
		}
	};
	
	private MouseListener mouseListener = new MouseListener() {
		
		public void mousePressed(MouseEvent e) {
			JLabel labelDay = (JLabel) e.getSource();
			String textDay = labelDay.getText();
			if (labelDay.isEnabled() && !textDay.equals(Constants.VOID)) {
				selectedDayExplicitly = Integer.parseInt(textDay);
				getSelectedDateCalendar().set(Calendar.DATE, selectedDayExplicitly);
				initLabels();
			}
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	};

	private MouseWheelListener mouseWheelListener = new MouseWheelListener() {
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			
			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				
				int wheelRotation = e.getWheelRotation();
				if (wheelRotation < 0) {
					nextMonth();
				}
				else {
					previousMonth();
				}
			}
		}
	};
	
	private KeyListener keyListener = new KeyListener() {
		
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP)
				nextYear();
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				previousYear();
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				nextMonth();
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				previousMonth();
		}
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (Dates.compareIgnoringTimeOfDay(getSelectedDate(), new Date()) == 0)
					dispose();
				else
					setSelectedDate(new Date());
			}
		}
		public void keyTyped(KeyEvent e) {}
	};
	
		
	public CalendarComponent() {
		super();
		initialize();
	}
	
	public int showInDialog(JButton buttonCalendar) {
		
		Point location = buttonCalendar.getLocationOnScreen();
		location.x = location.x - getWidth();// + buttonCalendar.getWidth() + 2;
		location.y = location.y + buttonCalendar.getHeight() + 2;
		
		return showInDialog(location);
	}
	
	@Override
	public int showInDialog(Point location) {
		getBtnAccept().setVisible(true);
		getBtnToday().setVisible(true);
		return super.showInDialog(location);
	}

	private LLabel[] getLabelsDaysOfWeek() {
		
		if (labelsDaysOfWeek == null) {
			String[] daysOfWeekLetters = CalendarDates.getDaysOfWeekLetters(Dates.getCalendarInstance(null));
			labelsDaysOfWeek = new LLabel[daysOfWeekLetters.length];
			for (int i = 0; i < daysOfWeekLetters.length; i++) {
				LLabel label = newLabelCalendario();
				label.setOpaque(true);
				label.setBackground(getSelectedBackground());
				label.setForeground(getSelectedForeground());
				label.setText(daysOfWeekLetters[i]);
				Dimension size = label.getPreferredSize();
				size.height++;
				label.setPreferredSize(size);
				label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getBackground()));
				labelsDaysOfWeek[i] = label;
			}
		}
		return labelsDaysOfWeek;
	}
	
	private LLabel[][] getLabelsDays() {
		
		if (labelsDays == null) {
			int numRows = 6;
			int numColumns = 7;
			labelsDays = new LLabel[numRows][numColumns];
			for (int row = 0; row < numRows; row++) {
				for (int column = 0; column < numColumns; column++) {
					LLabel label = newLabelCalendario();
					label.setForeground(getForeground());
					label.addMouseListener(mouseListener);
					labelsDays[row][column] = label;
				}
			}
		}
		return labelsDays;
	}
	
	private LLabel newLabelCalendario() {
		
		LLabel label = new LLabel(Constants.VOID);
		//label.setMargin(new Insets(2, 2, 2, 2));
		label.setOpaque(false);
		label.setFont(getFont());
		label.setHorizontalTextPosition(SwingConstants.RIGHT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setTooltipEnabled(false);
		
		return label;
	}
	
	private void adjustComponentsToFont() {
		
		Dimension sizeLabels = new Dimension(getFont().getSize()*2, (int) (getFont().getSize()*1.7));
		for (int i = 0; i < getLabelsDays().length; i++) {
			for (int j = 0; j < getLabelsDays()[i].length; j++) {
				getLabelsDays()[i][j].setFont(getFont());
				getLabelsDays()[i][j].setPreferredSize(sizeLabels);
			}
		}
		Dimension sizeLabelsDaysOfWeek = new Dimension(sizeLabels.width, sizeLabels.height+1);
		for (int i = 0; i < getLabelsDaysOfWeek().length; i++) {
			getLabelsDaysOfWeek()[i].setFont(getFont());
			getLabelsDaysOfWeek()[i].setPreferredSize(sizeLabelsDaysOfWeek);
		}
		
		getComboMonths().setFont(getFont());
		getComboMonths().setFontStyle(Font.BOLD);
		getTextFieldYear().setFont(getFont());
		getTextFieldYear().setFontStyle(Font.BOLD);
		
		getBtnAccept().setFontSize(getFont().getSize());
		getBtnToday().setFontSize(getFont().getSize());
		
		Dimension sizeCombo = getComboMonths().getPreferredSize();
		sizeCombo.width = sizeCombo.width + 5;
		getComboMonths().setMaximumSize(sizeCombo);
		getComboMonths().setMinimumSize(sizeCombo);
		
		Dimension sizeTextField = getTextFieldYear().getPreferredSize();
		sizeTextField.width = sizeTextField.width + 5;
		getTextFieldYear().setMaximumSize(sizeTextField);
		getTextFieldYear().setMinimumSize(sizeTextField);
		
		Dimension sizeBtnAccept = getBtnAccept().getPreferredSize();
		getBtnAccept().setMaximumSize(sizeBtnAccept);
		getBtnAccept().setMinimumSize(sizeBtnAccept);
		
		Dimension sizeBtnToday = getBtnToday().getPreferredSize();
		getBtnToday().setMaximumSize(sizeBtnToday);
		getBtnToday().setMinimumSize(sizeBtnToday);
		
		Dimension sizeBtnUpDown = new Dimension(sizeTextField.height, sizeTextField.height/2);
		getbtnDown().setPreferredSize(sizeBtnUpDown);
		getbtnDown().setMaximumSize(sizeBtnUpDown);
		getbtnDown().setMinimumSize(sizeBtnUpDown);
		getbtnUp().setPreferredSize(sizeBtnUpDown);
		getbtnUp().setMaximumSize(sizeBtnUpDown);
		getbtnUp().setMinimumSize(sizeBtnUpDown);
		
		
		getDefaultDialog().setTitleFont(UtilsGUI.getFontWithSize(getFont(), getFont().getSize()+2));
		revalidate();
	}
	
	private void nextYear() {
		int currentYear = Integer.parseInt(getTextFieldYear().getText());
		int newYear;
		if (getFilterDates().isEmpty())
			newYear = currentYear+1;
		else {
			List<Integer> years = getFilterDates().getYears();
			int indexCurrent = years.indexOf(currentYear);
			int nextIndex;
			if (indexCurrent < years.size() - 1)
				nextIndex = indexCurrent + 1;
			else
				nextIndex = 0;
			newYear = years.get(nextIndex);
		}
			
		setYear(newYear);
	}
	private void previousYear() {
		
		int currentYear = Integer.parseInt(getTextFieldYear().getText());
		int newYear;
		if (getFilterDates().isEmpty())
			newYear = currentYear-1;
		else {
			List<Integer> anos = getFilterDates().getYears();
			int indexActual = anos.indexOf(currentYear);
			int nextIndex;
			if (indexActual > 0)
				nextIndex = indexActual - 1;
			else
				nextIndex = anos.size() - 1;
			newYear = anos.get(nextIndex);
		}
		setYear(newYear);
	}
	
	private void setYear(int newYear) {
		updateTextFieldYear(newYear);
		initLabels();
	}
	
	private void nextMonth() {
		
		boolean filterExists = !getFilterDates().isEmpty();
		if (filterExists) {
			//Si hay filtro definido pasamos a la siguiente fecha en lugar de al siguiente mes
			Date nextDate = getFilterDates().getNextDate(getSelectedDate());
			setSelectedDate(nextDate);
		}
		else {
			int index = getComboMonths().getSelectedIndex() + 1;
			if (index >= getComboMonths().getItemCount()) {
				index = 0;
			}
			updateNewMonth(index, true);
			if (index == 0)
				nextYear();
			else
				initLabels();
		}
	}
	private void previousMonth() {
		
		boolean filterExists = !getFilterDates().isEmpty();
		if (filterExists) {
			//Si hay filtro definido pasamos a la anterior fecha en lugar de al anterior mes
			Date previousDate = getFilterDates().getPreviousDate(getSelectedDate());
			setSelectedDate(previousDate);
		}
		else {
			int index = getComboMonths().getSelectedIndex() - 1;
			if (index < 0) {
				index = getComboMonths().getItemCount() - 1;
			}
			updateNewMonth(index, false);
			if (index == getComboMonths().getItemCount() - 1)
				previousYear();
			else
				initLabels();
		}
	}
	
	private void monthChanged() {
		updateMonth();
		initLabels();
	}
	
	private void updateComboMonths(int newYear) {
		
		if (!getFilterDates().isEmpty()) {
			//Deshabiliatmos los meses que no estén en el filtro de fechas
			List<Integer> monthsOfYear = getFilterDates().getMonths(newYear);
			for (int month = 0; month < getComboMonths().getItemCount(); month++) {
				boolean enabledMonth = monthsOfYear.contains(month);
				getComboMonths().getItemAt(month).setEnabled(enabledMonth);
			}
			updateNewMonth(getComboMonths().getSelectedIndex(), true);
		}
		else {
			for (int month = 0; month < getComboMonths().getItemCount(); month++) {
				getComboMonths().getItemAt(month).setEnabled(true);
			}
		}
	}
	
	private void updateNewMonth(int newIndexMonth, boolean indexEnabledHigher) {
		
		int indexCurrent = getComboMonths().getSelectedIndex();
		if (newIndexMonth != indexCurrent || !getComboMonths().getItemAt(newIndexMonth).isEnabled()) {
			getComboMonths().removeItemListener(itemListener);
			if (getComboMonths().getItemAt(newIndexMonth).isEnabled()) {
				getComboMonths().setSelectedIndex(newIndexMonth);
			}
			else {
				int indexEnabled;
				if (indexEnabledHigher) {
					//Buscamos el siguiente mes habilitado
					indexEnabled = newIndexMonth + 1;
					while (indexEnabled < getComboMonths().getItemCount() && !getComboMonths().getItemAt(indexEnabled).isEnabled()) {
						indexEnabled++;
					}
					if (indexEnabled >= getComboMonths().getItemCount() || !getComboMonths().getItemAt(indexEnabled).isEnabled()) {
						//No había ningún mes mayor habilitado por lo que seguimos buscando desde el primero
						indexEnabled = 0;
						while (indexEnabled < newIndexMonth && !getComboMonths().getItemAt(indexEnabled).isEnabled()) {
							indexEnabled++;
						}
					}
				}
				else {
					//Buscamos el anterior mes habilitado
					indexEnabled = newIndexMonth - 1;
					while (indexEnabled >= 0 && !getComboMonths().getItemAt(indexEnabled).isEnabled()) {
						indexEnabled--;
					}
					if (indexEnabled < 0 || !getComboMonths().getItemAt(indexEnabled).isEnabled()) {
						//No había ningún mes menor habilitado por lo que seguimos buscando desde el último
						indexEnabled = getComboMonths().getItemCount() - 1;
						while (indexEnabled > newIndexMonth && !getComboMonths().getItemAt(indexEnabled).isEnabled()) {
							indexEnabled--;
						}
					}
				}
				getComboMonths().setSelectedIndex(indexEnabled);
			}
			updateMonth();
			getComboMonths().addItemListener(itemListener);
		}
	}
	
	private void updateTextFieldYear(int newYear) {
		
		if (!getTextFieldYear().getText().equals(String.valueOf(newYear))) {
			
			boolean newYearAllowed = getFilterDates().isEmpty();
			if (!newYearAllowed) {
				List<Integer> yearsFilter = getFilterDates().getYears();
				newYearAllowed = yearsFilter.contains(newYear);
			}
			
			if (newYearAllowed) {
				getTextFieldYear().setText(Integer.toString(newYear));
				updateYear();
				updateComboMonths(newYear);
			}
		}
	}
	
	private void updateMonth() {
		int month = getComboMonths().getSelectedIndex();
		Dates.setMonth(getSelectedDateCalendar(), month);
		adjustDayOfMonth();
	}
	
	private void updateYear() {
		if (Numbers.isIntegerNumber(getTextFieldYear().getText())) {
			Dates.setYear(getSelectedDateCalendar(), Integer.parseInt(getTextFieldYear().getText()));
			adjustDayOfMonth();
		}
	}
	
	private void adjustDayOfMonth() {
		
		int selectedDay = getSelectedDateCalendar().get(Calendar.DATE);
		if (selectedDay != selectedDayExplicitly) {
			int dayToSelect = selectedDayExplicitly;
			int lastDayOfMonth = getSelectedDateCalendar().getActualMaximum(Calendar.DATE);
			if (dayToSelect > lastDayOfMonth)
				dayToSelect = lastDayOfMonth;
			getSelectedDateCalendar().set(Calendar.DATE, dayToSelect);
		}
		if (!getFilterDates().isEmpty() && !getFilterDates().containsDate(getSelectedDateCalendar().getTime())) {
			int currentYear = getSelectedDateCalendar().get(Calendar.YEAR);
			int currentMonth = getSelectedDateCalendar().get(Calendar.MONTH);
			List<Integer> days = getFilterDates().getDays(currentYear, currentMonth);
			if (!days.isEmpty()) {
				int firstDayFilter = days.get(0);
				getSelectedDateCalendar().set(Calendar.DATE, firstDayFilter);
			}
		}
	}
	
	private void initLabels() {
		
		GregorianCalendar calendarToday = Dates.getCalendarInstance(new Date());
		GregorianCalendar calendar = Dates.getCalendarInstance(getSelectedDate());
		
		int dayToday = -1;
		boolean isYearMonthToday = calendarToday.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendarToday.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
		if (isYearMonthToday)
			dayToday = calendarToday.get(Calendar.DATE);
		
		int selectedDay = calendar.get(Calendar.DATE);
		
		calendar.set(Calendar.DATE, 1);
		int dayOfWeekFirstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
		int[] daysOfWeek = CalendarDates.getDaysOfWeek(calendar);
		int indexFirstDayOfMonth = Lists.arrayIndexOf(daysOfWeek, dayOfWeekFirstDayOfMonth);
		int lastDayOfMonth = calendar.getActualMaximum(Calendar.DATE);
		
		//isLastDayOfMonth = selectedDay == lastDayOfMonth;
		
		boolean filterExists = !getFilterDates().isEmpty();
		int day = 1;
		for (int indexWeek = 0; indexWeek < getLabelsDays().length; indexWeek++) {
			
			LLabel[] labelsWeek = getLabelsDays()[indexWeek];
			for (int indexDay = 0; indexDay < labelsWeek.length; indexDay++) {
				
				LLabel labelDay = labelsWeek[indexDay];
				
				String textLabel = Constants.VOID;
				boolean enabled = false;
				boolean opaque = false;
				Color foreground = getForeground();
				Color background = getBackground();
				Border border = null;
				
				boolean dayBlank = day > lastDayOfMonth || (indexWeek == 0 && indexDay < indexFirstDayOfMonth);
				if (!dayBlank) {
					textLabel = Integer.toString(day);
					calendar.set(Calendar.DATE, day);
					enabled = !filterExists || getFilterDates().containsDate(calendar.getTime());					
					
					if (day == dayToday) {
						opaque = true;
						if (getForegroundToday() != null)
							foreground = getForegroundToday();
						if (getBackgroundToday() != null)
							background = getBackgroundToday();
						border = getBorderToday();
					}
					if (day == selectedDay) {
						opaque = enabled;
						foreground = getSelectedForeground();
						background = getSelectedBackground();
						if (day != dayToday) 
							border = getBorderSelected();
					}
					day++;
				}
				
				labelDay.setText(textLabel);
				labelDay.setEnabled(enabled);
				labelDay.setOpaque(opaque);
				labelDay.setBackground(background);
				labelDay.setForeground(foreground);
				labelDay.setBorder(border);
			}
			getTextFieldYear().requestFocus();
		}
		
		String formattedDate = Dates.getFormattedDate(getSelectedDate(), "EEEE, d MMM yyyy");
		setTitle(Strings.capitalizeAllWords(formattedDate));
		getBtnToday().setEnabled(getFilterDates().isEmpty() || getFilterDates().containsDate(new Date()));
		
		repaint();
	}
	
	private LArrowButton getbtnDown() {
		if (btnDown == null) {
			btnDown = new LArrowButton(SwingConstants.SOUTH);
			Dimension size = new Dimension(14, 9);
			btnDown.setPreferredSize(size);
			btnDown.setMaximumSize(size);
			btnDown.setMinimumSize(size);
			btnDown.setBorderPainted(false);
			
			btnDown.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					previousYear();
				}
			});
		}
		return btnDown;
	}
	private LArrowButton getbtnUp() {
		if (btnUp == null) {
			btnUp = new LArrowButton(SwingConstants.NORTH);
			Dimension size = new Dimension(14, 9);
			btnUp.setPreferredSize(size);
			btnUp.setMaximumSize(size);
			btnUp.setMinimumSize(size);
			btnUp.setBorderPainted(false);
			
			
			btnUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					nextYear();
				}
			});
		}
		return btnUp;
	}
	public LButton getBtnToday() {
		if (btnToday == null) {
			btnToday = new LButton(TEXTS.today);
			btnToday.setMargin(new Insets(1, 10, 1, 10));
			btnToday.setVisible(false);
			btnToday.getButtonProperties().setRespectMaxMinSize(false);
			
			btnToday.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setSelectedDate(new Date());
				}
			});
		}
		return btnToday;
	}
	
	public LButton getBtnAccept() {
		if (btnAccept == null) {
			btnAccept = new LButton(ButtonsPanel.TEXTS.accept);
			btnAccept.setMargin(new Insets(1, 10, 1, 10));
			btnAccept.setVisible(false);
			btnAccept.getButtonProperties().setRespectMaxMinSize(false);
			
			btnAccept.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					getButtonsPanel().setResponse(ButtonsPanel.RESPONSE_ACCEPT_YES);
					dispose();
				}
			});
		}
		return btnAccept;
	}
	
	private LCombo<LLabel> getComboMonths() {
		if (comboMonths == null) {
			comboMonths = new LCombo<LLabel>();
			comboMonths.setBorder(BorderFactory.createEmptyBorder());
			comboMonths.makeButtonTransparent();
			comboMonths.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			comboMonths.setFont(getFont());
			comboMonths.setFontStyle(Font.BOLD);
			String[] monthNames = CalendarDates.getMonthNames();
			for (int i = 0; i < monthNames.length; i++) {
				comboMonths.addItem(new LLabel(monthNames[i]));
			}
			
			comboMonths.setMaximumSize(getPreferredSize());
			comboMonths.setMinimumSize(getPreferredSize());
			comboMonths.setBackground(getBackground());
			comboMonths.setOpaque(false);
			
			comboMonths.addItemListener(itemListener);
			comboMonths.addKeyListener(keyListener);
		}
		return comboMonths;
	}
	
	private LTextField getTextFieldYear() {
		if (textFieldYear == null) {
			textFieldYear = new LTextField();
			textFieldYear.setEditable(false);
			textFieldYear.setBorder(BorderFactory.createEmptyBorder());
			textFieldYear.setOpaque(false);
			textFieldYear.setFont(getFont());
			textFieldYear.setFontStyle(Font.BOLD);
			textFieldYear.setSelectTextWithFocus(false);
			//El textfield tiene que ser focusable para que funcionen los eventos de teclado del calendario
			//Añadimos el focusListener para que no se seleccione el texto al ganar el foco
			textFieldYear.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {}
				public void focusGained(FocusEvent e) {
					getTextFieldYear().setSelectionEnd(0);
				}
			});
		}
		return textFieldYear;
	}
	
	private void initialize() {
		
		setMargin(5);
		setAutoSize(true);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbcCombo = new GridBagConstraints();
		gbcCombo.gridx = 1;
		gbcCombo.gridy = 1;
		gbcCombo.gridwidth = 3;
		gbcCombo.fill = GridBagConstraints.HORIZONTAL;
		gbcCombo.weightx = 1.0;
		gbcCombo.weighty = 0.0;
		gbcCombo.insets = new Insets(0, 0, 0, 2);
		
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.gridx = 4;
		gbcTextField.gridy = 1;
		gbcTextField.gridwidth = 1;
		gbcTextField.fill = GridBagConstraints.NONE;
		gbcTextField.weightx = 0.0;
		gbcTextField.weighty = 0.0;
		gbcTextField.insets = new Insets(0, 0, 0, 0);
		
		GridBagConstraints gbcLabels = new GridBagConstraints();
		gbcLabels.gridx = 1;
		gbcLabels.gridy = 2;
		gbcLabels.gridwidth = 4;
		gbcLabels.fill = GridBagConstraints.BOTH;
		gbcLabels.weightx = 1.0;
		gbcLabels.weighty = 1.0;
		gbcLabels.insets = new Insets(5, 0, 0, 0);
		
		GridBagConstraints gbcBtnHoy = new GridBagConstraints();
		gbcBtnHoy.gridx = 1;
		gbcBtnHoy.gridy = 3;
		//gbcBtnHoy.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnHoy.gridwidth = 2;
		gbcBtnHoy.weightx = 0.0;
		gbcBtnHoy.weighty = 0.0;
		gbcBtnHoy.insets = new Insets(1, 1, 1, 2);
		
		GridBagConstraints gbcBtnAceptar = new GridBagConstraints();
		gbcBtnAceptar.gridx = 3;
		gbcBtnAceptar.gridy = 3;
		gbcBtnAceptar.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnAceptar.gridwidth = 2;
		gbcBtnAceptar.weightx = 1.0;
		gbcBtnAceptar.weighty = 0.0;
		gbcBtnAceptar.insets = new Insets(1, 2, 1, 1);
		
		getToggleButtonAux().setSelected(false);
		setBackground(UISupportButtons.getBackground(getToggleButtonAux()));
		setForeground(UISupportButtons.getForeground(getToggleButtonAux()));
		//getToggleButtonAux().setSelected(true);
		//setSelectedBackground(UISupportButtons.getBackground(getToggleButtonAux()));
		//setSelectedForeground(UISupportButtons.getForeground(getToggleButtonAux()));
		
		add(getComboMonths(), gbcCombo);
		add(getPanelTextField(), gbcTextField);
		add(getPanelLabels(), gbcLabels);
		add(getBtnToday(), gbcBtnHoy);
		add(getBtnAccept(), gbcBtnAceptar);
				
		setSelectedDate(new Date());
		
		setFont(AppGUI.getFont());

		getTextFieldYear().addKeyListener(keyListener);
		this.addMouseWheelListener(mouseWheelListener);
	}
	
	private LPanel getPanelTextField() {
		if (panelTextField == null) {
			panelTextField = new LPanel(new GridBagLayout());
			panelTextField.setOpaque(false);
			
			GridBagConstraints gbcTextField = new GridBagConstraints();
			gbcTextField.gridx = 1;
			gbcTextField.gridy = 1;
			gbcTextField.fill = GridBagConstraints.HORIZONTAL;
			gbcTextField.gridheight = 2;
			gbcTextField.weightx = 1.0;
			gbcTextField.weighty = 1.0;
			gbcTextField.insets = new Insets(0, 0, 0, 0);
			
			GridBagConstraints gbcUp = new GridBagConstraints();
			gbcUp.gridx = 2;
			gbcUp.gridy = 1;
			gbcUp.fill = GridBagConstraints.VERTICAL;
			gbcUp.weightx = 0.0;
			gbcUp.weighty = 1.0;
			gbcUp.insets = new Insets(0, 1, 0, 0);
			
			GridBagConstraints gbcDown = new GridBagConstraints();
			gbcDown.gridx = 2;
			gbcDown.gridy = 2;
			gbcDown.fill = GridBagConstraints.VERTICAL;
			gbcDown.weightx = 0.0;
			gbcDown.weighty = 1.0;
			gbcDown.insets = new Insets(0, 1, 0, 0);
			
			panelTextField.add(getTextFieldYear(), gbcTextField);
			panelTextField.add(getbtnUp(), gbcUp);
			panelTextField.add(getbtnDown(), gbcDown);
		}
		return panelTextField;
	}
	private LPanel getPanelLabels() {
		if (panelLabels == null) {
			panelLabels = new LPanel(new GridLayout(7, 7));
			panelLabels.setOpaque(false);
			
			for (int i = 0; i < getLabelsDaysOfWeek().length; i++) {
				panelLabels.add(getLabelsDaysOfWeek()[i]);
			}
			for (int i = 0; i < getLabelsDays().length; i++) {
				JLabel[] fila = getLabelsDays()[i];
				for (int j = 0; j < fila.length; j++) {
					panelLabels.add(fila[j]);
				}
			}
		}
		return panelLabels;
	}
	
	public Calendar getSelectedDateCalendar() {
		if (selectedDateCalendar == null)
			selectedDateCalendar = Dates.getCalendarInstance(null);
		return selectedDateCalendar;
	}
	
	public Date getSelectedDate() {
		return getSelectedDateCalendar().getTime();
	}
	public void setSelectedDate(Date selectedDate) {
		if (!getFilterDates().isEmpty()) {
			if (!getFilterDates().containsDate(selectedDate)) {
				int year = getFilterDates().getYears().get(0);
				int month = getFilterDates().getMonths(year).get(0);
				int day = getFilterDates().getDays(year, month).get(0);
				Calendar cal = Dates.getCalendarInstance(null);
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DATE, day);
				selectedDate = cal.getTime();
			}
		}
		getSelectedDateCalendar().setTime(selectedDate);
		selectedDayExplicitly = getSelectedDateCalendar().get(Calendar.DATE);
		updateTextFieldYear(getSelectedDateCalendar().get(Calendar.YEAR));
		updateNewMonth(getSelectedDateCalendar().get(Calendar.MONTH), true);
		initLabels();
	}
	
	public CalendarDates getFilterDates() {
		if (filterDates == null)
			filterDates = new CalendarDates();
		return filterDates;
	}
	public void setFilterDates(CalendarDates filterDates) {
		this.filterDates = filterDates;
		updateComboMonths(getSelectedDateCalendar().get(Calendar.YEAR));
		setSelectedDate(getSelectedDate());
	}
	
	private LToggleButton getToggleButtonAux() {
		if (toggleButtonAux == null)
			toggleButtonAux = new LToggleButton();
		return toggleButtonAux;
	}
	public Color getSelectedBackground() {
		if (selectedBackground == null) {
			getToggleButtonAux().setSelected(true);
			selectedBackground = UISupportButtons.getBackground(getToggleButtonAux());
		}
		return selectedBackground;
	}
	public Color getSelectedForeground() {
		if (selectedForeground == null) {
			getToggleButtonAux().setSelected(true);
			selectedForeground = UISupportButtons.getForeground(getToggleButtonAux());
		}
		return selectedForeground;
	}
	
	public Border getBorderToday() {
		if (borderToday == null) {
			RoundedBorder roundedBorder = new RoundedBorder(false, ColorsGUI.getColorInfo());
			roundedBorder.setCornersCurveSize(new Dimension(10, 10));
			roundedBorder.setThicknessLineBorder(2);
			borderToday = roundedBorder;
		}
		return borderToday;
	}
	public Border getBorderSelected() {
		if (borderSelected == null) {
			RoundedBorder roundedBorder = new RoundedBorder(false, getSelectedBackground());
			roundedBorder.setCornersCurveSize(new Dimension(10, 10));
			//roundedBorder.setThicknessLineBorder(2);
			borderSelected = roundedBorder;
		}
		return borderSelected;
	}
	
	public void setBorderToday(Border borderToday) {
		this.borderToday = borderToday;
	}
	public void setBorderSelected(Border borderSelected) {
		this.borderSelected = borderSelected;
	}
	public Color getBackgroundToday() {
		return backgroundToday;
	}
	public Color getForegroundToday() {
		return foregroundToday;
	}
	
	public void setFont(Font font) {
		super.setFont(font);
		adjustComponentsToFont();
		setPreferredSize(null);
		setSize(getPreferredSize());
		assignSize();
		revalidate();
	}
	public void setSelectedBackground(Color selectedBackground) {
		this.selectedBackground = selectedBackground;
		if (selectedBackground != null) {
			for (int i = 0; i < getLabelsDaysOfWeek().length; i++) {
				getLabelsDaysOfWeek()[i].setBackground(selectedBackground);
			}
			initLabels();
		}
	}
	public void setSelectedForeground(Color selectedForeground) {
		this.selectedForeground = selectedForeground;
		if (selectedForeground != null) {
			for (int i = 0; i < getLabelsDaysOfWeek().length; i++) {
				getLabelsDaysOfWeek()[i].setForeground(selectedForeground);
			}
			initLabels();
		}
	}
	public void setBackgroundToday(Color backgroundToday) {
		this.backgroundToday = backgroundToday;
		initLabels();
	}
	public void setForegroundToday(Color foregroundToday) {
		this.foregroundToday = foregroundToday;
		initLabels();
	}	
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		getComboMonths().setBackground(bg);
		getbtnUp().setBackground(bg);
		getbtnDown().setBackground(bg);
		getBtnAccept().setBackground(bg);
		getBtnToday().setBackground(bg);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		getComboMonths().setForeground(fg);
		getTextFieldYear().setForeground(fg);
		getBtnAccept().setForeground(fg);
		getBtnToday().setForeground(fg);
	}
}
