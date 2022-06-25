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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicBorders.FieldBorder;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;
import javax.swing.text.JTextComponent;

import linaje.gui.LLabel;
import linaje.statics.Constants;
import sun.swing.SwingUtilities2;

/**
 * Estos son métodos de pintado clásicos que NO SE USAN en el pintado y layout la mayoría de componentes de LinajeLookAndFeel, que se hace en UISupport
 * Se mantienen porque todavía se usan en algunos casos en los que no se puede aplicar el layout y pintado de UISupport o porque todavía no se han podido migrar
 **/
public class UISupportUtils {

	public static Insets getBorderInsets(Component c) {
		return getBorderInsets(c, false);
	}

	public static Insets getBorderInsetsIgnoreMargin(Component c) {
		return getBorderInsets(c, true);
	}

	public static Insets getBorderInsets(Component c, boolean ignoreMargin) {
		
		Insets borderInsets = new Insets(0,0,0,0);
		boolean borderPainted = true;
		if (c instanceof AbstractButton)
			borderPainted = ((AbstractButton) c).isBorderPainted();
		
		Border border = borderPainted && c instanceof JComponent ? ((JComponent)c).getBorder() : null;
		if (border != null) {
			borderInsets = border.getBorderInsets(c);
		
			if (ignoreMargin) {
				
				Insets margin = null;
				if (c instanceof AbstractButton) {
			
					AbstractButton button = (AbstractButton) c;
					margin = button.getMargin();
				}
				else if (c instanceof JTextComponent) {
					JTextComponent textComponent = (JTextComponent) c;
					margin = textComponent.getMargin();
				}
						
				if (margin != null && UISupportUtils.isMarginCompatibleBorder(border)) {
					borderInsets.left = borderInsets.left - margin.left;
					borderInsets.right = borderInsets.right - margin.right;
					borderInsets.top = borderInsets.top - margin.top;
					borderInsets.bottom = borderInsets.bottom - margin.bottom;
				}
			}
		}
		return borderInsets;
	}
	
	public static Rectangle getBackgroundRects(JComponent c) {
		
		Insets borderInsets = getBorderInsetsIgnoreMargin(c);
		
		int x = borderInsets.left;
		int y = borderInsets.top;
		int width = c.getWidth() - borderInsets.left - borderInsets.right;
		int height = c.getHeight() - borderInsets.top - borderInsets.bottom;
		
		return new Rectangle(x, y, width, height);
	}

	
	/**
	 * Nos da las coordenas en las que tendremos que pintar el texto 'text' con FontMetrics 'fm'
	 * alineado según especifiquemos en 'aligment' sobre 'container'
	 * Podemos ajustar mas la posición mediante 'insetsExtra'
	 **/
	public static final Point getLocation(JComponent container, String text, FontMetrics fm, int alignment, Insets insetsExtra) {
		
		//Obtenemos el tamaño del texto
		int widthTexto = fm.stringWidth(text);
		int heightTexto = fm.getFont().getSize();
	
		Point location = getLocation(container, new Dimension(widthTexto, heightTexto), alignment, insetsExtra);
		
		//Ajustamos aún mas la posición del texto
		location.y = location.y + fm.getAscent();
			
		return location;
	}

	/**
	 * Nos da las coordenas en las que tendremos que pintar el icono 'icon'
	 * alineado según especifiquemos en 'aligment' sobre 'container'
	 * Podemos ajustar mas la posición mediante 'insetsExtra'
	 **/
	public static final Point getLocation(JComponent container, Icon icon, int alignment, Insets insetsExtra) {
		
		Dimension imageSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		Point coordenadas = getLocation(container, imageSize, alignment, insetsExtra);
	
		return coordenadas;
	}

