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
 * Esta versión de linaje.tree.TreeNodeVector implementa javax.swing.tree.TreeNode para que podamos usarlo en Árboles de Swing
 * 
 */
@SuppressWarnings("serial")
public class LTreeNode<E> extends TreeNodeVector<E> implements javax.swing.tree.TreeNode {

	public LTreeNode() {
		super();
	}

	public LTreeNode(E userObject) {
		super(userObject);
	}

	public LTreeNode(E userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getIndex(javax.swing.tree.TreeNode node) {
		return super.getIndex((LTreeNode<E>) node);
	}

	public LTreeNode<E> getParent() {
		return (LTreeNode<E>) super.getParent();
	}
	
	public LTreeNode<E> getChildAt(int index) {
		return (LTreeNode<E>) super.getChildAt(index);
	}
	
	//Compatibilidad con MutableTreeNode
	/*
	@SuppressWarnings("unchecked")
	public void setParent(MutableTreeNode newParent) {
		super.setParent((LTreeNode<E>) newParent);
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public void insert(MutableTreeNode child, int index) {
		super.insert((LTreeNode<E>) child, index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void remove(MutableTreeNode node) {
		super.remove((LTreeNode<E>) node);
	}
	*/
}
