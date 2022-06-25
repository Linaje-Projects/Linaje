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

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;

import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.statics.Constants;
import linaje.utils.LFont;
import linaje.utils.Strings;

/**
 * Añadidos respecto a una JLabel:
 * 	- Underlined
 *  - Margin independiente de Insets
 *  - GradientBackGround
 *  - getTextRect() y getTextRectTrim(), que nos indican las coordenadas exactas del texto
 *  - Métodos de acceso a las propiedades de la fuente del texto
 *  - Tooltip cuando el componente está recortado visualmente
 **/
public class LLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;
	
	private boolean underlined = false;
	private Insets margin = null;
	
	private Rectangle textRect = null;
	private boolean gradientBackground = false;
	
	public LLabel() {
		super();
		initialize();
	}
	public LLabel(String text) {
		super(text);
		initialize();
	}
	public LLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		initialize();
	}
	public LLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		initialize();
	}
	public LLabel(Icon image) {
		super(image);
		initialize();
	}
	public LLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		initialize();
	}
	
	private void initialize() {
		
		setTooltipEnabled(true);
	}
	
	/**
	 * If a border has been set on this component, returns the
	 * border's insets, else calls super.getInsets.
	 *
	 * @return the value of the insets property.
	 * @see #setBorder
	 */
	public Insets getInsets() {
		
		Insets insets = super.getInsets();
	
		insets.top = insets.top + getMargin().top;
		insets.bottom = insets.bottom + getMargin().bottom;
		insets.right = insets.right + getMargin().right;
		insets.left = insets.left + getMargin().left;
	
		String[] lineas = Strings.getLines(getText());
		if (lineas.length == 0 || (lineas.length == 1 && lineas[0].trim().equals(Constants.VOID))) {
			
			insets.top = insets.top - getMargin().top;
			insets.bottom = insets.bottom - getMargin().bottom;
		}
		
		return insets;
	}
	
	public Insets getMargin() {
		if (margin == null)
			margin = new Insets(0,0,0,0);
		return margin;
	}
	
	public Rectangle getTextRect() {
		if (textRect == null)
			textRect = new Rectangle(0,0,0,0);
		return textRect;
	}
	
	public Rectangle getTextRectTrim() {
	
		Rectangle textRect = (Rectangle) getTextRect().clone();
	
		String text = getText();
		String[] lines = Strings.getLines(text);
		
		if (lines.length == 1) {
	
			int numSpaces = 0;
			StringBuffer leftSpaces = new StringBuffer();
			while (text.length() > numSpaces && text.charAt(numSpaces) == ' ') {
				leftSpaces.append(Constants.SPACE);
				numSpaces++;
			}
	
			int widthLeftSpaces = getFontMetrics(getFont()).stringWidth(leftSpaces.toString());
	
			textRect.x = textRect.x + widthLeftSpaces;
			textRect.width = textRect.width - widthLeftSpaces;
		}
	
		return textRect;
	}
	
	@Override
	public void paint(Graphics g) {
		if (isOpaque() && isGradientBackground()) {
			GraphicsUtils.paintGradientBackground(g, this, getBackground());
			getUI().paint(g, this);
		}
		else {
			super.paint(g);
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
	
		super.paintComponent(g);
	}
	
	public String getFontName() {
		return getFont().getName();
	}
	public int getFontSize() {
		return getFont().getSize();
	}
	public int getFontStyle() {
		return getFont().getStyle();
	}
	public int getFontLayout() {
		return getFont() instanceof LFont ? ((LFont) getFont()).getLayoutMode() : LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
	}
	
	public void setFontName(String fontName) {
		UtilsGUI.setFontName(this, fontName);
	}
	public void setFontSize(int fontSize) {
		UtilsGUI.setFontSize(this, fontSize);
	}
	public void setFontStyle(int fontStyle) {
		UtilsGUI.setFontStyle(this, fontStyle);
	}
	public void setFontLayout(int fontLayout) {
		UtilsGUI.setFontLayout(this, fontLayout);
	}
	
	public void setMargin(Insets margin) {
		this.margin = margin;
	}
	public boolean isUnderlined() {
		return underlined;
	}
	public void setUnderlined(boolean underlined) {
		this.underlined = underlined;
	}
	public void setTextRect(java.awt.Rectangle textRect) {
		this.textRect = textRect;
	}
	
	public void setTooltipEnabled(boolean enabled) {
		if (enabled)
			ToolTip.getInstance().registerComponent(this);
		else
			ToolTip.getInstance().unRegisterComponent(this);
	}
	
	@Override
	public void setIcon(Icon icon) {
		super.setIcon(icon);
		if (icon != null && icon instanceof StateIcon)
			setDisabledIcon(((StateIcon) getIcon()).getDisabledIcon());
	}

	public boolean isGradientBackground() {
		return gradientBackground;
	}

	public void setGradientBackground(boolean gradientBackGround) {
		this.gradientBackground = gradientBackGround;
	}
}
