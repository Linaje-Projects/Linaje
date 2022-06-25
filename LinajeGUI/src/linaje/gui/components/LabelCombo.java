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
package linaje.gui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import linaje.gui.LCombo;

@SuppressWarnings("serial")
public class LabelCombo<E> extends LabelComponent {
	
	private LCombo<E> combo = null;

	public LabelCombo() {
		this(null);
	}
	public LabelCombo(String text) {
		this(text, HORIZONTAL);
	}
	public LabelCombo(String text, int orientation) {
		this(text, orientation, 100);
	}
	public LabelCombo(String text, int orientation, int widthLabel) {
		this(text, orientation, 100, GridBagConstraints.HORIZONTAL);
	}
	public LabelCombo(String text, int orientation, int widthLabel, int fillComponent) {
		this(text, orientation, 100, GridBagConstraints.HORIZONTAL, null);
	}
	public LabelCombo(String text, int orientation, int widthLabel, int fillComponent, Color lineColor) {
		super(text, null, orientation, 100, fillComponent, lineColor);
		setComponent(getCombo());
	}
	
	public LCombo<E> getCombo() {
		if (combo == null) {
			combo = new LCombo<E>();
		}
		return combo;
	}
	
	public void addItems(List<E> items) {
		getCombo().addItems(items);
	}
	
	public Vector<E> getItems() {
		return getCombo().getItems();
	}
	
	public void addItemListener(ItemListener listener) {
		getCombo().addItemListener(listener);
	}
	public void removeItemListener(ItemListener listener) {
		getCombo().removeItemListener(listener);
	}
	
	public Object getItemAt(int index) {
		return getCombo().getItemAt(index);
	}
	public int getItemCount() {
		return getCombo().getItemCount();
	}
	public void addItem(E item) {
		getCombo().addItem(item);
	}
	
	public void removeAllItems() {
		getCombo().removeAllItems();
	}
	public void removeItem(Object item) {
		getCombo().removeItem(item);
	}
	public void removeItemAt(int index) {
		getCombo().removeItemAt(index);
	}
}
