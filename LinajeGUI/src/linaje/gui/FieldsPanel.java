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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import linaje.gui.cells.DataCell;
import linaje.gui.components.ComboMultiAspect;
import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.tests.TestPanel;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.FieldsChangesNotifier;
import linaje.utils.FormattedData;
import linaje.utils.Lists;
import linaje.utils.Numbers;
import linaje.utils.ReferencedColor;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.StateColor;
import linaje.utils.StateValues;
import linaje.utils.Strings;
import linaje.utils.FieldsChangeSupport.FieldValue;

/**
 * Se trata de un panel donde se añadirán componentes para poder modificar un TestComponent asociado y crear una ventana de test fácilmente
 * 	- 1º Asociamos el TestComponent que queremos probar mediante el constructor o con setTestComponent
 *  - 2º Añadimos los AccessComponent a través de los métodos 'addAccessComponents(...)' o 'addAccessComponentsFromFields(...)'
 *  
 *  AccessComponents: 
 *   - Están compuestos por un checkBox con la descripción del campo asociado y un componente para modificar el campo
 *   - Si seleccionamos el checkBox podremos modificar el campo y si lo deseleccionamos se restablecerá el valor original
 *   - Sólo se podrán crear AccessComponents de campos formateables (FormattedData.isFormateableType(Class<?> classType))
 *   - Si tenemos campos de tipo T, podremos definir a mano su tipo a través de setDefaultUntypedFieldType(...) o setUntypedFieldType(...)
 *   
 *  FieldsChangesNotifier
 *   - Si el TestObject implementa FieldsChangesNotifier, podremos inicializar los campos a través del parámetro fieldsChangedValues
 *   - A través de FieldsChangesNotifier.getFieldsChangeSupport() podremos obtener los valores de los campos modificados, codificarlos y decodificarlos
 *  
 **/
@SuppressWarnings("serial")
public class FieldsPanel extends LPanel {

	//DEFAULT_UNTYPED_FIELD_KEY se usa para definir el tipo T de una clase tipo Clase<T>
	public static final String DEFAULT_UNTYPED_FIELD_KEY = "default";
	
	public static final int MODE_SELECT_NONE = 0;
	public static final int MODE_SELECT_NO_NULL_VALUES = 1;
	
	private Component testComponent = null;
	private Map<String, Class<?>> untypedFieldsTypes = null;
	
	private int mode = MODE_SELECT_NONE;
	private boolean loading = false;
		
	public class AccessComponent extends LPanel {
		
		private ComboMultiAspect combo = null;
		LCheckBox checkBox = null;
		
		private Method methodGet = null;
		private Method methodSet = null;
		private Object testObject = null;
		
		private Object originalValue = null;
		private Object lastValue = null;
		
		private boolean reseting = false;
		
		public AccessComponent(Object testObject, String name, Method methodGet, Method methodSet, Object originalValue) {
			super();
			if (name != null && methodGet != null && methodSet != null) {
				setName(name);
				this.testObject = testObject;
				this.methodGet = methodGet;
				this.methodSet = methodSet;
				this.originalValue = originalValue;
				initialize();
			}
		}
		
		private String getLabelText() {
			//Introducimos un espacio delante de las mayúsculas que vengan precedidas de minúsculas
			String labelText = getName().replaceAll("([a-z])([A-Z])", "$1 $2");
			//Quitamos palabras redundantes
			String fieldClassName = getMethodGet().getReturnType().getSimpleName();
			String[] wordsDelete = {fieldClassName, fieldClassName.toLowerCase(), fieldClassName.toUpperCase()};
			labelText = Strings.replace(labelText, wordsDelete, Constants.VOID).trim();
			
			return labelText.equals(Constants.VOID) ? fieldClassName : labelText;
		}
		
