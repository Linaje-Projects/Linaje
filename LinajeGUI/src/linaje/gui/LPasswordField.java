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
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.text.Document;

import linaje.gui.ui.UISupport;
import linaje.statics.Constants;
import linaje.utils.Colors;

@SuppressWarnings("serial")
public class LPasswordField extends JPasswordField {

	private String textBackgroundVoid = null;
	
	public LPasswordField() {
		super();
	}
	public LPasswordField(String text) {
		super(text);
	}
	public LPasswordField(int columns) {
		super(columns);
	}
	public LPasswordField(String text, int columns) {
		super(text, columns);
	}
	public LPasswordField(Document doc, String txt, int columns) {
		super(doc, txt, columns);
	}

	public void setTextBackgroundVoid(String textBackgroundVoid) {
		this.textBackgroundVoid = textBackgroundVoid;
	}
	public String getTextBackgroundVoid() {
		if (textBackgroundVoid == null)
			textBackgroundVoid = Constants.VOID;
		return textBackgroundVoid;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		pintarTextBackgroundVoid(g);
	}
	
	private void pintarTextBackgroundVoid(Graphics g) {
		
		if (!getTextBackgroundVoid().trim().equals(Constants.VOID) && getPassword().length == 0) {
	
			final String text = getTextBackgroundVoid();
			final Color foreground = Colors.isColorDark(getForeground()) ? Colors.brighter(getForeground(), 0.3) : Colors.darker(getForeground(), 0.3);
			paintBGText(g, text, getFont(), foreground);
		}
	}
	
	private void paintBGText(Graphics g, String text, Font font, Color foreground) {
	
		g.setFont(font);
		g.setColor(foreground);
		
		UISupport.paintText(g, text, this, SwingConstants.CENTER, SwingConstants.CENTER);
	}
}
