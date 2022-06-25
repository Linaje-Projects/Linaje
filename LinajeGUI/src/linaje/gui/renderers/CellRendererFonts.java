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
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import linaje.gui.LLabel;
import linaje.gui.components.FontChooser;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class CellRendererFonts<E> extends LLabel implements ListCellRenderer<E>, TableCellRenderer {

	private boolean isFont = false;
	private Font defaultFont = null;
	private Font tipFont = null;
	
	public CellRendererFonts() {
		super();
		defaultFont = GeneralUIProperties.getInstance().getFontApp();
		tipFont = UtilsGUI.getFontWithSizeFactor(defaultFont,0.6f);
	}
		
	private void formatCell(Object value, boolean isSelected, Color background, Color foreground) {
	
		isFont = false;
			
		int style = Font.PLAIN;
		int size = defaultFont.getSize();
		String fontName = defaultFont.getName();
			
		String text;
		if (value == null) {
			text = Constants.VOID;
		}
		else if (value instanceof Font) {
	
			isFont = true;
			Font font = (Font) value;
			fontName = font.getName();
			text = fontName;
		}
		else {
			
			text = value.toString();
			
			if (text.equalsIgnoreCase(FontChooser.TEXTS.plain))
				style = Font.PLAIN;
			else if (text.equalsIgnoreCase(FontChooser.TEXTS.italic))
				style = Font.ITALIC;
			else if (text.equalsIgnoreCase(FontChooser.TEXTS.bold))
				style = Font.BOLD;
			else if (text.equalsIgnoreCase(FontChooser.TEXTS.boldItalic))
				style = Font.ITALIC+Font.BOLD;
			else if (Strings.isIntegerNumber(text.trim()))
				size = Integer.parseInt(text.trim());
		}
	
		setOpaque(isSelected);
		setBackground(background);
		setForeground(foreground);
		
		setFont(new Font(fontName, style, size));
		setText(text);
	}
	
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
	
		formatCell(value, isSelected, list.getBackground(), list.getForeground());
	
		if (isSelected && list.getSelectionForeground().getRGB() != getForeground().getRGB())
			list.setSelectionForeground(getForeground());
			
		return this;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
		formatCell(value, isSelected, table.getBackground(), table.getForeground());
		return this;
	}
	
	/*@Override
	public Dimension getPreferredSize() {
		
		Dimension prefSize = super.getPreferredSize();
		
		if (isFont) {
			int separation = 10;
			prefSize.width += Strings.getStringWidth(getText(), getFontMetrics(tipFont)) + separation;
		}
		
		return prefSize;
	}*/
	@Override
	protected void paintComponent(Graphics g) {
	
		if (isOpaque()) {
			UtilsRenderer.paintBackgroundSelected(g, this);
		}
		getUI().paint(g, this);
		
		//Pintamos el nombre de la fuente en pequeño, por si la fuente no es legible
		if (isFont) {
	
			Point location = UISupportUtils.getLocation(this, getText(), getFontMetrics(tipFont), SwingConstants.EAST, null);
			
			g.setFont(tipFont);
			g.setColor(Colors.optimizeColor(ColorsGUI.getColorInfo(), getBackground()));
			GraphicsUtils.drawString(g, getText(), location.x, location.y);
		}
	}
}
