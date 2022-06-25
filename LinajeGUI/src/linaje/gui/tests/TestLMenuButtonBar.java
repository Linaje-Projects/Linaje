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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import linaje.gui.LMenuButtonBar;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.utils.Lists;

public class TestLMenuButtonBar {

	/**
	 * TEST
	 */
	public static void main(String[] args) {
		
		LinajeLookAndFeel.init();
		
		LMenuButtonBar lMenuBar = new LMenuButtonBar();
		
		lMenuBar.addElement("ELEMENTO1");
		List<String> groupedElements = Lists.newList();
		groupedElements.add("ELEMENTO2");
		groupedElements.add("ELEMENTO3 MEDIO");
		groupedElements.add("ELEMENTO4 MAS LARGO");
		lMenuBar.addElement(groupedElements);
		lMenuBar.addElement("ELEMENTO5");
		lMenuBar.addElement("ELEMENTO6");
		
		lMenuBar.setSelectedIndex(0);
				
		LDialogContent dialogo = new LDialogContent();
		dialogo.setLayout(new BorderLayout());
		dialogo.setSize(new Dimension(380, 100));
		dialogo.setResizable(true);
		dialogo.add(lMenuBar, BorderLayout.CENTER);
		
		dialogo.showInFrame();
	}
}
