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
import java.awt.Font;

import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LPanel;
import linaje.gui.LTabbedPane;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.StateColor;

public class TestLTabbedPane {

	public static void main(String[] args) {

		try {
			
			LinajeLookAndFeel.init();
			
			LTabbedPane tabbedPane = getTestComponent();
			TestPanel testPanel = new TestPanel(tabbedPane);
			
			testPanel.getFieldsPanel().addAccessComponentsFromFields(tabbedPane.getUI());
			testPanel.getFieldsPanel().addAccessComponentsFromFields(tabbedPane.getTabProperties(0));
			
			tabbedPane.setFont(new Font("Microsoft New Tai Lue", Font.PLAIN, 30));
			
			LDialogContent.showComponentInFrame(testPanel);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
	
	//Testing
		public static LTabbedPane getTestComponent() {

			LPanel pestana1 = new LPanel();
			LPanel pestana2 = new LPanel();
			LPanel pestana3 = new LPanel();
			LPanel pestana4 = new LPanel();
			LPanel pestana5 = new LPanel();
			LPanel pestana6 = new LPanel();
			LPanel pestana7 = new LPanel();
			LPanel pestana8 = new LPanel();
			LPanel pestana9 = new LPanel();
			LPanel pestana10 = new LPanel();
			LPanel pestana11 = new LPanel();
			LPanel pestana12 = new LPanel();
			LPanel pestana13 = new LPanel();
			
			/*pestana1.getBackground();
			pestana1.setBackground(Color.red);
			pestana2.setBackground(Color.blue);
			pestana3.setBackground(Color.yellow);
			pestana4.setBackground(Color.red);
			pestana5.setBackground(Color.blue);
			pestana6.setBackground(Color.yellow);
			pestana7.setBackground(Color.red);
			pestana8.setBackground(Color.red);
			pestana9.setBackground(Color.blue);
			pestana10.setBackground(Color.yellow);
			pestana11.setBackground(Color.red);
			pestana12.setBackground(Color.blue);
			pestana13.setBackground(Color.yellow);
			*/
			LTabbedPane tabbedPane = new LTabbedPane();
			
			tabbedPane.addTab("Pesta??a 1", pestana1);
			tabbedPane.addTab("Pesta??a 2", pestana2);
			tabbedPane.addTab("Pesta??a 3\notra linea", pestana3);
			tabbedPane.addTab("Pesta??a 4", pestana4);
			tabbedPane.addTab("Pesta??a 5", pestana5);
			tabbedPane.addTab("Pesta??a 6", pestana6);
			
			tabbedPane.setIconAt(2, Icons.ARROW_UP);
			
			//pestana1.setBackground(Color.lightGray);
			pestana2.setBackground(Color.white);
			//pestana1.setOpaque(false);
			//tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
			
			pestana1.setTextBackground("Pesta??a 1");
			pestana2.setTextBackground("Pesta??a 2");
			pestana3.setTextBackground("Pesta??a 3");
			
			//tabbedPane.getTabProperties(0).setAspect(LTabbedPane.ASPECT_BUTTON);
			tabbedPane.getTabProperties(4).setAspect(LTabbedPane.ASPECT_BUTTON);
			tabbedPane.getTabProperties(5).setAspect(LTabbedPane.ASPECT_BUTTON);
			tabbedPane.getTabProperties(4).setShowInDialog(true);
			
			tabbedPane.getTabProperties(4).setIcon(Icons.getScaledIcon(Icons.FOLDER_NEW_256x256, 50, 50));
			
			tabbedPane.getTabProperties(3).setIcon(Icons.CALENDAR_48x48);
			tabbedPane.getTabProperties(3).setIconAux(Icons.COMPUTER);
			
			//tabbedPane.setForegroundAt(1, Color.white);
			tabbedPane.setForegroundAt(1, new StateColor(Color.red, Color.blue, Color.orange));
			/*tabbedPane.addTab("Pesta??a 7 larga", pestana7);
			tabbedPane.addTab("Pesta??a 8 mas larga", pestana8);
			tabbedPane.addTab("Pesta??a 9 algo mas larga", pestana9);
			tabbedPane.addTab("Pesta??a 10", pestana10);
			tabbedPane.addTab("Pesta??a 11", pestana11);
			tabbedPane.addTab("Pesta??a 12", pestana12);
			tabbedPane.addTab("Pesta??a 13", pestana13);
			
			
			tabbedPane.getTabProperties(4).setAspect(LTabbedPane.ASPECT_LINK);
			tabbedPane.getTabProperties(5).setAspect(LTabbedPane.ASPECT_LINK);
			tabbedPane.getTabProperties(5).setShowInDialog(true);
			tabbedPane.getTabProperties(10).setAspect(LTabbedPane.ASPECT_BUTTON);
			tabbedPane.getTabProperties(11).setAspect(LTabbedPane.ASPECT_BUTTON);
			*/
			
			//tabbedPane.setTabComponentAt(1, new LButton("+"));
			tabbedPane.setTabComponentAt(2, new LButton("Button"));
			
			tabbedPane.setSize(new Dimension(1280, 800));
						
			return tabbedPane;
		}
}
