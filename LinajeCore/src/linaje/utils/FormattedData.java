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
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import linaje.App;
import linaje.logs.Console;
import linaje.statics.Constants;

public class FormattedData {

	public static final int TYPE_TEXT = 0;
	public static final int TYPE_NUMBER = 1;
	public static final int TYPE_DATE = 2;
	public static final int TYPE_BOOLEAN = 3;
	public static final int TYPE_COLOR = 4;
	public static final int TYPE_FONT = 5;
	
	public static final int CAPS_DO_NOTHING = -1;
	public static final int CAPS_ALL_TO_UPPERCASE = 0;
	public static final int CAPS_ALL_TO_LOWERCASE = 1;
	public static final int CAPS_FIRST_CHARACTER = 2;
	public static final int CAPS_FIRST_CHARACTER_ALL_WORDS = 3;
	
	//Excluímos los artículos y preposiciones mas frecuentes
	public static final List<String> DEFAULT_CAPS_FIRST_CHAR_EXCLUDE_LIST_ES = Lists.newList("el", "la", "los", "las", "a", "en", "de", "con", "por", "sin");
	public static final List<String> DEFAULT_CAPS_FIRST_CHAR_EXCLUDE_LIST = DEFAULT_CAPS_FIRST_CHAR_EXCLUDE_LIST_ES;
	
	public static final String PROPERTY_VALUE = "value";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_DECIMALS = "decimals";
	public static final String PROPERTY_MAX_VALUE = "maxValue";
	public static final String PROPERTY_MIN_VALUE = "minValue";
	public static final String PROPERTY_MAX_CHARACTERS = "maxCharacters";
	public static final String PROPERTY_CAPITALIZE_TYPE = "capitalizeType";
	public static final String PROPERTY_LEVEL = "level";
	public static final String PROPERTY_INDENT_TEXT = "indentText";
	public static final String PROPERTY_PREFIX = "prefix";
	public static final String PROPERTY_POSTFIX = "postfix";
	public static final String PROPERTY_CAPS_EXCLUDE_LIST = "capsExcludeList";
	public static final String PROPERTY_FORMATTED_TEXT = "formattedText";
	public static final String PROPERTY_INDENT_LEVEL_ENABLED = "indentLevelEnabled";
	public static final String PROPERTY_THOUSANDS_SEPARATOR_ENABLED = "thousandsSeparatorEnabled";
	public static final String PROPERTY_FORMAT_EMPTY_TEXT_ENABLED = "formatEmptyTextEnabled";
	public static final String PROPERTY_PERIOD = "period";
	
	public static final String PREFIX_FIELD = "@fld@";
	
	private Object value = null;
	private int type = TYPE_TEXT;
	private int decimals = 0;
	private double maxValue = Numbers.MAX_DECIMAL_NUMBER;
	private double minValue = Numbers.MIN_DECIMAL_NUMBER;
	private int maxCharacters = Numbers.MAX_INTEGER_NUMBER;
	private int capitalizeType = CAPS_DO_NOTHING;
	private int level = 1;
	private String indentText = null;
	private boolean indentLevelEnabled = false;
	private boolean thousandsSeparatorEnabled = true;
	private boolean formatEmptyTextEnabled = true;
	
	private int period = Dates.PERIOD_DAILY;
	
	private String prefix = null;
	private String postfix = null;
	
	private List<String> capsExcludeList = null;
	private String formattedText = null;
	private PropertyChangeSupport propertyChangeSupport = null;
	
	//private boolean fireValueChangeOnFormat = false;
	private Object oldValue = null;
	private Class<?> classType = null; 
	
	public FormattedData() {
	}
	public FormattedData(Object value) {
		setValue(value);
	}

