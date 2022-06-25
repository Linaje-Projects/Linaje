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

import java.io.Serializable;

import linaje.tree.TreeNodeVector;

/**
 * 
 * Esta es una versión de javax.swing.tree.TreePath parametrizable que solo acepta TreeNodeVectors
 * 
 */
public class TreePath<E> extends javax.swing.tree.TreePath implements Serializable {
   
	private static final long serialVersionUID = 3003554773998000514L;
	
	/** Path representing the parent, null if lastPathComponent represents
     * the root. */
    private TreePath<E> parentPath;
    /** Last path component. */
    private TreeNodeVector<E> lastPathComponent;

    /**
     * Creates a {@code TreePath} from an array. The array uniquely
     * identifies the path to a node.
     *
     * @param path an array of objects representing the path to a node
     * @throws IllegalArgumentException if {@code path} is {@code null},
     *         empty, or contains a {@code null} value
     */
    public TreePath(TreeNodeVector<E>[] pathNodes) {
        if(pathNodes == null || pathNodes.length == 0)
            throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
        lastPathComponent = pathNodes[pathNodes.length - 1];
        if (lastPathComponent == null) {
            throw new IllegalArgumentException(
                "Last path component must be non-null");
        }
        if(pathNodes.length > 1)
            parentPath = new TreePath<E>(pathNodes, pathNodes.length - 1);
    }

    /**
     * Creates a {@code TreePath} containing a single element. This is
     * used to construct a {@code TreePath} identifying the root.
     *
     * @param lastPathComponent the root
     * @see #TreePath(Object[])
     * @throws IllegalArgumentException if {@code lastPathComponent} is
     *         {@code null}
     */
    public TreePath(TreeNodeVector<E> lastPathComponent) {
        if(lastPathComponent == null)
            throw new IllegalArgumentException("path in TreePath must be non null.");
        this.lastPathComponent = lastPathComponent;
        parentPath = null;
    }

    /**
     * Creates a {@code TreePath} with the specified parent and element.
     *
     * @param parent the path to the parent, or {@code null} to indicate
     *        the root
     * @param lastPathComponent the last path element
     * @throws IllegalArgumentException if {@code lastPathComponent} is
     *         {@code null}
     */
    protected TreePath(TreePath<E> parent, TreeNodeVector<E> lastPathComponent) {
        if(lastPathComponent == null)
            throw new IllegalArgumentException("path in TreePath must be non null.");
        parentPath = parent;
        this.lastPathComponent = lastPathComponent;
    }

    /**
     * Creates a {@code TreePath} from an array. The returned
     * {@code TreePath} represents the elements of the array from
     * {@code 0} to {@code length - 1}.
     * <p>
     * This constructor is used internally, and generally not useful outside
     * of subclasses.
     *
     * @param path the array to create the {@code TreePath} from
     * @param length identifies the number of elements in {@code path} to
     *        create the {@code TreePath} from
     * @throws NullPointerException if {@code path} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if {@code length - 1} is
     *         outside the range of the array
     * @throws IllegalArgumentException if any of the elements from
     *         {@code 0} to {@code length - 1} are {@code null}
     */
    protected TreePath(TreeNodeVector<E>[] pathNodes, int length) {
        lastPathComponent = pathNodes[length - 1];
        if (lastPathComponent == null) {
            throw new IllegalArgumentException(
                "Path elements must be non-null");
        }
        if(length > 1)
            parentPath = new TreePath<E>(pathNodes, length - 1);
    }

    /**
     * Creates an empty {@code TreePath}.  This is provided for
     * subclasses that represent paths in a different
     * manner. Subclasses that use this constructor must override
     * {@code getLastPathComponent}, and {@code getParentPath}.
     */
    protected TreePath() {
    }

    /**
     * Returns the last element of this path.
     *
     * @return the last element in the path
     */
    public TreeNodeVector<E> getLastPathComponent() {
        return lastPathComponent;
    }

    /**
     * Returns the {@code TreePath} of the parent. A return value of
     * {@code null} indicates this is the root node.
     *
     * @return the parent path
     */
    public TreePath<E> getParentPath() {
        return parentPath;
    }

   @SuppressWarnings("unchecked")
	public TreeNodeVector<E>[] getPath() {
    	int i = getPathCount();
        TreeNodeVector<E>[] result = new TreeNodeVector[i--];

        for(TreePath<E> path = this; path != null; path = path.getParentPath()) {
            result[i--] = path.getLastPathComponent();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
	public TreeNodeVector<E> getPathComponent(int index) {
    	return (TreeNodeVector<E>) super.getPathComponent(index);
        /*
    	int pathLength = getPathCount();

        if(index < 0 || index >= pathLength)
            throw new IllegalArgumentException("Index " + index + " is out of the specified range");

        TreePath<E> path = this;

        for(int i = pathLength-1; i != index; i--) {
            path = path.getParentPath();
        }
        return path.getLastPathComponent();
        */
    }

    /**
     * Returns a new path containing all the elements of this path
     * plus <code>child</code>. <code>child</code> is the last element
     * of the newly created {@code TreePath}.
     *
     * @param child the path element to add
     * @throws NullPointerException if {@code child} is {@code null}
     */
    public TreePath<E> pathByAddingChild(TreePath<E> child) {
        if(child == null)
            throw new NullPointerException("Null child not allowed");

        return new TreePath<E>(this, child.getLastPathComponent());
    }
    /**
     * Necesario para compatibilidad con javax.swing.tree.TreePath. NO USAR
     */
    @SuppressWarnings("unchecked")
	@Override
    public TreePath<E> pathByAddingChild(Object child) {
    	if (child != null && child instanceof TreePath)
    		return pathByAddingChild((TreePath<E>) child);
    	else
    		return null;
    }
    
    /*
    public int getPathCount() {
        int result = 0;
        for(TreePath<E> path = this; path != null; path = path.getParentPath()) {
            result++;
        }
        return result;
    }

    public boolean equals(TreePath<E> o) {
        if(o == this)
            return true;
        if(o instanceof TreePath) {
            TreePath<E> oTreePath = o;

            if(getPathCount() != oTreePath.getPathCount())
                return false;
            for(TreePath<E> path = this; path != null;
                path = path.getParentPath()) {
                if (!(path.getLastPathComponent().equals(oTreePath.getLastPathComponent()))) {
                    return false;
                }
                oTreePath = oTreePath.getParentPath();
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return getLastPathComponent().hashCode();
    }

    public boolean isDescendant(TreePath<E> aTreePath) {
        if(aTreePath == this)
            return true;

        if(aTreePath != null) {
            int pathLength = getPathCount();
            int oPathLength = aTreePath.getPathCount();

            if(oPathLength < pathLength)
                // Can't be a descendant, has fewer components in the path.
                return false;
            while(oPathLength-- > pathLength)
                aTreePath = aTreePath.getParentPath();
            return equals(aTreePath);
        }
        return false;
    }
    
    public String toString() {
        StringBuffer tempSpot = new StringBuffer("[");

        for(int counter = 0, maxCounter = getPathCount();counter < maxCounter; counter++) {
            if(counter > 0)
                tempSpot.append(", ");
            tempSpot.append(getPathComponent(counter));
        }
        tempSpot.append("]");
        return tempSpot.toString();
    }
    */
}
