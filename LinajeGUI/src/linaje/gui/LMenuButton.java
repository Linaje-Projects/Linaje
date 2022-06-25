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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.GraphicsUtils;
import linaje.statics.Constants;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class LMenuButton extends LToggleButton {

	private List<String> elements = null;
	private Vector<LMenuItem> menuItems = null;
	private LMenuButton buttonAux = null;
	private LPopupMenu popup = null;
	private String selectedItem = null;
	//private boolean fixedElement = false;
	
	private static int SIZE_TRIANGLE = GeneralUIProperties.getInstance().getFontApp().getSize() - 2;
	public static int MARGIN_MENU_BUTTON = 20;
	public static int MARGIN_MENU_BUTTON_EXPANDABLE = 30;
	
	private boolean mouseOverExpand = false;
	private boolean inPopup = false;
	
	public LMenuButton(String... elements) {
		this(Lists.newList(elements));
	}
	public LMenuButton(List<String> elements) {
		super();
		setElements(elements);
	}
	
	public void destroy() {
		
		getMenuItems().removeAllElements();
		getPopup().removeAll();
		
		elements = null;
		menuItems = null;
		buttonAux = null;
		popup = null;
		selectedItem = null;
	}
	
	public void processMouseEvent(MouseEvent e) {

		if (e.getSource() == this) {
			int id = e.getID();
			switch (id) {
				case MouseEvent.MOUSE_RELEASED:
					//if (mouseOverExpand && canShowPopup()) {
						e.consume();
						setSelected(true);
						//getPopup().setVisible(false);
					//}
					break;
			}
			
			if (!e.isConsumed())
				super.processMouseEvent(e);
		}
    }
	protected void processMouseMotionEvent(MouseEvent e) {

		if (e.getSource() == this) {
			int id = e.getID();
			switch (id) {
				case MouseEvent.MOUSE_MOVED :
					if (canShowPopup()) {
						/*boolean overIcono = getIconExpandRects().contains(e.getX(), e.getY());
						if (overIcono != mouseOverExpand) {
							mouseOverExpand = overIcono;
							// Repintamos el icono de desplegar
							RepaintManager.currentManager(LMenuButton.this).addDirtyRegion(LMenuButton.this, getIconExpandRects().x, getIconExpandRects().y, getIconExpandRects().width, getIconExpandRects().height);
							getModel().setRollover(!mouseOverExpand);
							if (mouseOverExpand)
								mostrarPopup();
						}*/
						mostrarPopup();
					}
					else {
						getPopup().setVisible(false);
					}
					break;
			}
		}
		
		if (!mouseOverExpand)
			super.processMouseMotionEvent(e);
	}
	
	private void mostrarPopup() {
		
		getPopup().setPreferredSize(new Dimension(getWidth(), getPopup().getPreferredSize().height));
		Point locationPopup = getLocation();
		if (isInPopup())
			locationPopup.x = locationPopup.x + getPreferredSize().width;
		else
			locationPopup.y = locationPopup.y + getPreferredSize().height;
		getPopup().show(getParent(), locationPopup.x, locationPopup.y);
	}
	
	private void updateComponent() {
		
		getMenuItems().removeAllElements();
		
		if (getElements().size() > 1) {
			
			for (int i = 0; i < getElements().size(); i++) {
				String elemento = getElements().get(i);
				if (elemento != null) {
					LMenuItem menuItem = newMenuItem(elemento);
					
					if (menuItem != null) {
						menuItem.addActionListener(new ActionListener() {
							
							public void actionPerformed(ActionEvent e) {
								
								LMenuItem lMenuItem = (LMenuItem) e.getSource();
								if (lMenuItem.getModel().isEnabled()) {
									boolean isSelected = isSelected();
									setSelectedItem(lMenuItem.getText());
									if (!isSelected)
										setSelected(true);
									rellenarPopup();
									if (isSelected)
										fireItemStateChanged(new ItemEvent(LMenuButton.this, ItemEvent.ITEM_STATE_CHANGED, LMenuButton.this, ItemEvent.SELECTED));
								}
							}
						});
						getMenuItems().addElement(menuItem);
					}
				}
			}
		}
		
		rellenarPopup();
		
		boolean esDesplegable = canShowPopup();
		
		Insets margin = getMargin();
		if (esDesplegable) {
			margin.right = MARGIN_MENU_BUTTON_EXPANDABLE;
			setPreferredSize(calcularPreferredSize());
		}
		else {
			margin.right = MARGIN_MENU_BUTTON;
			setPreferredSize(null);
		}
		
		setMargin(margin);
		
		if (getSelectedItem() == null || getElements().indexOf(getSelectedItem()) == -1)
			setSelectedItem(getElements().get(0));
	}
	
	private void rellenarPopup() {
		
		getPopup().removeAll();
		getPopup().setPreferredSize(null);
		for (int i = 0; i < getMenuItems().size(); i++) {
			if (i != getSelectedIndex()) {
				LMenuItem menuItem = getMenuItems().elementAt(i);
				getPopup().add(menuItem);
			}
		}
	}
	
	private Dimension calcularPreferredSize() {
		
		int width = 0;
		int height = 0;
		for (int i = 0; i < getElements().size(); i++) {
			String element = getElements().get(i);
			getButtonAux().setText(element);
			width = Math.max(width, getButtonAux().getPreferredSize().width);
			height = Math.max(height, getButtonAux().getPreferredSize().height);
		}
		return new Dimension(width, height);
	}
	
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		
		if (canShowPopup()) {
			
			int x = getIconExpandRects().x;
			int y = getIconExpandRects().y;
			
			int orientation = isInPopup() ? SwingConstants.EAST : SwingConstants.SOUTH;
			
			Color markColor;
			if (getModel().isEnabled()) {
				markColor = getButtonProperties().getMarkColor() != null ? getButtonProperties().getMarkColor() : UISupportButtons.getForeground(this);
				markColor = UISupportButtons.getStateColorValue(getModel(), markColor);
			}
			else {
				markColor = UIManager.getColor("controlShadow");
			}
			GraphicsUtils.paintTriangle(g, x, y, SIZE_TRIANGLE, isEnabled(), markColor, orientation, false);
		}
	}
	
	public boolean canShowPopup() {
		
		boolean thereAreVisibleItems = false;
		for (int i = 0; !thereAreVisibleItems && i < getMenuItems().size(); i++) {
			if (i != getSelectedIndex()) {
				LMenuItem menuItem = getMenuItems().elementAt(i);
				if (menuItem.isVisible())
					thereAreVisibleItems = true;
			}
		}
		
		return thereAreVisibleItems;
	}
	
	private Rectangle getIconExpandRects() {
		
		int width = SIZE_TRIANGLE;
		int height = SIZE_TRIANGLE/2 + 1;
		int x = getWidth() - width - 10;
		int y = (getHeight() - height) / 2;
		
		return new Rectangle(x, y, width, height);
	}
	
	public List<String> getElements() {
		if (elements == null) {
			elements = Lists.newList();
			elements.add(super.getText());
		}
		return elements;
	}
	
	protected LMenuItem newMenuItem(String text) {
		
		LMenuItem menuItem = new LMenuItem(text);
		
		return menuItem;
	}
	public int getSelectedIndex() {
		
		int itemSelectedIndex = getElements().indexOf(getSelectedItem());
		if (itemSelectedIndex == -1)
			itemSelectedIndex = 0;
		
		return itemSelectedIndex;
	}

	public void setSelectedIndex(int newSelectedIndex) {
		
		if (newSelectedIndex < 0 || newSelectedIndex > getElements().size() - 1)
			newSelectedIndex = 0;
		
		setSelectedItem(getElements().get(newSelectedIndex));
		setSelected(true);
	}
	
	public void setElements(List<String> elements) {
		this.elements = elements;
		updateComponent();
	}

	private LMenuButton getButtonAux() {
		if (buttonAux == null) {
			buttonAux = new LMenuButton(getText());
			Insets margin = getMargin();
			margin.right = MARGIN_MENU_BUTTON_EXPANDABLE;
			buttonAux.setMargin(margin);
		}
		return buttonAux;
	}
	private Vector<LMenuItem> getMenuItems() {
		if (menuItems == null) {
			menuItems = new Vector<LMenuItem>();
		}
		return menuItems;
	}
	public LPopupMenu getPopup() {
		if (popup == null) {
			popup = new LPopupMenu();//ColorsGUI.COLOR_FONDO_MENUITEM_WEB_ROLLOVER);
			//popup.setBorderPainted(false);
		}
		return popup;
	}
	
	public boolean isInPopup() {
		return inPopup;
	}
	public void setInPopup(boolean inPopup) {
		this.inPopup = inPopup;
	}
	public String getText() {
		return getSelectedItem();
	}
	public void setText(String text) {
		setSelectedItem(text);
	}
	public String getSelectedItem() {
		if (selectedItem == null && !getElements().isEmpty())
			selectedItem = getElements().get(0);
		return selectedItem;
	}
	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
		rellenarPopup();
		repaint();
	}
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append(" - selIndex: ");
		sb.append(getSelectedIndex());
		sb.append(" - ");
		sb.append(getText());
		if (!getMenuItems().isEmpty()) {
			for (int i = 0; i < getMenuItems().size(); i++) {
				sb.append(Constants.LINE_SEPARATOR);
				LMenuItem menuItem = getMenuItems().elementAt(i);
				sb.append(menuItem != null ? menuItem.toString() : null);
			}
		}
		return sb.toString();
	}
}
