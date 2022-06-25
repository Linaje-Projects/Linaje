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
package linaje.gui.utils;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import linaje.gui.Icons;
import linaje.gui.ui.UISupport;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Lists;
import linaje.utils.Strings;

public class GraphicsUtils {

	public static int GRADIENT_TYPE_NONE = 0;
	public static int GRADIENT_TYPE_VERTICAL = 1;
	public static int GRADIENT_TYPE_HORIZONTAL = 2;
	public static int GRADIENT_TYPE_AUTO = 3;
	public static int GRADIENT_TYPE_ELIPSE = 4;
	
	public static void paintGradientBackground(Graphics g, Component component, Color color) {
		paintBackground(g, component, color, null, true);
	}

	public static void paintGradientBackground(Graphics g, Component component, Color color1, Color color2) {
		paintBackground(g, component, color1, color2, true);
	}

	public static void paintBackground(Graphics g, Component component, Color color) {
		paintBackground(g, component, color, null, false);
	}

	private static void paintBackground(Graphics g, Component component, Color color1, Color color2, boolean gradientBackground) {
		
		Insets borderInsets = UISupportUtils.getBorderInsetsIgnoreMargin(component);
		
		int x = borderInsets.left;
		int y = borderInsets.top;
		int width = component.getWidth() - borderInsets.left - borderInsets.right;
		int height = component.getHeight() - borderInsets.top - borderInsets.bottom;
		
		Rectangle rect = new Rectangle(x, y, width, height);
		
		if (gradientBackground) {
			Paint paint = color2 == null ? getGradientPaint(rect, color1, GraphicsUtils.GRADIENT_TYPE_VERTICAL) : getGradientPaint(rect, color1, color2, GraphicsUtils.GRADIENT_TYPE_VERTICAL, false, false);
			fillRect(g, rect, paint, 0);
		}
		else {
			g.setColor(color1);
			//g.fillRect(x, y, width, height);
			fillRect(g, rect, GraphicsUtils.GRADIENT_TYPE_NONE);
		}
	}

