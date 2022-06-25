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
package linaje.gui.tree;

import linaje.tree.TreeNodeVector;

/**
 * 
 * Esta es una versión de javax.swing.tree.TreeModelEvent parametrizable que solo acepta TreeNodeVectors
 * 
 */
public class TreeModelEvent<E> extends javax.swing.event.TreeModelEvent {
   
	private static final long serialVersionUID = -2585085378007724820L;
	
    public TreeModelEvent(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children) {
        this(source, (path == null) ? null : new TreePath<E>(path), childIndices, children);
    }
    public TreeModelEvent(Object source, TreePath<E> path, int[] childIndices, TreeNodeVector<E>[] children) {
        super(source, path, childIndices, children);
    }
    public TreeModelEvent(Object source, TreeNodeVector<E>[] path) {
        this(source, (path == null) ? null : new TreePath<E>(path));
    }
    public TreeModelEvent(Object source, TreePath<E> path) {
        super(source, path);
    }

    @SuppressWarnings("unchecked")
	public TreePath<E> getTreePath() { 
    	return (TreePath<E>) super.getTreePath();
    }
    @SuppressWarnings("unchecked")
	public TreeNodeVector<E>[] getPath() {
       return (TreeNodeVector<E>[]) super.getPath();
    }
    @SuppressWarnings("unchecked")
	public TreeNodeVector<E>[] getChildren() {
        if(children != null) {
        	int cCount = children.length;
        	TreeNodeVector<E>[] retChildren = new TreeNodeVector[cCount];

            System.arraycopy(children, 0, retChildren, 0, cCount);
            return retChildren;
        }
        return null;
    }
}