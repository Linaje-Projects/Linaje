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
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ButtonModel;
import javax.swing.ImageIcon;

import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.StateColor;
import linaje.utils.StateValues;

/**
 * Permite asignar en un solo icono los diferentes iconos que se mostrarán según el estado del componente al que lo asignemos
 * Es como un StateColor pero con iconos y al igual que éste se rige por las normas de StatesValues para asignar
 * un valor no establecido en un determinado estado mediante el modo MODE_DEFINED_VALUES o MODE_DEFINED_VALUES_NON_NULL
 * 
 * Permite crear un StateIcon en base a un icono normal y un StateColor, de forma que se coloree el icono según el StateColor
 * 
 * @see StateValues
 * @see StateColor
 **/
@SuppressWarnings("serial")
public class StateIcon extends ImageIcon implements Cloneable {

	private StateValues<ImageIcon> stateValues = null;
	private static final Image EMPTY_IMAGE = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
	//
	//State colors constructors
	//
	public StateIcon(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon selectedIcon, ImageIcon rolloverIcon, ImageIcon pressedIcon) {
		this(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, null, pressedIcon, null, null, StateValues.MODE_DEFINED_VALUES_NON_NULL);
	}
	public StateIcon(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon selectedIcon, ImageIcon rolloverIcon, ImageIcon rolloverSelectedIcon, ImageIcon pressedIcon, ImageIcon pressedSelectedIcon, ImageIcon disabledSelectedIcon) {
		this(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon, pressedSelectedIcon, disabledSelectedIcon, StateValues.MODE_DEFINED_VALUES_NON_NULL);
	}
	public StateIcon(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon selectedIcon, ImageIcon rolloverIcon, ImageIcon rolloverSelectedIcon, ImageIcon pressedIcon, ImageIcon pressedSelectedIcon, ImageIcon disabledSelectedIcon, int mode) {
		super(getImage(defaultIcon));
		stateValues = new StateValues<ImageIcon>(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon, pressedSelectedIcon, disabledSelectedIcon, mode);
	}
	public StateIcon(ImageIcon baseIcon, Color baseColor, boolean brighterStateIcons) {
		this(baseIcon, new StateColor(baseColor, brighterStateIcons));
	}
	public StateIcon(ImageIcon baseIcon, Color baseColor, boolean brighterStateIcons, float factor) {
		this(baseIcon, new StateColor(baseColor, brighterStateIcons, factor));
	}
	public StateIcon(ImageIcon baseIcon, StateColor stateColor) {
		this(baseIcon, stateColor, true);
	}
	public StateIcon(ImageIcon baseIcon, StateColor stateColor, boolean colorizeDefaultIcon) {
		super(getImage(baseIcon));
		initIcons(baseIcon, stateColor, colorizeDefaultIcon);
	}
	
	@Override
	public StateIcon clone() {
		try {
			return (StateIcon) super.clone();
		}
		catch (CloneNotSupportedException ex) {
			Console.printException(ex);
			return null;
		}
	}
	
	private static final Image getImage(ImageIcon imageIcon) {
		if (imageIcon != null)
			return imageIcon.getImage() != null ? imageIcon.getImage() : Icons.getImage(imageIcon);
		else
			return EMPTY_IMAGE;
	}
	
	public void initIcons(ImageIcon baseIcon, StateColor stateColor, boolean colorizeDefaultIcon) {
		
		ImageIcon defaultIcon = colorizeDefaultIcon ? Icons.getColorizedIcon(baseIcon, stateColor.getDefaultColor()) : baseIcon;
		ImageIcon disabledIcon = Icons.getColorizedIcon(baseIcon, stateColor.getDisabledColor());
		ImageIcon selectedIcon = Icons.getColorizedIcon(baseIcon, stateColor.getSelectedColor()); 
		ImageIcon rolloverIcon = Icons.getColorizedIcon(baseIcon, stateColor.getRolloverColor()); 
		ImageIcon rolloverSelectedIcon = Icons.getColorizedIcon(baseIcon, stateColor.getRolloverSelectedColor());
		ImageIcon pressedIcon = Icons.getColorizedIcon(baseIcon, stateColor.getPressedColor()); 
		ImageIcon pressedSelectedIcon = Icons.getColorizedIcon(baseIcon, stateColor.getPressedSelectedColor());
		int mode = stateColor.getMode();
		
		stateValues = new StateValues<ImageIcon>(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon, pressedSelectedIcon, null, mode);
	}
	
