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
package linaje.gui.renderers;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;

public class UtilsRenderer {

	public static void paintBackgroundSelected(Graphics g, JComponent c) {

		Color color = ColorsGUI.getColorRollover();
		Color colorBorder = ColorsGUI.getColorRolloverDark();
					
		GraphicsUtils.paintGradientBackground(g, c, color);
		g.setColor(colorBorder);
		//g.drawRect(0,0,getWidth()-1,getHeight()-1);
		g.drawLine(0, 0, c.getWidth()-1, 0);
		g.drawLine(0, c.getHeight()-1, c.getWidth()-1, c.getHeight()-1);
	}
	
	public static void obscureEvenRows(Color background, int row, JComponent c) {
		if (row % 2 == 0) {
			//Oscurecemos las filas pares
			background = ColorsGUI.getColorRowEven(background);
		}	
		c.setBackground(background);
	}
	/*
	public static void paintCellBorders(Graphics g, boolean isFirstRow, boolean isFirstColumn, JComponent c) {
		
		paintCellBorders(g, isFirstRow, isFirstColumn, 1, 0, c);
	}
	public static void paintCellBorders(Graphics g, boolean isFirstRow, boolean isFirstColumn, int borderThickness, int marginLeft, JComponent c) {
		
		int top = isFirstRow ? borderThickness : 0;
		int left = isFirstColumn ? borderThickness : 0;
		int bottom = borderThickness;
		int right = borderThickness;
				
		Insets borderThicks = new Insets(top, left, bottom, right);
		Insets margins = new Insets(0,marginLeft,0,0);
		
		paintCellBorders(g, borderThicks, margins, c);
	}
	public static void paintCellBorders(Graphics g, boolean isFirstRow, boolean isFirstColumn, boolean isLastRow, boolean isLastColumn, int borderThickness, int marginLeft, JComponent c) {
		
		int top = isFirstRow ? borderThickness : 0;
		int left = isFirstColumn ? borderThickness : 0;
		int bottom = isLastRow ? borderThickness : 0;
		int right = isLastColumn ? borderThickness : 0;
		
		if (top != 0 || left != 0 || bottom != 0 || right != 0) {
			
			Insets borderThicks = new Insets(top, left, bottom, right);
			Insets margins = new Insets(0,marginLeft,0,0);
			
			paintCellBorders(g, borderThicks, margins, c);
		}
	}
	public static void paintCellBorders(Graphics g, Insets borderThicks, Insets margins, JComponent c) {

		if (borderThicks == null)
			borderThicks = new Insets(1,1,1,1);
		if (margins == null)
			margins = new Insets(0,0,0,0);
		
		g.setColor(ColorsGUI.getGridColor(c.getBackground()));
		if (borderThicks.left != 0) {
			int x1 = margins.left;
			int y1 = margins.top;
			int x2 = x1;
			int y2 = c.getHeight() - margins.bottom;
			g.drawLine(x1, y1, x2, y2);
		}
		if (borderThicks.right != 0) {
			int x1 = c.getWidth() - margins.right - 1;
			int y1 = margins.top;
			int x2 = x1;
			int y2 = c.getHeight() - margins.bottom;
			g.drawLine(x1, y1, x2, y2);
		}
		if (borderThicks.top != 0) {
			int x1 = margins.left;
			int y1 = margins.top;
			int x2 = c.getWidth() - margins.right;
			int y2 = y1;
			g.drawLine(x1, y1, x2, y2);
		}
		if (borderThicks.bottom != 0) {
			int x1 = margins.left;
			int y1 = c.getHeight() - margins.top - 1;
			int x2 = c.getWidth() - margins.right;
			int y2 = y1;
			g.drawLine(x1, y1, x2, y2);
		}
	}*/
}
