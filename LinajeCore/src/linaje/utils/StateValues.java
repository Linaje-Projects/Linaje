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

import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import linaje.logs.Console;

/**
 * Nos permite unificar en un solo sitio los valores de los diferentes estados de un componente
 * 
 * @see linaje.utils.StateColor
 * @see linaje.gui.components.StateIcon
 **/
public class StateValues <T> implements FieldsChangesNotifier, Cloneable {

	private T defaultValue = null;
	private T disabledValue = null;
	private T pressedValue = null;
	private T rolloverValue = null;
	private T selectedValue = null;
	private T disabledSelectedValue = null;
	private T pressedSelectedValue = null;
	private T rolloverSelectedValue = null;
	
	public static final int MODE_DEFINED_VALUES = 0;
	public static final int MODE_DEFINED_VALUES_NON_NULL = 1;
	
	private int mode = MODE_DEFINED_VALUES_NON_NULL;
	
	private FieldsChangeSupport fieldsChangeSupport = null;
	
	public static final String PROPERTY_defaultValue = "defaultValue";
	public static final String PROPERTY_disabledValue = "disabledValue";
	public static final String PROPERTY_pressedValue = "pressedValue";
	public static final String PROPERTY_rolloverValue = "rolloverValue";
	public static final String PROPERTY_selectedValue = "selectedValue";
	public static final String PROPERTY_disabledSelectedValue = "disabledSelectedValue";
	public static final String PROPERTY_pressedSelectedValue = "pressedSelectedValue";
	public static final String PROPERTY_pressedSelectedOffset = "pressedSelectedOffset";
	public static final String PROPERTY_rolloverSelectedValue = "rolloverSelectedValue";
	public static final String PROPERTY_mode = "mode";
	
