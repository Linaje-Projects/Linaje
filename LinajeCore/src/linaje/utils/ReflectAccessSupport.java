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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import linaje.logs.Console;
import linaje.statics.Constants;

public class ReflectAccessSupport {

	public static final String GET = "get";
	public static final String SET = "set";
	public static final String IS = "is";
	
	private Object source = null;
	private int superClassesToSearch = 2;
	
	//DEFAULT_UNTYPED_FIELD_KEY se usa para definir el tipo T de una clase tipo Clase<T>
	private Class<?> defaultUntypedFieldsTypes = null;
	
	
	public ReflectAccessSupport(Object source) {
		this(source, 2);
	}
	public ReflectAccessSupport(Object source, int superClassesToSearch) {
		this.source = source;
		setSuperClassesToSearch(superClassesToSearch);
	}
	
	//
	// Search for fields
	//
	
	public Field findField(String fieldName) throws NoSuchFieldException {
		return findField(fieldName, getSuperClassesToSearch());
	}
	public Field findField(String fieldName, int superClassesToSearch) throws NoSuchFieldException {
		return findField(fieldName, source.getClass() == Class.class ? (Class<?>) source : source.getClass(), superClassesToSearch);
	}
	public static Field findField(String fieldName, Class<?> fieldOwnerClass) throws NoSuchFieldException {
		return findField(fieldName, fieldOwnerClass, 0);
	}
	public static Field findField(String fieldName, Class<?> fieldOwnerClass, int superClassesToSearch) throws NoSuchFieldException {
		Field field = null;
		try {
			if (fieldName != null)
				field = fieldOwnerClass.getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException ex) {
			if (superClassesToSearch > 0) {
				Class<?> superClass = fieldOwnerClass.getSuperclass();
				if (superClass != null)
					field = findField(fieldName, superClass, superClassesToSearch--);
			}
			if (field == null)
				throw ex;
		}
		return field;
	}
	
