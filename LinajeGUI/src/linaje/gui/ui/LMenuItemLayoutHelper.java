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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import sun.swing.MenuItemLayoutHelper;
import linaje.gui.utils.UISupportUtils;
import linaje.utils.Strings;

public class LMenuItemLayoutHelper extends MenuItemLayoutHelper {

	public LMenuItemLayoutHelper() {
		super();
	}

	public LMenuItemLayoutHelper(JMenuItem mi, Icon checkIcon,
			Icon arrowIcon, Rectangle viewRect, int gap, String accDelimiter,
			boolean isLeftToRight, Font font, Font accFont,
			boolean useCheckAndArrow, String propertyPrefix) {
		super(mi, checkIcon, arrowIcon, viewRect, gap, accDelimiter,
				isLeftToRight, font, accFont, useCheckAndArrow, propertyPrefix);
	}

	protected void calcWidthsAndHeights() {
		
		super.calcWidthsAndHeights();
		
		String[] lineas = Strings.getLines(getMenuItem().getText());
		if (lineas.length > 1) {
			
			Dimension textSize = UISupportUtils.getTextSize(getMenuItem(), lineas);
			getTextSize().setWidth(textSize.width);
			getTextSize().setHeight(textSize.height);
			
			 // labelRect
	        if (isColumnLayout()) {
	        	
	        	getLabelSize().setWidth(getIconSize().getWidth() + textSize.width + getGap());
	        	getLabelSize().setHeight(max(getCheckSize().getHeight(), getIconSize().getHeight(),
	                    textSize.height, getAccSize().getHeight(), getArrowSize().getHeight()));
	        }
	        else {
	            
	        	Dimension  labelSize = UISupportUtils.getPreferredSize(getMenuItem(), lineas, getIcon(), getGap());
	            labelSize.width += getLeftTextExtraWidth();
	            
	            getLabelSize().setHeight(labelSize.height);
	            getLabelSize().setWidth(labelSize.width);
	        }
		}
	}
}
