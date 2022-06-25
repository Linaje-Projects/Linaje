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

import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import linaje.gui.table.LTable;
import linaje.gui.table.ScrollableTable;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;

public class TestScrollableTable {

	//
	// TEST
	//
	public static void main(String[] args) {	
		try {
	
			LinajeLookAndFeel.init();
			
			int filas = 20;
			int columnas = 20;
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
			
			columnNames[0] = columnNames[0] + "\nOtra línea";
			
			LTable<String> table = new LTable<String>(datos, columnNames);
			
			filas = 10;
			String[][] hijos = new String[filas][columnas];
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					hijos[i][j] = "celdaHijo"+i+""+j;
				}
			}
			
			Vector<TreeNodeVector<String>> rowsChildren = TreeNodeVector.getNodes(hijos);
			TreeNodeVector<String> primeraFila = table.getModel().getRows().elementAt(0);
			table.getModel().addNodesInto(rowsChildren, primeraFila);
			
			TreeNodeVector<String> filaHijo3 = rowsChildren.elementAt(2);
			Vector<TreeNodeVector<String>> rowsChildrenLvl2 = TreeNodeVector.getNodes(hijos);
			table.getModel().addNodesInto(rowsChildrenLvl2, filaHijo3);
			
			filas = 3;
			String[][] datosRef = new String[filas][columnas];
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					datosRef[i][j] = "celdaRef"+i+""+j;
				}
			}
			LTable<String> tableRef = new LTable<String>(datosRef, columnNames);
			
			ScrollableTable<String> scrollableTable = new ScrollableTable<>(table, tableRef);
			List<Integer> fixedColumns = Lists.newList(0, 1);
			//List<Integer> fixedColumnIndices = Lists.newList(0, 1, 2, 3, 4);
			scrollableTable.setFixedColumnIndices(fixedColumns);
			scrollableTable.setWidthTableFixed(200);
			//scrollableTable.setRowSelectionAllowed(true);
			//scrollableTable.setColumnSelectionAllowed(false);
			scrollableTable.initTable(10);
			Dimension size = scrollableTable.getPreferredSize();
			size.height+= 10;
			scrollableTable.setSize(size);
			
			LDialogContent.showComponentInFrame(scrollableTable);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
