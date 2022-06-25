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
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import linaje.gui.LButton;
import linaje.gui.TabCloseComponent;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;

public class TestTabCloseComponent {

public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			JTabbedPane tabbedPane = new JTabbedPane();
			JPanel pestana1 = new JPanel(new LFlowLayout());
			tabbedPane.addTab("Pestaña", pestana1);
			tabbedPane.addTab("Otra Pestaña", new JPanel());
			tabbedPane.addTab("Otra Pestaña", new JPanel());
			
			final TabCloseComponent tcc1 = new TabCloseComponent(tabbedPane); 
			@SuppressWarnings("serial")
			Action closeAction1 = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MessageDialog.showMessage("Cierro la pestaña", MessageDialog.ICON_INFO);
					tcc1.defaultCloseActionPerformed();
				}
			};
			tcc1.setCloseAction(closeAction1);
			
			tabbedPane.setTabComponentAt(0, tcc1);
			tabbedPane.setTabComponentAt(1, new TabCloseComponent(tabbedPane));
			tabbedPane.setTabComponentAt(2, new LButton("Otra Pestaña"));
			
			TabCloseComponent tlb = new TabCloseComponent();
			tlb.setOpaque(true);
			tlb.setBackground(Color.cyan);
			pestana1.add(tlb);
			
			LDialogContent.showComponentInFrame(tabbedPane);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
