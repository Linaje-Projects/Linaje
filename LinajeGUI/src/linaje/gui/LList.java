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
package linaje.gui;

/**
 * <b>Funcionalidad:</b><br>
 * Integrar las funciones más comunes que se realizan con una Lista:
 *
 *	  - Se inicia ya con un DefaultListModel
 *	  - Métodos de acceso a los elementos del DefaultListModel
 *	  - Seleccionar todos los elementos o ninguno
 *	  - Mover elemento arriba o abajo
 *	  - Posibilidad de selección múltiple sin presionar CTRL
 *    - Definir el número de columnas visibles a la vez
 *	  - ...
 * <p>
 * <b>Uso:</b><br>
 *
 * Crear una nueva instancia y agregar directamente elementos sin necesidad de asignar un ListModel.
 * Luego se pueden útilizar los métodos de acceso que se necesiten
 *
 * MODE_MULTIPLE_SELECTION_WITHOUT_CTRL
 *
 *	  Este mode es como una lista normal con selectionMode=MULTIPLE_INTERVAL_SELECTION
 *	  con la salvedad de que no requiere que se este presionando la tecla "control"
 *	  para que se seleccionen los elementos de la lista.
 *
 *	  NOTA: Este mode de lista no admite Drag&Drop
 * <p>
 * 
 * ActionsRenderer
 * 
 * Nos dará el indice de la acción por la que está pasando el ratón
 * 
 * 
 * @author: Pablo Linaje (07/01/2005 10:07:10)
 * @version 00.04
 */
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import linaje.gui.cells.DataCell;
import linaje.gui.cells.LabelCell;
import linaje.gui.renderers.ActionsRenderer;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.LFont;
import linaje.utils.Lists;
import linaje.utils.ReflectAccessSupport;
import sun.swing.SwingUtilities2;


@SuppressWarnings("serial")
public class LList<E> extends JList<E> {
	
	public static final int MODE_DEFAULT = 0;
	public static final int MODE_MULTIPLE_SELECTION_WITHOUT_CTRL = 1;
	public static final int MODE_SINGLE_SELECTION = 2;
	
	private int mode = MODE_DEFAULT;
	private int lastRowSelected = -1;
	private boolean lockedSelection = false;
	
	private int numberOfColumns = 1;
	private boolean scrollMultiColumnVertical = false;
	private JViewport currentViewport = null;
	
	private int indexOver = -1;
	private Point mousePositionRelativeToCell = null;
	
	private int actionMouseOver = -1;
	
	ComponentListener componentListener = new ComponentListener() {
		public void componentResized(ComponentEvent e) {
			adjustColumnsWidth();
		}
		public void componentShown(ComponentEvent e) {}
		public void componentMoved(ComponentEvent e) {}
		public void componentHidden(ComponentEvent e) {}
	};
	
	ListDataListener listDataListener = new ListDataListener() {
		
		public void intervalRemoved(ListDataEvent e) {
			adjustColumnsWidth();
		}
		public void intervalAdded(ListDataEvent e) {
			adjustColumnsWidth();
		}
		public void contentsChanged(ListDataEvent e) {
			adjustColumnsWidth();
		}
	};
		
	public LList() {
		this(MODE_SINGLE_SELECTION);
	}
	public LList(int mode) {
		super();
		setMode(mode);
		initialize();
	}
	
	public LList(JList<E> baseList) {
		
		super();
		initialize();
		if (baseList != null) {
			//Creamos la lista copiando las características de la lista base
			copyPropertiesFrom(baseList);
		}
	}
	
	public void copyPropertiesFrom(JList<E> baseList) {
		
		setFont(baseList.getFont());
		setForeground(baseList.getForeground());
		setBackground(baseList.getBackground());
		setOpaque(baseList.isOpaque());
		setAutoscrolls(baseList.getAutoscrolls());
		setCellRenderer(baseList.getCellRenderer());
		setEnabled(baseList.isEnabled());
		setFixedCellHeight(baseList.getFixedCellHeight());
		setFixedCellWidth(baseList.getFixedCellWidth());
		
		if (baseList instanceof LList) {
			
			LList<E> lList = (LList<E>) baseList;
			setMode(lList.getMode());
			setNumberOfColumns(lList.getNumberOfColumns());
			setScrollMultiColumnVertical(lList.isScrollMultiColumnVertical());
			setLockedSelection(lList.isLockedSelection());
		}
		
		//for (int i = 0; i < listaBase.getModel().getSize(); i++)
		//	getDefaultListModel().addElement(listaBase.getModel().getElementAt(i));
	}
	
