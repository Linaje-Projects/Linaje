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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import linaje.gui.utils.GraphicsUtils;
import linaje.utils.Colors;

public class LSliderUI extends BasicSliderUI {

	private boolean rolloverThumb = false;
	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {
		
		public void mouseMoved(MouseEvent e) {
			updateThumbState(e.getX(), e.getY());
		}
		public void mouseDragged(MouseEvent e) {}
	};
	
	private MouseListener mouseListener = new MouseListener() {
		
		public void mouseReleased(MouseEvent e) {
			rolloverThumb = false;
		}
		public void mouseExited(MouseEvent e) {
			rolloverThumb = false;
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
	};
	
	public LSliderUI(JSlider b) {
		super(b);
	}

	public static ComponentUI createUI(JComponent c) {
		return new LSliderUI((JSlider) c);
	}
	
	public void paintThumb(Graphics g)  {
   
		Rectangle rects = thumbRect;
		//Rectangle rects = new Rectangle(thumbRect.x+2, thumbRect.y+2, thumbRect.width-4, thumbRect.height-4);
		   
		Color bgColor = slider.getBackground();
		boolean hasFocus = false;
		
		if (slider.isEnabled()) {
		    
			hasFocus = slider.hasFocus();
		    
			GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
			
			bgColor = Colors.darker(bgColor, 0.3);
			
		    if (isDragging())
		    	bgColor = generalUIProperties.getColorRolloverDark();
		    else if (rolloverThumb)
		    	bgColor = generalUIProperties.getColorRollover();
		    else if (hasFocus)
		    	bgColor = Colors.darker(bgColor, 0.1);
		}
		g.setColor(bgColor);
		//Rectangle bgRects = new Rectangle(rects.x+1, rects.y+1, rects.width-2, rects.height-2);
		GraphicsUtils.fillRect(g, rects, GraphicsUtils.GRADIENT_TYPE_VERTICAL);
		
		Color shadowColor = Colors.darker(bgColor, hasFocus ? 0.5 : 0.4);
		Color lightColor = isDragging() ? shadowColor : Colors.brighter(bgColor, 0.1);
		
		GraphicsUtils.paintBorderColors(g, rects, lightColor, lightColor, shadowColor, shadowColor);
    }
	
	public void paintTrack(Graphics g)  {

		super.paintTrack(g);
		
		int thick = 2;
		Rectangle trackColorBounds;
		if (slider.getOrientation() == JSlider.HORIZONTAL)
			trackColorBounds = new Rectangle(trackRect.x, trackRect.y + (trackRect.height/2) - thick + 1, thumbRect.x, thick);
		else
			trackColorBounds = new Rectangle(trackRect.x +  (trackRect.width/2) - thick + 1, trackRect.y, thick, thumbRect.y);
		
		int gradientType = slider.getOrientation() == JSlider.HORIZONTAL ? GraphicsUtils.GRADIENT_TYPE_HORIZONTAL : GraphicsUtils.GRADIENT_TYPE_VERTICAL;
		g.setColor(GeneralUIProperties.getInstance().getColorApp());
		GraphicsUtils.fillRect(g, trackColorBounds, gradientType);
    }
	
	public void installUI(JComponent c)   {
		
		super.installUI(c);
		
		slider.setOpaque(false);
		//slider.setPaintTicks(true);
		//slider.setMajorTickSpacing((slider.getMaximum() - slider.getMinimum()) / 2);
		
		slider.removeMouseMotionListener(mouseMotionListener);
		slider.removeMouseListener(mouseListener);
		slider.addMouseMotionListener(mouseMotionListener);
		slider.addMouseListener(mouseListener);
	}
	
	@Override
	public void uninstallUI(JComponent c) {

		slider.removeMouseMotionListener(mouseMotionListener);
		slider.removeMouseListener(mouseListener);
		
		super.uninstallUI(c);
	}
	
	private void updateThumbState(int x, int y) {
		rolloverThumb = thumbRect.contains(x, y);
		slider.repaint(thumbRect);
    }
	
	@Override
	public void paintFocus(Graphics g) {
		
	}
}
