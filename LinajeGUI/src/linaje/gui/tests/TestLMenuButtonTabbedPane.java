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
import java.awt.Color;
import java.awt.Dimension;

import linaje.gui.LMenuButtonTabbedPane;
import linaje.gui.LPanel;
import linaje.gui.LTabbedPane;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestLMenuButtonTabbedPane {

public static void main(String[] args) {
		
		try {
			
			LinajeLookAndFeel.init();
			
			LPanel pestana1 = new LPanel();
			pestana1.setBackground(Color.red);
			LPanel pestana2 = new LPanel();
			pestana2.setBackground(Color.blue);
			LPanel pestana3 = new LPanel();
			pestana3.setBackground(Color.yellow);
			LPanel pestana31 = new LPanel();
			pestana31.setBackground(Color.red);
			LPanel pestana32 = new LPanel();
			pestana32.setBackground(Color.blue);
			LPanel pestana33 = new LPanel();
			pestana33.setBackground(Color.yellow);
			LPanel pestana4 = new LPanel();
			pestana4.setBackground(Color.red);
			LPanel pestana5 = new LPanel();
			pestana5.setBackground(Color.blue);
			LPanel pestana6 = new LPanel();
			pestana6.setBackground(Color.yellow);
			LPanel pestana61 = new LPanel();
			pestana61.setBackground(Color.red);
			LPanel pestana62 = new LPanel();
			pestana62.setBackground(Color.blue);
			LPanel pestana63 = new LPanel();
			pestana63.setBackground(Color.yellow);
			LPanel pestana7 = new LPanel();
			pestana7.setBackground(Color.red);
			
			LTabbedPane tabbedPane = new LTabbedPane();
			tabbedPane.addTab("Pestaña 1", pestana1);
			tabbedPane.addTab("Pestaña 2", pestana2);
			tabbedPane.addTab("Pestaña 3", pestana3);
			tabbedPane.addTab("Pestaña A. 31", pestana31);
			tabbedPane.addTab("Pestaña Agr. 32", pestana32);
			tabbedPane.addTab("Pestaña Agruapda 33", pestana33);
			tabbedPane.addTab("Pestaña 4", pestana4);
			tabbedPane.addTab("Pestaña 5", pestana5);
			tabbedPane.addTab("Pestaña 6", pestana6);
			tabbedPane.addTab("Pestaña A. 61", pestana61);
			tabbedPane.addTab("Pestaña Agr. 62", pestana62);
			tabbedPane.addTab("Pestaña Agruapda 63", pestana63);
			tabbedPane.addTab("Pestaña 7", pestana7);
			
			tabbedPane.getTabProperties(4).setGrouped(true);
			tabbedPane.getTabProperties(5).setGrouped(true);
			tabbedPane.getTabProperties(10).setGrouped(true);
			tabbedPane.getTabProperties(11).setGrouped(true);
			
			//BUG: REVISAR QUE NO ESTÁN FUNCIONANDO BIEN LOS ELEMENTOS FIJOS
			//tabbedPane.getTabProperties(12).setAspect(LTabbedPane.ASPECT_LINK);
			//tabbedPane.getTabProperties(1).setAspect(LTabbedPane.ASPECT_LINK);
			
			LMenuButtonTabbedPane lMenuTabbedPane = new LMenuButtonTabbedPane();
			lMenuTabbedPane.setTabbedPane(tabbedPane);
			
			LPanel panelPrincipal = new LPanel(new BorderLayout());
			panelPrincipal.add(lMenuTabbedPane, BorderLayout.NORTH);
			panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
			
			lMenuTabbedPane.getPanelFixedElements().setBackground(Color.blue);
			
			LDialogContent dialogo = new LDialogContent();
			dialogo.setLayout(new BorderLayout());
			dialogo.setSize(new Dimension(800, 400));
			dialogo.setResizable(true);
			dialogo.add(panelPrincipal, BorderLayout.CENTER);
			
			dialogo.showInFrame();
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
