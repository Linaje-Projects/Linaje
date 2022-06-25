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


import javax.swing.Icon;
import javax.swing.JToggleButton;

import linaje.gui.utils.UtilsGUI;
import linaje.utils.LFont;

/**
 * Añadidos respecto a un JToggleButton:
 * 	- LButtonPropertable, por lo que podemos cambiar sus propiedades de LButtonProperties individualmente
 *  - Métodos de acceso a las propiedades de la fuente del texto
 **/
@SuppressWarnings("serial")
public class LToggleButton extends JToggleButton implements LButtonPropertable {

	private LButtonProperties buttonProperties = null;
	
	public LToggleButton() {
		super();
		initialize();
	}
	public LToggleButton(String text) {
		super(text);
		initialize();
	}
	public LToggleButton(String text, Icon icon) {
		super(text, icon);
		initialize();
	}
	public LToggleButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		initialize();
	}
	public LToggleButton(String text, boolean selected) {
		super(text, selected);
		initialize();
	}
	public LToggleButton(Icon icon) {
		super(icon);
		initialize();
	}
	public LToggleButton(Icon icon, boolean selected) {
		super(icon, selected);
		initialize();
	}
	
	private void initialize() {
	}
	
	public LButtonProperties getButtonProperties() {
		if (buttonProperties == null)
			buttonProperties = LButtonProperties.createButtonPropertiesUIBased(this);
		return buttonProperties;
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
	
}
