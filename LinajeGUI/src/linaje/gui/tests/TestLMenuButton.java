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
import java.util.List;

import javax.swing.ButtonGroup;

import linaje.gui.LMenuButton;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.utils.Lists;

public class TestLMenuButton {

	public static void main(String[] args) {

		LinajeLookAndFeel.init();
		
		List<String> elements = Lists.newList();
		elements.add("Elemento 1");
		elements.add("Elemento medio 2");
		elements.add("Elemento largo largo 3");
		LMenuButton lMenuItem = new LMenuButton(elements);
		LMenuButton lMenuItem2 = new LMenuButton("Elemento sin hijos");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(lMenuItem);
		bg.add(lMenuItem2);
		
		LDialogContent dialogo = new LDialogContent();
		dialogo.setSize(new Dimension(300, 50));
		dialogo.setResizable(true);
		dialogo.add(lMenuItem);
		dialogo.add(lMenuItem2);
		
		dialogo.showInFrame();
	}
}
