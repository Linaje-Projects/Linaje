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

import javax.swing.JList;

import linaje.gui.AppGUI;
import linaje.gui.cells.DataCell;
import linaje.gui.cells.LabelCell;
import linaje.gui.components.LabelCombo;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

public class TestLabelCombo {

@SuppressWarnings("serial")
public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			/*LabelCombo<String> labelCombo1 = new LabelCombo<String>("Combo");
			labelCombo1.addItem("Elemento 1");
			labelCombo1.addItem("Elemento 2");
			labelCombo1.addItem("Elemento 3");
			*/
			DataCell dc1 = new DataCell("001", "Elemento 1");
			DataCell dc2 = new DataCell("002", "Elemento 2", 2);
			DataCell dc3 = new DataCell("003", "Elemento 3", 2);
			DataCell dc4 = new DataCell("004", "Elemento 4");
			dc3.setEnabled(false);
			
			LabelCombo<DataCell> labelCombo = new LabelCombo<DataCell>("Combo");
			labelCombo.addItem(dc1);
			labelCombo.addItem(dc2);
			labelCombo.addItem(dc3);
			labelCombo.addItem(dc4);
			
			labelCombo.getCombo().setRenderer(new LCellRenderer<DataCell>() {
				@Override
				public LabelCell getListCellRendererComponent(JList<? extends DataCell> list, DataCell value, int index, boolean isSelected, boolean cellHasFocus) {
					LabelCell labelCell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					
					labelCell.setTextAux("Text Aux");
					return labelCell;
				}
			});
		
			TestPanel testPanel = new TestPanel(labelCombo);
			testPanel.getFieldsPanel().addAccessComponentsFromFields(AppGUI.getCurrentAppGUI());
			AppGUI.getCurrentAppGUI().setShowingCodes(true);
			
			LDialogContent.showComponentInFrame(testPanel);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
