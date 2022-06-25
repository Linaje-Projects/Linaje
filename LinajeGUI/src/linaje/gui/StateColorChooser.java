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
package linaje.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.utils.StateColor;

/**
 * Panel para asignar los colores de un stateColor 
 **/
@SuppressWarnings("serial")
public class StateColorChooser extends LDialogContent {

	private StateColor value = null;
	private FieldsPanel fieldsPanel = null;
	
	public static final int MODE_SELECT_NONE = 0;
	public static final int MODE_SELECT_NO_NULL_VALUES = 1;
	
	public StateColorChooser() {
		this(null);
	}
	
	public StateColorChooser(StateColor stateColor) {
		this(stateColor, null);
	}

	public StateColorChooser(StateColor stateColor, Frame frame) {
		super(frame);
		initialize();
		setValue(stateColor);
	}

	private void initialize() {
		
		setLayout(new BorderLayout());
		ButtonsPanel buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT_CANCEL);
		buttonsPanel.setAutoCloseOnAccept(true);
		setButtonsPanel(buttonsPanel);
		add(getFieldsPanel(), BorderLayout.NORTH);
	}
	
	protected FieldsPanel getFieldsPanel() {
		if (fieldsPanel == null) {
			fieldsPanel = new FieldsPanel(null, FieldsPanel.MODE_SELECT_NO_NULL_VALUES);
			fieldsPanel.setDefaultUntypedFieldType(Color.class);
		}
		return fieldsPanel;
	}
	
	public StateColor getValue() {
		if (value == null)
			value = new StateColor();
		return value;
	}
	public void setValue(StateColor value) {
		//Asignamos una instancia nueva para no modificar el StateColor original
		this.value = value != null ? value.clone() : null;
		getFieldsPanel().removeAll();
		getFieldsPanel().addAccessComponentsFromFields(getValue().getStateValues());
		setSize(getPreferredSize());
		validate();
		repaint();
	}
	public void setValueEncoded(String valueEncoded) {
		setValue(StateColor.decode(valueEncoded));
	}
}
