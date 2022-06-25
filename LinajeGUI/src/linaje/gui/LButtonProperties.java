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
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;

import javax.swing.AbstractButton;
import javax.swing.UIManager;

import sun.awt.AppContext;
import linaje.gui.ui.UISupportButtons;
import linaje.logs.Console;
import linaje.utils.FieldsChangeSupport;
import linaje.utils.FieldsChangesNotifier;
import linaje.utils.Utils;

public class LButtonProperties implements FieldsChangesNotifier, Cloneable {

	public static final int SHADOW_TEXT_MODE_NEVER = 0;
	public static final int SHADOW_TEXT_MODE_ALWAYS = 1;
	public static final int SHADOW_TEXT_MODE_ON_ROLLOVER = 2;
	public static final int SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND = 3;
	
	private boolean gradientBackgroundEnabled = true;
	private boolean iconForegroundEnabled = false;
	private boolean respectMaxMinSize = true;//si se pone a true, getPreferredSize() no se podrá salir de los márgenes de getMinSize() y getMaxSize()
	private boolean ignoreIconHeight = true;
	private boolean markExpanded = true;
	private boolean paintBgEffectsWhenTransparent = true;
	
	private int shadowTextMode = SHADOW_TEXT_MODE_NEVER;
	//Si no definimos posición, la posición de la sombra cambiará según sea rollover o no (Ver UISupportButtons.getShadowPosition(LButtonProperties buttonProperties, ButtonModel buttonModel))
	private int shadowPosition = -1;
	private int pressedSelectedOffset = 1;
	
	private Color lineBackgroundColor = null;
	private Color markColor = null;
	
	private FieldsChangeSupport fieldsChangeSupport = null;
	
	public static final String PROPERTY_gradientBackgroundEnabled = "gradientBackgroundEnabled";
	public static final String PROPERTY_iconForegroundEnabled = "iconForegroundEnabled";
	public static final String PROPERTY_respectMaxMinSize = "respectMaxMinSize";
	public static final String PROPERTY_ignoreIconHeight = "ignoreIconHeight";
	public static final String PROPERTY_markExpanded = "markExpanded";
	public static final String PROPERTY_shadowTextMode = "shadowTextMode";
	public static final String PROPERTY_shadowPosition = "shadowPosition";
	public static final String PROPERTY_pressedSelectedOffset = "pressedSelectedOffset";
	public static final String PROPERTY_lineBackgroundColor = "lineBackgroundColor";
	public static final String PROPERTY_markColor = "markColor";
	public static final String PROPERTY_paintBgEffectsWhenTransparent = "paintBgEffectsWhenTransparent";
	
	public LButtonProperties() {
		super();
	}

	public static LButtonProperties createButtonPropertiesUIBased(AbstractButton b) {
		return UISupportButtons.getDefaultUIButtonProperties(b).clone();
	}
	
