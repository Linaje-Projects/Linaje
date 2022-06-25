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

import java.util.List;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import linaje.gui.RoundedBorder;
import linaje.gui.renderers.ActionsRenderer;
import linaje.gui.renderers.CellRendererExpandable;
import linaje.gui.ui.GeneralUIProperties;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;
import linaje.utils.Strings;

public class LTable<E> extends JTable {

	private static final long serialVersionUID = 1L;
	
	public final int TIME_BETWEEN_KEYSTROKES = 1000;

	private Timer timer = null;
	private String keyStrokes;
	private boolean onlyLetters;
	private List<Insets> spans = null;
	private int actionMouseOver = -1;
	private int rowMouseOver = -1;
	private int columnMouseOver = -1;
	private boolean actionPressed = false;
	
	private Border bordeEdicion = new RoundedBorder(false, GeneralUIProperties.getInstance().getColorBorderDark(), 2);//BorderFactory.createLineBorder(GeneralUIProperties.getInstance().getColorBorderDark(), 2);
	
	protected EventListenerList listenerList = new EventListenerList();
	
	class TimerActionSearch implements ActionListener {
		
		public void actionPerformed(ActionEvent x) {

			if (keyStrokes != null) {
				String text = keyStrokes;
				timer.stop();
				timer = null;
				keyStrokes = null;

				int fila = getSelectedRow();
				int columna = getSelectedColumn();
				while (!positionInCell(fila, columna, text, onlyLetters) && text.length() > 1) {
					text = text.substring(0, text.length() - 1);
				}
			}
		}
	}
	private TableCellRenderer renderDefault = null;
	protected TableCellRenderer headerRenderDefault = null;

	public LTable() {
		this(new LTableModel<E>());
	}
	public LTable(E[][] rowData, String[] columnNames) {
		this(new LTableModel<E>(rowData, columnNames));
	}
	public LTable(int numRows, int numColumns) {
		this(new LTableModel<E>(numRows, numColumns));
	}
	public LTable(Vector<? extends TreeNodeVector<E>> rowData, Vector<String> columnNames) {
		this(new LTableModel<E>(rowData, columnNames));
	}
	public LTable(LTableModel<E> dm) {
		super(dm);
		initialize();
	}
	public LTable(LTableModel<E> dm, TableColumnModel cm) {
		super(dm, cm);
		initialize();
	}
	public LTable(LTableModel<E> dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		initialize();
	}
	
	@Override
	protected LTableModel<E> createDefaultDataModel() {
		
		LTableModel<E> lTableModel = new LTableModel<E>();
		lTableModel.setTable(this);
		
        return lTableModel;
    }
	
	@Override
	protected JTableHeader createDefaultTableHeader() {
        return new LTableHeader(this);
    }
	
	@Override
	public void createDefaultColumnsFromModel() {
		LTableModel<E> m = getModel();
        if (m != null) {
            // Remove any current columns
            removeAllColumns();

            // Create new columns from the data model info
            for (int i = 0; i < m.getColumnCount(); i++) {
                LColumn newColumn = new LColumn(i);
                addColumn(newColumn);
            }
        }
    }
	
	public void addColumn(LColumn column) {
	
		if (column.getCellRenderer() == null || column.getCellRenderer().getClass() == DefaultTableCellRenderer.class) {
			column.setCellRenderer(getRenderDefault());
			//column.setHeaderRenderer(getHeaderRenderDefault());
		}
	
		super.addColumn(column);
	}
	
	/*private TableCellRenderer getHeaderRenderDefault() {
		if (headerRenderDefault == null)
			headerRenderDefault = createHeaderRenderDefault();
		return headerRenderDefault;
	}
	protected TableCellRenderer createHeaderRenderDefault() {
		return new LHeaderRenderer();
	}*/
	
	
	