	//
	//StateValue constructors
	//
	public StateValues(T defaultValue) {
		this(defaultValue, null, null, null, null, null, null, null);
	}
	public StateValues(T defaultValue, int mode) {
		this(defaultValue, null, null, null, null, null, null, null, mode);
	}
	public StateValues(T defaultValue, T disabledValue, T selectedValue, T rolloverValue, T rolloverSelectedValue, T pressedValue, T pressedSelectedValue, T disabledSelectedValue) {
		this(defaultValue, disabledValue, selectedValue, rolloverValue, rolloverSelectedValue, pressedValue, pressedSelectedValue, disabledSelectedValue, MODE_DEFINED_VALUES_NON_NULL);
	}
	public StateValues(T defaultValue, T disabledValue, T selectedValue, T rolloverValue, T rolloverSelectedValue, T pressedValue, T pressedSelectedValue, T disabledSelectedValue, int mode) {
		this.defaultValue = defaultValue;
		this.disabledValue = disabledValue;
		this.pressedValue = pressedValue;
		this.rolloverValue = rolloverValue;
		this.selectedValue = selectedValue;
		this.disabledSelectedValue = disabledSelectedValue;
		this.rolloverSelectedValue = rolloverSelectedValue;
		this.pressedSelectedValue = pressedSelectedValue;
		this.mode = mode;
	}
	public StateValues(HashMap<String, T> values) {
		this(values, MODE_DEFINED_VALUES_NON_NULL);
	}
	public StateValues(HashMap<String, T> values, int mode) {
		setValues(values, true);
		setMode(mode);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public StateValues<T> clone() {
		try {
			return (StateValues<T>) super.clone();
		}
		catch (CloneNotSupportedException ex) {
			Console.printException(ex);
			return null;
		}
	}
	
	public T getStateValue(boolean disabled, boolean pressed, boolean rollover, boolean selected) {
		
		if (getMode() == MODE_DEFINED_VALUES)
			return getStateValueDefined(disabled, pressed, rollover, selected);
		else
			return getStateValueNonNull(disabled, pressed, rollover, selected);
	}
	/**
	 * Devuelve el valor exacto del estado pudiendo ser null
	 * En caso de que vengan varias a true la prioridad es las siguiente: disabled > pressed > rollover
	 */
	private T getStateValueDefined(boolean disabled, boolean pressed, boolean rollover, boolean selected) {
		
		T stateValue;
		if (disabled)
			stateValue = selected ? getDisabledSelectedValue() : getDisabledValue();
		else if (pressed)
			stateValue = selected ? getPressedSelectedValue() : getPressedValue();
		else if (rollover)
			stateValue = selected ? getRolloverSelectedValue() : getRolloverValue();
		else			
			stateValue = selected ? getSelectedValue() : getDefaultValue();
			
		return stateValue;
	}
	
	/**
	 * Devuelve el valor más aproximado del estado que no sea null
	 * En caso de que vengan varias a true la prioridad es las siguiente: disabled > pressed > rollover
	 * Si selected es true y no hay especificado selectedValue para ese estado, se devolverá el valor del estado prioriatrio "no selected"
	 */
	private T getStateValueNonNull(boolean disabled, boolean pressed, boolean rollover, boolean selected) {
		
		T stateValue = getStateValueDefined(disabled, pressed, rollover, selected);
		
		if (stateValue == null) {
			
			//Disabled no es compatible con pressed o rollover
			if (disabled) {
				if (selected) //disabledSelected -> disabled -> main
					stateValue = getDisabledSelectedValue() != null ? getDisabledSelectedValue() : getDisabledValue();
				else //disabled -> main
					stateValue = getDisabledValue();
			}
			else {
				
				if (selected) {
					if (pressed) //pressedSelected -> pressed -> rolloverSelected -> rollover -> selected -> main
						stateValue = getPressedSelectedValue() != null ? getPressedSelectedValue() : getPressedValue() != null ? getPressedValue() : getRolloverSelectedValue() != null ? getRolloverSelectedValue() : getRolloverValue() != null ? getRolloverValue() : getSelectedValue();
					else if (rollover) //rolloverSelected -> rollover -> selected -> main
						stateValue = getRolloverSelectedValue() != null ? getRolloverSelectedValue() : getRolloverValue() != null ? getRolloverValue() : getSelectedValue();
					else //selected -> main
						stateValue = getSelectedValue();
				}
				else {
					if (pressed) //pressed -> rollover -> main
						stateValue = getPressedValue() != null ? getPressedValue() : getRolloverValue();
					else if (rollover) //rollover -> main
						stateValue = getRolloverValue();
				}
			}
			
			if (stateValue == null)
				stateValue = getDefaultValue();
		}
		
		return stateValue;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, T> getValues() {
		
		LinkedHashMap<String, T> values = new LinkedHashMap <>();
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				Field field = fields[i];
				Class<?> fieldType = field.getType(); 
				if (!Modifier.isFinal(field.getModifiers()) && fieldType != int.class && fieldType != FieldsChangeSupport.class) {
					T value = (T) field.get(this);
					if (value != null) {
						String fieldName = field.getName();
						values.put(fieldName, value);
					}
				}
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
		return values;
	}
	
	public void setValues(HashMap<String, T> values) {
		setValues(values, false);
	}
	private void setValues(HashMap<String, T> values, boolean defaultValues) {
		if (values != null) {
			ReflectAccessSupport reflectAccessSupport = new ReflectAccessSupport(this);
			for (Iterator<Entry<String, T>> iterator = values.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, T> entry = iterator.next();
				String fieldName = entry.getKey();
				T value = entry.getValue();
				try {
					boolean useSetter = !defaultValues;
					reflectAccessSupport.setFieldValue(fieldName, value, useSetter);
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
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
	public T getDefaultValue() {
		return defaultValue;
	}
	public T getDisabledValue() {
		return disabledValue;
	}
	public T getPressedValue() {
		return pressedValue;
	}
	public T getRolloverValue() {
		return rolloverValue;
	}
	public T getSelectedValue() {
		return selectedValue;
	}
	public T getDisabledSelectedValue() {
		return disabledSelectedValue;
	}
	public T getPressedSelectedValue() {
		return pressedSelectedValue;
	}
	public T getRolloverSelectedValue() {
		return rolloverSelectedValue;
	}
	public int getMode() {
		return mode;
	}
	
	//SETTERS
	public void setDefaultValue(T defaultValue) {
		T oldValue = this.defaultValue;
		T newValue = defaultValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.defaultValue = defaultValue;
			firePropertyChange(PROPERTY_defaultValue, oldValue, newValue);
		}
	}
	public void setDisabledValue(T disabledValue) {
		T oldValue = this.disabledValue;
		T newValue = disabledValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.disabledValue = disabledValue;
			firePropertyChange(PROPERTY_disabledValue, oldValue, newValue);
		}
	}
	public void setPressedValue(T pressedValue) {
		T oldValue = this.pressedValue;
		T newValue = pressedValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.pressedValue = pressedValue;
			firePropertyChange(PROPERTY_pressedValue, oldValue, newValue);
		}
	}
	public void setRolloverValue(T rolloverValue) {
		T oldValue = this.rolloverValue;
		T newValue = rolloverValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.rolloverValue = rolloverValue;
			firePropertyChange(PROPERTY_rolloverValue, oldValue, newValue);
		}
	}
	public void setSelectedValue(T selectedValue) {
		T oldValue = this.selectedValue;
		T newValue = selectedValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.selectedValue = selectedValue;
			firePropertyChange(PROPERTY_selectedValue, oldValue, newValue);
		}
	}
	public void setDisabledSelectedValue(T disabledSelectedValue) {
		T oldValue = this.disabledSelectedValue;
		T newValue = disabledSelectedValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.disabledSelectedValue = disabledSelectedValue;
			firePropertyChange(PROPERTY_disabledSelectedValue, oldValue, newValue);
		}
	}
	public void setPressedSelectedValue(T pressedSelectedValue) {
		T oldValue = this.pressedSelectedValue;
		T newValue = pressedSelectedValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.pressedSelectedValue = pressedSelectedValue;
			firePropertyChange(PROPERTY_pressedSelectedValue, oldValue, newValue);
		}
	}
	public void setRolloverSelectedValue(T rolloverSelectedValue) {
		T oldValue = this.rolloverSelectedValue;
		T newValue = rolloverSelectedValue;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.rolloverSelectedValue = rolloverSelectedValue;
			firePropertyChange(PROPERTY_rolloverSelectedValue, oldValue, newValue);
		}
	}
	public void setMode(int mode) {
		int oldValue = this.mode;
		int newValue = mode;
		if (oldValue != newValue) {
			this.mode = mode;
			firePropertyChange(PROPERTY_mode, oldValue, newValue);
		}
	}
}
