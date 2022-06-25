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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import linaje.gui.LArrowButton;
import linaje.gui.RoundedBorder;
import linaje.gui.ToolTip;
import linaje.gui.renderers.LCellRenderer;

public class LComboUI extends BasicComboBoxUI {
	
	public LComboUI() {
		super();
	}
	
	protected JButton createArrowButton() {
		
		LArrowButton arrowButton = new LArrowButton();
		arrowButton.setBorder(BorderFactory.createEmptyBorder());
		arrowButton.setFocusPainted(false);
		
		return arrowButton;
	}
	
	@SuppressWarnings("rawtypes")
	protected ListCellRenderer createRenderer() {
        return new LCellRenderer<>();
    }
	
	public LArrowButton getArrowButton() {
		return (LArrowButton) arrowButton;
	}
	
	public static ComponentUI createUI(JComponent c) {
        return new LComboUI();
    }
	
	@Override
	protected void installComponents() {
		super.installComponents();
		
		ToolTip.getInstance().registerComponent(comboBox);
	}
	
	@SuppressWarnings("serial")
	@Override
	protected ComboPopup createPopup() {
		final RoundedBorder popupBorder = new RoundedBorder();
		popupBorder.setPaintInsideAlways(true);
		popupBorder.setThicknessInsetsExtra(-1);
		BasicComboPopup popup = new BasicComboPopup(comboBox) {
			@Override
			public void show(Component invoker, int x, int y) {
								
				Dimension scrollSize = scroller.getPreferredSize();
				//scrollSize.height = scrollSize.height - 1;
				scrollSize.width = scrollSize.width + popupBorder.getThicknessShadow();
				
				scroller.setMaximumSize(scrollSize);
				scroller.setPreferredSize(scrollSize);
				scroller.setMinimumSize(scrollSize);
				scroller.setOpaque(true);

				list.revalidate();
								
				super.show(invoker, x, y);
			}
		};
		popup.setBorder(popupBorder);
		//popup.setOpaque(true);
		
		return popup;
	}
	
	@Override
	public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
		super.paintCurrentValue(g, bounds, false);
	}
}