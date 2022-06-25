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
package linaje.gui.components;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import linaje.gui.LArrowButton;
import linaje.gui.LLabel;
import linaje.gui.LMenu;
import linaje.gui.LMenuItem;
import linaje.gui.LPanel;
import linaje.gui.LPopupMenu;
import linaje.gui.RoundedBorder;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Utils;

/**
 * Botón selector de colores desplegable
 * Por cada color que se añada se creará un submenú con colores degradados de ese color
 * Se puede personalizar con los colores que se quiera 
 **/
@SuppressWarnings("serial")
public class ColorButton extends LPanel {
	
	private LArrowButton arrowButton = null;
	private LLabel labelSelectedColor = null;
	private LPopupMenu popUpMenu = null;
	private Color selectedColor = null;
	private Color defaultColor = null;
	
	public static final String PROPERTY_selectedColor = "selectedColor";
	
	public ColorButton() {
		this(true);
	}
	public ColorButton(boolean addDefaultcolors) {
		super();
		initialize();
		if (addDefaultcolors)
			addDefaultColors();
	}
	
	private void initialize() {
		
		setOpaque(false);
		
		Dimension prefSize = new Dimension(50, 40);
		setPreferredSize(prefSize);
		setSize(prefSize);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(getLabelSelectedColor(), gbc);
		gbc.weightx = 0.15;
		gbc.gridx = 2;
		add(getArrowButton(), gbc);
		
		RoundedBorder roundedBorder = new RoundedBorder();
		roundedBorder.setThicknessShadow(0);
		roundedBorder.setThicknessInsetsExtra(-1);
		setBorder(roundedBorder);
	}
	
	public void addColor(Color color) {
		
		if (color.equals(Color.white)) {
			
			LMenuItem menuItemWhite = createMenuItemColor(color);
			getPopUpMenu().add(menuItemWhite);
		}
		else {
			
			final JLabel labelColor = createLabelColor(color);
			final LMenu menuColors = new LMenu(Constants.SPACE) {
				public Dimension getPreferredSize() {
					//Hacemos el menú del mismo ancho que el botón
					Dimension prefSize = super.getPreferredSize();
					prefSize.width = ColorButton.this.getWidth();
					return prefSize;
				}
			};
			menuColors.addMouseListener(new MouseListener() {
				public void mousePressed(MouseEvent e) {
					setSelectedColor(labelColor.getBackground());
					setDefaultColor(labelColor.getBackground());
					getPopUpMenu().setVisible(false);
					menuColors.getPopupMenu().setVisible(false);
					repaint();
				}
				public void mouseClicked(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});
			menuColors.setFocusPainted(false);
			menuColors.setRequestFocusEnabled(false);
			menuColors.setLayout(new BorderLayout());
			menuColors.add(labelColor, BorderLayout.CENTER);
			
			addGradientColors(menuColors, color);
			getPopUpMenu().add(menuColors);
		}
		
		if (isShowing())
			repaint();
	}
	
	private void addGradientColors(LMenu menuColors, Color color) {
		
		for (int i = 1; i <= 5; i++){
			
			Color colorGradient = Colors.degrade(color, i);
			LMenuItem itemGradient = createMenuItemColor(colorGradient);
			menuColors.add(itemGradient);
		}
	}
	
	private LMenuItem createMenuItemColor(Color color) {
		
		LMenuItem menuItemColor = new LMenuItem(Constants.SPACE);
		
		menuItemColor.setFocusPainted(false);
		menuItemColor.setRequestFocusEnabled(false);
		menuItemColor.setLayout(new BorderLayout());
		
		final JLabel labelColor = createLabelColor(color);
		menuItemColor.add(labelColor, BorderLayout.CENTER);
		
		menuItemColor.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				setSelectedColor(labelColor.getBackground());
				setDefaultColor(labelColor.getBackground());
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
		return menuItemColor;
	}
	
	private JLabel createLabelColor(Color color) {
		
		final JLabel labelColor = new JLabel(Constants.SPACE);
		labelColor.setOpaque(true);
		labelColor.setBackground(color);
		
		return labelColor;
	}
	
	public void destruir() {
		removeAll();
		setLayout(null);
		
		arrowButton = null;
		labelSelectedColor = null;
		popUpMenu = null;
		selectedColor = null;
		defaultColor = null;
	}
	
	public LArrowButton getArrowButton() {
		if (arrowButton == null) {
			arrowButton = new LArrowButton(SwingConstants.SOUTH);
			arrowButton.setBorderPainted(false);
			
			Dimension prefSize = new Dimension(25, 40);
			arrowButton.setPreferredSize(prefSize);
			arrowButton.setMinimumSize(new Dimension(9,9));
			
			arrowButton.addMouseListener(new MouseListener() {
				public void mousePressed(MouseEvent e) {
					showPopup();
				}
				public void mouseClicked(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});
		}
		return arrowButton;
	}
	
	public Color getDefaultColor() {
		return defaultColor;
	}
	public Color getSelectedColor() {
		return selectedColor;
	}
	
	public LLabel getLabelSelectedColor() {
		if (labelSelectedColor == null) {
			labelSelectedColor = new LLabel();
			labelSelectedColor.setOpaque(true);
			Dimension prefSize = new Dimension(25, 40);
			labelSelectedColor.setPreferredSize(prefSize);
			labelSelectedColor.setText(Constants.VOID);
			labelSelectedColor.setBackground(Color.white);
			labelSelectedColor.setHorizontalAlignment(SwingConstants.CENTER);
			labelSelectedColor.setMargin(new Insets(0, 0, 0, 0));
			
			labelSelectedColor.addMouseListener(new MouseListener() {		
				public void mousePressed(MouseEvent e) {
					showPopup();
				}
				public void mouseClicked(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});
		}
		return labelSelectedColor;
	}
	
	private void showPopup() {
		getPopUpMenu().show(ColorButton.this, 0, ColorButton.this.getHeight());
	}
	
	private LPopupMenu getPopUpMenu() {
		if (popUpMenu == null) {
			popUpMenu = new LPopupMenu();
			popUpMenu.setRequestFocusEnabled(false);
		}
		return popUpMenu;
	}
		
	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}
	
	public void setSelectedColor(Color selectedColor) {
		Color oldValue = this.selectedColor;
		Color newValue = selectedColor;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.selectedColor = selectedColor;
			firePropertyChange(PROPERTY_selectedColor, oldValue, newValue);
			getLabelSelectedColor().setBackground(selectedColor);
			getArrowButton().setBackground(selectedColor);
			getLabelSelectedColor().setForeground(Colors.getInverseColor(selectedColor));
		}
	}
	
	private void addDefaultColors() {
		addColor(Color.red);
		addColor(new Color(255,192,64));
		addColor(Color.yellow);
		addColor(new Color(192,255,64));
		addColor(Color.green);
		addColor(new Color(64,255,192));
		addColor(Color.cyan);
		addColor(new Color(64,192,255));
		addColor(Color.blue);
		addColor(new Color(192,64,255));
		addColor(Color.magenta);
		addColor(new Color(255,64,192));
		addColor(Color.black);
		addColor(Color.white);
	}
}
