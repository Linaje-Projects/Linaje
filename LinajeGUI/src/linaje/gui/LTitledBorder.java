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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;

/**
 * Esta clase hace los mismo que TitledBorder
 * con la diferencia de que cuando su posicíon sea AVOVE_TOP o BELOW_BOTTOM
 * si el componente sobre el que se aplica el borde es opaco, el fondo del título
 * se pintará del color del primer padre opaco y no del color del componente
 * 
 * Además ignoraremos el margen interior y usaremos el propio de LTitledBorder
 */

@SuppressWarnings("serial")
public class LTitledBorder extends TitledBorder {

	private JLabel label = null;
	private Insets internalInsets = null;
	
	private static final int DEFAULT_INTERNAL_MARGIN = 4;
	
	public LTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor) {
		super(border, title, titleJustification, titlePosition, titleFont, titleColor);
	}
	public LTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont) {
		super(border, title, titleJustification, titlePosition, titleFont);
	}
	public LTitledBorder(Border border, String title, int titleJustification, int titlePosition) {
		super(border, title, titleJustification, titlePosition);
	}
	public LTitledBorder(Border border, String title) {
		super(border, title);
	}
	public LTitledBorder(Border border) {
		super(border);
	}
	public LTitledBorder(String title) {
		super(title);
	}

	public Insets getInternalInsets() {
		if (internalInsets == null)
			internalInsets = new Insets(DEFAULT_INTERNAL_MARGIN, DEFAULT_INTERNAL_MARGIN, DEFAULT_INTERNAL_MARGIN, DEFAULT_INTERNAL_MARGIN);
		return internalInsets;
	}
	public void setInternalInsets(Insets internalInsets) {
		this.internalInsets = internalInsets;
	}
	public void setInternalMargin(int margin) {
		setInternalInsets(new Insets(margin, margin, margin, margin));
	}
	
	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
       
		insets = super.getBorderInsets(c, insets);

        String title = getTitle();
        if (title != null && !title.isEmpty()) {
            
        	//Quitamos el margen interior del TitledBorder y respetaremos el del LTitledBorder
        	Border border = getBorder();
    		int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;
        	int titledMargin = edge + TEXT_SPACING;
            
            insets.top = insets.top - titledMargin + getInternalInsets().top;
            insets.left = insets.left - titledMargin + getInternalInsets().left;
            insets.right = insets.right - titledMargin + getInternalInsets().right;
            insets.bottom = insets.bottom - titledMargin + getInternalInsets().bottom;
        }
        return insets;
    }
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Border border = getBorder();
		String title = getTitle();
		if (title != null && !title.isEmpty()) {
			
			int edge = 0;//(border instanceof TitledBorder) ? 0 : EDGE_SPACING;
			JLabel label = getLabel(c);
			Dimension size = label.getPreferredSize();
			Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));

			int borderX = x + edge;
			int borderY = y + edge;
			int borderW = width - edge - edge;
			int borderH = height - edge - edge;

			int labelY = y;
			int labelH = size.height;
			int position = getPosition();
			switch (position) {
				case ABOVE_TOP:
					insets.left = 0;
					insets.right = 0;
					borderY += labelH - edge;
					borderH -= labelH - edge;
					break;
				case TOP:
					insets.top = edge + insets.top / 2 - labelH / 2;
					if (insets.top < edge) {
						borderY -= insets.top;
						borderH += insets.top;
					} else {
						labelY += insets.top;
					}
					break;
				case BELOW_TOP:
					labelY += insets.top + edge;
					break;
				case ABOVE_BOTTOM:
					labelY += height - labelH - insets.bottom - edge;
					break;
				case BOTTOM:
					labelY += height - labelH;
					insets.bottom = edge + (insets.bottom - labelH) / 2;
					if (insets.bottom < edge) {
						borderH += insets.bottom;
					} else {
						labelY -= insets.bottom;
					}
					break;
				case BELOW_BOTTOM:
					insets.left = 0;
					insets.right = 0;
					labelY += height - labelH;
					borderH -= labelH - edge;
					break;
				}
				insets.left += edge + TEXT_INSET_H;
				insets.right += edge + TEXT_INSET_H;
	
				int labelX = x;
				int labelW = width - insets.left - insets.right;
				if (labelW > size.width) {
					labelW = size.width;
				}
				switch (getJustification(c)) {
				case LEFT:
					labelX += insets.left;
					break;
				case RIGHT:
					labelX += width - insets.right - labelW;
					break;
				case CENTER:
					labelX += (width - labelW) / 2;
					break;
			}

			if (border != null) {
				if ((position != TOP) && (position != BOTTOM)) {
					border.paintBorder(c, g, borderX, borderY, borderW, borderH);
				}
				else {
					
					Graphics2D g2d = (Graphics2D) g.create();
					try {
						Path2D path = new Path2D.Float();
						path.append(new Rectangle(borderX, borderY, borderW,
								labelY - borderY), false);
						path.append(new Rectangle(borderX, labelY, labelX
								- borderX - TEXT_SPACING, labelH), false);
						path.append(new Rectangle(labelX + labelW
								+ TEXT_SPACING, labelY, borderX - labelX
								+ borderW - labelW - TEXT_SPACING, labelH),
								false);
						path.append(new Rectangle(borderX, labelY + labelH,
								borderW, borderY - labelY + borderH - labelH),
								false);
						g2d.clip(path);
					
						border.paintBorder(c, g2d, borderX, borderY, borderW, borderH);
					}
					finally {
						g2d.dispose();
					}
				}
			}
			/** ESTO ES LO QUE CAMBIA respecto al paintBorder() de TitledBorder que sobreescribimos**/
			if ((position != BELOW_TOP && position != ABOVE_BOTTOM) && c.isOpaque() && c.getParent() != null) {
				//Rellenamos la parte de texto que queda fuera del borde con el color del primer padre opaco
				Color parentBackground = ColorsGUI.getFirstOpaqueParentBackground(c.getParent());
				if (parentBackground != null) {
					g.setColor(parentBackground);
					
					//ABOVE_TOP || BELOW_BOTTOM
					int fillX = x;
					int fillY = labelY;
					int fillW = width;
					int fillH = labelH;
					
					//ATOP || BOTTOM
					if (position == TOP) {
						fillY = y;
						fillH = borderY - fillY;
					}
					else if (position == BOTTOM) {
						fillY = borderY + borderH;
						fillH = height - fillY;
					}
					
					g.fillRect(fillX, fillY, fillW, fillH);
				}
			}
			/************/
			g.translate(labelX, labelY);
			label.setSize(labelW, labelH);
			label.paint(g);
			g.translate(-labelX, -labelY);
		}
		else if (border != null) {
			border.paintBorder(c, g, x, y, width, height);
		}
	}

	private int getPosition() {
		int position = getTitlePosition();
		if (position != DEFAULT_POSITION) {
			return position;
		}
		Object value = UIManager.get("TitledBorder.position");
		if (value instanceof Integer) {
			int i = (Integer) value;
			if ((0 < i) && (i <= 6)) {
				return i;
			}
		} else if (value instanceof String) {
			String s = (String) value;
			if (s.equalsIgnoreCase("ABOVE_TOP")) {
				return ABOVE_TOP;
			}
			if (s.equalsIgnoreCase("TOP")) {
				return TOP;
			}
			if (s.equalsIgnoreCase("BELOW_TOP")) {
				return BELOW_TOP;
			}
			if (s.equalsIgnoreCase("ABOVE_BOTTOM")) {
				return ABOVE_BOTTOM;
			}
			if (s.equalsIgnoreCase("BOTTOM")) {
				return BOTTOM;
			}
			if (s.equalsIgnoreCase("BELOW_BOTTOM")) {
				return BELOW_BOTTOM;
			}
		}
		return TOP;
	}

	private int getJustification(Component c) {
		int justification = getTitleJustification();
		if (justification == LEADING || (justification == DEFAULT_JUSTIFICATION)) {
			return c.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT;
		}
		if (justification == TRAILING) {
			return c.getComponentOrientation().isLeftToRight() ? RIGHT : LEFT;
		}
		return justification;
	}

	protected Font getFont(Component c) {
		Font font = getTitleFont();
		if (font != null) {
			return font;
		}
		if (c != null) {
			font = c.getFont();
			if (font != null) {
				return font;
			}
		}
		return UtilsGUI.getFontWithStyle(GeneralUIProperties.getInstance().getFontApp(), Font.BOLD);
	}

	private Color getColor(Component c) {
		Color color = getTitleColor();
		if (color != null) {
			return color;
		}
		return (c != null) ? c.getForeground() : null;
	}

	private JLabel getLabel(Component c) {
		if (label == null)
			label = new JLabel();
		label.setText(getTitle());
		label.setFont(getFont(c));
		label.setForeground(getColor(c));
		label.setComponentOrientation(c.getComponentOrientation());
		label.setEnabled(c.isEnabled());
		return label;
	}

	private static Insets getBorderInsets(Border border, Component c,
			Insets insets) {
		if (border == null) {
			insets.set(0, 0, 0, 0);
		} else if (border instanceof AbstractBorder) {
			AbstractBorder ab = (AbstractBorder) border;
			insets = ab.getBorderInsets(c, insets);
		} else {
			Insets i = border.getBorderInsets(c);
			insets.set(i.top, i.left, i.bottom, i.right);
		}
		return insets;
	}
}
