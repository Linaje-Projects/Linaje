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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;

public class LTextAreaUI extends BasicTextAreaUI {

	FocusListener focusListener = new FocusListener() {
		public void focusLost(FocusEvent e) {
			getComponent().repaint();
		}
		public void focusGained(FocusEvent e) {
			getComponent().repaint();
		}
	};
	public LTextAreaUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent c) {
        return new LTextAreaUI();
    }
	
	@Override
	protected void installDefaults() {
		super.installDefaults();
		getComponent().addFocusListener(focusListener);
	}
	
	@Override
	protected void uninstallDefaults() {
		getComponent().removeFocusListener(focusListener);
		super.uninstallDefaults();
	}
}
