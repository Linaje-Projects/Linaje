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
package linaje.gui.tests;

import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import linaje.gui.table.LTableEvent;
import linaje.gui.table.LTableListener;
import linaje.gui.table.LTableObject;
import linaje.gui.table.LTableTableObjects;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.tree.TreeNodeVector;

public class TestLTableTableObjects {

	public static void main(String[] args) {	
		try {
	
			LinajeLookAndFeel.init();
			
			int filas = 20;
			int columnas = 5;
			String[][] datos = new String[filas][columnas];
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					datos[i][j] = "celda"+i+""+j;
				}
			}
			String[] columnNames = new String[columnas];
			for (int i = 0; i < columnNames.length; i++) {
				columnNames[i] = "Column"+i;
			}
			
			LTableTableObjects<String> tableTree = new LTableTableObjects<String>(datos, columnNames);
			tableTree.getModel().addTableModelListener(new TableModelListener() {
				
				public void tableChanged(TableModelEvent e) {
					Console.println("tableChanged -- Type: "+e.getType() + ", column: "+e.getColumn() + ", FirstRow: "+e.getFirstRow() + ", LastRow: "+e.getLastRow());
				}
			});
			
			tableTree.addLTableListener(new LTableListener<LTableObject<String>>() {
				
				public void selectionChanged() {
					Console.println("selectionChanged");
				}
				public void columnClicked(LTableEvent<LTableObject<String>> event) {
					Console.println("columnClicked -- ColumnIndex: "+event.getColumnIndex() + ", RowIndex: "+event.getRowIndex());
				}
				public void cellClicked(LTableEvent<LTableObject<String>> event) {
					Console.println("cellClicked -- ColumnIndex: "+event.getColumnIndex() + ", RowIndex: "+event.getRowIndex());
				}
			});
			
			filas = 10;
			String[][] hijos = new String[filas][columnas];
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					hijos[i][j] = "celdaHijo"+i+""+j;
				}
			}
			
			Vector<TreeNodeVector<LTableObject<String>>> rowsChildren = LTableTableObjects.getRows(hijos);
			TreeNodeVector<LTableObject<String>> primeraFila = tableTree.getModel().getRows().elementAt(0);
			tableTree.getModel().addNodesInto(rowsChildren, primeraFila);
			
			TreeNodeVector<LTableObject<String>> filaHijo3 = rowsChildren.elementAt(2);
			Vector<TreeNodeVector<LTableObject<String>>> rowsChildrenLvl2 = LTableTableObjects.getRows(hijos);
			tableTree.getModel().addNodesInto(rowsChildrenLvl2, filaHijo3);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setSize(300, 400);
			scrollPane.setViewportView(tableTree);
			
			LDialogContent.showComponentInFrame(scrollPane);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
