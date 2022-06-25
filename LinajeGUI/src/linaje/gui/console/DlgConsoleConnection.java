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
package linaje.gui.console;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import linaje.LocalizedStrings;
import linaje.comunications.Connection;
import linaje.gui.LButton;
import linaje.gui.LLabel;
import linaje.gui.LTextField;
import linaje.gui.components.LabelTextField;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;

/**
 * <b>Funcionalidad:</b><br>
 * Dialogo que permite reintentar o cancelar la conexión a la consola
 * <p>
 * <b>Uso:</b><br>
 * <p>
 * 
 * @author Pablo Linaje
 * @version 1.4
 * 
 * @see ConsoleWindow
 * 
 */

@SuppressWarnings("serial")
public class DlgConsoleConnection extends LDialogContent {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String retry;
		public String retryAlways;
		public String title;
		public String host;
		public String port;
				
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private LLabel labelMessage = null;
	
	private LButton buttonRetry = null;
	private LButton buttonRetryAlways = null;
	private LButton buttonCancel = null;
	
	private LabelTextField lblTxtHost = null;
	private LabelTextField lblTxtPort = null;
	
	private String host = null;
	private boolean retryAlways = false;

	public DlgConsoleConnection() {
		super();
		initialize();
	}
	public DlgConsoleConnection(Frame owner) {
		super(owner);
		initialize();
	}
	
	private void retry(boolean always) {
		setHost(getLblTxtHost().getText());
		setRetryAlways(always);
		Console.getInstance().setPort(getLblTxtPort().getTextField().getValueNumber().intValue());
	}
	
	private LButton getbtnCancelar() {
		if (buttonCancel == null) {
			buttonCancel = new LButton(ButtonsPanel.TEXTS.cancel);
			buttonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setHost(null);
					dispose();
				}
			});
		}
		return buttonCancel;
	}
	
	private LButton getbtnReintentar() {
		if (buttonRetry == null) {
			buttonRetry = new LButton(TEXTS.retry);
			buttonRetry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					retry(false);
					dispose();
				}
			});
		}
		return buttonRetry;
	}
	
	private LButton getbtnReintentarSiempre() {
		if (buttonRetryAlways == null) {
			buttonRetryAlways = new LButton(TEXTS.retryAlways);
			buttonRetryAlways.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					retry(true);
					dispose();
				}
			});
		}
		return buttonRetryAlways;
	}
	
	
	private LLabel getLabelMessage() {
		if (labelMessage == null) {
			labelMessage = new LLabel();
			labelMessage.setIcon(MessageDialog.ICON_WARNING);
			labelMessage.setPreferredSize(new Dimension(600, labelMessage.getPreferredSize().height));
		}
		return labelMessage;
	}
	
	private LabelTextField getLblTxtHost() {
		if (lblTxtHost == null) {
			lblTxtHost = new LabelTextField(TEXTS.host);
			lblTxtHost.setWidthLabel(150);
			lblTxtHost.setHorizontalAlignment(SwingConstants.CENTER);
			Dimension size = new Dimension(300, lblTxtHost.getPreferredSize().height);
			lblTxtHost.setPreferredSize(size);
			lblTxtHost.setMinimumSize(size);
			lblTxtHost.setMaximumSize(size);
		}
		return lblTxtHost;
	}
	
	private LabelTextField getLblTxtPort() {
		if (lblTxtPort == null) {
			lblTxtPort = new LabelTextField(TEXTS.port);
			lblTxtPort.setWidthLabel(getLblTxtHost().getWidthLabel());
			Dimension size = getLblTxtHost().getPreferredSize();
			lblTxtPort.setPreferredSize(size);
			lblTxtPort.setMinimumSize(size);
			lblTxtPort.setMaximumSize(size);
			
			lblTxtPort.setType(LTextField.TYPE_NUMBER);
			lblTxtPort.setSpinNumericsVisible(true);
			lblTxtPort.getTextField().setMaxValue(Connection.PORT_MAX);
			lblTxtPort.getTextField().setMinValue(Connection.PORT_MIN);
			lblTxtPort.getTextField().setValue(Console.getInstance().getPort());
		}
		return lblTxtPort;
	}
	
	
	private void initLDialogContent() {
	
		ButtonsPanel buttonsPanel = new ButtonsPanel();
		buttonsPanel.addJComponent(getbtnReintentar(), ButtonsPanel.POSITION_RIGHT);
		buttonsPanel.addJComponent(getbtnReintentarSiempre(), ButtonsPanel.POSITION_RIGHT);
		buttonsPanel.addJComponent(getbtnCancelar(), ButtonsPanel.POSITION_RIGHT);
		
		setButtonsPanel(buttonsPanel);
	}
	
	private void initialize() {
		initLDialogContent();
		setResizable(false);
		setLayout(new GridBagLayout());
		setModal(true);
		setTitle(TEXTS.title);
		getDefaultDialog().setBorderColor(ColorsGUI.getColorWarning());
		setMargin(15);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getLabelMessage(), gbc);
		
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		add(getLblTxtHost(), gbc);
		
		gbc.gridy = 3;
		add(getLblTxtPort(), gbc);
		
		setSize(getPreferredSize());
	}
	
	public String show(String message, String host) {
		getLabelMessage().setText(message);
		getLblTxtHost().setText(host);
		getLblTxtPort().getTextField().setValue(Console.getInstance().getPort());
		showInDialog();
		return getHost();
	}
	
	
	public String getHost() {
		return host;
	}
	private void setHost(String host) {
		this.host = host;
	}
	
	public boolean isRetryAlways() {
		return retryAlways;
	}
	private void setRetryAlways(boolean retryAlways) {
		this.retryAlways = retryAlways;
	}
}