	public void addElement(E element) {
	
		if (element != null && !getDefaultListModel().contains(element)) {
	
			if (element instanceof String && element.equals(Constants.VOID)) {
				return;
			}
			getDefaultListModel().addElement(element);
			setSelectedIndex(getDefaultListModel().size() - 1);
		}
	}
	
	/**
	 * @author: Pablo Linaje (07/01/2005 10:36:55)
	 */
	public void removeSelectedElement() {
	
		try {
	
			int index = getSelectedIndex();
			if (index > -1 && index < getDefaultListModel().size()) {
				
				getDefaultListModel().removeElementAt(index);
	
				if (getDefaultListModel().size() > 0) {
					
					if (index != getDefaultListModel().size())
						setSelectedIndex(index);
					else
						setSelectedIndex(getDefaultListModel().size() - 1);
				}
			}
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	/**
	 * @author: Pablo Linaje (05/04/2005 11:40:00) 
	 * @return int
	 */
	public int getMode() {
		return mode;
	}
	/**
	 * @author: Pablo Linaje (07/01/2005 10:14:32) 
	 * @return javax.swing.DefaultListModel
	 */
	public DefaultListModel<E> getDefaultListModel() {
	
		if (getModel() == null)// || !(getModel() instanceof DefaultListModel))
			setModel(new DefaultListModel<E>());
			
		return (DefaultListModel<E>) getModel();
	}
	
	public Vector<E> getElements() {
	
		Vector<E> elements = new Vector<E>();
		for (int i = 0; i < getDefaultListModel().size(); i++)
			elements.addElement(getDefaultListModel().elementAt(i));
		
		return elements;
	}
	
	public String getFontName() {
		return getFont().getName();
	}
	public int getFontSize() {
		return getFont().getSize();
	}
	public int getFontStyle() {
		return getFont().getStyle();
	}
	public int getFontLayout() {
		return getFont() instanceof LFont ? ((LFont) getFont()).getLayoutMode() : LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
	}
	
	public int getLastRowSelected() {
		return lastRowSelected;
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
	
		setModel(new DefaultListModel<E>());
		setFont(AppGUI.getFont());
		setForeground(ColorsGUI.getColorText());
		
		int renderType = getMode() == MODE_MULTIPLE_SELECTION_WITHOUT_CTRL ? LabelCell.TYPE_CHECKBOX : LabelCell.TYPE_LABEL;
		LCellRenderer<E> render = new LCellRenderer<E>(renderType);
		setCellRenderer(render);
		
		adjustColumnsWidth();
	}
	
	public void insertElementAt(E element, int index) {
	
		if (element != null && !getDefaultListModel().contains(element)) {
	
			if (element instanceof String && element.equals(Constants.VOID)) {
				return;
			}
			
			getDefaultListModel().add(index, element);
			
			setSelectedIndex(index);
		}
	}
	
	public boolean isLockedSelection() {
		return lockedSelection;
	}
	
	public void mousePressed(MouseEvent e) {
	
		if (getMode() == MODE_MULTIPLE_SELECTION_WITHOUT_CTRL) {
		
			int row = locationToIndex(new Point(e.getX(), e.getY()));
			if (isSelectedIndex(row)) {
				removeSelectionInterval(row, row);
			}
			else {
				setLastRowSelected(row);
				addSelectionInterval(row, row);
			}
		}
	}
	
	public void moveElementDown() {
	
		try {
			
			int index = getSelectedIndex();
			if (index > -1 && index < getDefaultListModel().size() - 1) {
				
				E element = getSelectedValue();
				getDefaultListModel().removeElementAt(index);
				getDefaultListModel().insertElementAt(element, index + 1);
				setSelectedIndex(index + 1);
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public void moveElementUp() {
	
		try {
	
			int index = getSelectedIndex();
			if (index > 0) {
				
				E element = getSelectedValue();
				getDefaultListModel().removeElementAt(index);
				getDefaultListModel().insertElementAt(element, index - 1);
				setSelectedIndex(index - 1);
			}
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
	
		if (e.getID() == MouseEvent.MOUSE_EXITED) {
			
			analyzeMousePosition(null);
		}
		else if (isEnabled()) {
			
			analyzeMousePosition(e);
			
			if ( e.getID() == MouseEvent.MOUSE_PRESSED) {
				//Comprobamos si el elemento está deshabilitado
				int index = getIndexOver();
				Vector<E> elements = getElements();
				if (index >= 0 && index < elements.size()) {
					boolean isEnabled = true;
					E element = elements.elementAt(index);
					if (element != null) {
						if (element instanceof DataCell) {
							isEnabled = ((DataCell) element).isEnabled();
						}
						else if (element instanceof Component) {
							isEnabled = ((Component) element).isEnabled();
						}
						else {
							try {
								ReflectAccessSupport ras = new ReflectAccessSupport(element);
								Method isEnabledMethod = ras.findMethod("isEnabled", (Class<?>) null);
								if (isEnabledMethod != null) {
									Object valueIsEnabled = ras.invokeMethod(isEnabledMethod, (Class<?>) null);
									isEnabled = valueIsEnabled == null || !(valueIsEnabled instanceof Boolean) || ((Boolean) valueIsEnabled).booleanValue();
								}
							}
							catch (Exception e2) {}
						}
						if (!isEnabled)
							return;
					}
				}
			}
		}
		/*
		 * MODE_MULTIPLE_SELECTION_WITHOUT_CTRL: El evento lo ponemos siempre con ctrl pulsado
		 * MODE_SINGLE_SELECTION: El evento lo ponemos siempre con ctrl sin pulsar
		 */
		int modifiers = getMode() == MODE_MULTIPLE_SELECTION_WITHOUT_CTRL ? InputEvent.CTRL_DOWN_MASK : getMode() == MODE_SINGLE_SELECTION ? 0 : -1;
		
		MouseEvent mouseEvent;
		if (modifiers == -1) {
			mouseEvent = e;
		}
		else {	
			mouseEvent = new MouseEvent(
			(Component) e.getSource(), 
			e.getID(), 
			e.getWhen(), 
			modifiers,
			e.getX(), 
			e.getY(), 
			e.getXOnScreen(), 
			e.getYOnScreen(), 
			e.getClickCount(), 
			e.isPopupTrigger(), 
			e.getButton());
		}
		
		super.processMouseEvent(mouseEvent);
	}
	
	protected void processMouseMotionEvent(MouseEvent e) {
	
		if (e.getID() == MouseEvent.MOUSE_MOVED) {
			
			analyzeMousePosition(e);
			if (getCellRenderer() != null && getCellRenderer() instanceof ActionsRenderer)
				setCursor(new Cursor(getActionMouseOver() != -1 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
		}
		
		if (getMode() == MODE_MULTIPLE_SELECTION_WITHOUT_CTRL) {
			
			int modifiers = InputEvent.CTRL_DOWN_MASK;// | e.getModifiers();
	
			MouseEvent eventoCtrl = new MouseEvent(
			(Component) e.getSource(), 
			e.getID(), 
			e.getWhen(), 
			modifiers,
			e.getX(), 
			e.getY(), 
			e.getXOnScreen(), 
			e.getYOnScreen(), 
			e.getClickCount(), 
			e.isPopupTrigger(), 
			e.getButton());
			
			super.processMouseMotionEvent(eventoCtrl);
		}
		else {
	
			super.processMouseMotionEvent(e);
		}
	}
	
	protected void analyzeMousePosition(MouseEvent e) {
	
		int index = -1;
		int indexAction = -1;
		
		if (e != null) {
			
			Point cursorPosition = e.getPoint();
			index = SwingUtilities2.loc2IndexFileList(this, cursorPosition);
			
			if (index > -1) {
				
				Rectangle cellRect = getCellBounds(index, index);
				Point mousePositionRelativeToCell = new Point(cursorPosition.x - cellRect.x, cursorPosition.y - cellRect.y);
				setMousePositionRelativeToCell(mousePositionRelativeToCell);
				
				// Repintamos la celda por la que pasamos el ratón
				RepaintManager.currentManager(this).addDirtyRegion(this, cellRect.x, cellRect.y, cellRect.width, cellRect.height);
				
				if (getCellRenderer() != null && getCellRenderer() instanceof ActionsRenderer) {
		
					E valor = getElements().elementAt(index);
					ActionsRenderer render = (ActionsRenderer) getCellRenderer().getListCellRendererComponent(this, valor, index, false, false);
					List<Rectangle> actionsRects = render.getActionsRects();
					
					if (actionsRects != null && !actionsRects.isEmpty()) {
						
						for (int i = 0; i < actionsRects.size() && indexAction == -1; i++) {
							
							Rectangle rectsAction = actionsRects.get(i);
							if (rectsAction.contains(mousePositionRelativeToCell))
								indexAction = i;
						}
					}
				}
			}
		}
		
		setIndexOver(index);
		setActionMouseOver(indexAction);
	}
	
	public void selectNone() {
		getSelectionModel().clearSelection();	
	}
	public void selectAll() {
		getSelectionModel().setSelectionInterval(0, getModel().getSize() - 1);	
	}
	
	public void setMode(int mode) {
		
		this.mode = mode;
	
		if (mode == MODE_MULTIPLE_SELECTION_WITHOUT_CTRL)
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		else
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void setElements(List<E> elements) {
		
		getDefaultListModel().removeListDataListener(listDataListener);
		
		try {
			getDefaultListModel().removeAllElements();
			for (int i = 0; i < elements.size(); i++)
				getDefaultListModel().addElement(elements.get(i));
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		
		getDefaultListModel().addListDataListener(listDataListener);
		
		adjustColumnsWidth();
	}
	
	public int[] getSelectedIndices(List<E> elementsToSearch) {
		
		int[] selectedIndices = null;
		
		if (elementsToSearch != null) {
			int numElements = elementsToSearch.size();
			selectedIndices = new int[numElements];
			Vector<E> listElements = getElements();
			for (int i = 0; i < numElements; i++) {
				int selectedIndex = listElements.indexOf(elementsToSearch.get(i));
				selectedIndices[i] = selectedIndex;
			}
		}
		return selectedIndices;
	}
	
	public void setFontName(String fontName) {
		UtilsGUI.setFontName(this, fontName);
	}
	public void setFontSize(int fontSize) {
		UtilsGUI.setFontSize(this, fontSize);
	}
	public void setFontStyle(int fontStyle) {
		UtilsGUI.setFontStyle(this, fontStyle);
	}
	public void setFontLayout(int fontLayout) {
		UtilsGUI.setFontLayout(this, fontLayout);
	}
	
	/**
	 * Select the specified interval.  Both the anchor and lead indices are
	 * included.  It's not neccessary for anchor to be less than lead.
	 * This is a convenience method that just delegates to the selectionModel.
	 *
	 * @param anchor The first index to select
	 * @param lead The last index to select
	 * @see ListSelectionModel#setSelectionInterval
	 * @see #addSelectionInterval
	 * @see #removeSelectionInterval
	 * @see #addListSelectionListener
	 */
	public void setSelectionInterval(int anchor, int lead) {

		setLockedSelection(false);
		if (getSelectionModel().getAnchorSelectionIndex() != anchor && getSelectionModel().getLeadSelectionIndex() != lead) {

			Point oldValue = new Point(getSelectionModel().getAnchorSelectionIndex(), getSelectionModel().getLeadSelectionIndex());
			Point newValue = new Point(anchor, lead);
			
			firePropertyChange("selectionInterval", oldValue, newValue);
		}
		if (!isLockedSelection())
			super.setSelectionInterval(anchor, lead);
	}
	
	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
		
		removeComponentListener(componentListener);
		if (getCurrentViewport() != null)
			getCurrentViewport().removeComponentListener(componentListener);
		
		int layoutOrientation = JList.VERTICAL;
		if (numberOfColumns > 1) {
			layoutOrientation = JList.VERTICAL_WRAP;
			addComponentListener(componentListener);
			if (getCurrentViewport() != null)
				getCurrentViewport().addComponentListener(componentListener);
		}
		setLayoutOrientation(layoutOrientation);
		
		adjustColumnsWidth();	
	}
	
	private void adjustColumnsWidth() {
		
		if (getNumberOfColumns() <= 1) {
			setVisibleRowCount(-1);
			setFixedCellWidth(-1);
		}
		else {
			if (isScrollMultiColumnVertical()) {
				//Número de filas visibles
				int visibleRowCount = 0;
				int rows = getElements().size();
				if (rows > 0) {
					int rowsColumn = rows / getNumberOfColumns();
					if (rows % getNumberOfColumns() > 0)
						rowsColumn++;
					visibleRowCount = rowsColumn;		
				}
				if (visibleRowCount != getVisibleRowCount())
					setVisibleRowCount(visibleRowCount);
			}
			//Anchos de columnas
			JViewport viewPort = getParent() instanceof JViewport ? (JViewport) getParent() : null;
			int widthList = viewPort != null ? viewPort.getViewRect().width : getWidth();
			
			int fixedCellWidth = getNumberOfColumns() > 1 ? widthList / getNumberOfColumns() : -1;
			if (fixedCellWidth != getFixedCellWidth())
				setFixedCellWidth(fixedCellWidth);
			
			if (isScrollMultiColumnVertical()) {
				//Lo dejamos automático en caso de que las filas/columnas ocupen menos del alto actual de la lista
				if (getPreferredSize().height < getHeight())
					setVisibleRowCount(-1);
			}
		}
		
		if (getCurrentViewport() != null) {
			getCurrentViewport().revalidate();
			getCurrentViewport().repaint();
		}
		else {
			revalidate();
			repaint();
		}
	}
	
	public void setModel(ListModel<E> model) {
	
		ListModel<E> oldModel = getModel();
		
		super.setModel(model);
		
		if (model != oldModel) {
			
			if (oldModel != null)
				oldModel.removeListDataListener(listDataListener);
			
			if (model != null)
				model.addListDataListener(listDataListener);
		}
	}
	public boolean isScrollMultiColumnVertical() {
		return scrollMultiColumnVertical;
	}
	public void setScrollMultiColumnVertical(boolean scrollMultiColumnVertical) {
		this.scrollMultiColumnVertical = scrollMultiColumnVertical;
	}
	
	@Override
	public void addNotify() {
		
		JViewport viewport = getParent() instanceof JViewport ? (JViewport) getParent() : null;
		setCurrentViewport(viewport);
		super.addNotify();
	}
	
	@Override
	public void removeNotify() {
		
		setCurrentViewport(null);	
		super.removeNotify();
	}
	
	public JViewport getCurrentViewport() {
		return currentViewport;
	}
	private void setCurrentViewport(JViewport currentViewport) {
		JViewport oldViewPort = getCurrentViewport();
		this.currentViewport = currentViewport;
		
		if (oldViewPort != null)
			oldViewPort.removeComponentListener(componentListener);
		
		if (getNumberOfColumns() > 1 && currentViewport != null)
			currentViewport.addComponentListener(componentListener);
		
		adjustColumnsWidth();
	}
	
	public int getIndexOver() {
		return indexOver;
	}
	private void setIndexOver(int newIndexOver) {
		if (indexOver != newIndexOver) {
			
			if (indexOver != -1) {
				//Repintamos la fila que estamos dejando de pasar el ratón por encima
				Rectangle cellRect = getCellBounds(indexOver, indexOver);
				if (cellRect != null)
					RepaintManager.currentManager(this).addDirtyRegion(this, cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			}
			indexOver = newIndexOver;
		}
	}
	
	public E[] getSelectedValues() {
        return Lists.listToArray(getSelectedValuesList());
    }
	
	public void setLockedSelection(boolean lockedSelection) {
		this.lockedSelection = lockedSelection;
	}
	public void setLastRowSelected(int lastRowSelected) {
		this.lastRowSelected = lastRowSelected;
	}
	public int getNumberOfColumns() {
		return numberOfColumns;
	}
	
	public Point getMousePositionRelativeToCell() {
		return mousePositionRelativeToCell;
	}
	private void setMousePositionRelativeToCell(Point mousePositionRelativeToCell) {
		this.mousePositionRelativeToCell = mousePositionRelativeToCell;
	}
	private void setActionMouseOver(int actionMouseOver) {
		this.actionMouseOver = actionMouseOver;	
	}
	public int getActionMouseOver() {
		return actionMouseOver;
	}
}
