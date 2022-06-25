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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import linaje.gui.LPanel;
import linaje.gui.cells.DataCell;
import linaje.gui.table.LColumn;
import linaje.gui.table.LTable;
import linaje.gui.table.LTableModel;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;

/**
 * Esta tabla se usa en ComboMultiAspect cuando utilizamos los aspectos ASPECT_BUTTON_TABLE y ASPECT_TABLE
 * Nos permite mostrar de forma óptima los elementos de un combo cuando el número de elementos es muy alto
 * y además nos permite indentar los elementos y plegarlos y desplegarlos
 **/
@SuppressWarnings("serial")
public class TableItemsCombo<E> extends LPanel {
	
	private LPanel panelAuxTable = null;
	private LTable<E> table = null;
	private LColumn colElements = null;
	private JScrollPane scrollPane = null;
	
	private Vector<E> items = null;
	private List<Component> selectableComponents = null;
	private List<Integer> columnWidths = null;
	private boolean validColumnWidths = false;
	
	private List<ColumnDataCombo> columnsDataCombo = null;
		
	public TableItemsCombo() {
		super();
		initialize();
	}
		
	private LColumn getColElements() {
		if (colElements == null) {
			colElements = new LColumn();
			colElements.setHeaderValue("Elements");
			colElements.setModelIndex(0);
		}
		return colElements;
	}
	
