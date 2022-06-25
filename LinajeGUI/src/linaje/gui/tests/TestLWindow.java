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
import java.awt.FlowLayout;
import java.awt.Point;

import linaje.gui.components.LabelCombo;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LWindow;

public class TestLWindow {

	public static void main(String[] args) {
		
//		General.iniciarWindowsClassicLookAndFeel();
		LinajeLookAndFeel.init();
		
		LWindow mw = new LWindow();
		mw.setWindowLocation(new Point(150, 150));
		mw.setSize(new Dimension(300, 50));
		LabelCombo<String> lCombo = new LabelCombo<String>();
		lCombo.getCombo().addItem("Solicitudes alta");
		lCombo.getCombo().addItem("Stock");
		lCombo.getCombo().addItem("TPVs virtuales");
		lCombo.getCombo().addItem("Elemento largo largo largo largo");
		//lCombo.setBorder(new DropShadowBorder());
		//lCombo.setPreferredSize(new Dimension(100, lCombo.getPreferredSize().height));
		
		mw.setLayout(new FlowLayout());
		mw.add(lCombo);
		//mw.setBackground(ColoresUsabilidad.CAOBA_CLARO);
		//mw.getBordeVentana().setColorBorde(ColoresUsabilidad.LIMA_OSCURO);
		//mw.getBordeVentana().setTamanoCurvaEsquinas(new Dimension(12, 12));
		mw.showWindow();
		
		LWindow mw2 = new LWindow();
		mw2.setWindowLocation(new Point(655, 150));
		mw2.setSize(new Dimension(500, 100));
		LabelCombo<String> lCombo2 = new LabelCombo<String>();
		lCombo2.getCombo().addItem("Solicitudes alta");
		lCombo2.getCombo().addItem("Stock");
		lCombo2.getCombo().addItem("TPVs virtuales");
		lCombo2.getCombo().addItem("Elemento largo largo largo largo");
		//lCombo.setPreferredSize(new Dimension(100, lCombo.getPreferredSize().height));
		
		mw2.setLayout(new FlowLayout());
		mw2.add(lCombo2);
		UtilsGUI.setWindowOpaque(mw2.getWindow(), false);
		
		//mw.setBackground(Color.red);
		mw2.showWindow();
	}
}
