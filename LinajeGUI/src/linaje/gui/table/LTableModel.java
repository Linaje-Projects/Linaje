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

import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import linaje.table.TableModelTree;
import linaje.tree.TreeNodeVector;

public class LTableModel<E> extends TableModelTree<E> implements javax.swing.table.TableModel {

	private static final long serialVersionUID = -4129547962107694626L;

	protected EventListenerList listenerList = null;
	
	private LTable<E> tabla;

	public LTableModel() {
		super();
	}
	public LTableModel(E[][] data, String[] columnNames) {
		super(data, columnNames);
	}
	public LTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}
	public LTableModel(String[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}
	public LTableModel(Vector<String> columnNames, int rowCount) {
		super(columnNames, rowCount);
	}
	public LTableModel(Vector<? extends TreeNodeVector<E>> data, Vector<String> columnNames) {
		super(data, columnNames);
	}

	
	//
	//  Managing Listeners
	//

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param   l               the TableModelListener
     */
	@Override
    public void addTableModelListener(TableModelListener l) {
		getListenerList().add(TableModelListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param   l               the TableModelListener
     */
	@Override
    public void removeTableModelListener(TableModelListener l) {
		getListenerList().remove(TableModelListener.class, l);
    }

    /**
     * Returns an array of all the table model listeners
     * registered on this model.
     *
     * @return all of this model's <code>TableModelListener</code>s
     *         or an empty
     *         array if no table model listeners are currently registered
     *
     * @see #addTableModelListener
     * @see #removeTableModelListener
     *
     * @since 1.4
     */
    public TableModelListener[] getTableModelListeners() {
        return getListenerList().getListeners(TableModelListener.class);
    }
    
    private EventListenerList getListenerList() {
    	if (listenerList == null)
    		listenerList = new EventListenerList();
    	return listenerList;
    }

	//
	//  Fire methods
	//

    /**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notifies all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the <code>JTable</code> receives this event and its
     * <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     *
     * @param  firstRow  the first row
     * @param  lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     *
     */
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    /**
     * Notifies all listeners that the value of the cell at
     * <code>[row, column]</code> has been updated.
     *
     * @param row  row of cell which has been updated
     * @param column  column of cell which has been updated
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableCellUpdated(int row, int column) {
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
    	TableModelListener[] tableModelListeners = getTableModelListeners();
    	if (tableModelListeners.length > 0) {
    		for (int i = 0; i < tableModelListeners.length; i++) {
    			tableModelListeners[i].tableChanged(e);
			}
    	}
    	if (getTable() != null)
    		getTable().clearSelection();
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>AbstractTableModel</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument
     * with a class literal,
     * such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * model <code>m</code>
     * for its table model listeners with the following code:
     *
     * <pre>TableModelListener[] tmls = (TableModelListener[])(m.getListeners(TableModelListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this component,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getTableModelListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return getListenerList().getListeners(listenerType);
    }
    
    //
	//  compatibilidad con javax.swing.table.TableModel
	//
    
    /**
     * Necesario para compatibilidad con javax.swing.table.TableModel. NO USAR
     */
	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		super.setValueAt((E) aValue, rowIndex, columnIndex);
	}
	
	//  *******************************************************************
	//  Hasta aquí tenemos la misma funcionalidad que un DefaultTableModel
	//  Ahora añadimos otras funcionalidades
	//  *******************************************************************
	
	//
	//  Métodos que necesitan la tabla a la que se ha asignado éste modelo
	//
	
	public LTable<E> getTable() {
		return tabla;
	}
	public void setTable(LTable<E> newTabla) {
		tabla = newTabla;
	}
	/**
	 * Para saber las selectedRows el modelo tiene que saber quien es la tabla que le contiene
	 */
	public Vector<TreeNodeVector<E>> getSelectedRows() {
		int[] selectedIndices = getTable() != null ? getTable().getSelectedRows() : null;
		return getRows(selectedIndices);
	}
	public TreeNodeVector<E> getSelectedRow() {
		Vector<TreeNodeVector<E>> selectedRows = getSelectedRows();
		return selectedRows == null || selectedRows.isEmpty() ? null : selectedRows.firstElement();
	}
	/**
	 * Para saber si una celda es editable el modelo tiene que saber quien es la tabla que le contiene
	 */
	public boolean isCellEditable(int row, int column) {
		try {
			// debo convertir la columna a la columna de la vista
			if (getTable() != null) {
				
				int c = getTable().convertColumnIndexToView(column);
				TableColumn tableColumn = getTable().getColumnModel().getColumn(c);
				if (tableColumn!= null && tableColumn instanceof LColumn)
					return ((LColumn) tableColumn).isEditable();
			}
		}
		catch (Throwable ex) {
		}
		
		return super.isCellEditable(row, column);
	}
}
