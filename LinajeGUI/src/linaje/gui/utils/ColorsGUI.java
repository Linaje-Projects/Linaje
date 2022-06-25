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
package linaje.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Window;

import javax.swing.ButtonModel;

import linaje.gui.Icons;
import linaje.gui.LPanel;
import linaje.gui.ui.GeneralUIProperties;
import linaje.utils.Colors;
import linaje.utils.StateColor;

public final class ColorsGUI {

	public static final Color BLUE = new Color(1,122,153);//Textos
	public static final Color BLUE_BRIGHT = new Color(138,205,222);
	
	public static final Color YELLOW = new Color(255,205,0);
	public static final Color YELLOW_DARK = new Color(247,193,5);//Textos
	public static final Color YELLOW_BRIGHT = new Color(254, 255, 123);
	
	public static final Color GREEN = new Color(168,201,95);
	public static final Color GREEN_DARK = new Color(0, 128, 0);//Verde oscuro//Verde (menos chillón que el puro)
	public static final Color GREEN_BRIGHT = new Color(242,245,215);
	
	public static final Color RED = new Color(181,3,2);//Color negativo / Mensaje error
	public static final Color RED_DARK = new Color(128, 0, 0);//Textos
	
	public static final Color ORANGE = new Color(255,158,27);//Color apoyo
	public static final Color ORANGE_BRIGHT = new Color(255,214,165);
		
	public static final Color ROW_EVEN_BASE_COLOR = new Color(247, 245, 243);
	
	//// COLOR UTILITIES ////
	
	public static Color getFirstOpaqueParentBackground(Component component) {
		Color color = null;
		if (component != null) {
			if (component.isOpaque())
				color = component.getBackground();
			else if (component instanceof LPanel) {
				LPanel lPanel = (LPanel) component;
				if (lPanel.getOpacity() < 1) {
					Color lPanelColor = lPanel.getBackground();
					color = new Color(lPanelColor.getRed(), lPanelColor.getGreen(), lPanelColor.getBlue(), (int)(lPanel.getOpacity()*255));
				}
			}
		}
		
		if (color == null && component != null && !(component instanceof Window)) {
			Container parent = component.getParent();
			if (parent != null)
				return getFirstOpaqueParentBackground(parent);
		}	
		return color;
	}
	
	public static Image getColorizedImage(Image image, Color color) {
		return Icons.createColorizedImage(image, color);
	}
	
	public static Color getStateValue(StateColor stateColor, ButtonModel buttonModel) {
		
		if (stateColor == null)
			return null;
		else if (buttonModel == null)
			return stateColor;
		
		boolean disabled = !buttonModel.isEnabled();
		boolean pressed = buttonModel.isPressed() && buttonModel.isArmed();
		boolean rollover = buttonModel.isRollover();
		boolean selected = buttonModel.isSelected();
		
		return stateColor.getStateValue(disabled, pressed, rollover, selected);
	}
	
	//// UI CALCULATED COLORS ////
	
	public static Color getColorTextCode() {
		return getColorPositive();
	}
	
	public static Color getColorTextError() {
		return getColorNegative();
	}
	
	public static Color getGridColor(Color background) {
		double factor = 0.15;//0.06;
		return Colors.isColorDark(background) ? Colors.brighter(background, factor) : Colors.darker(background, factor);
	}
	
	public static Color getHeaderGridColor() {
		return ColorsGUI.getColorPanels();
	}
	
	public static Color getColorRowEvenDefault(Color background) {
		return getColorRowEven(getColorRowOddDefault(background));
	}
	public static Color getColorRowOddDefault(Color background) {
		return background != null ? background : getColorPanelsBrightest();
	}
	
	public static Color getColorRowEven(Color colorRowOdd) {
		if (colorRowOdd == null)
			colorRowOdd = getColorRowOddDefault(null);
		//return Colors.isColorDark(colorRowOdd) ? Colors.brighter(colorRowOdd, 0.02) : Colors.darker(colorRowOdd, 0.02);
		return Colors.colorize(colorRowOdd, ROW_EVEN_BASE_COLOR);
	}
	
	public static Color getColorTextDisabled(Color textBackground) {
		
		if (textBackground == null)
			textBackground = getColorPanels();
		
		boolean backgroundIsDark = Colors.isColorDark(textBackground);
		
	    return backgroundIsDark ? Colors.brighter(textBackground, 0.22) : Colors.darker(textBackground, 0.32);
	}
	
	public static Color getColorShadow(Color background) {
		return getColorShadow(background, false);
	}
	public static Color getColorShadow(Color background, boolean isDefault) {
		return Colors.darker(background, isDefault ? 0.5 : 0.4);
	}
	public static Color getColorLight(Color background) {
		return Colors.brighter(background, 0.1);
	}
	
	//// UI DEFINED COLORS ////
	
	public static Color getColorApp() {
		return GeneralUIProperties.getInstance().getColorApp();
	}
	public static Color getColorAppDark() {
		return GeneralUIProperties.getInstance().getColorAppDark();
	}
	
	public static Color getColorPanels() {
		return GeneralUIProperties.getInstance().getColorPanels();
	}
	public static Color getColorPanelsBright() {
		return GeneralUIProperties.getInstance().getColorPanelsBright();
	}
	public static Color getColorPanelsBrightest() {
		return GeneralUIProperties.getInstance().getColorPanelsBrightest();
	}
	public static Color getColorPanelsDark() {
		return GeneralUIProperties.getInstance().getColorPanelsDark();
	}
	public static Color getColorPanelsDarkest() {
		return GeneralUIProperties.getInstance().getColorPanelsDarkest();
	}
	
	public static Color getColorImportant() {
		return GeneralUIProperties.getInstance().getColorImportant();
	}
	public static Color getColorInfo() {
		return GeneralUIProperties.getInstance().getColorInfo();
	}
	public static Color getColorWarning() {
		return GeneralUIProperties.getInstance().getColorWarning();
	}
	public static Color getColorPositive() {
		return GeneralUIProperties.getInstance().getColorPositive();
	}
	public static Color getColorNegative() {
		return GeneralUIProperties.getInstance().getColorNegative();
	}
	
	public static Color getColorText() {
		return GeneralUIProperties.getInstance().getColorText();
	}
	public static Color getColorTextBright() {
		return GeneralUIProperties.getInstance().getColorTextBright();
	}
	public static Color getColorTextBrightest() {
		return GeneralUIProperties.getInstance().getColorTextBrightest();
	}
	
	public static Color getColorBorder() {
		return GeneralUIProperties.getInstance().getColorBorder();
	}
	public static Color getColorBorderBright() {
		return GeneralUIProperties.getInstance().getColorBorderBright();
	}
	public static Color getColorBorderDark() {
		return GeneralUIProperties.getInstance().getColorBorderDark();
	}
	
	public static Color getColorRollover() {
		return GeneralUIProperties.getInstance().getColorRollover();
	}
	public static Color getColorRolloverDark() {
		return GeneralUIProperties.getInstance().getColorRolloverDark();
	}
	
	public static Color getColorShadow() {
		return GeneralUIProperties.getInstance().getColorShadow();
	}
	
	public static Color getColorTip() {
		return getColorPanelsBrightest();
	}
}
