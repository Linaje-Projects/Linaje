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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.RepaintManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import linaje.gui.LList;
import linaje.gui.StateIcon;
import linaje.gui.ToolTip;
import linaje.gui.cells.LabelCell;
import linaje.gui.utils.UtilsGUI;
import linaje.tree.TreeNodeVector;
import linaje.utils.Colors;

@SuppressWarnings("serial")
public class LCellRenderer<E> extends LabelCell implements ListCellRenderer<E>, TableCellRenderer, TreeCellRenderer {
	
	private boolean obscureEvenRows = true;
	private Point mouseLocation = null;
	
	private LabelCell labelCell = null;
	
	public LCellRenderer() {
		super();
		initialize();
	}
	public LCellRenderer(int type) {
		super(type);
		initialize();
	}
	public LCellRenderer(boolean indentEnabled) {
		super(indentEnabled);
		initialize();
	}
	public LCellRenderer(boolean indentEnabled, boolean descOverCode) {	
		super(indentEnabled, descOverCode);
		initialize();
	}
	
	private void initialize() {
		//setForeground(GeneralUIProperties.getInstance().getColorText());
	}
	private void initLabelCell() {
		
		if (labelCell == null || labelCell.getType() != getType())
			labelCell = new LabelCell(getType());
		
		labelCell.copyPropertiesFrom(this);
	}
	
	public LabelCell getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
			
		initLabelCell();
		
		if (index == -1)
			isSelected = false;
		labelCell.setEnabled(list.isEnabled());
		labelCell.setFont(list.getFont());
		labelCell.setForeground(list.getForeground());
		Color background = list.getBackground();
		labelCell = initLabelCell(labelCell, value, isSelected, background);
		
		Point mousePosition = null;
		if (list instanceof LList) {
			LList<? extends E> llist = (LList<? extends E>) list;
			if (index == llist.getIndexOver()) {
				mousePosition = llist.getMousePositionRelativeToCell();
			}
		}
		
		setMouseLocation(mousePosition);
		
		if (isMouseOver() && isSelectable() && (!isSelected || getType() == TYPE_CHECKBOX)) {
			if (getType() == TYPE_CHECKBOX) {
				Icon icon = labelCell.getIcon();
				if (icon != null && icon instanceof StateIcon)
					labelCell.setIcon(((StateIcon) icon).getRolloverIcon());
			}				
			else {
				Color bg = Colors.isColorDark(background) ? Colors.brighter(background, 0.05) : Colors.darker(background, 0.05);
				labelCell.setBackground(bg);
			}
		}
		
		labelCell.setOpaque(true);
		
		if (isSelected && (labelCell.isSelectedBackgroundGradient() || labelCell.getSelectedBorderColor() != null)) {
			Rectangle selectionRects = UtilsGUI.getSelectionRects(list);
			labelCell.setSelectionRects(selectionRects);
			RepaintManager.currentManager(list).addDirtyRegion(list, selectionRects.x, selectionRects.y, selectionRects.width, selectionRects.height);
		}
		
		if (!ToolTip.getInstance().getRegisteredComponents().contains(list)) {
			ToolTip.getInstance().registerComponent(list);
		}
		
		return labelCell;
	}
	
	public LabelCell getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
		ToolTip.getInstance().registerComponent(table);
		
		initLabelCell();
	

		labelCell.setEnabled(table.isEnabled());
		labelCell.setFont(table.getFont());
		labelCell.setForeground(table.getForeground());
		Color background = table.getBackground();
		if (isObscureEvenRows()) {
			if (row % 2 != 0) {
				//Oscurecemos las filas impares
				background = Colors.isColorDark(background) ? Colors.brighter(background, 0.02) : Colors.darker(background, 0.02);
			}
		}
		/*
		@SuppressWarnings("unchecked")
		LTableObject<E> tableObject = (value instanceof LTableObject) ? (LTableObject<E>) value : null;
		if (tableObject != null)
			value = tableObject.getValue();
		*/
		labelCell = initLabelCell(labelCell, value, isSelected, background);
		labelCell.setOpaque(true);
		
		if (isSelected && (labelCell.isSelectedBackgroundGradient() || labelCell.getSelectedBorderColor() != null)) {
			Rectangle selectionRects = UtilsGUI.getSelectionRects(table);
			labelCell.setSelectionRects(selectionRects);
			if (selectionRects != null)
				RepaintManager.currentManager(table).addDirtyRegion(table, selectionRects.x, selectionRects.y, selectionRects.width, selectionRects.height);
		}
		
		return labelCell;
	}
	
	@SuppressWarnings("rawtypes")
	public LabelCell getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	
		Object nodeUserObject = null;
		
		if (value != null) {
			if (value instanceof TreeNodeVector)
				nodeUserObject = ((TreeNodeVector) value).getUserObject();
			else if (value instanceof DefaultMutableTreeNode)
				nodeUserObject = ((DefaultMutableTreeNode) value).getUserObject();
		}
		
		initLabelCell();
	
		if (row == -1)
			isSelected = false;
		labelCell.setEnabled(tree.isEnabled());
		labelCell.setFont(tree.getFont());
		labelCell.setForeground(tree.getForeground());
		Color background = tree.getBackground();
		labelCell = initLabelCell(labelCell, nodeUserObject, isSelected, background);
		
		labelCell.setOpaque(true);
		
		if (isSelected && (labelCell.isSelectedBackgroundGradient() || labelCell.getSelectedBorderColor() != null)) {
			Rectangle selectionRects = UtilsGUI.getSelectionRects(tree);
			labelCell.setSelectionRects(selectionRects);
			RepaintManager.currentManager(tree).addDirtyRegion(tree, selectionRects.x, selectionRects.y, selectionRects.width, selectionRects.height);
		}
		
		//if (!ToolTip.getInstance().getRegisteredComponents().contains(tree)) {
		//	ToolTip.getInstance().registerComponent(tree);
		//}
		
		return labelCell;
	}
	
	public boolean isObscureEvenRows() {
		return obscureEvenRows;
	}
	public void setObscureEvenRows(boolean obscureEvenRows) {
		this.obscureEvenRows = obscureEvenRows;
	}
	protected boolean isMouseOver() {
		return getMouseLocation() != null;
	}
	protected Point getMouseLocation() {
		return mouseLocation;
	}
	private void setMouseLocation(Point mouseLocation) {
		this.mouseLocation = mouseLocation;
	}
}