	public StateIcon(StateIcon stateIcon, ImageIcon newMainIcon) {
		this(newMainIcon != null ? newMainIcon : stateIcon.getStateValues().getDefaultValue(),
			stateIcon.getStateValues().getDisabledValue(),
			stateIcon.getStateValues().getPressedValue(),
			stateIcon.getStateValues().getRolloverValue(),
			stateIcon.getStateValues().getSelectedValue(),
			stateIcon.getStateValues().getRolloverSelectedValue(),
			stateIcon.getStateValues().getPressedSelectedValue(),
			stateIcon.getStateValues().getDisabledSelectedValue(),
			stateIcon.getStateValues().getMode());
	}
	
	public ImageIcon getStateValue(ButtonModel buttonModel) {
		
		if (buttonModel == null)
			return getDefaultIcon();
		
		boolean disabled = !buttonModel.isEnabled();
		boolean pressed = buttonModel.isPressed() && buttonModel.isArmed();
		boolean rollover = buttonModel.isRollover();
		boolean selected = buttonModel.isSelected();
		
		return getStateValue(disabled, pressed, rollover, selected);
	}
	public ImageIcon getStateValue(boolean disabled, boolean pressed, boolean rollover, boolean selected) {
		return getStateValues().getStateValue(disabled, pressed, rollover, selected);
	}
	
	public String getIconName() {
		
		String iconName;
		File iconFile = new File(getDescription());
		if (iconFile.exists()) {
			
			String fileName = iconFile.getName();
			int indexNameEnd = fileName.lastIndexOf(Constants.MINUS);
			if (indexNameEnd == -1)
				indexNameEnd = fileName.lastIndexOf(Constants.POINT);
			
			iconName = indexNameEnd != -1 ? fileName.substring(0, indexNameEnd) : fileName;
		}
		else {
			iconName = "Icon";
		}
		
		return iconName;
	}
	
	public StateIcon getScaledInstance(float widthScale, float heightScale) {
		
		ImageIcon defaultIcon = Icons.getScaledIcon(this, widthScale, heightScale);
		ImageIcon disabledIcon = getDisabledIcon() != null ? Icons.getScaledIcon(getDisabledIcon(), widthScale, heightScale) : null;
		ImageIcon selectedIcon = getSelectedIcon() != null ? Icons.getScaledIcon(getSelectedIcon(), widthScale, heightScale) : null;
		ImageIcon rolloverIcon = getRolloverIcon() != null ? Icons.getScaledIcon(getRolloverIcon(), widthScale, heightScale) : null;
		ImageIcon rolloverSelectedIcon = getRolloverSelectedIcon() != null ? Icons.getScaledIcon(getRolloverSelectedIcon(), widthScale, heightScale) : null;
		ImageIcon pressedIcon = getPressedIcon() != null ? Icons.getScaledIcon(getPressedIcon(), widthScale, heightScale) : null;
		ImageIcon pressedSelectedIcon = getPressedSelectedIcon() != null ? Icons.getScaledIcon(getPressedSelectedIcon(), widthScale, heightScale) : null;
		ImageIcon disabledSelectedIcon = getDisabledSelectedIcon() != null ? Icons.getScaledIcon(getDisabledSelectedIcon(), widthScale, heightScale) : null;
		int mode = getMode();
		
		return new StateIcon(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon, pressedSelectedIcon, disabledSelectedIcon, mode);
	}

