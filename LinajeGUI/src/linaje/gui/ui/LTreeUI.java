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
package linaje.gui.ui;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import linaje.gui.renderers.LCellRenderer;

public class LTreeUI extends BasicTreeUI {

	public LTreeUI() {
		super();
	}

	public static ComponentUI createUI(JComponent x) {
        return new LTreeUI();
    }

	@Override
	protected void installDefaults() {
		super.installDefaults();
		
		 if(tree.getForeground() == null ||
            tree.getForeground() instanceof UIResource) {
            tree.setForeground(UIManager.getColor("Tree.foreground"));
        }
		int rowHeight = tree.getFontMetrics(tree.getFont()).getHeight() + 2;
		LookAndFeel.installProperty(tree, "rowHeight", rowHeight);
	}
	
	protected TreeCellRenderer createDefaultCellRenderer() {
        return new LCellRenderer<>();
    }
	
	@Override
	protected void paintHorizontalLine(Graphics g, JComponent c, int y,
			int left, int right) {
		//super.paintHorizontalLine(g, c, y, left, right);
	}
	@Override
	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
		//super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
	}
	@Override
	protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
		//super.paintVerticalLine(g, c, x, top, bottom);
	}
	@Override
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
		//super.paintVerticalPartOfLeg(g, clipBounds, insets, path);
	}
}
