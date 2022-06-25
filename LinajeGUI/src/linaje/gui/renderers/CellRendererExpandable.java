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

import javax.swing.JTable;

import linaje.gui.Icons;
import linaje.gui.StateIcon;
import linaje.gui.cells.LabelCell;
import linaje.gui.table.LTable;
import linaje.gui.table.LTableObject;
import linaje.tree.TreeNodeVector;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class CellRendererExpandable<E> extends LCellRenderer<LTableObject<E>> {

	@Override
	public LabelCell getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		LabelCell labelCell = (LabelCell) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		labelCell.getActionsRects().removeAllElements();
		if (value != null && table instanceof LTable) {
			if (column == 0) {
				
				@SuppressWarnings("unchecked")
				LTable<E> lTable = (LTable<E>) table;
				TreeNodeVector<E> rowNode = lTable.getModel().getRows().elementAt(row);
				boolean nodeHasChildren = rowNode.getChildCount() > 0;
				StateIcon icon = nodeHasChildren ? (rowNode.isExpanded() ? Icons.STATEICON_ARROW_DOWN : Icons.STATEICON_ARROW_RIGHT) : Icons.STATEICON_VOID;
				labelCell.setIcon(icon);
				labelCell.setIndentEnabled(true);
				labelCell.setIndentText(Strings.getIndent(rowNode.getLevel()));
				
				if (nodeHasChildren) {
					
					if (lTable.getRowMouseOver() == row && lTable.getActionMouseOver() == 0)
						labelCell.setIcon(lTable.isActionPressed() && icon.getSelectedIcon() != null ? icon.getSelectedIcon() : icon.getRolloverIcon() != null ? icon.getRolloverIcon() : icon);
					
					labelCell.getActionsRects().add(labelCell.getBoundsIcon());
				}
			}
			else {
				labelCell.setIcon(null);
				labelCell.setIndentEnabled(false);
			}
		}
		return labelCell;
	}
}
