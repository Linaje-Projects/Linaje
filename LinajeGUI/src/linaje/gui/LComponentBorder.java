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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.utils.Colors;

/**
 * Borde por defecto de muchos de los componentes visuales al usar LinajeLookAndFeel
 * Se pintará de una forma u otra dinámicamente dependiendo del componente que lo implemente y del estado de dicho componente
 **/
@SuppressWarnings("serial")
public class LComponentBorder extends RoundedBorder implements UIResource {

	public LComponentBorder() {
		super();
		setThicknessShadow(0);
		setCornersCurveSize(new Dimension(6, 6));
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		
		if (isBorderVisible(c)) {
			
			boolean isPressed = false;
			boolean isDefault = false;
			boolean isRollover = false;
			boolean isSelected = false;

			ButtonModel model = null;
			AbstractButton b = null;
			if (c instanceof AbstractButton) {
				
				b = (AbstractButton) c;
				model = b.getModel();
	
				boolean isToggleButton = c instanceof JToggleButton;
						
				isPressed = c instanceof JMenuItem || (!isToggleButton && model.isPressed() && model.isArmed());
				isRollover = model.isRollover() && b.isRolloverEnabled();
				isSelected = model.isSelected();
				
				if (c instanceof JButton) {
					isDefault = !isRollover && ((JButton) c).isDefaultButton();
				}
				else {
					isDefault = !isRollover && !isSelected && b.hasFocus();
				}
				
				Container parent = c.getParent();
				if (!c.isOpaque() && parent != null && parent instanceof JToolBar) {
					//En una toolbar sólo se pintará el borde cuando se esté interactuando con el botón
					boolean borderVisible = isRollover || (isToggleButton && isSelected);
					if (!borderVisible)
						return;
				}
				//setAnchoSombraInterior(isSelected ? 1 : 0);
				setThicknessInnerShadow(0);
			}
			else {
				isDefault = c.hasFocus();
				if (c instanceof JTextComponent) {
					setThicknessInnerShadow(1);
					isSelected = true;
				}
				else {
					isPressed = true;
				}
			}
			
			Color bgColor = b == null ? ColorsGUI.getFirstOpaqueParentBackground(c) : UISupportButtons.getBackground(b);
			
			boolean paintClassicBorder = false;
			if (paintClassicBorder) {
				paintClassicBorder(g, x, y, width, height, bgColor, isPressed, isSelected, isDefault);
			}
			else {
				Color borderColor = ColorsGUI.getColorBorder();
				Color shadowColor = bgColor;//bgColor para que no se vea
				setLineBorderColor(null, null, null, null);
				if (!c.isEnabled()) {
					boolean backgroundIsDark = Colors.isColorDark(bgColor);
					borderColor = backgroundIsDark ? Colors.brighter(bgColor, 0.32) : Colors.darker(bgColor, 0.32);
					if (isRollover)
						shadowColor = ColorsGUI.getColorApp();
				}
				else if (b != null) {
					if (!isSelected)
						shadowColor = ColorsGUI.getColorShadow();
					if (isDefault)
						bgColor = ColorsGUI.getColorApp();
					Color lineShadowColor = Colors.isColorDark(bgColor) ? Colors.darker(bgColor, 0.3) : Colors.darker(bgColor, 0.4);//ColorsGUI.getColorShadow(bgColor, isDefault);
					Color lineLightColor = Colors.isColorDark(bgColor) ? Colors.brighter(bgColor, 0.1) : Colors.darker(bgColor, 0.2);//ColorsGUI.getColorLight(bgColor);
					if (isPressed) {
						if (Colors.isColorDark(bgColor))
							lineShadowColor = lineLightColor;
						else
							lineLightColor = lineShadowColor;
					}
					if (isSelected)
						setLineBorderColor(lineShadowColor, lineShadowColor, lineLightColor, lineLightColor);
					else
						setLineBorderColor(lineLightColor, lineLightColor, lineShadowColor, lineShadowColor);
				}
				else if (c instanceof JTextComponent) {
					JTextComponent textComponent = (JTextComponent) c;
					borderColor = Colors.isColorDark(bgColor) ? Colors.brighter(bgColor, 0.1) : Colors.darker(bgColor, 0.1);
					int validateState = c instanceof LTextField ? ((LTextField) c).getValidateState() : -1;
					if (validateState != -1) {
						if (validateState == LTextField.VALIDATE_NO)
							borderColor = ColorsGUI.getColorNegative();
						else if (validateState == LTextField.VALIDATE_YES)
							borderColor = ColorsGUI.getColorPositive();
						else
							validateState = -1;
					}
					
					if (isDefault && textComponent.isEditable()) {
						if (validateState != -1)
							borderColor = Colors.isColorDark(borderColor) ? Colors.brighter(borderColor, 0.2) : Colors.darker(borderColor, 0.2);
						else
							borderColor = ColorsGUI.getColorApp();
					}
				}
				else if (c instanceof JComboBox && isDefault) {
					borderColor = ColorsGUI.getColorApp();
				}
				setShadowColor(shadowColor);				
				setLineBorderColor(borderColor);
				super.paintBorder(c, g, x, y, width, height);
			}
		}
	}

	private boolean isBorderVisible(Component c) {
		return c != null;
	}
	
	public static void paintClassicBorder(Graphics g, int x, int y, int width, int height, Color buttonBackground, boolean isPressed, boolean isSelected, boolean isDefault) {
		
		Color shadowColor = ColorsGUI.getColorShadow(buttonBackground, isDefault);
		Color lightColor = ColorsGUI.getColorLight(buttonBackground);
		if (isPressed) {
			if (Colors.isColorDark(buttonBackground))
				shadowColor = lightColor;
			else
				lightColor = shadowColor;
		}
		
		if (isSelected)
			GraphicsUtils.paintBorderColors(g, new Rectangle(x, y, width, height), shadowColor, shadowColor, lightColor, lightColor);
		else
			GraphicsUtils.paintBorderColors(g, new Rectangle(x, y, width, height), lightColor, lightColor, shadowColor, shadowColor);
	}
	
	public static void paintRoundedBorder(Graphics g, int x, int y, int width, int height, Color buttonBackground, boolean isPressed, boolean isSelected, boolean isDefault) {
		
		Color shadowColor = ColorsGUI.getColorShadow(buttonBackground, isDefault);
		Color lightColor = ColorsGUI.getColorLight(buttonBackground);
		if (isPressed) {
			if (Colors.isColorDark(buttonBackground))
				shadowColor = lightColor;
			else
				lightColor = shadowColor;
		}
		
		if (isSelected)
			GraphicsUtils.paintBorderColors(g, new Rectangle(x, y, width, height), shadowColor, shadowColor, lightColor, lightColor);
		else
			GraphicsUtils.paintBorderColors(g, new Rectangle(x, y, width, height), lightColor, lightColor, shadowColor, shadowColor);
	}
	/*public Insets getBorderInsets(Component c, Insets insets) {
		int thick = isBorderVisible(c) ? 1 : 0;
		insets.set(thick, thick, thick, thick);
		return insets;
	}*/
}
