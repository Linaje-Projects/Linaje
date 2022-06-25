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
package linaje.gui.cells;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;

import linaje.statics.Constants;
import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;
import linaje.utils.Strings;

/**
 * Objeto común básico a partir del cual renderizar elementos de combos, listas, arboles, etc
 * Nos permite mostrar código del elemento, icono, nivel de desglose/indentación, así como deshabilitar elementos de combos, listas, arboles...
 **/
public class DataCell {

	private Object code = null;
	private Object value = null;
	private int level = 1;
	private Icon icon = null;
	private boolean enabled = true;
	private boolean selectedDefault = false;
	
	public DataCell() {
		this(Constants.VOID, Constants.VOID, 1, null);
	}
	public DataCell(Object code, Object  value) {
		this(code, value, 1, null);
	}
	public DataCell(Object code, Object value, int level) {
		this(code, value, level, null);
	}
	public DataCell(Object code, Object value, int level, Icon icon) {
		super();
		if (code == null)
			code = Constants.VOID;
		if (value == null)
			value = Constants.VOID;
		this.code = code;
		this.value = value;
		this.level = level;
		this.icon = icon;
	}
	
	public static DataCell getDataCellMultiple(Object[] selectedObjects) {
		
		//Creamos un único DataCell a partir de varios elementos que pueden ser o no DataCells
		DataCell selectedItem = null;
		if (selectedObjects.length > 0) {
			
			if (selectedObjects.length == 1) {
				
				Object item = selectedObjects[0];
				selectedItem = item instanceof DataCell ? (DataCell) item : new DataCell(item, item);
			}
			else {
				
				List<Object> codes = Lists.newList();
				List<Object> values = Lists.newList();
				for (int i = 0; i < selectedObjects.length; i++) {
					
					Object item = selectedObjects[i];
					DataCell itemDataCell = item instanceof DataCell ? (DataCell) item : null;
					Object code = itemDataCell != null ? itemDataCell.getCode() : item;
					Object value = itemDataCell != null ? itemDataCell.getValue() : item;
					
					codes.add(code);
					values.add(value);
				}
				
				selectedItem = new DataCell(codes, values);
				selectedItem.setEnabled(true);
			}
		}
		else {		
			selectedItem = new DataCell(Constants.NULL, Constants.VOID);
		}
		
		return selectedItem;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Vector<TreeNodeVector<T>> dataCellsToNodeVectors(List<DataCell> dataCellList) {
		
		HashMap<Integer, TreeNodeVector<T>> mapLastRowLevel = new HashMap<>();
		Vector<TreeNodeVector<T>> rows = Lists.newVector(dataCellList.size());
		for (int i = 0; i < dataCellList.size(); i++) {
			DataCell dataCell = dataCellList.get(i);
			TreeNodeVector<T> row = new TreeNodeVector<T>((T) dataCell);
			int level = dataCell.getLevel();
			TreeNodeVector<T> lasRowPreviousLevel = mapLastRowLevel.get(level-1);
			if (lasRowPreviousLevel != null)
				lasRowPreviousLevel.addChild(row);
			else
				rows.add(row);
			
			mapLastRowLevel.put(level, row);
		}
		
		return rows;
	}
	
	public boolean equals(DataCell dataCell) {
		
		boolean equals = false;
		
		if (dataCell != null) {
	
			equals = getLevel() == dataCell.getLevel();
			if (dataCell.getCode() != null && getCode() != null)
				equals = equals && getCode().equals(dataCell.getCode());
			
			if (dataCell.getValue() != null && getValue() != null)
				equals = equals && getValue().equals(dataCell.getValue());
		}
		return equals;
	}
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(Strings.getIndent(getLevel()));
		sb.append(getValue());
		sb.append(Constants.HASH);
		sb.append(getCode());
		
		return sb.toString();
	}
	
	public Object getCode() {
		return code;
	}
	public Object getValue() {
		return value;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public int getLevel() {
		return level;
	}
	public Icon getIcon() {
		return icon;
	}
	public boolean isSelectedDefault() {
		return selectedDefault;
	}
	
	public void setCode(Object code) {
		this.code = code;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	public void setSelectedDefault(boolean seleccionadoDefecto) {
		this.selectedDefault = seleccionadoDefecto;
	}
}
