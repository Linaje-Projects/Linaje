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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import linaje.LocalizedStrings;
import linaje.gui.Icons;
import linaje.gui.LLabel;
import linaje.gui.LMenuItem;
import linaje.gui.LPopupMenu;
import linaje.gui.renderers.LHeaderRenderer;
import linaje.logs.Console;
import linaje.table.TableModelTree;

@SuppressWarnings("serial")
public class LTableHeader extends JTableHeader implements ActionListener {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String orderAscent;
		public String orderDescent;
		public String orderRestore;
			
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private LPopupMenu popupMenu = null;
	private LMenuItem mniSortAscending = null;
	private LMenuItem mniSortDescending = null;
	private LMenuItem mniSortRestore = null;
	
	private TableColumn columnClicked = null;
	
	private boolean isDragging = false;

	public LTableHeader(JTable table) {
		super(table.getColumnModel());
	}
	
	protected TableCellRenderer createDefaultRenderer() {
        return new LHeaderRenderer();
    }
	
	/**
	 * actionPerformed method comment.
	 */
	public void actionPerformed(ActionEvent e) {
	
		if (e.getSource() instanceof JMenuItem && getColumnClicked() != null) {
	
			int modelIndex = getColumnClicked().getModelIndex();
			
			if (e.getSource() == getMniSortAscending())
				sort(modelIndex, true);
			else if (e.getSource() == getMniSortDescending())
				sort(modelIndex, false);
			else if (e.getSource() == getMniSortRestore())
				restoreOriginalSort();
		}
	}
	
	private TableColumn getColumnClicked() {
		return columnClicked;
	}
		
	protected Vector<String> getColumnNames() {
	
		Vector<String> columnNames = new Vector<String>();
		for (int i = 0; i < getTable().getModel().getColumnCount(); i++)
			columnNames.add(getTable().getModel().getColumnName(i));
		
		return columnNames;
	}
	
	private LMenuItem getMniSortAscending() {
		
		if (mniSortAscending == null) {
	
			mniSortAscending = new LMenuItem(TEXTS.orderAscent);
			mniSortAscending.setHorizontalTextPosition(LMenuItem.RIGHT);
			mniSortAscending.setIcon(Icons.SORT_ASCEN);
			mniSortAscending.setMargin(new Insets(2, 0, 2, 0));
			mniSortAscending.addActionListener(this);
		}
		return mniSortAscending;
	}
	
	private LMenuItem getMniSortDescending() {
	
		if (mniSortDescending == null) {
	
			mniSortDescending = new LMenuItem(TEXTS.orderDescent);
			mniSortDescending.setHorizontalTextPosition(LMenuItem.RIGHT);
			mniSortDescending.setIcon(Icons.SORT_DESCEN);
			mniSortDescending.setMargin(new Insets(2, 0, 2, 0));
			mniSortDescending.addActionListener(this);
		}
		return mniSortDescending;
	}
	
	private LMenuItem getMniSortRestore() {
	
		if (mniSortRestore == null) {
	
			mniSortRestore = new LMenuItem(TEXTS.orderRestore);
			mniSortRestore.setHorizontalTextPosition(LMenuItem.RIGHT);
			mniSortRestore.setIcon(Icons.UNDO);
			mniSortRestore.setMargin(new Insets(2, 0, 2, 0));
			mniSortRestore.addActionListener(this);
		}
		return mniSortRestore;
	}
	
	private LPopupMenu getPopupMenu() {
	
		if (popupMenu == null) {
	
			popupMenu = new LPopupMenu();
			popupMenu.add(getMniSortAscending());
			popupMenu.add(getMniSortDescending());
			popupMenu.add(getMniSortRestore());
		}
		return popupMenu;
	}
	
	protected void sort(int modelIndex, boolean useNaturalOrder) {
	
		if (getTable() != null && getTable().getModel() instanceof TableModelTree) {
			
			TableModelTree<?> model = (TableModelTree<?>) getTable().getModel();
			model.sortTree(useNaturalOrder, modelIndex);
			
			repaint();
		}
	}
	
	protected void restoreOriginalSort() {
		
		if (getTable() != null && getTable().getModel() instanceof TableModelTree) {
			
			TableModelTree<?> model = (TableModelTree<?>) getTable().getModel();
			model.restoreOriginalTreeSort();
			
			repaint();
		}
	}
	
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
	