	public List<LColumn> getColumns() {
		List<LColumn> columns = Lists.newList();
		TableColumnModel cm = getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			columns.add((LColumn) cm.getColumn(i));
		}
		return columns;
	}
	
	/**
	 * Remove any current columns
	 */
	public void removeAllColumns() {
		TableColumnModel cm = getColumnModel();
        while (cm.getColumnCount() > 0) {
            cm.removeColumn(cm.getColumn(0));
        }
	}
	
	private TableCellRenderer getRenderDefault() {
		if (renderDefault == null)
			renderDefault = createRenderDefault();
		return renderDefault;
	}
	
	protected TableCellRenderer createRenderDefault() {
		return new CellRendererExpandable<E>();
	}
		
	@SuppressWarnings("unchecked")
	public LTableModel<E> getModel() {
		if (super.getModel() == null) {
			setModel(new LTableModel<E>());
			getModel().setTable(this);
		}
		return (LTableModel<E>) super.getModel();
	}
	
	/**
     * Necesario para compatibilidad con javax.swing.table.JTable. NO USAR
     */
	@Override
	public void setModel(TableModel newModel) {
		if (newModel == null || (newModel instanceof LTableModel && newModel != getModel()))
			super.setModel(newModel);
	}
	
	@SuppressWarnings("unchecked")
	public void setModel(LTableModel<E> newModel) {
		TableModel oldModel = super.getModel();
		if (oldModel != null && oldModel instanceof LTableModel) {
			((LTableModel<E>) oldModel).setTable(null);
		}
		
		super.setModel(newModel);
		if (newModel != null)
			newModel.setTable(this);
	}
	
	public LTableHeader getTableHeader() {
		/*if (super.getTableHeader() == null) {
			setTableHeader(new LTableHeader(this));
		}*/
        return (LTableHeader) super.getTableHeader();
    }
	
	/**
     * Necesario para compatibilidad con javax.swing.table.JTable. NO USAR
     */
	@Override
	public void setTableHeader(JTableHeader newTableHeader) {
		if (newTableHeader == null || (newTableHeader instanceof LTableHeader && newTableHeader != getTableHeader()))
			super.setTableHeader(newTableHeader);
	}
	
	public void setTableHeader(LTableHeader lTableHeader) {
		super.setTableHeader(lTableHeader);
	}
	
	private void initialize() {
		
		if (getModel() != null)
			getModel().setTable(this);
		
		this.addLTableListener(new LTableListener<E>() {

			public void cellClicked(LTableEvent<E> event) {
				
				MouseEvent mouseEvent = event.getMouseEvent();
				if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
					int row = event.getRowIndex();//getRowMouseOver();
					int column = event.getColumnIndex();//getColumnMouseOver();
					if (row != -1 && column == 0 && getColumn(0).getCellRenderer() instanceof CellRendererExpandable) {
						
						int indexAction = getActionMouseOver();
						TreeNodeVector<E> rowClicked = getModel().getRows().elementAt(row);
						if (indexAction == 0) {
							if (rowClicked.isExpanded())
								getModel().collapse(rowClicked);
							else
								getModel().expand(rowClicked);
						}
					}
				}
			}
			public void columnClicked(LTableEvent<E> event) {}
			public void selectionChanged() {}
		});
		
		//setTableHeader(new LTableHeader(this));
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
	/**
	 * Posiciona el cursor y el viewport en la fila/columna seleccionada
	 */
	public void positionInCell(int fila, int columna) {
		
		try {
			
			JViewport viewport = null;
			Container parent = getParent();
			while (parent != null && !(parent instanceof JViewport))
				parent = parent.getParent();
			if (parent != null)
				viewport = (JViewport) parent;
			else
				return;
	
			int x = viewport.getViewPosition().x;
			int y = fila * getRowHeight();
			
			int maximo = viewport.getViewSize().height - viewport.getExtentSize().height;
			if (y > maximo)
				y = maximo;
	
			if (y > 0) {
				viewport.setViewPosition(new Point(x, y));
			}
			if (fila != -1)
				setRowSelectionInterval(fila, fila);
			if (columna != -1)
				setColumnSelectionInterval(columna, columna);
			
			viewport.paintImmediately(this.getBounds());
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	/**
	 * Posiciona el cursor y el viewport en la SIGUIENTE fila a la seleccionada
	 * que empieze por el texto indicado en la columna y devuelve si la ha encontrado
	 * 
	 */
	protected synchronized boolean positionInCell(int rowIndex, int columnIndex, String text) {
		return positionInCell(rowIndex, columnIndex, text, false);
	}
	/**
	 * Posiciona el cursor y el viewport en la SIGUIENTE fila a la seleccionada
	 * que empieze por el texto indicado en la columna y devuelve si la ha encontrado
	 * 
	 */
	protected synchronized boolean positionInCell(int rowIndex, int columnIndex, String text, boolean onlyLetters) {
		
		boolean rowFound = false;
		if (columnIndex != -1 && rowIndex != -1 && rowIndex < getRowCount()) {
			
			text = Strings.replace(text, Constants.SPACE, Constants.VOID);
			TableColumn column = getColumnModel().getColumn(columnIndex);
			int modelIndex = column.getModelIndex();
			
			TreeNodeVector<E> row;
			String value;
			
			for (int i = rowIndex + 1; i < getRowCount(); i++) {
				
				row = getModel().getRows().elementAt(i);
				value = row.elementAt(modelIndex).toString().trim();
				value = Strings.replace(text, Constants.SPACE, Constants.VOID);
				if (onlyLetters && value != null && value.length() > 1) {
					//Nos disponemos a ignorar los numeros en la tabla
					if (!Strings.containsNumbers(text)) { //Si lo que escribimos tiene numeros entonces no ignoramos los numeros
						//Ignoramos los numeros del texto de la celda
						value = Strings.removeNumbers(value);
					}
				}
				//Siempre ignoraremos lo que no sean numeros o letras
				if (!Strings.containsOtherCharacters(text)) { //Si lo que escribimos tiene caracteres extraños entonces no ignoramos los caracteres extraños
					//Ignoramos lo que no sean numeros o letras
					value = Strings.removeOtherCharacters(value);
				}
				if (value.toUpperCase().startsWith(text.toUpperCase())) {
					
					positionInCell(i, columnIndex);
					rowFound = true;
					break;
				}
			}
			if (!rowFound && rowIndex != 0) {
				
				//Si no encontramos la fila por abajo volvemos a buscar desde el principio
				for (int i = 0; i < rowIndex; i++) {
					
					row = getModel().getRows().elementAt(i);
					value = row.elementAt(modelIndex).toString().trim();
					value = Strings.replace(text, Constants.SPACE, Constants.VOID);
					if (onlyLetters && value != null && value.length() > 1) {
						//Nos disponemos a ignorar los numeros en la tabla
						if (!Strings.containsNumbers(text)) { //Si lo que escribimos tiene numeros entonces no ignoramos los numeros
							//Ignoramos los numeros del texto de la celda
							value = Strings.removeNumbers(value);
						}
					}
					//Siempre ignoraremos lo que no sean numeros o letras
					if (!Strings.containsOtherCharacters(text)) { //Si lo que escribimos tiene caracteres extraños entonces no ignoramos los caracteres extraños
						//Ignoramos lo que no sean numeros o letras
						value = Strings.removeOtherCharacters(value);
					}
					if (value.toUpperCase().startsWith(text.toUpperCase())) {
						
						positionInCell(i, columnIndex);
						rowFound = true;
						break;
					}
				}
			}
		}
		return rowFound;
	}
	public void moveRow(boolean moveUp, ListSelectionListener listener) {
		
		int from = getSelectedRow();
		int to = moveUp ? from-1 : from+1;
		
		if (to >= 0 && to < getRowCount()) {
	    	
			getModel().moveRows(from, from, to);
			if (listener != null) {
				getSelectionModel().removeListSelectionListener(this);
				getSelectionModel().setSelectionInterval(to, to);
				getSelectionModel().addListSelectionListener(this);
			}
	    	positionInCell(to, -1);
		}
	}
		
	public void setSpans(List<Insets> spans) {
		this.spans = spans;
	}
	public List<Insets> getSpans() {
		if (spans == null) {
			spans = Lists.newList();
		}
		return spans;
	}
	
	@Override
	public Component add(Component comp) {
		if (comp != null && comp == editorComp && comp instanceof JComponent) {
			((JComponent) comp).setBorder(bordeEdicion);
		}
		return super.add(comp);
	}
	
	@Override
	public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
		
		Rectangle cellRect = super.getCellRect(row, column, includeSpacing);
		
		if (isEditing() && getEditingRow() == row && getEditingColumn() == column) {
			
			Insets insets = bordeEdicion.getBorderInsets(getEditorComponent());
			//cellRect.x = cellRect.x + insets.left;
			cellRect.y = cellRect.y - insets.top;
			cellRect.width = cellRect.width + insets.left + insets.right;
			cellRect.height = cellRect.height + insets.top + insets.bottom;
		}
		
		return cellRect;
	}
	
	@SuppressWarnings("unchecked")
	public LTableListener<E>[] getTableListeners() {
        return listenerList.getListeners(LTableListener.class);
    }
	
	public void addLTableListener(LTableListener<E> l) {
        listenerList.add(LTableListener.class, l);
    }
	
	 public void removeLTableListener(LTableListener<E> l) {
        listenerList.remove(LTableListener.class, l);
    }
	
	public LColumn getColumn(int columnIndex) {
		try {
			return (LColumn) getColumnModel().getColumn(columnIndex);
		}
		catch (Exception e) {
			return null;
		}
    }
	
	public TreeNodeVector<E> getRow(int rowIndex) {
		try {
			return getModel().getRows().elementAt(rowIndex);
		}
		catch (Exception e) {
			return null;
		}
    }
	
	protected void fireCellClicked(MouseEvent mouseEvent) {
		
		LTableListener<E>[] tableListeners = getTableListeners();
    	if (tableListeners.length > 0) {
    		int columnIndex = getColumnMouseOver();
    		int rowIndex = getRowMouseOver();
    		
    		LTableEvent<E> mtEvent = new LTableEvent<E>(this, columnIndex, rowIndex, mouseEvent);
    		for (int i = 0; i < tableListeners.length; i++) {
    			tableListeners[i].cellClicked(mtEvent);
			}
    	}
    }
	
	protected void fireColumnClicked(MouseEvent mouseEvent, int columnIndex) {
		
		LTableListener<E>[] tableListeners = getTableListeners();
    	if (tableListeners.length > 0) {
    		LTableEvent<E> mtEvent = new LTableEvent<E>(this, columnIndex, -1, mouseEvent);
    		for (int i = 0; i < tableListeners.length; i++) {
    			tableListeners[i].columnClicked(mtEvent);
			}
    	}
    }
	
	protected void fireSelectionChanged() {
		LTableListener<E>[] tableListeners = getTableListeners();
    	if (tableListeners.length > 0) {
    		for (int i = 0; i < tableListeners.length; i++) {
    			tableListeners[i].selectionChanged();
			}
    	}
    }
	
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		if (!e.getValueIsAdjusting()) {
			fireSelectionChanged();
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
	
		setActionPressed(false);
			
		if (e.getSource() == this) {
			
			int id = e.getID();
			switch (id) {
			case MouseEvent.MOUSE_PRESSED:
				analizeMousePosition(e);
				setActionPressed(getActionMouseOver() != -1);//getActionMouseOver() es -1 cuando no pinchamos en ningun enlace
				fireCellClicked(e);
				break;
			case MouseEvent.MOUSE_RELEASED:
				//mouseReleased(e);
				break;
			case MouseEvent.MOUSE_CLICKED:
				break;
			case MouseEvent.MOUSE_EXITED:
				analizeMousePosition(null);
				break;
			case MouseEvent.MOUSE_ENTERED:
				break;
			}
		}
		
		if (!isActionPressed()) //Cuando pulsamos en una acción no propagamos mas el evento
			super.processMouseEvent(e);
	}
	
	protected void processMouseMotionEvent(MouseEvent e) {
	
		if (e.getSource() == this) {
			int id = e.getID();
			switch (id) {
			case MouseEvent.MOUSE_MOVED:
				super.processMouseMotionEvent(e);
				mouseMoved(e);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				//Cuando desplegamos a través de un Tooltip se propaga un mouseDragged que selecciona varias filas
				//Lo evitamos no propagando  el mouseDragged al pasar por una acción
				if (getActionMouseOver() == -1)
					super.processMouseMotionEvent(e);
				break;
			}
		}
	}
	
	public void mouseMoved(MouseEvent e) {
	
		analizeMousePosition(e);
		if (isRenderCompatibleActions(getColumnMouseOver()))
			setCursor(new Cursor(getActionMouseOver() != -1 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
	}
	
	public void processKeyEvent(KeyEvent e) {
		
		super.processKeyEvent(e);
		
		if (e.getID() == KeyEvent.KEY_TYPED) {
			
			if (e.isShiftDown())
				onlyLetters = true;
			else
				onlyLetters = false;
			
			String letter = new Character(e.getKeyChar()).toString();
			try {
				if (timer == null) {
					timer = new Timer(TIME_BETWEEN_KEYSTROKES / 2, new TimerActionSearch());
					timer.start();
				} else {
					timer.stop();
					timer = new Timer(TIME_BETWEEN_KEYSTROKES, new TimerActionSearch());
					timer.start();
				}
				if (keyStrokes == null)
					keyStrokes = letter;
				else
					keyStrokes = keyStrokes + letter;
					
			} catch (Throwable ex) {
				keyStrokes = letter;
			}
		}
	}
	
	protected void analizeMousePosition(MouseEvent e) {
	
		int rowIndex = -1;
		int columnIndex = -1;
		int actionIndex = -1;
		
		if (!isEditing() && e != null && e.getSource() == this) {
	
			Point mousePosition = e.getPoint();
			rowIndex = rowAtPoint(mousePosition);
			columnIndex = columnAtPoint(mousePosition);
			
			if (rowIndex > -1 && columnIndex > -1 && isRenderCompatibleActions(columnIndex)) {
	
				Rectangle cellRect = getCellRect(rowIndex, columnIndex, true);
				Point cellRelativePosition = new Point(mousePosition.x - cellRect.x, mousePosition.y - cellRect.y);
	
				E value = getModel().getValueAt(rowIndex, columnIndex);
				ActionsRenderer render = (ActionsRenderer) getCellRenderer(rowIndex, columnIndex).getTableCellRendererComponent(this, value, false, false, rowIndex, columnIndex);
				List<Rectangle> actionRects = render.getActionsRects();
				
				if (actionRects != null && !actionRects.isEmpty()) {
					
					for (int i = 0; i < actionRects.size() && actionIndex == -1; i++) {
						
						Rectangle boundsAction = actionRects.get(i);
						if (boundsAction != null && boundsAction.contains(cellRelativePosition))
							actionIndex = i;
					}
					// Repintamos la celda por la que pasamos el ratón
					RepaintManager.currentManager(this).addDirtyRegion(this, cellRect.x, cellRect.y, cellRect.width, cellRect.height);
				}
			}
		}
		
		setRowMouseOver(rowIndex);
		setColumnMouseOver(columnIndex);
		setActionMouseOver(actionIndex);
	}
	
	private boolean isRenderCompatibleActions(int columnIndex) {
		
		boolean isRenderCompatible = false;
		if (columnIndex != -1) {
			TableColumn column = getColumnModel().getColumn(columnIndex);
			TableCellRenderer render = column != null ? column.getCellRenderer() : null;
			isRenderCompatible = render != null && render instanceof ActionsRenderer;
		}
		
		return isRenderCompatible;
	}
	
	public void sort(int modelIndex, boolean useNaturalOrder) {
		getModel().sortTree(useNaturalOrder, modelIndex);
	}
	
	public void restoreOriginalSort() {
		getModel().restoreOriginalTreeSort();
	}
	
	private void setActionMouseOver(int actionMouseOver) {
		this.actionMouseOver = actionMouseOver;	
	}
	private void setRowMouseOver(int newRowMouseOver) {
		
		if (this.rowMouseOver != newRowMouseOver) {
			
			if (rowMouseOver != -1 && isRenderCompatibleActions(getColumnMouseOver())) {
				//Repintamos la fila que estamos dejando de pasar el ratón por encima
				Rectangle cellRect = getCellRect(rowMouseOver, 0, true);
				cellRect.width = getWidth();
				RepaintManager.currentManager(this).addDirtyRegion(this, cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			}
			this.rowMouseOver = newRowMouseOver;
		}
	}
	private void setColumnMouseOver(int columnMouseOver) {
		this.columnMouseOver = columnMouseOver;
	}
	private void setActionPressed(boolean actionPressed) {
		this.actionPressed = actionPressed;
	}
	public int getActionMouseOver() {
		return actionMouseOver;
	}
	public int getRowMouseOver() {
		return rowMouseOver;
	}
	public int getColumnMouseOver() {
		return columnMouseOver;
	}
	public boolean isActionPressed() {
		return actionPressed;
	}
	
	public boolean isEnabledRow(int rowIndex) {
		return isEnabledRowAtColumn(rowIndex, 0);
	}
	public boolean isEnabledRow(TreeNodeVector<E> rowNode) {
		return isEnabledRowAtColumn(rowNode, 0);
	}
	public boolean isEnabledRowAtColumn(int rowIndex, int columnIndex) {
		TreeNodeVector<E> rowNode = rowIndex > 0 && rowIndex < getRowCount() ? getModel().getRows().elementAt(rowIndex) : null;
		return isEnabledRowAtColumn(rowNode, rowIndex, columnIndex);
	}
	public boolean isEnabledRowAtColumn(TreeNodeVector<E> rowNode, int columnIndex) {
		int rowIndex = rowNode != null ? getModel().getRows().indexOf(rowNode) : -1;
		return isEnabledRowAtColumn(rowNode, rowIndex, columnIndex);
	}
	private boolean isEnabledRowAtColumn(TreeNodeVector<E> rowNode, int rowIndex, int columnIndex) {
		
		if (rowIndex >= 0 && rowNode != null) {
			TableCellRenderer render = getCellRenderer(rowIndex, 0);
			int modelIndex = getColumnModel().getColumn(columnIndex).getModelIndex();
			
			Component cellRendererComponent = render.getTableCellRendererComponent(this, rowNode.get(modelIndex), true, true, rowIndex, columnIndex);
			
			return cellRendererComponent.isEnabled();
		}
		else return false;
	}
}