	public String getFormattedData(Object objectValue) {
		if (objectValue == null)
			return Constants.VOID;
		
		if (objectValue instanceof Number)
			return getFormattedData((Number) objectValue);
		else if (objectValue instanceof Date)
			return getFormattedData((Date) objectValue);
		else if (objectValue instanceof Color)
			return getFormattedData((Color) objectValue);
		else if (objectValue instanceof Boolean)
			return getFormattedData((Boolean) objectValue);
		else
			return getFormattedData(objectValue.toString());
	}
	public String getFormattedData(Color color) {
		return Colors.encode(color);
	}
	public String getFormattedData(Boolean booleanValue) {
		return booleanValue != null ? booleanValue.toString() : "false";
	}
	public String getFormattedData(Date date) {
		return Dates.getFormattedDate(date, getPeriod());
	}
	public String getFormattedData(Number number) {
		if (isThousandsSeparatorEnabled())
			return Numbers.formatMonetaryNumber(number, getDecimals());
		else
			return Numbers.formatNumber(number, getDecimals());
	}
	public String getFormattedData(Font font) {
		return font != null ? font.toString() : Constants.VOID;
	}
	public String getFormattedData(String text) {
		return getFormattedData(text, false);
	}
	private String getFormattedData(String text, boolean changeValue) {
		
		String formattedData = text;
		
		try {
			
			if (getType() == TYPE_DATE) {
				
				Date possibleDate = getValueDate(text);
				if (possibleDate != null) {
					formattedData = getFormattedData(possibleDate);
					if (changeValue)
						setValue(possibleDate, false);
				}
			}
			else if (getType() == TYPE_NUMBER) {
	
				boolean formatText = !text.equals(Constants.VOID) || isFormatEmptyTextEnabled();
				if(formatText) {
					double valor = getValueNumber(text).doubleValue();
					if (valor > getMaxValue())
						valor = getMaxValue();
					if (valor < getMinValue())
						valor = getMinValue();
					
					formattedData = getFormattedData(new Double(valor));
					if (changeValue)
						setValue(new Double(valor), false);
				}
			}
			else if (getType() == TYPE_BOOLEAN) {
				
				Boolean booleanValue = getValueBoolean(text);
				formattedData = getFormattedData(booleanValue);
				if (changeValue)
					setValue(booleanValue, false);
			}
			else if (getType() == TYPE_COLOR) {
				
				Color color = getValueColor(text);
				formattedData = text.startsWith(PREFIX_FIELD) ? text : getFormattedData(color);
				if (changeValue)
					setValue(color, false);
			}
			else if (getType() == TYPE_FONT) {
				
				Font font = getValueFont(text);
				formattedData = text.startsWith(PREFIX_FIELD) ? text : getFormattedData(font);
				if (changeValue)
					setValue(font, false);
			}
		}
		catch (Throwable ex) {
			//Excepcion controlada. Si pasa por aqui es que estamos intentando formatear algo que no esta relacionado con su tipo
		}
		
		return formattedData;
	}
	
	private void formatText(Object value) {
		
		String formattedText = Constants.VOID;
		
		if (value != null) {
				
			if (value instanceof Date || value instanceof Calendar) {
				
				Date date = value instanceof Date ? (Date) value : ((Calendar) value).getTime();
				formattedText = getFormattedData(date);
			}
			else if (value instanceof Number) {
				
				Number number = (Number) value;
				formattedText = getFormattedData(number);
			}
			else if (value instanceof Boolean) {
				
				Boolean booleanValue = (Boolean) value;
				formattedText = getFormattedData(booleanValue);
			}
			else if (value instanceof Color) {
				
				Color color = (Color) value;
				formattedText = getFormattedData(color);
			}
			else if (value instanceof Font) {
				
				Font font = (Font) value;
				formattedText = getFormattedData(font);
			}
			else {
				
				formattedText = getFormattedData(value.toString(), true);
			}
			
			if (getPrefix() != null)
				formattedText = getPrefix() + formattedText;
			if (getPostfix() != null)
				formattedText = formattedText + getPostfix();
			
			if (getCapitalizeType() == CAPS_ALL_TO_UPPERCASE)
				formattedText = formattedText.toUpperCase();
			else if (getCapitalizeType() == CAPS_ALL_TO_LOWERCASE)
				formattedText = formattedText.toLowerCase();
			else if (getCapitalizeType() == CAPS_FIRST_CHARACTER)
				formattedText = Strings.capitalizeFirstWord(formattedText);
			else if (getCapitalizeType() == CAPS_FIRST_CHARACTER_ALL_WORDS)
				formattedText = Strings.capitalizeAllWords(formattedText, getCapsExcludeList());
			
			if (isIndentLevelEnabled())
				formattedText = getIndentText() + formattedText;
		}
		
		setFormattedText(formattedText);
	}
	
