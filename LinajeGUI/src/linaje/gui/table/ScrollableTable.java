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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;

import linaje.gui.renderers.LCellRenderer;
import linaje.gui.tests.TestScrollableTable;
import linaje.gui.renderers.CellRendererExpandable;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.logs.Console;
import linaje.utils.Lists;

/**
 * Visualizar rapidamente columnas que no caben en una table normal mediante un scroll horizontal mientras se visualizan siempre las columnas fijas que elijamos.
 * - Permite asociar un ScrollableTable de referencia (normalmente de pocas filas 1-4 filas) que se sincronizará con éste en cuanto a tamaños y división,
 * 	 que se puede usar por ejemplo para visualizar totales y medias de forma separada a la tabla principal y poder compararlos siempre con el ScrollableTable principal.
 *
 * @see TestScrollableTable
 *
 * @author Pablo Linaje
 * @version 1.36
 */

@SuppressWarnings("serial")
public class ScrollableTable<E> extends JPanel implements ComponentListener, MouseMotionListener, PropertyChangeListener, ChangeListener, ListSelectionListener, TableColumnModelListener, TableModelListener {
	
	//UI Components
	private JPanel panelWest = null;
	private JPanel panelSouthWest = null;
	private JPanel panelBorderBottomTableFixed = null;
	
	private JSplitPane splitPane = null;
	
	private JScrollPane scrollPaneTableScroll = null;
	private JScrollPane scrollPaneTableFixed = null;
	
	private LTable<E> tableScroll = null;
	private LTable<E> tableFixed = null;
	private LTable<E> table = null;
	private LTable<E> tableRef;
	
	private ScrollableTable<E> scrollableTableRef = null;
	
	//Config fields
	private List<Integer> fixedColumnIndices = null;
	private int displayedColumns = 0;
	//Optional config fields
	private int dividerSize = 2;
	private int widthTableFixed = 0;
	
	//Support fields
	private boolean firstTime = true;
	private boolean firstView = true;
	
	private int displayedScrollColumns = 0;
	private int lastWidth = 0;
	
	public ScrollableTable(LTable<E> table) {
		this(table, null);
	}
	public ScrollableTable(LTable<E> table, LTable<E> tableRef) {
		super();
		setTable(table);
		setTableRef(tableRef);
		initialize();
	}
	
	//
	// Tables functions access
	//
	
	public void clearSelection() {
		getTableScroll().clearSelection();
		getTableFixed().clearSelection();
	}
	
	public int getSelectionMode() {
		return getTable().getSelectionModel().getSelectionMode();
	}
	public void setSelectionMode(int selectionMode) {
		getTable().setSelectionMode(selectionMode);
		getTableFixed().setSelectionMode(selectionMode);
		getTableScroll().setSelectionMode(selectionMode);
	}
	
	public void setFont(Font font) {
		
		getTableFixed().setFont(font);
		getTableScroll().setFont(font);
		if (getTable() != null)
			getTable().setFont(font);
	
		super.setFont(font);
	}
	
