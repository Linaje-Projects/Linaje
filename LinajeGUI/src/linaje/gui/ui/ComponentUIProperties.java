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
package linaje.gui.ui;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import linaje.App;
import linaje.gui.LComponentBorder;
import linaje.gui.LTableBorder;
import linaje.gui.utils.ColorsGUI;
import linaje.logs.Console;
import linaje.utils.FieldsChangeSupport;
import linaje.utils.FieldsChangesNotifier;
import linaje.utils.Lists;
import linaje.utils.ReferencedColor;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.StateColor;
import linaje.utils.StateValues;
import linaje.utils.Utils;

/**
 * Los valores de propiedades que aquí se definan se iniciaran en
 * UIDefaults.put(K, V) para cada UI de
 * LinajeLookAndFeel.UI_LCOMPONENTS_OTHER_MAP
 * K = ComponentName.propertyName, V = value
 * Ej: UIDefaults.put(Table.background, Color.white)
 * Se podrán sobreescribir estos valores a través del
 * fichero de configuración de LinajeLookAndFeel (defaultUIConfig.cfg)
 * El fichero de configuración se puede editar a mano o ejecutando UIConfig.class
 */
public class ComponentUIProperties implements FieldsChangesNotifier {

	protected Border border = null;
	protected Font font = null;
	protected Color background = null;
	protected Color foreground = null;
	
	private Class<?> uiClass = null;
	
	private FieldsChangeSupport fieldsChangeSupport = null;
	
	public static final String PROPERTY_border = "border";
	public static final String PROPERTY_font = "font";
	public static final String PROPERTY_background = "background";
	public static final String PROPERTY_foreground = "foreground";
	
	public ComponentUIProperties(Class<?> uiClass) {
		this.uiClass = uiClass;
		App.getMapObjectsByName().put(LinajeLookAndFeel.getUIName(uiClass), this);
		try {
			initDefaultUIValues();
		} catch (Exception ex) {
			Console.printException(ex);
		}
	}

	protected void initDefaultUIValues() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
		
		double fgLuminanceFactor = 0;
		double bgLuminanceFactor = 0;
		
		font = generalUIProperties.getFontApp();
		border = BorderFactory.createEmptyBorder();
		
