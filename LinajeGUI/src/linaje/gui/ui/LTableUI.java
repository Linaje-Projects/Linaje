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
package linaje.gui.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import javax.swing.table.TableCellRenderer;

import linaje.gui.table.LTable;
import linaje.gui.utils.ColorsGUI;
import linaje.logs.Console;

public class LTableUI extends BasicTableUI {
	
	private int firstRowVisible = 0;
	private int lastRowVisible = 0;
	
	public LTableUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent c) {
        return new LTableUI();
    }
	
	@Override
	protected void installDefaults() {
		super.installDefaults();
		
		LookAndFeel.installBorder(table, "Table.border");
		
		int rowHeight = table.getFontMetrics(table.getFont()).getHeight() + 2;
		LookAndFeel.installProperty(table, "rowHeight", rowHeight);
		Color background = table.getBackground();
		table.setGridColor(ColorsGUI.getGridColor(background));
		table.setShowHorizontalLines(false);
		table.setIntercellSpacing(new Dimension(1, 0));
	}
	private void updateVisibleRows() {
	
		final boolean paintOnlyVisibleRows = true;
	
		if (paintOnlyVisibleRows) {
			//Rectangle clipBounds = new Rectangle(g.getClipBounds());
			Rectangle clipBounds = new Rectangle(table.getVisibleRect());
			int tableWidth = table.getColumnModel().getTotalColumnWidth();
			clipBounds.width = Math.min(clipBounds.width, tableWidth);
			
			int firstIndex = table.rowAtPoint(new Point(0, clipBounds.y));
			int lastIndex = lastVisibleRow(clipBounds);
	
			setFirstRowVisible(firstIndex);
			setLastRowVisible(lastIndex);
		}
		else {
	
			setFirstRowVisible(0);
			setLastRowVisible(table.getRowCount() - 1);
		}
	}
	private int getFirstRowVisible() {
		return firstRowVisible;
	}
	private int getLastRowVisible() {
		return lastRowVisible;
	}
	private int lastVisibleRow(Rectangle clip) {
		int lastIndex = table.rowAtPoint(new Point(0, clip.y + clip.height - 1));
		// If the table does not have enough rows to fill the view we'll get -1.
		if (lastIndex == -1) {
			lastIndex = table.getRowCount() - 1;
		}
		return lastIndex;
	}
	public void paint(Graphics g, JComponent c) {
		
		super.paint(g, c);
		try {
	
			updateVisibleRows();
			if (thereIsSpan())
				paintSpan(g, c);
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void paintCellSpan(Graphics g, Rectangle cellRect, int row, int column) {
		// The cellRect is inset by half the intercellSpacing before painted
		int spacingHeight = table.getRowMargin();
		int spacingWidth = table.getColumnModel().getColumnMargin();
	
		// Round so that when the spacing is 1 the cell does not paint obscure lines.
		cellRect.setBounds(cellRect.x + spacingWidth/2, cellRect.y + spacingHeight/2,
						   cellRect.width - spacingWidth, cellRect.height - spacingHeight);
	
		if (table.isEditing() && table.getEditingRow()==row &&
								 table.getEditingColumn()==column) {
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		}
		else {
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = table.prepareRenderer(renderer, row, column);
	
			if (component.getParent() == null) {
				rendererPane.add(component);
			}
			rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y,
										cellRect.width, cellRect.height, true);
		}
		// Have to restore the cellRect back to it's orginial size
		cellRect.setBounds(cellRect.x - spacingWidth/2, cellRect.y - spacingHeight/2,
						   cellRect.width + spacingWidth, cellRect.height + spacingHeight);
	}
	
	private void paintSpan(Graphics g, JComponent c) {
	
		int columns = table.getColumnCount();
	
		int colSpan = 1;
		int rowSpan = 1;
		for (int column = 0; column < columns; column++) {
	
			for (int row = getFirstRowVisible(); row < getLastRowVisible() + 1; row = row + rowSpan) {
	
				colSpan = getColSpan(row, column);
				rowSpan = getRowSpan(row, column);
	
				if (colSpan > 1 || rowSpan > 1) {
	
					Rectangle cellRectSpan = table.getCellRect(row, column, true);
					for (int i = 1; i < colSpan; i++) {
	
						Rectangle cellRectSpanNext = table.getCellRect(row, column + i, true);
						cellRectSpan.width = cellRectSpan.width + cellRectSpanNext.width;
					}
					for (int i = 1; i < rowSpan; i++) {
	
						Rectangle cellRectSpanNext = table.getCellRect(row + i, column, true);
						cellRectSpan.height = cellRectSpan.height + cellRectSpanNext.height;
					}
					
					paintCellSpan(g, cellRectSpan, row, column);
				}
				if (rowSpan < 1)
					rowSpan = 1;
			}
		}
	}
	
	private void setFirstRowVisible(int firstRowVisible) {
		this.firstRowVisible = firstRowVisible;
	}
	private void setLastRowVisible(int lastRowVisible) {
		this.lastRowVisible = lastRowVisible;
	}
	
	private Insets getSpan(int row, int column) {
	
		if (table instanceof LTable) {
			LTable<?> lTable = (LTable<?>) table;
			for (int i = 0; i < lTable.getSpans().size(); i++) {
				
				Insets span = lTable.getSpans().get(i);
				if (span.top == row && span.left == column)
					return span;		
			}
		}
		return null;
	}
	protected int getColSpan(int row, int column) {
		
		int colSpan = 1;
		try {
			
			Insets span = getSpan(row, column);
			colSpan = span.right - span.left + 1;
		}
		catch (Exception e) {
		}
		return colSpan;
	}
	
	protected int getRowSpan(int row, int column) {
	
		int rowSpan = 1;
		try {
			
			Insets span = getSpan(row, column);
			rowSpan = span.bottom - span.top + 1;
		}
		catch (Exception e) {
		}
		return rowSpan;
	}
	
	protected boolean thereIsSpan() {
		
		if (table instanceof LTable)
			return !((LTable<?>) table).getSpans().isEmpty();
		else
			return false;
	}
}

