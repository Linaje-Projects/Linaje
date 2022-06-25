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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import linaje.LocalizedStrings;
import linaje.expressions.Variable;
import linaje.gui.LButton;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.MessageDialog;
import linaje.gui.windows.HeaderPanel;
import linaje.gui.windows.LDialogContent;

@SuppressWarnings("serial")
public abstract class DlgVariablesGeneric extends LDialogContent implements ActionListener {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String addVariable;
		public String descDialog;
		public String errorNameNotValid;
		public String errorVariableExists;
		public String errorVariableNotValid;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private ExpressionsPanel panelExpresions = null;
	private LButton btnAddVariable = null;
	private LButton btnCancel = null;
	
	public DlgVariablesGeneric(ExpressionsPanel expressionsPanel) {
		
		super();
		setPanelExpresions(expressionsPanel);
		initialize();
	}
	public DlgVariablesGeneric(ExpressionsPanel expressionsPanel, Frame frame) {
		
		super(frame);
		setPanelExpresions(expressionsPanel);
		initialize();
	}
	
	private void initConnections() {
		getBtnAddVariable().addActionListener(this);
		getBtnCancel().addActionListener(this);
	}
	
	private void initialize() {
		initLDialogContent();
		initConnections();
	}
	
	private void initLDialogContent() {
		
		//Añadimos los paneles habituales
		HeaderPanel headerPanel = new HeaderPanel();
		headerPanel.setDescription(TEXTS.descDialog);
		
		ButtonsPanel buttonsPanel = new ButtonsPanel();
		buttonsPanel.addButton(TEXTS.addVariable, ButtonsPanel.POSITION_RIGHT);
		buttonsPanel.addButton(ButtonsPanel.BUTTON_CANCEL, ButtonsPanel.POSITION_RIGHT);
		buttonsPanel.setAutoCloseOnCancel(true);
		
		setHeaderPanel(headerPanel);
		setButtonsPanel(buttonsPanel);
		
		setTitle(TEXTS.addVariable);
		setMargin(5);
		setResizable(false);
	}
	
	public void actionPerformed(ActionEvent e) {
	
		if (e.getSource() == getBtnAddVariable()) {
	
			Variable variable = getSelectedVariable();
			String descError = null;
			if (variable != null) {
				
				String name = variable.getName();
				if (!Variable.isVariableNameValid(name)) {
	
					descError = TEXTS.errorNameNotValid;
				}
				else if (Variable.searchVariable(name, getVariables()) != null) {
	
					descError = TEXTS.errorVariableExists;
				}
				else {
	
					getButtonsPanel().setResponse(ButtonsPanel.RESPONSE_ACCEPT_YES);
					this.dispose();
					return;
				}
			}
	
			if (descError == null)
				descError = TEXTS.errorVariableNotValid;
	
			MessageDialog.showMessage(descError, MessageDialog.ICON_WARNING);
		}
		else if (e.getSource() == getBtnCancel()) {
	
			getButtonsPanel().setResponse(ButtonsPanel.RESPONSE_CANCEL);
			this.dispose();
		}
	}
	
	private LButton getBtnAddVariable() {
	
		if (btnAddVariable == null) {
			btnAddVariable = getButtonsPanel().getButton(TEXTS.addVariable);
		}
		return btnAddVariable;
	}
	
	private LButton getBtnCancel() {
	
		if (btnCancel == null) {
			btnCancel = getButtonsPanel().getButton(ButtonsPanel.BUTTON_CANCEL);
		}
		return btnCancel;
	}
	
	public ExpressionsPanel getPanelExpresions() {
		return panelExpresions;
	}
	
	public Vector<Variable> getVariables() {
	
		if (getPanelExpresions() != null)
			return getPanelExpresions().getVariables();
		else
			return null;
	}
	
	public abstract Variable getSelectedVariable();
	
	private void setPanelExpresions(ExpressionsPanel panelExpresions) {
		this.panelExpresions = panelExpresions;
	}
}