	public static LButtonProperties getDefaultButtonProperties(String uiPropertyPrefix) {
		
		AppContext appContext = AppContext.getAppContext();
		String bpKey = uiPropertyPrefix + LButtonProperties.class.getSimpleName();
		
		LButtonProperties defaultButtonProperties = (LButtonProperties) appContext.get(bpKey);
		if (defaultButtonProperties == null) {
			
			defaultButtonProperties = new LButtonProperties();
			Field[] fields = LButtonProperties.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					String fieldKey = uiPropertyPrefix + fields[i].getName();
					Object value = UIManager.get(fieldKey);
					if (value != null)
						fields[i].set(defaultButtonProperties, value);
				}
				catch (Exception ex) {
				}
			}
		}
	
		return defaultButtonProperties;
	}
	
	@Override
	public LButtonProperties clone() {
		try {
			LButtonProperties clonedButtonProperties = (LButtonProperties) super.clone();
			clonedButtonProperties.fieldsChangeSupport = null;
			return clonedButtonProperties;
		}
		catch (CloneNotSupportedException ex) {
			Console.printException(ex);
			return null;
		}
	}
	
	//
	// PropertyChange field methods
	//
	
	public FieldsChangeSupport getFieldsChangeSupport() {
		if (fieldsChangeSupport == null)
			fieldsChangeSupport = new FieldsChangeSupport(this);
		return fieldsChangeSupport;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getFieldsChangeSupport().addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getFieldsChangeSupport().addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getFieldsChangeSupport().removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getFieldsChangeSupport().removePropertyChangeListener(propertyName, listener);
	}

	private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getFieldsChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void resetDefaultFieldsValues() {
		getFieldsChangeSupport().resetDefaultFieldsValues();
	}
	
	//GETTERS
	public boolean isGradientBackgroundEnabled() {
		return gradientBackgroundEnabled;
	}
	public boolean isIconForegroundEnabled() {
		return iconForegroundEnabled;
	}
	public boolean isRespectMaxMinSize() {
		return respectMaxMinSize;
	}
	public boolean isIgnoreIconHeight() {
		return ignoreIconHeight;
	}
	public boolean isMarkExpanded() {
		return markExpanded;
	}
	public boolean isPaintBgEffectsWhenTransparent() {
		return paintBgEffectsWhenTransparent;
	}

	public int getShadowTextMode() {
		return shadowTextMode;
	}
	public int getShadowPosition() {
		return shadowPosition;
	}
	public int getPressedSelectedOffset() {
		return pressedSelectedOffset;
	}
	public Color getLineBackgroundColor() {
		return lineBackgroundColor;
	}
	public Color getMarkColor() {
		return markColor;
	}
	
	//SETTERS
	public void setGradientBackgroundEnabled(boolean gradientBackgroundEnabled) {
		boolean oldValue = this.gradientBackgroundEnabled;
		boolean newValue = gradientBackgroundEnabled;
		if (oldValue != newValue) {
			this.gradientBackgroundEnabled = gradientBackgroundEnabled;
			firePropertyChange(PROPERTY_gradientBackgroundEnabled, oldValue, newValue);
		}
	}
	public void setIconForegroundEnabled(boolean iconForegroundEnabled) {
		boolean oldValue = this.iconForegroundEnabled;
		boolean newValue = iconForegroundEnabled;
		if (oldValue != newValue) {
			this.iconForegroundEnabled = iconForegroundEnabled;
			firePropertyChange(PROPERTY_iconForegroundEnabled, oldValue, newValue);
		}
	}
	public void setRespectMaxMinSize(boolean respectMaxMinSize) {
		boolean oldValue = this.respectMaxMinSize;
		boolean newValue = respectMaxMinSize;
		if (oldValue != newValue) {
			this.respectMaxMinSize = respectMaxMinSize;
			firePropertyChange(PROPERTY_respectMaxMinSize, oldValue, newValue);
		}
	}
	public void setIgnoreIconHeight(boolean ignoreIconHeight) {
		boolean oldValue = this.ignoreIconHeight;
		boolean newValue = ignoreIconHeight;
		if (oldValue != newValue) {
			this.ignoreIconHeight = ignoreIconHeight;
			firePropertyChange(PROPERTY_ignoreIconHeight, oldValue, newValue);
		}
	}
	public void setMarkExpanded(boolean markExpanded) {
		boolean oldValue = this.markExpanded;
		boolean newValue = markExpanded;
		if (oldValue != newValue) {
			this.markExpanded = markExpanded;
			firePropertyChange(PROPERTY_markExpanded, oldValue, newValue);
		}
	}
	public void setPaintBgEffectsWhenTransparent(boolean paintBgEffectsWhenTransparent) {
		boolean oldValue = this.paintBgEffectsWhenTransparent;
		boolean newValue = paintBgEffectsWhenTransparent;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.paintBgEffectsWhenTransparent = paintBgEffectsWhenTransparent;
			firePropertyChange(PROPERTY_paintBgEffectsWhenTransparent, oldValue, newValue);
		}
	}
	
	public void setShadowTextMode(int shadowTextMode) {
		int oldValue = this.shadowTextMode;
		int newValue = shadowTextMode;
		if (oldValue != newValue) {
			this.shadowTextMode = shadowTextMode;
			firePropertyChange(PROPERTY_shadowTextMode, oldValue, newValue);
		}
	}
	public void setShadowPosition(int shadowPosition) {
		int oldValue = this.shadowPosition;
		int newValue = shadowPosition;
		if (oldValue != newValue) {
			this.shadowPosition = shadowPosition;
			firePropertyChange(PROPERTY_shadowPosition, oldValue, newValue);
		}
	}
	public void setPressedSelectedOffset(int pressedSelectedOffset) {
		int oldValue = this.pressedSelectedOffset;
		int newValue = pressedSelectedOffset;
		if (oldValue != newValue) {
			this.pressedSelectedOffset = pressedSelectedOffset;
			firePropertyChange(PROPERTY_pressedSelectedOffset, oldValue, newValue);
		}
	}
	public void setLineBackgroundColor(Color lineBackgroundColor) {
		Color oldValue = this.lineBackgroundColor;
		Color newValue = lineBackgroundColor;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.lineBackgroundColor = lineBackgroundColor;
			firePropertyChange(PROPERTY_lineBackgroundColor, oldValue, newValue);
		}
	}
	public void setMarkColor(Color markColor) {
		Color oldValue = this.markColor;
		Color newValue = markColor;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.markColor = markColor;
			firePropertyChange(PROPERTY_markColor, oldValue, newValue);
		}
	}
}
