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

import javax.swing.*;

import java.awt.*;

/**
 * Panel con un ScrollPane que se asigna a la tabla
 * Nos permite ocultar la cabecera de una tabla
 **/
@SuppressWarnings("serial")
public class TableContainer extends JPanel {
	
	private JPanel panelAuxTable = null;
	private JScrollPane scrollPane = null;
	private JTable table = null;
	private boolean headerVisible = true;
	
	public TableContainer(JTable table) {
		this(table, true);
	}
	public TableContainer(JTable table, boolean headerVisible) {
		super();
		setTable(table);
		initialize();
		setHeaderVisible(headerVisible);
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		add(getScrollPane(), BorderLayout.CENTER);
	}
	
	private JPanel getPanelAuxTable() {
		
		if (panelAuxTable == null) {
			panelAuxTable = new JPanel(new BorderLayout());
			panelAuxTable.setOpaque(false);
		}
		return panelAuxTable;
	}
	
	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
		}
		return scrollPane;
	}
	
	public JTable getTable() {
		return table;
	}
	private void setTable(JTable table) {
		this.table = table;
	}
	
	public void setHeaderVisible(boolean headerVisible) {
		this.headerVisible = headerVisible;
		updateView();
	}
	public boolean isHeaderVisible() {
		return headerVisible;
	}
	
	private void updateView() {
		if (isHeaderVisible())
			getScrollPane().setViewportView(getTable());
		else {
			getPanelAuxTable().add(getTable(), BorderLayout.NORTH);
			getScrollPane().setViewportView(getPanelAuxTable());
		}
	}
}