	public StateIcon getScaledInstance(int width, int height) {
		
		ImageIcon defaultIcon = Icons.getScaledIcon(this, width, height);
		ImageIcon disabledIcon = getDisabledIcon() != null ? Icons.getScaledIcon(getDisabledIcon(), width, height) : null;
		ImageIcon selectedIcon = getSelectedIcon() != null ? Icons.getScaledIcon(getSelectedIcon(), width, height) : null;
		ImageIcon rolloverIcon = getRolloverIcon() != null ? Icons.getScaledIcon(getRolloverIcon(), width, height) : null;
		ImageIcon rolloverSelectedIcon = getRolloverSelectedIcon() != null ? Icons.getScaledIcon(getRolloverSelectedIcon(), width, height) : null;
		ImageIcon pressedIcon = getPressedIcon() != null ? Icons.getScaledIcon(getPressedIcon(), width, height) : null;
		ImageIcon pressedSelectedIcon = getPressedSelectedIcon() != null ? Icons.getScaledIcon(getPressedSelectedIcon(), width, height) : null;
		ImageIcon disabledSelectedIcon = getDisabledSelectedIcon() != null ? Icons.getScaledIcon(getDisabledSelectedIcon(), width, height) : null;
		int mode = getMode();
		
		return new StateIcon(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon, pressedSelectedIcon, disabledSelectedIcon, mode);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		if (getImage() == null && getDefaultIcon() != null)
			getDefaultIcon().paintIcon(c, g, x, y);
		else
			super.paintIcon(c, g, x, y);
	}
	@Override
	public int getIconWidth() {
		if (getImage() == null && getDefaultIcon() != null)
			return getDefaultIcon().getIconWidth();
		else
			return super.getIconWidth();
	}
	@Override
	public int getIconHeight() {
		if (getImage() == null && getDefaultIcon() != null)
			return getDefaultIcon().getIconHeight();
		else
			return super.getIconHeight();
	}
	
	//GETTERS
	public StateValues<ImageIcon> getStateValues() {
		if (stateValues == null)
			stateValues = new StateValues<ImageIcon>(this);
		return stateValues;
	}
	
	public ImageIcon getDefaultIcon() {
		return getStateValues().getDefaultValue();
	}
	public ImageIcon getDisabledIcon() {
		return getStateValues().getDisabledValue();
	}
	public ImageIcon getPressedIcon() {
		return getStateValues().getPressedValue();
	}
	public ImageIcon getRolloverIcon() {
		return getStateValues().getRolloverValue();
	}
	public ImageIcon getSelectedIcon() {
		return getStateValues().getSelectedValue();
	}
	public ImageIcon getDisabledSelectedIcon() {
		return getStateValues().getDisabledSelectedValue();
	}
	public ImageIcon getPressedSelectedIcon() {
		return getStateValues().getPressedSelectedValue();
	}
	public ImageIcon getRolloverSelectedIcon() {
		return getStateValues().getRolloverSelectedValue();
	}
	public int getMode() {
		return getStateValues().getMode();
	}
	
	//SETTERS
	public void setDefaultIcon(ImageIcon defaultIcon) {
		setImage(getImage(defaultIcon));
		getStateValues().setDefaultValue(defaultIcon);
	}
	public void setDisabledIcon(ImageIcon disabledIcon) {
		getStateValues().setDisabledValue(disabledIcon);
	}
	public void setPressedIcon(ImageIcon pressedIcon) {
		getStateValues().setPressedValue(pressedIcon);
	}
	public void setRolloverIcon(ImageIcon rolloverIcon) {
		getStateValues().setRolloverValue(rolloverIcon);
	}
	public void setSelectedIcon(ImageIcon selectedIcon) {
		getStateValues().setSelectedValue(selectedIcon);
	}
	public void setDisabledSelectedIcon(ImageIcon disabledSelectedIcon) {
		getStateValues().setDisabledSelectedValue(disabledSelectedIcon);
	}
	public void setPressedSelectedIcon(ImageIcon pressedSelectedIcon) {
		getStateValues().setPressedSelectedValue(pressedSelectedIcon);
	}
	public void setRolloverSelectedIcon(ImageIcon rolloverSelectedIcon) {
		getStateValues().setRolloverSelectedValue(rolloverSelectedIcon);
	}
	public void setMode(int mode) {
		getStateValues().setMode(mode);
	}
}
