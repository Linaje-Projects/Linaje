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
package linaje.utils;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import linaje.logs.Console;
import linaje.statics.Constants;


/**
 * Nos permite unificar en una sola clase todos los posibles colores de los estados de un componente
 **/
@SuppressWarnings("serial")
public class StateColor extends Color implements Cloneable {

	private StateValues<Color> stateValues = null;
	private static final String VALUE_FIELD_NAME = "value";
	
	//El factor y el modo lo necesitaremos a nivel de clase para codificar y decodificar el stateColor
	/*private static final int COLORS_MODE_DEFAULT = 0;
	private static final int COLORS_MODE_BRIGHTER_COLORS = 1;
	private static final int COLORS_MODE_DARKER_COLORS = 2;
	private int colorsMode = COLORS_MODE_DEFAULT;
	private float factor = 0f;*/
	
	//
	//State colors constructors
	//
	public StateColor() {
		this(Color.white);
	}
	public StateColor(Color defaultColor) {
		this(defaultColor, null, null, null, null, null, null, null);
	}
	public StateColor(Color defaultColor, Color selectedColor, Color rolloverColor) {
		this(defaultColor, null, selectedColor, rolloverColor, null, null, null, null, StateValues.MODE_DEFINED_VALUES_NON_NULL);
	}
	public StateColor(Color defaultColor, Color disabledColor, Color selectedColor, Color rolloverColor, Color rolloverSelectedColor, Color pressedColor, Color pressedSelectedColor, Color disabledSelectedColor) {
		this(defaultColor, disabledColor, selectedColor, rolloverColor, rolloverSelectedColor, pressedColor, pressedSelectedColor, disabledSelectedColor, StateValues.MODE_DEFINED_VALUES_NON_NULL);
	}
	public StateColor(Color defaultColor, Color disabledColor, Color selectedColor, Color rolloverColor, Color rolloverSelectedColor, Color pressedColor, Color pressedSelectedColor, Color disabledSelectedColor, int mode) {
		super(defaultColor.getRed(), defaultColor.getGreen(), defaultColor.getBlue(), defaultColor.getAlpha());
		//this.factor = 0f;
		//this.colorsMode = COLORS_MODE_DEFAULT;
		reloadReferencedColors(defaultColor, disabledColor, selectedColor, rolloverColor, rolloverSelectedColor, pressedColor, pressedSelectedColor, disabledSelectedColor);
		stateValues = new StateValues<Color>(defaultColor, disabledColor, selectedColor, rolloverColor, rolloverSelectedColor, pressedColor, pressedSelectedColor, disabledSelectedColor, mode);
		initialize();
	}
	public StateColor(Color baseColor, boolean brighterStateColors) {
		this(baseColor, brighterStateColors, brighterStateColors ? (float) 0.9 : (float) 0.7);
	}
	public StateColor(Color baseColor, boolean brighterStateColors, float factor) {
		super(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), baseColor.getAlpha());
		//this.factor = factor;
		//this.colorsMode = brighterStateColors ? COLORS_MODE_BRIGHTER_COLORS : COLORS_MODE_DARKER_COLORS;
		reloadReferencedColors(baseColor);
		Color rolloverColor = brighterStateColors ? Colors.brighter(baseColor, factor) : Colors.darker(baseColor, factor);
		Color pressedColor = brighterStateColors ? null : Colors.darker(rolloverColor, 0.1);
		Color rolloverSelectedColor = brighterStateColors ? null : Colors.darker(baseColor, 0.1);
		Color pressedSelectedColor = brighterStateColors ? null : Colors.darker(rolloverSelectedColor, 0.1);
		stateValues = new StateValues<Color>(baseColor, null, null, rolloverColor, rolloverSelectedColor, pressedColor, pressedSelectedColor, null);
		initialize();
	}
	
	
	public StateColor(StateColor stateColor, Color newMainColor) {
		this(newMainColor != null ? newMainColor : stateColor.getStateValues().getDefaultValue(),
			stateColor.getStateValues().getDisabledValue(),
			stateColor.getStateValues().getSelectedValue(),
			stateColor.getStateValues().getRolloverValue(),
			stateColor.getStateValues().getRolloverSelectedValue(),
			stateColor.getStateValues().getPressedValue(),
			stateColor.getStateValues().getPressedSelectedValue(),
			stateColor.getStateValues().getDisabledSelectedValue(),
			stateColor.getStateValues().getMode());
	}
	
	private void reloadReferencedColors(Color... colors) {
		for (Color color : colors) {
			if (color instanceof ReferencedColor && color != this) {
				((ReferencedColor) color).reload();
			}
		}
	}
	private void initialize() {
		getStateValues().addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(StateValues.PROPERTY_defaultValue) ){
					updateBaseColor((Color) evt.getNewValue(), false);
				}
			}
		});
		if (getDefaultColor() instanceof ReferencedColor) {
			((ReferencedColor) getDefaultColor()).reload();
			updateBaseColor(getDefaultColor(), false);
		}
	}
	
	public Color getStateValue(boolean disabled, boolean pressed, boolean rollover, boolean selected) {
		return getStateValues().getStateValue(disabled, pressed, rollover, selected);
	}
	
	@Override
	public StateColor clone() {
		try {
			return new StateColor(this, getDefaultColor());
		} catch (Exception e) {
			try {
				return (StateColor) super.clone();
			} catch (CloneNotSupportedException e2) {
				return null;
			}
		}
	}
	
	@Override
	public int getRGB() {
		return super.getRGB();
	}
	
	/*
	 * No implementamos equals ya que sino funcionan mal los colorChoosers y otras clases del framework de Java
	 * @Override
	public boolean equals(Object obj) {
		boolean equals = super.equals(obj);
		if (equals)
			equals = obj.toString().equals(this.toString());
		return equals;
	}*/
	
	public String encode() {
		
		HashMap<String, String> encodedFieldValuesMap = new LinkedHashMap<>();
		
		int mode = getStateValues().getMode();
		if (mode != StateValues.MODE_DEFINED_VALUES_NON_NULL) {
			encodedFieldValuesMap.put("mode", Integer.toString(mode));
		}
		
		HashMap<String, Color> colors = getStateValues().getValues();
		for (Iterator<Entry<String, Color>> iterator = colors.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Color> entry = iterator.next();
			String fieldName = entry.getKey();
			Color color = entry.getValue();
			if (color != null) {
				encodedFieldValuesMap.put(fieldName, Colors.encode(color));
			}
		}	
		
		return Utils.encodeFieldValuesMap(encodedFieldValuesMap, getClass(), Constants.SEMICOLON);
	}
	
	public static StateColor decode(String encodedStateColor) {
		
		int mode = StateValues.MODE_DEFINED_VALUES_NON_NULL;
		Color defaultColor = null;
		HashMap<String, Color> values = new LinkedHashMap<>();
		
		HashMap<String, String> encodedFieldValuesMap = Utils.decodeFieldValuesMap(encodedStateColor, Constants.SEMICOLON);
		for (Iterator<Entry<String, String>> iterator = encodedFieldValuesMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> entry = iterator.next();
			String fieldName = entry.getKey();
			String encodedValue = entry.getValue();
			if (fieldName.equals(StateValues.PROPERTY_mode)) {
				mode = Integer.parseInt(encodedValue);
			}
			else {
				Color color = Colors.decode(encodedValue);
				values.put(fieldName, color);
				if (fieldName.equals(StateValues.PROPERTY_defaultValue)) {
					defaultColor = color;
				}
			}
		}
		
		StateColor stateColor = new StateColor(defaultColor, null, null, null, null, null, null, null, mode);
		stateColor.getStateValues().setValues(values);
		
		return stateColor;
	}
	
	private void updateBaseColor(Color defaultColor, boolean updateStateValue) {
		try {
			int rgb = defaultColor.getRGB();
			Field valueField = Color.class.getDeclaredField(VALUE_FIELD_NAME);
			valueField.setAccessible(true);
			valueField.setInt(this, rgb);
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
		finally {
			if (updateStateValue)
				getStateValues().setDefaultValue(defaultColor);
		}
	}
	
	//GETTERS
	public StateValues<Color> getStateValues() {
		if (stateValues == null)
			stateValues = new StateValues<Color>(this);
		return stateValues;
	}
	
	public Color getDefaultColor() {
		return getStateValues().getDefaultValue();
	}
	public Color getDisabledColor() {
		return getStateValues().getDisabledValue();
	}
	public Color getPressedColor() {
		return getStateValues().getPressedValue();
	}
	public Color getRolloverColor() {
		return getStateValues().getRolloverValue();
	}
	public Color getSelectedColor() {
		return getStateValues().getSelectedValue();
	}
	public Color getDisabledSelectedColor() {
		return getStateValues().getDisabledSelectedValue();
	}
	public Color getPressedSelectedColor() {
		return getStateValues().getPressedSelectedValue();
	}
	public Color getRolloverSelectedColor() {
		return getStateValues().getRolloverSelectedValue();
	}
	public int getMode() {
		return getStateValues().getMode();
	}
	
	//SETTERS
	public void setDefaultColor(Color defaultColor) {
		if (defaultColor == null)
			defaultColor = Color.white;
		getStateValues().setDefaultValue(defaultColor);
	}
		
	public void setDisabledColor(Color disabledColor) {
		getStateValues().setDisabledValue(disabledColor);
	}
	public void setPressedColor(Color pressedColor) {
		getStateValues().setPressedValue(pressedColor);
	}
	public void setRolloverColor(Color rolloverColor) {
		getStateValues().setRolloverValue(rolloverColor);
	}
	public void setSelectedColor(Color selectedColor) {
		getStateValues().setSelectedValue(selectedColor);
	}
	public void setDisabledSelectedColor(Color disabledSelectedColor) {
		getStateValues().setDisabledSelectedValue(disabledSelectedColor);
	}
	public void setPressedSelectedColor(Color pressedSelectedColor) {
		getStateValues().setPressedSelectedValue(pressedSelectedColor);
	}
	public void setRolloverSelectedColor(Color rolloverSelectedColor) {
		getStateValues().setRolloverSelectedValue(rolloverSelectedColor);
	}
	public void setMode(int mode) {
		getStateValues().setMode(mode);
	}
	
	@Override
	public String toString() {
		return encode();
	}
	
	public void reload() {
		updateBaseColor(getDefaultColor(), false);
	}
	
	public static StateColor getReferencedStateColor(StateColor sourceStateColor, Field sourceStateColorField, Object fieldOwner) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		StateColor referencedStateColor = null;
		if (sourceStateColor != null) {
			
			Field stateValuesField = StateColor.class.getDeclaredField("stateValues");
			
			StateValues<Color> stateValues = sourceStateColor.getStateValues();
			ReferencedColor defaultColor = new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_defaultValue)}, 0);
			ReferencedColor disabledColor = stateValues.getDisabledValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_disabledValue)}, 0)  : null;
			ReferencedColor selectedColor = stateValues.getSelectedValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_selectedValue)}, 0)  : null;
			ReferencedColor rolloverColor = stateValues.getRolloverValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_rolloverValue)}, 0)  : null;
			ReferencedColor rolloverSelectedColor = stateValues.getRolloverSelectedValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_rolloverSelectedValue)}, 0)  : null;
			ReferencedColor pressedColor = stateValues.getPressedValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_pressedValue)}, 0)  : null;
			ReferencedColor pressedSelectedColor = stateValues.getPressedSelectedValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_pressedSelectedValue)}, 0)  : null;
			ReferencedColor disabledSelectedColor = stateValues.getDisabledSelectedValue() != null ? new ReferencedColor(fieldOwner, new Field[]{sourceStateColorField, stateValuesField, StateValues.class.getDeclaredField(StateValues.PROPERTY_disabledSelectedValue)}, 0)  : null;
			int mode = stateValues.getMode();
			
			referencedStateColor = new StateColor(defaultColor, disabledColor, selectedColor, rolloverColor, rolloverSelectedColor, pressedColor, pressedSelectedColor, disabledSelectedColor, mode);
		}
		return referencedStateColor;
	}
}