	private String calculateIndentText() {
		return isIndentLevelEnabled() ? Strings.getIndent(getLevel()) : Constants.VOID;
	}
	
	public Number getValueNumber() {
		if (getValue() != null && getValue() instanceof Number)
			return (Number) getValue();
		else
			return new Double(0.0);
	}
	
	public Number getValueNumber(String text) {
		return getValueNumber(text, isThousandsSeparatorEnabled());
	}
	
	public static Number getValueNumber(String text, boolean thousandsSeparatorEnabled) {
		
		if (text != null && text.startsWith(PREFIX_FIELD)) {
			Object valueField = getValueField(text);
			if (valueField == null)
				text = null;
			else if (valueField instanceof Number)
				return (Number) valueField;
			else if (valueField.getClass() == int.class)
				return new Integer((int) valueField);
			else if (valueField.getClass() == float.class)
				return new Float((float) valueField);
			else if (valueField.getClass() == double.class)
				return new Double((double) valueField);
			else if (valueField.getClass() == long.class)
				return new Long((long) valueField);
			else if (valueField.getClass() == short.class)
				return new Short((short) valueField);
			else if (valueField.getClass() == byte.class)
				return new Byte((byte) valueField);
			else
				text = valueField.toString();
		}
		
		Number number = thousandsSeparatorEnabled ? Numbers.getFormattedNumberValue(text) : Numbers.getUnformattedNumberValue(text);
		if (number == null)
			number = 0;
		return number;
	}
	
	public Date getValueDate() {
		if (getValue() != null && (getValue() instanceof Date || getValue() instanceof Calendar))
			return getValue() instanceof Date ? (Date) getValue() : ((Calendar) getValue()).getTime();
		else
			return new Date();
	}
	
	public Date getValueDate(String text) throws ParseException {
		return getValueDate(text, isFormatEmptyTextEnabled());
	}
	
	public static Date getValueDate(String text, boolean formatEmptyText) throws ParseException {
		
		if (text != null && text.startsWith(PREFIX_FIELD)) {
			Object valueField = getValueField(text);
			if (valueField == null)
				text = null;
			else if (valueField instanceof Date)
				return (Date) valueField;
			else if (valueField instanceof Calendar)
				return ((Calendar) valueField).getTime();
			else
				text = valueField.toString();
		}
		
		return text != null && (!text.equals(Constants.VOID) || !formatEmptyText) ? Dates.getDate(text) : new Date();
	}
	
	public Boolean getValueBoolean() {
		if (getValue() != null && getValue() instanceof Boolean)
			return (Boolean) getValue();
		else
			return new Boolean(false);
	}
	
	public static Boolean getValueBoolean(String text) {
		
		if (text != null && text.startsWith(PREFIX_FIELD)) {
			Object valueField = getValueField(text);
			if (valueField == null)
				text = null;
			else if (valueField instanceof Boolean)
				return (Boolean) valueField;
			else if (valueField.getClass() == boolean.class)
				return new Boolean((boolean) valueField);
			else
				text = valueField.toString();
		}
		
		boolean booleanValue = text != null && text.matches(Constants.REGEX_YES);
		return new Boolean(booleanValue);
	}
	
