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

import java.awt.Dimension;
import java.util.Date;

import linaje.gui.cells.DataCell;
import linaje.gui.components.ComboMultiSelect;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Lists;

public class TestComboMultiSelect {

	public static void main(String[] args) {
		
		try {

			LinajeLookAndFeel.init();
			
			ComboMultiSelect<Object> aComboMultiSelect = new ComboMultiSelect<Object>();
			aComboMultiSelect.addItem("Solicitudes alta");
			aComboMultiSelect.addItem("Stock");
			aComboMultiSelect.addItem("Formularios de baja");
			aComboMultiSelect.addItem("Elemento largo largo largo largo sd fdsf sdf sdf asdf largo");
			aComboMultiSelect.addItem("Otro elemento");
			aComboMultiSelect.addItem("Otro mas");
			aComboMultiSelect.addItem("Otro mas 2");
			aComboMultiSelect.addItem("Otro mas 3");
			//aComboMultiSelect.setEnabled(false);
			Date date = new Date();
			aComboMultiSelect.addItem(new DataCell(date, date));
			
			//aComboMultiSelect.setSelectedIndex(2);
			aComboMultiSelect.setSelectedIndices(Lists.newArrayInt(1,3,4));
			aComboMultiSelect.setPreferredSize(new Dimension(250, aComboMultiSelect.getPreferredSize().height));
			
			LDialogContent dialog = new LDialogContent();
			dialog.setSize(600, 400);
			dialog.add(aComboMultiSelect);
			dialog.showInFrame();
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
