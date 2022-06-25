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
package linaje.gui.renderers;

/**
 * This type was created in VisualAge.
 */
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;

import java.awt.*;

import linaje.gui.Icons;
import linaje.gui.table.LColumn;
import linaje.gui.table.LTableHeader;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.statics.Constants;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class LHeaderRenderer extends JLabel implements TableCellRenderer {
	
	public static final int ORDER_ORIGINAL = LColumn.ORDER_ORIGINAL;
	public static final int ORDER_ASCENT = LColumn.ORDER_ASCENT;
	public static final int ORDER_DESCENT = LColumn.ORDER_DESCENT;
	
	//Nos indica cuando combinaremos las cabeceras, se define una vez
	public static final int COMBINE_HEADERS_NEVER =  LColumn.COMBINE_HEADERS_NEVER;
	public static final int COMBINE_HEADERS_FIRST_LINE_MATCH =  LColumn.COMBINE_HEADERS_FIRST_LINE_MATCH;
	public static final int COMBINE_HEADERS_ANY_LINE_MATCH =  LColumn.COMBINE_HEADERS_ANY_LINE_MATCH;
	public static final int COMBINE_HEADERS_FIRST_LINE_ALWAYS =  LColumn.COMBINE_HEADERS_FIRST_LINE_ALWAYS;
	public static final int COMBINE_HEADERS_ANY_LINE_ALWAYS =  LColumn.COMBINE_HEADERS_ANY_LINE_ALWAYS;
	
	//El tipo de span se calculará dinámicamente según el texto de las cabeceras
	//y la configuración de combinar que le hayamos dado a la columna
	public static final int TYPE_SPAN_NONE = LColumn.TYPE_SPAN_NONE;
	public static final int TYPE_SPAN_OPEN_RIGHT = LColumn.TYPE_SPAN_OPEN_RIGHT;
	public static final int TYPE_SPAN_OPEN_LEFT = LColumn.TYPE_SPAN_OPEN_LEFT;
	public static final int TYPE_SPAN_OPEN_BOTH = LColumn.TYPE_SPAN_OPEN_BOTH;
	public static final int TYPE_SPAN_CLOSE_BOTH = LColumn.TYPE_SPAN_CLOSE_BOTH; //Este caso es una columna con doble cabecera que queremos separar una linea de otra
	
	private int order;
	private String[] lines = null;
	private int[] typeSpan = {TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE};
	private boolean isLastColumn = false;
	private boolean usingDefaultRender = false;

	private Color columnBackground = null;
	private Color columnForeground = null;
	
	public boolean gradientBackround = true;
	
	public LHeaderRenderer() {
		super(Constants.VOID, SwingConstants.CENTER);
	}
	public LHeaderRenderer(String text) {
		this(text, SwingConstants.CENTER);
	}
	public LHeaderRenderer(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}
	public LHeaderRenderer(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}
	public LHeaderRenderer(Icon image) {
		super(image);
	}
	public LHeaderRenderer(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public String[] getLines() {
	
		if (lines == null) {
			lines = new String[1];
			lines[0] = "";
		}
		return lines;
	}
	
	public int getOrder() {
		return order;
	}
	
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		String[] lines = Strings.getLines(getText());
		int heightFont = getFontMetrics(getFont()).getHeight();
		
		size.height = lines.length * heightFont + getInsets().top + getInsets().bottom;
		return size;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
		String text = value == null ? Constants.VOID : value.toString();
		String[] lines = Strings.getLines(text);
		
		setLines(lines);
		setText(text);
		
		JTableHeader header = table != null ? table.getTableHeader() : null;
		if (header != null) {
		
			LColumn lColumn = null;
			TableColumn tableColumn = table.getColumnModel().getColumn(column);		
			if (tableColumn != null && tableColumn instanceof LColumn)
				lColumn = (LColumn) tableColumn;
			
			setBackground(header.getBackground());
			setForeground(header.getForeground());
			setFont(header.getFont());
			setOpaque(true);
			
			if (lColumn != null) {
				columnBackground = lColumn.getHeaderBackground();
				columnForeground = lColumn.getHeaderForeground();
				if (columnBackground != null && columnBackground.equals(getBackground()))
					columnBackground = null;
				if (columnForeground != null && columnForeground.equals(getForeground()))
					columnForeground = null;
			}
			
			int[] typesSpan = getTypesSpan(lColumn, table, column, lines);
			
			//Guardamos el tipo de span mayor (el de la 1ª linea de la cabecera) para no tener que calcularlo mas tarde en caso de que haya que hacer colspan en las filas de datos
			if (lColumn != null)
				lColumn.setTypesSpan(typesSpan);
						
			setTypeSpan(typesSpan);
			
			//Solo pintaremos de un color específico la parte de columna sin colspan
			if (!hasColSpan()) {
				if (columnBackground != null)
					setBackground(columnBackground);
				if (columnForeground != null)
					setForeground(columnForeground);
			}
		
			//Pintamos el borde por defecto en caso de que no tenga colspan y siempre que no sea la última columna
			isLastColumn = column == table.getColumnCount() - 1;
			usingDefaultRender = tableColumn.getHeaderRenderer() == null;
			
			Border border = null;
			if (header != null && !hasColSpan() && !isLastColumn) {
				border = hasFocus ? UIManager.getBorder("TableHeader.focusCellBorder") : UIManager.getBorder("TableHeader.cellBorder");
			}
			setBorder(border);
			//
		}
		
		return this;
	}
		
	public int[] getTypeSpan() {
		return typeSpan;
	}
	
	private static int[] getTypesSpan(TableColumn column, JTable table, int columnIndex, String[] lines) {
	
		int[] typeSpan = {TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE, TYPE_SPAN_NONE};
		
		if (column == null || table == null || columnIndex == -1 || lines == null || !(column instanceof LColumn))
			return typeSpan;
		
		LColumn lColumn = (LColumn) column;
		int typeCombine = lColumn.getTypeCombineHeaders();
	
		if (typeCombine != COMBINE_HEADERS_NEVER && lines.length > 1 && columnIndex != -1) {
	
			int lineasSpan = lines.length - 1;
			if (typeCombine == COMBINE_HEADERS_FIRST_LINE_MATCH || typeCombine == COMBINE_HEADERS_FIRST_LINE_ALWAYS)
				lineasSpan = 1;
			
			for (int i = 0; i < lineasSpan; i++) {
	
				boolean sameHeaderPrevious = false;
				boolean sameHeaderNext = false;
				
				if (columnIndex > 0) {
	
					TableColumn columnPrevious = table.getColumnModel().getColumn(columnIndex - 1);
					LColumn lColumnPrevious = columnPrevious instanceof LColumn ? (LColumn) columnPrevious : null;
					int typeCombinePrevious = lColumnPrevious != null ? lColumnPrevious.getTypeCombineHeaders() : COMBINE_HEADERS_NEVER;
					
					if (typeCombinePrevious == COMBINE_HEADERS_ANY_LINE_ALWAYS ||
					   (typeCombinePrevious == COMBINE_HEADERS_FIRST_LINE_ALWAYS && i == 0) ||
					   (typeCombinePrevious == COMBINE_HEADERS_FIRST_LINE_MATCH && i == 0) ||
						typeCombinePrevious == COMBINE_HEADERS_ANY_LINE_MATCH) {
						
						String headerText = columnPrevious.getHeaderValue() != null ? columnPrevious.getHeaderValue().toString() : Constants.VOID;
						String[] linesColumnPrevious = Strings.getLines(headerText);
						String headerTextPrevious = null;
						if (linesColumnPrevious.length > i+1)
							headerTextPrevious = linesColumnPrevious[i];
	
						sameHeaderPrevious = headerTextPrevious != null && headerTextPrevious.equalsIgnoreCase(lines[i]);
	
						//Controlamos que el span de lines inferiores no se salga del span de la linea superior
						if (sameHeaderPrevious && i > 0 && (typeSpan[i-1] == TYPE_SPAN_OPEN_RIGHT || typeSpan[i-1] == TYPE_SPAN_NONE || typeSpan[i-1] == TYPE_SPAN_CLOSE_BOTH))
							sameHeaderPrevious = false;
					}
				}
				if (columnIndex < table.getColumnCount() - 1) {
					
					TableColumn columnNext = table.getColumnModel().getColumn(columnIndex + 1);
					LColumn lColumnNext = columnNext instanceof LColumn ? (LColumn) columnNext : null;
					int typeCombineNext = lColumnNext != null ? lColumnNext.getTypeCombineHeaders() : COMBINE_HEADERS_NEVER;
					
					if (typeCombineNext == COMBINE_HEADERS_ANY_LINE_ALWAYS ||
					   (typeCombineNext == COMBINE_HEADERS_FIRST_LINE_ALWAYS && i == 0) ||
					   (typeCombineNext == COMBINE_HEADERS_FIRST_LINE_MATCH && i == 0) ||
						typeCombineNext == COMBINE_HEADERS_ANY_LINE_MATCH) {
					
						String headerText = columnNext.getHeaderValue() != null ? columnNext.getHeaderValue().toString() : Constants.VOID;
						String[] linesColumnNext = Strings.getLines(headerText);
						String headerTextNext = null;
						if (linesColumnNext.length > i+1)
							headerTextNext = linesColumnNext[i];
	
						sameHeaderNext = headerTextNext != null && headerTextNext.equalsIgnoreCase(lines[i]);
	
						//Controlamos que el span de lines inferiores no se salga del span de la linea superior
						if (sameHeaderNext && i > 0 && (typeSpan[i-1] == TYPE_SPAN_OPEN_LEFT || typeSpan[i-1] == TYPE_SPAN_NONE || typeSpan[i-1] == TYPE_SPAN_CLOSE_BOTH))
							sameHeaderNext = false;
					}
				}
				
				
				if (sameHeaderPrevious && sameHeaderNext)
					typeSpan[i] = TYPE_SPAN_OPEN_BOTH;
				else if (sameHeaderPrevious)
					typeSpan[i] = TYPE_SPAN_OPEN_LEFT;
				else if (sameHeaderNext)
					typeSpan[i] = TYPE_SPAN_OPEN_RIGHT;
				
				else if (typeCombine == COMBINE_HEADERS_FIRST_LINE_ALWAYS ||
						 typeCombine == COMBINE_HEADERS_ANY_LINE_ALWAYS) {
	
					typeSpan[i] = TYPE_SPAN_CLOSE_BOTH;
				}
			}
		}
	
		return typeSpan;
	}
	
	public void paint(Graphics g) {
	
		super.paint(g);
	
		if (hasColSpan())
			paintColSpan(g);
	}
	
	public void paintComponent(Graphics g) {
	
		//Pintamos el fondo de la cabecera de la columna
		if (gradientBackround)
			GraphicsUtils.paintGradientBackground(g, this, getBackground());
		else		
			GraphicsUtils.paintBackground(g, this, getBackground());
		
		//Si no tiene colspan dejamos que el UI pinte el texto y los bordes por defecto
		//Si tiene colSpan, pintaremos los bordes y los textos en paint(Graphics)
		if (!hasColSpan()) 
			getUI().paint(g, this);
	}
	
	public boolean hasColSpan() {
		for (int i = 0; i < getTypeSpan().length; i++) {
			if (getTypeSpan()[i] != TYPE_SPAN_NONE)
				return true;
		}
		return false;
	}
	
	protected void paintColSpan(Graphics g) {
	
		Object lTableHeader = usingDefaultRender ? null : UtilsGUI.getParent(this, LTableHeader.class);
		int lineWithoutColSpan = 0;
		int heightLine = getFontMetrics(getFont()).getHeight();

		for (int i = 0; i < getTypeSpan().length && lineWithoutColSpan == 0; i++) {
		
			int typeSpan = getTypeSpan()[i];
			
			if (typeSpan != TYPE_SPAN_NONE) {
	
				int x = 0;
				int y = heightLine*i;
				int w = getWidth();
				int h = heightLine;
				Rectangle rectsLabelSpan = new Rectangle(x, y, w, h);
				
				//Si la tabla tiene un LTableHeader pintaremos el texto en el para que los textos queden centrados respecto a todas las columnas,
				//pero si la tabla no tiene un LTableHeader se pintará aquí
				//Cada columna tiene que tener su propio render, por lo que si estamos usando el defaultRender para todas no podremos pintar los textos en LTableHeader
				String text = (lTableHeader == null && typeSpan == TYPE_SPAN_OPEN_RIGHT) ? getLines()[i] : Constants.VOID;
				paintLabelSpan(g, rectsLabelSpan, typeSpan, text);
			}
			else {
				lineWithoutColSpan = i;
			}
		}
		
		//Pintamos siempre el texto sin colSpan
		StringBuffer sb = new StringBuffer();
		sb.append(getLines()[lineWithoutColSpan]);
		for (int i = lineWithoutColSpan+1; i < getLines().length; i++) {
			sb.append(Constants.LINE_SEPARATOR);
			sb.append(getLines()[i]);
		}
		
		int x = 0;
		int y = lineWithoutColSpan*heightLine;
		int w = getWidth();
		int h = getHeight() - y;
		Rectangle rectsLabelSpan = new Rectangle(x, y, w, h);
		
		paintLabelSpan(g, rectsLabelSpan, TYPE_SPAN_NONE, sb.toString(), columnForeground, columnBackground);
	}
	
	protected void paintLabelSpan(Graphics g, Rectangle rectsLabel, int typeSpan, String text) {
		paintLabelSpan(g, rectsLabel, typeSpan, text, null, null);
	}
	protected void paintLabelSpan(Graphics g, Rectangle rectsLabel, int typeSpan, String text, Color foreground, Color background) {
		
		int topBorder = 0;
		int leftBorder = 0;
		int bottomBorder = 0;
		int rightBorder = 0;
		
		if (typeSpan == TYPE_SPAN_NONE) {
			if (!isLastColumn)
				rightBorder = 1;
		}
		else {	
			bottomBorder = 1;
			if (typeSpan == TYPE_SPAN_OPEN_LEFT && !isLastColumn)
				rightBorder = 1;
		}
		
		JLabel label = new JLabel(text);
		label.setForeground(foreground != null ? foreground : getForeground());
		label.setBackground(background != null ? background : getBackground());
		label.setFont(getFont());
		label.setBorder(BorderFactory.createMatteBorder(topBorder, leftBorder, bottomBorder, rightBorder, ColorsGUI.getHeaderGridColor()));
		label.setBounds(rectsLabel);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		if (background != null) {
			if (gradientBackround) {
				Paint paint = GraphicsUtils.getGradientPaint(getBounds(), background, GraphicsUtils.GRADIENT_TYPE_VERTICAL);
				GraphicsUtils.fillRect(g, rectsLabel, paint, 0);
			}
			else {
				g.setColor(background);
				GraphicsUtils.fillRect(g, rectsLabel, GraphicsUtils.GRADIENT_TYPE_NONE);
			}
		}
		
		Graphics cg = g.create(rectsLabel.x, rectsLabel.y, rectsLabel.width, rectsLabel.height);
		label.paint(cg);
	}
	
	private void setLines(String[] lines) {
		this.lines = lines;
	}
	
	public void setOrder(int order) {
		
		ImageIcon icon = null;
		
		switch (order)
		{
			case ORDER_ORIGINAL:
			break;
			case ORDER_ASCENT:
				icon = Icons.SORT_ASCEN;
			break;
			case ORDER_DESCENT:
				icon = Icons.SORT_DESCEN;
			break;
		}
		setIcon(icon);
		
		this.order = order;
		
	}
	
	private void setTypeSpan(int[] typeSpan) {
		this.typeSpan = typeSpan;
	}
	public Border getBorder() {
	    return super.getBorder();
	}
	public void setBorder(Border border) {
		super.setBorder(border);
	}
}