	/**
	 * Nos da las coordenas en las que tendremos que pintar algo con tamaño 'componentSize'
	 * alineado según especifiquemos en 'aligment' sobre 'container'
	 * Podemos ajustar mas la posición mediante 'insetsExtra'
	 **/
	public static final Point getLocation(JComponent container, Dimension componentSize, int alignment, Insets insetsExtra) {
		
		Insets insets = (Insets) container.getInsets().clone();
		if (insetsExtra != null) {
			insets.top += insetsExtra.top;
			insets.left += insetsExtra.left;
			insets.bottom += insetsExtra.bottom;
			insets.right += insetsExtra.right;
		}
		
		Rectangle viewRect = container.getBounds();
		viewRect.x = insets.left;
		viewRect.y = insets.top;
		viewRect.width -= (insets.left + insets.right);
		viewRect.height -= (insets.top + insets.bottom);
		
		return getLocation(viewRect, componentSize, alignment);
	}

	/**
	 * Nos da las coordenas en las que tendremos que pintar algo con tamaño 'componentSize'
	 * alineado según especifiquemos en 'aligment' sobre el espacio de visualización 'viewRect' 
	 **/
	public static final Point getLocation(Rectangle viewRect, Dimension componentSize, int alignment) {
		
		int width = componentSize.width;
		int height = componentSize.height;
		//Obtenemos las coordenadas según la alineación
		
		int xCenter = viewRect.x + (viewRect.width - width) / 2;
		int xLeft = viewRect.x;
		int xRight = viewRect.x + viewRect.width - width;
		
		int yCenter = viewRect.y + (viewRect.height - height) / 2;
		int yTop = viewRect.y;
		int yBottom = viewRect.y + viewRect.height - height;
		
		int x, y;
		switch (alignment) {
			
			case SwingConstants.WEST: {
				x = xLeft;
				y = yCenter;
				break;
			}
			case SwingConstants.EAST: {
				x = xRight;
				y = yCenter;
				break;
			}
			case SwingConstants.NORTH: {
				x = xCenter;
				y = yTop;
				break;
			}
			case SwingConstants.SOUTH: {
				x = xCenter;
				y = yBottom;
				break;
			}
			case SwingConstants.NORTH_WEST: {
				x = xLeft;
				y = yTop;
				break;
			}
			case SwingConstants.NORTH_EAST: {
				x = xRight;
				y = yTop;
				break;
			}
			case SwingConstants.SOUTH_WEST: {
				x = xLeft;
				y = yBottom;
				break;
			}
			case SwingConstants.SOUTH_EAST: {
				x = xRight;
				y = yBottom;
				break;
			}
			default: {
				//Por defecto centramos la imagen o el texto
				x = xCenter;
				y = yCenter;
				break;
			}
		}
			
		return new Point(x, y);
	}

	public static Dimension getPreferredComponentSize(JComponent c, String text, Icon icon, int textIconGap) {
	    
		int verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition;
		if (c instanceof AbstractButton) {
	
			AbstractButton b = (AbstractButton) c;
			verticalAlignment = b.getVerticalAlignment();
			horizontalAlignment = b.getHorizontalAlignment();
			verticalTextPosition = b.getVerticalTextPosition();
			horizontalTextPosition = b.getHorizontalTextPosition();
			if (textIconGap == -1 && icon != null)
				textIconGap = b.getIconTextGap();
			if (text == null)
				text = b.getText();
		}
		else if (c instanceof JLabel) {
	
			JLabel lbl = (JLabel) c;
			verticalAlignment = lbl.getVerticalAlignment();
			horizontalAlignment = lbl.getHorizontalAlignment();
			verticalTextPosition = lbl.getVerticalTextPosition();
			horizontalTextPosition = lbl.getHorizontalTextPosition();
			if (textIconGap == -1 && icon != null)
				textIconGap = lbl.getIconTextGap();
			if (text == null)
				text = lbl.getText();
		}
		else {
	
			verticalAlignment = SwingConstants.CENTER;
			horizontalAlignment = SwingConstants.CENTER;
			verticalTextPosition = SwingConstants.CENTER;
			horizontalTextPosition = SwingConstants.TRAILING;
			if (textIconGap == -1)
				textIconGap = 4;
			if (text == null)
				text = Constants.VOID;
		}
		
	    Font font = c.getFont();
	    FontMetrics fm = c.getFontMetrics(font);
	      
	    Rectangle iconR = new Rectangle();
	    Rectangle textR = new Rectangle();
	    Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
	
	    SwingUtilities.layoutCompoundLabel(
	        c, fm, text, icon,
	        verticalAlignment, horizontalAlignment,
	        verticalTextPosition, horizontalTextPosition,
	        viewR, iconR, textR, (text == null ? 0 : textIconGap)
	    );
	
	    /* The preferred size of the button is the size of 
	     * the text and icon rectangles plus the buttons insets.
	     */
	
	    Rectangle r = iconR.union(textR);
	
	    Insets insets = c.getInsets();
	    r.width += insets.left + insets.right;
	    r.height += insets.top + insets.bottom;
	
	    return r.getSize();
	}

