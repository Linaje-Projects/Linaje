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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;

import linaje.gui.components.MultiComboDates;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Dates;
import linaje.utils.Numbers;

public class TestMultiComboDates {

	public static void main(String[] args) {
		
		try {
			
			LinajeLookAndFeel.init();
			
			final MultiComboDates multiComboDates = new MultiComboDates();
			/*DATOS DE PRUEBA*/
			Calendar cal = Dates.getCalendarInstance(null);
			cal.setTime(new Date());
			Console.println("Fechas a cargar");
			Console.println("---------------------");
			for (int i = 0; i < 50; i++) {
				cal.set(Calendar.DATE, Numbers.getRandomNumberInt(1, 31));
				cal.set(Calendar.MONTH, Numbers.getRandomNumberInt(0, 11));
				cal.set(Calendar.YEAR, Numbers.getRandomNumberInt(2015, 2018));
				multiComboDates.addItem(cal.getTime());
				Console.println(Dates.getFormattedDate(cal.getTime()));
			}
			Console.println("---------------------");
			Console.println("Selected Date: " + Dates.getFormattedDate(multiComboDates.getSelectedDate()));
			
			ItemListener itemListener = new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Console.println("Selected Date: " + Dates.getFormattedDate(multiComboDates.getSelectedDate()));
					}
				}
			};
			multiComboDates.addItemListener(itemListener);
			
			LDialogContent.showComponentInFrame(multiComboDates);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
