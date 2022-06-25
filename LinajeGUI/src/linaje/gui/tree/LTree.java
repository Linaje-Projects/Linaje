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

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import linaje.gui.LMenuItem;
import linaje.gui.LPopupMenu;
import linaje.gui.renderers.LCellRenderer;
import linaje.logs.Console;
import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class LTree<E> extends JTree {

	public static final int COMPORTAMIENTO_NORMAL = 0;
	public static final int COMPORTAMIENTO_SELECCION_MULTIPLE_SIN_CTRL = 1;
	
	private int comportamiento = COMPORTAMIENTO_NORMAL;
	//private int ultimaFilaSeleccionada = -1;
	private LTreeNode<E> rootNode = null;
	private LTreeNode<E> ultimoNodoSeleccionado = null;
	
	public LTree() {
		super(getTreeModelDefecto(null));
		initialize();
	}
	public LTree(Object[] value) {
		super(getTreeModelDefecto(value));
		initialize();
	}
	public LTree(LTreeModel<E> newModel) {
		super(newModel);
		initialize();
	}
	
	protected static <T> LTreeModel<T> getTreeModelDefecto(T[] values) {
		LTreeNode<T> root = new LTreeNode<T>();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				root.addChild(new LTreeNode<T>(values[i]));
			}
		}
        return new LTreeModel<>(root);
    }
	
	public LTreeNode<E> addElement(E elemento) {
		return addElement(elemento, null);
	}
	public LTreeNode<E> addElement(E elemento, LTreeNode<E> parentNode) {
		LTreeNode<E> node = new LTreeNode<E>(elemento, false);
		addNode(node, parentNode);
		return node;
	}
	public void addNode(LTreeNode<E> elemento) {
		addNode(elemento, null);
	}
	public void addNode(LTreeNode<E> node, LTreeNode<E> padre) {
	
		if (node != null) {
	
			if (padre == null)
				padre = getRootNode();
			
			if (!padre.getAllowsChildren())
				padre.setAllowsChildren(true);
	
			getModel().addNodeInto(node, padre);
		}
	}
	
	public void expandParent(LTreeNode<E> node) {
	
		try {
	
			if (node != null && node.getParent() != null) {
	
				LTreeNode<E> parentNode = (LTreeNode<E>) node.getParent();
				TreePath<E> parentPath = new TreePath<E>(parentNode.getPath());
				expandPath(parentPath);
			}
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public void expandRoot() {
		if (getRootNode().getChildCount() > 0) {
			TreePath<E> treePathCuspide = new TreePath<E>(getRootNode().getPath());
			expandPath(treePathCuspide);
		}
	}
	
	public void expandParents(Vector<LTreeNode<E>> nodes) {
	
		try {
	
			for (int i = 0; i < nodes.size(); i++) {
				
				LTreeNode<E> nodo = nodes.elementAt(i);
				expandParent(nodo);
			}
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	/**
	 * Sobreescribimos para actualizar la propiedad expanded de TreeNodeVector
	 */
	@Override
	protected void setExpandedState(javax.swing.tree.TreePath path, boolean state) {
		super.setExpandedState(path, state);
		@SuppressWarnings("unchecked")
		TreeNodeVector<E> nodeVector = (TreeNodeVector<E>) path.getLastPathComponent();
		nodeVector.setExpanded(state);
	}
	
	public void collapseRoot() {
		
		//expandAll(false);
		collapseNode(getRootNode());
		setExpandedState(new TreePath<E>(getRootNode().getPath()), true);
	}
	
	public void collapseNode(LTreeNode<E> node) {
		
		List<TreePath<E>> expandendPaths = getExpandendPaths(new TreePath<E>(node));
		//Hay que ordenarlos porque si no se pliegan en orden (de mas abajo a mas arriba) no se pliega bien
		Lists.sortElemsToString(expandendPaths);
		for (int i = expandendPaths.size()-1; i >= 0; i--) {
			TreePath<E> expandedPath = expandendPaths.get(i);
			collapsePath(expandedPath);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<TreePath<E>> getExpandendPaths(TreePath<E> path) {
		
		Enumeration<javax.swing.tree.TreePath> expandedDescendats = getExpandedDescendants(path);
		List list = Lists.newList();
		Lists.addEnumerationElements(list, expandedDescendats);
		
		return list;
	}
	
	public void expandAll(boolean expand) {
	    TreeNodeVector<E> root = getModel().getRoot();
	    if (root != null) {   
	        // Traverse tree from root
	        expandAll(new TreePath<E>(root), expand);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public LTreeModel<E> getModel() {
		return (LTreeModel<E>) super.getModel();
	}
	
	public void setModel(TreeModel newModel) {
		//No dejamos asignar un modelo que no sea un LTreeModel
		if (newModel == null || newModel instanceof LTreeModel) {
			super.setModel(newModel);
		}
	}
	
	private boolean expandAll(TreePath<E> parent, boolean expand) {
	    // Traverse children
	    TreeNodeVector<E> node = parent.getLastPathComponent();
	    if (node.getChildCount() > 0) {
	        boolean childExpandCalled = false;
	        for (Enumeration<TreeNodeVector<E>> e = node.children(); e.hasMoreElements(); ) {
	            TreeNodeVector<E> n = (TreeNodeVector<E>) e.nextElement();
	            TreePath<E> path = parent.pathByAddingChild(n);
	            childExpandCalled = expandAll(path, expand) || childExpandCalled; // the OR order is important here, don't let childExpand first. func calls will be optimized out !
	        }
	
	        if (!childExpandCalled) { // only if one of the children hasn't called already expand
	            // Expansion or collapse must be done bottom-up, BUT only for non-leaf nodes
	            if (expand) {
	                expandPath(parent);
	            } else {
	                collapsePath(parent);
	            }
	        }
	        return true;
	    } else {
	        return false;
	    }
	}

	public int getComportamiento() {
		return comportamiento;
	}
	
	public LTreeNode<E> getRootNode() {
		if (rootNode == null)
			rootNode = new LTreeNode<E>();
		return rootNode;
	}
	
	private LTreeNode<E> getUltimoNodoSeleccionado() {
		return ultimoNodoSeleccionado;
	}
	
	private void initialize() {
	
		setModel(new LTreeModel<E>(getRootNode()));
		setCellRenderer(new LCellRenderer<E>());
		setSelectionModel(new DefaultTreeSelectionModel());
		
		if (getRootNode().getChildCount() > 0) {
			setRootVisible(false);
	        setShowsRootHandles(true);
	        expandRoot();
		}
	}
	
	private boolean podemosSeleccionar(MouseEvent e) {
	
		//Podremos seleccionar si pinchamos en la celda
		//Si pinchamos fuera o en la zona de desplegar devolverá false
		TreePath<E> path = getClosestPathForLocation(e.getX(), e.getY());
	
		if (path != null) {
			
			Rectangle bounds = getPathBounds(path);
	
			if (e.getY() > (bounds.y + bounds.height)) {
				return false;
			}
	
			int x = e.getX();
	
			if (x > bounds.x) {
				if (x <= (bounds.x + bounds.width)) {
					return true;
				}
			}
		}
	
		return false;
	}
	
	public void posicionarEnNodo(LTreeNode<E> nodo, boolean seleccionarNodo) {
	
		try {
	
			Rectangle pathPadreBounds = null;
			if (nodo.getParent() != null) {
	
				LTreeNode<E> nodoPadre = (LTreeNode<E>) nodo.getParent();
				TreePath<E> treePathPadre = new TreePath<E>(nodoPadre.getPath());
				expandPath(treePathPadre);
				pathPadreBounds = getPathBounds(treePathPadre);
			}
	
			TreePath<E> treePath = new TreePath<E>(nodo.getPath());
			if (seleccionarNodo)
				setSelectionPath(treePath);
		
			JViewport viewport = null;
			Container parent = getParent();
			while (parent != null && !(parent instanceof JViewport))
				parent = parent.getParent();
			if (parent != null)
				viewport = (JViewport) parent;
			else
				return;
	
			viewport.validate();
			
			Rectangle pathBounds = getPathBounds(treePath);
	
			int x, y;
			if (pathPadreBounds != null) {
				x = pathPadreBounds.x;
				y = pathPadreBounds.y;
			}
			else {
				x = pathBounds.x;
				y = pathBounds.y;
			}
	
			int maximoY = viewport.getViewSize().height - viewport.getExtentSize().height;
			if (y > maximoY)
				y = maximoY;
			
			int maximoX = viewport.getViewSize().width - viewport.getExtentSize().width;
			if (x > maximoX)
				x = maximoX;
	
			Rectangle viewportBounds = new Rectangle(x, y, viewport.getExtentSize().width, viewport.getExtentSize().height);
			if (!viewportBounds.contains(pathBounds)) {
	
				y = pathBounds.y - viewportBounds.height + pathBounds.height;
			}
	
			if (y < 0)
				y = 0;
			if (x < 0)
				x = 0;
			
			viewport.setViewPosition(new Point(x, y));
			
			viewport.paintImmediately(this.getBounds());
			
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
	
		if (getComportamiento() == COMPORTAMIENTO_SELECCION_MULTIPLE_SIN_CTRL) {
			
			switch (e.getID()) {
				case MouseEvent.MOUSE_PRESSED :
					if (podemosSeleccionar(e))
						seleccionarElemento(e);
					else
						super.processMouseEvent(e);
					break;
				case MouseEvent.MOUSE_RELEASED :
					//Anulamos el mouseReleased si pinchamos en la celda
					if (!podemosSeleccionar(e))
						super.processMouseEvent(e);
					break;
				default :
					super.processMouseEvent(e);
					break;
			}
		}
		else {
	
			super.processMouseEvent(e);
		}
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
	
		if (getComportamiento() == COMPORTAMIENTO_SELECCION_MULTIPLE_SIN_CTRL) {
			
			switch (e.getID()) {
				case MouseEvent.MOUSE_DRAGGED :
					//Cuando hacemos drag se llama al setSelectionInterval(int, int) y no queremos que eso pase
					//mouseDragged(e);
					break;
				default :
					super.processMouseMotionEvent(e);
					break;
			}
		}
		else {
	
			super.processMouseMotionEvent(e);
		}
	}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	private void seleccionarElemento(MouseEvent e) {
	
		if (getComportamiento() == COMPORTAMIENTO_SELECCION_MULTIPLE_SIN_CTRL) {
		
			TreePath<E> path = getClosestPathForLocation(e.getX(), e.getY());
			if (path != null) {
	
				LTreeNode<E> nodo = null;
				if (path.getLastPathComponent() instanceof LTreeNode)
					nodo = (LTreeNode<E>) path.getLastPathComponent();
				
				if (e.isMetaDown()) {
	
					setUltimoNodoSeleccionado(nodo);
				
					LPopupMenu popup = new LPopupMenu();
					LMenuItem item = new LMenuItem("Seleccionar elemento e hijos");
					item.addActionListener(new ActionListener() {
					    public void actionPerformed(ActionEvent event) {
	
						    LTreeNode<E> node = getUltimoNodoSeleccionado();
							//if (node.getChildCount() > 0) {
								selectAllChilds(node);
								return;
							//}
					    }
					});
					popup.add(item);
										
					popup.show(this, e.getX() + 5, e.getY() + 5);
				}
				else {
					
					if (e.getClickCount() == 2 && nodo != null) {
	
						if (nodo.getChildCount() > 0) {
							selectAllChilds(nodo);
							return;
						}
					}
					
					if (getSelectionPaths() != null) {
						for (int i = 0; i < getSelectionPaths().length; i++) {
	
							if (getSelectionPaths()[i].getLastPathComponent() == path.getLastPathComponent()) {
	
								removeSelectionPath(path);
								return;
							}	
						}
					}
					addSelectionPath(path);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public TreePath<E> getClosestPathForLocation(int x, int y) {
		javax.swing.tree.TreePath treePath = super.getClosestPathForLocation(x, y);
		if (treePath instanceof TreePath)
			return (TreePath<E>) treePath;
		else
			return new TreePath<E>((TreeNodeVector<E>) treePath.getLastPathComponent());
    }
	
	public void selectionClear() {		
		getSelectionModel().clearSelection();	
	}
	
	public void selectNode(LTreeNode<E> nodo) {
		if (nodo != null) {
			TreePath<E> path = new TreePath<E>(nodo.getPath());
			selectPath(path);
		}
	}
	
	public void selectPath(TreePath<E> path) {
		if (path != null) {
			if (getSelectionPaths() != null) {
				for (int i = 0; i < getSelectionPaths().length; i++) {
	
					if (getSelectionPaths()[i].getLastPathComponent() == path.getLastPathComponent()) {
						//removeSelectionPath(path);
						return;
					}
				}
			}
			addSelectionPath(path);
		}
	}
	
	public void selectAll() {
		selectAllChilds(null);
	}
	public void selectAllChilds(LTreeNode<E> nodo) {
	
		selectionClear();
		collapseRoot();
		if (nodo == null)
			nodo = getRootNode();
	
		int numNodos = nodo.getChildCount() + 1;
		@SuppressWarnings("unchecked")
		TreePath<E>[] paths = new TreePath[numNodos];
	
		paths[0] = new TreePath<E>(nodo.getPath());
		TreePath<E>[] pathsHijos = getPathsChildren(nodo);
		for (int i = 0; i < pathsHijos.length; i++) {
			paths[i+1] = pathsHijos[i];
		}
		
		addSelectionPaths(paths);
	}
	
	public TreePath<E>[] getPathsChildren(LTreeNode<E> node) {
	
		int numNodos = node.getChildCount();
		@SuppressWarnings("unchecked")
		TreePath<E>[] paths = new TreePath[numNodos];
	
		if (node.getChildCount() > 0) {
	
			Vector<LTreeNode<E>> hijos = node.getChildrenCopyVector();
			for (int i = 0; i < hijos.size(); i++) {
				paths[i] = new TreePath<E>(hijos.get(i).getPath());
			}
		}
	
		return paths;
	}
	
	public void selectLevel(int levelToSelect) {
	
		selectionClear();
		collapseRoot();
		TreePath<E>[] pathsLevel = getPathsLevel(levelToSelect, getRootNode());
		addSelectionPaths(pathsLevel);
	}
	
	public TreePath<E>[] getPathsLevel(int treeLevel, LTreeNode<E> node) {
	
		Vector<TreePath<E>> paths = new Vector<TreePath<E>>();
		
		int nivelNodo = node.getLevel();
		if (nivelNodo == treeLevel)
			paths.addElement(new TreePath<E>(node.getPath()));
		else if (nivelNodo < treeLevel && node.getChildCount() > 0) {
			TreePath<E>[] childrenLevel;
			if (nivelNodo == treeLevel -1) {
				childrenLevel = getPathsChildren(node);
				for (int i = 0; i < childrenLevel.length; i++) {
					paths.addElement(childrenLevel[i]);
				}
			}
			else {
				Vector<LTreeNode<E>> hijos = node.getChildrenCopyVector();
				for (int i = 0; i < hijos.size(); i++) {
					childrenLevel = getPathsLevel(treeLevel, hijos.get(i));
					for (int j = 0; j < childrenLevel.length; j++) {
						paths.addElement(childrenLevel[j]);
					}
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		TreePath<E>[] pathsLevel = new TreePath[paths.size()];
		for (int i = 0; i < paths.size(); i++) {
			pathsLevel[i] = paths.elementAt(i);
		}
	
		return pathsLevel;
	}
	
	public void setComportamiento(int newComportamiento) {
		
		comportamiento = newComportamiento;
	
		if (newComportamiento == COMPORTAMIENTO_SELECCION_MULTIPLE_SIN_CTRL)
			getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		else
			getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	public void setRootNodeElement(E objetoRootNode) {
		getRootNode().setUserObject(objetoRootNode);
	}
	public void setRootNode(LTreeNode<E> newRootNode) {
		rootNode = newRootNode;
		if (newRootNode != null) {
			setModel(new LTreeModel<E>(newRootNode, false));
			//getModel().setRoot(newRootNode);
		}
	}
	
	private void setUltimoNodoSeleccionado(LTreeNode<E> newUltimoNodoSeleccionado) {
		ultimoNodoSeleccionado = newUltimoNodoSeleccionado;
	}
}