		pintarOrderIcon(g);
		paintColSpans(g, getTable());
	}
	
	private void pintarOrderIcon(Graphics g) {
		
		super.paintComponent(g);
	
		try {
	
			if (getTable() != null && getTable().getModel() instanceof TableModelTree) {
				
				TableModelTree<?> model = (TableModelTree<?>) getTable().getModel();
				if (model.isSorted()) {
					
					int modelIndexSort = model.getIndexSort();
					if (modelIndexSort != -1) {
						
						int indexSorted = - 1;
						for (int i = 0; indexSorted == - 1 && i < getTable().getColumnModel().getColumnCount(); i++){
							TableColumn columna = getTable().getColumnModel().getColumn(i);
							if (columna.getModelIndex() == modelIndexSort) {
								indexSorted = i;
							}
						}
						
						if (indexSorted != -1) {
				
							Rectangle headerRect = getHeaderRect(indexSorted);
							
							ImageIcon icon = model.useNaturalOrderSort() ? Icons.SORT_ASCEN_TRASLUCENT : Icons.SORT_DESCEN_TRASLUCENT;
							
							Color foreground = null;
							TableColumn sortedColumn = getTable().getColumnModel().getColumn(indexSorted);
							if (sortedColumn instanceof LColumn) {
								foreground = ((LColumn) sortedColumn).getHeaderForeground();
							}
							if (foreground == null) {
								TableCellRenderer render = sortedColumn.getHeaderRenderer();
								if (render != null && render instanceof JComponent) {
									foreground = ((JComponent) render).getForeground();
								}
								else {
									foreground = getForeground();
								}
							}
							Image colorizedImage = Icons.createColorizedImage(icon.getImage(), foreground, false);
							ImageIcon colorizedIcon = new ImageIcon(colorizedImage);
							
							int width = icon.getIconWidth();
							int height = icon.getIconHeight();
									
							g.drawImage(colorizedIcon.getImage(), headerRect.x + 2, headerRect.height - height - 2, width, height, this);
						}
					}
				}
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	public void processMouseMotionEvent(MouseEvent e) {
		isDragging = e.getID() == MouseEvent.MOUSE_DRAGGED;
		super.processMouseMotionEvent(e);
	}
	
	public void processMouseEvent(MouseEvent e) {
	
		super.processMouseEvent(e);
		
		if (e.getID() == MouseEvent.MOUSE_PRESSED)
			isDragging = false;
		
		if (e.getSource() == this && e.getID() != MouseEvent.MOUSE_ENTERED && e.getID() != MouseEvent.MOUSE_EXITED && e.getID() != MouseEvent.MOUSE_CLICKED) {
	
			int columnIndex = getColumnModel().getColumnIndexAtX(e.getX());
			TableColumn columnClicked = columnIndex != -1 ? getColumnModel().getColumn(columnIndex) : null;	
			setColumnClicked(columnClicked);
			
			if (e.getID() != MouseEvent.MOUSE_RELEASED && getTable() instanceof LTable<?>) {
				((LTable<?>) getTable()).fireColumnClicked(e, columnIndex);
			}
						
			if (e.getID() == MouseEvent.MOUSE_RELEASED) {
				
				if (isDragging) {
					isDragging = false;
				}
				else {
	
					if (e.getClickCount() == 1) {
	
						if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
	
							getPopupMenu().show(this, e.getX(), e.getY());
						}
						else {
	
							if (getTable() != null && getTable().getModel() instanceof TableModelTree) {
								
								TableModelTree<?> model = (TableModelTree<?>) getTable().getModel();
								
								int modelIndex = columnClicked.getModelIndex();
								int indexSort = model.getIndexSort();
								boolean useNaturalOrder = true;
								if (modelIndex == indexSort)
									useNaturalOrder = !model.useNaturalOrderSort();
								
								sort(modelIndex, useNaturalOrder);
							}
						}
					}
				}
			}
		}
	}
	
	private void setColumnClicked(TableColumn columnClicked) {
		this.columnClicked = columnClicked;
	}
		
	private void paintColSpans(Graphics g, JTable table) {

		int indexFirstColSpanTemp = 0;
		int widthColsSpanTemp = 0;

		int columns = table.getColumnCount();

		Font font = null;
		Color background = null;
		Color foreground = null;
		for (int row = 0; row < 5; row++) {
			
			for (int i = 0; i < columns; i++) {

				TableColumn column = table.getColumnModel().getColumn(i);
				TableCellRenderer render = column.getHeaderRenderer();
				
				if (render != null && render instanceof LHeaderRenderer) {

					LHeaderRenderer lHeaderRender = (LHeaderRenderer) render;
					Rectangle boundsCol = table.getCellRect(0, i, true);

					int typeSpan = lHeaderRender.getTypeSpan()[row];

					if (typeSpan == LHeaderRenderer.TYPE_SPAN_OPEN_RIGHT) {

						indexFirstColSpanTemp = boundsCol.x;
						widthColsSpanTemp = boundsCol.width;
						font = lHeaderRender.getFont();
						background = lHeaderRender.getBackground();
						foreground = lHeaderRender.getForeground();
					}
					else if (typeSpan == LHeaderRenderer.TYPE_SPAN_OPEN_BOTH) {

						widthColsSpanTemp = widthColsSpanTemp + boundsCol.width;
					}
					else if (typeSpan == LHeaderRenderer.TYPE_SPAN_OPEN_LEFT || typeSpan == LHeaderRenderer.TYPE_SPAN_CLOSE_BOTH) {

						if (typeSpan == LHeaderRenderer.TYPE_SPAN_CLOSE_BOTH) {

							indexFirstColSpanTemp = boundsCol.x;
							widthColsSpanTemp = boundsCol.width;
							font = lHeaderRender.getFont();
							background = lHeaderRender.getBackground();
							foreground = lHeaderRender.getForeground();
						}
						else {
						
							widthColsSpanTemp = widthColsSpanTemp + boundsCol.width;
							if (typeSpan == LHeaderRenderer.TYPE_SPAN_OPEN_RIGHT || background == null) {
								font = lHeaderRender.getFont();
								background = lHeaderRender.getBackground();
								foreground = lHeaderRender.getForeground();
							}
						}
						
						int altoLinea = lHeaderRender.getFontMetrics(font).getHeight();
						
						int x = indexFirstColSpanTemp;
						int y = (row * altoLinea);
						int width = widthColsSpanTemp - 1;
						int height = altoLinea;
						
						Rectangle rectsLabel = new Rectangle(x, y, width, height);
						String text = lHeaderRender.getLines()[row];
						paintLabelSpan(g, rectsLabel, text, font, background, foreground);
						
	 					/*

						//Tapamos los textos y lineas separatorias de la primera cabecera de las columnas actuales
						int x = posPrimeraColSpanTemp;
						int y = (linea * altoLinea);
						int width = widthColsSpanTemp - 1;
						int height = altoLinea;
						
						g.setColor(background);

						int x1 = width/2;
						int y1 = y;
						int x2 = x1;
						int y2 = y + height;

						//Fondo
						g.fillRect(x, y, width, height);
						
						//Pintamos un único borde para la cabecera span
						x1 = posPrimeraColSpanTemp;
						x2 = posPrimeraColSpanTemp + widthColsSpanTemp - 2;
						y1 = altoLinea * (linea + 1) - 1;
						y2 = altoLinea * (linea + 1) - 1;

						g.setColor(ColorsGUI.getHeaderGridColor());
						g.drawLine(x1, y1, x2, y2);

						//Ponemos una única primera cabecera centrada para todas las columans
						fuente = new Font(fuente.getName(), Font.BOLD, fuente.getSize());
						String cabecera = lHeaderRender.getLineas()[linea];
						x = getPosicionCabecera(cabecera, posPrimeraColSpanTemp, widthColsSpanTemp, fuente, tabla);
						y = (altoLinea * (linea + 1)) - 4;

						String textoAjustado = UtilsGUI.getTextoAjustado(cabecera, widthColsSpanTemp, fuente);

						g.setColor(foreground);
						g.setFont(fuente);
						UtilsGUI.drawString(g, textoAjustado, x, y);*/
					}
				}
			}
		}
	}

	private void paintLabelSpan(Graphics g, Rectangle rectsLabel, String text, Font font, Color background, Color foreground) {
		
		LLabel label = new LLabel(text);
		label.setForeground(foreground);
		label.setBackground(background);
		label.setFont(font);
		label.setBounds(rectsLabel);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		//label.setOpaque(true);
		//label.setGradientBackGround(true);
		//label.setBackground(background);
		
		Graphics cg = g.create(rectsLabel.x, rectsLabel.y, rectsLabel.width, rectsLabel.height);
		label.paint(cg);
	}
/*
	public static void paintColSpans(Graphics g, JTable tabla) {

		int posPrimeraColSpanTemp = 0;
		int widthColsSpanTemp = 0;

		int columnas = tabla.getColumnCount();

		Color background = null;
		Color foreground = null;
		for (int linea = 0; linea < 5; linea++) {
			
			for (int i = 0; i < columnas; i++) {

				TableColumn columna = tabla.getColumnModel().getColumn(i);
				TableCellRenderer render = columna.getHeaderRenderer();
				if (render == null)
					render = tabla.getTableHeader().getDefaultRenderer();
				
				if (render instanceof LHeaderRenderer) {

					LHeaderRenderer lHeaderRender = (LHeaderRenderer) render;
					Rectangle boundsCol = tabla.getCellRect(0, i, true);

					int tipoSpan = lHeaderRender.getTipoSpan()[linea];

					if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_RIGHT) {

						posPrimeraColSpanTemp = boundsCol.x;
						widthColsSpanTemp = boundsCol.width;
						background = lHeaderRender.getBackground();
						foreground = lHeaderRender.getForeground();
					}
					else if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_BOTH) {

						widthColsSpanTemp = widthColsSpanTemp + boundsCol.width;
					}
					else if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_LEFT || tipoSpan == LHeaderRenderer.TYPE_SPAN_CLOSE_BOTH) {

						if (tipoSpan == LHeaderRenderer.TYPE_SPAN_CLOSE_BOTH) {

							posPrimeraColSpanTemp = boundsCol.x;
							widthColsSpanTemp = boundsCol.width;
							background = lHeaderRender.getBackground();
							foreground = lHeaderRender.getForeground();
						}
						else {
						
							widthColsSpanTemp = widthColsSpanTemp + boundsCol.width;
							if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_RIGHT || background == null) {
								background = lHeaderRender.getBackground();
								foreground = lHeaderRender.getForeground();
							}
						}
						//int altoLinea = getHeight()/lHeaderRender.getLineas().length;
						Font fuente = lHeaderRender.getFont();
						
						int altoLinea = tabla.getFontMetrics(fuente).getHeight() + 1;
						if (fuente.getStyle() == Font.BOLD)
							altoLinea = fuente.getSize() + 5;

						//Tapamos los textos y lineas separatorias de la primera cabecera de las columnas actuales
						int x = posPrimeraColSpanTemp;
						int y = (linea * altoLinea);
						int width = widthColsSpanTemp - 1;
						int height = altoLinea;
						
						g.setColor(background);

						int x1 = width/2;
						int y1 = y;
						int x2 = x1;
						int y2 = y + height;

						//Fondo
						g.fillRect(x, y, width, height);
						
						//Pintamos un único borde para la cabecera span
						x1 = posPrimeraColSpanTemp;
						x2 = posPrimeraColSpanTemp + widthColsSpanTemp - 2;
						y1 = altoLinea * (linea + 1) - 1;
						y2 = altoLinea * (linea + 1) - 1;

						g.setColor(ColorsGUI.getHeaderGridColor());
						g.drawLine(x1, y1, x2, y2);

						//Ponemos una única primera cabecera centrada para todas las columans
						fuente = new Font(fuente.getName(), Font.BOLD, fuente.getSize());
						String cabecera = lHeaderRender.getLineas()[linea];
						x = getPosicionCabecera(cabecera, posPrimeraColSpanTemp, widthColsSpanTemp, fuente, tabla);
						y = (altoLinea * (linea + 1)) - 4;

						String textoAjustado = UtilsGUI.getTextoAjustado(cabecera, widthColsSpanTemp, fuente);

						g.setColor(foreground);
						g.setFont(fuente);
						UtilsGUI.drawString(g, textoAjustado, x, y);
					}
				}
			}
		}
	}

	public static void pintarColSpansClasicos(Graphics g, JTable tabla) {

		int posPrimeraColSpanTemp = 0;
		int widthColsSpanTemp = 0;

		int columnas = tabla.getColumnCount();

		Color background = null;
		Color foreground = null;
		for (int linea = 0; linea < 5; linea++) {
			
			for (int i = 0; i < columnas; i++) {

				TableColumn columna = tabla.getColumnModel().getColumn(i);
				TableCellRenderer render = columna.getHeaderRenderer();
				if (render instanceof LHeaderRenderer) {

					LHeaderRenderer lHeaderRender = (LHeaderRenderer) render;
					Rectangle boundsCol = tabla.getCellRect(0, i, true);

					int tipoSpan = lHeaderRender.getTipoSpan()[linea];

					if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_RIGHT) {

						posPrimeraColSpanTemp = boundsCol.x;
						widthColsSpanTemp = boundsCol.width;
						background = lHeaderRender.getBackground();
						foreground = lHeaderRender.getForeground();
					}
					else if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_BOTH) {

						widthColsSpanTemp = widthColsSpanTemp + boundsCol.width;
					}
					else if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_LEFT || tipoSpan == LHeaderRenderer.TYPE_SPAN_CLOSE_BOTH) {

						if (tipoSpan == LHeaderRenderer.TYPE_SPAN_CLOSE_BOTH) {

							posPrimeraColSpanTemp = boundsCol.x;
							widthColsSpanTemp = boundsCol.width;
							background = lHeaderRender.getBackground();
							foreground = lHeaderRender.getForeground();
						}
						else {
						
							widthColsSpanTemp = widthColsSpanTemp + boundsCol.width;
							if (tipoSpan == LHeaderRenderer.TYPE_SPAN_OPEN_RIGHT || background == null) {
								background = lHeaderRender.getBackground();
								foreground = lHeaderRender.getForeground();
							}
						}
						//int altoLinea = getHeight()/lHeaderRender.getLineas().length;
						Font fuente = lHeaderRender.getFont();
						
						int altoLinea = tabla.getFontMetrics(fuente).getHeight() + 1;
						if (fuente.getStyle() == Font.BOLD)
							altoLinea = fuente.getSize() + 5;

						//Tapamos los textos y lineas separatorias de la primera cabecera de las columnas actuales
						int x = posPrimeraColSpanTemp + 2;
						int y = (linea * altoLinea) + 1;
						int width = widthColsSpanTemp - 5;
						int height = altoLinea - 2;
						
						g.setColor(background);

						int x1 = width/2;
						int y1 = y;
						int x2 = x1;
						int y2 = y + height;

						//Fondo
						Graphics2D g2d = (Graphics2D) g;
						GradientPaint gp = new GradientPaint(x1, y1, background.brighter(), x2, y2, background, false);
						g2d.setPaint(gp);
						Rectangle2D.Double rectangle = new Rectangle2D.Double(x, y, width, height);	
						g2d.fill(rectangle);
			
						//Pintamos un único borde para la cabecera span
						x1 = posPrimeraColSpanTemp + 1;
						x2 = posPrimeraColSpanTemp + widthColsSpanTemp - 3;
						y1 = linea * altoLinea;
						y2 = linea * altoLinea;

						//Borde superior
						g.setColor(Color.white);
						g.drawLine(x1, y1, x2, y2);

						//Borde inferior
						y1 = altoLinea * (linea + 1);
						y2 = altoLinea * (linea + 1);

						g.drawLine(x1, y1, x2, y2);

						x2 = x2 - 1;
						y1 = y1 - 1;
						y2 = y2 - 1;

						g.setColor(Color.gray);
						g.drawLine(x1, y1, x2, y2);

						//Ponemos una única primera cabecera centrada para todas las columans
						fuente = new Font(fuente.getName(), Font.BOLD, fuente.getSize());
						String cabecera = lHeaderRender.getLineas()[linea];
						x = getPosicionCabecera(cabecera, posPrimeraColSpanTemp, widthColsSpanTemp, fuente, tabla);
						y = (altoLinea * (linea + 1)) - 4;

						String textoAjustado = UtilsGUI.getTextoAjustado(cabecera, widthColsSpanTemp, fuente);

						g.setColor(foreground);
						g.setFont(fuente);
						UtilsGUI.drawString(g, textoAjustado, x, y);
					}
				}
			}
		}
	}
*/
}