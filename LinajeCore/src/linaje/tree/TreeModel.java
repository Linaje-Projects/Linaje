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
package linaje.tree;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import linaje.table.DataIndices;
import linaje.utils.Lists;

/**
 * 
 * Esta es una versión de javax.swing.tree.DefaultTreeModel para evitar tener dependencias a las librerías de swing
 * Se ha añadido que es parametrizable y solo acepta TreeNodeVectors
 * Se han abstraido los métodos que disparan eventos para que los implemente un UI
 * 
 */
public abstract class TreeModel<E> implements Serializable {
    
	private static final long serialVersionUID = 991429111826575345L;
	
	/** Root of the tree. */
    protected TreeNodeVector<E> root;
    /**
      * Determines how the <code>isLeaf</code> method figures
      * out if a node is a leaf node. If true, a node is a leaf
      * node if it does not allow children. (If it allows
      * children, it is not a leaf node, even if no children
      * are present.) That lets you distinguish between <i>folder</i>
      * nodes and <i>file</i> nodes in a file system, for example.
      * <p>
      * If this value is false, then any node which has no
      * children is a leaf node, and any node may acquire
      * children.
      *
      * @see TreeNodeVector#getAllowsChildren
      * @see TreeModel#isLeaf
      * @see #setAsksAllowsChildren
      */
    protected boolean asksAllowsChildren;

    /**
      * Creates a tree in which any node can have children.
      *
      * @param root a TreeNodeVector object that is the root of the tree
      * @see #DefaultTreeModel(TreeNodeVector, boolean)
      */
     @ConstructorProperties({"root"})
     public TreeModel(TreeNodeVector<E> root) {
        this(root, false);
    }

    /**
      * Creates a tree specifying whether any node can have children,
      * or whether only certain nodes can have children.
      *
      * @param root a TreeNodeVector object that is the root of the tree
      * @param asksAllowsChildren a boolean, false if any node can
      *        have children, true if each node is asked to see if
      *        it can have children
      * @see #asksAllowsChildren
      */
    public TreeModel(TreeNodeVector<E> root, boolean asksAllowsChildren) {
        super();
        this.root = root;
        this.asksAllowsChildren = asksAllowsChildren;
    }

    /**
      * Sets whether or not to test leafness by asking getAllowsChildren()
      * or isLeaf() to the TreeNodes.  If newvalue is true, getAllowsChildren()
      * is messaged, otherwise isLeaf() is messaged.
      */
    public void setAsksAllowsChildren(boolean newValue) {
        asksAllowsChildren = newValue;
    }

    /**
      * Tells how leaf nodes are determined.
      *
      * @return true if only nodes which do not allow children are
      *         leaf nodes, false if nodes which have no children
      *         (even if allowed) are leaf nodes
      * @see #asksAllowsChildren
      */
    public boolean asksAllowsChildren() {
        return asksAllowsChildren;
    }

    /**
     * Sets the root to <code>root</code>. A null <code>root</code> implies
     * the tree is to display nothing, and is legal.
     */
    public void setRoot(TreeNodeVector<E> root) {
    	TreeNodeVector<E> oldRoot = this.root;
        this.root = root;
        if (root == null && oldRoot != null) {
            fireTreeStructureChanged(this, null, null, null);
        }
        else {
            nodeStructureChanged(root);
        }
    }

    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    public TreeNodeVector<E> getRoot() {
        return root;
    }