	public List<Field> findFields(String... fieldNames) throws NoSuchFieldException {
		return findFields(getSuperClassesToSearch(), fieldNames);
	}
	public List<Field> findFields(int superClassesToSearch, String... fieldNames) throws NoSuchFieldException {
		List<Field> fieldsList = Lists.newList();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			Field field = findField(fieldNames[i], superClassesToSearch);
			if (field != null)
				fieldsList.add(field);
		}
		return fieldsList;
	}
	//
	// Search for methods
	//
	
	public Method findMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		return findMethod(methodName, getSuperClassesToSearch(), parameterTypes);
	}
	public Method findMethod(String methodName, int superClassesToSearch, Class<?>... parameterTypes) throws NoSuchMethodException {
		return findMethod(methodName, source.getClass() == Class.class ? (Class<?>) source : source.getClass(), superClassesToSearch, parameterTypes);
	}
	public static Method findMethod(String methodName, Class<?> parameterTypes, Class<?> methodOwnerClass) throws NoSuchMethodException {
		return findMethod(methodName, methodOwnerClass, 0, parameterTypes);
	}	
	public static Method findMethod(String methodName, Class<?> methodOwnerClass, int superClassesToSearch, Class<?>... parameterTypes) throws NoSuchMethodException {
		Method method = null;
		try {
			method = methodOwnerClass.getDeclaredMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException ex) {
			if (superClassesToSearch > 0) {
				Class<?> superClass = methodOwnerClass.getSuperclass();
				if (superClass != null)
					method = findMethod(methodName, superClass, superClassesToSearch--, parameterTypes);
			}
			if (method == null)
				throw ex;
		}
		return method;
	}
	
	public static Method[] findMethods(String methodNameOrRegex, Method[] methods) {
		List<Method> findedMethods = Lists.newList();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().matches(methodNameOrRegex))
				findedMethods.add(method);
		}
		return Lists.listToArray(findedMethods, Method.class);
	}
	
	public static Method[] findMethodsUnknownTypeParameters(String methodNameOrRegex, Class<?> methodOwnerClass, int superClassesToSearch) throws NoSuchMethodException {
		
		List<Method> findedMethods = Lists.newList();
		Method[] classMethods = methodOwnerClass.getDeclaredMethods();
		
		Method[] methods = findMethods(methodNameOrRegex, classMethods);
		Lists.addElements(findedMethods, methods);
		
		if (superClassesToSearch > 0) {
			Class<?> superClass = methodOwnerClass.getSuperclass();
			for (int i = 0; i < superClassesToSearch && superClass != null; i++) {
				Method[] superClassMethods = methodOwnerClass.getDeclaredMethods();
				methods = findMethods(methodNameOrRegex, superClassMethods);
				Lists.addElements(findedMethods, methods);
				superClass = superClass.getSuperclass();
			}
		}

		return Lists.listToArray(findedMethods, Method.class);
	}
	
	public static Method findMethodGet(Field field) {
		return findMethodGet(field.getName(), field.getDeclaringClass());
	}
	public static Method findMethodGet(String fieldName, Class<?> methodOwnerClass) {
		
		String fieldNameCap = Strings.capitalize(fieldName, false);
		String methodName = Constants.GET + fieldNameCap;
		Method methodGet = null;
		try {
			methodGet = methodOwnerClass.getMethod(methodName);
		} catch (NoSuchMethodException ex1) {
			methodName = Constants.IS + fieldNameCap;
			try {
				methodGet = methodOwnerClass.getMethod(methodName);
			} catch (NoSuchMethodException ex2) {}
		}
		
		return methodGet;
	}
	
	public Method findMethodSet(Field field) {
		return findMethodSet(field.getName(),  getType(field), field.getDeclaringClass());
	}
	public static Method findMethodSet(Field field, Class<?> defaultUntypedFieldsType) {
		return findMethodSet(field.getName(),  getType(field, defaultUntypedFieldsType), field.getDeclaringClass());
	}
	public static Method findMethodSet(String fieldName,  Class<?> fieldType, Class<?> methodOwnerClass) {

		String fieldNameCap = Strings.capitalize(fieldName, false);
		String methodName = Constants.SET + fieldNameCap;
		Method methodSet = null;
		try {
			methodSet = methodOwnerClass.getMethod(methodName, fieldType);
		} catch (NoSuchMethodException ex1) {
		}
		
		return methodSet;
	}
	

	//
	// Get classes from package 
	//
	
	public static List<Class<?>> getClassesFromPackage(Package pkg) {

		List<Class<?>> classes = Lists.newList();
		String packageNameRes = pkg.getName().replace('.', '/');
		try {
			List<String> packageResources = Resources.getResourceNamesFromResourceDir(packageNameRes, true, false);
			for (String resourceName : packageResources) {
				if (resourceName.toLowerCase().endsWith(".class") && resourceName.indexOf('$') == -1) {
					String className = resourceName.substring(0, resourceName.length() - 6).replace('/', '.');
					Class<?> c = Class.forName(className);
					classes.add(c);
				}
			}
		}
		catch (IOException | ClassNotFoundException ex) {
			Console.printException(ex);
		}
		
		return classes;
	}
	
	
	//
	// Assign field values 
	//
	
	public void setFieldValue(String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalAccessException, InvocationTargetException {
		setFieldValue(fieldName, value, false);
	}
	public void setFieldValue(String fieldName, Object value, boolean useSetter) throws NoSuchFieldException, SecurityException, IllegalAccessException, InvocationTargetException {
		setFieldValue(fieldName, source.getClass() == Class.class ? (Class<?>) source : source.getClass(), value, useSetter);
	}
	public void setFieldValue(String fieldName, Class<?> fieldOwnerClass, Object value, boolean useSetter) throws NoSuchFieldException, SecurityException, IllegalAccessException, InvocationTargetException {
		Field field = fieldOwnerClass.getDeclaredField(fieldName);
		setFieldValue(field, value, useSetter);
	}
	public void setFieldValue(Field field, Object value) throws IllegalAccessException, SecurityException, InvocationTargetException {
		setFieldValue(field, value, false);
	}
	public void setFieldValue(Field field, Object value, boolean useSetter) throws IllegalAccessException, SecurityException, InvocationTargetException {
		if (useSetter) {
			Method setMethod = findMethodSet(field);
			invokeMethod(setMethod, value);
		}
		else {
			if (!field.isAccessible())
				field.setAccessible(true);		
			field.set(source, value);
		}
	}
	
	public void setFindedFieldsValues(List<String> fieldNames, List<Object> values, boolean useSetter) throws NoSuchFieldException, SecurityException, IllegalAccessException, InvocationTargetException {
		for (int i = 0; i < fieldNames.size(); i++) {
			setFieldValue(fieldNames.get(i), values.get(i), useSetter);
		}
	}
	
	public void setFieldsValues(List<Field> fields, List<?> values, boolean useSetter) throws IllegalAccessException, SecurityException, InvocationTargetException {
		for (int i = 0; i < fields.size(); i++) {
			setFieldValue(fields.get(i), values.get(i), useSetter);
		}
	}
	
	//
	// Get field values 
	//
	
	public Object getFieldValue(Field field, boolean useGetter) throws IllegalAccessException, SecurityException, InvocationTargetException {
		if (useGetter) {
			Method methodGet = findMethodGet(field);
			return invokeMethod(methodGet);
		}
		else {
			if (!field.isAccessible())
				field.setAccessible(true);		
			return field.get(source);
		}
	}
	
	//
	// Invoke methods
	//
	
	public Object invokeMethod(String methodName, Class<?> methodOwnerClass, Class<?> parameterType, Object parameterValue) throws NoSuchMethodException, InvocationTargetException, SecurityException, IllegalAccessException {
		Method method = methodOwnerClass.getDeclaredMethod(methodName, parameterType);
		return invokeMethod(method, parameterValue);
	}
	public Object invokeMethod(Method method, Object... parameterValues) throws InvocationTargetException, IllegalAccessException, SecurityException {
		if (method != null) {
			if (!method.isAccessible())
				method.setAccessible(true);
			return method.invoke(source, parameterValues);
		}
		else return null;
	}
	
	//
	// Filter fields
	//
	
	public Field[] filterFields(Field[] fields, boolean includeMatches, Class<?>... fieldTypes) {
		return filterFields(fields, getDefaultUntypedFieldTypes(), includeMatches, fieldTypes);
	}
	public static Field[] filterFields(Field[] fields, Class<?> defaultUntypedFieldsType, boolean includeMatches, Class<?>... fieldTypes) {
		
		if (fieldTypes != null && fieldTypes.length > 0) {
			
			List<Field> filteredFields = Lists.newList();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				Class<?> fieldType = getType(field, defaultUntypedFieldsType);
				boolean matchesFilter = Lists.arrayContains(fieldTypes, fieldType);
				if ((matchesFilter && includeMatches) || (!matchesFilter && !includeMatches))
					filteredFields.add(field);
			}
			return Lists.listToArray(filteredFields, Field.class);
		}
		else {
			return fields;
		}
	}
	
	public static Field[] filterFields(Field[] fields, boolean includeMatches, String regex, String... fieldNames) {
		
		if (regex != null || (fieldNames != null && fieldNames.length > 0)) {
			
			List<Field> filteredFields = Lists.newList();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				boolean matchesFilter = (regex != null && fieldName.matches(regex)) || (fieldNames != null && Lists.arrayContains(fieldNames, fieldName));
				if ((matchesFilter && includeMatches) || (!matchesFilter && !includeMatches))
					filteredFields.add(field);
			}
			return Lists.listToArray(filteredFields, Field.class);
		}
		else {
			return fields;
		}
	}
	
	public void encodeFieldsValues(StringBuffer sb, String prefix, String... fieldNames) {
		try {
			List<Field> fields = findFields(fieldNames);
			if (!fields.isEmpty())
				encodeFieldsValues(sb, prefix, Lists.listToArray(fields, Field.class));
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	public void encodeFieldsValues(StringBuffer sb, String prefix, Field... fields) {
		
		if (fields == null)
			return;
		
		if (prefix == null)
			prefix = Constants.VOID;
		
		for (int i = 0; i < fields.length; i++) { 
			encodeFieldValue(sb, prefix, fields[i]);
		}
	}
	
	public void encodeFieldValue(StringBuffer sb, String prefix, Field field) {
		
		if (field == null)
			return;
		
		if (prefix == null)
			prefix = Constants.VOID;
		
		try {
			String fieldName = field.getName();
			String defaultKey = prefix + fieldName;
			Object value = getFieldValue(field, false);
			sb.append(defaultKey);
			sb.append(Constants.EQUAL);
			FormattedData formattedData = new FormattedData(value);
			sb.append(formattedData.getFormattedText());
			sb.append(Constants.LINE_SEPARATOR_SYSTEM);
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	public void setEncodedFieldValues(String prefix, String... encodedFields) {
		
		if (encodedFields == null)
			return;
		if (prefix == null)
			prefix = Constants.VOID;
		
		for (int i = 0; i < encodedFields.length; i++) {
			String encodedField = encodedFields[i];
			if (encodedField.startsWith(prefix) && !encodedField.startsWith(Constants.HASH)) {
				try {
					String[] keyValue = Strings.split(encodedField.substring(prefix.length()), Constants.EQUAL, 2);
					String fieldNames = keyValue[0];
					String encodedValue = keyValue[1];
					String[] fieldNamesTree = Strings.split(fieldNames, Constants.POINT);
					setEncodedFieldValue(fieldNamesTree, encodedValue);
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
		}
	}
	
	public void setEncodedFieldValue(String[] fieldNamesTree, String encodedValue) throws NoSuchFieldException, ParseException, InvocationTargetException, IllegalAccessException, SecurityException {
		//Si viene algo del tipo field1.field2=value,
		//primero obtendremos el campo field1
		//para luego buscar dentro de él, el campo field2 al que finalmente asignaremos el valor
		ReflectAccessSupport ras = this;
		for (int i = 0; i < fieldNamesTree.length; i++) {
			String fieldName = fieldNamesTree[i];
			Field field = ras.findField(fieldName, getSuperClassesToSearch());
			if (i == fieldNamesTree.length - 1) {
				Class<?> classType = getType(field);
				Object value = decodeValue(encodedValue, classType);
				ras.setFieldValue(field, value, true);
			}
			else {
				Object fieldValue = ras.getFieldValue(field, false);
				ras = new ReflectAccessSupport(fieldValue, getSuperClassesToSearch());
			}
		}
	}
	
	protected Object decodeValue(String encodedValue, Class<?> classType) throws ParseException {
		
		Object value;
		if (classType == String[].class)
			value = Strings.split(encodedValue, Constants.COMMA);
		else if (classType == List.class)
			value = Lists.arrayToList(Strings.split(encodedValue, Constants.COMMA));
		else if (classType == Vector.class)
			value = Lists.arrayToVector(Strings.split(encodedValue, Constants.COMMA));
		else if (classType == Vector.class)
			value = Lists.arrayToVector(Strings.split(encodedValue, Constants.COMMA));
		else
			value = FormattedData.getValueObject(encodedValue, classType);
		
		return value;
	}
	
	public Object getFieldValue(String fieldName, boolean useGetter) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
		Field field = findField(fieldName, getSuperClassesToSearch());
		return getFieldValue(field, useGetter);
	}
	public Object getFieldValue(String[] fieldNamesTree, boolean useGetter) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
		
		Object value = null;
		ReflectAccessSupport ras = this;
		for (int i = 0; i < fieldNamesTree.length; i++) {
			String fieldName = fieldNamesTree[i];
			Field field = ras.findField(fieldName, getSuperClassesToSearch());
			if (i == fieldNamesTree.length - 1) {
				value = ras.getFieldValue(field, useGetter);
			}
			else {
				Object fieldValue = ras.getFieldValue(field, false);
				ras = new ReflectAccessSupport(fieldValue, getSuperClassesToSearch());
			}
		}
		
		return value;
	}
	
	/**
	 * Se usa para definir el tipo T de una clase tipo Clase<T> 
	 */
	public void setDefaultUntypedFieldTypes(Class<?> defaultUntypedFieldsTypes) {
		this.defaultUntypedFieldsTypes = defaultUntypedFieldsTypes;
	}
	public Class<?> getDefaultUntypedFieldTypes() {
		if (defaultUntypedFieldsTypes == null)
			defaultUntypedFieldsTypes = Object.class;
		return defaultUntypedFieldsTypes;
	}
	
	public Class<?> getReturnType(Method method) {
		return getReturnType(method, getDefaultUntypedFieldTypes());
	}
	public Class<?> getType(Field field) {
		return getType(field, getDefaultUntypedFieldTypes());
	}
	
	public static Class<?> getReturnType(Method method, Class<?> defaultUntypedFieldsType) {
		Class<?> returnType = method.getReturnType();
		if (returnType == Object.class && defaultUntypedFieldsType != null)
			returnType = defaultUntypedFieldsType;
		return returnType;
	}
	public static Class<?> getType(Field field, Class<?> defaultUntypedFieldsType) {
		Class<?> type = field.getType();
		if (type == Object.class && defaultUntypedFieldsType != null)
			type = defaultUntypedFieldsType;
		return type;
	}
	
	public int getSuperClassesToSearch() {
		return superClassesToSearch;
	}
	public void setSuperClassesToSearch(int superClassesToSearch) {
		this.superClassesToSearch = superClassesToSearch;
	}
	
	public Field[] getDeclaredFields() {
		return getDeclaredFields(source.getClass(), getSuperClassesToSearch());
	}
	
	public static Field[] getDeclaredFields(Class<?> sourceClass, int superClassesToSearch) {
		
		Field[] fields = sourceClass.getDeclaredFields();
		Class<?> superClass = sourceClass.getSuperclass();
		for (int i = 0; i < superClassesToSearch && superClass != null; i++) {
			Field[] superClassFields = superClass.getDeclaredFields();
			fields = Lists.concat(Field.class, fields, superClassFields);
			superClass = superClass.getSuperclass();
		}
		return fields;
	}
	
	
	//
	// Instance classes
	//
	
	public static <T> T newInstance(Class<T> cl, Class<?>[] parameterTypes, Object... parameterValues) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = getConstructor(cl, parameterTypes);
		return newInstance(constructor, parameterValues);
	}
	
	public static <T> T newInstance(Constructor<T> constructor, Object... parameterValues) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(parameterValues);
	}
	
	public static <T> Constructor<T> getConstructor(Class<T> cl, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		return cl.getConstructor(parameterTypes);
	}
}