	public boolean getRowSelectionAllowed() {
		return getTable().getRowSelectionAllowed();
	}
	public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
		getTable().setRowSelectionAllowed(rowSelectionAllowed);
		getTableFixed().setRowSelectionAllowed(rowSelectionAllowed);
		getTableScroll().setRowSelectionAllowed(rowSelectionAllowed);
		if (getTableRef() != null)
			getScrollableTableRef().setRowSelectionAllowed(rowSelectionAllowed);
	}
	public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
		getTable().setColumnSelectionAllowed(columnSelectionAllowed);
		getTableFixed().setColumnSelectionAllowed(columnSelectionAllowed);
		getTableScroll().setColumnSelectionAllowed(columnSelectionAllowed);
		if (getTableRef() != null)
			getScrollableTableRef().setColumnSelectionAllowed(columnSelectionAllowed);
	}		
	
	public LTableModel<E> getLTableModel() {
		if (getTable() != null)
			return getTable().getModel();
		else
			return null;
	}
	public LTableModel<E> getLTableModelRef() {
		if (getTableRef() != null)
			return getTableRef().getModel();
		else
			return null;
	}
	private int getFixedColumns() {
		return getTableFixed().getColumnCount();
	}
	private int getScrollColumns() {
		return getTableScroll().getColumnCount();
	}
	
	//
	// Init and finalize methods
	//
	
	private void initialize() {
		
		Dimension defaultSize = new Dimension(800, 500);
		setSize(defaultSize);
		getSplitPane().setSize(defaultSize);
		
		setLayout(new BorderLayout());
		add(getSplitPane(), BorderLayout.CENTER);
		
		//Asignamos el mismo modelo y fuente a las dos tablas
		getTableScroll().setModel(getLTableModel());
		getTableFixed().setModel(getLTableModel());
	
		getTableScroll().setBackground(getTable().getBackground());
		getTableFixed().setBackground(getTable().getBackground());
	
		//Construimos la table asociada cuando proceda
		if (getTableRef() != null) {
			
			ScrollableTable<E> scrTableRef = new ScrollableTable<E>(getTableRef(), null);
			setScrollableTableRef(scrTableRef);
			getScrollableTableRef().getScrollPaneTableScroll().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getScrollableTableRef().getPanelWest().remove(getScrollableTableRef().getPanelSouthWest());
			add(getScrollableTableRef(), BorderLayout.NORTH);
		}
		
		initConnections();
		initTableProperties(getTable());
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		getSplitPane().setBorder(BorderFactory.createEmptyBorder());
	}
	
	private void initTableProperties(LTable<E> table) {
		
		table.setOpaque(false);
		table.setAutoCreateColumnsFromModel(false);
		
		table.getTableHeader().setUpdateTableInRealTime(false);
	}
	
	public void destroy() {
		
		try {
			
			finalizeConnections();
	
			//UI Components
			panelWest = null;
			panelSouthWest = null;
			panelBorderBottomTableFixed = null;
			
			splitPane = null;
			
			scrollPaneTableScroll = null;
			scrollPaneTableFixed = null;
			
			tableScroll = null;
			tableFixed = null;
			table = null;
			tableRef = null;
			
			scrollableTableRef = null;
			
			//Config fields
			getFixedColumnIndices().clear();
			fixedColumnIndices = null;
			
			finalize();
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}	
	
	//
	// Init components
	//
	
	private JPanel getPanelBorderBottomTableFixed() {
		if (panelBorderBottomTableFixed == null) {
			panelBorderBottomTableFixed = new JPanel();
			panelBorderBottomTableFixed.setLayout(null);
			panelBorderBottomTableFixed.setOpaque(false);
			panelBorderBottomTableFixed.setPreferredSize(new Dimension(0, 1));
			panelBorderBottomTableFixed.setMaximumSize(new Dimension(0, 0));
		}
		return panelBorderBottomTableFixed;
	}

	private JPanel getPanelWest() {
		if (panelWest == null) {
			panelWest = new JPanel(new BorderLayout());
			panelWest.setOpaque(false);
			panelWest.setPreferredSize(new Dimension(250, 0));
			panelWest.setMinimumSize(new Dimension(250, 0));
			panelWest.add(getScrollPaneTableFixed(), BorderLayout.CENTER);
		}
		return panelWest;
	}
	
	private JPanel getPanelSouthWest() {
		if (panelSouthWest == null) {
			
			final int SCROLL_HEIGHT = getScrollPaneTableScroll().getHorizontalScrollBar().getPreferredSize().height;
			panelSouthWest = new JPanel(new BorderLayout());
			panelSouthWest.setOpaque(false);
			panelSouthWest.setPreferredSize(new Dimension(0, SCROLL_HEIGHT));
			panelSouthWest.setMinimumSize(new Dimension(0, SCROLL_HEIGHT));
			panelSouthWest.add(getPanelBorderBottomTableFixed(), BorderLayout.NORTH);
		}
		return panelSouthWest;
	}
	
	public JScrollPane getScrollPaneTableScroll() {
		if (scrollPaneTableScroll == null) {
			scrollPaneTableScroll = new JScrollPane();
			scrollPaneTableScroll.setOpaque(false);
			scrollPaneTableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPaneTableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPaneTableScroll.setViewportView(getTableScroll());
			scrollPaneTableScroll.setBorder(BorderFactory.createEmptyBorder());
			
			scrollPaneTableScroll.setMaximumSize(new Dimension(0, 0));
			scrollPaneTableScroll.setPreferredSize(new Dimension(0, 0));
			scrollPaneTableScroll.setMinimumSize(new Dimension(0, 0));
		}
		return scrollPaneTableScroll;
	}

	public JScrollPane getScrollPaneTableFixed() {
		if (scrollPaneTableFixed == null) {
			scrollPaneTableFixed = new JScrollPane();
			scrollPaneTableFixed.setOpaque(false);
			scrollPaneTableFixed.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPaneTableFixed.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPaneTableFixed.setViewportView(getTableFixed());
			scrollPaneTableFixed.setBorder(BorderFactory.createEmptyBorder());
			
			scrollPaneTableFixed.setMaximumSize(new Dimension(0, 0));
			scrollPaneTableFixed.setPreferredSize(new Dimension(200, 0));
			scrollPaneTableFixed.setMinimumSize(new Dimension(200, 0));
		}
		return scrollPaneTableFixed;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setOpaque(false);
			splitPane.add(getPanelWest(), JSplitPane.LEFT);
			splitPane.add(getScrollPaneTableScroll(), JSplitPane.RIGHT);
			splitPane.setDividerSize(getDividerSize());
			splitPane.setContinuousLayout(true);
			//Sobreescribimos el UI para pintar el divider casi invsible
			splitPane.setUI(new BasicSplitPaneUI() {
				@Override
				public BasicSplitPaneDivider createDefaultDivider() {
					return new BasicSplitPaneDivider(this) {
						
						@Override
						public void setBorder(Border border) {
							super.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
						}
						@Override
						public void paint(Graphics g) {
							
							g.setColor(ColorsGUI.getColorBorder());
							Rectangle rect = new Rectangle(0, 0, getWidth(), getHeight());
							float transparency = 0.98f;
							GraphicsUtils.fillRect(g, rect, null, transparency);
						}
					};
				}
			});

		}
		return splitPane;
	}
	
	public LTable<E> getTableScroll() {
		if (tableScroll == null) {
			tableScroll = new LTable<E>();
			getScrollPaneTableScroll().setColumnHeaderView(tableScroll.getTableHeader());
			getTableScroll().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			//Propiedades comunes a la tabla de scroll y de fijas
			initTableProperties(getTableScroll());
		}
		return tableScroll;
	}
	
	public LTable<E> getTableFixed() {
		if (tableFixed == null) {
			tableFixed = new LTable<E>();
			getScrollPaneTableFixed().setColumnHeaderView(tableFixed.getTableHeader());
			getTableFixed().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			//Propiedades comunes a la table de scroll y de fijas
			initTableProperties(getTableFixed());
		}
		return tableFixed;
	}
	
	//
	// get and set field Methods
	//
	
	public LTable<E> getTableRef() {
		return tableRef;
	}
	public LTable<E> getTable() {
		return table;
	}
	private ScrollableTable<E> getScrollableTableRef() {
		return scrollableTableRef;
	}
	
	private void setTableRef(LTable<E> newTablaRef) {
		tableRef = newTablaRef;
	}
	
	public int getWidthTableFixed() {
		return widthTableFixed;
	}
	public void setWidthTableFixed(int widthTableFixed) {
		this.widthTableFixed = widthTableFixed;
	}
	
	private int getDisplayedScrollColumns() {
		return displayedScrollColumns;
	}
	private void setDisplayedScrollColumns(int displayedScrollColumns) {
		this.displayedScrollColumns = displayedScrollColumns;
	}
	
	private int getDisplayedColumns() {
		return displayedColumns;
	}
	private void setDisplayedColumns(int displayedColumns) {
		this.displayedColumns = displayedColumns;
	}
	
	public List<Integer> getFixedColumnIndices() {
		if (fixedColumnIndices == null)
			fixedColumnIndices = Lists.newList();
		return fixedColumnIndices;
	}
	public void setFixedColumnIndices(List<Integer> fixedColumns) {
		this.fixedColumnIndices = fixedColumns;
	}
	
	
	//
	// get and set field Methods (Modify components)
	//
	
	public int getDividerSize() {
		return dividerSize;
	}
	public void setDividerSize(int dividerSize) {
		this.dividerSize = dividerSize;
		getSplitPane().setDividerSize(dividerSize);
	}
	
	private void setTable(LTable<E> table) {
		this.table = table;
	}
	public void setScrollableTableRef(ScrollableTable<E> scrollableTableRef) {	
		this.scrollableTableRef = scrollableTableRef;
		if (scrollableTableRef != null && scrollableTableRef.getScrollableTableRef() == null) {
			scrollableTableRef.setScrollableTableRef(this);
			setTableRef(scrollableTableRef.getTable());
		}
	}
	
	//
	// Initilize table and columns
	//
	
	public void initTable(int displayedColumns) {
	
		try {
		   
			initColumns(displayedColumns);
			
			int fixedColumns = getTableFixed().getColumnCount();
			int widthFixedColumns = 0;
			int minWidth = 0;
			int maxWidth = 0;
			
			for (int i = 0; i < fixedColumns; i++) {
				LColumn column = getTableFixed().getColumn(i);
				widthFixedColumns = widthFixedColumns + column.getPreferredWidth();
				minWidth = minWidth + column.getMinWidth();
				if (maxWidth < 9999)
					maxWidth = maxWidth + column.getMaxWidth();
			}
			
			if (maxWidth > 9999)
				maxWidth = 9999;
			
			if (widthFixedColumns < minWidth)
				widthFixedColumns = minWidth;
	
			maxWidth = maxWidth + 4 /*2 pixels por cada lado de la tabla*/ + fixedColumns /*1 pixel por columna*/;
			
			if (getWidthTableFixed() == 0)
				setWidthTableFixed(widthFixedColumns + 4 /*2 pixels por cada lado de la table*/ + fixedColumns /*1 pixel por columna*/);
	
			if (getWidthTableFixed() > maxWidth)
				setWidthTableFixed(maxWidth);
			
			if (firstTime) {
				
				//Asignamos el tamaño del panel de referencia
				getPanelWest().setMinimumSize(new Dimension(minWidth + 4 /*2 pixels por cada lado de la table*/ + fixedColumns /*1 pixel por columna*/, 0));
				getSplitPane().setDividerLocation(getWidthTableFixed());
				firstTime = false;
			}
			
			//Obtenemos el numero de columnas de scroll visibles	
			int scrollColumns = getDisplayedColumns() - fixedColumns;
			if (scrollColumns < 0)
				scrollColumns = 0;
			if (scrollColumns >= getScrollColumns()) {
				scrollColumns = getScrollColumns();
				getScrollPaneTableScroll().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			} 
			else {
				getScrollPaneTableScroll().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
			}
			setDisplayedScrollColumns(scrollColumns);
			
			//Reiniciamos el prefrerredSize del tableheader porque sino no se entera si cambia el ancho de la cabecera de alguna columna 	
			getTableScroll().getTableHeader().setPreferredSize(null);
			getTableFixed().getTableHeader().setPreferredSize(null);
			
			//Asignamos el tamaño de las columnas y de la tabla de scroll
			resizeScrollColumns();
			repaint();
			
			//Si hay Tabla Asociada la iniciamos también
			if (getScrollableTableRef() != null) {
				getScrollableTableRef().setFixedColumnIndices(getFixedColumnIndices());
				getScrollableTableRef().initTableRef(getDisplayedColumns());
			}
			
			//Ponemos o no scroll a la table de fijn caso de que no haya o no columnas de scroll
			if (getScrollColumns() == 0) {
				getScrollPaneTableFixed().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				if (getScrollableTableRef() != null)
					getScrollableTableRef().getScrollPaneTableFixed().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			} 
			else {
				getScrollPaneTableFixed().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				if (getScrollableTableRef() != null)
					getScrollableTableRef().getScrollPaneTableFixed().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void initTableRef(int displayedColumns) {
		
		try {
		    
			initColumns(displayedColumns);
			
			//Asignamos el tamaño del panel de referencia
			getPanelWest().setMinimumSize(getScrollableTableRef().getPanelWest().getMinimumSize());
			getSplitPane().setDividerLocation(getScrollableTableRef().getSplitPane().getDividerLocation());
			revalidate();
	
			getPanelWest().remove(getPanelSouthWest());
			getScrollPaneTableScroll().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			setDisplayedScrollColumns(getScrollableTableRef().getDisplayedScrollColumns());
			
			//Asignamos el tamaño de las columnas y de la tabla de scroll
			resizeScrollColumns();
			repaint();
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		firstTime = false;
	}
	
	private void initColumns(int displayedColumns) {
		
		setDisplayedColumns(displayedColumns);
	
		//Eliminamos las columnas de las tablas
		getTableScroll().removeAllColumns();
		getTableFixed().removeAllColumns();
				
		//Asignamos las columnas fijas y de scroll a cada tabla
		List<LColumn> allColumns = getTable().getColumns();
		List<LColumn> fixedColumns = LColumn.getColumns(allColumns, getFixedColumnIndices());
		
		for (int i = 0; i < allColumns.size(); i++) {
			
			LColumn column = allColumns.get(i);
			if (fixedColumns.contains(column)) {
					
				getTableFixed().addColumn(column);
				column.setWidth(column.getPreferredWidth());
			} 
			else {
				getTableScroll().addColumn(column);
			}
		}
		if (getTableFixed().getColumnCount() > 0 && getTableScroll().getColumnCount() > 0) {
			/*remove(getScrollPaneTableScroll());
			remove(getScrollPaneTableFixed());
			add(getSplitPane(), BorderLayout.CENTER);*/
			LColumn firstScrollColumn = getTableScroll().getColumn(0);
			if (firstScrollColumn.getCellRenderer() instanceof CellRendererExpandable) {
				firstScrollColumn.setCellRenderer(new LCellRenderer<>());
			}
		}/*
		else if (getTableFixed().getColumnCount() == 0) {
			remove(getSplitPane());
			remove(getScrollPaneTableFixed());
			add(getScrollPaneTableScroll(), BorderLayout.CENTER);
		}
		else if (getTableScroll().getColumnCount() == 0) {
			remove(getSplitPane());
			remove(getScrollPaneTableScroll());
			add(getScrollPaneTableFixed(), BorderLayout.CENTER);
		}*/
	}
	
	//
	// ScrollableTable changes
	//
	/**
	 * Si hay table asociada dejamos un hueco equivalente al scroll vertical
	 */
	private void addVerticalScrollAuxScroll() {
		
		if (getScrollableTableRef() != null && isMainScrollableTable()) {
	
			getScrollableTableRef().getScrollPaneTableScroll().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getScrollableTableRef().getScrollPaneTableScroll().getVerticalScrollBar().setEnabled(false);
		}
	}
	/**
	 * Si hay table asociada dejamos un hueco equivalente al scroll vertical
	 */
	private void addVerticalScrollAuxFixed() {
		
		if (getScrollableTableRef() != null && isMainScrollableTable()) {
	
			if (getDisplayedScrollColumns() == 0) {

				getScrollableTableRef().getScrollPaneTableFixed().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				getScrollableTableRef().getScrollPaneTableFixed().getVerticalScrollBar().setEnabled(false);
			}
		}
	}
	
	/**
	 * Si hay table asociada eliminamos el hueco equivalente al scroll vertical
	 */
	private void removeVerticalScrollAuxScroll() {
		if (getScrollableTableRef() != null && isMainScrollableTable()) {
			getScrollableTableRef().getScrollPaneTableScroll().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
	}
	
	/**
	 * Si hay table asociada eliminamos el hueco equivalente al scroll vertical
	 */
	private void removeVerticalScrollAuxFixed() {
	
		if (getScrollableTableRef() != null && isMainScrollableTable()) {
			if (getDisplayedScrollColumns() == 0) {
				getScrollableTableRef().getScrollPaneTableFixed().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			}
		}
	}
	
	private boolean isMainScrollableTable() {
		return getScrollableTableRef() == null || getTableRef() != null;
	}
	
	private int getTotalWidthTables() {
		
		int totalWidthTables = getTable().getWidth();
		if (totalWidthTables == 0) {
		
			if (this.getParent() != null) {
				//Si pasamos por aquí es que la tabla está metida dentro de un layout y no vemos el ancho de scrTable
				totalWidthTables = this.getParent().getBounds().width;
			}
		}
		return totalWidthTables;
	}
	
	private Dimension getPreferredSizeTables() {
		Dimension prefSizeTableFixed = getTableFixed().getPreferredSize();
		Dimension prefSizeTableScroll = getTableScroll().getPreferredSize();
		//int widthTable = prefSizeTableFixed.width + prefSizeTableScroll.width;
		int heightTable = getFixedColumns() > 0 ? prefSizeTableFixed.height : prefSizeTableScroll.height;
		
		Dimension prefSizeHeaderFixed = getTableFixed().getTableHeader().getPreferredSize();
		Dimension prefSizeHeaderScroll = getTableScroll().getTableHeader().getPreferredSize();
		//int widthHeader = prefSizeHeaderFixed.width + prefSizeHeaderScroll.width;
		int heightHeader = getFixedColumns() > 0 ? prefSizeHeaderFixed.height : prefSizeHeaderScroll.height;
		
		//int width = Math.max(widthTable, widthHeader);
		int width = getDisplayedColumns()*100;
		int height = heightTable + heightHeader;
		
		return new Dimension(width, height);
	}
	
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		else {
			Dimension prefSize = getPreferredSizeTables();
			if (getTableFixed() != null && isMainScrollableTable()) {
				prefSize.height = prefSize.height + getScrollableTableRef().getPreferredSizeTables().height;
				if (getScrollColumns() > getDisplayedScrollColumns()) {
					prefSize.height = prefSize.height + getScrollPaneTableScroll().getHorizontalScrollBar().getPreferredSize().height;
				}
			}
			if (getBorder() != null) {
				Insets borderInsets = getBorder().getBorderInsets(this);
				prefSize.height = prefSize.height + borderInsets.top + borderInsets.bottom;
			}
			return prefSize;
		}
	}
	
	private void matchWidthHeaders() {
	
		if (getDisplayedScrollColumns() > 0 && getFixedColumns() > 0) {

			int widthHeaderScroll = 0;
			int widthHeaderFixed = 0;
			
			if (widthHeaderScroll == 0)
				widthHeaderScroll = getTableScroll().getTableHeader().getPreferredSize().height;
			if (widthHeaderFixed == 0)
				widthHeaderFixed = getTableFixed().getTableHeader().getPreferredSize().height;
				
			if (widthHeaderScroll != 0 && widthHeaderFixed != 0 && widthHeaderScroll != widthHeaderFixed) {
				if (widthHeaderScroll > widthHeaderFixed) {
					if (getTableFixed().getColumnCount() != 0) {
						getTableFixed().getTableHeader().setPreferredSize(new Dimension(0, widthHeaderScroll));
						getTableFixed().getTableHeader().setMaximumSize(new Dimension(0, widthHeaderScroll));
						getTableFixed().getTableHeader().setMinimumSize(new Dimension(0, widthHeaderScroll));
					}
				}
				else {
					if (getTableScroll().getColumnCount() != 0) {
						getTableScroll().getTableHeader().setPreferredSize(new Dimension(0, widthHeaderFixed));
						getTableScroll().getTableHeader().setMaximumSize(new Dimension(0, widthHeaderFixed));
						getTableScroll().getTableHeader().setMinimumSize(new Dimension(0, widthHeaderFixed));
					}
				}
			}
		}
	}
	
	public void positionInRow(int row) {
	
		try {
	
			int x = getScrollPaneTableFixed().getViewport().getViewPosition().x;
			int y = row * getTableFixed().getRowHeight();
			getScrollPaneTableFixed().getViewport().setViewPosition(new Point(x, y));
			x = getScrollPaneTableScroll().getViewport().getViewPosition().x;
			getScrollPaneTableScroll().getViewport().setViewPosition(new Point(x, y));
	
			this.paintImmediately(this.getBounds());
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void resizeScrollColumns() {
		
		try {
		
			int totalWidthTables = getTotalWidthTables();
			
			if (getDisplayedScrollColumns() != 0) {
	
				if (getFixedColumns() == 0) {
	
					//Ocultamos el splitPane
					getSplitPane().setDividerSize(0);
					getSplitPane().setDividerLocation(0);
				}
				else {
					if (getSplitPane().getDividerLocation() == 0 ||
						getSplitPane().getDividerLocation() == totalWidthTables)
							getSplitPane().setDividerLocation(getWidthTableFixed());
					getSplitPane().setDividerSize(getDividerSize());
				}
				
				//Igualamos el ancho de las cabeceras de las columnas en caso de que sean distintos
				matchWidthHeaders();
	
				//Asignamos los anchos de las columnas respecto al ancho visible de la table
				resizeColumns(getScrollPaneTableScroll(), getTableScroll().getColumns(), getDisplayedScrollColumns(), true);
			} 
			else {
	
				//Ocultamos el splitPane
				getSplitPane().setDividerSize(0);
				getPanelWest().setMinimumSize(new Dimension(totalWidthTables, 0));
				
				getSplitPane().setDividerLocation(totalWidthTables);
				
				//Ponemos firstTime a true para que cuando se vean columnas de scroll se ajuste el tamaño de la table de fijas a su tamaño original
				firstTime = true;
			}
	
			List<LColumn> fixedColumns = getTableFixed().getColumns();
			resizeColumns(getScrollPaneTableFixed(), fixedColumns);
	
			sizeColumnsToFit(getTableScroll());
			//sizeColumnsToFit(getTableFixed());
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	protected void resizeScrollColumns(int[] widthsColumn) {
	
		//Reasignamos los anchos guardados antes
		for (int i = 0; i < widthsColumn.length; i++) {
	
			LColumn column = getTableScroll().getColumn(i);
			column.setWidth(widthsColumn[i]);
			column.setPreferredWidth(widthsColumn[i]);
		}
	
		sizeColumnsToFit(getTableScroll());
	}
	
	private void repaintTables() {
		
		getTableScroll().revalidate();
		getTableFixed().revalidate();
		
		getTableScroll().repaint();
		getTableScroll().getTableHeader().repaint();
		getTableFixed().repaint();
		getTableFixed().getTableHeader().repaint();
	}
	
	public void restoreTable() {
	
		//Restauramos la separación de tablas si esta cambiada
		if (getSplitPane().getDividerSize() != 0 && getSplitPane().getDividerLocation() != getWidthTableFixed()) {
			getSplitPane().setDividerLocation(getWidthTableFixed());
		}
		else { 
			//Redimensionamos las columnas de scroll
			initTable(getDisplayedColumns());
		}
		
		//Dejamos las columnas fijas como cuando abrimos por primera vez
		for (int i = 0; i < getTableFixed().getColumnCount(); i++) {
	
			//Asignamos el ancho minimo para que se redimensionen automáticamente proporcionalmente a este minimo
			TableColumn column = getTableFixed().getColumnModel().getColumn(i);
			column.setWidth(column.getMinWidth());
			column.setPreferredWidth(column.getMinWidth());
		}
		getTableFixed().sizeColumnsToFit(-1);
	}
	
	private void sizeColumnsToFit(JTable table) {
	
	    Enumeration<TableColumn> enumeration = table.getColumnModel().getColumns();
	    while (enumeration.hasMoreElements()) {
	        TableColumn aColumn = enumeration.nextElement();
	        aColumn.setPreferredWidth(aColumn.getWidth());
	    }
	
	    table.getTableHeader().setPreferredSize(new Dimension(table.getPreferredSize().width, table.getTableHeader().getPreferredSize().height));
	    table.getTableHeader().setSize(table.getPreferredSize().width, table.getTableHeader().getPreferredSize().height);
	}
	
	private void synchronizeWidthsFixedColumns() {
		if (getScrollableTableRef() != null) {
			for (int i = 0; i < getTableFixed().getColumnCount(); i++) {
				TableColumn column = getTableFixed().getColumn(i);
				TableColumn columnRef = getScrollableTableRef().getTableFixed().getColumn(i);
				column.setWidth(columnRef.getWidth());
				column.setPreferredWidth(columnRef.getPreferredWidth());
			}
			//repintarTableFixed();
		}
	}
	
	private void moveColumn(LTable<E> table, int fromIndex, int toIndex) {
		table.getColumnModel().removeColumnModelListener(this);
		try {
			table.moveColumn(fromIndex, toIndex);
		}
		finally {
			table.getColumnModel().addColumnModelListener(this);
		}
	}

	@Override
	public void doLayout() {
		
		super.doLayout();
		
		try {	
			if (firstView) {
				resizeScrollColumns();
				
				if (getScrollPaneTableScroll().getVerticalScrollBar().isVisible())
					addVerticalScrollAuxScroll();
				else
					removeVerticalScrollAuxScroll();
				
				if (getScrollPaneTableFixed().getVerticalScrollBar().isVisible())
					addVerticalScrollAuxFixed();
				else
					removeVerticalScrollAuxFixed();
			}
		} catch (Exception e) {
			firstView = false;
		}
	}
	//
	// Listening Events
	//
	
	private void initConnections() {
		
		try {
			
			getTableFixed().getTableHeader().addMouseMotionListener(this);
			getTableScroll().getTableHeader().addMouseMotionListener(this);
			
			getScrollPaneTableScroll().getViewport().addChangeListener(this);
			getScrollPaneTableFixed().getViewport().addChangeListener(this);
			
			getScrollPaneTableScroll().getVerticalScrollBar().addComponentListener(this);
			getScrollPaneTableFixed().getVerticalScrollBar().addComponentListener(this);
			getScrollPaneTableScroll().getHorizontalScrollBar().addComponentListener(this);
			getScrollPaneTableScroll().addComponentListener(this);
			
			getSplitPane().addPropertyChangeListener(this);
				
			getTableFixed().getSelectionModel().addListSelectionListener(this);
			getTableScroll().getSelectionModel().addListSelectionListener(this);
			
			getTable().getColumnModel().addColumnModelListener(this);
			getTableScroll().getColumnModel().addColumnModelListener(this);
			getTableFixed().getColumnModel().addColumnModelListener(this);
			
			getLTableModel().addTableModelListener(this);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	private void finalizeConnections() {
		
		getTableFixed().getTableHeader().removeMouseMotionListener(this);
		getTableScroll().getTableHeader().removeMouseMotionListener(this);
		
		getScrollPaneTableScroll().getViewport().removeChangeListener(this);
		getScrollPaneTableFixed().getViewport().removeChangeListener(this);
		
		getScrollPaneTableScroll().getVerticalScrollBar().removeComponentListener(this);
		getScrollPaneTableFixed().getVerticalScrollBar().removeComponentListener(this);
		getScrollPaneTableScroll().getHorizontalScrollBar().removeComponentListener(this);
		getScrollPaneTableScroll().removeComponentListener(this);
		
		getSplitPane().removePropertyChangeListener(this);
			
		getTableFixed().getSelectionModel().removeListSelectionListener(this);
		getTableScroll().getSelectionModel().removeListSelectionListener(this);
		
		getTable().getColumnModel().removeColumnModelListener(this);
		getTableScroll().getColumnModel().removeColumnModelListener(this);
		getTableFixed().getColumnModel().removeColumnModelListener(this);
		
		getLTableModel().removeTableModelListener(this);
	}
	
	//Listening resizing columns
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
		
		try {
			
			e.consume();
			if (e.getSource() == getTableFixed().getTableHeader()) {
				
				if (getTableFixed().getColumnCount() > 0) {
					
					TableColumn resizingColumn = getTableFixed().getTableHeader().getResizingColumn();
					if (resizingColumn != null) {
						
						if (getTableScroll().getColumnCount() > 0) {
							//Redimensionamos el panel que contiene la columna fija si la columna es la última		
							if (getTableFixed().getColumnModel().getColumnIndex(resizingColumn.getIdentifier()) == getTableFixed().getColumnCount() - 1) {
								
								int width = e.getX();
								int minWidthFixed = 7;
								for (int i = 0; i < getTableFixed().getColumnCount(); i++) {
		
									TableColumn column = getTableFixed().getColumnModel().getColumn(i);
									minWidthFixed = minWidthFixed + column.getMinWidth();
								}
								if (width < minWidthFixed)
									width = minWidthFixed;
								
								getSplitPane().setDividerLocation(width);
							}
						}
						if (getScrollableTableRef() != null)
							getScrollableTableRef().synchronizeWidthsFixedColumns();
					}
				}
			}
			else if (e.getSource() == getTableScroll().getTableHeader()) {
				
				TableColumn resizingColumn = getTableScroll().getTableHeader().getResizingColumn();
				if (resizingColumn != null) {
					
					int[] validWidths = new int[getTableScroll().getColumnCount()];
					for (int i = 0; i < getTableScroll().getColumnCount(); i++) {
						TableColumn column = getTableScroll().getColumnModel().getColumn(i);
						validWidths[i] = column.getWidth();
					}
					
					resizeScrollColumns(validWidths);
					getScrollableTableRef().resizeScrollColumns(validWidths);
				}
			}
			
		}
		catch (Throwable ex) {
		}
	}
	
	//Listening scroll viewPort changes
	public void stateChanged(ChangeEvent e) {
		
		if (e.getSource() == getScrollPaneTableScroll().getViewport()) {

			if (getFixedColumns() > 0) {
				matchWidthHeaders();
				int y = getScrollPaneTableScroll().getViewport().getViewPosition().y;
				getScrollPaneTableFixed().getViewport().removeChangeListener(this);
				getScrollPaneTableFixed().getViewport().setViewPosition(new Point(0, y));
				getScrollPaneTableFixed().getViewport().addChangeListener(this);
			}
			//Si hay table Asociada cambiamos la vista horizontal
			if (getScrollableTableRef() != null) {
				int y = getScrollableTableRef().getScrollPaneTableScroll().getViewport().getViewPosition().y;
				int x = getScrollPaneTableScroll().getViewport().getViewPosition().x;
				getScrollableTableRef().getScrollPaneTableScroll().getViewport().removeChangeListener(this);
				getScrollableTableRef().getScrollPaneTableScroll().getViewport().setViewPosition(new Point(x, y));
				getScrollableTableRef().getScrollPaneTableScroll().getViewport().addChangeListener(this);
			}
		} 
		else if (e.getSource() == getScrollPaneTableFixed().getViewport()) {
		
			if (getScrollColumns() > 0) {
				matchWidthHeaders();
				//Movemos la vista de la table de scroll segun movemos el scroll vertical de la table de fijas
				int y = getScrollPaneTableFixed().getViewport().getViewPosition().y;
				getScrollPaneTableScroll().getViewport().removeChangeListener(this);
				getScrollPaneTableScroll().getViewport().setViewPosition(new Point(0, y));
				getScrollPaneTableScroll().getViewport().addChangeListener(this);
			}
		}
	}
	
	//Listening SplitPane location change
	public void propertyChange(PropertyChangeEvent e) {
		
		//Cambia el número de columnas de SCROLL visibles
		if (!firstTime && e.getSource() == getSplitPane() && e.getPropertyName().equals("lastDividerLocation")) {
	
			//Si hay table asociada la redimensionamos igual
			if (getScrollableTableRef() != null) {
				getScrollableTableRef().getSplitPane().setDividerLocation(getSplitPane().getDividerLocation());
			}
		}
	}
	
	//Listening table size changes and scroll hidden/shown
	public void componentMoved(ComponentEvent e) {
	}
	public void componentResized(ComponentEvent e) {
		//Cambia el tamaño de la tabla de scroll principal
		if (e.getSource() == getScrollPaneTableScroll()) {

			if (lastWidth != getScrollPaneTableScroll().getWidth()) {
				lastWidth = getScrollPaneTableScroll().getWidth();
				//Redimensionamos las columnas de scroll para el nuevo tamaño de la table
				resizeScrollColumns();
			}
		}
	}
	public void componentShown(ComponentEvent e) {
		
		//Damos por supuesto que la table de referencia siempre va a tener el mísmo nº de filas x lo que su scroll vertical nunca aparecerá por si solo
		//Igualmente tambien damos por supuesto que la table de columnas fijas nunca necesitará scroll horizontal
		if (e.getSource() == getScrollPaneTableScroll().getVerticalScrollBar()) {
			if (isMainScrollableTable()) {
				//Si se muestra el scroll vertical, ponemos también scroll vertical en la table de referencia si la hay
				addVerticalScrollAuxScroll();
			}
			//Redimensionamos las columnas de scroll para que se adapten al nuevo espacio con scroll
			resizeScrollColumns();
		}
		else if (isMainScrollableTable()) {

			//Si se muestra el scroll horizontal, añadimos un panel equivalente en la table de fijas
			if (e.getSource() == getScrollPaneTableScroll().getHorizontalScrollBar()) {
				getPanelWest().add(getPanelSouthWest(), BorderLayout.SOUTH);
				getPanelWest().validate();
			}
			else if (e.getSource() == getScrollPaneTableFixed().getVerticalScrollBar()) {
				addVerticalScrollAuxFixed();
			}
		}
	}
	public void componentHidden(ComponentEvent e) {
		
		if (e.getSource() == getScrollPaneTableScroll().getVerticalScrollBar()) {
			if (isMainScrollableTable()) {
				//Si se oculta el scroll vertical, quitamos también el scroll vertical de la table de referencia si la hay
				removeVerticalScrollAuxScroll();
			}
			//Redimensionamos las columnas de scroll para que se adapten al nuevo espacio sin scroll
			resizeScrollColumns();
		}
		if (isMainScrollableTable()) {
				
			if (e.getSource() == getScrollPaneTableScroll().getHorizontalScrollBar()) {
				//Si se oculta el scroll horizontal, quitamos el panel equivalente de la table de fijas
				getPanelWest().remove(getPanelSouthWest());
				getPanelWest().validate();
			}
			else if (e.getSource() == getScrollPaneTableFixed().getVerticalScrollBar()) {
				removeVerticalScrollAuxFixed();
			}
		}
	}
	
	//Listening table selection changes
	public void valueChanged(ListSelectionEvent e) {
		
		//if (!e.getValueIsAdjusting()) {
			
			boolean rowSelectionFixed = getTableFixed().getRowSelectionAllowed() && !getTableFixed().getColumnSelectionAllowed();
			boolean rowSelectionScroll = getTableScroll().getRowSelectionAllowed() && !getTableScroll().getColumnSelectionAllowed();
			boolean rowSelection = rowSelectionFixed && rowSelectionScroll;
			if (e.getSource() == getTableFixed().getSelectionModel()) {
				int[] selectedRows = rowSelection ? getTableFixed().getSelectedRows() : null;
				
				if (selectedRows != null && selectedRows.length > 0)
					getTableScroll().setRowSelectionInterval(selectedRows[0], selectedRows[selectedRows.length - 1]);
				else
					getTableScroll().clearSelection();
			}
			else if (e.getSource() == getTableScroll().getSelectionModel()) {
				int[] selectedRows = rowSelection ? getTableScroll().getSelectedRows() : null;
				
				if (selectedRows != null && selectedRows.length > 0)
					getTableFixed().setRowSelectionInterval(selectedRows[0], selectedRows[selectedRows.length - 1]);
				else
					getTableFixed().clearSelection();
			}
		//}
	}
	
	//Listening original table column model changes
	public void columnMoved(TableColumnModelEvent e) {
		
		if (getScrollableTableRef() != null) {
			
			if (e.getSource() == getTableScroll().getColumnModel() || e.getSource() == getTableFixed().getColumnModel()) {
				//Movemos la columna equivalente de la table asociada
				int fromIndex = e.getFromIndex();
				int toIndex = e.getToIndex();
				
				LTable<E> targetTable = e.getSource() == getTableScroll().getColumnModel() ? getScrollableTableRef().getTableScroll() : getScrollableTableRef().getTableFixed();
				getScrollableTableRef().moveColumn(targetTable, fromIndex, toIndex);
			}
		}
	}
	public void columnMarginChanged(ChangeEvent e) {}
	public void columnSelectionChanged(ListSelectionEvent e) {}
	public void columnAdded(TableColumnModelEvent e) {
		if (e.getSource() == getTable().getColumnModel()) {
			if (isMainScrollableTable())
				initTable(getDisplayedColumns());
		}
	}
	public void columnRemoved(TableColumnModelEvent e) {
		if (e.getSource() == getTable().getColumnModel()) {
			if (isMainScrollableTable())
				initTable(getDisplayedColumns());
		}
	}
	
	//Listening tablemodel changes
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			//Repintamos las tablas al ordenar, para que se repinten las cabeceras de todas las tablas
			repaintTables();
			if (getScrollableTableRef() != null) {
				getScrollableTableRef().repaintTables();
			}
		}
	}
	
	
	//
	// Statics
	//
	
	public static int resizeColumns(JScrollPane scrollPane, List<LColumn> columns) {
		return resizeColumns(scrollPane, columns, columns.size(), false);
	}
	public static int resizeColumns(JScrollPane scrollPane, List<LColumn> columns, int visibleColumns, boolean resizeUnrestrictedColumns) {

		if (visibleColumns <= 0)
			return 0;
		
		int widthTable = 0;
				
		try {

			Insets insetsBorderScroll = scrollPane.getBorder().getBorderInsets(scrollPane);
			int widthTableVisible = scrollPane.getWidth() - insetsBorderScroll.left - insetsBorderScroll.right;
			if (scrollPane.getVerticalScrollBar().isVisible())
				widthTableVisible = widthTableVisible - scrollPane.getVerticalScrollBar().getPreferredSize().width;
				
			//Calculamos el ancho de las columnas NO limitadas
			int widthRegularColumn = widthTableVisible / visibleColumns;
			int numCols = columns.size();

			//Calculamos el ancho de la table
			if (numCols > 0) {
				
				List<TableColumn> unrestrictedColumns = Lists.newList();
				int excess = widthTableVisible % visibleColumns;
				int restrictedColumns = 0;
				
				//Miramos si hay columnas limitadas a un ancho máximo menor o un ancho mímino mayor del que se asignaría si todas las columnas fuesen iguales
				//La table medirá la suma de todas las columnas
				for (int i = 0; i < columns.size(); i++) {

					TableColumn column = columns.get(i);
					int widthColumn = 0;
					int maxWidthCol = column.getMaxWidth();
					int minWidthCol = column.getMinWidth();

					if (maxWidthCol != 0 && maxWidthCol < widthRegularColumn) {

						widthColumn = maxWidthCol;
						if (i < visibleColumns) {
							//Solo calculamos el excedente para las primeras columnas visibles
							excess = excess + widthRegularColumn - widthColumn;
							restrictedColumns++;
						}
					}
					else if (minWidthCol != 0 && minWidthCol > widthRegularColumn) {
						
						widthColumn = minWidthCol;
						if (i < visibleColumns) {
							//Solo calculamos el excedente para las primeras columnas visibles
							excess = excess + widthRegularColumn - widthColumn;
							restrictedColumns++;
						}
					}
					else {

						if (i < visibleColumns) {
							//Columnas no limitadas dentro de las primeras columnas visibles
							widthColumn = -1;
							unrestrictedColumns.add(column);
						}
						else {
							//Columnas no limitadas que se ven al desplazar el scroll
							widthColumn = widthRegularColumn;
						}
					}

					if (widthColumn != -1) {

						widthTable = widthTable + widthColumn;

						if (widthColumn != widthRegularColumn || resizeUnrestrictedColumns) {
							
							column.setPreferredWidth(widthColumn);
							column.setWidth(widthColumn);
						}
					}
				}

				//Repartimos el ancho restante equitativamente entre el resto de columnas (Dentro de las primeras columnas visibles)
				if (resizeUnrestrictedColumns) {

					if (unrestrictedColumns.size() > 0) {

						int extraExcess = 0;

						//Si el excedente es menor que cero recalculamos el ancho normal de las columnas y el excedente, sino se calcula con el excedente
						if (excess < 0) {
							widthRegularColumn = (widthTableVisible - widthTable) / (visibleColumns - restrictedColumns);
							excess = (widthTableVisible - widthTable) % (visibleColumns - restrictedColumns);
						}
						
						int newColumnWidth;
						if (excess > 0 && restrictedColumns < visibleColumns) {

							int excessWidth = excess / (visibleColumns - restrictedColumns);
							extraExcess = excess % (visibleColumns - restrictedColumns);
							newColumnWidth = widthRegularColumn + excessWidth;
						}
						else
							newColumnWidth = widthRegularColumn;

						for (int i = 0; i < unrestrictedColumns.size(); i++) {

							TableColumn column = unrestrictedColumns.get(i);
							int maxWidthCol = column.getMaxWidth();
							int minWidthCol = column.getMinWidth();

							int columnWidth = newColumnWidth;
							if (i < extraExcess)
								columnWidth++;
							
							if (newColumnWidth < minWidthCol)
								columnWidth = minWidthCol;
							else if (newColumnWidth > maxWidthCol)
								columnWidth = maxWidthCol;

							widthTable = widthTable + columnWidth;
							
							column.setPreferredWidth(columnWidth);
							column.setWidth(columnWidth);
						}
					}
				}
				else {
					widthTable = widthTableVisible;
				}
			}
			scrollPane.getHorizontalScrollBar().setUnitIncrement(widthRegularColumn);
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}

		return widthTable;
	}
}
