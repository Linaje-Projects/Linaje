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

import linaje.gui.LPanel;
import linaje.gui.components.ColorButton;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestColorButton {

	public static LPanel getTestComponent() {
		
		LPanel testComponent = new LPanel(new LFlowLayout(false));
		testComponent.setSize(100, 100);
		
		ColorButton botonColor1 = new ColorButton();
		botonColor1.setSelectedColor(Color.blue);
		
		ColorButton botonColor2 = new ColorButton(false);
		botonColor2.addColor(Color.red);
		botonColor2.addColor(Color.yellow);
		botonColor2.addColor(Color.green);
		botonColor2.addColor(Color.blue);
		botonColor2.addColor(Color.black);
		botonColor2.addColor(Color.white);
		botonColor2.setSelectedColor(Color.green);
		botonColor2.setPreferredSize(new Dimension(60, 20));
		botonColor2.getLabelSelectedColor().setText("Text");
		
		testComponent.add(botonColor1);		
		testComponent.add(botonColor2);
		
		return testComponent;
	}
	
	public static void main(String[] args) {
		
		try {
			
			LinajeLookAndFeel.init();
			
			LPanel testComponent = getTestComponent();
			
			TestPanel testPanel = new TestPanel(testComponent, false);
			testPanel.getFieldsPanel().addAccessComponentsFromFields(testComponent.getComponent(0), 0);
			
			LDialogContent.showComponentInFrame(testPanel);
			
			/*LPanel panel = new LPanel(new BorderLayout());
			panel.add(new ColorButton(), BorderLayout.CENTER);
			panel.setBorder(new EmptyBorder(60, 60, 60, 60));
			LDialogContent.showComponentInFrame(panel);	*/
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
}