	public static void paintTriangle(Graphics g, int x, int y, int width, boolean enabled, Color color, int direction, boolean rollover) {
		
		Graphics2D g2d = (Graphics2D) g.create();
		
		try {
			
			if (color == null)
				color = ColorsGUI.getColorText();
			
			int mid, i, j;
		
			j = 0;
			
			x = x+width/4;
			y = y+width/4 + 1;
			
			int size = width/2;
			size = Math.max(size, 2);
			mid = size / 2;
			
			if (direction != SwingConstants.WEST && width%2 != 0)
				x++;
			
			if (size % 2 == 0) {
				if (direction == SwingConstants.WEST || direction == SwingConstants.EAST)
					y = y - 1;
				else if (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH)
					x = x - 1;
			}
				
			//Consola.println("ancho:" + ancho + ", size:" + size + ", mid:" + mid);
		
			double factor = 0.2;
			Color color1 = Colors.isColorDark(color) ? color : Colors.darker(color, factor);
			if (enabled && rollover) {
				color1 = Colors.brighter(color1, factor);
			}
			//Color color2 = Colors.brighter(color1, factor);	
			
			g2d.setColor(color);
		
			//GradientPaint gp = new GradientPaint(mid+x, y, color1, mid+x, size+y, color2, true);
			Rectangle gpRect = new Rectangle(x, y, width, width);
			Paint gp = getGradientPaint(gpRect, color1, GraphicsUtils.GRADIENT_TYPE_VERTICAL);
			g2d.setPaint(gp);
			
			//g.translate(x, y);
			
			int x1,y1,x2,y2;
			switch (direction) {
				case SwingConstants.NORTH :
					for (i = 0; i < size; i++) {
						x1=mid-i; y1=i; x2=mid+i; y2=i;
						//g.drawLine(x1+x, y1+y, x2+x, y2+y);
						Rectangle2D.Double rectangle = new Rectangle2D.Double(x1+x, y1+y, (i*2)+1, 1);	
						g2d.fill(rectangle);
						//Line2D.Double line = new Line2D.Double(x1+x, y1+y, x2+x, y2+y);
						//g2d.draw(line);
					}
					//if (!isEnabled) {
						x1=mid-i+1; y1=i; x2=mid+i-1; y2=i;
						g2d.setColor(UIManager.getColor("controlLtHighlight"));
						g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
					//}
					break;
				case SwingConstants.SOUTH :
					//if (!isEnabled) {
						//g2d.translate(1, 1);
						x++;
						y++;
						g2d.setColor(UIManager.getColor("controlLtHighlight"));
						for (i = size - 1; i >= 0; i--) {
							x1=mid-i; y1=j; x2=mid+i; y2=j;
							g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
							j++;
						}
						x--;
						y--;
						//g2d.translate(-1, -1);
						g2d.setColor(color);
						g2d.setPaint(gp);
					//}
		
					Point puntoArribaDer = null;
					j = 0;
					for (i = size - 1; i >= 0; i--) {
						x1=mid-i; y1=j; x2=mid+i; y2=j;
						//g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
						Rectangle2D.Double rectangle = new Rectangle2D.Double(x1+x, y1+y, (i*2)+1, 1);	
						g2d.fill(rectangle);
						if (i == size -1) {
							puntoArribaDer = new Point((int)(rectangle.x+rectangle.width), (int)rectangle.y);
						}
						j++;
					}
					//Pintamos un pixel mas de sombra arriba a la derecha
					if (puntoArribaDer != null) {
						g2d.setColor(UIManager.getColor("controlLtHighlight"));
						g2d.drawLine(puntoArribaDer.x, puntoArribaDer.y, puntoArribaDer.x, puntoArribaDer.y);
					}
					break;
				case SwingConstants.WEST :
					for (i = 0; i < size; i++) {
						x1=i; y1=mid-i; x2=i; y2=mid+i;
						//g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
						Rectangle2D.Double rectangle = new Rectangle2D.Double(x1+x, y1+y, 1, (i*2)+1);	
						g2d.fill(rectangle);
					}
					//if (!isEnabled) {
						g2d.setColor(UIManager.getColor("controlLtHighlight"));
						x1=i; y1=mid-i+1; x2=i; y2=mid+i-1;
						g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
					//}
					break;
				case SwingConstants.EAST :
					//if (!isEnabled) {
						//g2d.translate(1, 1);
						x++;
						y++;
						g2d.setColor(UIManager.getColor("controlLtHighlight"));
						for (i = size - 1; i >= 0; i--) {
							x1=j; y1=mid-i; x2=j; y2=mid+i;
							g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
							j++;
						}
						x--;
						y--;
						//g2d.translate(-1, -1);
						g2d.setColor(color);
						g2d.setPaint(gp);
					//}
		
					Point puntoAbajoIzq = null;
					j = 0;
					for (i = size - 1; i >= 0; i--) {
						x1=j; y1=mid-i; x2=j; y2=mid+i;
						//g2d.drawLine(x1+x, y1+y, x2+x, y2+y);
						Rectangle2D.Double rectangle = new Rectangle2D.Double(x1+x, y1+y, 1, (i*2)+1);
						g2d.fill(rectangle);
						if (i == size -1) {
							puntoAbajoIzq = new Point((int)rectangle.x, (int)(rectangle.y+rectangle.height));
						}
						j++;
					}
					//Pintamos un pixel mas de sombra abajo a la izquierda
					if (puntoAbajoIzq != null) {
						g2d.setColor(UIManager.getColor("controlLtHighlight"));
						g2d.drawLine(puntoAbajoIzq.x, puntoAbajoIzq.y, puntoAbajoIzq.x, puntoAbajoIzq.y);
					}
					break;
			}
			//g2d.translate(-x, -y);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		finally {
			g2d.dispose();
		}
	}

	public static void paintX(Graphics g, JComponent c, int thickness) {
		int size = Math.min(c.getWidth(), c.getHeight());
		Dimension dim = new Dimension(size, size);
		Point location = UISupportUtils.getLocation(c, dim, SwingConstants.CENTER, null);
		paintX(g, location.x, location.y, size, thickness);
	}

	public static void paintX(Graphics g, int x, int y, int size) {
		paintX(g, x, y, size, 1);
	}

