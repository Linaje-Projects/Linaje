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
package linaje;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Locale;

import linaje.statics.Constants;
import linaje.utils.ReflectAccessSupport;

public abstract class LocalizedStrings {

	public static String DEFAULT_RESOURCE_BUNDLE = "linaje.localization.linaje";
	public static String DEFAULT_TESTS_RESOURCE_BUNDLE = "linaje.tests.localization.linajeTests";
	public static String DEFAULT_GUI_RESOURCE_BUNDLE = "linaje.gui.localization.linaje_gui";
	public static String DEFAULT_GUI_TESTS_RESOURCE_BUNDLE = "linaje.gui.tests.localization.linaje_gui_tests";
	
	private Locale locale = null;
	
	public LocalizedStrings() {
		//Se llamará a este constructor si sabemos que se ha añadido previamente su 'resourceBundle' o si no vamos a usar uno iniciando los Strings de otra forma
		this(DEFAULT_RESOURCE_BUNDLE, null);
	}
	public LocalizedStrings(String resourceBundle) {
		this(resourceBundle, null);
	}
	
	public LocalizedStrings(String resourceBundle, Locale locale) {
		App.getDefaults().addResourceBundle(resourceBundle);
		setLocale(locale);
		
		initValues();
	}

	protected abstract void initValues();
	
	public String getPrefixClassString(Class<?> prefixClass) {
		return prefixClass != null ? prefixClass.getSimpleName()+Constants.POINT : null;
	}
	public void initValuesFromFieldNames() {
		initValuesFromFieldNames((String) null);
	}
	public void initValuesFromFieldNames(Class<?> prefixClass) {
		initValuesFromFieldNames(getPrefixClassString(prefixClass));
	}
	public void initValuesFromFieldNames(String prefix) {
		ReflectAccessSupport ras = new ReflectAccessSupport(this);
		ras.setSuperClassesToSearch(0);
		Field[] fields = ras.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Class<?> fieldType = field.getType();
			String fieldName = field.getName();
			try {
				if (String.class.isAssignableFrom(fieldType)) {
						String value = getString(prefix, fieldName);
						ras.setFieldValue(field, value);
				}
				else if (int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType)) {
					int value = getMnemonic(prefix, fieldName);
					ras.setFieldValue(field, value);
				}
			} catch (Throwable ex) {
				System.err.println(ex.getMessage());
			}
		}
	}
	
	public String getString(Class<?> prefixClass, String key) {
		return getString(getPrefixClassString(prefixClass), key);
	}
	public String getString(String prefix, String key) {
		String finalKey = prefix != null && key != null ? prefix+key : key;
		return getString(finalKey);
	}
	public String getString(String key) {
		return App.getString(key, getLocale());
	}
	
	public int getMnemonic(Class<?> prefixClass, String key) {
		return getMnemonic(getPrefixClassString(prefixClass), key);
	}	
	public int getMnemonic(String prefix, String key) {
		String finalKey = prefix != null && key != null ? prefix+key : key;
		return getMnemonic(finalKey);
	}
	public int getMnemonic(String key) {
        return App.getInt(key, getLocale());
    }
	
	public Locale getLocale() {
		return locale != null ? locale : Locale.getDefault();
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public void resetStrings() {
		resetStrings(null);
	}
	public void resetStrings(Locale locale) {
		setLocale(locale);
		initValues();
	}
	
	public static String getCtrlDesc(String keyStroke) {
		if (keyStroke != null && keyStroke.length() > 0) {
			char keyChar = keyStroke.toUpperCase().charAt(0);
			return "  Ctrl+"+keyChar;
		}
		return Constants.VOID;
	}
	
	public static int getKeyCode(String keyStroke) {
		if (keyStroke != null && keyStroke.length() > 0) {
			char keyChar = keyStroke.charAt(0);
			return KeyEvent.getExtendedKeyCodeForChar(keyChar);
		}
		return -1;
	}
}
