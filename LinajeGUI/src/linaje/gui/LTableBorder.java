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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;

@SuppressWarnings("serial")
public class LTableBorder extends AbstractBorder implements UIResource {

	public LTableBorder() {
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		
		if (isBorderVisible(c)) {
			
			Color borderColor = c instanceof JTable ? ColorsGUI.getGridColor(c.getBackground()) : ColorsGUI.getColorBorderBright();
			
			GraphicsUtils.paintBorderColors(g, new Rectangle(x, y, width, height), borderColor, borderColor, borderColor, borderColor);
		}
	}

	private boolean isBorderVisible(Component c) {
		return c != null;
	}
	
	public Insets getBorderInsets(Component c, Insets insets) {
		int thick = isBorderVisible(c) ? 1 : 0;
		insets.set(thick, thick, thick, thick);
		return insets;
	}
}
