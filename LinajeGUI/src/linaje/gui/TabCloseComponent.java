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

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import java.awt.Insets;

import linaje.gui.ui.LTabbedPaneUI;
import linaje.gui.utils.ColorsGUI;
import linaje.statics.Constants;
import linaje.utils.StateColor;

/**
 * Si se asigna como tabComponent a un JTabbedPane (tabbedPane.setTabComponentAt(int index, Component component))
 * se pintará una x junto al texto de la pestaña que cerrará la pestaña al hacer click sobre ella
 * 
 * También se podría sobreescribir la acción de cierre de la pestaña y modificarla o añadir lo que queramos
 * 	TabCloseComponent tcc = new TabCloseComponent(tabbedPane); 
 *	Action closeAction = new AbstractAction() {
 *		
 *		@Override
 *		public void actionPerformed(ActionEvent e) {
 *			MessageDialog.showMessage("Se va a cerrar la pestaña", MessageDialog.ICON_INFO);
 *			tcc.defaultCloseActionPerformed();
 *		}
 *	};
 *	tcc.setCloseAction(closeAction);
 **/
public class TabCloseComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	private LButton buttonClose = null;
	private JLabel label = null;
	private LButton labelAux = null;
	
	private JTabbedPane tabbedPane = null;
	private Action closeAction = null;
	
	public TabCloseComponent() {
		this(null, null);
	}
	public TabCloseComponent(JTabbedPane tabbedPane) {
		this(tabbedPane, null);
	}
	public TabCloseComponent(JTabbedPane tabbedPane, Action closeAction) {
		super();
		this.tabbedPane = tabbedPane;
		this.closeAction = closeAction;
		initialize();
	}

	private void initialize() {

		GridBagConstraints gbConstraintsLabel = new GridBagConstraints();
		gbConstraintsLabel.gridx = 0;
		gbConstraintsLabel.gridy = 0;
		gbConstraintsLabel.insets = new Insets(0, 0, 0, 0);
		gbConstraintsLabel.ipadx = 0;
		gbConstraintsLabel.ipady = 0;
		gbConstraintsLabel.fill = GridBagConstraints.HORIZONTAL;
		gbConstraintsLabel.weightx = 1.0D;
		gbConstraintsLabel.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gbConstraintsBoton = new GridBagConstraints();
		gbConstraintsBoton.gridx = 1;
		gbConstraintsBoton.gridy = 0;
		gbConstraintsBoton.insets = new Insets(0, 5, 0, 2);
		
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(183, 18));
		this.setOpaque(false);
		this.add(getLabel(), gbConstraintsLabel);
		
		this.add(getButtonClose(), gbConstraintsBoton);
		initConnections();
	}

	private void initConnections() {
		
		ActionListener al = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() == getButtonClose()) {
					if (getCloseAction() != null) {
						getCloseAction().actionPerformed(e);
					}
					else {
						defaultCloseActionPerformed();
					}
				}
			}
		};
		getButtonClose().addActionListener(al);
	}

	public void defaultCloseActionPerformed() {
		int index = getIndex();
		if (index != -1) {
			int oldSelectedIndex = getTabbedPane().getSelectedIndex();
			if (oldSelectedIndex <= index && !(oldSelectedIndex == getTabbedPane().getTabCount()))
				getTabbedPane().setSelectedIndex(oldSelectedIndex);
			else
				getTabbedPane().setSelectedIndex(oldSelectedIndex - 1);
			getTabbedPane().remove(index);
		}
	}
	
	public Action getCloseAction() {
		return closeAction;
	}
	
	public void setCloseAction(Action closeAction) {
		this.closeAction = closeAction;
	}
	
	public LButton getButtonClose() {
		if (buttonClose == null) {
			buttonClose = new LButton(Constants.VOID);
			StateColor foreground = new StateColor(buttonClose.getForeground());
			foreground.setRolloverColor(ColorsGUI.getColorNegative());
			
			buttonClose.setForeground(foreground);
			buttonClose.setOpaque(false);
			buttonClose.setIcon(Icons.getIconX(7, 1, null));
			buttonClose.getButtonProperties().setPaintBgEffectsWhenTransparent(false);
			buttonClose.getButtonProperties().setIconForegroundEnabled(false);
			buttonClose.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			buttonClose.setMargin(new Insets(0, 0, 0, 0));
		}
		return buttonClose;
	}

	@SuppressWarnings("serial")
	public JLabel getLabel() {
		if (label == null) {
			//label = new LLabel() {
			label = new JLabel() {
				//Ponemos el texto de la pestaña
				public String getText() {
					if (getTabbedPane() != null) {
						int index = getIndex();
						String title = index != -1 ? getTabbedPane().getTitleAt(index) : Constants.VOID;
						return title;
					}
					return super.getText();
				}
				@Override
				protected void paintComponent(Graphics g) {
					if (tabbedPane != null && tabbedPane.getUI() instanceof LTabbedPaneUI) {
						LTabbedPaneUI lTabUI = (LTabbedPaneUI) tabbedPane.getUI();
						int index = getIndex();
						lTabUI.paintLabelAux(g, getText(), getBounds(), getIndex(), tabbedPane.getIconAt(index), lTabUI.getFont(index), true);
					}
					else {
						super.paintComponent(g);
					}
				}
				
				@Override
				public Dimension getPreferredSize() {
					Dimension prefSize = new Dimension(getLabelAux().getPreferredSize());
					return prefSize;
				}
			};
			label.setText("TabCloseComponent");
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return label;
	}

	@SuppressWarnings("serial")
	private LButton getLabelAux() {
		if (labelAux == null) {
			labelAux = new LButton() {
				public String getText() {
					return TabCloseComponent.this.getLabel().getText();
				}
				
				@Override
				public Font getFont() {
					Font font;
					if (tabbedPane != null && tabbedPane.getUI() instanceof LTabbedPaneUI) {
						LTabbedPaneUI lTabUI = (LTabbedPaneUI) tabbedPane.getUI();
						int index = getIndex();
						font = lTabUI.getFont(index);
					}
					else {
						font = super.getFont();
					}
					return font;// font != null ? font.deriveFont(Font.BOLD) : null;
				}
			};
			labelAux.setText(Constants.VOID);
			labelAux.setHorizontalAlignment(SwingConstants.CENTER);
			labelAux.setBorder(BorderFactory.createEmptyBorder());
			labelAux.setOpaque(false);
			labelAux.getButtonProperties().setGradientBackgroundEnabled(false);
			labelAux.setMargin(new Insets(0, 0, 0, 0));
		}
		return labelAux;
	}
	
	private JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	public int getIndex() {
		int index = getTabbedPane() != null ? getTabbedPane().indexOfTabComponent(this) : -1;
		return index;
	}
}
