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

import java.util.List;

import linaje.gui.components.SearchPanel;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Lists;

public class TestSearchPanel {

	public static void main(String[] args) {

		try {

			LinajeLookAndFeel.init();
			
			SearchPanel<String> searchPanel = new SearchPanel<String>();
			
			List<String> searchElements = Lists.newList();
			for (int i = 0; i < 50; i++) {
				searchElements.add("Elemento " + i);
			}
			searchPanel.setSearchElements(searchElements);
			
			LDialogContent dialog = new LDialogContent();
			dialog.setSize(600, 400);
			dialog.add(searchPanel);
			dialog.showInFrame();
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