	public Color getValueColor() {
		if (getValue() != null && getValue() instanceof Color)
			return (Color) getValue();
		else
			return Color.white;
	}
	
	public static Color getValueColor(String text) {
		if (text != null && text.startsWith(PREFIX_FIELD)) {
			Object valueField = getValueField(text);
			if (valueField == null)
				text = null;
			else if (valueField instanceof Color)
				return (Color) valueField;
			else
				text = valueField.toString();
		}
		
		return text != null ? Colors.decode(text) : null;
	}
	
	public Font getValueFont() {
		if (getValue() != null && getValue() instanceof Font)
			return (Font) getValue();
		else
			return null;
	}
	
	public static Font getValueFont(String text) {
		if (text != null && text.startsWith(PREFIX_FIELD)) {
			Object valueField = getValueField(text);
			if (valueField == null)
				text = null;
			else if (valueField instanceof Font)
				return (Font) valueField;
			else
				text = valueField.toString();
		}
		
		return text != null ? Utils.decodeFont(text) : null;
	}
	
	public Object getValueObject(String text) throws ParseException {
		
		if (text == null)
			return null;
		
		if (getType() == TYPE_NUMBER)
			return getValueNumber(text);
		else if (getType() == TYPE_DATE)
			return getValueDate(text);
		else if (getType() == TYPE_COLOR)
			return getValueColor(text);
		else if (getType() == TYPE_BOOLEAN)
			return getValueBoolean(text);
		else if (getType() == TYPE_FONT)
			return getValueFont(text);
		else
			return text;
	}
	
	public static Object getValueObject(String text, Class<?> classType) throws ParseException {
		Object objectValue = getValueObject(text, getType(classType));
		if (objectValue != null && objectValue instanceof Number) {
			objectValue = Numbers.getNumberByType((Number) objectValue, classType);
		}
		return objectValue;
	}
	public static Object getValueObject(String text, int type) throws ParseException {
		
		if (text == null)
			return null;
		
		if (type == TYPE_NUMBER)
			return getValueNumber(text, false);
		else if (type == TYPE_DATE)
			return getValueDate(text, false);
		else if (type == TYPE_COLOR)
			return getValueColor(text);
		else if (type == TYPE_BOOLEAN)
			return getValueBoolean(text);
		else if (type == TYPE_FONT)
			return getValueFont(text);
		else
			return text;
	}
	
	public static int getType(Class<?> classType) {
		
		int type = TYPE_TEXT;
		if (Date.class.isAssignableFrom(classType) || Calendar.class.isAssignableFrom(classType)) {
			type = TYPE_DATE;
		}
		else if (Boolean.class.isAssignableFrom(classType) || boolean.class.isAssignableFrom(classType)) {
			type = TYPE_BOOLEAN;
		}
		else if (Color.class.isAssignableFrom(classType)) {
			type = TYPE_COLOR;
		}
		else if (Font.class.isAssignableFrom(classType)) {
			type = TYPE_FONT;
		}
		else if (Numbers.isNumberClass(classType)) {
			type = TYPE_NUMBER;
		}
		return type;
	}
	
	public String toString() {
		return getFormattedText();
	}
	
	//
	// PropertyChange methods
	//
	
