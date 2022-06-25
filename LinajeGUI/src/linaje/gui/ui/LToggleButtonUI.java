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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import sun.awt.AppContext;

public class LToggleButtonUI extends LButtonUI {

	protected static final Object L_TOGGLE_BUTTON_UI_KEY = new Object();

	public LToggleButtonUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent b) {
        AppContext appContext = AppContext.getAppContext();
        LToggleButtonUI lToggleButtonUI = (LToggleButtonUI) appContext.get(L_TOGGLE_BUTTON_UI_KEY);
        if (lToggleButtonUI == null) {
        	lToggleButtonUI = new LToggleButtonUI();
            appContext.put(L_TOGGLE_BUTTON_UI_KEY, lToggleButtonUI);
        }
        return lToggleButtonUI;
    }
}
