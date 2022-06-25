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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class LToolBarUI extends BasicToolBarUI {

	public LToolBarUI() {
		super();
	}

	public static ComponentUI createUI(JComponent c) {
		return new LToolBarUI();
	}

	@Override
	protected void setBorderToNonRollover(Component c) {
		//super.setBorderToNonRollover(c);
	}
	
	@Override
	protected void setBorderToNormal(Component c) {
		//super.setBorderToNormal(c);
	}
	
	@Override
	protected void setBorderToRollover(Component c) {
		//super.setBorderToRollover(c);
	}
	
	@Override
	public void setRolloverBorders(boolean rollover) {
		//super.setRolloverBorders(rollover);
	}
	
	@Override
	protected void installNonRolloverBorders(JComponent c) {
		//super.installNonRolloverBorders(c);
	}
	
	@Override
	protected void installNormalBorders(JComponent c) {
		//super.installNormalBorders(c);
	}
	
	@Override
	protected void installRolloverBorders(JComponent c) {
		//super.installRolloverBorders(c);
	}
}
