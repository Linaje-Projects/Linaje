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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import linaje.gui.ui.UISupport;

/**
 * Centralizamos la forma de pintar los colores de los iconos pintados con Graphics
 *  - Si queremos que se pinte con el foreground o stateForeground del componente al que se asigne, lo crearemos pasando color=null en el constructor
 * 		- 	Esto puede ser útil porque se pintará con el color exacto del foreground, 
 * 	  		en lugar de colorizarse en UISupport cuando iconforegroundEnabled = true, que es mas costoso y puede haber alguna ligera variación en el color final
 * - El disabledIcon se calcula a partir del background del componente, 
 *   en cuyo caso devuelve Color.black para que la colorización final sea fiel al color deseado (@see: LinajeLookAndFeel.getDisabledIcon(...))
 * - Implementa el método getImage(Component c) para poder calcular la imagen en base al color correcto cuando el componente está deshabilitado
 * - Si se inicializa con un color y se va a usar iconForegroundEnable, se recomienda usar un color cercano al negro
 **/
@SuppressWarnings("serial")
public class PaintedImageIcon extends ImageIcon {
	
	private Color color = null;
	private Color currentColor = null;
	
	public PaintedImageIcon(Color color) {
		super();
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Color getCurrentColor() {
		return currentColor != null ? currentColor : color != null ? color : Color.black;
	}
	
	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		updateCurrentColor(c);
		g.setColor(currentColor);
	}
	
	public Color updateCurrentColor(Component c) {
		currentColor = !c.isEnabled() ? Color.black : color != null ? color : c instanceof JComponent ? UISupport.getForeground((JComponent)c) : c.getForeground();
		return currentColor;
	}
	
	public Image getImage(Component c) {
		updateCurrentColor(c);
		return getImage();
	}
}
