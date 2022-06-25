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
package linaje.gui.table;

import linaje.gui.LLabel;
import linaje.tree.TreeNodeVector;

public class LTableObject<E> extends LLabel {

	private static final long serialVersionUID = 6184393397668131624L;
	
	private TreeNodeVector<LTableObject<E>> rowParent = null;
	private E value = null;
	private char nature = 0;
	
	private int rowSpan = 1;
	private int colSpan = 1;
	private CellSpan combineCells = null;
	private CellSpan combineRows = null;
	
	private String text = null;
	
	public LTableObject(TreeNodeVector<LTableObject<E>> rowParent) {
		this(rowParent, null);
	}
	public LTableObject(TreeNodeVector<LTableObject<E>> rowParent, E valor) {
		this(rowParent, valor, '0');
	}
	public LTableObject(TreeNodeVector<LTableObject<E>> rowParent, E valor, char naturaleza) {
		super();
		setRowParent(rowParent);
		setValue(valor);
		setNature(naturaleza);
	}
		
	public int getColSpan() {
		return colSpan;
	}
	public CellSpan getCombineCells() {
		return combineCells;
	}
	public CellSpan getCombineRows() {
		return combineRows;
	}
	public char getNature() {
		return nature;
	}
	public int getRowSpan() {
		return rowSpan;
	}
	public E getValue() {
		return value;
	}
	
	public TreeNodeVector<LTableObject<E>> getRowParent() {
		return rowParent;
	}
	
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}
	public void setCombineCells(CellSpan combineCells) {
		this.combineCells = combineCells;
	}
	public void setCombineRows(CellSpan combineRows) {
		this.combineRows = combineRows;
	}
	public void setNature(char nature) {
		this.nature = nature;
	}
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}
	public void setValue(E value) {
		this.value = value;
	}
	public void setRowParent(TreeNodeVector<LTableObject<E>> rowParent) {
		this.rowParent = rowParent;
	}
	
	public boolean isExpanded() {
		boolean isExpanded = false;
		if (getRowParent() != null) {
			isExpanded = getRowParent().getChildCount() > 0 && getRowParent().isExpanded();
		}
		return isExpanded;
	}
	
	public boolean hasChildren() {
		boolean hasChildren = false;
		if (getRowParent() != null) {
			hasChildren = getRowParent().getChildCount() > 0;
		}
		return hasChildren;
	}
	
	@Override
	public String getText() {
		if (text == null && getValue() != null) {
			//No se ha iniciado el texto formateado por lo que devolvemos el value
			return getValue().toString();
		}
		else {
			return super.getText();
		}
	}
	
	public int getNivel() {
		if (getRowParent() == null)
			return 1;
		else
			return getRowParent().getLevel();
	}
	@Override
	public void setText(String text) {
		this.text = text;
		super.setText(text);
	}
	
	public String toString() {
		return getText();
	}	
}
