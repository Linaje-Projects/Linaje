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

import javax.swing.JScrollPane;
import javax.swing.JTable;

import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestJTable {

public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			String data[][] = { { "101", "Amit", "670000" }, { "102", "Jai", "780000" },
					{ "101", "Sachin", "700000" } };
			String columns[] = { "ID", "NAME", "SALARY" };
			JTable table = new JTable(data, columns);
			JScrollPane sp = new JScrollPane(table);
			
			LDialogContent.showComponentInFrame(sp);	
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
