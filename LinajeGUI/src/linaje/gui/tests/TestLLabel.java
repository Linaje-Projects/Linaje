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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import linaje.gui.Icons;
import linaje.gui.LLabel;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestLLabel {

	public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			LLabel lLabel = new LLabel("Texto label\notra linea");
			lLabel.setUnderlined(true);
			JLabel jLabel = new JLabel("Texto label\notra linea");
			jLabel.setIcon(Icons.ACCEPT);
			jLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			jLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			//JLabel jLabel2 = new JLabel("Texto label\notra linea");
			JLabel jLabel2 = new JLabel("Texto label");
			jLabel2.setVerticalTextPosition(SwingConstants.BOTTOM);
			jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel2.setVerticalAlignment(SwingConstants.BOTTOM);
			jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
			
			JButton jButton = new JButton("Texto botón\notra linea");
			//jButton.setFont(jLabel.getFont());
			jButton.setBorder(BorderFactory.createEmptyBorder());
			jButton.setMargin(new Insets(0, 0, 0, 0));
			jButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			jButton.setHorizontalTextPosition(SwingConstants.CENTER);
			jButton.setVerticalAlignment(SwingConstants.BOTTOM);
			jButton.setHorizontalAlignment(SwingConstants.CENTER);
			
			JPanel panel = new JPanel(new GridBagLayout());
			
			GridBagConstraints gbcLabel = new GridBagConstraints();
			gbcLabel.fill = GridBagConstraints.BOTH;
			gbcLabel.weightx = 1.0;
			gbcLabel.weighty = 1.0;
			
			TestPanel testPanelLabel = new TestPanel(lLabel);
			//TestPanel testPanelLabel = new TestPanel(jLabel);
			//TestPanel testPanelLabel2 = new TestPanel(jLabel2);
			TestPanel testPanelLabel2 = new TestPanel(jLabel);
			TestPanel testPanelButton = new TestPanel(jButton);
			
			testPanelLabel2.setOpaque(true);
			testPanelLabel2.setBackground(Color.lightGray);
			
			
			panel.add(testPanelLabel, gbcLabel);
			panel.add(testPanelLabel2, gbcLabel);
			panel.add(testPanelButton, gbcLabel);
			
			LDialogContent.showComponentInFrame(panel);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
