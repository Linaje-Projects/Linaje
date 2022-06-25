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
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import linaje.gui.Icons;
import linaje.gui.LToggleButton;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestLToggleButton {

	public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			LToggleButton lToggleButton = new LToggleButton("LToggleButton");
			LToggleButton lToggleButton2 = new LToggleButton("Botón Estilo 1");
			LToggleButton lToggleButton3 = new LToggleButton("Botón Estilo 2");
			LToggleButton lToggleButton4 = new LToggleButton("");
			LToggleButton lToggleButton5 = new LToggleButton("Botón\nEstilo normal");
			LToggleButton lToggleButton6 = new LToggleButton("Botón\nEstilo normal");
			LToggleButton lToggleButton7 = new LToggleButton("Botón\nEstilo normal");
			
			LToggleButton lToggleButton8 = new LToggleButton("VISTA GENERAL");
			LToggleButton lToggleButton9 = new LToggleButton("VISTA CANAL");
			LToggleButton lToggleButton10 = new LToggleButton("VISTA$$USO BACKOFFICE");
			
			LToggleButton lToggleButton11 = new LToggleButton("VISTA GENERAL");
			LToggleButton lToggleButton12 = new LToggleButton("VISTA CANAL");
			LToggleButton lToggleButton13 = new LToggleButton("VISTA$$USO BACKOFFICE");
			
			JToggleButton aJToggleButton = new JToggleButton("JToggleButton");
			
			lToggleButton4.setIcon(Icons.SEARCH);
			lToggleButton3.getButtonProperties().setLineBackgroundColor(ColorsGUI.getColorImportant());
	
			lToggleButton6.setIcon(Icons.SEARCH);
			lToggleButton7.setIcon(Icons.SEARCH);
			lToggleButton7.setHorizontalAlignment(SwingConstants.LEFT);
			
			lToggleButton13.setBorderPainted(false);
			
			lToggleButton8.setIcon(Icons.ARROW_UP);
			lToggleButton9.setIcon(Icons.ARROW_DOWN);
			lToggleButton10.setIcon(Icons.ARROW_LEFT);
			
			lToggleButton11.setIcon(Icons.ARROW_RIGHT);
			lToggleButton12.setIcon(Icons.ACCEPT);
			lToggleButton13.setIcon(Icons.CANCEL);
			
			lToggleButton10.setVerticalTextPosition(SwingConstants.BOTTOM);
			lToggleButton10.setHorizontalTextPosition(SwingConstants.CENTER);
			
			lToggleButton13.setVerticalTextPosition(SwingConstants.BOTTOM);
			lToggleButton13.setHorizontalTextPosition(SwingConstants.CENTER);
			
			lToggleButton8.setSelected(true);
			ButtonGroup bg = new ButtonGroup();
			bg.add(lToggleButton8);
			bg.add(lToggleButton9);
			bg.add(lToggleButton10);
			
			lToggleButton11.setSelected(true);
			ButtonGroup bg3 = new ButtonGroup();
			bg3.add(lToggleButton11);
			bg3.add(lToggleButton12);
			bg3.add(lToggleButton13);
			
			lToggleButton5.setSelected(true);
			ButtonGroup bg2 = new ButtonGroup();
			bg2.add(lToggleButton5);
			bg2.add(lToggleButton6);
			bg2.add(lToggleButton7);
			
			JPanel contentPane = new JPanel(new FlowLayout());
			contentPane.setSize(400, 250);
			
			contentPane.add(lToggleButton);
			contentPane.add(aJToggleButton);
			contentPane.add(lToggleButton2);
			contentPane.add(lToggleButton3);
			contentPane.add(lToggleButton4);
			contentPane.add(lToggleButton5);
			contentPane.add(lToggleButton6);
			contentPane.add(lToggleButton7);
			contentPane.add(lToggleButton8);
			contentPane.add(lToggleButton9);
			contentPane.add(lToggleButton10);
			contentPane.add(lToggleButton11);
			contentPane.add(lToggleButton12);
			contentPane.add(lToggleButton13);
			
			lToggleButton.setForeground(Color.blue);
					
			LDialogContent.showComponentInFrame(contentPane);	
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
