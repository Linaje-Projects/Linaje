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

import java.awt.Color;
import java.util.List;

import javax.swing.table.*;

import linaje.gui.renderers.LHeaderRenderer;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class LColumn extends TableColumn {
	
	public static final int ORDER_ORIGINAL = 0;
	public static final int ORDER_ASCENT = 1;
	public static final int ORDER_DESCENT = -1;
	
	//Nos indica cuando combinaremos las cabeceras, se define una vez
	public static final int COMBINE_HEADERS_NEVER = 0;
	public static final int COMBINE_HEADERS_FIRST_LINE_MATCH = 1;
	public static final int COMBINE_HEADERS_ANY_LINE_MATCH = 2;
	public static final int COMBINE_HEADERS_FIRST_LINE_ALWAYS = 3;
	public static final int COMBINE_HEADERS_ANY_LINE_ALWAYS = 4;
	
	//El tipo de span se calculará dinámicamente según el texto de las cabeceras
	//y la configuración de combinar que le hayamos dado a la columna
	public static final int TYPE_SPAN_NONE = 0;
	public static final int TYPE_SPAN_OPEN_RIGHT = 1;
	public static final int TYPE_SPAN_OPEN_LEFT = 2;
	public static final int TYPE_SPAN_OPEN_BOTH = 3;
	public static final int TYPE_SPAN_CLOSE_BOTH = 4; //Este caso es una columna con doble cabecera que queremos separar una linea de otra
	
	private boolean editable = false; 
	private boolean ordenable = true; 
	private int typeCombineHeaders = COMBINE_HEADERS_ANY_LINE_MATCH;
	private int[] typesSpan = {TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE};

	private Color headerBackground = null;
	private Color headerForeground = null;
	
	public LColumn() {
		this(0);
	}
	public LColumn(int modelIndex) {
		this(modelIndex, 20);
	}
	public LColumn(int modelIndex, int width) {
		this(modelIndex, width, null, null);
	}
	public LColumn(int modelIndex, int width, TableCellRenderer cellRenderer, TableCellEditor cellEditor) {
		super(modelIndex, width, cellRenderer, cellEditor);
		setMinWidth(20);
		setHeaderRenderer(new LHeaderRenderer());
	}
	
	public boolean isEditable() {
		return editable;
	}
	public boolean isOrdenable() {
		return ordenable;
	}
	public int getTypeCombineHeaders() {
		return typeCombineHeaders;
	}
	public int[] getTypesSpan() {
		return typesSpan;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public void setOrdenable(boolean ordenable) {
		this.ordenable = ordenable;
	}
	public void setTypeCombineHeaders(int typeCombineHeaders) {
		this.typeCombineHeaders = typeCombineHeaders;
	}
	public void setTypesSpan(int[] typesSpan) {
		this.typesSpan = typesSpan;
	}
	
	public Color getHeaderBackground() {
		return headerBackground;
	}
	public void setHeaderBackground(Color headerBackground) {
		this.headerBackground = headerBackground;
	}
	public Color getHeaderForeground() {
		return headerForeground;
	}
	public void setHeaderForeground(Color headerForeground) {
		this.headerForeground = headerForeground;
	}
	
	public static List<LColumn> getColumns(List<LColumn> columns, int[] modelIndexs) {
		List<Integer> listModelIndexs = Lists.arrayToList(modelIndexs);
		return getColumns(columns, listModelIndexs);
	}
	public static List<LColumn> getColumns(List<LColumn> columns, List<Integer> modelIndexs) {
		
		List<LColumn> columnsFinded = Lists.newList();
		for (int i = 0; i < columns.size(); i++) {
			LColumn column = columns.get(i);
			boolean columnFinded = false;
			for (int j = 0; !columnFinded && j < modelIndexs.size(); j++) {
				int modelIndex = modelIndexs.get(j);
				if (column.getModelIndex() == modelIndex) {
					columnsFinded.add(column);
					columnFinded = true;
				}
			}
		}
		return columnsFinded;
	}
}
