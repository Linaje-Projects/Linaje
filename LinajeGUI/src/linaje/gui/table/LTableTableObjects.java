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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;

public class LTableTableObjects<E> extends LTable<LTableObject<E>> {

	private static final long serialVersionUID = -7248340936062767572L;
	
	public LTableTableObjects() {
		super();
		initialize();
	}
	public LTableTableObjects(int numRows, int numColumns) {
		super(numRows, numColumns);
		initialize();
	}
	public LTableTableObjects(LTableModel<LTableObject<E>> dm,	TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		initialize();
	}
	public LTableTableObjects(LTableModel<LTableObject<E>> dm,	TableColumnModel cm) {
		super(dm, cm);
		initialize();
	}
	public LTableTableObjects(LTableModel<LTableObject<E>> dm) {
		super(dm);
		initialize();
	}
	public LTableTableObjects(LTableObject<E>[][] rowData, String[] columnNames) {
		super(rowData, columnNames);
		initialize();
	}
	public LTableTableObjects(Vector<? extends TreeNodeVector<LTableObject<E>>> rowData, Vector<String> columnNames) {
		super(rowData, columnNames);
		initialize();
	}
	/*public LTableTableObjects(Vector<LRow<E>> rowData, Vector<String> columnNames) {
		super(rowData, columnNames);
		initialize();
	}*/
	public LTableTableObjects(E[][] rowData, String[] columnNames) {
		this(getRows(rowData), Lists.arrayToVector(columnNames));
	}
	public LTableTableObjects(E[][] rowData, Vector<String> columnNames) {
		this(getRows(rowData), columnNames);
	}
	public LTableTableObjects(Collection<Collection<E>> rowData, Vector<String> columnNames) {
		super(getRows(rowData), columnNames);
		initialize();
	}
	
	private void initialize() {
		
	}
	
	//
	// Convert from E to LTableObject<E>
	//
	
	@SuppressWarnings("unchecked")
	private static <T> void setElementObjects(TreeNodeVector<LTableObject<T>> row, T... elements) {
		row.setElements(getElementsTableObject(row, elements));
	}
	private static <T> void setElementObjects(TreeNodeVector<LTableObject<T>> row, Collection<? extends T> elements) {
		row.setElements(getElementsTableObject(row, elements));
	}
	
	@SuppressWarnings("unchecked")
	@SafeVarargs
	private static <T> LTableObject<T>[] getElementsTableObject(TreeNodeVector<LTableObject<T>> row, T... elements) {
		
		LTableObject<T>[] elementsTableObjects = null;
		if (elements == null) {
			elementsTableObjects = Lists.newArray(LTableObject.class, 0);
		}
		else if (elements instanceof LTableObject[]) {
			elementsTableObjects = (LTableObject<T>[]) elements; 
		}
		else {
			elementsTableObjects = new LTableObject[elements.length];
			for (int i = 0; i < elements.length; i++) {
				elementsTableObjects[i] = new LTableObject<T>(row, elements[i]);
			}
			
		}
		return elementsTableObjects;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<LTableObject<T>> getElementsTableObject(TreeNodeVector<LTableObject<T>> row, Collection<? extends T> elements) {
		
		List<LTableObject<T>> elementsTableObjects = Lists.newList();
		if (elements != null) {
			for (T t : elements) {
				LTableObject<T> tableObject = (t instanceof LTableObject) ? (LTableObject<T>) t :  new LTableObject<T>(row, t);
				elementsTableObjects.add(tableObject);
			}
		}
		return elementsTableObjects;
	}
	
	public static <T> Vector<TreeNodeVector<LTableObject<T>>> getRows(T[][] elements) {
		
		Vector<TreeNodeVector<LTableObject<T>>> rows = new Vector<TreeNodeVector<LTableObject<T>>>();
		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
				T[] rowArray = elements[i];
				TreeNodeVector<LTableObject<T>> row = new TreeNodeVector<LTableObject<T>>(rowArray.length);
				setElementObjects(row, rowArray);
				rows.addElement(row);
			}
		}
		return rows;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Vector<TreeNodeVector<LTableObject<T>>> getRows(Collection<? extends Collection<T>> elements) {
		
		Vector<TreeNodeVector<LTableObject<T>>> rows = new Vector<TreeNodeVector<LTableObject<T>>>();
		if (elements != null) {
			for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
				Collection<T> collection = (Collection<T>) iterator.next();
				TreeNodeVector<LTableObject<T>> row = new TreeNodeVector<LTableObject<T>>(collection.size());
				setElementObjects(row, collection);
				rows.addElement(row);
			}
		}
		return rows;
	}	
}