		private void initialize() {
			
			try {
				
				setLayout(new BorderLayout());
				final ReflectAccessSupport reflectAccessSupport = new ReflectAccessSupport(getTestObject());
				combo = new ComboMultiAspect();
				checkBox = new LCheckBox(getLabelText()) {
					@Override
					public Dimension getPreferredSize() {
						Dimension size = super.getPreferredSize();
						int w = 200;//Math.min(200, size.width);
						int h = size.height;
						return new Dimension(w, h);
					}
				};
				//checkBox.setPreferredSize(new Dimension(160, checkBox.getPreferredSize().height));
				add(checkBox, BorderLayout.WEST);
				add(combo, BorderLayout.CENTER);
				//setPreferredSize(new Dimension(360, getPreferredSize().height));
				
				Class<?> methodGetType = getMethodGet().getReturnType();
				if (methodGetType == Object.class)
					methodGetType = getUntypedFieldType(getName());
				if (methodGetType == Object.class) {
					try {
						Object value = reflectAccessSupport.invokeMethod(getMethodGet());
						if (value != null)
							methodGetType = value.getClass();
					} catch (Exception ex) {
						Console.printException(ex);
					}
				}
				
				if (Boolean.class.isAssignableFrom(methodGetType) || boolean.class.isAssignableFrom(methodGetType)) {
					combo.setAspect(ComboMultiAspect.ASPECT_YES_NO);
				}
				else {
					combo.setAspect(ComboMultiAspect.ASPECT_TEXTFIELD_CONTAINER);
					combo.getTextField().setClassType(methodGetType);
				}
				
				Object valueGet = reflectAccessSupport.invokeMethod(getMethodGet());
				boolean selected;
				
				if (getMode() == MODE_SELECT_NONE) {
					selected = false;
					if (originalValue == null)
						originalValue = valueGet;
				}
				else {
					Object valueField = reflectAccessSupport.getFieldValue(getName(), false);
					selected = valueField != null;
					if (originalValue == null)
						originalValue = valueField;
				}
				
				lastValue = valueGet;
				
				combo.setEnabled(selected);
				checkBox.setSelected(selected);
				
				//No dejamos deselecionar el defaultValue de un StateValues
				if (selected && testObject instanceof StateValues && getName().equals(StateValues.PROPERTY_defaultValue))
					checkBox.setEnabled(false);
				
				combo.setSelectedItem(new DataCell(valueGet, valueGet));
				combo.addItemListener(new ItemListener() {
					
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							if (getTestObject() != null) {
								try {
									
									DataCell selectedItem = combo.getSelectedItem();
									Object newValue = selectedItem == null || reseting ? null : selectedItem.getCode();
									setFieldValue(newValue, reflectAccessSupport);
								}
								catch (Exception ex) {
									Console.printException(ex);
								}
							}
						}
					}
				});
				
				checkBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						try {
							
							boolean enabled = checkBox.isSelected();
							combo.setEnabled(enabled);
							if (enabled) {
								//Object oldValue = reflectAccessSupport.invokeMethod(getMethodGet());
								//Object newValue = lastValue;
								combo.setSelectedItem(new DataCell(lastValue, lastValue));
							}
							else {
								try {
									Object value = lastValue;
									if (getMode() == MODE_SELECT_NONE) {
										reseting = originalValue == null;
										combo.setSelectedItem(new DataCell(originalValue, originalValue));
									}
									else {
										reseting = true;
										combo.setSelectedItem(new DataCell(null, null));
									}
									lastValue = value;
								}
								finally {
									reseting = false;
								}
							}
						}
						catch (Exception ex) {
							Console.printException(ex);
						}
					}
				});
			
			} catch (Exception ex) {
				Console.printException(ex);
			}
		}

		private Method getMethodGet() {
			return methodGet;
		}
		private Method getMethodSet() {
			return methodSet;
		}
		private Object getTestObject() {
			return testObject;
		}
		
		private Object getValue() {
			return checkBox.isSelected() ? lastValue : originalValue;
		}
		
		private void setFieldValue(Object newValue, ReflectAccessSupport reflectAccessSupport) throws InvocationTargetException, IllegalAccessException, SecurityException {
			
			if (reflectAccessSupport == null)
				reflectAccessSupport = new ReflectAccessSupport(getTestObject());
			
			if (newValue != null && newValue instanceof Number) {
				final Class<?> methodSetType = getMethodSet().getParameterTypes()[0];
				newValue = Numbers.getNumberByType((Number) newValue, methodSetType);
			}
			
			//Object oldValue = reflectAccessSupport.invokeMethod(getMethodGet());
			if (!loading)
				reflectAccessSupport.invokeMethod(getMethodSet(), newValue);
			lastValue = newValue;
			//getFieldsChangeSupport(getTestObject()).firePropertyChange(getName(), oldValue, newValue);
			
			if (getTestObject() instanceof Component) {
				((Component) getTestObject()).revalidate();
				((Component) getTestObject()).repaint();
			}
			if (getTestComponent() != null) {
				getTestComponent().revalidate();
				getTestComponent().repaint();
			}
			reload(getName(), true);
		}
		
		private void refreshTextValue() {
			//Reasignamos el texto para que se actualice el valor del color
			combo.getLTextFieldContainer().getLTextField().reload();
			revalidate();
			repaint();
		}
		
		private void initFromChangedField() {
			
			if (getTestObject() != null && getTestObject() instanceof FieldsChangesNotifier) {
				
				FieldsChangesNotifier fcn = (FieldsChangesNotifier) getTestObject();
				FieldValue<Object, Object> fieldChanged = fcn.getFieldsChangeSupport().getFieldsChangedValues().get(getName());
				
				boolean selectField = fieldChanged != null;
				if (selectField) {
					originalValue = fieldChanged.getOriginalValue();
					lastValue = fieldChanged.getValue();
				}
				else {
					lastValue = originalValue;
				}
				
				if (checkBox.isSelected() == selectField)
					combo.setSelectedItem(new DataCell(lastValue, lastValue));
				else
					checkBox.setSelected(selectField);
			}
		}
	}
	
	public FieldsPanel() {
		this(null);
	}
	public FieldsPanel(Component testComponent) {
		this(testComponent, MODE_SELECT_NONE);
	}
	public FieldsPanel(Component testComponent, int mode) {
		super(new VerticalBagLayout());
		setTestComponent(testComponent);
		setMode(mode);
	}
	
	private Object getOriginalFieldValue(String fieldName, Map<String, FieldValue<Object, Object>> fieldsChangedValues) {
		Object originalFieldValue = null;
		if (fieldsChangedValues != null && fieldsChangedValues.containsKey(fieldName))
			originalFieldValue = fieldsChangedValues.get(fieldName).getOriginalValue();
		return originalFieldValue;
	}
	
	/**
	 * accessMethods serán los campos de la clase o superclase que queramos modificar
	 * Por ejemplo, si queremos testear como se comporta un botón al cambiar el background y el foreground haremos los siguiente:
	 * 
	 * LButton testButton = new LButton();
	 * HashMap<String, Class<?>> componentMethods = new HashMap<>();
	 * componentMethods.put("background", Component.class);
	 * componentMethods.put("foreground", Component.class);
	 * fieldsPanel.addAccessComponents(testButton, componentMethods);
	 * 
	 **/
	public void addAccessComponents(Object testObject, Map<String, Class<?>> accessMethods) {
		addAccessComponents(testObject, accessMethods, null);
	}
	/**
	 * - fieldsChangedValues valores de campos que queramos inicializar distintos al valor por defecto
	 **/
	public void addAccessComponents(Object testObject, Map<String, Class<?>> accessMethods, HashMap<String, FieldValue<Object, Object>> fieldsChangedValues) {
		
		if (testObject != null) {
						
			Iterator<Entry<String, Class<?>>> accessMethodsNames = accessMethods.entrySet().iterator();
			for (Iterator<Entry<String, Class<?>>> iterator = accessMethodsNames; iterator.hasNext();) {
				Entry<String, Class<?>> entry = iterator.next();
				String fieldName = entry.getKey();
				Class<?> objectClass = entry.getValue();
				Method methodGet = ReflectAccessSupport.findMethodGet(fieldName, objectClass);
				if (methodGet != null) {
					Class<?> fieldType = methodGet.getReturnType();
					Method methodSet = ReflectAccessSupport.findMethodSet(fieldName, fieldType, objectClass);
					if (methodSet != null) {
						Object originalValue = getOriginalFieldValue(fieldName, fieldsChangedValues);
						AccessComponent accessComponent = new AccessComponent(testObject, fieldName, methodGet, methodSet, originalValue);
						add(accessComponent);
					}
				}
			}
		}
	}
	
	public void addAccessComponentsFromFields(Object testObject) {
		addAccessComponentsFromFields(testObject, null);
	}
	public void addAccessComponentsFromFields(Object testObject, Map<String, FieldValue<Object, Object>> fieldsChangedValues) {
		addAccessComponentsFromFields(testObject, 2, fieldsChangedValues);
	}
	/**
	 * Se añadirá un AccesComponent por cada campo formateable con métodos de acceso get y set que tenga el testObject
	 *  - parentsFields será el número de superclases de las que queramos añadir campos
	 **/
	public void addAccessComponentsFromFields(Object testObject, int parentsFields) {
		addAccessComponentsFromFields(testObject, parentsFields, null);
	}
	/**
	 * - fieldsChangedValues valores de campos que queramos inicializar distintos al valor por defecto
	 **/
	public void addAccessComponentsFromFields(Object testObject, int parentsFields, Map<String, FieldValue<Object, Object>> fieldsChangedValues) {
		
		if (testObject != null) {
			Class<?> objectClass = testObject.getClass();
			while (parentsFields >= 0 && objectClass != null) {
				addAccessComponentsFromFields(objectClass, testObject, fieldsChangedValues);
				objectClass = objectClass.getSuperclass();
				parentsFields--;
			}
		}
	}
	/**
	 * Se añadirá un AccesComponent por cada campo formateable con métodos de acceso get y set que tenga el testObject en la superclase objectClass
	 **/
	public void addAccessComponentsFromFields(Class<?> objectClass, Object testObject) {
		addAccessComponentsFromFields(objectClass, testObject, null);
	}
	public void addAccessComponentsFromFields(Class<?> objectClass, Object testObject, Map<String, FieldValue<Object, Object>> fieldsChangedValues) {
		
		Field[] fields = objectClass.getDeclaredFields();
			
		final ReflectAccessSupport reflectAccessSuport = new ReflectAccessSupport(testObject);
		
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Class<?> fieldType = field.getType();
			if (fieldType == Object.class)
				fieldType = getUntypedFieldType(field.getName());
			if (fieldType == Object.class) {
				try {
					Object value = reflectAccessSuport.getFieldValue(field, true);
					if (value != null)
						fieldType = value.getClass();
				} catch (Exception ex) {
					Console.printException(ex);
				}
			}
			
			if (isFormateableType(fieldType)) {
				Method methodGet = ReflectAccessSupport.findMethodGet(field.getName(), objectClass);
				if (methodGet != null) {
					Method methodSet = ReflectAccessSupport.findMethodSet(field.getName(), field.getType(), objectClass);
					if (methodSet != null) {
						try {
							String fieldName = field.getName();
							Object originalValue = getOriginalFieldValue(fieldName, fieldsChangedValues);
							AccessComponent accessComponent = new AccessComponent(testObject, fieldName, methodGet, methodSet, originalValue);
							add(accessComponent);
						}
						catch (Exception ex) {
							Console.printException(ex);
						}
					}
				}
			}
		}
	}
	
	private boolean isFormateableType(Class<?> classType) {
		return FormattedData.isFormateableType(classType);
	}

	public Map<String, Class<?>> getUntypedFieldsTypes() {
		if (untypedFieldsTypes == null)
			untypedFieldsTypes = new LinkedHashMap<>();
		return untypedFieldsTypes;
	}
	
	/**
	 * Se usa para definir el tipo T por defecto de una clase tipo Clase<T> 
	 */
	public void setDefaultUntypedFieldType(Class<?> defaultType) {
		getUntypedFieldsTypes().put(DEFAULT_UNTYPED_FIELD_KEY, defaultType);
	}
	public Class<?> getDefaultUntypedFieldType() {
		Class<?> fieldType = getUntypedFieldsTypes().get(DEFAULT_UNTYPED_FIELD_KEY);
		if (fieldType == null)
			fieldType = Object.class;
		return fieldType;
	}
	
	/**
	 * Se usa para definir el tipo T de un campo concreto de una clase tipo Clase<T> 
	 */
	public void setUntypedFieldType(String fieldName, Class<?> fieldType) {
		getUntypedFieldsTypes().put(DEFAULT_UNTYPED_FIELD_KEY, fieldType);
	}
	public Class<?> getUntypedFieldType(String fieldName) {
		Class<?> fieldType = getUntypedFieldsTypes().get(fieldName);
		if (fieldType == null)
			fieldType = getDefaultUntypedFieldType();
		return fieldType;
	}
	
	/*private Map<Object, FieldsChangeSupport> getFieldsChangeSupportMap() {
		if (fieldsChangeSupportMap == null)
			fieldsChangeSupportMap = new HashMap<Object, FieldsChangeSupport>();
		return fieldsChangeSupportMap;
	}
	
	private FieldsChangeSupport getFieldsChangeSupport(Object testObject) {
		FieldsChangeSupport fcs = getFieldsChangeSupportMap().get(testObject);
		if (fcs == null) {
			fcs = new FieldsChangeSupport(testObject);
			getFieldsChangeSupportMap().put(testObject, fcs);
		}
		return fcs;
	}*/
	
	public Component getTestComponent() {
		return testComponent;
	}
	public void setTestComponent(Component testComponent) {
		this.testComponent = testComponent;
	}
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void reload(String fieldChanged) {
		reload(fieldChanged, false);
	}
	private void reload(String fieldChanged, boolean fromSetFieldValue) {
		
		for (int i = 0; i < getComponentCount(); i++) {
			Component c = getComponent(i);
			if (c instanceof AccessComponent && (!fromSetFieldValue || !c.getName().equals(fieldChanged))) {
				AccessComponent accessComponent = (AccessComponent) c;
				if (accessComponent.getValue() != null) {
					try {
						
						boolean colorReloaded = false;
						
						if (accessComponent.getValue() instanceof ReferencedColor) {
							ReferencedColor refColor = (ReferencedColor) accessComponent.getValue();
							while(refColor != null && !colorReloaded) {
								//if (refColor.getPath().contains(fieldChanged)) {
								if (Lists.getLastElement(refColor.getColorFieldTree()).getName().equals(fieldChanged)) {
									refColor.reload();
									colorReloaded = true;
								}
								else if (refColor.getColor() instanceof ReferencedColor) {
									refColor = (ReferencedColor) refColor.getColor();
								}
								else {
									refColor = null;
								}
							}
						}
						else if (accessComponent.getValue() instanceof StateColor) {
							StateColor stateColor = (StateColor) accessComponent.getValue();
							Map<String, Color> mapValues = stateColor.getStateValues().getValues();
							Collection<Color> values = mapValues.values();
							for (Iterator<Color> iterator = values.iterator(); iterator.hasNext();) {
								Color color = (Color) iterator.next();
								if (color instanceof ReferencedColor) {
									ReferencedColor refColor = (ReferencedColor) color;
									while(refColor != null && !colorReloaded) {
										//if (refColor.getPath().contains(fieldChanged)) {
										if (Lists.getLastElement(refColor.getColorFieldTree()).getName().equals(fieldChanged)) {
											refColor.reload();
											colorReloaded = true;
										}
										else if (refColor.getColor() instanceof ReferencedColor) {
											refColor = (ReferencedColor) refColor.getColor();
										}
										else {
											refColor = null;
										}
									}
								}
							}
							if (colorReloaded)
								stateColor.reload();
						}
						if (colorReloaded) {
							accessComponent.setFieldValue(accessComponent.getValue(), null);
							//accessComponent.combo.setSelectedItem(new DataCell(accessComponent.lastValue, accessComponent.lastValue));
							accessComponent.refreshTextValue();
						}
					}
					catch (Exception ex) {
						Console.printException(ex);
					}
				}
			}
		}
	}
	
	public void selectModifiedFields() {
		try {
			loading = true;
			for (int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);
				if (c instanceof AccessComponent) {
					AccessComponent accessComponent = (AccessComponent) c;
					accessComponent.initFromChangedField();
				}
			}
		} finally {
			loading = false;
		}
	}
	
	public static void main(String[] args) {
		TestPanel.main(args);
	}
}
