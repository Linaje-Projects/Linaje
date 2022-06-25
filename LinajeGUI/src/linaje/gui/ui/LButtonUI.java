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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import sun.awt.AppContext;
import linaje.gui.LButtonProperties;
import linaje.utils.Lists;

public class LButtonUI extends BasicButtonUI {

	protected static final Object L_BUTTON_UI_KEY = new Object();
	
	private Rectangle textViewRect = new Rectangle();
	private Rectangle iconRect = new Rectangle();
	private Rectangle textRect = new Rectangle();
	private List<Rectangle> textBounds = Lists.newList();
	private List<Point> offsets = Lists.newList();
	private Dimension preferredSize = new Dimension();
	
	public LButtonUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent b) {
        AppContext appContext = AppContext.getAppContext();
        LButtonUI lButtonUI = (LButtonUI) appContext.get(L_BUTTON_UI_KEY);
        if (lButtonUI == null) {
        	lButtonUI = new LButtonUI();
            appContext.put(L_BUTTON_UI_KEY, lButtonUI);
        }
        return lButtonUI;
    }
	
	/*protected void installDefaults(AbstractButton b) {	
		super.installDefaults(b);
    }*/
	public void update(Graphics g, JComponent c) {
		//if (c.isOpaque()) {
			AbstractButton b = (AbstractButton) c;
			UISupportButtons.paintButtonBackground(g, b);
        //}
        paint(g, c);
    }
	
	protected String getPropertyPrefix() {
        return UISupport.getPropertyPrefix(this.getClass());
    }
	
	public Dimension getPreferredSize(JComponent c) {
		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null)
        	return super.getPreferredSize(c);
        else {
			AbstractButton b = (AbstractButton) c;
			return UISupportButtons.getPreferredSize(b);
        }
	}
	
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
	}
	
	@Override
	public void paint(Graphics g, JComponent c) {
		
		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null)
        	super.paint(g, c);
        else
        	paintElements(g, c);
	}
	
	public void paintElements(Graphics g, JComponent c) {
		// TODO Auto-generated method stub
		AbstractButton button = (AbstractButton) c;
        String text = button.getText();
        Icon icon = UISupportButtons.getIcon(button);

        if ((icon == null) && (text == null)) {
            return;
        }

        UISupport.layoutTextIcon(button, textViewRect, textRect, textBounds, iconRect, preferredSize, offsets, button.getText(), icon, button.getIconTextGap(), button.getVerticalAlignment(), button.getHorizontalAlignment(), button.getVerticalTextPosition(), button.getHorizontalTextPosition());
		
		ButtonModel model = button.getModel();
		LButtonProperties buttonProperties = UISupportButtons.getButtonProperties(button);
 
		boolean enabled = model.isEnabled();
        int shiftOffset = 0;
        int mnemonicIndex = button.getDisplayedMnemonicIndex();
        boolean isShadowTextEnabled = false;
        int shadowPosition = -1;
        
        if (enabled) {
        	
        	shiftOffset = UISupportButtons.getShiftOffset(model, buttonProperties.getPressedSelectedOffset());
        	
        	isShadowTextEnabled = UISupportButtons.isShadowTextEnabled(button, buttonProperties.getShadowTextMode());
            if (isShadowTextEnabled) {
            	shadowPosition = UISupportButtons.getShadowPosition(buttonProperties, model);
            }
        }
        
        Color underlineColor = null;
        boolean subrrallar = false;
        
        UISupport.paintText(g, c, textViewRect, textBounds, offsets, text, enabled, shiftOffset, isShadowTextEnabled, shadowPosition, subrrallar, underlineColor);
		UISupportButtons.paintIcon(g, button, iconRect);
		
		/////////////////////////////////////////
		/*g.setColor(Color.black);
		Insets insets = button.getInsets();
		g.drawRect(insets.left, insets.top, button.getWidth()-insets.right-insets.left-1, button.getHeight()-insets.bottom-insets.top-1);
		
		g.setColor(Color.orange);
		for (int i = 0; i < textBounds.size(); i++) {
			Rectangle tb = textBounds.get(i);
			g.drawRect(tb.x, tb.y, tb.width-1, tb.height-1);
		}*/
		
		//g.setColor(Color.blue);
		//g.drawRect(textViewRect.x, textViewRect.y, textViewRect.width-1, textViewRect.height-1);
		/////////////////////////////////////////
	}
	/*
	protected void paintText(Graphics g, JComponent c, Rectangle textRect, String clippedText) {

		AbstractButton b = (AbstractButton) c;
		paintText(g, b, textRect, clippedText);
	}
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String clippedText) {
		//UISupportButtons.paintText(g, b, textRect, clippedText);
		UISupportButtons.paintTextAndIcon(g, b, textRect, clippedText);
	}
	
	public Dimension getPreferredSize(JComponent c) {
		AbstractButton b = (AbstractButton) c;
		//return UISupportButtons.getPreferredSize(b);
		return getPreferredSizeTest(c);
	}
	
	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
		
		AbstractButton b = (AbstractButton) c;
		String[] lineas = UtilsGUI.getLineas(b.getText());
		int numLineas = lineas.length;
		//Si hay mas de una linea se pintará el icono relativo al texto en paintText(...)
		if (numLineas <= 1 || b.getHorizontalTextPosition() != SwingConstants.CENTER) {
			UISupportButtons.paintIcon(g, b, iconRect);
		}
	}*/
}
