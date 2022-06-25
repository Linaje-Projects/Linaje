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

import linaje.gui.components.LabelCombo;
import linaje.gui.components.LabelComponent;
import linaje.gui.components.LabelTextField;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.utils.LFont;

public class TestLabelComponent {

	public static void main(String[] args) {
		
		LinajeLookAndFeel.init();
		
		LabelCombo<String> labelCombo1 = new LabelCombo<String>("TEXTO");
		labelCombo1.setLineColor(ColorsGUI.getColorApp());
		labelCombo1.getLabel().setFontLayout(LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT);
		labelCombo1.setOrientation(LabelComponent.VERTICAL);
		labelCombo1.addItem("Elemento1 dasd as");
		labelCombo1.addItem("Elemento2 czx");
		labelCombo1.addItem("Elemento3");
			
		LabelCombo<String> labelCombo2 = new LabelCombo<String>("Texto mas Largo TEXT");
		labelCombo2.setOrientation(LabelComponent.VERTICAL);
		labelCombo2.addItem("Elemento1");
		labelCombo2.addItem("Elemento2 hola");
		labelCombo2.addItem("Elemento3");
		
		LabelCombo<String> labelCombo3 = new LabelCombo<String>("Text\nMultilinea");
		labelCombo3.setAutoSizeLabel(true);
		labelCombo3.addItem("Elemento1");
		labelCombo3.addItem("Elemento2");
		labelCombo3.addItem("Elemento3");
		
		LabelTextField labelTextField = new LabelTextField();
		labelTextField.setTextLabel("Texto muy largo largo");
		labelTextField.setAutoSizeLabel(true);
		labelTextField.setText("Texto de Prueba");
		
		LabelTextField labelTextField2 = new LabelTextField();
		labelTextField2.setLineColor(ColorsGUI.getColorNegative());
		labelTextField2.setTextLabel("Text\nMultilinea");
		labelTextField2.setText("Texto de Prueba");
		labelTextField2.setEditable(false);
		labelTextField2.setOrientation(LabelComponent.VERTICAL);
		
		LabelTextField labelTextField3 = new LabelTextField();
		labelTextField3.setTextLabel("Texto muy largo largo");
		labelTextField3.setAutoSizeLabel(true);
		labelTextField3.setText("Texto de Prueba");
		labelTextField3.setEnabled(false);
		
				
		LDialogContent dialogo = new LDialogContent();
		dialogo.setSize(new Dimension(600, 300));
		dialogo.setResizable(true);
		dialogo.add(labelCombo3);
		dialogo.add(labelCombo1);
		dialogo.add(labelCombo2);
		dialogo.add(labelTextField);
		dialogo.add(labelTextField2);
		dialogo.add(labelTextField3);
		
		
		
		dialogo.showInFrame();
	}
	
	
}
