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

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import linaje.gui.cells.DataCell;
import linaje.gui.ui.LComboUI;
import linaje.gui.utils.UtilsGUI;
import linaje.utils.LFont;

/**
 * Añadidos respecto a un JComboBox:
 * 	- Método getItems()
 *  - isFocusable será true cuando el combo sea editable independientemente del valor de su propiedad
 *  - getSelectedItem() devuelve tipo del combo en lugar de Object
 *  - setSelectedItem no funcionará si el elemento a seleccionar está deshabilitado
 *  - Método makeButtonTransparent() para hacer el botón de despliegue transparente
 *  - getArrowButton() nos dará acceso al botón de despliegue del combo
 *  - Métodos de acceso a las propiedades de la fuente del texto
 **/
@SuppressWarnings("serial")
public class LCombo<E> extends JComboBox<E> {
		
	public LCombo() {
		super();
	}
	public LCombo(E[] items) {
		super(items);
	}
	public LCombo(Vector<E> items) {
		super(items);
	}
	public LCombo(ComboBoxModel<E> model) {
		super(model);
	}
	
	public void addItems(List<E> items) {
		if (items != null && !items.isEmpty()) {
			for (int i = 0; i < items.size(); i++) {
				addItem(items.get(i));
			}
		}
	}
	
	public Vector<E> getItems() {
		Vector<E> items = new Vector<E>();
		for (int i = 0; i < getItemCount(); i++)
			items.addElement(getItemAt(i));
		
		return items;
	}
		
	@Override
	public boolean isFocusable() {
	    return isEditable() || super.isFocusable();
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
	
	@SuppressWarnings("unchecked")
	@Override
	public E getSelectedItem() {
		return (E) super.getSelectedItem();
	}
	
	@Override
	public void setSelectedItem(Object item) {
		
		boolean canSelectItem = true;
		if (item != null) {
			if (item instanceof Component)
				canSelectItem = ((Component) item).isEnabled();
			else if (item instanceof DataCell)
				canSelectItem = ((DataCell) item).isEnabled();
		}
		
		if (canSelectItem)
			super.setSelectedItem(item);
	}
	
	public void makeButtonTransparent() {
		LArrowButton arrowButton = getArrowButton();
		if (arrowButton != null) {
			arrowButton.setBorder(null);
			arrowButton.setOpaque(false);
			arrowButton.getButtonProperties().setGradientBackgroundEnabled(false);
		}
	}
	
	public LArrowButton getArrowButton() {
		return getUI() instanceof LComboUI ? ((LComboUI) getUI()).getArrowButton() : null;
	}
}