		if (getUiClass() == LTableHeaderUI.class) {
			
			font = font.deriveFont(Font.BOLD);
			border = BorderFactory.createMatteBorder(0, 0, 0, 1, ColorsGUI.getHeaderGridColor());
			
			ButtonUIProperties instance = UISupportButtons.getDefaultButtonUIProperties(LToggleButtonUI.class);
			Color tButtonbackground = instance.getBackground();
			Color tButtonforeground = instance.getForeground();
			
			String bgProperty = tButtonbackground instanceof StateColor ? ButtonUIProperties.PROPERTY_background : ButtonUIProperties.PROPERTY_defaultStateBackground;
			String fgProperty = tButtonforeground instanceof StateColor ? ButtonUIProperties.PROPERTY_foreground : ButtonUIProperties.PROPERTY_defaultStateForeground;
			Field stateValuesField = StateColor.class.getDeclaredField("stateValues");
			Field selectedValueField = StateValues.class.getDeclaredField(StateValues.PROPERTY_selectedValue);
			
			ReflectAccessSupport ras = new ReflectAccessSupport(instance);
			background = new ReferencedColor(instance, new Field[]{ras.findField(bgProperty), stateValuesField, selectedValueField}, bgLuminanceFactor);			
			foreground = new ReferencedColor(instance, new Field[]{ras.findField(fgProperty), stateValuesField, selectedValueField}, fgLuminanceFactor);
		}
		else {
			
			String fgProperty = GeneralUIProperties.PROPERTY_colorText;
			String bgProperty = GeneralUIProperties.PROPERTY_colorPanels;
			
			if (getUiClass() == LTableUI.class) {
				bgProperty = GeneralUIProperties.PROPERTY_colorPanelsBrightest;
				border = new LTableBorder();
			}
			/*else if (getUiClass() == LTableHeaderUI.class) {
				bgProperty = GeneralUIProperties.PROPERTY_colorPanelsDark;
				fgProperty = GeneralUIProperties.PROPERTY_colorTextBrightest;
				border = BorderFactory.createMatteBorder(0, 0, 0, 1, ColorsGUI.getColorPanels());
			}*/
			else if (getUiClass() == LComboUI.class) {
				bgProperty = GeneralUIProperties.PROPERTY_colorPanelsBrightest;
				border = new BorderUIResource.CompoundBorderUIResource(new LComponentBorder(), new MarginBorder());
			}
			else if (getUiClass() == LLabelUI.class) {
				bgProperty = GeneralUIProperties.PROPERTY_colorPanelsBrightest;
			}
			else if (getUiClass() == LTextFieldUI.class) {
				bgProperty = GeneralUIProperties.PROPERTY_colorPanelsBrightest;
				border = new BorderUIResource.CompoundBorderUIResource(new LComponentBorder(), new MarginBorder());
			}
			else if (getUiClass() == LTextAreaUI.class || getUiClass() == LTextPaneUI.class) {
				bgProperty = GeneralUIProperties.PROPERTY_colorPanelsBrightest;
				border = new BorderUIResource.CompoundBorderUIResource(BorderFactory.createEmptyBorder(), new MarginBorder());
			}
			else if (getUiClass() == LTabbedPaneUI.class) {
				float newSize = font.getSize() + 4;
				font = font.deriveFont(newSize);
			}
			
			GeneralUIProperties instance = GeneralUIProperties.getInstance();
			ReflectAccessSupport ras = new ReflectAccessSupport(instance);
			background = new ReferencedColor(instance, new Field[]{ras.findField(bgProperty)}, bgLuminanceFactor);
			foreground = new ReferencedColor(instance, new Field[]{ras.findField(fgProperty)}, fgLuminanceFactor);
		}
	}
	
	protected void initComponentDefaults(UIDefaults table) {
		
		//init ComponentUIProperties fields
		Field[] fields = ReflectAccessSupport.filterFields(ComponentUIProperties.class.getDeclaredFields(), null, false, Class.class);
		String prefix = UISupport.getPropertyPrefix(getUiClass());
		initUIComponentDefaultsFromFields(table, prefix, fields, this);
	}
	
	private static void initUIComponentDefaultsFromFields(UIDefaults table, String prefix, Field[] fields, Object source) {
		
		ReflectAccessSupport reflectAccess = new ReflectAccessSupport(source);
		
		List<Object> defaults = Lists.newList();
		for (int i = 0; i < fields.length; i++) {
			try {
				
				Field field = fields[i];
				int fieldModifiers = field.getModifiers();
				if (!Modifier.isFinal(fieldModifiers) && !Modifier.isStatic(fieldModifiers)) {
					String fieldName = field.getName();
					String defaultKey = prefix + fieldName;
					Object value = reflectAccess.getFieldValue(field, false);
					defaults.add(defaultKey);
					defaults.add(LinajeLookAndFeel.createResourceValue(value));
				}
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
		
		if (!defaults.isEmpty())  {
			table.putDefaults(Lists.listToArray(defaults, Object.class));
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

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getFieldsChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void resetDefaultFieldsValues() {
		getFieldsChangeSupport().resetDefaultFieldsValues();
	}
	
	//
	// GETTERS
	//
	
	public Class<?> getUiClass() {
		return uiClass;
	}
	public Border getBorder() {
		return border;
	}
	public Font getFont() {
		return font;
	}
	public Color getBackground() {
		return background;
	}
	public Color getForeground() {
		return foreground;
	}
	
	//
	// SETTERS
	//
	
	public void setBorder(Border border) {
		Border oldValue = this.border;
		Border newValue = border;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.border = border;
			firePropertyChange(PROPERTY_border, oldValue, newValue);
		}
	}
	public void setFont(Font font) {
		Font oldValue = this.font;
		Font newValue = font;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.font = font;
			firePropertyChange(PROPERTY_font, oldValue, newValue);
		}
	}
	public void setBackground(Color background) {
		Color oldValue = this.background;
		Color newValue = background;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.background = background;
			firePropertyChange(PROPERTY_background, oldValue, newValue);
		}
	}
	public void setForeground(Color foreground) {
		Color oldValue = this.foreground;
		Color newValue = foreground;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.foreground = foreground;
			firePropertyChange(PROPERTY_foreground, oldValue, newValue);
		}
	}
		
	public void encodeFieldsChanged(StringBuffer sb) {
		
		//init from ComponentUIProperties fields
		String prefix = UISupport.getPropertyPrefix(getUiClass());
		getFieldsChangeSupport().encodeFieldsChanged(sb, prefix);
	}
	
	public void updateUIPropertiesFromEncodedFields(String... encodedFields) {
		
		try {
			String prefix = UISupport.getPropertyPrefix(getUiClass());
			ReflectAccessSupport ras = new ReflectAccessSupport(this);
			ras.setEncodedFieldValues(prefix, encodedFields);
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
}
