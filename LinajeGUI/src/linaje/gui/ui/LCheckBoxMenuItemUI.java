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
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import linaje.gui.LButtonProperties;
import linaje.gui.utils.GraphicsUtils;
import linaje.statics.Constants;
import linaje.utils.StateColor;

public class LCheckBoxMenuItemUI extends LMenuItemUI {
	
	@SuppressWarnings("serial")
	private class LCheckBoxIcon implements Icon, Serializable, UIResource {
		
		private AbstractButton button = null;
		private LCheckBoxIcon(AbstractButton b) {
			super();
			button = b;
		}
		
		public void paintIcon(Component c, Graphics g, int x, int y) {
			
			AbstractButton checkBox = c != null && c instanceof AbstractButton ? (AbstractButton) c : new JCheckBox(Constants.VOID, true);
			ButtonModel model = checkBox.getModel();
			boolean selected = model.isSelected();
			if (selected) {
				
				LButtonProperties buttonProperties = UISupportButtons.getButtonProperties(checkBox);
				
				Color markColor;
				if (model.isEnabled()) {
					Color foreground = UISupportButtons.getForeground(checkBox);
					markColor = buttonProperties.getMarkColor() != null ? buttonProperties.getMarkColor() : foreground;
					if (markColor instanceof StateColor) {
						Color rolloverColor = ((StateColor) markColor).getRolloverColor();
						if (rolloverColor != null)
							markColor = rolloverColor;
					}
				}
				else {
					markColor = UIManager.getColor("CheckBox.shadow");
				}
				
				boolean markExpanded = buttonProperties.isMarkExpanded();
				
				GraphicsUtils.paintCheckMarkComplex(g, x, y, markColor, getIconSize(), markExpanded);
			}
		}

		public int getIconWidth() {
			return getIconSize();
		}

		public int getIconHeight() {
			return getIconSize();
		}
		
		private int getIconSize() {
			return (int)(button.getFontMetrics(button.getFont()).getHeight() / 1.5);
		}
	}
	
	public LCheckBoxMenuItemUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent b) {
		return new LCheckBoxMenuItemUI();
    }
	
	protected void installDefaults(){
		UIManager.put(getPropertyPrefix() + ".checkIcon", new LCheckBoxIcon(menuItem));
		super.installDefaults();
    }
}