	public static Dimension getPreferredSize(JComponent c, String[] lines, Icon icon, int iconTextGap) {
		
		Dimension preferredSize = getPreferredComponentSize(c, lines[0], icon, iconTextGap);
		if (lines.length > 1) {
			
			for (int i = 0; i < lines.length; i++) {
		
				Dimension preferredSizeAux = getPreferredComponentSize(c, lines[i], icon, iconTextGap);
				if (preferredSizeAux.width > preferredSize.width)
					preferredSize = preferredSizeAux;
			}
			
			FontMetrics fm = c.getFontMetrics(c.getFont());
			preferredSize.height += (lines.length - 1) * fm.getHeight();
		}
		return preferredSize;
	}

	public static String layoutCompoundLabel(FontMetrics fm, JComponent c, String text, Rectangle textRect, int textIconGap, Icon icon) {
		return layoutCompoundLabel(fm, c, text, textRect, null, textIconGap, icon, 0, 1);
	}

	public static String layoutCompoundLabel(FontMetrics fm, JComponent c, String text, Rectangle textRect, int textIconGap, Icon icon, int line, int lines) {
		return layoutCompoundLabel(fm, c, text, textRect, null, textIconGap, icon, line, lines);
	}

	public static String layoutCompoundLabel(FontMetrics fm, JComponent c, String text, Rectangle textRect, Rectangle iconRect, int textIconGap, Icon icon, int line, int lines) {
		
		int verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition;
		if (c instanceof AbstractButton) {
	
			AbstractButton b = (AbstractButton) c;
			verticalAlignment = b.getVerticalAlignment();
			horizontalAlignment = b.getHorizontalAlignment();
			verticalTextPosition = b.getVerticalTextPosition();
			horizontalTextPosition = b.getHorizontalTextPosition();
		}
		else if (c instanceof JLabel) {
	
			JLabel lbl = (JLabel) c;
			verticalAlignment = lbl.getVerticalAlignment();
			horizontalAlignment = lbl.getHorizontalAlignment();
			verticalTextPosition = lbl.getVerticalTextPosition();
			horizontalTextPosition = lbl.getHorizontalTextPosition();
		}
		else {
	
			verticalAlignment = SwingConstants.CENTER;
			horizontalAlignment = SwingConstants.CENTER;
			verticalTextPosition = SwingConstants.CENTER;
			horizontalTextPosition = SwingConstants.TRAILING;
		}
		
		return layoutCompoundLabel(fm, c, text, textRect, iconRect, textIconGap, icon, line, lines, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition);
	}

