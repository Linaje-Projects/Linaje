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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import linaje.gui.LList;
import linaje.gui.LPanel;
import linaje.gui.components.LTextFieldContainer;
import linaje.gui.components.LabelComponent;
import linaje.gui.components.LabelTextField;
import linaje.gui.components.SpinButton;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.statics.Constants;
import linaje.utils.FormattedData;
import linaje.utils.Lists;
import linaje.utils.Numbers;

public class TestLList {

public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			final LList<String> lista = new LList<String>();
			LCellRenderer<String> render = new LCellRenderer<String>();
			/*render.setForeground(ColorsGUI.COLOR_FUENTE_PESTANA_WEB);
			render.setForegroundCodigo(ColorsGUI.GRIS4_OSCURO_BOTON);
			render.setSelectedForeground(Color.white);
			render.setSelectedForegroundCodigo(Color.white);
			render.setSelectedBackground(ColorsGUI.LIMA_OSCURO);-*/
			//render.setSelectedBorderColor(null);
			render.setSelectedBackgroundGradient(false);
			render.setShowCodesAlways(true);
			render.setSwapCodeDesc(true);
			render.setIndentEnabled(false);
			//render.setIndentacion("6");
			
			lista.setCellRenderer(render);
			//JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			//lista.setScrollMultiColumnVertical(true);
			JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setViewportView(lista);
			
			final LPanel panelCentro = new LPanel(new BorderLayout());			
			panelCentro.add(scrollPane, BorderLayout.CENTER);
			
			LabelTextField lblTxtElementos = new LabelTextField("Elementos", LabelComponent.HORIZONTAL, -1);
			lblTxtElementos.setAutoSizeLabel(true);
			lblTxtElementos.setSpinNumericsVisible(true);
			lblTxtElementos.setType(FormattedData.TYPE_NUMBER);
			lblTxtElementos.getTextField().setMaxValue(1000);
			final SpinButton spinElementos = lblTxtElementos.getLTextFieldContainer().getSpinButton();
			spinElementos.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					
					List<String> elementos = Lists.newList();
					int numElementos = spinElementos.getValue();
					for (int i = 0; i < numElementos; i++) {
						String num = Numbers.formatWithZeros(i+1, 3);
						elementos.add("Descripción del elemento#" + num);
					}
					elementos.add(Constants.NON_EXIST_ELEMENT+"#"+Numbers.formatWithZeros(numElementos+1, 3));
					elementos.add("Descripción del elemento#" + Constants.NON_EXIST_ELEMENT);
					
					lista.setElements(elementos);
				}
			});
			
			LTextFieldContainer contTxtColumnas = new LTextFieldContainer();
			contTxtColumnas.setSpinNumericsVisible(true);
			contTxtColumnas.getLTextField().setType(FormattedData.TYPE_NUMBER);
			contTxtColumnas.getLTextField().setMaxValue(1000);
			
			LabelComponent lblCompColumnas = new LabelComponent();
			lblCompColumnas.setTextLabel("Columnas");
			lblCompColumnas.setComponent(contTxtColumnas);
			
			final SpinButton spinColumnas = contTxtColumnas.getSpinButton();
			spinColumnas.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
	
					int numColumnas = spinColumnas.getValue();
					lista.setNumberOfColumns(numColumnas);
				}
			});
			
			spinElementos.setValue(51);
			spinColumnas.setValue(4);
			
			spinElementos.increment(false);
			spinColumnas.increment(false);
			
			LPanel panelCombos = new LPanel(new LFlowLayout(SwingConstants.CENTER, SwingConstants.TOP, 5, 1, true));
			panelCombos.add(lblTxtElementos);
			panelCombos.add(lblCompColumnas);
			
			LDialogContent dialogo = new LDialogContent();
			dialogo.setLayout(new BorderLayout());
			dialogo.setSize(400, 200);
			dialogo.add(panelCentro, BorderLayout.CENTER);
			dialogo.add(panelCombos, BorderLayout.EAST);
			dialogo.setResizable(true);
			dialogo.showInFrame();
		}
		catch (Throwable exception) {
			exception.printStackTrace();
		}
	}
}
