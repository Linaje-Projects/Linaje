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

import java.awt.event.MouseEvent;
import java.util.EventObject;

@SuppressWarnings("serial")
public class LTableEvent<E> extends EventObject {

	private int columnIndex = -1;
	private int rowIndex = -1;
	private MouseEvent mouseEvent = null;
	
	public LTableEvent(Object source, int columnIndex, int rowIndex, MouseEvent mouseEvent) {
		super(source);
		setColumnIndex(columnIndex);
		setRowIndex(rowIndex);
		setMouseEvent(mouseEvent);
	}

	public int getColumnIndex() {
		return columnIndex;
	}
	public int getRowIndex() {
		return rowIndex;
	}
	public MouseEvent getMouseEvent() {
		return mouseEvent;
	}

	private void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	private void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	private void setMouseEvent(MouseEvent mouseEvent) {
		this.mouseEvent = mouseEvent;
	}
}
