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

import java.awt.Color;
import java.awt.Dimension;
import java.util.Date;
import java.util.GregorianCalendar;

import linaje.gui.LCombo;
import linaje.gui.LPanel;
import linaje.gui.cells.DataCell;
import linaje.gui.components.ComboMultiAspect;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Dates;
import linaje.utils.FormattedData;

public class TestComboMultiAspect {

public static void main(String[] args) {
		
		try {
			
			LinajeLookAndFeel.init();
			
			LPanel testComponent = getTestComponent();
			
			ComboMultiAspect aComboMultiAspecto;
			aComboMultiAspecto = new ComboMultiAspect();
			aComboMultiAspecto.setAspect(ComboMultiAspect.ASPECT_DEFAULT);
			
			//Datos de prueba
			aComboMultiAspecto.addItem(new DataCell("001", "Elem1 sdf sd f", 1));
			aComboMultiAspecto.addItem(new DataCell("002", "Elem2 fdsf", 1));
			aComboMultiAspecto.addItem(new DataCell("003", "Elem3 dsff sd", 2));
			aComboMultiAspecto.addItem(new DataCell("004", "Elem4 dfsf   dsf", 2));
			aComboMultiAspecto.addItem(new DataCell("005", "Elem1", 1));
			aComboMultiAspecto.addItem(new DataCell("006", "Elem2", 1));
			aComboMultiAspecto.addItem(new DataCell("007", "Elem3", 2));
			aComboMultiAspecto.addItem(new DataCell("008", "Elem4", 2));
			aComboMultiAspecto.addItem(new DataCell("009", "Elem1", 1));
			aComboMultiAspecto.addItem(new DataCell("010", "Elem2", 1));
			aComboMultiAspecto.addItem(new DataCell("011", "Elem3", 2));
			aComboMultiAspecto.addItem(new DataCell("012", "Elem4", 2));
			aComboMultiAspecto.addItem(new DataCell("013", "Elem1", 1));
			aComboMultiAspecto.addItem(new DataCell("014", "Elem2", 1));
			aComboMultiAspecto.addItem(new DataCell("015", "Elem3", 2));
			aComboMultiAspecto.addItem(new DataCell("016", "Elem4", 2));
			
			testComponent.add(aComboMultiAspecto);
			
			TestPanel testPanel = new TestPanel(testComponent, false);
			testPanel.getFieldsPanel().addAccessComponentsFromFields(aComboMultiAspecto);
			
			LDialogContent.showComponentInFrame(testPanel);			
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public static LPanel getTestComponent() {
		
		ComboMultiAspect aComboMultiAspecto;
		aComboMultiAspecto = new ComboMultiAspect();
		aComboMultiAspecto.setAspect(ComboMultiAspect.ASPECT_DEFAULT);
		
		/*aComboMultiAspecto.getPanelRadioButton().setPreferredSize(aComboMultiAspecto.getPreferredSize());
		aComboMultiAspecto.getComboBotonTabla().setPreferredSize(aComboMultiAspecto.getPreferredSize());
		aComboMultiAspecto.getMultiComboFechas().setPreferredSize(aComboMultiAspecto.getPreferredSize());
		aComboMultiAspecto.getTextField().setPreferredSize(aComboMultiAspecto.getPreferredSize());
		
		panel.add(aComboMultiAspecto.getPanelRadioButton());
		panel.add(aComboMultiAspecto.getComboBotonTabla());
		panel.add(aComboMultiAspecto.getMultiComboFechas());
		panel.add(aComboMultiAspecto.getTextField());
		*/

		//Datos de prueba
		aComboMultiAspecto.addItem(new DataCell("001", "Elem1", 1));
		aComboMultiAspecto.addItem(new DataCell("002", "Elem2", 1));
		aComboMultiAspecto.addItem(new DataCell("003", "Elem3", 2));
		aComboMultiAspecto.addItem(new DataCell("004", "Elem4", 2));
		
		//aComboMultiAspecto.setAspectoCombo(ASPECTO_TEXTFIELD);
	/**/
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2004, 2, 20, 0, 0, 0).getTime(), "Elem1", 1));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2004, 2, 21, 0, 0, 0).getTime(), "Elem2", 1));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2004, 2, 22, 0, 0, 0).getTime(), "Elem3", 2));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2004, 2, 23, 0, 0, 0).getTime(), "Elem4", 2));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2003, 3, 10, 0, 0, 0).getTime(), "Elem1", 1));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2003, 3, 11, 0, 0, 0).getTime(), "Elem2", 1));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2003, 3, 12, 0, 0, 0).getTime(), "Elem3", 2));
		aComboMultiAspecto.addItem(new DataCell(new GregorianCalendar(2003, 3, 13, 0, 0, 0).getTime(), "Elem4", 2));
		
		aComboMultiAspecto.setPeriod(Dates.PERIOD_MONTHLY);
		aComboMultiAspecto.setPreferredSize(new Dimension(200, aComboMultiAspecto.getPreferredSize().height));
		aComboMultiAspecto.setSelectedIndex(2);
		aComboMultiAspecto.setAspect(ComboMultiAspect.ASPECT_BUTTON_TABLE);
		//aComboMultiAspecto.setAspectoVisualizado(ASPECT_TABLE);
		
		
		LCombo<String> lCombo = new LCombo<>();
		lCombo.addItem("Soicitudes alta");
		lCombo.addItem("Stock");
		lCombo.addItem("TPVs virtuales");
		lCombo.addItem("Elemento largo largo largo largo");
		lCombo.setPreferredSize(new Dimension(100, lCombo.getPreferredSize().height));
		
		ComboMultiAspect aComboMultiAspecto2 = new ComboMultiAspect();
		//Datos de prueba
		aComboMultiAspecto2.addItem(new DataCell("001", "Elem1", 1));
		aComboMultiAspecto2.addItem(new DataCell("002", "Elem2", 1));
		aComboMultiAspecto2.addItem(new DataCell("003", "Elem3", 2));
		aComboMultiAspecto2.addItem(new DataCell("004", "Elem4", 2));
		aComboMultiAspecto2.setAspect(ComboMultiAspect.ASPECT_BUTTON_TABLE);
		
		ComboMultiAspect aComboMultiAspecto3 = new ComboMultiAspect();
		//Datos de prueba
		aComboMultiAspecto3.addItem(new DataCell("001", "Elem1", 1));
		aComboMultiAspecto3.addItem(new DataCell("002", "Elem2", 1));
		aComboMultiAspecto3.addItem(new DataCell("003", "Elem3", 2));
		aComboMultiAspecto3.addItem(new DataCell("004", "Elem4", 2));
		aComboMultiAspecto3.setAspect(ComboMultiAspect.ASPECT_TOGGLE);
		
		ComboMultiAspect aComboMultiAspecto4 = new ComboMultiAspect();
		aComboMultiAspecto4.setEnabled(true);
		aComboMultiAspecto4.setAspect(ComboMultiAspect.ASPECT_TEXTFIELD_CONTAINER);
		aComboMultiAspecto4.setSelectedItem(new DataCell("hola", "hola"));
		
		ComboMultiAspect aComboMultiAspecto5 = new ComboMultiAspect();
		aComboMultiAspecto5.setEnabled(true);
		aComboMultiAspecto5.setAspect(ComboMultiAspect.ASPECT_TEXTFIELD_CONTAINER);
		aComboMultiAspecto5.getFormattedData().setType(FormattedData.TYPE_NUMBER);
		aComboMultiAspecto5.getFormattedData().setDecimals(2);
		aComboMultiAspecto5.setSelectedItem(new DataCell("45", "45"));
		
		
		ComboMultiAspect aComboMultiAspecto6 = new ComboMultiAspect();
		aComboMultiAspecto6.setEnabled(true);
		aComboMultiAspecto6.setAspect(ComboMultiAspect.ASPECT_TEXTFIELD_CONTAINER);
		aComboMultiAspecto6.getFormattedData().setType(FormattedData.TYPE_DATE);
		aComboMultiAspecto6.setSelectedItem(new DataCell(new Date(), new Date()));
		
		ComboMultiAspect aComboMultiAspecto7 = new ComboMultiAspect();
		aComboMultiAspecto7.setEnabled(true);
		aComboMultiAspecto7.setAspect(ComboMultiAspect.ASPECT_TEXTFIELD_CONTAINER);
		aComboMultiAspecto7.getFormattedData().setType(FormattedData.TYPE_COLOR);
		aComboMultiAspecto7.setSelectedItem(new DataCell(Color.blue, Color.blue));
		
		
		LPanel testComponent = new LPanel(new LFlowLayout());
		testComponent.setSize(600, 400);
		testComponent.add(aComboMultiAspecto);
		testComponent.add(aComboMultiAspecto2);
		testComponent.add(aComboMultiAspecto3);
		testComponent.add(aComboMultiAspecto4);
		testComponent.add(aComboMultiAspecto5);
		testComponent.add(aComboMultiAspecto6);
		testComponent.add(aComboMultiAspecto7);
		
		return testComponent;
	}
}