    /**
     * Returns the index of child in parent.
     * If either the parent or child is <code>null</code>, returns -1.
     * @param parent a note in the tree, obtained from this data source
     * @param child the node we are interested in
     * @return the index of the child in the parent, or -1
     *    if either the parent or the child is <code>null</code>
     */
    public int getIndexOfChild(TreeNodeVector<E> parent, TreeNodeVector<E> child) {
        if(parent == null || child == null)
            return -1;
        return parent.getIndex(child);
    }

    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> &gt;= 0 &amp;&amp;
     * <i>index</i> &lt; getChildCount(<i>parent</i>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public TreeNodeVector<E> getChild(TreeNodeVector<E> parent, int index) {
        return parent.getChildAt(index);
    }

    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getChildCount(TreeNodeVector<E> parent) {
        return parent.getChildCount();
    }

    /**
     * Returns whether the specified node is a leaf node.
     * The way the test is performed depends on the
     * <code>askAllowsChildren</code> setting.
     *
     * @param node the node to check
     * @return true if the node is a leaf node
     *
     * @see #asksAllowsChildren
     * @see TreeModel#isLeaf
     */
    public boolean isLeaf(TreeNodeVector<E> node) {
        if(asksAllowsChildren)
            return !node.getAllowsChildren();
        return node.isLeaf();
    }

    /**
     * Invoke this method if you've modified the {@code TreeNodeVector}s upon which
     * this model depends. The model will notify all of its listeners that the
     * model has changed.
     */
    public void reload() {
        reload(root);
    }

    /**
     * Invoked this to insert newChild at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create
     * the appropriate event.
     */
    public void insertNodeInto(TreeNodeVector<E> newChild, TreeNodeVector<E> parent, int index){ 
    	parent.insertChild(newChild, index);
        
    	int[] newIndexs = new int[1];
        newIndexs[0] = index;
        nodesWereInserted(parent, newIndexs);
    }
    public void insertNodeIntoRoot(TreeNodeVector<E> newChild, int index){ 
    	insertNodeInto(newChild, getRoot(), index);
    }
    public void addNodeInto(TreeNodeVector<E> newChild, TreeNodeVector<E> parent){ 
    	insertNodeInto(newChild, parent, parent.getChildCount());
    }
    public void addNodeToRoot(TreeNodeVector<E> newChild){ 
    	addNodeInto(newChild, getRoot());
    }

    public void addNodesToRoot(TreeNodeVector<E>[] newChilds) { 
    	addNodesInto(newChilds, getRoot());
    }
    public void addNodesToRoot(List<? extends TreeNodeVector<E>> newChilds) { 
    	addNodesInto(newChilds, getRoot());
    }
    public void addNodesInto(TreeNodeVector<E>[] newChilds, TreeNodeVector<E> parent) { 
    	List<TreeNodeVector<E>> newChildsList = Lists.arrayToList(newChilds);
    	insertNodesInto(newChildsList, parent, parent.getChildCount());
    }
    public void addNodesInto(List<? extends TreeNodeVector<E>> newChilds, TreeNodeVector<E> parent) { 
    	insertNodesInto(newChilds, parent, parent.getChildCount());
    }
    public void insertNodesIntoRoot(List<? extends TreeNodeVector<E>> newChilds, int index) {
    	insertNodesInto(newChilds, getRoot(), index);
    }
    public void insertNodesIntoRoot(TreeNodeVector<E>[] newChilds, int index) {
    	insertNodesInto(newChilds, getRoot(), index);
    }
    public void insertNodesInto(TreeNodeVector<E>[] newChilds, TreeNodeVector<E> parent, int index) {
    	List<TreeNodeVector<E>> newChildsList = Lists.arrayToList(newChilds);
    	insertNodesInto(newChildsList, parent, index);
    }
    public void insertNodesInto(List<? extends TreeNodeVector<E>> newChilds, TreeNodeVector<E> parent, int index) {
    	
    	if (!newChilds.isEmpty()) {
	    	
    		TreeNodeVector<E> firstParent = null;
    		List<Integer> newIndexs = Lists.newList();
    		int newIndex = index;
	        for (int i = 0; i < newChilds.size(); i++) {
	        	TreeNodeVector<E> childNode = newChilds.get(i);
	        	if (i == 0)
	        		firstParent = childNode.getParent();
	        	
	        	if (childNode.getParent() == firstParent) {
	        		//Sólo añadimos los hijos que tengan el mismo padre que el primer nodo para evitar añadir hijos directamente al root
		    		parent.insertChild(childNode, newIndex);
		    		newIndexs.add(newIndex);
		    		newIndex++;
	        	}
			}
	        
	        int[] newIndexsArray = Lists.listToArrayInt(newIndexs);
	        nodesWereInserted(parent, newIndexsArray);
    	}
    }
    
    
	
	/**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     */
	public void removeNodeFromParent(TreeNodeVector<E> node) {
		@SuppressWarnings("unchecked")
		TreeNodeVector<E>[] nodeChilds = new TreeNodeVector[]{node};
		removeNodesFromParent(nodeChilds);
	}
	public void removeNodesFromParent(List<TreeNodeVector<E>> nodeChilds) {
		TreeNodeVector<E>[] nodeChildsArray = Lists.listToArray(nodeChilds);
		removeNodesFromParent(nodeChildsArray);
	}
	public void removeNodesFromParent(TreeNodeVector<E>[] nodeChilds) {
		//Cogemos el padre del primer nodo y asumimos que el resto de nodos tienen el mismo padre
		TreeNodeVector<E> parent = nodeChilds[0].getParent();
		removeNodesFrom(nodeChilds, parent);
	}
	public void removeNodesFromRoot(List<TreeNodeVector<E>> nodeChilds) {
		TreeNodeVector<E>[] nodeChildsArray = Lists.listToArray(nodeChilds);
		removeNodesFromRoot(nodeChildsArray);
	}
	public void removeNodesFromRoot(TreeNodeVector<E>[] nodeChilds) {
		TreeNodeVector<E> parent = getRoot();
		removeNodesFrom(nodeChilds, parent);
	}
	public void removeNodesFrom(List<TreeNodeVector<E>> nodeChilds, TreeNodeVector<E> parent) {
		TreeNodeVector<E>[] nodeChildsArray = Lists.listToArray(nodeChilds);
		removeNodesFrom(nodeChildsArray, parent);
	}
	public void removeNodesFrom(TreeNodeVector<E>[] nodeChilds, TreeNodeVector<E> parent) {

		if (parent == null)
			throw new IllegalArgumentException("parent can't be null.");

		int[] childIndexs = new int[nodeChilds.length];
		for (int i = 0; i < nodeChilds.length; i++) {
			TreeNodeVector<E> nodeChild = nodeChilds[i];
			childIndexs[i] = parent.getIndex(nodeChild);
			if (childIndexs[i] == -1)
				throw new IllegalArgumentException("parent don't contain a node child.");
		}

		removeNodesFrom(childIndexs, nodeChilds, parent);
	}

	public void removeNodesFrom(int[] childIndexs, TreeNodeVector<E> parent) {

		if (parent == null)
			throw new IllegalArgumentException("node does not have a parent.");

		@SuppressWarnings("unchecked")
		TreeNodeVector<E>[] nodeChilds = new TreeNodeVector[childIndexs.length];
		for (int i = 0; i < childIndexs.length; i++) {
			int childIndex = childIndexs[i];
			nodeChilds[i] = parent.getChildAt(childIndex);
		}

		removeNodesFrom(childIndexs, nodeChilds, parent);
	}

	private void removeNodesFrom(int[] childIndexs, TreeNodeVector<E>[] nodeChilds, TreeNodeVector<E> parent) {

		if (parent != null) {

			for (int i = 0; i < childIndexs.length; i++) {
				parent.removeChild(childIndexs[i]);
			}
			nodesWereRemoved(parent, childIndexs, nodeChilds);
		}
	}

    /**
      * Invoke this method after you've changed how node is to be
      * represented in the tree.
      */
    public void nodeChanged(TreeNodeVector<E> node) {
        if(node != null) {
        	TreeNodeVector<E> parent = node.getParent();

            if(parent != null) {
                int anIndex = parent.getIndex(node);
                if(anIndex != -1) {
                    int[] cIndexs = new int[1];

                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
            else if (node == getRoot()) {
                nodesChanged(node, null);
            }
        }
    }

    /**
     * Invoke this method if you've modified the {@code TreeNodeVector}s upon which
     * this model depends. The model will notify all of its listeners that the
     * model has changed below the given node.
     *
     * @param node the node below which the model has changed
     */
    public void reload(TreeNodeVector<E> node) {
        if(node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
      * Invoke this method after you've inserted some TreeNodes into
      * node.  childIndices should be the index of the new elements and
      * must be sorted in ascending order.
      */
    public void nodesWereInserted(TreeNodeVector<E> node, int[] childIndices) {
        if(node != null && childIndices != null && childIndices.length > 0) {
            int cCount = childIndices.length;
            @SuppressWarnings("unchecked")
			TreeNodeVector<E>[] newChildren = new TreeNodeVector[cCount];

            for(int counter = 0; counter < cCount; counter++)
                newChildren[counter] = node.getChildAt(childIndices[counter]);
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
        }
    }

    /**
      * Invoke this method after you've removed some TreeNodes from
      * node.  childIndices should be the index of the removed elements and
      * must be sorted in ascending order. And removedChildren should be
      * the array of the children objects that were removed.
      */
    public void nodesWereRemoved(TreeNodeVector<E> node, int[] childIndices, TreeNodeVector<E>[] removedChildren) {
        if(node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
        }
    }

    /**
      * Invoke this method after you've changed how the children identified by
      * childIndicies are to be represented in the tree.
      */
    public void nodesChanged(TreeNodeVector<E> node, int[] childIndices) {
        if(node != null) {
            if (childIndices != null) {
                int cCount = childIndices.length;

                if(cCount > 0) {
                	@SuppressWarnings("unchecked")
					TreeNodeVector<E>[] cChildren = new TreeNodeVector[cCount];

                    for(int counter = 0; counter < cCount; counter++)
                        cChildren[counter] = node.getChildAt(childIndices[counter]);
                    fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
                }
            }
            else if (node == getRoot()) {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }

    /**
      * Invoke this method if you've totally changed the children of
      * node and its children's children...  This will post a
      * treeStructureChanged event.
      */
    public void nodeStructureChanged(TreeNodeVector<E> node) {
        if(node != null) {
           fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     *
     * @param aNode the TreeNodeVector to get the path for
     */
    public TreeNodeVector<E>[] getPathToRoot(TreeNodeVector<E> aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     *
     * @param aNode  the TreeNodeVector to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node
     */
    @SuppressWarnings("unchecked")
	protected TreeNodeVector<E>[] getPathToRoot(TreeNodeVector<E> aNode, int depth) {
    	TreeNodeVector<E>[] retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeNodeVector[depth];
        }
        else {
            depth++;
            if(aNode == root)
                retNodes = new TreeNodeVector[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    //
    //  Events
    //

    // Serialization support.
    private void writeObject(ObjectOutputStream s) throws IOException {
        Vector<Object> values = new Vector<Object>();

        s.defaultWriteObject();
        // Save the root, if its Serializable.
        if(root != null && root instanceof Serializable) {
            values.addElement("root");
            values.addElement(root);
        }
        s.writeObject(values);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        @SuppressWarnings("unchecked")
		Vector<TreeNodeVector<E>> values = (Vector<TreeNodeVector<E>>) s.readObject();
        int indexCounter = 0;
        int maxCounter = values.size();

        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("root")) {
            root = values.elementAt(++indexCounter);
            indexCounter++;
        }
    }

    public void restoreOriginalTreeSort() {
    	restoreOriginalChildrenSort(getRoot());
    }
    public void restoreOriginalChildrenSort(TreeNodeVector<E> parent) {
    	if (parent.getChildCount() > 0) {
	    	parent.restoreOriginalChildrenSort();
	    	nodeStructureChanged(parent);
    	}
    }
    
    public void sortTree(boolean useNaturalOrder, int index) {
    	sortChildren(getRoot(), useNaturalOrder, index);
    }
    public void sortChildren(TreeNodeVector<E> parent, boolean useNaturalOrder, int index) {
    	if (parent.getChildCount() > 0) {
	    	parent.sortChildren(useNaturalOrder, index);
	    	nodeStructureChanged(parent);
    	}
    }
    
    public boolean isSorted() {
    	return getRoot().areChildrenSorted();
    }
    public boolean useNaturalOrderSort() {
    	return getRoot().useNaturalOrderSort();
    }
    public int getIndexSort() {
    	return getRoot().getIndexSort();
    }
    public DataIndices getDataIndices() {
    	return getRoot().getDataIndices();
    }
    
    protected abstract void fireTreeNodesChanged(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children);
    protected abstract void fireTreeNodesInserted(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children);
    protected abstract void fireTreeNodesRemoved(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children);
    protected abstract void fireTreeStructureChanged(Object source, TreeNodeVector<E>[] path, int[] childIndices, TreeNodeVector<E>[] children);

} // End of class DefaultTreeModel