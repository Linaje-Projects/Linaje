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
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import linaje.logs.Console;

/**
 * Nos permite monitorizar los cambios de los campos de la clase que imlemente FieldsChangesNotifier
 * También permite restablecer los valores originales, 
 * así como guardar en fichero el estado de los campos en un momento determinado y asignar ese estado mas tarde
 * 
 * @see FieldsChangesNotifier
 **/
@SuppressWarnings("serial")
public class FieldsChangeSupport extends PropertyChangeSupport { 

	private Object source = null;
	private int superClassesToSearch = 2;
	
	private ReflectAccessSupport reflectAccessSupport = null;
	private Map<String, FieldValue<Object, Object>> fieldsChangedValues = null;
	
	public class FieldValue<K, V> implements Entry<K, V> {

		private K originalValue = null;
		private V value = null;
		
		public FieldValue(K originalValue, V value) {
			this.originalValue = originalValue;
			this.value = value;
		}
		
		public K getOriginalValue() {
			return originalValue;
		}
		
		@Override
		public K getKey() {
			return originalValue;
		}
		@Override
		public V getValue() {
			return value;
		}
		@Override
		public V setValue(V value) {
			return this.value = value;
		}	
	}
	
	public FieldsChangeSupport(Object source) {
		this(source, 2);
	}
	public FieldsChangeSupport(Object source, int superClassesToSearch) {
		super(source);
		this.source = source;
		this.superClassesToSearch = superClassesToSearch;
	}

	public void firePropertyChange(String fieldName, Object oldValue, Object newValue) {
		
		FieldValue<Object, Object> fieldValue = getFieldsChangedValues().get(fieldName);
		if (fieldValue != null) {
			if (!Utils.propertyChanged(fieldValue.getOriginalValue(), newValue)) {
				getFieldsChangedValues().remove(fieldName);
			}
			else {
				fieldValue.setValue(newValue);
				getFieldsChangedValues().put(fieldName, fieldValue);
			}
		}
		else {
			fieldValue = new FieldValue<Object, Object>(oldValue, newValue);
			getFieldsChangedValues().put(fieldName, fieldValue);
		}
		
		if (newValue != null && newValue instanceof Color) {
			//Modificamos el oldValue para forzar la propagación del evento,
			//ya que super.firePropertyChange() no distinguirá que ha ha cambiado con algunos StateColor o ReferenceColor
			oldValue = newValue.equals(Color.white) ? Color.black : Color.white;
		}
		super.firePropertyChange(fieldName, oldValue, newValue);
	}
	
	public Map<String, FieldValue<Object, Object>> getFieldsChangedValues() {
		if (fieldsChangedValues == null) {
			//fieldsChangedValues = new LinkedHashMap<String, FieldValue<Object, Object>>();
			//fieldsChangedValues = Collections.synchronizedMap(new LinkedHashMap<String, FieldValue<Object, Object>>());
			//fieldsChangedValues = new ConcurrentHashMap<String, FieldValue<Object, Object>>();
			fieldsChangedValues = new ConcurrentSkipListMap<String, FieldValue<Object, Object>>();
		}
		return fieldsChangedValues;
	}
		
	public boolean fieldsChanged() {
		return fieldsChangedValues != null && !fieldsChangedValues.isEmpty();
	}
	
	public List<String> getFieldNamesChanged() {
		return Lists.iteratorToList(getFieldsChangedValues().keySet().iterator());
	}
	public List<Field> getFieldsChanged() throws NoSuchFieldException {
		ReflectAccessSupport ras = new ReflectAccessSupport(source, getSuperClassesToSearch());
		return ras.findFields(Lists.listToArray(getFieldNamesChanged(), String.class));
	}
	
	public void setFieldsValues(HashMap<String, FieldValue<Object, Object>> fieldChangedValues) {
		
		if (fieldChangedValues != null && !fieldChangedValues.isEmpty()) {
			Iterator<Entry<String, FieldValue<Object, Object>>> fieldsChanged = fieldChangedValues.entrySet().iterator();
			for (Iterator<Entry<String, FieldValue<Object, Object>>> iterator = fieldsChanged; iterator.hasNext();) {
				Entry<String, FieldValue<Object, Object>> entry = iterator.next();
				String fieldName = entry.getKey();
				FieldValue<Object, Object> fieldValue = entry.getValue();
				
				if (fieldName != null && fieldValue != null) {
					Object newValue = fieldValue.getValue();
					try {
						Field field = getReflectAccessSupport().findField(fieldName);
						if (field != null) {
							Object oldValue = getReflectAccessSupport().getFieldValue(field, false);
							getReflectAccessSupport().setFieldValue(field, newValue);
							firePropertyChange(fieldName, oldValue, newValue);
						}
					}
					catch (Exception ex) {
						Console.printException(ex);
					}
				}
			}
		}
	}

	/**
	 * Si se utiliza un SecurityManager que limite la accesibilidad de campos privados por Introspección, éste método no funcionara.
	 * En ese caso la clase que utiliza el FieldsChangeSupport deberá implementar una copia de éste método
	 * */
	public void resetDefaultFieldsValues() {
		
		if (fieldsChanged()) {
			Iterator<Entry<String, FieldValue<Object, Object>>> fieldsChanged = getFieldsChangedValues().entrySet().iterator();
			for (Iterator<Entry<String, FieldValue<Object, Object>>> iterator = fieldsChanged; iterator.hasNext();) {
				Entry<String, FieldValue<Object, Object>> entry = iterator.next();
				String fieldName = entry.getKey();
				FieldValue<Object, Object> fieldValue = entry.getValue();
				resetFieldValue(fieldName, fieldValue);
			}
		}
	}
	
	public void resetFieldValue(String fieldName) {
		FieldValue<Object, Object> fieldValue = getFieldsChangedValues().get(fieldName);
		resetFieldValue(fieldName, fieldValue);
	}
			
	private void resetFieldValue(String fieldName, FieldValue<Object, Object> fieldValue) {
		
		if (fieldName != null && fieldValue != null) {
			Object originalValue = fieldValue.getOriginalValue();
			try {
				Field field = getReflectAccessSupport().findField(fieldName);
				if (field != null) {
					Object oldValue = getReflectAccessSupport().getFieldValue(field, false);
					getReflectAccessSupport().setFieldValue(field, originalValue);
					firePropertyChange(fieldName, oldValue, originalValue);
				}
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
	}
	
	public void encodeFieldsChanged(StringBuffer sb, String prefix) {
		//init from ComponentUIProperties fields
		ReflectAccessSupport ras = new ReflectAccessSupport(source, getSuperClassesToSearch());
		ras.encodeFieldsValues(sb, prefix, Lists.listToArray(getFieldNamesChanged(), String.class));
	}
	
	public int getSuperClassesToSearch() {
		return superClassesToSearch;
	}
	
	public ReflectAccessSupport getReflectAccessSupport() {
		if (reflectAccessSupport == null)
			reflectAccessSupport = new ReflectAccessSupport(source, superClassesToSearch);
		return reflectAccessSupport;
	}
}
