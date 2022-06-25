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

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import linaje.tree.TreeModel;
import linaje.tree.TreeNodeVector;

@SuppressWarnings("serial")
public class LTreeModel<E> extends TreeModel<E> implements javax.swing.tree.TreeModel {

	/** Listeners. */
    protected EventListenerList listenerList = new EventListenerList();
    
    public LTreeModel(TreeNodeVector<E> root) {
		super(root);
	}

	public LTreeModel(TreeNodeVector<E> root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getChild(Object parent, int index) {
		return super.getChild((TreeNodeVector<E>) parent, index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildCount(Object parent) {
		return super.getChildCount((TreeNodeVector<E>) parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isLeaf(Object node) {
		return super.isLeaf((TreeNodeVector<E>) node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return super.getIndexOfChild((TreeNodeVector<E>) parent, (TreeNodeVector<E>) child);
	}

	/**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @see     #removeTreeModelListener
     * @param   l       the listener to add
     */
    public void addTreeModelListener(TreeModelListener<E> l) {
        listenerList.add(TreeModelListener.class, l);
    }
    /**
     * Necesario para compatibilidad con javax.swing.tree.TreeModel. NO USAR
     */
    @Override
	public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    	if (l != null && l instanceof TreeModelListener)
    		listenerList.add(javax.swing.event.TreeModelListener.class, l);
	}
	
    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener<E> l) {
        listenerList.remove(TreeModelListener.class, l);
    }
    /**
     * Necesario para compatibilidad con javax.swing.tree.TreeModel. NO USAR
     */
    @Override
	public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    	if (l != null && l instanceof TreeModelListener)
    		listenerList.remove(javax.swing.event.TreeModelListener.class, l);
	}
	
    /**
     * Returns an array of all the tree model listeners
     * registered on this model.
     *
     * @return all of this model's <code>TreeModelListener</code>s
     *         or an empty
     *         array if no tree model listeners are currently registered
     *
     * @see #addTreeModelListener
     * @see #removeTreeModelListener
     *
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
	public TreeModelListener<E>[] getTreeModelListeners() {
        return listenerList.getListeners(TreeModelListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent of the nodes that changed; use
     *             {@code null} to identify the root has changed
     * @param childIndices the indices of the changed elements
     * @param children the changed elements
     */
    protected void fireTreeNodesChanged(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children) {
    	// Process the listeners last to first, notifying
        // those that are interested in this event
    	TreeModelListener<E>[] treeModelListeners = getTreeModelListeners();
    	if (treeModelListeners.length > 0) {
    		TreeModelEvent<E> treeModelEvent = new TreeModelEvent<E>(source, path, childIndices, children);
    		for (int i = 0; i < treeModelListeners.length; i++) {
    			treeModelListeners[i].treeNodesChanged(treeModelEvent);
			}
    	}
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent the nodes were added to
     * @param childIndices the indices of the new elements
     * @param children the new elements
     */
    protected void fireTreeNodesInserted(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children) {
    	// Process the listeners last to first, notifying
        // those that are interested in this event
    	TreeModelListener<E>[] treeModelListeners = getTreeModelListeners();
    	if (treeModelListeners.length > 0) {
    		TreeModelEvent<E> treeModelEvent = new TreeModelEvent<E>(source, path, childIndices, children);
    		for (int i = 0; i < treeModelListeners.length; i++) {
    			treeModelListeners[i].treeNodesInserted(treeModelEvent);
			}
    	}
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent the nodes were removed from
     * @param childIndices the indices of the removed elements
     * @param children the removed elements
     */
    protected void fireTreeNodesRemoved(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children) {
    	// Process the listeners last to first, notifying
        // those that are interested in this event
    	TreeModelListener<E>[] treeModelListeners = getTreeModelListeners();
    	if (treeModelListeners.length > 0) {
    		TreeModelEvent<E> treeModelEvent = new TreeModelEvent<E>(source, path, childIndices, children);
    		for (int i = 0; i < treeModelListeners.length; i++) {
    			treeModelListeners[i].treeNodesRemoved(treeModelEvent);
			}
    	}
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent of the structure that has changed;
     *             use {@code null} to identify the root has changed
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     */
    protected void fireTreeStructureChanged(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children) {
    	// Process the listeners last to first, notifying
        // those that are interested in this event
    	TreeModelListener<E>[] treeModelListeners = getTreeModelListeners();
    	if (treeModelListeners.length > 0) {
    		TreeModelEvent<E> treeModelEvent = new TreeModelEvent<E>(source, path, childIndices, children);
    		for (int i = 0; i < treeModelListeners.length; i++) {
    			treeModelListeners[i].treeStructureChanged(treeModelEvent);
			}
    	}
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this model.
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
     * <code>DefaultTreeModel</code> <code>m</code>
     * for its tree model listeners with the following code:
     *
     * <pre>TreeModelListener[] tmls = (TreeModelListener[])(m.getListeners(TreeModelListener.class));</pre>
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
     * @see #getTreeModelListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

	/**
     * This sets the user object of the TreeNodeVector identified by path
     * and posts a node changed.  If you use custom user objects in
     * the TreeModel you're going to need to subclass this and
     * set the user object of the changed node to something meaningful.
     */
	@SuppressWarnings("unchecked")
	@Override
	public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {

	   TreeNodeVector<E> aNode = (TreeNodeVector<E>) path.getLastPathComponent();
       aNode.setUserObject((E) newValue);
       nodeChanged(aNode);
   }
}
