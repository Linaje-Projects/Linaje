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
package linaje.gui.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;

import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import linaje.App;
import linaje.LocalizedStrings;
import linaje.gui.LButton;
import linaje.gui.LLabel;
import linaje.gui.LPasswordField;
import linaje.gui.LTextField;
import linaje.gui.ui.UISupport;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.StateColor;

@SuppressWarnings("serial")
public abstract class DialogUserPassword extends LDialogContent {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String user;
		public String pass;
		public String error;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	LDialog customDialog = null;
	LTextField txtUser = null;
	LPasswordField txtPassword = null;
	LLabel lblError = null;
	
	boolean verified = false;

	public DialogUserPassword() {
		this(null, null);
	}
	
	public DialogUserPassword(Frame owner) {
		this(owner, null);
	}

	public DialogUserPassword(Frame owner, String user) {
		super(owner);
		setUser(user);
		initialize();
	}

	private void initialize() {

		setOpaque(false);
		setLayout(new GridBagLayout());
		setDialog(getCustomDialog());
		setResizable(false);
		setTitle(App.getCurrentApp().getName());
		if (getUser().length() > 0)
			setFirstFocusableComponent(getTxtPassword());
		else
			setFirstFocusableComponent(getTxtUser());
		
		getLblError().setPreferredSize(getTxtUser().getPreferredSize());

		ButtonsPanel pb = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT_CANCEL);
		pb.setAutoCloseOnCancel(false);
		setButtonsPanel(pb);

		final LButton btnAccept = pb.getButton(ButtonsPanel.BUTTON_ACCEPT);
		final LButton btnCancel = pb.getButton(ButtonsPanel.BUTTON_CANCEL);

		Color bgColor = new Color(140, 140, 140);
		StateColor fgColor = new StateColor(Color.white);
		fgColor.setRolloverColor(ColorsGUI.getColorApp());
		fgColor.setDisabledColor(new Color(85, 85, 85));

		btnAccept.setBackground(bgColor);
		btnAccept.setForeground(fgColor);
		btnAccept.setOpaque(false);
		btnCancel.setBackground(bgColor);
		btnCancel.setForeground(fgColor);
		btnCancel.setOpaque(false);

		btnAccept.setEnabled(false);

		DocumentListener documentListener = new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				update();
			}

			public void insertUpdate(DocumentEvent e) {
				update();
			}

			public void changedUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				boolean txtsRellenados = !getTxtUser().getText().isEmpty() && getTxtPassword().getPassword().length > 0;
				btnAccept.setEnabled(txtsRellenados);
			}
		};

		KeyListener keyListener = new KeyListener() {

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (btnAccept.isEnabled())
						btnAccept.doClick();
				}
			}
			
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}			
		};

		getTxtUser().getDocument().addDocumentListener(documentListener);
		getTxtPassword().getDocument().addDocumentListener(documentListener);

		getTxtUser().addKeyListener(keyListener);
		getTxtPassword().addKeyListener(keyListener);

		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					btnAccept.setEnabled(false);
					boolean verified = verifyUserPassword();
					setVerified(verified);
					if (verified)
						dispose();
					else
						getLblError().setText(getDefaultErrorMessage());
				}
				catch (Throwable ex) {
					getLblError().setText(ex.getMessage());
				}
				btnAccept.setEnabled(true);
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.insets = new Insets(20, 20, 10, 20);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		add(getTxtUser(), gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(0, 20, 5, 20);

		add(getTxtPassword(), gbc);

		gbc.gridy = 3;
		gbc.insets = new Insets(0, 0, 5, 0);

		add(getLblError(), gbc);

		setSize(250, getPreferredSize().height);
	}

	private LDialog getCustomDialog() {
		if (customDialog == null) {
			customDialog = new LDialog(getFrameOwner());
			customDialog.setModal(true);
			customDialog.setBackgroundContent(Color.black);
			customDialog.setForeground(ColorsGUI.getColorApp());
			customDialog.setTransparency(0.3f);
			customDialog.setBorderColor(ColorsGUI.getColorApp());
			customDialog.setCloseable(true);
			customDialog.setMaximizable(false);
			customDialog.setMinimizable(false);
		}
		return customDialog;
	}

	protected LTextField getTxtUser() {
		if (txtUser == null) {
			txtUser = new LTextField();
			txtUser.setTextBackgroundVoid(TEXTS.user);
			txtUser.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return txtUser;
	}

	protected LPasswordField getTxtPassword() {
		if (txtPassword == null) {
			txtPassword = new LPasswordField();
			txtPassword.setTextBackgroundVoid(TEXTS.pass);
			txtPassword.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return txtPassword;
	}

	private LLabel getLblError() {
		if (lblError == null) {
			lblError = new LLabel();
			//Optimizamos el rojo de ColorNegative para que se vea mejor con el color de fondo del diálogo (Algo mas claro ya que es semitransparente)
			lblError.setForeground(Colors.optimizeColor(ColorsGUI.getColorNegative(), Colors.getBrighterColor(getCustomDialog().getBackground())));
			lblError.setPreferredSize(lblError.getPreferredSize());
			lblError.setText(Constants.VOID);
			lblError.setHorizontalAlignment(SwingConstants.CENTER);
			lblError.setFontStyle(Font.BOLD);
		}
		return lblError;
	}

	public String getUser() {
		return getTxtUser().getText();
	}
	public void setUser(String user) {
		getTxtUser().setText(user != null ? user : Constants.VOID);
	}
	
	public char[] getPassword() {
		return getTxtPassword().getPassword();
	}
	public void setPassword(String password) {
		getTxtPassword().setText(password != null ? password : Constants.VOID);
	}

	public void setEmptyPasswordText(String emptyPasswordText) {
		getTxtPassword().setTextBackgroundVoid(emptyPasswordText != null ? emptyPasswordText : Constants.VOID);
	}
	
	public void setEmptyUserText(String emptyUserText) {
		getTxtUser().setTextBackgroundVoid(emptyUserText != null ? emptyUserText : Constants.VOID);
	}
	
	public String getDefaultErrorMessage() {
		return TEXTS.error;
	}
	
	public boolean isVerified() {
		return verified;
	}

	private void setVerified(boolean verified) {
		this.verified = verified;
	}

	
	protected abstract boolean verifyUserPassword();
}