	private LPanel getPanelAuxTable() {
		if (panelAuxTable == null) {
			panelAuxTable = new LPanel(new BorderLayout());
			panelAuxTable.setOpaque(false);
			panelAuxTable.add(getTable(), BorderLayout.NORTH);
		}
		return panelAuxTable;
	}
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setOpaque(false);
			scrollPane.setViewportView(getPanelAuxTable());
		}
		return scrollPane;
	}
	
	public LTable<E> getTable() {
		if (table == null) {
			table = new LTable<E>();
			table.setAutoCreateColumnsFromModel(false);
			table.addColumn(getColElements());
			table.addComponentListener(new ComponentListener() {
				public void componentResized(ComponentEvent e) {
					try {
						
						//Cambia el tamaño de la table de datos principal
						if (e.getSource() == getTable()) {
							//Redimensionamos las columnas de datos para el nuevo tamaño de la table
							resizeColumns();
							validate();
							repaint();
						}
					} catch (Throwable ex) {
						Console.printException(ex);
					}
				}
				public void componentHidden(ComponentEvent e) {}
				public void componentMoved(ComponentEvent e) {}
				public void componentShown(ComponentEvent e) {}
			});
		}
		return table;
	}
	
	public List<Component> getSelectableComponents() {
		
		if (selectableComponents == null) {
			
			selectableComponents = new Vector<Component>();
			selectableComponents.add(getTable());
			selectableComponents.add(getScrollPane());
			//selectableComponents.addElement(getColElements());
		}
		return selectableComponents;
	}
	
	public Vector<E> getItems() {
		if (items == null)
			items = new Vector<E>();
		return items;
	}
	
	public LTableModel<E> getModel() {
		return getTable().getModel();
	}
	
	public boolean isValidColumnWidths() {
		return validColumnWidths;
	}
	public List<Integer> getColumnWidths() {
		return columnWidths;
	}
	
	public void setValidColumnWidths(boolean validColumnWidths) {
		this.validColumnWidths = validColumnWidths;
	}
	public void setColumnWidths(List<Integer> columnWidths) {
		this.columnWidths = columnWidths;
	}
	
	private void initialize() {
		
		setOpaque(false);
		setLayout(new BorderLayout());
		setSize(250, 150);
		add(getScrollPane(), BorderLayout.CENTER);

		getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTable().sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
		getTable().clearSelection();
	
		getScrollPane().setBorder(BorderFactory.createEmptyBorder());
		getScrollPane().getViewport().setOpaque(false);
	}
	
	public void iniciarTabla(Vector<E> itemsCombo, boolean showHeaders, String headerName) {
		
		if (headerName == null)
			headerName = Constants.VOID;
	
		ColumnDataCombo columnDC = new ColumnDataCombo();
		columnDC.setHeaderName(headerName);
	
		Vector<ColumnDataCombo> columnsDC = new Vector<ColumnDataCombo>();
		columnsDC.addElement(columnDC);
		
		initTable(itemsCombo, showHeaders, columnsDC, true);
	}
	
	@SuppressWarnings("unchecked")
	public void initTable(Vector<E> itemsCombo, boolean showHeaders, List<ColumnDataCombo> columnsDC, boolean selectableRows) {
	
		setColumnsDataCombo(columnsDC);
		
		if (showHeaders) {
			getScrollPane().setViewportView(getTable());
		}
		else {
			getPanelAuxTable().add(getTable(), BorderLayout.CENTER);
			getScrollPane().setViewportView(getPanelAuxTable());
		}
	
		//Eliminamos todas las columnas
		getTable().removeAllColumns();
		
		if (selectableRows == false) {
			getTable().setRowSelectionAllowed(selectableRows);
			getTable().setCellSelectionEnabled(false);
			getTable().setColumnSelectionAllowed(false);
		}
		
		getTable().addColumn(getColElements());
	
		List<Integer> widthColumns = Lists.newList();
		for (int i = 0; i < columnsDC.size(); i++) {
	
			ColumnDataCombo columnDC = columnsDC.get(i);
			String cabecera = columnDC.getHeaderName();
			int width = columnDC.getWidth();
	
			widthColumns.add(width);
			
			LColumn column;
			if (i == 0) {
				column = getColElements();
			}
			else {
				column = new LColumn();
	
				column.setMinWidth(20);
				column.setMaxWidth(1280);
				column.setModelIndex(i + 3);
				
				getTable().addColumn(column);
			}
			
			column.setHeaderValue(cabecera);
		}
		
		setColumnWidths(widthColumns);
		resizeColumns();
	
		if (itemsCombo != null)
			items = (Vector<E>) itemsCombo.clone();
	
		prepareDataTable();
		
		try {
			getModel().expandRows(1);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}

	private void prepareDataTable() {
		
		List<LColumn> columns = getTable().getColumns();
		Vector<String> columnIdentifiers = Lists.newVector(columns.size());
		for (int i = 0; i < columns.size(); i++) {
			columnIdentifiers.add(columns.get(i).getHeaderValue().toString());
		}
		
		Vector<TreeNodeVector<E>> rows;
		if (getItems().isEmpty() || !(getItems().elementAt(0) instanceof DataCell))
			rows = TreeNodeVector.getNodesOneElement(getItems());
		else
			rows = DataCell.dataCellsToNodeVectors((Vector<DataCell>) getItems());
		
		getModel().setRows(rows, columnIdentifiers);
	}
	
	public void setSelectedItem(E selectedCode) {
		
		try {
	
			if (selectedCode != null) {
				
				getTable().clearSelection();
	
				TreeNodeVector<E> selectedRow = getModel().findRowSimilar(selectedCode);
				if (selectedRow != null) {
					getModel().expandParents(selectedRow);
					int row = getModel().getRows().indexOf(selectedRow);
					if (row != -1)
						getTable().positionInCell(row, 0);
				}
			}
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
	
	private void resizeColumns() {
	
		try {
	
			List<Integer> columnWidths = obtainColumnWidths(getColumnWidths());
			if (isValidColumnWidths()) {
				for (int i = 0; i < columnWidths.size(); i++) {
	
					TableColumn column = getTable().getColumnModel().getColumn(i);
					column.setWidth(Integer.parseInt(columnWidths.get(i).toString()));
					column.setPreferredWidth(Integer.parseInt(columnWidths.get(i).toString()));
				}
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public List<Integer> obtainColumnWidths(List<Integer> initialColumnWidths) {
		
		List<Integer> columnWidths = Lists.newList();
		List<Integer> nullColumns = Lists.newList();
		List<Integer> finalColumnWidths = Lists.newList();
		
		int sum = 0;
		if (initialColumnWidths != null) {
			
			for (int i = 0; i < initialColumnWidths.size(); i++) {
				sum = sum + Integer.parseInt(initialColumnWidths.get(i).toString());
			}
			if (sum > 0 && sum <= 100) {
				for (int i = 0; i < initialColumnWidths.size(); i++) {
					int columnWidth = 0;
					int initialColumnWidth = Integer.parseInt(initialColumnWidths.get(i).toString());
					columnWidth = ((this.getWidth() * initialColumnWidth) / 100);
					columnWidths.add(columnWidth);
					if (columnWidth == 0)
						nullColumns.add(i);
				}
				setValidColumnWidths(true);
				int sumVoids = 0;
				for (int i = 0; i < columnWidths.size(); i++) {
					sumVoids = sumVoids + columnWidths.get(i).intValue();
				}
				if (nullColumns.isEmpty()) {
					int extraSpace = (this.getWidth() - sumVoids) / columnWidths.size();
					for (int i = 0; i < columnWidths.size(); i++) {
						int finalWidth = extraSpace + columnWidths.get(i).intValue();
						finalColumnWidths.add(finalWidth);
					}
				}
				else {
					int blankSpace = (this.getWidth() - sumVoids) / nullColumns.size();
					for (int i = 0; i < columnWidths.size(); i++) {
						if (columnWidths.get(i) == 0) {
							finalColumnWidths.add(blankSpace);
						}
						else {
							finalColumnWidths.add(columnWidths.get(i));
						}
					}
				}
			}
			else {
				finalColumnWidths = null;
				setValidColumnWidths(false);
			}
		}
		
		return finalColumnWidths;
	}
	
	public void collapseRows() {
		getModel().collapseRows();
	}
	public void expandRows() {
		getModel().expandRows();
		if (getTable().getSelectedRow() != -1)
			positionInRow(getTable().getSelectedRow());
	}
	public void modifyExpandLevel(int expandLevel) {
		collapseRows();
		getModel().expandRows(expandLevel + 1);
		if (getTable().getSelectedRow() != -1)
			positionInRow(getTable().getSelectedRow());
	}
	
	private void positionInRow(int row) {
		getTable().positionInCell(row, 0);
	}
	
	public List<ColumnDataCombo> getColumnsDataCombo() {
		return columnsDataCombo;
	}

	private void setColumnsDataCombo(List<ColumnDataCombo> columnsDataCombo) {
		this.columnsDataCombo = columnsDataCombo;
	}
}
