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
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import linaje.gui.Icons;
import linaje.gui.LRadioButton;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestLRadioButton {

public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			LRadioButton lRadioButton1 = new LRadioButton("LRadioButton 1");
			LRadioButton lRadioButton2 = new LRadioButton("LRadioButton 2");
			LRadioButton lRadioButton3 = new LRadioButton("LRadioButton 3");
			LRadioButton lRadioButton4 = new LRadioButton("LRadioButton 4");
			LRadioButton lRadioButton5 = new LRadioButton("LRadioButton\nmultilinea");
			LRadioButton lRadioButton6 = new LRadioButton("LRadioButton Vista");
			LRadioButton lRadioButton7 = new LRadioButton("LRadioButton Vista\nmultilinea");
			LRadioButton lRadioButton8 = new LRadioButton("LRadioButton Vista\nmultilinea\notra línea");
			LRadioButton lRadioButton9 = new LRadioButton("");
			
			//lRadioButton.setPreferredSize(new Dimension(100, lRadioButton.getPreferredSize().height));
			
			
			lRadioButton5.setOpaque(true);
			lRadioButton5.setBackground(Color.lightGray);
			lRadioButton5.setBorder(BorderFactory.createLineBorder(Color.black));
			
			lRadioButton6.setFontStyle(Font.BOLD);
			lRadioButton6.setIcon(Icons.SEARCH);
			lRadioButton7.setIcon(Icons.SEARCH);
			lRadioButton8.setIcon(Icons.SEARCH);
			
			lRadioButton6.setToggleAspect(true);
			lRadioButton7.setToggleAspect(true);
			lRadioButton8.setToggleAspect(true);
			
			JPanel panelRadioButtons = new JPanel(new FlowLayout());
			panelRadioButtons.setSize(400, 50);
	
			panelRadioButtons.add(lRadioButton1);
			panelRadioButtons.add(lRadioButton2);
			panelRadioButtons.add(lRadioButton3);
			panelRadioButtons.add(lRadioButton4);
			panelRadioButtons.add(lRadioButton5);
			panelRadioButtons.add(lRadioButton6);
			panelRadioButtons.add(lRadioButton7);
			panelRadioButtons.add(lRadioButton8);
			panelRadioButtons.add(lRadioButton9);
			
			lRadioButton1.setFontSize(10);
			lRadioButton2.setFontSize(11);
			lRadioButton3.setFontSize(12);
			lRadioButton4.setFontSize(13);
			lRadioButton5.setFontSize(14);
			//lRadioButton6.setFontSize(15);
			//lRadioButton7.setFontSize(16);
			//lRadioButton8.setFontSize(17);
			lRadioButton9.setFontSize(18);
			
			lRadioButton2.setForeground(Color.green);
			lRadioButton3.setBackground(Color.cyan);
			lRadioButton3.setOpaque(true);
			
			lRadioButton1.setHorizontalTextPosition(SwingConstants.CENTER);
			lRadioButton1.setVerticalTextPosition(SwingConstants.BOTTOM);
			lRadioButton1.setVerticalAlignment(SwingConstants.TOP);
			lRadioButton1.getButtonProperties().setMarkColor(ColorsGUI.BLUE);
			lRadioButton2.getButtonProperties().setMarkColor(ColorsGUI.RED);
			//lRadioButton3.setColorMarca(Color.blue);
			lRadioButton4.setSelected(true);
			lRadioButton4.setEnabled(false);
			
			LDialogContent.showComponentInFrame(panelRadioButtons);	
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