	public static String layoutCompoundLabel(FontMetrics fm, JComponent c, String text, Rectangle textRect, Rectangle iconRect, int textIconGap, Icon icon, int line, int lines, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition) {
		
		Insets i = c.getInsets();
	
		Rectangle viewRect = new Rectangle();
		if (iconRect == null)
			iconRect = new Rectangle();
		if (textRect == null)
			textRect = new Rectangle();
		
		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = c.getWidth() - (i.right + viewRect.x);
		viewRect.height = c.getHeight() - (i.bottom + viewRect.y);
	
		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
	
		if (text == null)
			textIconGap = 0;
		
		// layout the text and icon
		String layoutText = SwingUtilities.layoutCompoundLabel(
			c, fm, text, icon, 
			verticalAlignment, horizontalAlignment,
			verticalTextPosition, horizontalTextPosition,
			viewRect, iconRect, textRect, 
			textIconGap
		);
	
		final int lineSpacing = fm.getDescent() + fm.getLeading();
		int realFontSize = c.getFont().getSize() - Math.abs(fm.getAscent() - c.getFont().getSize());
		int realTotalSize = realFontSize*lines + lineSpacing*(lines - 1);
		int zeroPositionText = fm.getAscent() - fm.getHeight();
		
		int iconTextDifference = 0;
		if (iconRect.height > 0)
			iconTextDifference = (realFontSize - iconRect.height)/2;
	
		Insets borderInsets = getBorderInsets(c);
			
		if (verticalAlignment == SwingConstants.TOP) {
	
			textRect.y = i.top + zeroPositionText;
	
			if (iconRect.height > 0) {
				
				if (iconRect.height >= c.getHeight() - borderInsets.top - borderInsets.bottom) {
	
					iconRect.y = borderInsets.top;
				}
				else {
	
					//Aumentamos la diferencia por arriba en caso de que los margenes no coincidan por arriba y por abajo (ponemos mas arriba)
					if (c instanceof JLabel && (!((realFontSize - iconRect.height) % 2 != 0))) {
						if (iconTextDifference > 0)
							iconTextDifference++;
						else
							iconTextDifference--;
					}
					
					iconRect.y = i.top + iconTextDifference;
				}
			}
			
			int bottomPositionText = textRect.y + realTotalSize - zeroPositionText;
			int marginExcess = bottomPositionText - c.getHeight(); 
			if (marginExcess > 0) {
				textRect.y = textRect.y - marginExcess;
				iconRect.y = iconRect.y - marginExcess;
			}
	
			if (iconRect.height > 0) {
	
				int bottomPositionIcon = iconRect.y + iconRect.height;
				marginExcess = bottomPositionIcon - c.getHeight(); 
				if (marginExcess > 0)
					iconRect.y = iconRect.y - marginExcess;
			}
		
			if (textRect.y < fm.getAscent() - fm.getHeight())
				textRect.y = fm.getAscent() - fm.getHeight();
			if (iconRect.y < borderInsets.top)
				iconRect.y = borderInsets.top;
		}
		else if (verticalAlignment == SwingConstants.CENTER) {
	
			textRect.y = (c.getHeight() - realTotalSize + 1) / 2;
			textRect.y = textRect.y + zeroPositionText;
			
			iconRect.y = (c.getHeight() - iconRect.height + 1) / 2;
	
			if (lines == 1 && c instanceof JLabel) {
				//Este ajuste es para que se quede igual que en una label normal de una linea donde no se centra con exactitud (Para no tener problemas con componentes hechos anteriormente)
				textRect.y = textRect.y + 1;
				//iconRect.y = iconRect.y + 1;
			}
			
		}
		else if (verticalAlignment == SwingConstants.BOTTOM) {
	
			textRect.y = c.getHeight() - realFontSize - i.bottom + zeroPositionText;
			textRect.y = textRect.y - (realFontSize + lineSpacing) * (lines - 1);
	
			if (iconRect.height > 0) {
				
				if (iconRect.height >= c.getHeight() - borderInsets.bottom) {
	
					iconRect.y = c.getHeight() - iconRect.height - borderInsets.bottom;
				}
				else {
					
					iconRect.y = c.getHeight() - iconRect.height - i.bottom;
					iconRect.y = iconRect.y - iconTextDifference;
				}
			}
			
			int topPositionText = textRect.y - zeroPositionText;
			int marginExcess = 0 - topPositionText;
			if (marginExcess > 0) {
				textRect.y = textRect.y + marginExcess;
				iconRect.y = iconRect.y + marginExcess;
				if (iconRect.y > c.getHeight() - iconRect.height)
					iconRect.y = c.getHeight() - iconRect.height;
			}
	
			if (iconRect.height > 0) {
	
				int topPositionIcon = iconRect.y;
				marginExcess = 0 - topPositionIcon;
				if (marginExcess > 0)
					iconRect.y = iconRect.y + marginExcess;
			}
		
			if (textRect.y > c.getHeight() - realFontSize)
				textRect.y = c.getHeight() - realFontSize;
			if (iconRect.y > c.getHeight() - iconRect.height - borderInsets.bottom)
				iconRect.y = c.getHeight() - iconRect.height - borderInsets.bottom;
		}
	
		int yLine = 0;
		if (line > 0)
			yLine = (realFontSize + lineSpacing) * line;
			
		if (c instanceof LLabel && line == lines - 1)//Este es el textRect de todas las lineas en conjunto
			((LLabel) c).setTextRect(new Rectangle(textRect.x, textRect.y - zeroPositionText, textRect.width, textRect.height + yLine + zeroPositionText));
	
		textRect.y = textRect.y + yLine;
		
		if (c instanceof JLabel)
			textRect.y = textRect.y + c.getFont().getSize();
		
		return layoutText;
	}

