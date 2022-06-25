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

import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.AuxDescriptionPanel;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;

public class TestMessageDialog {

	public static void main(String[] args) {
		try {
			
			//Iniciamos LookAndFeel y demás
			LinajeLookAndFeel.init();
			
			MessageDialog dlg = new MessageDialog();
			AuxDescriptionPanel detalle = new AuxDescriptionPanel();
			detalle.setMarginTextArea(1,1,1,1);
			detalle.setSize(400, 100);
			//dlg.setAjustarDialogoADetalle(true);
			dlg.setMessageFont(new java.awt.Font("customDialog", 0, 12));
			dlg.setDetail(detalle);
			dlg.setResizable(true);
			//dlg.setMargen(10);
			//HeaderPanel panelCabecera = new HeaderPanel("Cabecera", "Descripción de la cabecera");
			//dlg.setPanelCabecera(panelCabecera);
			dlg.show("¡¡Esto es un mensaje de error con detalle desplegable!!");
			
			MessageDialog.showMessage("Esto es un mensaje de error\nmensaje de error", MessageDialog.ICON_ERROR);
			MessageDialog.showMessage("Esto es un mensaje de aviso\nmensaje de aviso", MessageDialog.ICON_WARNING);
			MessageDialog.showMessage("Esto es un mensaje de información\nmensaje de información", MessageDialog.ICON_INFO);
			MessageDialog.showMessage("Esto es un mensaje de pregunta\nmensaje de pregunta", MessageDialog.ICON_QUESTION);
			
			
			System.exit(0);
			
		} catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
