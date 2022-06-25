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
package linaje.gui;

import java.awt.*;

import javax.swing.*;

import linaje.gui.utils.GraphicsUtils;

/**
 * Añadidos respecto a un JMenuBar:
 * 	- Se puede poner el fondo de la barra degradado
 **/
@SuppressWarnings("serial")
public class LMenuBar extends JMenuBar {

	private boolean gradientBackground = false;
	
	public LMenuBar() {
		super();
	}
	
	public void paintComponent(Graphics g) {
	
		if (isGradientBackground()) {
	
			GraphicsUtils.paintGradientBackground(g, this, getBackground());
			getUI().paint(g, this);
		}
		else super.paintComponent(g);
	}
	
	public boolean isGradientBackground() {	
		return gradientBackground;
	}
	public void setGradientBackground(boolean gradientBackground) {
		this.gradientBackground = gradientBackground;
	}
}
