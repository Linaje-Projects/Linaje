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

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import linaje.gui.utils.UtilsGUI;
import linaje.utils.LFont;

/**
 * Añadidos respecto a un JMenu:
 * 	- LButtonPropertable, por lo que podemos cambiar sus propiedades individualmente
 *  - Se pinta sobre componentes pesados
 *  - Métodos de acceso a las propiedades de la fuente del texto
 **/
@SuppressWarnings("serial")
public class LMenu extends JMenu implements LButtonPropertable {

	private LButtonProperties buttonProperties = null;
	
	public LMenu() {
		super();
	}
	public LMenu(String s) {
		super(s);
	}
	public LMenu(String s, boolean b) {
		super(s, b);
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
	
	public void setPopupMenuVisible(boolean b) {
		//Forzamos que el popup, que es ligero, se pinte sobre componentes pesados AWT haciendo un revalidate
		revalidate();
		super.setPopupMenuVisible(b);
	}
	public JPopupMenu getPopupMenu() {
		JPopupMenu popupMenu = super.getPopupMenu();
		popupMenu.setLightWeightPopupEnabled(true);
		return popupMenu;
	}
	public boolean isLightWeightPopupEnabled() {
		//Lo forzamos porque si cambia no se pintan las transparencias
	    return true;
	}
}
