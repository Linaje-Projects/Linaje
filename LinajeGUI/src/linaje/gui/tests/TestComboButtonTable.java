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

import linaje.gui.LCombo;
import linaje.gui.cells.DataCell;
import linaje.gui.components.ComboButtonTable;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestComboButtonTable {

	public static void main(String[] args) {	
		try {
			
			LinajeLookAndFeel.init();
			
			LCombo<DataCell> combo = new LCombo<>();
			combo.addItem(new DataCell("001", "Elem1", 1));
			combo.addItem(new DataCell("002", "Elem2", 1));
			combo.addItem(new DataCell("003", "Elem3", 2));
			combo.addItem(new DataCell("004", "Elem4", 2));
			
			ComboButtonTable<DataCell> comboButtonTable = new ComboButtonTable<>(combo);
			
			LDialogContent.showComponentInFrame(comboButtonTable);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}

	
}
