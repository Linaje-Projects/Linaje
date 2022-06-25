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

import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JTextField;

import linaje.gui.LTextField;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Dates;
import linaje.utils.FormattedData;

public class TestLTextField {

	public static void main(String[] args) {	
		try {
	
			LinajeLookAndFeel.init();
			
			LTextField lTextField = new LTextField();
			LTextField lTextField2 = new LTextField();
			LTextField lTextField3 = new LTextField();
			JTextField aTextField = new JTextField("Hola");
			
			lTextField2.setValidateState(LTextField.VALIDATE_INPROGRESS);
			lTextField2.setValidateState(LTextField.VALIDATE_YES);
			lTextField3.setValidateState(LTextField.VALIDATE_NO);
			lTextField3.setCompatibleWindowsFiles(true);
	
			lTextField2.setType(FormattedData.TYPE_DATE);
			lTextField3.setType(FormattedData.TYPE_NUMBER);
			
			//lTextField.setPreferredSize(new Dimension(100, 21));
			//lTextField2.setPreferredSize(new Dimension(100, 21));
			//lTextField3.setPreferredSize(new Dimension(100, 21));
			
			
			lTextField2.getFormattedData().setPeriod(Dates.PERIOD_MONTHLY);
			
			lTextField3.getFormattedData().setPostfix(" %");
			lTextField3.getFormattedData().setDecimals(3);
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, 11);
			
			lTextField.setValue(cal);
			lTextField2.setValue(cal);
			lTextField3.setValue(123.45);
				
			JPanel contentPane = new JPanel(new FlowLayout());
			contentPane.setSize(300, 50);
			
			contentPane.add(lTextField);
			contentPane.add(lTextField2);
			contentPane.add(lTextField3);
			contentPane.add(aTextField);
			
			LDialogContent.showComponentInFrame(contentPane);
			
		} catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
