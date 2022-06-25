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

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;

import sun.awt.AppContext;
import linaje.gui.LLabel;
import linaje.statics.Constants;
import linaje.utils.Lists;

public class LLabelUI extends BasicLabelUI {

	protected static final Object L_LABEL_UI_KEY = new Object();
	
	private Rectangle textViewRect = new Rectangle();
	private Rectangle iconRect = new Rectangle();
	private Rectangle textRect = new Rectangle();
	private List<Rectangle> textBounds = Lists.newList();
	private List<Point> offsets = Lists.newList();
	
	public static final String TYPOGRAPHY_TEXT_MOST = "| TypogrÂphy |";
	public static final String TYPOGRAPHY_TEXT_COMMON = "TypogrAphy";
	
	public LLabelUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent c) {
	   AppContext appContext = AppContext.getAppContext();
	    LLabelUI lLabelUI = (LLabelUI) appContext.get(L_LABEL_UI_KEY);
	    if (lLabelUI == null) {
	    	lLabelUI = new LLabelUI();
	        appContext.put(L_LABEL_UI_KEY, lLabelUI);
	    }
	    return lLabelUI;
	}
	
	public Dimension getPreferredSize(JComponent c) {
		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null)
        	return super.getPreferredSize(c);
        else {
			return getPreferredLabelSize(c);
        }
	}
	
	private Dimension getPreferredLabelSize(JComponent c) {
	
		JLabel label = (JLabel) c;
		Icon icon = getIcon(label);
		String text = label.getText();
		if (text == null)
			text = Constants.VOID;
		
		int	horizontalTextPosition = label.getHorizontalTextPosition();			
		int	verticalTextPosition = label.getVerticalTextPosition();			
		int verticalAlignment = label.getVerticalAlignment();
		int horizontalAlignment = label.getHorizontalAlignment();
		
		int textIconGap = label.getIconTextGap();
		
		boolean ignoreIconHeight = false;
		boolean respectMaxMinSize = true;
		
		return UISupport.getPreferredSize(label, text, icon, textIconGap, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, ignoreIconHeight, respectMaxMinSize);
	}
	
	public void paint(Graphics g, JComponent c) {
		
		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null)
        	super.paint(g, c);
        else
        	paintElements(g, c);
	}

	public void paintElements(Graphics g, JComponent c) {
		
        JLabel label = (JLabel) c;
        String text = label.getText();
        Icon icon = getIcon(label);

        if ((icon == null) && (text == null)) {
            return;
        }

        UISupport.layoutTextIcon(label, textViewRect, textRect, textBounds, iconRect, null, offsets, label.getText(), icon, label.getIconTextGap(), label.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(), label.getHorizontalTextPosition());
		
		boolean enabled = label.isEnabled();
        int shiftOffset = 0;
        boolean isShadowTextEnabled = false;
        int shadowPosition = -1;
        
        boolean subrrallar = false;
        if (label instanceof LLabel)	{
        	LLabel lLabel = (LLabel) label;
			subrrallar = lLabel.isUnderlined();
			lLabel.setTextRect(new Rectangle(textRect));
        }
        
        Color underlineColor = null;
        
		UISupport.paintText(g, c, textViewRect, textBounds, offsets, text, enabled, shiftOffset, isShadowTextEnabled, shadowPosition, subrrallar, underlineColor);
		paintIcon(g, label, iconRect, icon);
    }
	
	private void paintIcon(Graphics g, JLabel label, Rectangle iconRect, Icon icon) {
		
		int iconShiftOffset = 0;
        boolean isShadowTextEnabled = false;
        int shadowPosition = -1;		        
		boolean iconForegroundEnabled = false;
		 
		UISupport.paintIcon(g, label, iconRect, icon, iconShiftOffset, iconForegroundEnabled, isShadowTextEnabled, shadowPosition);
	}

	private Icon getIcon(JLabel label) {
		Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
		return icon;
	}
}
