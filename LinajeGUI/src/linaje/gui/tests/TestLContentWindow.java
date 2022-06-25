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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import linaje.gui.LButton;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialog;
import linaje.gui.windows.LDialogContent;
import linaje.utils.Colors;
import linaje.utils.StateColor;

public class TestLContentWindow {

	public static void main(String[] args) {
		
		LinajeLookAndFeel.init();
		
		LDialogContent lDialogContent = new LDialogContent();
		lDialogContent.setLayout(new LFlowLayout());
		lDialogContent.setOpaque(false);
		lDialogContent.setSize(new Dimension(200, 200));
		
		
		ButtonsPanel pb = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
		pb.setAutoCloseOnAccept(true);
		lDialogContent.setButtonsPanel(pb);
		
		LButton button = new LButton("Close me...");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		lDialogContent.add(button);
		
		LDialog lDialog = lDialogContent.getDefaultDialog();//new LDialog(AplicacionGUI.getAplicacionGUIActual().getFrame(), true);
		lDialog.setTitle("Titulo dialogo");
		//tdialog.setTransparency(0);
		lDialog.setBackgroundContent(Colors.darker(Color.blue, 0.5));
		lDialog.setTransparency(0f);
		lDialog.setUseContentWindowWhenOpaque(true);
		lDialog.getDefaultRoundedBorder().setLineBorderColor(Color.magenta);
		StateColor foreground = new StateColor(ColorsGUI.getColorTextBrightest(), null, null, ColorsGUI.getColorApp(), null, null, null, null);
		lDialog.setForeground(foreground);
		
		lDialogContent.setDialog(lDialog);
		lDialogContent.showInDialog();

		System.exit(0);
	}
}
