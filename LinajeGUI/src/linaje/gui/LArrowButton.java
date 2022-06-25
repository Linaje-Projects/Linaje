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
import java.io.Serializable;

import javax.swing.*;

import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.GraphicsUtils;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.StateColor;

/**
 * Añadidos respecto a un BasicArrowButton:
 * 	- LButtonPropertable, por lo que podemos cambiar sus propiedades de LButtonProperties individualmente
 *  - Métodos de acceso a las propiedades de la fuente del texto
 *  - Permite cambiar el color de la flecha
 *  - Efectos de rollover 
 **/
@SuppressWarnings("serial")
public class LArrowButton extends LButton {

	private int direction = SOUTH;
	
	private class LArrowButtonIcon implements Icon, Serializable {

		public void paintIcon(Component c, Graphics g, int x, int y) {
			
			ButtonModel model = getModel();
			
			LButtonProperties buttonProperties = getButtonProperties();
			
			Color markColor;
			if (model.isEnabled()) {
				markColor = buttonProperties.getMarkColor() != null ? buttonProperties.getMarkColor() : UISupportButtons.getForeground(LArrowButton.this);
				markColor = UISupportButtons.getStateColorValue(getModel(), markColor);
			}
			else {
				markColor = UIManager.getColor("controlShadow");
			}
			
			GraphicsUtils.paintTriangle(g, x, y, getIconWidth(), isEnabled(), markColor, getDirection(), model.isRollover());
		}

		public int getIconWidth() {
			return getIconSize();
		}

		public int getIconHeight() {
			return getIconSize();
		}
	}
	
	public LArrowButton() {
		super();
		initialize();
	}
	public LArrowButton(int direction) {
		super();
		initialize();
		setDirection(direction);
	}
	public LArrowButton(String text) {
		super(text);
		initialize();
	}
	public LArrowButton(String text, Icon icon) {
		super(text, icon);
		initialize();
	}
	public LArrowButton(Action a) {
		super(a);
		initialize();
	}
	public LArrowButton(Icon icon) {
		super(icon);
		initialize();
	}
	public Color getArrowColor() {
		return getButtonProperties().getMarkColor();
	}
	public int getDirection() {
		return direction;
	}
	
	public int getIconSize() {
		
		if (getText().equals(Constants.VOID) && getButtonProperties().isIgnoreIconHeight()) {
			
			int w, h, size;
			
			w = getSize().width;
			h = getSize().height;
			
			if (w%2 != 0)
				w++;
			if (h%2 != 0)
				h++;
			
			// If there's no room to draw arrow, bail
			if (h < 5 || w < 5) {
				return 0;
			}
		
			size = Math.min(h/2, w/2);
			size = Math.max(size, 2);
			
			return size;
		}
		else {
			return (int)(getFontMetrics(getFont()).getHeight() / 2);
		}
	}
	
	private void initialize() {
		
		setText(Constants.VOID);
		getButtonProperties().setIgnoreIconHeight(true);
		
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
		
		Color defaultArrowColor = generalUIProperties.getColorText();
		Color rolloverArrowColor = generalUIProperties.getColorApp();
		Color pressedArrowColor = Colors.brighter(generalUIProperties.getColorApp(), 0.1);
		
		setArrowColor(new StateColor(defaultArrowColor, null, null, rolloverArrowColor, null, pressedArrowColor, null, null));
		setIcon(new LArrowButtonIcon());
	}
	
	public void setArrowColor(Color arrowColor) {
		getButtonProperties().setMarkColor(arrowColor);
	}

	public void setDirection(int newDirection) {
		direction = newDirection;
	}
}
