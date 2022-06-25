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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import linaje.App;
import linaje.logs.Console;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class ReferencedColor extends Color {

	private static final String VALUE_FIELD_NAME = "value";
	
	private Color color = null;
	private Color referenceColor = null;
	private double luminanceFactor = 0;
	private Field[] colorFieldTree = null;
	private Object source = null;
	private String path = null;
	
	public ReferencedColor(String encodedUIColor) throws NoSuchFieldException, IllegalAccessException, SecurityException, InvocationTargetException {
		super(0);
		setColor(encodedUIColor);
	}
	public ReferencedColor(Object source, Field[] colorFieldTree, double luminanceFactor) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(0);
		setColor(source, colorFieldTree, luminanceFactor);
	}
	public ReferencedColor(Object source, String path) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(0);
		this.source = source;
		setColor(path);
	}
	public ReferencedColor(Object source, String path, double luminanceFactor) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(0);
		this.source = source;
		setColor(path);
		setLuminanceFactor(luminanceFactor);
	}

	private void updateColor() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		Color color = getReferenceColor();
		if (color instanceof ReferencedColor && color != this) {
			((ReferencedColor) color).reload();
		}
		if (getLuminanceFactor() > 0)
			color = Colors.darker(color, getLuminanceFactor());
		else if (getLuminanceFactor() < 0)
			color = Colors.brighter(color, Math.abs(getLuminanceFactor()));
		
		updateBaseColor(color);
	}

	private void updateBaseColor(Color baseColor) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (baseColor == null)
			baseColor = Color.white;
		this.color = baseColor;
		int rgb = baseColor.getRGB();
		Field valueField = Color.class.getDeclaredField(VALUE_FIELD_NAME);
		valueField.setAccessible(true);
		valueField.setInt(this, rgb);
	}
	
	public Color getReferenceColor() throws IllegalAccessException, SecurityException, InvocationTargetException {
		if (referenceColor == null) {
			ReflectAccessSupport ras = new ReflectAccessSupport(getFieldColorOwner());
			referenceColor = (Color) ras.getFieldValue(getColorFieldTree()[getColorFieldTree().length-1], true);
		}
		return referenceColor;
	}
	
	public void setColor(String encodedUIColor) throws NoSuchFieldException, IllegalAccessException, SecurityException, InvocationTargetException {
		
		//Eliminamos todo lo que hay antes y después de los corchetes
		int beginIndex = encodedUIColor.indexOf(Constants.BRACKET_OPEN);
		int endIndex = encodedUIColor.lastIndexOf(Constants.BRACKET_CLOSE);
		String encodedUIColorValue = beginIndex != -1 && endIndex != -1 ? encodedUIColor.substring(beginIndex+1, endIndex) : encodedUIColor;
		
		//Buscamos el objeto que contiene el color
		String[] tokens = Strings.split(encodedUIColorValue, Constants.POINT);
		String sourceName = tokens[0];
		Object source = App.getMapObjectsByName().get(sourceName);
		
		int startIndexTree = 1;
		int fieldsTreeLength = tokens.length-1;
		if (source == null && getSource() != null) {
			source = getSource();
			if (!sourceName.equals("this") && !sourceName.equals(source.getClass().getSimpleName()) && !sourceName.equals(source.getClass().getName())) {
				//Se ha mandado el path de campos sin el source
				startIndexTree = 0;
				fieldsTreeLength = tokens.length;
			}
		}
		
		if (source != null) {
			
			String[] fieldNamesTree = new String[fieldsTreeLength];
			String modifierPlus = null;
			String modifierMinus = null;
			for (int i = startIndexTree; i < tokens.length; i++) {
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
				fieldNamesTree[i-startIndexTree] = fieldName;
			}
			
			ReflectAccessSupport ras = new ReflectAccessSupport(source);
			Field[] fieldTree = new Field[fieldNamesTree.length];
			for (int i = 0; i < fieldNamesTree.length; i++) {
				String fieldName = fieldNamesTree[i];
				Field field = ras.findField(fieldName, 2);
				fieldTree[i] = field;
				if (i < fieldNamesTree.length - 1) {
					Object fieldOwner = ras.getFieldValue(field, false);
					ras = new ReflectAccessSupport(fieldOwner);
				}
			}
			
			double factor = 0;
			if (modifierPlus != null) {
				//Formateamos el número del factor con puntos para que no haya problemas con el Locale
				//modifierPlus = Strings.replace(modifierPlus, Constants.COMMA, Constants.POINT);
				factor = Numbers.getNumberValue(modifierPlus).doubleValue();
			}
			else if (modifierMinus != null) {
				//Formateamos el número del factor con puntos para que no haya problemas con el Locale
				//modifierMinus = Strings.replace(modifierMinus, Constants.COMMA, Constants.POINT);
				factor = Numbers.getNumberValue("-"+modifierMinus).doubleValue();
			}
			setColor(source, fieldTree, factor);
		}
	}
	
	public void setColor(Object source, Field[] colorFieldTree, double luminanceFactor) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		this.path = null;
		this.referenceColor = null;
		this.colorFieldTree = colorFieldTree;
		this.source = source;
		this.luminanceFactor = luminanceFactor;
		updateColor();
	}
	
	/**
	 * El Objeto que contendrá el color será el penultimo del arbol de campos
	 * Si sólo hay un campo en el arbol, será source 
	 **/
	public Object getFieldColorOwner() throws IllegalAccessException, SecurityException, InvocationTargetException {
		
		Object fieldOwner = getSource();
		
		if (getColorFieldTree().length > 1) {
			for (int i = 0; i < getColorFieldTree().length-1; i++) {
				ReflectAccessSupport ras = new ReflectAccessSupport(fieldOwner);
				Field fieldParent = getColorFieldTree()[i];
				fieldOwner = ras.getFieldValue(fieldParent, true);
			}
		}
		
		return fieldOwner;
	}
	
	public String getPath() {
		if (path == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(App.getObjectName(getSource()));
			for (int i = 0; i < getColorFieldTree().length; i++) {
				Field field = getColorFieldTree()[i];
				sb.append(Constants.POINT);
				sb.append(field.getName());
			}
			path = sb.toString();
		}
		return path;
	}
	
	public String encode() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append(Constants.BRACKET_OPEN);
		sb.append(getPath());
		
		if (getLuminanceFactor() != 0) {
			//Formateamos el número del factor con comas para que no haya problemas con los puntos del arbol de campos
			String factorString = Strings.replace(String.valueOf(getLuminanceFactor()), Constants.POINT, Constants.COMMA);
			if (getLuminanceFactor() > 0)
				factorString = Constants.PLUS + factorString;
			sb.append(Constants.AT);
			sb.append(factorString);
		}
		
		sb.append(Constants.BRACKET_CLOSE);
		
		return sb.toString();
	}
	
	public static ReferencedColor decode(String encodedUIColor) {
		try {
			return new ReferencedColor(encodedUIColor);
		} catch (Exception ex) {
			Console.printException(ex);
			return null;
		}
	}
	
	public Color getColor() {
		return color;
	}
	public Field[] getColorFieldTree() {
		return colorFieldTree;
	}
	public Object getSource() {
		return source;
	}
	public double getLuminanceFactor() {
		return luminanceFactor;
	}
	
	public void setLuminanceFactor(double luminanceFactor) {
		this.luminanceFactor = luminanceFactor;
		try {
			updateColor();
		} catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	public static List<ReferencedColor> getColors(Object source, String... childFieldsColors) {
		return getColors(source, false, childFieldsColors);
	}
	public static List<ReferencedColor> getColors(Object source, boolean includeChildStateColorValues, String... childFieldsColors) {
		List<ReferencedColor> colors = Lists.newList();
		addColors(colors, source, null, includeChildStateColorValues);
		if (childFieldsColors != null) {
			ReflectAccessSupport reflectAccessSupport = new ReflectAccessSupport(source);
			try {
				List<Field> fields = reflectAccessSupport.findFields(childFieldsColors);
				for (int i = 0; i < fields.size(); i++) {
					try {
						Field fieldParent = fields.get(i);
						addColors(colors, source, new Field[]{fieldParent}, includeChildStateColorValues);
					}
					catch (Exception ex) {
						Console.printException(ex);
					}
				}
			} catch (Exception ex) {
				Console.printException(ex);
			}
		}
		return colors;
	}
	
	private static void addColors(List<ReferencedColor> colors, Object source, Field[] fieldsParents, boolean includeChildStateColorValues) {
		
		Field fieldParent = Lists.getLastElement(fieldsParents);
		Class<?> colorFieldsOwnerType = fieldParent != null ? fieldParent.getType() : source.getClass();		
		
		int superClassesToSearch = 0;
		Class<?> superClass = colorFieldsOwnerType.getSuperclass();
		while (superClass != null && FieldsChangesNotifier.class.isAssignableFrom(superClass)) {
			superClassesToSearch++;
			superClass = superClass.getSuperclass();
		}
			
		Field[] allFields = ReflectAccessSupport.getDeclaredFields(colorFieldsOwnerType, superClassesToSearch);	
		Field[] colorFields = ReflectAccessSupport.filterFields(allFields, Color.class, true, Color.class, StateColor.class);
		//Field[] colorFields = ReflectAccessSupport.filterFields(allFields, Color.class, true, Color.class);
		
		for (int i = 0; i < colorFields.length; i++) {
			try {			
				Field field = colorFields[i];
				int fieldModifiers = field.getModifiers();
				if (!Modifier.isFinal(fieldModifiers)) {
					
					Field[] colorFieldTree;
					if (fieldParent != null) {
						colorFieldTree = Lists.concat(Field.class, fieldsParents, field);
					}
					else {
						colorFieldTree = new Field[]{field};
					}
					
					ReferencedColor uiColor = new ReferencedColor(source, colorFieldTree, 0);
					colors.add(uiColor);
					
					if (includeChildStateColorValues && StateColor.class.isAssignableFrom(field.getType())) {
						Field stateValuesField = ReflectAccessSupport.findField("stateValues", StateColor.class);
						colorFieldTree = Lists.concat(Field.class, colorFieldTree, stateValuesField);
						addColors(colors, source, colorFieldTree, false);
					}
				}
			} catch (Exception ex) {
				Console.printException(ex);
			}
		}
	}
	
	public void reload() {
		referenceColor = null;
		try {
			updateColor();
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	@Override
	public int getRGB() {
		return super.getRGB();
	}
	@Override
	public String toString() {
		return encode();
	}
	@Override
	public ReferencedColor clone() {
		try {
			return new ReferencedColor(getSource(), getColorFieldTree(), getLuminanceFactor());
		} catch (Exception e) {
			try {
				return (ReferencedColor) super.clone();
			} catch (CloneNotSupportedException e2) {
				return null;
			}
		}
	}
}
