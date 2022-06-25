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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;

import sun.awt.AppContext;
import linaje.gui.LButtonProperties;
import linaje.gui.utils.GraphicsUtils;
import linaje.statics.Constants;
import linaje.utils.StateColor;

public class LCheckBoxUI extends LButtonUI {

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
			
			Color background = UISupportButtons.getBackground(checkBox);
			
			boolean markExpanded = buttonProperties.isMarkExpanded();
			boolean selected = model.isSelected();
			
			GraphicsUtils.paintCheckBox(g, x, y, background, selected, markColor, markExpanded, getIconSize());
		}

		public int getIconWidth() {
			return getIconSize();
		}

		public int getIconHeight() {
			return getIconSize();
		}
		
		private int getIconSize() {
			return (int)(button.getFontMetrics(button.getFont()).getHeight() / 1.33);
		}
	}

	protected static final Object L_CHECK_BOX_UI_KEY = new Object();
	
	public LCheckBoxUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent c) {
        AppContext appContext = AppContext.getAppContext();
        LCheckBoxUI lCheckBoxUI =(LCheckBoxUI) appContext.get(L_CHECK_BOX_UI_KEY);
        if (lCheckBoxUI == null) {
        	lCheckBoxUI = new LCheckBoxUI();
            appContext.put(L_CHECK_BOX_UI_KEY, lCheckBoxUI);
        }
        return lCheckBoxUI;
    }
	
	public void installDefaults(AbstractButton b){
        super.installDefaults(b);
        
        b.setMargin(new InsetsUIResource(2, 0, 2, 2));
        b.setIcon(new LCheckBoxIcon(b));
        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
        defaultTextIconGap = 3;
    }
	
	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
			Rectangle textRect, Rectangle iconRect) {
		//No hacemos nada, el foco se pintará en LCheckBoxIcon.paintIcon(...)
	}
	
}
