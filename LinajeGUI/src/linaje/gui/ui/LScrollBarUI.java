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
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import linaje.gui.LArrowButton;
import linaje.gui.utils.GraphicsUtils;
import linaje.utils.Colors;
import linaje.utils.StateColor;

public class LScrollBarUI extends BasicScrollBarUI {

	@SuppressWarnings("serial")
	private class LScrollBarArrowButton extends LArrowButton {

		private LScrollBarArrowButton(int direction) {
			super(direction);
			setBorder(BorderFactory.createEmptyBorder());
		}

		public Dimension getPreferredSize() {
			int size = getScrollSize();
			return new Dimension(size, size);
		}
	}
    
	public LScrollBarUI() {
		super();
	}

	public static ComponentUI createUI(JComponent c) {
		return new LScrollBarUI();
	}

	protected JButton createDecreaseButton(int orientation) {
		return createButton(orientation);
	}

	protected JButton createIncreaseButton(int orientation) {
		return createButton(orientation);
	}

	private JButton createButton(int orientation) {
		
		LScrollBarArrowButton arrowButton = new LScrollBarArrowButton(orientation);
		
		return arrowButton;
	}
	
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		super.paintTrack(g, c, trackBounds);
	}
	
	private int getScrollSize() {
		if (scrollbar != null) {
			return scrollbar.getOrientation() == JScrollBar.HORIZONTAL ? scrollbar.getHeight() : scrollbar.getWidth();
		}
		else {
			return scrollBarWidth;
		}
	}
	
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		
		//super.paintThumb(g, c, thumbBounds);
		
		JScrollBar sb = (JScrollBar) c;
		if (sb.isEnabled()) {
			
			if (sb.getUnitIncrement() == 1)
				sb.setUnitIncrement(getScrollSize());
			
			Color color = incrButton.getBackground();
			StateColor stateColor = color instanceof StateColor ? (StateColor) color : UISupportButtons.getDefaultButtonUIProperties(LButtonUI.class).getDefaultStateBackground();
			if (isDragging) {
				if (stateColor.getPressedColor() != null)
					color = stateColor.getPressedColor();
				else if (stateColor.getRolloverColor() != null) {
					color = stateColor.getRolloverColor();
					color = Colors.isColorDark(color) ? Colors.brighter(color, 0.01) : Colors.darker(color, 0.01);
				}
				else
					color = GeneralUIProperties.getInstance().getColorRolloverDark();
			}
			else if (isThumbRollover()) {
				if (stateColor.getRolloverColor() != null)
					color = stateColor.getRolloverColor();
				else
					color = GeneralUIProperties.getInstance().getColorRollover();
			}
			else {
				color = Colors.isColorDark(color) ? Colors.brighter(color, 0.2) : Colors.darker(color, 0.2);
			}
			Graphics2D g2d = (Graphics2D) g;
			g2d.setPaint(GraphicsUtils.getGradientPaint(thumbBounds, color, GraphicsUtils.GRADIENT_TYPE_AUTO));
			g2d.fill(thumbBounds);
		}
	}
}
