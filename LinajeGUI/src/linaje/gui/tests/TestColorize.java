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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.LPanel;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.FilesGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.statics.Constants;
import linaje.statics.Directories;

@SuppressWarnings("serial")
public class TestColorize extends LPanel {

	public TestColorize() {
		super();
		initialize();
	}

	private void initialize() {
		
		setName("Colors");
		setLayout(new GridBagLayout());
		
		JColorChooser colorChooser = new JColorChooser(Color.gray);
		final AbstractColorChooserPanel colorPanel = colorChooser.getChooserPanels()[2];//ColorChooserComponentFactory.getDefaultChooserPanels()[3];
		LButton btnFile = new LButton("Imagen");
		final LCheckBox checkColorize = new LCheckBox("Colorize", false);
		checkColorize.setFontSize(16);
		final JScrollPane scrollPane = new JScrollPane();
		final JLabel labelOrig = new JLabel(Constants.VOID);
		final JLabel labelColorized = new JLabel(Constants.VOID);
		
		scrollPane.setViewportView(labelOrig);
		
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = FilesGUI.chooseFile(FilesGUI.MODE_LOAD, new File(Directories.SYSTEM_USER_DIR, "Images"));
				if (file != null) {
					ImageIcon icon = new ImageIcon(file.getAbsolutePath());
					labelOrig.setIcon(icon);
					labelOrig.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
					labelColorized.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
					labelColorized.setIcon(new ImageIcon(Icons.createColorizedImage(icon.getImage(), colorPanel.getColorSelectionModel().getSelectedColor())));
				}
			}
		});
		
		checkColorize.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean colorize = e.getStateChange() == ItemEvent.SELECTED;
				scrollPane.setViewportView(colorize ? labelColorized : labelOrig);
			}
		});
		
		colorPanel.getColorSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (labelOrig.getIcon() != null) {
					labelColorized.setIcon(new ImageIcon(Icons.createColorizedImage(Icons.getImage(labelOrig.getIcon()), colorPanel.getColorSelectionModel().getSelectedColor())));
				}
			}
		});
		
		GridBagConstraints gbcScroll = new GridBagConstraints();
		gbcScroll.anchor = GridBagConstraints.CENTER;
		gbcScroll.insets = new Insets(5, 5, 5, 5);
		gbcScroll.gridwidth = 2;
		gbcScroll.gridx = 1;
		gbcScroll.gridy = 1;
		gbcScroll.weightx = 1.0;
		gbcScroll.weighty = 1.0;
		gbcScroll.fill = GridBagConstraints.BOTH; 
		
		GridBagConstraints gbcBtn = new GridBagConstraints();
		gbcBtn.anchor = GridBagConstraints.SOUTH;
		gbcBtn.insets = new Insets(0, 5, 5, 5);
		gbcBtn.gridx = 1;
		gbcBtn.gridy = 2;
		gbcBtn.weightx = 0.0;
		gbcBtn.weighty = 0.0;
		gbcBtn.fill = GridBagConstraints.VERTICAL;
		
		GridBagConstraints gbcCheck = new GridBagConstraints();
		gbcCheck.anchor = GridBagConstraints.NORTH;
		gbcCheck.insets = new Insets(5, 0, 5, 5);
		gbcCheck.gridx = 1;
		gbcCheck.gridy = 3;
		gbcCheck.weightx = 0.0;
		gbcCheck.weighty = 0.0;
		gbcCheck.fill = GridBagConstraints.VERTICAL; 		
		
		GridBagConstraints gbcColorPanel = new GridBagConstraints();
		gbcColorPanel.anchor = GridBagConstraints.WEST;
		gbcColorPanel.insets = new Insets(5, 5, 5, 5);
		gbcColorPanel.gridheight = 2;
		gbcColorPanel.gridx = 2;
		gbcColorPanel.gridy = 2;
		gbcColorPanel.weightx = 1.0;
		gbcColorPanel.weighty = 0.0;
		gbcColorPanel.fill = GridBagConstraints.HORIZONTAL; 
		
		setLayout(new GridBagLayout());
		add(scrollPane, gbcScroll);
		add(btnFile, gbcBtn);
		add(checkColorize, gbcCheck);
		add(colorPanel, gbcColorPanel);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		//Truco para obtener la clase actual de forma estática y copiar y pegar éste main en otras clases
		Class currentClass = new Object() { }.getClass().getEnclosingClass();
		UtilsGUI.quickMain(currentClass);
	}
}