	private PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			propertyChangeSupport = new PropertyChangeSupport(this);
		return propertyChangeSupport;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
	}

	private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (!propertyName.equals(PROPERTY_VALUE) //Se formateará dentro de setValue() según sea necesario
		 && !propertyName.equals(PROPERTY_INDENT_LEVEL_ENABLED) //Modifica indentText, que es la propiedad que realmente hará que se formatee el texto
		 && !propertyName.equals(PROPERTY_LEVEL)) {//Modifica indentText, que es la propiedad que realmente hará que se formatee el texto
			
			boolean isField = newValue != null && newValue.toString().startsWith(PREFIX_FIELD);
			if (!isField)
				formatText(getValue());
		}
		
		if (propertyChangeSupport != null) {
			if (newValue != null && newValue instanceof Color) {
				//Modificamos el oldValue para forzar la propagación del evento,
				//ya que no distinguirá que ha ha cambiado con algunos Statecolor o ReferenceColor
				oldValue = newValue.equals(Color.white) ? Color.black : Color.white;
			}
			getPropertyChangeSupport().firePropertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
			//getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
		}
	}
	
	//
	// Getters
	//
	
	public Object getValue() {
		if (formattedText != null && formattedText.startsWith(PREFIX_FIELD))
			return getValueField(formattedText);
		else
			return value;
	}
	public String getFormattedText() {
		return formattedText;
	}

	public List<String> getCapsExcludeList() {
		return capsExcludeList;
	}
	public int getType() {
		return type;
	}
	public int getDecimals() {
		return decimals;
	}
	public double getMaxValue() {
		if (getDecimals() == 0 && maxValue > Numbers.MAX_INTEGER_NUMBER)
			return Numbers.MAX_INTEGER_NUMBER;
		else
			return maxValue;
	}
	public double getMinValue() {
		if (getDecimals() == 0 && minValue < Numbers.MIN_INTEGER_NUMBER)
			return Numbers.MIN_INTEGER_NUMBER;
		else
			return minValue;
	}
	public int getMaxCharacters() {
		return maxCharacters;
	}
	public int getCapitalizeType() {
		return capitalizeType;
	}
	public boolean isFormatEmptyTextEnabled() {
		return formatEmptyTextEnabled;
	}
	public String getPrefix() {
		return prefix;
	}
	public String getPostfix() {
		return postfix;
	}
	public boolean isIndentLevelEnabled() {
		return indentLevelEnabled;
	}
	public int getLevel() {
		return level;
	}
	public String getIndentText() {
		if (indentText == null)
			indentText = calculateIndentText();
		return indentText;
	}
	public boolean isThousandsSeparatorEnabled() {
		return thousandsSeparatorEnabled;
	}
	
	
	//
	// Setters
	//
	
	public void setValue(Object value) {
		setValue(value, true);
	}
	private void setValue(Object value, boolean formatText) {
		this.oldValue = this.value;
		Object newValue = value;
		if (Utils.propertyChanged(oldValue, newValue) || isFormatEmptyTextEnabled() && newValue != null && newValue.equals(Constants.VOID)) {
			
			boolean isNumberText = getType() == TYPE_NUMBER && !(newValue instanceof Number);
			boolean isDateText = getType() == TYPE_DATE && !(newValue instanceof Date) && !(newValue instanceof Calendar);
			boolean isBooleanText = getType() == TYPE_BOOLEAN && !(newValue instanceof Boolean);
			boolean isColorText = getType() == TYPE_COLOR && !(newValue instanceof Color);
			boolean isFontText = getType() == TYPE_FONT && !(newValue instanceof Font);
			//No lanzamos el propertyChange cuando es un número o fecha en texto,
			//ya que en ese caso se volverá a asignar valor al formatear el texto
			boolean asignValueOnFormat = isNumberText || isDateText || isBooleanText || isColorText || isFontText;
			
			//fireValueChangeOnFormat = false;
			if (!asignValueOnFormat) {
				this.value = newValue;
				//Lanzaremos el propertyChange cuando ya se haya asignado el texto formateado en setFormattedText(...)
				//firePropertyChange(PROPERTY_VALUE, this.oldValue, newValue);
			}
			
			if (formatText)
				formatText(value);
		}
	}
	private void setFormattedText(String formattedText) {
		String oldValue = this.formattedText;
		String newValue = formattedText;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.formattedText = formattedText;
			//Si ha cambiado el valor, lanzamos el propertyChange aquí, una vez asignado el valor del texto formateado
			if (Utils.propertyChanged(this.oldValue, this.value))
				firePropertyChange(PROPERTY_VALUE, this.oldValue, this.value);
			
			firePropertyChange(PROPERTY_FORMATTED_TEXT, oldValue, newValue);
		}
	}
	
	public void setCapsExcludeList(List<String> capsExcludeList) {
		List<String> oldValue = this.capsExcludeList;
		List<String> newValue = capsExcludeList;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.capsExcludeList = capsExcludeList;
			firePropertyChange(PROPERTY_CAPS_EXCLUDE_LIST, oldValue, newValue);
		}
	}
	public void setType(int type) {
		int oldValue = this.type;
		int newValue = type;
		if (oldValue != newValue) {
			this.type = type;
			firePropertyChange(PROPERTY_TYPE, oldValue, newValue);
		}
	}
	public void setDecimals(int decimals) {
		int oldValue = this.decimals;
		int newValue = decimals;
		if (oldValue != newValue) {
			this.decimals = decimals;
			firePropertyChange(PROPERTY_DECIMALS, oldValue, newValue);
		}
	}
	public void setMaxValue(double maxValue) {
		double oldValue = this.maxValue;
		double newValue = maxValue;
		if (oldValue != newValue) {
			this.maxValue = maxValue;
			if (getValueNumber().doubleValue() > newValue)
				setValue(new Double(newValue));//, false);
			firePropertyChange(PROPERTY_MAX_VALUE, oldValue, newValue);
		}
	}
	public void setMinValue(double minValue) {
		double oldValue = this.minValue;
		double newValue = minValue;
		if (oldValue != newValue) {
			this.minValue = minValue;
			if (getValueNumber().doubleValue() < newValue)
				setValue(new Double(newValue));//, false);
			firePropertyChange(PROPERTY_MIN_VALUE, oldValue, newValue);
		}
	}
	public void setMaxCharacters(int maxCharacters) {
		int oldValue = this.maxCharacters;
		int newValue = maxCharacters;
		if (oldValue != newValue) {
			this.maxCharacters = maxCharacters;
			firePropertyChange(PROPERTY_MAX_CHARACTERS, oldValue, newValue);
		}
	}
	public void setCapitalizeType(int capitalizeType) {
		int oldValue = this.capitalizeType;
		int newValue = capitalizeType;
		if (oldValue != newValue) {
			this.capitalizeType = capitalizeType;
			firePropertyChange(PROPERTY_CAPITALIZE_TYPE, oldValue, newValue);
		}
	}
	public void setFormatEmptyText(boolean formatEmptyText) {
		boolean oldValue = this.formatEmptyTextEnabled;
		boolean newValue = formatEmptyText;
		if (oldValue != newValue) {
			this.formatEmptyTextEnabled = formatEmptyText;
			firePropertyChange(PROPERTY_FORMAT_EMPTY_TEXT_ENABLED, oldValue, newValue);
		}
	}
	public void setPrefix(String prefix) {
		String oldValue = this.prefix;
		String newValue = prefix;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.prefix = prefix;
			firePropertyChange(PROPERTY_PREFIX, oldValue, newValue);
		}
	}
	public void setPostfix(String postfix) {
		String oldValue = this.postfix;
		String newValue = postfix;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.postfix = postfix;
			firePropertyChange(PROPERTY_POSTFIX, oldValue, newValue);
		}
	}
	public void setIndentLevelEnabled(boolean indentLevelEnabled) {
		boolean oldValue = this.indentLevelEnabled;
		boolean newValue = indentLevelEnabled;
		if (oldValue != newValue) {
			this.indentLevelEnabled = indentLevelEnabled;
			setIndentText(calculateIndentText());
			firePropertyChange(PROPERTY_INDENT_LEVEL_ENABLED, oldValue, newValue);
		}
	}
	public void setLevel(int level) {
		int oldValue = this.level;
		int newValue = level;
		if (oldValue != newValue) {
			this.level = level;
			setIndentText(calculateIndentText());
			firePropertyChange(PROPERTY_LEVEL, oldValue, newValue);
		}
	}
	private void setIndentText(String indentText) {
		String oldValue = this.indentText;
		String newValue = indentText;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.indentText = indentText;
			firePropertyChange(PROPERTY_INDENT_TEXT, oldValue, newValue);
		}
	}
	public void setThousandsSeparatorEnabled(boolean thousandsSeparatorEnabled) {
		boolean oldValue = this.thousandsSeparatorEnabled;
		boolean newValue = thousandsSeparatorEnabled;
		if (oldValue != newValue) {
			this.thousandsSeparatorEnabled = thousandsSeparatorEnabled;
			firePropertyChange(PROPERTY_THOUSANDS_SEPARATOR_ENABLED, oldValue, newValue);
		}
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		int oldValue = this.period;
		int newValue = period;
		if (oldValue != newValue) {
			this.period = period;
			setIndentText(calculateIndentText());
			firePropertyChange(PROPERTY_PERIOD, oldValue, newValue);
		}
	}
	
	public void setClassType(Class<?> classType) {
		this.classType = classType;
		int type = getType(classType);
		setType(type);
		if (type == TYPE_NUMBER && Numbers.isNumberDecimalClass(classType) && getDecimals() == 0)
			setDecimals(2);
	}

	public static boolean isFormateableType(Class<?> classType) {
		
		if (String.class.isAssignableFrom(classType)
		 || Date.class.isAssignableFrom(classType)
		 || Calendar.class.isAssignableFrom(classType)
		 || Boolean.class.isAssignableFrom(classType)
		 || boolean.class.isAssignableFrom(classType)
		 || Color.class.isAssignableFrom(classType)
		 || Font.class.isAssignableFrom(classType)
		 || Numbers.isNumberClass(classType)) {
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public Class<?> getClassType() {
		return classType;
	}
	
	public static Object getValueField(String encodedField) {
		Object valueField = null;
		try {			
			if (encodedField.startsWith(PREFIX_FIELD)) {
				String[] tokens = Strings.split(encodedField, Constants.POINT);
				String sourceName = tokens[0].substring(PREFIX_FIELD.length());
				Object source = App.getMapObjectsByName().get(sourceName);
				if (source != null) {
					
					String[] fieldNamesTree = new String[tokens.length-1];
					String modifierPlus = null;
					String modifierMinus = null;
					for (int i = 1; i < tokens.length; i++) {
						String fieldName = tokens[i];
						if (i == tokens.length-1) {
							String[] tokensModPlus = Strings.split(fieldName, Constants.AT+Constants.PLUS);
							if (tokensModPlus.length > 1) {
								fieldName = tokensModPlus[0];
								modifierPlus = tokensModPlus[1];
							}
							else {
								String[] tokensModMinus = Strings.split(fieldName, Constants.AT+Constants.MINUS);
								if (tokensModMinus.length > 1) {
									fieldName = tokensModMinus[0];
									modifierMinus = tokensModMinus[1];
								}
							}
						}
						fieldNamesTree[i-1] = fieldName;
					}
					ReflectAccessSupport ras = new ReflectAccessSupport(source);
					valueField = ras.getFieldValue(fieldNamesTree, true);
					if (valueField != null && valueField instanceof Color) {
						Color color = (Color) valueField;
						if (modifierPlus != null) {
							double factor = Numbers.getNumberValue(modifierPlus).doubleValue();
							valueField = Colors.darker(color, factor);
						}
						else if (modifierMinus != null) {
							double factor = Numbers.getNumberValue(modifierMinus).doubleValue();
							valueField = Colors.brighter(color, factor);
						}
					}
				}
			}
		} catch (Exception ex) {
			Console.printException(ex);
		}
		return valueField;
	}
}
