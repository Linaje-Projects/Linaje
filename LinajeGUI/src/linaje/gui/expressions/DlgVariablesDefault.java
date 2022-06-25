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
package linaje.gui.expressions;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.SwingConstants;

import linaje.LocalizedStrings;
import linaje.expressions.Variable;
import linaje.gui.LCombo;
import linaje.gui.LTextField;
import linaje.gui.components.LabelCombo;
import linaje.gui.components.LabelComponent;
import linaje.gui.components.LabelTextField;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class DlgVariablesDefault extends DlgVariablesGeneric {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String numerical;
		public String numericDecimal;
		public String alphanumeric;
		public String date;
		public String global;
		public String type;
		public String name;
		public String value;
			
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final String NATURE_NUMERICAL = TEXTS.numerical;
	public static final String NATURE_NUMERIC_DECIMAL = TEXTS.numericDecimal;
	public static final String NATURE_ALPHANUMERIC = TEXTS.alphanumeric;
	public static final String NATURE_DATE = TEXTS.date;
	public static final String NATURE_GLOBAL = TEXTS.global;
	
	private LabelCombo<String> lblCmbTypeVar = null;
	private LabelTextField lblTxtNameVar = null;
	private LabelTextField lblTxtValue = null;
	
	public DlgVariablesDefault(ExpressionsPanel expressionsPanel) {
		super(expressionsPanel);
		initialize();
	}
	public DlgVariablesDefault(ExpressionsPanel expressionsPanel, Frame frame) {
		super(expressionsPanel, frame);
		initialize();
	}
	
	private LabelCombo<String> getLblCmbTypeVar() {
		if (lblCmbTypeVar == null) {
			lblCmbTypeVar = new LabelCombo<>(TEXTS.type);
			lblCmbTypeVar.setOrientation(LabelComponent.VERTICAL);
			LCombo<String> combo = lblCmbTypeVar.getCombo();
			
			combo.addItem(NATURE_ALPHANUMERIC);
			combo.addItem(NATURE_NUMERICAL);
			combo.addItem(NATURE_NUMERIC_DECIMAL);
			combo.addItem(NATURE_DATE);
			combo.addItem(NATURE_GLOBAL);
			
			combo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						updateTxtValueProperties();
					}
				}
			});
		}
		return lblCmbTypeVar;
	}

	private LabelTextField getLblTxtNameVar() {
		if (lblTxtNameVar == null) {
			lblTxtNameVar = new LabelTextField(TEXTS.name);
			lblTxtNameVar.setOrientation(LabelComponent.VERTICAL);
		}
		return lblTxtNameVar;
	}
	
	private LabelTextField getLblTxtValue() {
		if (lblTxtValue == null) {
			lblTxtValue = new LabelTextField(TEXTS.value);
			lblTxtValue.setOrientation(LabelComponent.VERTICAL);
		}
		return lblTxtValue;
	}
	
	private Class<?> getSelectedVariableType() {
	
		String selectedType = getLblCmbTypeVar().getCombo().getSelectedItem().toString();
		if (selectedType.equals(NATURE_NUMERICAL))
			return Integer.class;
		else if (selectedType.equals(NATURE_NUMERIC_DECIMAL))
			return Double.class;
		else if (selectedType.equals(NATURE_DATE))
			return Date.class;
		else if (selectedType.equals(NATURE_ALPHANUMERIC))
			return String.class;
		else
			return null;
	}
	
	private Object getSelectedVariableValue() {
		return getLblTxtValue().getTextField().getValue();
	}
	
	public Variable getSelectedVariable() {
		
		String name = getLblTxtNameVar().getText();
		Class<?> type = getSelectedVariableType();
		Object value = getSelectedVariableValue();
		
		return new Variable(name, type, value);
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		add(getLblTxtNameVar());
		add(getLblCmbTypeVar());
		add(getLblTxtValue());
		
		int width = Math.max(getPreferredSize().width, getLblCmbTypeVar().getPreferredSize().width*3);//AppGUI.getFont().getSize()*20);
		int height = getPreferredSize().height;
		setSize(width, height);
		
		updateTxtValueProperties();
	}
	
	private void updateTxtValueProperties() {
		
		String typeSelected = getLblCmbTypeVar().getCombo().getSelectedItem().toString();
		
		LTextField txtValue = getLblTxtValue().getTextField();
		
		if (typeSelected.equals(NATURE_NUMERICAL)) {

			txtValue.setType(LTextField.TYPE_NUMBER);
			txtValue.setDecimals(0);
			txtValue.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		else if (typeSelected.equals(NATURE_NUMERIC_DECIMAL)) {

			txtValue.setType(LTextField.TYPE_NUMBER);
			txtValue.setDecimals(2);
			txtValue.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		else if (typeSelected.equals(NATURE_DATE)) {

			txtValue.setType(LTextField.TYPE_DATE);
			txtValue.setValue(txtValue.getValueDate());
			txtValue.setHorizontalAlignment(SwingConstants.CENTER);
		}
		else {

			txtValue.setType(LTextField.TYPE_TEXT);
			txtValue.setText(Constants.VOID);
			txtValue.setHorizontalAlignment(SwingConstants.LEFT);
		}
	}
}
