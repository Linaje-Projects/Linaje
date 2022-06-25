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

import java.util.List;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;

/**
 * Insert the type's description here.
 * Creation date: (28/02/2008 12:09:42)
 * @author: Pablo Linaje
 */

import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Lists;
import linaje.utils.Numbers;

public class ColumnDataCombo {
	
	private String headerName = null;
	private String dataFieldName = null;
	private int dataAlignment = SwingConstants.LEFT;
	private int width = 0;
	
	public ColumnDataCombo() {
		super();
	}
	public ColumnDataCombo(String headerName) {
		super();
		setHeaderName(headerName);
	}
	
	public int getDataAlignment() {
		return dataAlignment;
	}	
	public int getWidth() {
		return width;
	}
	public String getHeaderName() {
		if (headerName == null)
			headerName = Constants.VOID;
		return headerName;
	}
	public String getDataFieldName() {
		if (dataFieldName == null)
			dataFieldName = Constants.VOID;
		return dataFieldName;
	}
	
	public void setDataAlignment(int dataAlignment) {
		if (dataAlignment != SwingConstants.LEFT && dataAlignment != SwingConstants.RIGHT && dataAlignment != SwingConstants.CENTER)
			dataAlignment = SwingConstants.LEFT;
		this.dataAlignment = dataAlignment;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeaderName(String header) {
		this.headerName = header;
	}
	public void setDataFieldName(String dataFieldName) {
		this.dataFieldName = dataFieldName;
	}
	
	public static List<ColumnDataCombo> getColumns(String encodedColumns) {
		
		List<ColumnDataCombo> columns = Lists.newList();
		
		if (encodedColumns != null && !encodedColumns.trim().equals(Constants.VOID)) {
	
			try {
	
				StringTokenizer stItems = new StringTokenizer(encodedColumns, Constants.CEDILLA);
				String item, headerName, dataFieldName, dataAlignment;
				String widthColumn = Constants.VOID;
				StringTokenizer stItemElements;
	
				while (stItems.hasMoreTokens()) {
	
					item = stItems.nextToken();
					stItemElements = new StringTokenizer(item, Constants.ASTERISK);
	
					dataFieldName = stItemElements.nextToken();
					headerName = stItemElements.nextToken();
					dataAlignment = stItemElements.nextToken();
					if (stItemElements.hasMoreTokens())
						widthColumn = stItemElements.nextToken();
					else {
						widthColumn = "0";
					}
	
					ColumnDataCombo column = new ColumnDataCombo();
	
					column.setHeaderName(headerName);
					column.setDataFieldName(dataFieldName);
					column.setDataAlignment(Integer.parseInt(dataAlignment));
	
					int width = 0;
					if (Numbers.isIntegerNumber(widthColumn))
						width = Integer.parseInt(widthColumn);
						
					column.setWidth(width);
	
					columns.add(column);
				}
			}
			catch (Throwable ex) {
				Console.printException(ex);
				return Lists.newList();
			}
		}
	
		return columns;
	}
}
