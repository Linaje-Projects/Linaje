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

import linaje.gui.ui.UISupportButtons;

import javax.swing.Icon;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import linaje.gui.ui.LRadioButtonUI;
import linaje.gui.ui.LToggleButtonUI;
import linaje.gui.utils.UtilsGUI;
import linaje.utils.LFont;
import linaje.utils.StateColor;

/**
 * Añadidos respecto a un JRadioButton:
 * 	- LButtonPropertable, por lo que podemos cambiar sus propiedades de LButtonProperties individualmente
 *  - Métodos de acceso a las propiedades de la fuente del texto
 *  - Posibilidad de pintarse con aspecto de toggleButton
 **/
@SuppressWarnings("serial")
public class LRadioButton extends JRadioButton implements LButtonPropertable {

	private LButtonProperties buttonProperties;
	private Icon defaultIcon;
	
	public LRadioButton() {
		super();
		initialize();
	}
	public LRadioButton(String text) {
		super(text);
		initialize();
	}
	public LRadioButton(String text, Icon icon) {
		super(text, icon);
		initialize();
	}
	public LRadioButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		initialize();
	}
	public LRadioButton(String text, boolean selected) {
		super(text, selected);
		initialize();
	}
	public LRadioButton(Icon icon) {
		super(icon);
		initialize();
	}
	public LRadioButton(Icon icon, boolean selected) {
		super(icon, selected);
		initialize();
	}
	
	public LButtonProperties getButtonProperties() {
		if (buttonProperties == null)
			buttonProperties = LButtonProperties.createButtonPropertiesUIBased(this);
		return buttonProperties;
	}
	
	public String getFontName() {
		return getFont().getName();
	}
	public int getFontSize() {
		return getFont().getSize();
	}
	public int getFontStyle() {
		return getFont().getStyle();
	}
	public int getFontLayout() {
		return getFont() instanceof LFont ? ((LFont) getFont()).getLayoutMode() : LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setTooltipEnabled(true);
	}
	
	public void setFontName(String fontName) {
		UtilsGUI.setFontName(this, fontName);
	}
	public void setFontSize(int fontSize) {
		UtilsGUI.setFontSize(this, fontSize);
	}
	public void setFontStyle(int fontStyle) {
		UtilsGUI.setFontStyle(this, fontStyle);
	}
	public void setFontLayout(int fontLayout) {
		UtilsGUI.setFontLayout(this, fontLayout);
	}
	
	public void setTooltipEnabled(boolean enabled) {
		if (enabled)
			ToolTip.getInstance().registerComponent(this);
		else
			ToolTip.getInstance().unRegisterComponent(this);
	}
	
	@Override
	public void setIcon(Icon icon) {
		if (defaultIcon == null)
			defaultIcon = icon;
		else if (icon == null)
			icon = defaultIcon;
		super.setIcon(icon);
	}
	
	public boolean isToggleAspect() {
		return getUI() instanceof LToggleButtonUI;
	}
	public void setToggleAspect(boolean toggleAspect) {
		setUI(toggleAspect ? new LToggleButtonUI() : new LRadioButtonUI());
		setOpaque(toggleAspect);
		String borderUIKey = toggleAspect ? "ToggleButton.border" : "RadioButton.border";
		setBorder(UIManager.getBorder(borderUIKey));
		setBorderPainted(toggleAspect);
		StateColor defaultStateBackground = UISupportButtons.getDefaultUIStateBackground(this);
		StateColor defaultStateForeground = UISupportButtons.getDefaultUIStateForeground(this);
		setBackground(defaultStateBackground.clone());
		setForeground(defaultStateForeground.clone());
	}
}
