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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.SwingConstants;
import javax.swing.Timer;

import linaje.gui.LLabel;
import linaje.gui.LPanel;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.utils.Dates;

@SuppressWarnings("serial")
public class LClock extends LPanel {

	private String formatHour = null;
	private String formatDate = null;
	
	private LLabel labelHour = null;
	private LLabel labelDate = null;
	
	private Timer timer = null;
	
	private Calendar currentDisplayedCalendar = null;
	
	public LClock() {
		super();
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setOpaque(false);
		add(getLabelHour(), BorderLayout.CENTER);
		add(getLabelDate(), BorderLayout.SOUTH);
		setFontSize(50);
		updateClock();
		getTimer().start();
	}
	
	private void formatText(Calendar calendar, boolean updateDate) {
		currentDisplayedCalendar = calendar;
		getLabelHour().setText(Dates.getFormattedDate(calendar.getTime(), getFormatHour(), false));
		getLabelDate().setText(Dates.getFormattedDate(calendar.getTime(), getFormatDate(), false));
	}
	
	public Timer getTimer() {
		if (timer == null) {
			timer = new Timer(1000, new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					updateClock();
					repaint();
				}
			});
		}
		return timer;
	}
	
	private void updateClock() {
		
		boolean updateDate = true;
		boolean updateHour = true;
		Calendar currentCalendar = Calendar.getInstance();
		if (currentDisplayedCalendar != null) {
			updateDate = currentCalendar.get(Calendar.DATE) != currentDisplayedCalendar.get(Calendar.DATE);
			if (!getFormatHour().contains("ss"))
				updateHour = currentCalendar.get(Calendar.MINUTE) != currentDisplayedCalendar.get(Calendar.MINUTE);
		}
		if (updateHour)
			formatText(currentCalendar, updateDate);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			getTimer().restart();
			formatText(Calendar.getInstance(), true);
		}
		else {
			getTimer().stop();
		}
	}
	
	public String getFormatHour() {
		if (formatHour == null)
			formatHour = Dates.FORMAT_HH_MM;
		return formatHour;
	}
	public String getFormatDate() {
		if (formatDate == null)
			formatDate = Dates.FORMAT_EEEE_DD_MMMM_YYYY;
		return formatDate;
	}
	public void setFormatHour(String formatHour) {
		this.formatHour = formatHour;
	}
	public void setFormatDate(String formatDate) {
		this.formatDate = formatDate;
	}
	private LLabel getLabelDate() {
		if (labelDate == null) {
			labelDate = new LLabel();
			labelDate.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return labelDate;
	}
	private LLabel getLabelHour() {
		if (labelHour == null) {
			labelHour = new LLabel();
			labelHour.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return labelHour;
	}
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		setForeground(fg, fg);
	}
	public void setForeground(Color fgHour, Color fgDate) {
		getLabelHour().setForeground(fgHour);
		getLabelDate().setForeground(fgDate);
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		getLabelHour().setFont(font);
		getLabelDate().setFont(font.deriveFont((float) (font.getSize()/4)));
	}
	
	public static void main(String[] args) {
		
		LinajeLookAndFeel.init();
		
		LClock clock = new LClock();
		LDialogContent.showComponentInFrame(clock);
	}
	
	public void setFontSize(float fontSize) {
		setFont(getFont().deriveFont(fontSize));
	}
}
