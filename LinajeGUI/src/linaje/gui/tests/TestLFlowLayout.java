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
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

import linaje.gui.LCombo;
import linaje.gui.LPanel;
import linaje.gui.components.LabelCombo;
import linaje.gui.components.LabelComponent;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.LDialogContent;

public class TestLFlowLayout {

public static void main(String[] args) {
		
		try {

			LinajeLookAndFeel.init();
			
			LPanel panel1 = new LPanel();			
			panel1.setBackground(Color.blue);
			panel1.setPreferredSize(new Dimension(30, 30));
			
			LPanel panel2 = new LPanel();
			panel2.setBackground(Color.blue);
			panel2.setPreferredSize(new Dimension(20, 20));
			
			LPanel panel3 = new LPanel();
			panel3.setBackground(Color.blue);
			panel3.setPreferredSize(new Dimension(10, 10));
			
			LPanel panel4 = new LPanel();
			panel4.setBackground(Color.blue);
			panel4.setPreferredSize(new Dimension(40, 40));
			
			final LFlowLayout lFlowLayout = new LFlowLayout(FlowLayout.CENTER, 5, 5);
			//lFlowLayout.setAlignOnBaseline(true);
			
			final LPanel panelCenter = new LPanel();
			panelCenter.setLayout(lFlowLayout);
			panelCenter.setBackground(Color.gray);
			
			panelCenter.add(panel1);
			panelCenter.add(panel2);
			panelCenter.add(panel3);
			panelCenter.add(panel4);
			
			LabelCombo<String> lblComboAVI = new LabelCombo<String>("Alineación Vertical");
			lblComboAVI.setOrientation(LabelComponent.VERTICAL);
			lblComboAVI.setLineColor(ColorsGUI.getColorApp());
						
			final LCombo<String> comboAVI = lblComboAVI.getCombo();
			comboAVI.addItem("TOP");
			comboAVI.addItem("CENTER");
			comboAVI.addItem("BOTTOM");
			comboAVI.setSelectedIndex(1);
			
			LabelCombo<String> lblComboAVG = new LabelCombo<String>("A. Vertical Global");
			lblComboAVG.setOrientation(LabelComponent.VERTICAL);
			lblComboAVG.setLineColor(ColorsGUI.getColorApp());
			
			final LCombo<String> comboAVG = lblComboAVG.getCombo();
			comboAVG.addItem("TOP");
			comboAVG.addItem("CENTER");
			comboAVG.addItem("BOTTOM");
			comboAVG.setSelectedIndex(1);
			
			LabelCombo<String> lblComboAH = new LabelCombo<String>("A. Horizontal");
			lblComboAH.setOrientation(LabelComponent.VERTICAL);
			lblComboAH.setLineColor(ColorsGUI.getColorApp());
			
			final LCombo<String> comboAH = lblComboAH.getCombo();
			comboAH.addItem("LEFT");
			comboAH.addItem("CENTER");
			comboAH.addItem("RIGHT");
			comboAH.setSelectedIndex(1);
			
			ItemListener itemListener = new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						
						if (e.getSource() == comboAVI) {
							if (comboAVI.getSelectedIndex() == 1)
								lFlowLayout.setVerticalAlignment(SwingConstants.CENTER);
							else if (comboAVI.getSelectedIndex() == 2)
								lFlowLayout.setVerticalAlignment(SwingConstants.BOTTOM);
							else
								lFlowLayout.setVerticalAlignment(SwingConstants.TOP);
						}
						else if (e.getSource() == comboAVG) {
							if (comboAVG.getSelectedIndex() == 1)
								lFlowLayout.setVerticalAlignmentGlobal(SwingConstants.CENTER);
							else if (comboAVG.getSelectedIndex() == 2)
								lFlowLayout.setVerticalAlignmentGlobal(SwingConstants.BOTTOM);
							else
								lFlowLayout.setVerticalAlignmentGlobal(SwingConstants.TOP);
						}
						else if (e.getSource() == comboAH) {
							if (comboAH.getSelectedIndex() == 1)
								lFlowLayout.setHorizontalAlignment(SwingConstants.CENTER);
							else if (comboAH.getSelectedIndex() == 2)
								lFlowLayout.setHorizontalAlignment(SwingConstants.RIGHT);
							else
								lFlowLayout.setHorizontalAlignment(SwingConstants.LEFT);
						}
						panelCenter.validate();
						panelCenter.revalidate();
						panelCenter.repaint();
					}
				}
			};
			
			comboAVI.addItemListener(itemListener);
			comboAVG.addItemListener(itemListener);
			comboAH.addItemListener(itemListener);
			
			LPanel panelCombos = new LPanel(new VerticalBagLayout());
			panelCombos.add(lblComboAVI);
			panelCombos.add(lblComboAVG);
			panelCombos.add(lblComboAH);
			panelCombos.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
			
			LDialogContent dialogContent = new LDialogContent();
			dialogContent.setLayout(new BorderLayout());
			dialogContent.setSize(400, 200);
			//dialogo.setButtonsPanel(new ButtonsPanel(ButtonsPanel.ASPECT_VOID));
			dialogContent.setMargin(10);
			dialogContent.add(panelCenter, BorderLayout.CENTER);
			dialogContent.add(panelCombos, BorderLayout.EAST);
			dialogContent.setResizable(true);
			dialogContent.getPreferredSize();
			dialogContent.showInFrame();
		}
		catch (Throwable exception) {
			exception.printStackTrace();
		}
	}
}
