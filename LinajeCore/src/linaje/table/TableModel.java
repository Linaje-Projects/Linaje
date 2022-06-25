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
package linaje.table;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;

/**
 * 
 * Esta es una versión de javax.swing.table.TableModel para evitar tener dependencias a las librerías de swing
 * Se ha añadido que es parametrizable y solo acepta TreeNodeVectors
 * Se han abstraido los métodos que disparan eventos para que los implemente un UI
 * 
 */
public abstract class TableModel<E> implements Serializable {

	private static final long serialVersionUID = -4366449066883472405L;

//
// Instance Variables
//

    /**
     * The <code>Vector</code> of <code>Vectors</code> of
     * <code>Object</code> values.
     */
    protected Vector<TreeNodeVector<E>> rows;

    /** The <code>Vector</code> of column identifiers. */
    protected Vector<String> columnIdentifiers;

//
// Constructors
//

    /**
     *  Constructs a default <code>DefaultTableModel</code>
     *  which is a table of zero columns and zero rows.
     */
    public TableModel() {
        this(0, 0);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> with
     *  <code>rowCount</code> and <code>columnCount</code> of
     *  <code>null</code> object values.
     *
     * @param rowCount           the number of rows the table holds
     * @param columnCount        the number of columns the table holds
     *
     * @see #setValueAt
     */
    public TableModel(int rowCount, int columnCount) {
        this(new Vector<String>(columnCount), rowCount);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> with as many columns
     *  as there are elements in <code>columnNames</code>
     *  and <code>rowCount</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> vector.
     *
     * @param columnNames       <code>vector</code> containing the names
     *                          of the new columns; if this is
     *                          <code>null</code> then the model has no columns
     * @param rowCount           the number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public TableModel(Vector<String> columnNames, int rowCount) {
    	Vector<TreeNodeVector<E>> dataVector = new Vector<TreeNodeVector<E>>();
    	setRows(dataVector, columnNames);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> with as many
     *  columns as there are elements in <code>columnNames</code>
     *  and <code>rowCount</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> array.
     *
     * @param columnNames       <code>array</code> containing the names
     *                          of the new columns; if this is
     *                          <code>null</code> then the model has no columns
     * @param rowCount           the number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public TableModel(String[] columnNames, int rowCount) {
        this(Lists.arrayToVector(columnNames), rowCount);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> and initializes the table
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataVector</code> method.
     *
     * @param data              the data of the table, a <code>Vector</code>
     *                          of <code>Vector</code>s of <code>Object</code>
     *                          values
     * @param columnNames       <code>vector</code> containing the names
     *                          of the new columns
     * @see #getDataVector
     * @see #setDataVector
     */
    public TableModel(Vector<TreeNodeVector<E>> data, Vector<String> columnNames) {
    	setRows(data, columnNames);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> and initializes the table
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataVector</code>
     *  method. The first index in the <code>Object[][]</code> array is
     *  the row index and the second is the column index.
     *
     * @param data              the data of the table
     * @param columnNames       the names of the columns
     * @see #getDataVector
     * @see #setDataVector
     */
    public TableModel(E[][] data, String[] columnNames) {
    	setRows(data, columnNames);
    }

    /**
     *  Returns the <code>Vector</code> of <code>Vectors</code>
     *  that contains the table's
     *  data values.  The vectors contained in the outer vector are
     *  each a single row of values.  In other words, to get to the cell
     *  at row 1, column 5: <p>
     *
     *  <code>((Vector)getRows().elementAt(1)).elementAt(5);</code>
     *
     * @return  the vector of vectors containing the tables data values
     *
     * @see #newDataAvailable
     * @see #newRowsAdded
     * @see #setRows
     */
    /*public Vector<TreeNodeVector<E>> getDataVector() {
    	return getRows();
    }*/
    public Vector<TreeNodeVector<E>> getRows() {
    	if (rows == null)
    		rows = new Vector<TreeNodeVector<E>>();
        return rows;
    }
    public Vector<TreeNodeVector<E>> getRows(int[] rowIndices) {

    	Vector<TreeNodeVector<E>> rows = new Vector<TreeNodeVector<E>>();
    	if (rowIndices != null) {
			for (int i = 0; i < rowIndices.length; i++){
				rows.addElement(getRows().elementAt(rowIndices[i]));
			}
    	}
		return rows;
	}
    
    public Vector<String> getColumnIdentifiers() {
    	if (columnIdentifiers == null)
    		columnIdentifiers = new Vector<String>();
        return columnIdentifiers;
    }
    public Vector<String> getColumnNames() {
    	return getColumnIdentifiers();
    }
    
    /**
     *  Replaces the current <code>rows</code> instance variable
     *  with the new <code>Vector</code> of rows, <code>rows</code>.
     *  Each row is represented in <code>rows</code> as a
     *  <code>Vector</code> of <code>Object</code> values.
     *  <code>columnIdentifiers</code> are the names of the new
     *  columns.  The first name in <code>columnIdentifiers</code> is
     *  mapped to column 0 in <code>rows</code>. Each row in
     *  <code>dataVector</code> is adjusted to match the number of
     *  columns in <code>columnIdentifiers</code>
     *  either by truncating the <code>Vector</code> if it is too long,
     *  or adding <code>null</code> values if it is too short.
     *  <p>Note that passing in a <code>null</code> value for
     *  <code>dataVector</code> results in unspecified behavior,
     *  an possibly an exception.
     *
     * @param   dataVector         the new data vector
     * @param   columnIdentifiers     the names of the columns
     * @see #getDataVector
     */
   /* public void setDataVector(Vector<TreeNodeVector<E>> dataVector, Vector<String> columnIdentifiers) {
        setRows(dataVector, columnIdentifiers);
    }*/
    public void setRows(Vector<TreeNodeVector<E>> rows, Vector<String> columnIdentifiers) {
    	this.rows = rows;
        this.columnIdentifiers = columnIdentifiers;
        justifyRows(0, getRowCount());
        fireTableStructureChanged();
    }

    /**
     *  Replaces the value in the <code>dataVector</code> instance
     *  variable with the values in the array <code>dataVector</code>.
     *  The first index in the <code>Object[][]</code>
     *  array is the row index and the second is the column index.
     *  <code>columnIdentifiers</code> are the names of the new columns.
     *
     * @param dataVector                the new data vector
     * @param columnIdentifiers the names of the columns
     * @see #setDataVector(Vector, Vector)
     */
    public void setRows(E[][] dataArray, String[] columnIdentifiers) {
        setRows(getRows(dataArray), Lists.arrayToVector(columnIdentifiers));
    }
    /*
    public void setDataVector(E[][] dataArray, String[] columnIdentifiers) {
    	setRows(dataArray, columnIdentifiers);
    }*/

//
// Manipulating rows
//

    private void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed
        // instead of the AbstractTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        getRows().setSize(getRowCount());

        for (int i = from; i < to; i++) {
            if (getRows().elementAt(i) == null) {
            	getRows().setElementAt(new TreeNodeVector<E>(getColumnCount()), i);
            }
            else {
            	getRows().elementAt(i).setSize(getColumnCount());
            }
        }
    }

    /**
     *  Sets the number of rows in the model.  If the new size is greater
     *  than the current size, new rows are added to the end of the model
     *  If the new size is less than the current size, all
     *  rows at index <code>rowCount</code> and greater are discarded.
     *
     *  @see #setColumnCount
     * @since 1.3
     */
    public void setRowCount(int rowCount) {
    	int old = getRowCount();
        if (old == rowCount) {
            return;
        }
        getRows().setSize(rowCount);
        if (rowCount <= old) {
            fireTableRowsDeleted(rowCount, old-1);
        }
        else {
            justifyRows(old, rowCount);
            fireTableRowsInserted(old, rowCount-1);
        }
    }

    /**
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(TreeNodeVector<E> rowData) {
        insertRow(getRowCount(), rowData);
    }
    public void addRows(Vector<TreeNodeVector<E>> rowsData) {
        insertRows(getRowCount(), rowsData);
    }

    /**
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(E[] rowData) {
        addRow(getRowNode(rowData));
    }
    public void addRows(E[][] rowsData) {
        addRows(getRows(rowsData));
    }

    /**
     *  Inserts a row at <code>row</code> in the model.  The new row
     *  will contain <code>null</code> values unless <code>rowData</code>
     *  is specified.  Notification of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
     */
    public void insertRow(int rowIndex, TreeNodeVector<E> rowData) {
    	Vector<TreeNodeVector<E>> rows = new Vector<TreeNodeVector<E>>(1);
    	rows.addElement(rowData);
    	insertRows(rowIndex, rows);
    }
    
    /**
     *  Inserts rows at <code>row</code> in the model.  The new rows
     *  will contain <code>null</code> values unless <code>rowData</code>
     *  is specified.  Notification of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
     */
    public void insertRows(int rowIndex, Vector<TreeNodeVector<E>> rows) {
    	if (rows != null && !rows.isEmpty()) {
    		int rowsInserted = insertRowsAndChildren(rowIndex, rows);
	    	justifyRows(rowIndex, rowIndex + rowsInserted);
	        fireTableRowsInserted(rowIndex, rowIndex + rowsInserted - 1);
    	}
    }
    
    private int insertRowsAndChildren(int rowIndex, Vector<TreeNodeVector<E>> rows) {
    	int rowsInserted = 0;
    	if (rows != null && !rows.isEmpty()) {
	    	for (int i = 0; i < rows.size(); i++) {
	    		TreeNodeVector<E> rowData = rows.elementAt(i);
	    		getRows().insertElementAt(rowData, rowIndex+rowsInserted);
	    		rowsInserted++;
	    		if (rowData.isExpanded() && rowData.getChildCount() > 0) {
	    			int rowsChildrenInserted = insertRowsAndChildren(rowIndex + rowsInserted + 1, rowData.getChildrenCopyVector());
	    			rowsInserted = rowsInserted + rowsChildrenInserted;
	    		}
			}
    	}
    	return rowsInserted;
    }

    /**
     *  Inserts a row at <code>row</code> in the model.  The new row
     *  will contain <code>null</code> values unless <code>rowData</code>
     *  is specified.  Notification of the row being added will be generated.
     *
     * @param   row      the row index of the row to be inserted
     * @param   rowData          optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
     */
    public void insertRow(int row, E[] rowData) {
        insertRow(row, getRowNode(rowData));
    }
    public void insertRows(int row, E[][] rowsData) {
    	insertRows(row, getRows(rowsData));
    }

    /**
     *  Moves one or more rows from the inclusive range <code>start</code> to
     *  <code>end</code> to the <code>to</code> position in the model.
     *  After the move, the row that was at index <code>start</code>
     *  will be at index <code>to</code>.
     *  This method will send a <code>tableChanged</code> notification
       message to all the listeners.
     *
     *  <pre>
     *  Examples of moves:
     *
     *  1. moveRow(1,3,5);
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|g|h|B|C|D|i|j|k   - after
     *
     *  2. moveRow(6,7,1);
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     *  </pre>
     *
     * @param   start       the starting row index to be moved
     * @param   end         the ending row index to be moved
     * @param   to          the destination of the rows to be moved
     * @exception  ArrayIndexOutOfBoundsException  if any of the elements
     * would be moved out of the table's range
     *
     */
    public void moveRows(int start, int end, int to) {
        
    	int shift = to - start;
        int first, last;
        if (shift < 0) {
            first = to;
            last = end;
        }
        else {
            first = start;
            last = to + end - start;
        }
        
        rotateElements(getRows(), first, last, shift);
        
        fireTableRowsUpdated(first, last);
    }
    
    /**
     *  Moves one or more elements from the inclusive range <code>start</code> to
     *  <code>end</code> to the <code>to</code> position in the model.
     *  After the move, the element that was at index <code>start</code>
     *  will be at index <code>to</code>.
     *
     *  <pre>
     *  Examples of moves:
     *
     *  1. moveElements(1,3,5);
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|g|h|B|C|D|i|j|k   - after
     *
     *  2. moveElements(6,7,1);
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     *  </pre>
     *
     * @param   start       the starting element index to be moved
     * @param   end         the ending element index to be moved
     * @param   to          the destination of the elements to be moved
     * @exception  ArrayIndexOutOfBoundsException  if any of the elements
     * would be moved out of the list's range
     *
     */
    public static <T> void moveElements(List<T> list, int start, int end, int to) {
    	
    	int shift = to - start;
        int first, last;
        if (shift < 0) {
            first = to;
            last = end;
        }
        else {
            first = start;
            last = to + end - start;
        }
        
        rotateElements(list, first, last, shift);
    }
    
    private static <T> void rotateElements(List<T> list, int first, int last, int shift) {
        
        int size = last - first + 1;
        int r = size - shift;
        int g = gcd(size, r);
        
        for(int i = 0; i < g; i++) {
            int toTmp = i;
            T elem = list.get(first + toTmp);
            /*if (elem instanceof TreeNodeVector) {
            	TreeNodeVector<?> elemNV = (TreeNodeVector<?>) elem;
            	if (elemNV.getChildCount() > 0) {
            		rotateElements(elemNV.getChildrenCopyVector(), first, last, shift);
            	}
            }*/
            for(int from = (toTmp + r) % size; from != i; from = (toTmp + r) % size) {
            	int indexFrom = first + from;
            	int indexTo = first + toTmp;
            	list.set(indexTo, list.get(indexFrom));
            	toTmp = from;
            }
            list.set(first + toTmp, elem);
        }
    }
    private static int gcd(int i, int j) {
        return (j == 0) ? i : gcd(j, i%j);
    }

    /**
     *  Se borrará la fila y todos los hijos de ésta
     */
    public void removeRow(int rowIndex) {
    	removeRows(rowIndex, rowIndex);
    }
    
    public void removeRow(TreeNodeVector<E> rowNodeVector) {
    	int rowIndex = getRows().indexOf(rowNodeVector);
    	if (rowIndex != -1) {
    		removeRow(rowIndex);
    	}
    }
    
    /**
     *  Se borrarán las filas del nivel de la primera desde firstRow hasta lastRowSameLevel
     *  y todos los hijos de éstas
     *  Si hacemos removeRows(3,5) del siguiente ejemplo:
     *  Fila1 Nivel1
     *  	Fila11 Nivel2
     *  	Fila12 Nivel2
     *  Fila2 Nivel1
     *  	Fila21 Nivel2
     *  		Fila31 Nivel3
     *  		Fila32 Nivel3
     *  Fila3 Nivel1
     *  	Fila31 Nivel2
     *  Fila4 Nivel1
     *  	Fila41 Nivel2
     *  Fila5 Nivel1
     *  	Fila11 Nivel2
     *  	Fila21 Nivel2
     *  
     *  Daría como resultado:
     *  Fila1 Nivel1
     *  	Fila11 Nivel2
     *  	Fila12 Nivel2
     *  Fila5 Nivel1
     *  	Fila11 Nivel2
     *  	Fila21 Nivel2
     *  
     *  Si hubieramos hecho removeRows(1,2) habría dado el siguiente resultado:
     *  Fila1 Nivel1
     *  Fila2 Nivel1
     *  	Fila21 Nivel2
     *  		Fila31 Nivel3
     *  		Fila32 Nivel3
     *  Fila3 Nivel1
     *  	Fila31 Nivel2
     *  Fila4 Nivel1
     *  	Fila41 Nivel2
     *  Fila5 Nivel1
     *  	Fila11 Nivel2
     *  	Fila21 Nivel2
     *  
     */
    public void removeRows(int firstRow, int lastRowSameLevel) {
    	int rowsRemoved = removeRowsAndChildren(firstRow, lastRowSameLevel);
        fireTableRowsDeleted(firstRow, firstRow + rowsRemoved - 1);
    }
    
    private int removeRowsAndChildren(int firstRow, int lastRowSameLevel) {
    	if (firstRow > lastRowSameLevel) {
    		int aux = firstRow;
    		firstRow = lastRowSameLevel;
    		lastRowSameLevel = aux;
    	}
    	
    	int rowsRemoved = 0;
    	for (int i = firstRow; i <= lastRowSameLevel; i++) {
	    	TreeNodeVector<E> rowRemoved = getRows().elementAt(firstRow);
	        getRows().removeElementAt(firstRow);
	        rowsRemoved++;
	        
	        if (rowRemoved.isExpanded() && rowRemoved.getChildCount() > 0) {
	        	//Antes de borrar los hijos, nos aseguramos de que realmente están incluidos en el modelo
	        	if (getRows().elementAt(firstRow) == rowRemoved.getChildAt(0)) {
	        		int rowsChildrenRemoved = removeRowsAndChildren(firstRow, firstRow + rowRemoved.getChildCount() - 1);
	        		rowsRemoved = rowsRemoved + rowsChildrenRemoved;
	        	}
	        }
    	}
    	return rowsRemoved;
    }

//
// Manipulating columns
//

    /**
     * Replaces the column identifiers in the model.  If the number of
     * <code>newIdentifier</code>s is greater than the current number
     * of columns, new columns are added to the end of each row in the model.
     * If the number of <code>newIdentifier</code>s is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded.
     *
     * @param   columnIdentifiers  vector of column identifiers.  If
     *                          <code>null</code>, set the model
     *                          to zero columns
     * @see #setNumRows
     */
    public void setColumnIdentifiers(Vector<String> columnIdentifiers) {
        setRows(getRows(), columnIdentifiers);
    }

    /**
     * Replaces the column identifiers in the model.  If the number of
     * <code>newIdentifier</code>s is greater than the current number
     * of columns, new columns are added to the end of each row in the model.
     * If the number of <code>newIdentifier</code>s is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded.
     *
     * @param   newIdentifiers  array of column identifiers.
     *                          If <code>null</code>, set
     *                          the model to zero columns
     * @see #setNumRows
     */
    public void setColumnIdentifiers(String[] newIdentifiers) {
        setColumnIdentifiers(Lists.arrayToVector(newIdentifiers));
    }

    /**
     *  Sets the number of columns in the model.  If the new size is greater
     *  than the current size, new columns are added to the end of the model
     *  with <code>null</code> cell values.
     *  If the new size is less than the current size, all columns at index
     *  <code>columnCount</code> and greater are discarded.
     *
     *  @param columnCount  the new number of columns in the model
     *
     *  @see #setColumnCount
     * @since 1.3
     */
    public void setColumnCount(int columnCount) {
    	getColumnIdentifiers().setSize(columnCount);
        justifyRows(0, getRowCount());
        fireTableStructureChanged();
    }

    /**
     *  Adds a column to the model.  The new column will have the
     *  identifier <code>columnName</code>, which may be null.  This method
     *  will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *  This method is a cover for <code>addColumn(Object, Vector)</code> which
     *  uses <code>null</code> as the data vector.
     *
     * @param   columnName the identifier of the column being added
     */
    public void addColumn(String columnName) {
        addColumn(columnName, (Vector<E>) null);
    }

    /**
     *  Adds a column to the model.  The new column will have the
     *  identifier <code>columnName</code>, which may be null.
     *  <code>columnData</code> is the
     *  optional vector of data for the column.  If it is <code>null</code>
     *  the column is filled with <code>null</code> values.  Otherwise,
     *  the new data will be added to model starting with the first
     *  element going to row 0, etc.  This method will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *
     * @param   columnName the identifier of the column being added
     * @param   columnData       optional data of the column being added
     */
    public void addColumn(String columnName, Vector<E> columnData) {
        getColumnIdentifiers().addElement(columnName);
        if (columnData != null) {
            int columnSize = columnData.size();
            if (columnSize > getRowCount()) {
            	getRows().setSize(columnSize);
            }
            justifyRows(0, getRowCount());
            int newColumn = getColumnCount() - 1;
            for(int i = 0; i < columnSize; i++) {
                  TreeNodeVector<E> row = getRows().elementAt(i);
                  row.setElementAt(columnData.elementAt(i), newColumn);
            }
        }
        else {
            justifyRows(0, getRowCount());
        }

        fireTableStructureChanged();
    }

    /**
     *  Adds a column to the model.  The new column will have the
     *  identifier <code>columnName</code>.  <code>columnData</code> is the
     *  optional array of data for the column.  If it is <code>null</code>
     *  the column is filled with <code>null</code> values.  Otherwise,
     *  the new data will be added to model starting with the first
     *  element going to row 0, etc.  This method will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *
     * @see #addColumn(Object, Vector)
     */
    public void addColumn(String columnName, E[] columnData) {
        addColumn(columnName, Lists.arrayToVector(columnData));
    }

//
// Implementing the TableModel interface
//

    /**
     * Returns the number of rows in this data table.
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return getRows().size();
    }

    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return getColumnIdentifiers().size();
    }

    /**
     * Returns the column name.
     *
     * @return a name for this column using the string value of the
     * appropriate member in <code>columnIdentifiers</code>.
     * If <code>columnIdentifiers</code> does not have an entry
     * for this index, returns the default
     * name provided by the superclass.
     */
    public String getColumnName(int column) {
        Object id = null;
        // This test is to cover the case when
        // getColumnCount has been subclassed by mistake ...
        if (column < getColumnIdentifiers().size() && (column >= 0)) {
            id = getColumnIdentifiers().elementAt(column);
        }
        return (id == null) ? getAbstractColumnName(column) : id.toString();
    }
    
    private String getAbstractColumnName(int column) {
        String result = "";
        for (; column >= 0; column = column / 26 - 1) {
            result = (char)((char)(column%26)+'A') + result;
        }
        return result;
    }

    /**
     * Returns true regardless of parameter values.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  true
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Returns an attribute value for the cell at <code>row</code>
     * and <code>column</code>.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  the value Object at the specified cell
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public E getValueAt(int row, int column) {
        TreeNodeVector<E> rowVector = getRows().elementAt(row);
        return rowVector.elementAt(column);
    }

    /**
     * Sets the object value for the cell at <code>column</code> and
     * <code>row</code>.  <code>aValue</code> is the new value.  This method
     * will generate a <code>tableChanged</code> notification.
     *
     * @param   aValue          the new value; this can be null
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public void setValueAt(E aValue, int row, int column) {
    	TreeNodeVector<E> rowVector = getRows().elementAt(row);
        rowVector.setElementAt(aValue, column);
        fireTableCellUpdated(row, column);
    }

//
// Protected Methods
//

    /**
     * Returns a vector of vectors that contains the same objects as the array.
     * @param anArray  the double array to be converted
     * @return the new vector of vectors; if <code>anArray</code> is
     *                          <code>null</code>, returns <code>null</code>
     */
    protected static <T> Vector<TreeNodeVector<T>> getRows(T[][] elementsRows) {
    	return TreeNodeVector.getNodes(elementsRows);
    }
    
    @SafeVarargs
	protected static <T> TreeNodeVector<T> getRowNode(T... elements) {
        return TreeNodeVector.newNode(elements);
    }

    /**
     * Returns a column given its name.
     * Implementation is naive so this should be overridden if
     * this method is to be called often. This method is not
     * in the <code>TableModel</code> interface and is not used by the
     * <code>JTable</code>.
     *
     * @param columnName string containing name of column to be located
     * @return the column with <code>columnName</code>, or -1 if not found
     */
    public int findColumn(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     *  @param columnIndex  the column being queried
     *  @return the Object.class
     */
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    //
	//  Fire methods
	//

    public abstract void fireTableDataChanged();
    public abstract void fireTableStructureChanged();
    public abstract void fireTableRowsInserted(int firstRow, int lastRow);
    public abstract void fireTableRowsUpdated(int firstRow, int lastRow);
    public abstract void fireTableRowsDeleted(int firstRow, int lastRow);
    public abstract void fireTableCellUpdated(int row, int column);

} // End of class DefaultTableModel