	public static void paintX(Graphics g, int x, int y, int size, int thickness) {
		
		if (size > 0) {
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				//double d = Math.sqrt(Math.exp(size)*2);
				//int diagonal = Math.round(Math.round(d));
				//if (diagonal%2 == 0)
				//	size--;//Para que la x quede mas limpia intentamos que la diagonal sea impar
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				
				int tickMargin = (thickness/2)+1;
				g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.drawLine(x+tickMargin, y+tickMargin, x+size-tickMargin, y+size-tickMargin);
				g2d.drawLine(x+size-tickMargin, y+tickMargin, x+tickMargin, y+size-tickMargin);
			}
			finally {
				g2d.dispose();
			}
		}
	}

	public static void paintPlus(Graphics g, int x, int y, int size, int thickness) {
		
		if (size > 0) {
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				
				if ((size-thickness)%2 != 0)
					size--;
				
				int gap = thickness/2;
				
				//No usamos drawLine ya que no respeta bien thickness
				g2d.fillRect(x, y+(size/2) - gap, size, thickness); // --
				g2d.fillRect(x+(size/2) - gap, y, thickness, size); // |
			}
			finally {
				g2d.dispose();
			}
		}
	}

	public static void paintBarHoriz(Graphics g, int x, int y, int size, int thickness) {
		
		if (size > 0) {
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				
				if ((size-thickness)%2 != 0)
					size--;
				
				int gap = thickness/2;
				
				//No usamos drawLine ya que no respeta bien thickness
				g2d.fillRect(x, y+(size/2) - gap, size, thickness); // --
				//g2d.fillRect(x+(size/2) - gap, y, thickness, size); // |
			}
			finally {
				g2d.dispose();
			}
		}
	}

	public static void paintBarVert(Graphics g, int x, int y, int size, int thickness) {
		
		if (size > 0) {
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				
				if ((size-thickness)%2 != 0)
					size--;
				
				int gap = thickness/2;
				
				//No usamos drawLine ya que no respeta bien thickness
				//g2d.fillRect(x, y+(size/2) - gap, size, thickness); // --
				g2d.fillRect(x+(size/2) - gap, y, thickness, size); // |
			}
			finally {
				g2d.dispose();
			}
		}
	}

	public static void paintRectangle(Graphics g, int x, int y, int width, int height) {
		paintRectangle(g, x, y, width, height, 1);
	}

	public static void paintRectangle(Graphics g, int x, int y, int width, int height, int thickness) {
		Graphics2D g2d = (Graphics2D) g.create();
		try {
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
			
			
			//No usamos drawRect porque con thickness > 4 no respeta x e y de forma lineal
			//int gap = thickness - 1;
			//g2d.drawRect(x + gap, y + gap, width - gap - 1, height - gap - 1);
			
			//No usamos drawLine ya que no respeta bien thickness
			g2d.fillRect(x, y, width, thickness); // --
			g2d.fillRect(x, y+height-thickness, width, thickness); // --
			g2d.fillRect(x, y, thickness, height); // |
			g2d.fillRect(x+width-thickness, y, thickness, height); // |
		}
		finally {
			g2d.dispose();
		}
	}

	public static void paintCheckBox(Graphics g, int x, int y, Color background, boolean selected, Color markColor, boolean markExpanded) {
		paintCheckBox(g, x, y, background, selected, markColor, markExpanded, Icons.SIZE_ICONS_CHECK_RADIO);
	}

	public static void paintCheckBox(Graphics g, int x, int y, Color background, boolean selected, Color markColor, boolean markExpanded, int size) {
		
		final boolean classicAspect = false;
		final int ARC_SIZE = 4;
		
		if (background == null)
			background = ColorsGUI.getColorPanelsBright();
		if (markColor == null)
			markColor = ColorsGUI.getColorApp();
		
		//Borde
		Graphics2D g2d = (Graphics2D) g.create();
		try {
			if (classicAspect) {
				// outer bevel border
				g2d.setColor(UIManager.getColor("CheckBox.background"));
				g2d.fill3DRect(x, y, size, size, false);
				// inner bevel border
				g2d.setColor(UIManager.getColor("CheckBox.shadow"));
				g2d.fill3DRect(x + 1, y + 1, size - 2, size - 2, false);
			}
			else {
				final boolean antialias = true;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_DEFAULT);
				
				g2d.setColor(ColorsGUI.getColorText());
				g2d.drawRoundRect(x + 1, y + 1, size - 2, size - 2, ARC_SIZE, ARC_SIZE);
			}
			
			//Inside box
			g2d.setColor(background);
			Color gradientColor = Colors.getBrighterColor(background);
			GradientPaint gp = new GradientPaint(x+6, y+1, gradientColor, x+6, y+9, background, false);
			g2d.setPaint(gp);
			Rectangle2D.Double rectangle = new Rectangle2D.Double(x+2, y+2, size-3, size-3);	
			g2d.fill(rectangle);
		
			// paint check
			if (selected) {
				if (markExpanded)
					paintCheckMarkComplex(g2d, x+2, y, markColor, size-1, true);
				else
					paintCheckMark(g2d, x+3, y+3, markColor, size-5, false);
			}
		}
		finally {
			g2d.dispose();
		}
	}

	public static void paintCheckMarkComplex(Graphics g, int x, int y, Color colorMark, int size, boolean extended) {
		Color brightColor = Colors.brighter(colorMark, 0.3);
		if (extended) {
			paintCheckMark(g, x, y, brightColor, size, false);
			paintCheckMark(g, x, y-1, brightColor, size, true);
			paintCheckMark(g, x, y, colorMark, size, true);
		}
		else {
			paintCheckMark(g, x, y-1, brightColor, size, true);
			paintCheckMark(g, x, y, colorMark, size, false);
		}
	}

	public static void paintCheckMark(Graphics g, int x, int y, Color markColor, int size) {
		paintCheckMark(g, x, y, markColor, size, false);
	}

	public static void paintCheckMark(Graphics g, int x, int y, Color markColor, int size, boolean shortMark) {
		
		Color colorSombra = Colors.darker(markColor, 0.15);
		
		g.setColor(colorSombra);
		
		int thick = Math.max(size/4, 2);
		
		int xStart = x + size/3;
		int yStart = y + size - 1;
		
		int x1 = xStart;
		int y1 = yStart;
		int x2 = x1;
		int y2 = y1 - thick;
		
		//g.drawLine(x1, y1, x2, y2);
		fillRect(g, GraphicsUtils.lineToRectangle(x1, y1, x2, y2), GraphicsUtils.GRADIENT_TYPE_VERTICAL);
		int limit = x;
		if (shortMark)
			limit++;
		while (y2 > y && x2 > limit) {
			x1--;
			x2--;
			y1--;
			y2--;
			//g.drawLine(x1, y1, x2, y2);
			fillRect(g, GraphicsUtils.lineToRectangle(x1, y1, x2, y2), GraphicsUtils.GRADIENT_TYPE_VERTICAL);
		}
	
		x1 = xStart;
		y1 = yStart;
		x2 = x1;
		y2 = y1 - thick;
		limit = x+size-1;
		//if (shortMark)
		//	limit--;
		while (y2 > y && x2 < limit) {
			x1++;
			x2++;
			y1--;
			y2--;
			//g.drawLine(x1, y1, x2, y2);
			fillRect(g, GraphicsUtils.lineToRectangle(x1, y1, x2, y2), GraphicsUtils.GRADIENT_TYPE_VERTICAL);
		}
	}

	public static void paintRadioButton(Graphics g, int x, int y, Color background, boolean selected, Color markColor) {
		paintRadioButton(g, x, y, background, selected, markColor, Icons.SIZE_ICONS_CHECK_RADIO);
	}

	public static void paintRadioButton(Graphics g, int x, int y, Color background, boolean selected, Color markColor, int size) {
	
		Graphics2D g2d = (Graphics2D) g.create();
		try {
			if (background == null)
				background = ColorsGUI.getColorPanelsBright();
			if (markColor == null)
				markColor = ColorsGUI.getColorApp();
			
			// fill interior
			g2d.setColor(background);
			
			Rectangle iconRect = new Rectangle(x+2, y+1, size-2, size-2);
			
			Paint gp = getGradientPaint(iconRect, background, GraphicsUtils.GRADIENT_TYPE_VERTICAL);
			g2d.setPaint(gp);
			g2d.fillOval(iconRect.x, iconRect.y, iconRect.width, iconRect.height);
				
			final boolean antialias = true;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_DEFAULT);
			
			g2d.setColor(ColorsGUI.getColorText());
			g2d.drawOval(iconRect.x, iconRect.y, iconRect.width, iconRect.height);
			
			// indicate whether selected or not
			if (selected) {
		
				int markMargin = (int) (iconRect.width / 3.33);
				int markSize = iconRect.width - markMargin*2 + 1;
				g2d.setColor(markColor);
				Rectangle rectsMark = new Rectangle(iconRect.x + markMargin, iconRect.y + markMargin, markSize, markSize);
				Paint paint = getGradientPaint(rectsMark, markColor, GraphicsUtils.GRADIENT_TYPE_ELIPSE, true, false);
				fillOval(g2d, rectsMark, paint, 0);
			}
		}
		finally {
			g2d.dispose();
		}
	}

	public static void paintBorderRadioButtonClassic(Graphics g, Rectangle iconRect) {
		
		// outter left arc
		g.setColor(UIManager.getColor("RadioButton.shadow"));
		g.drawLine(iconRect.x + 2, iconRect.y - 2, iconRect.x + 5, iconRect.y - 2);
		g.drawLine(iconRect.x + 0, iconRect.y - 1, iconRect.x + 1, iconRect.y - 1);
		g.drawLine(iconRect.x + 6, iconRect.y - 1, iconRect.x + 7, iconRect.y - 1);
		g.drawLine(iconRect.x - 1, iconRect.y + 0, iconRect.x - 1, iconRect.y + 1);
		g.drawLine(iconRect.x - 2, iconRect.y + 2, iconRect.x - 2, iconRect.y + 5);
		g.drawLine(iconRect.x - 1, iconRect.y + 6, iconRect.x - 1, iconRect.y + 7);
	
		// outter right arc
		g.setColor(UIManager.getColor("RadioButton.highlight"));
		g.drawLine(iconRect.x + 0, iconRect.y + 8, iconRect.x + 1, iconRect.y + 8);
		g.drawLine(iconRect.x + 2, iconRect.y + 9, iconRect.x + 5, iconRect.y + 9);
		g.drawLine(iconRect.x + 6, iconRect.y + 8, iconRect.x + 7, iconRect.y + 8);
		g.drawLine(iconRect.x + 8, iconRect.y + 7, iconRect.x + 8, iconRect.y + 6);
		g.drawLine(iconRect.x + 9, iconRect.y + 5, iconRect.x + 9, iconRect.y + 2);
		g.drawLine(iconRect.x + 8, iconRect.y + 1, iconRect.x + 8, iconRect.y + 0);
	
		// inner left arc
		g.setColor(UIManager.getColor("RadioButton.darkShadow"));
		g.drawLine(iconRect.x + 2, iconRect.y - 1, iconRect.x + 5, iconRect.y - 1);
		g.drawLine(iconRect.x + 0, iconRect.y + 0, iconRect.x + 1, iconRect.y + 0);
		g.drawLine(iconRect.x + 6, iconRect.y + 0, iconRect.x + 7, iconRect.y + 0);
		g.drawLine(iconRect.x + 0, iconRect.y + 1, iconRect.x + 0, iconRect.y + 1);
		g.drawLine(iconRect.x - 1, iconRect.y + 2, iconRect.x - 1, iconRect.y + 5);
		g.drawLine(iconRect.x + 0, iconRect.y + 6, iconRect.x + 0, iconRect.y + 6);
	
		// inner right arc
		g.setColor(UIManager.getColor("RadioButton.background"));
		g.drawLine(iconRect.x + 0, iconRect.y + 7, iconRect.x + 1, iconRect.y + 7);
		g.drawLine(iconRect.x + 2, iconRect.y + 8, iconRect.x + 5, iconRect.y + 8);
		g.drawLine(iconRect.x + 6, iconRect.y + 7, iconRect.x + 7, iconRect.y + 7);
		g.drawLine(iconRect.x + 7, iconRect.y + 6, iconRect.x + 7, iconRect.y + 6);
		g.drawLine(iconRect.x + 8, iconRect.y + 5, iconRect.x + 8, iconRect.y + 2);
		g.drawLine(iconRect.x + 7, iconRect.y + 1, iconRect.x + 7, iconRect.y + 1);
	}

	public static void paintHighlightSearchBold(Graphics g, int xText, int yText, String text, String searchText, Color foreground, Color background) {
	
		try {
	
			if (searchText != null && !searchText.trim().equalsIgnoreCase(Constants.VOID)) {
	
				String[] textsSearched = Strings.split(searchText.trim(), Constants.SPACE);
				for (int i = 0; i < textsSearched.length; i++) {
					
					String highlightText = textsSearched[i];
					int indexSearch = text.toUpperCase().indexOf(highlightText.toUpperCase());
		
					if (indexSearch != -1) {
		
						FontMetrics fm = g.getFontMetrics();
						String previousText = text.substring(0, indexSearch);
						//Hacemos esto porque si cogemos el searchText original puede que cambien mayusculas o minusculas y cambie el tamaño del texto
						highlightText = text.substring(indexSearch, indexSearch + highlightText.length());
						
						int widthPreviousText = SwingUtilities.computeStringWidth(fm, previousText);
						int widthSearchText = SwingUtilities.computeStringWidth(fm, highlightText);
						
						//El texto en negrita tiene un tamaño distinto al texto plano,
						//por lo que para que quede perfecto creamos una imagen del texto en negrita y la pintamos con el tamaño del texto original
						Font boldFont = g.getFont().deriveFont(Font.BOLD);
						BufferedImage textImage = Icons.createImageTextClassic(highlightText, boldFont, Colors.darker(foreground, 0.1), background);
						
						int width = widthSearchText;
						int height = textImage.getHeight();
						int x = xText + widthPreviousText;
						int y = yText - g.getFontMetrics().getAscent();
						
						Graphics2D g2d = (Graphics2D) g.create();
						try {
							//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
							//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.drawImage(textImage, x, y, width, height, null);
							
							//Finalmente subrayamos el texto resaltado con un color tenue
							g2d.setColor(ColorsGUI.getColorBorderBright());
							g2d.drawLine(x, height-1, x+width, height-1);
						}
						finally {
							g2d.dispose();
						}
					}
				}
			}
		
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}

	public static void paintHighlightSearchBackground(Graphics g, int xText, int yText, String text, String searchText, Color color) {
	
		try {
	
			if (searchText != null && !searchText.trim().equalsIgnoreCase(Constants.VOID)) {
	
				int indexSearch = text.toUpperCase().indexOf(searchText.toUpperCase());
	
				if (indexSearch != -1) {
	
					FontMetrics fm = g.getFontMetrics();
					String previousText = text.substring(0, indexSearch);
					//Hacemos esto porque si cogemos el searchText original puede que cambien mayusculas o minusculas y cambie el tamaño del texto
					searchText = text.substring(indexSearch, indexSearch + searchText.length());
					
					int widthPreviousText = SwingUtilities.computeStringWidth(fm, previousText);
					int widthSearchText = SwingUtilities.computeStringWidth(fm, searchText);
					
					int x = xText + widthPreviousText;
					int y = 1;
					int width = widthSearchText;
					int height = fm.getHeight();
					
					Graphics2D g2d = (Graphics2D) g.create();
					try {
						g2d.setColor(color);
						fillRect(g2d, new Rectangle(x, y, width, height), GraphicsUtils.GRADIENT_TYPE_VERTICAL);
					}
					finally {
						g2d.dispose();
					}
					
				}		
			}
		
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}

	public static void paintBorderColors(Graphics g, Rectangle rects, Color colorTop, Color colorLeft, Color colorBottom, Color colorRight) {
		paintBorderColors(g, rects, colorTop, colorLeft, colorBottom, colorRight, 1);
	}

	public static void paintBorderColors(Graphics g, Rectangle rects, Color colorTop, Color colorLeft, Color colorBottom, Color colorRight, int thick) {
		
		if (thick > 0) {
			
			int x = rects.x;
			int y = rects.y;
			int w = rects.width;
			int h = rects.height;
			
			if (colorTop != null) {
				g.setColor(colorTop);
				if (thick == 1)
					g.drawLine(x, y, x+w-1, y); // top
				else
					g.fillRect(x, y, w-1, thick); // top
			}
			if (colorLeft != null) {
				g.setColor(colorLeft);
				if (thick == 1)
					g.drawLine(x, y+1, x, y+h-1); // left
				else
					g.fillRect(x, y+1, thick, h-1); // left
			}
			if (colorBottom != null) {
				g.setColor(colorBottom);
				if (thick == 1)
					g.drawLine(x+1, y+h-1, x+w-1, y+h-1); // bottom
				else
					g.fillRect(x+1, y+h-1-thick, w-1, thick); // bottom
			}
			if (colorRight != null) {
				g.setColor(colorRight);
				if (thick == 1)
					g.drawLine(x+w-1, y+1, x+w-1, y+h-1); // right
				else
					g.fillRect(x+w-1-thick, y+1, thick, h-1); // right
			}
		}
	}

	/**
	 * Pinta una linea en el fondo del componente
	 **/
	public static void paintLineBackground(Graphics g, JComponent component, Color color) {
		final int SELECTED_LINE_THICKNESS = 2;
		paintLineBackground(g, component, color, SwingConstants.BOTTOM, SELECTED_LINE_THICKNESS);
	}

	public static void paintLineBackground(Graphics g, JComponent component, Color color, int position, int thickness) {
		
		Rectangle componentRects = component.getBounds();
		componentRects.x = 0;
		componentRects.y = 0;
		if (component instanceof AbstractButton) {
			ButtonModel model = ((AbstractButton) component).getModel();
			if (model.isSelected() || model.isArmed())
				componentRects.y++;
		}
		Insets borderInsets = UISupportUtils.getBorderInsetsIgnoreMargin(component);
		Insets insets = new Insets(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right);
				
		GraphicsUtils.paintLineBackground(g, color, position, thickness, componentRects, insets);
	}

	public static void paintLineBackground(Graphics g, Color color, int position, int thickness, Rectangle componentRects, Insets componentInsets) {
		paintLineBackground(g, color, position, thickness, componentRects, componentInsets, GraphicsUtils.GRADIENT_TYPE_AUTO);
	}

	public static void paintLineBackground(Graphics g, Color color, int position, int thickness, Rectangle componentRects, Insets componentInsets, int gradientType) {
		Rectangle selectedTabLineRects = getLineBackgroundRects(position, thickness, componentRects, componentInsets);
		g.setColor(color);
		GraphicsUtils.fillRect(g, selectedTabLineRects, gradientType, true);
	}
	
	private static Rectangle getLineBackgroundRects(int position, int thickness, Rectangle componentRects, Insets componentInsets) {
		
		int x = componentRects.x + componentInsets.left;
		int y = componentRects.y + componentInsets.top;
		int w = componentRects.width - componentInsets.left - componentInsets.right;
		int h = componentRects.height - componentInsets.top - componentInsets.bottom;
		
		Rectangle lineRects;
		switch (position) {
			case SwingConstants.LEFT:
				lineRects = new Rectangle(x, y, thickness, h);
			    break;
			
			case SwingConstants.RIGHT:
				lineRects = new Rectangle(x+w-thickness, y, thickness, h);
			    break;
			
			case SwingConstants.BOTTOM:
				lineRects = new Rectangle(x, y+h-thickness, w, thickness);
				break;
			
			case SwingConstants.TOP:
			default:
				lineRects = new Rectangle(x, y, w, thickness);
		}
		
		return lineRects;
	}

	public static Paint getGradientPaint(Rectangle rects, Color color, int gradientType) {
		return getGradientPaint(rects, color, gradientType, false, false);
	}

	public static Paint getGradientPaint(Rectangle rects, Color color, int gradientType, boolean reflect, boolean invertColors) {
		
		Color darkerColor = color;
		final double LINEAR_FACTOR = 0.10;
		final double REFLECT_FACTOR = 0.02;
		final double brightFactor = reflect ? REFLECT_FACTOR : LINEAR_FACTOR;
		Color brighterColor = Colors.brighter(color, brightFactor);
		//if (color.getRGB() == Color.white.getRGB()) {
		float luminance = Colors.getLuminance(color);
		
		if (luminance > 0.9) {
			darkerColor = Colors.darker(color, brightFactor*0.8);
		}
		
		return getGradientPaint(rects, brighterColor, darkerColor, gradientType, reflect, invertColors);
	}

	public static Paint getGradientPaint(Rectangle rects, Color brighterColor, Color darkerColor, int gradientType, boolean reflect, boolean invertColors) {
		
		if (gradientType == GraphicsUtils.GRADIENT_TYPE_AUTO)
			gradientType = rects.width > rects.height ? GraphicsUtils.GRADIENT_TYPE_HORIZONTAL : GraphicsUtils.GRADIENT_TYPE_VERTICAL;
		
		int x = rects.x;
		int y = rects.y;
		int width = rects.width;
		int height = rects.height;
		
		if (width == 0)
			width = 2;
		if (height == 0)
			height = 2;
		
		float[] dist = {0.0f, 1.0f};
		Color color1 = invertColors ? darkerColor : brighterColor;
		Color color2 = invertColors ? brighterColor : darkerColor;
	    Color[] colors = {color1, color2};
	    
		Paint paint = null;
		CycleMethod cycleMethod = reflect ? CycleMethod.REFLECT : CycleMethod.NO_CYCLE;
		int x1,y1,x2,y2;
		if (gradientType == GraphicsUtils.GRADIENT_TYPE_VERTICAL) {
			
			x1 = x + width/2;
			y1 = y;
			x2 = x1;
			y2 = y + (reflect ? height/2 : height);
			
			Point2D start = new Point2D.Float(x1, y1);
			Point2D end = new Point2D.Float(x2, y2);
			paint = new LinearGradientPaint(start, end, dist, colors, cycleMethod, ColorSpaceType.LINEAR_RGB, new AffineTransform());
		}
		else if (gradientType == GraphicsUtils.GRADIENT_TYPE_HORIZONTAL) {
			
			x1 = x;
			y1 = y + height/2;
			x2 = x + (reflect ? width/2 : width);
			y2 = y1;
			
			//paint = new LinearGradientPaint(x1, y1, x2, y2, dist, colors, cycleMethod);
			Point2D start = new Point2D.Float(x1, y1);
			Point2D end = new Point2D.Float(x2, y2);
			paint = new LinearGradientPaint(start, end, dist, colors, cycleMethod, ColorSpaceType.LINEAR_RGB, new AffineTransform());
		}
		else if (gradientType == GraphicsUtils.GRADIENT_TYPE_ELIPSE) {
			
			x1 = x;
			y1 = y + height/2;
			x2 = x + (reflect ? width/2 : width);
			y2 = y1;
			
			paint = new RadialGradientPaint(rects, dist, colors, cycleMethod);
		}
		
		return paint;
	}

	public static void fillRect(Graphics g, Rectangle rect, int gradientType) {
		fillRect(g, rect, gradientType, false);
	}

	public static void fillRect(Graphics g, Rectangle rect, int gradientType, boolean reflect) {
		fillRect(g, rect, gradientType, reflect, false, 0);
	}

	public static void fillRect(Graphics g, Rectangle rect, int gradientType, boolean reflect, boolean invertColors, float transparency) {
		Paint paint = getGradientPaint(rect, g.getColor(), gradientType, reflect, invertColors);
		fillShape(g, rect, paint, transparency, gradientType == GraphicsUtils.GRADIENT_TYPE_ELIPSE);
	}

	public static void fillRect(Graphics g, Rectangle rect, Paint paint, float transparency) {
		fillShape(g, rect, paint, transparency, false);
	}

	public static void fillOval(Graphics g, Rectangle rect, Paint paint, float transparency) {
		fillShape(g, rect, paint, transparency, true);
	}

	public static void fillShape(Graphics g, Rectangle rect, Paint paint, float transparency, boolean fillOval) {
		
		if (transparency < 1) {
			
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				
				if (transparency > 0) {
					float alpha = 1 - transparency;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					int type = AlphaComposite.SRC_OVER;
					AlphaComposite alphaComposite = AlphaComposite.getInstance(type, alpha);
					g2d.setComposite(alphaComposite);
				}
			
				if (paint != null) {
					g2d.setPaint(paint);
					if (fillOval) {
						Ellipse2D.Double oval = new Ellipse2D.Double(rect.x, rect.y, rect.width, rect.height);
						g2d.fill(oval);
					}
					else {
						Rectangle2D.Double rectangle = new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height);
						g2d.fill(rectangle);
					}
				}
				else {
					if (fillOval)
						g2d.fillOval(rect.x, rect.y, rect.width, rect.height);
					else
						g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
				}
			}
			finally {
				g2d.dispose();
			}
		}
	}

	public static void drawString(Graphics g, String str, int x, int y) {
		
		boolean antialiasing = true;
		drawString(g, str, x, y, antialiasing);
	}

	public static void drawString(Graphics g, String str, int x, int y, boolean antialiasing) {
		
		Graphics2D g2d = (Graphics2D) g.create();
		try {
			if (antialiasing) {
				//VALUE_TEXT_ANTIALIAS_LCD_HRGB es menos agresivo que VALUE_TEXT_ANTIALIAS_ON
				//g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
				//g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				//g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
			
			g2d.drawString(str, x, y);
		}
		finally {
			g2d.dispose();
		}
	}

	public static void obscureRect(Graphics g, Rectangle rect, float alpha) {
		g.setColor(Color.black);
		fillRect(g, rect, null, 1 - alpha);
	}

	/**
	 * Convierte las coordenadas de una linea en un rectángulo
	 **/
	public static Rectangle lineToRectangle(int x1, int y1, int x2, int y2) {
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int width = Math.max(x1, x2) - x + 1;
		int height = Math.max(y1, y2) - y + 1;
		return new Rectangle(x, y, width, height);
	}
	
	
	public static void paintLabelLayouts(Graphics g, JLabel label, boolean paintTextLinesBounds, boolean paintGlobalTextRect, boolean paintViewRect) {
		
		if (paintTextLinesBounds || paintGlobalTextRect || paintViewRect) {
			
			Rectangle textViewRect = new Rectangle();
			Rectangle textRect = new Rectangle();
			Rectangle iconRect = new Rectangle();
			List<Rectangle> textLinesBounds = Lists.newList();
			List<Point> offsets = Lists.newList();
			
			UISupport.layoutTextIcon(label, textViewRect, textRect, textLinesBounds, iconRect, null, offsets, label.getText(), label.getIcon(), label.getIconTextGap(), label.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(), label.getHorizontalTextPosition());
			
			Graphics2D g2d = (Graphics2D) g.create();
			
			try {
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
				
				g2d.setColor(Color.orange);
				for (int i = 0; i < textLinesBounds.size(); i++) {
					Rectangle textBounds = textLinesBounds.get(i);
					g2d.drawRect(textBounds.x, textBounds.y, textBounds.width-1, textBounds.height-1);
				}
				
				if (paintGlobalTextRect) {
					g2d.setColor(Color.blue);
					g2d.drawRect(textViewRect.x, textViewRect.y, textViewRect.width-1, textViewRect.height-1);
				}
				
				if (paintViewRect) {
					g2d.setColor(Color.black);
					g2d.drawRect(0, 0, label.getWidth()-1, label.getHeight()-1);
				}
			}
			finally {
				g2d.dispose();
			}
		}
	}
}
