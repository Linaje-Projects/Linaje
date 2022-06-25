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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import linaje.gui.AppGUI;
import linaje.gui.LPanel;
import linaje.gui.table.LTable;
import linaje.gui.table.LTableEvent;
import linaje.gui.table.LTableListener;
import linaje.gui.table.TableContainer;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.tree.TreeNodeVector;

@SuppressWarnings("serial")
public class TestTables extends LPanel {

	private LTable<String> table = null;
	
	public TestTables() {
		super();
		initialize();
	}

	private void initialize() {
		
		setName("Tables");
		boolean showHeader = true;
		final TableContainer tableContainer = new TableContainer(getTable(), showHeader);
		
		JCheckBox checkHeaderVisible = new JCheckBox("Show header", showHeader);
		checkHeaderVisible.setMargin(new Insets(1, 0, 5, 0));
		checkHeaderVisible.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				tableContainer.setHeaderVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		setLayout(new BorderLayout());
		add(checkHeaderVisible, BorderLayout.NORTH);
		add(tableContainer, BorderLayout.CENTER);
	}

	public LTable<String> getTable() {
		
		if (table == null) {
			
			int filas = 20;
			int columnas = 6;
			//Definimos una matriz de datos principal
			String[][] datos = new String[filas][columnas];
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					datos[i][j] = "Cell "+j+"_"+i+"#code";
				}
			}
			
			//Definimos textos de cabeceras con multiples líneas
			//de forma que algunas coincidan para hacer colspan
			String[] columnNames = new String[columnas];
			for (int i = 0; i < columnNames.length; i++) {
				columnNames[i] = "Column "+i;
			}
			for (int i = 0; i < 2; i++) {
				columnNames[i] = "Text 1\n" + columnNames[i];
			}
			for (int i = 0; i < 3; i++) {
				columnNames[i] = "This is a long text\n" + columnNames[i];
			}	
			for (int i = 3; i < 5; i++) {
				columnNames[i] = "Text 2\n" + columnNames[i];
			}
			columnNames[5] = "Text 3\n" + columnNames[5];
			
			//Nos ponemos a la escucha de eventos de cambios en el modelo de la tabla
			table = new LTable<String>(datos, columnNames);
			table.getModel().addTableModelListener(new TableModelListener() {	
				public void tableChanged(TableModelEvent e) {
					Console.println("tableChanged -- Type: "+e.getType() + ", column: "+e.getColumn() + ", FirstRow: "+e.getFirstRow() + ", LastRow: "+e.getLastRow());
				}
			});
			
			//Nos ponemos a la escucha de eventos de interacción sobre la tabla
			table.addLTableListener(new LTableListener<String>() {
				
				public void selectionChanged() {
					Console.println("selectionChanged");
				}
				public void columnClicked(LTableEvent<String> event) {
					Console.println("columnClicked -- ColumnIndex: "+event.getColumnIndex() + ", RowIndex: "+event.getRowIndex());
				}
				public void cellClicked(LTableEvent<String> event) {
					Console.println("cellClicked -- ColumnIndex: "+event.getColumnIndex() + ", RowIndex: "+event.getRowIndex());
				}
			});
			
			//Creamos una matriz de datos secundaria para añadirla como hijo de alguna fila de la matriz de datos principal
			filas = 10;
			String[][] hijos = new String[filas][columnas];
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					hijos[i][j] = "Child cell "+i+"_"+j;
				}
			}
			
			//Añadimos hijos a la primera fila de la tabla
			Vector<TreeNodeVector<String>> rowsChildren = TreeNodeVector.getNodes(hijos);
			TreeNodeVector<String> primeraFila = table.getModel().getRows().elementAt(0);
			table.getModel().addNodesInto(rowsChildren, primeraFila);
			
			//Añadimos hijos a la tercera fila de los hijos de la primera fila
			TreeNodeVector<String> filaHijo3 = rowsChildren.elementAt(2);
			Vector<TreeNodeVector<String>> rowsChildrenLvl2 = TreeNodeVector.getNodes(hijos);
			table.getModel().addNodesInto(rowsChildrenLvl2, filaHijo3);	
			
			//Cambiamos el color de la cabecera de la primera columna para ver como se pinta cuando hay colspan
			table.getColumn(0).setHeaderBackground(ColorsGUI.getColorImportant());
			table.getColumn(0).setHeaderForeground(Color.blue);
			//NOTA: EL color de la cabecera y el orden de las columnas se reseteará cuando cambie el modelo de datos de la tabla (por ejemplo al ordenar),
			//ya que autoCreateColumnsFromModel está a true, por lo que lo ponemos a false tras completar el modelo		
			table.setAutoCreateColumnsFromModel(false);
			
		}
		return table;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		//Truco para obtener la clase actual de forma estática y copiar y pegar éste main en otras clases
		Class currentClass = new Object() { }.getClass().getEnclosingClass();
		UtilsGUI.quickMain(currentClass);
	}
}