	public static Rectangle getIconRectRelative(AbstractButton b, Rectangle textRect) {
	
		Icon icon = b.getIcon();
		int horizontalTextPosition = b.getHorizontalTextPosition();
		int	verticalTextPosition = b.getVerticalTextPosition();			
		int iconTextGap = b.getIconTextGap();
		
		return getIconRectRelative(textRect, icon, iconTextGap, horizontalTextPosition, verticalTextPosition);
	}

	public static Rectangle getIconRectRelative(Rectangle textRect, Icon icon, int iconTextGap, int horizontalTextPosition, int verticalTextPosition) {
		
		int x = 0;
		int y = 0;
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		
		if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
			x = textRect.x + textRect.width + iconTextGap;
		else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
			x = textRect.x - w - iconTextGap;
		else //CENTER
			x = textRect.x + (textRect.width - w) / 2;
		
		if (verticalTextPosition == SwingConstants.TOP)
			y = textRect.y + textRect.height + iconTextGap;
		else if (verticalTextPosition == SwingConstants.BOTTOM)
			y = textRect.y - h - iconTextGap;
		else //CENTER
			y = textRect.y + (textRect.height - h) / 2;
		
		return new Rectangle(x, y, w, h);
	}

	public static boolean isMarginCompatibleBorder(Border border) {
		
		boolean isMarginCompatibleBorder = false;
		if (border != null) {
			if (border instanceof CompoundBorder)
				border = ((CompoundBorder) border).getInsideBorder();
			isMarginCompatibleBorder = border instanceof MarginBorder || border instanceof FieldBorder;
		}
		return isMarginCompatibleBorder;
	}

	public static Dimension getTextSize(JComponent c, String[] lines) {
		
		Dimension textSize = getTextSize(c, lines[0]);
		if (lines.length > 1) {
			
			for (int i = 0; i < lines.length; i++) {
		
				Dimension textSizeAux = getTextSize(c, lines[i]);
				if (textSizeAux.width > textSize.width)
					textSize = textSizeAux;
			}
			
			FontMetrics fm = c.getFontMetrics(c.getFont());
			textSize.height += (lines.length - 1) * fm.getHeight();
		}
		return textSize;
	}

	public static Dimension getTextSize(JComponent c, String text) {
		FontMetrics fm = c.getFontMetrics(c.getFont());
		int w = SwingUtilities2.stringWidth(c, fm, text);
		int h = fm.getHeight();
		return new Dimension(w, h);
	}

	public static String getTextAdjusted(String text, int size, Font font) {
		
		JLabel label = new JLabel(text);
		label.setSize(size-6, font.getSize() + 4);
		FontMetrics fontMetrics = label.getFontMetrics(font); 
		Icon icon = null;
		Rectangle viewR = label.getBounds();
		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		
		return SwingUtilities.layoutCompoundLabel(
				(JComponent) label,
				fontMetrics,
				text,
				icon,
				label.getVerticalAlignment(),
				label.getHorizontalAlignment(),
				label.getVerticalTextPosition(),
				label.getHorizontalTextPosition(),
				viewR,
				iconR,
				textR,
				label.getIconTextGap());
	}

}
