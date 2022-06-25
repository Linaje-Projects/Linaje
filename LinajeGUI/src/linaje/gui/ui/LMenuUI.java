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
package linaje.gui.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

import linaje.gui.LButtonProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.statics.Constants;
import linaje.utils.Strings;

public class LMenuUI extends BasicMenuUI {
	
	@SuppressWarnings("serial")
	private class LArrowMenuIcon implements Icon, Serializable {

		public void paintIcon(Component c, Graphics g, int x, int y) {
			
			AbstractButton menuItem = c != null ? (AbstractButton) c : new JMenu();
			ButtonModel model = menuItem.getModel();
			
			LButtonProperties buttonProperties = UISupportButtons.getButtonProperties(menuItem);
			
			Color markColor;
			if (model.isEnabled()) {
				markColor = buttonProperties.getMarkColor() != null ? buttonProperties.getMarkColor() : UISupportButtons.getForeground(menuItem);
				markColor = UISupportButtons.getStateColorValue(menuItem.getModel(), markColor);
			}
			else {
				markColor = UIManager.getColor("controlShadow");
			}
			
			GraphicsUtils.paintTriangle(g, x, y, getIconWidth(), menuItem.isEnabled(), markColor, SwingConstants.EAST, model.isRollover());
		}

		public int getIconWidth() {
			return getIconSize();
		}

		public int getIconHeight() {
			return getIconSize();
		}
		
		public int getIconSize() {
			
			if (menuItem.getText().equals(Constants.VOID) && UISupportButtons.getButtonProperties(menuItem).isIgnoreIconHeight()) {
				
				int w, h, size;
				
				w = menuItem.getSize().width;
				h = menuItem.getSize().height;
				
				if (w%2 != 0)
					w++;
				if (h%2 != 0)
					h++;
				
				// If there's no room to draw arrow, bail
				if (h < 5 || w < 5) {
					return 0;
				}
			
				size = Math.min(h/2, w/2);
				size = Math.max(size, 2);
				
				return size;
			}
			else {
				return (int)(menuItem.getFontMetrics(menuItem.getFont()).getHeight() / 2);
			}
		}
	}
	
	public LMenuUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent c) {
        return new LMenuUI();
    }
	
	protected void installDefaults() {
		super.installDefaults();
		LookAndFeel.installProperty(menuItem, "opaque", Boolean.FALSE);
		UIManager.put(getPropertyPrefix() + ".arrowIcon", new LArrowMenuIcon());
	}
   
	protected String getPropertyPrefix() {
		//En BasicMenuItemUI esperan el prefijo sin punto
		return LinajeLookAndFeel.getUIName(this.getClass());
        //return UISupport.getPropertyPrefix(this.getClass());
    }
	
	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
		
		AbstractButton b = (AbstractButton) c;
		String[] lineas = Strings.getLines(b.getText());
		int numLineas = lineas.length;
		//Si hay mas de una linea se pintará el icono relativo al texto en paintText(...)
		if (numLineas <= 1 || b.getHorizontalTextPosition() != SwingConstants.CENTER) {
			UISupportButtons.paintIcon(g, b, iconRect);
		}
	}

	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
		//UISupportButtons.paintText(g, menuItem, textRect, text);
		UISupportButtons.paintTextClassicAndIcon(g, menuItem, textRect, text);
	}
	
	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		
		ButtonModel buttonModel = menuItem.getModel();
		boolean rollover = buttonModel.isSelected();
		buttonModel.setRollover(rollover);
		if (rollover) {
			UISupportButtons.paintButtonBackground(g, menuItem, true);
			Rectangle rects = UISupportUtils.getBackgroundRects(menuItem);
			if (buttonModel.isEnabled()) {
				g.drawRect(rects.x, rects.y, rects.width, rects.height);
			}
		}
		else {
			super.paintBackground(g, menuItem, ColorsGUI.getFirstOpaqueParentBackground(menuItem.getParent()));
		}
		//Consola.println(menuItem.getText() + " rollover: "+rollover);
	}
}
