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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.ui.TypographyLabel;
import linaje.gui.windows.LDialogContent;

public class TestTypogrphyLabel {

	public static void main(String[] a) {
		LinajeLookAndFeel.init();
		
		TypographyLabel typographyLabel = new TypographyLabel();
	    
	    JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.fill = GridBagConstraints.BOTH;
		gbcLabel.weightx = 1.0;
		gbcLabel.weighty = 1.0;
		
		TestPanel testPanel = new TestPanel(typographyLabel, false);
		
		testPanel.getFieldsPanel().addAccessComponentsFromFields(typographyLabel, 6);
		panel.add(testPanel, gbcLabel);
		
		LDialogContent.showComponentInFrame(panel);
	}
}
