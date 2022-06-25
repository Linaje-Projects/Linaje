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
package linaje.expressions;

/**
 *  @author: Pablo Linaje (20/12/2005 13:38:21)
 *  @version 00.01
 */
import java.util.*;

import linaje.LocalizedStrings;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Lists;
import linaje.utils.Strings;

public class Variable {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String global;
		public String numeric;
		public String numericDecimal;
		public String date;
		public String text;

		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	public static final String SEPARATOR_ELEMENTS = "~";
	public static final String SEPARATOR_VARIABLES = "»";

	public static final int MAX_CHARS_NAMES = 25;
	
	private String name = null;
	private Class<?> type = null;
	private Object value = null;
	private String description = null;
	private boolean metPartialExpression = false;
	private boolean calledOutOfFunction = false;
	private Vector<Object> values = null;
	private Vector<String> callingFunctions = null;
	private Vector<Object> functionResults = null;

	public Variable(String name) {
		super();
		setName(name);
		setType(String.class);
	}
	public Variable(String name, Class<?> type) {
		super();
		setName(name);
		setType(type);
	}
	public Variable(String name, Class<?> type, Object value) {
		super();
		setName(name);
		setType(type);
		setValue(value);
	}
	
	public static Variable searchVariable(String name, Vector<Variable> variables) {
	
		if (name != null && variables != null) {
	
			try {
	
				Variable variable = null;
				for (int i = 0; i < variables.size(); i++) {
	
					variable = variables.elementAt(i);
					if (variable.getName().equalsIgnoreCase(name))
						return variable;
					else if (variable.getDescription().equalsIgnoreCase(name))
						return variable;
				}
			} catch (Throwable ex) {
				Console.printException(ex);
			}
		} 
		return null;
	}
	
	public boolean isCalledOutOfFunction() {
		return calledOutOfFunction;
	}
	
	public static boolean isVariableNameValid(String variableName) {
	
		if (!variableName.trim().equals(Constants.VOID) && !variableName.trim().equals("true") && !variableName.trim().equals("false")) {
	
			int operatorType = ExpressionsAnalyzer.getOperatorType(variableName);
			if (operatorType == -1) {
				
				char caracter;
				for (int i = 0; i < variableName.length(); i++) {
	
					caracter = variableName.charAt(i);
					if (i == 0 && Character.isDigit(caracter))
						return false; //El name no puede empezar por un número
					else if (!Character.isLetterOrDigit(caracter) && caracter != '_')
						return false; //Sólo puede contener números y letras y el guión bajo '_'
				}
				return true;
			}
			else return false;
		}
		else return false;
	}
	
	public boolean isValid() {
		return true;
	}
	
	public int getNature() {
		
		Class<?> type = getType();
				
		if (type == null)
			return Expression.NATURE_GLOBAL;
		else if (type == Integer.class || type == int.class || type == Long.class || type == long.class || type == Double.class || type == double.class  || type == Float.class || type == float.class)
			return Expression.NATURE_NUMERICAL;
		else if (type == Date.class || type == Calendar.class)
			return Expression.NATURE_DATE;
		else
			return Expression.NATURE_ALPHANUMERIC;
	}
	
	public boolean isMetPartialExpression() {
		return metPartialExpression;
	}
	
	public String getDescription() {
		if (description == null)
			description = getName();
		return description;
	}
	
	public String getNatureDescription() {
		
		Class<?> type = getType();
				
		if (type == null)
			return TEXTS.global;
		else if (type == Integer.class || type == int.class)
			return TEXTS.numeric;
		else if (type == Double.class || type == double.class || type == Float.class || type == float.class || type == Long.class || type == long.class)
			return TEXTS.numericDecimal;
		else if (type == Date.class || type == Calendar.class)
			return TEXTS.date;
		else
			return TEXTS.text;
	}
	
	public Vector<String> getCallingFunctions() {
		if (callingFunctions == null)
			callingFunctions = Lists.newVector();
		return callingFunctions;
	}
	
	public Object getMax() {
		return ExpressionsAnalyzer.getFunctionResult(Expression.OF_MAX, getValues(), getValue());
	}
	
	public Object getMax2() {
		return ExpressionsAnalyzer.getFunctionResult(Expression.OF_MAX2, getValues(), getValue());
	}
	
	public double getAvg() {
		return ExpressionsAnalyzer.getAvg(getValues(), getValue()).doubleValue();
	}
	
	public double getAbs() {
		return ExpressionsAnalyzer.getAbsValue(getValues(), getValue()).doubleValue();
	}
	
	public Object getMin() {
		return ExpressionsAnalyzer.getFunctionResult(Expression.OF_MIN, getValues(), getValue());
	}
	
	public Object getMin2() {
		return ExpressionsAnalyzer.getFunctionResult(Expression.OF_MIN2, getValues(), getValue());
	}
	
	public double getSum() {
		return ExpressionsAnalyzer.getSum(getValues(), getValue()).doubleValue();
	}
	
	public String getName() {
		return name;
	}
	
	public Object getFunctionResult(String function) {
		return ExpressionsAnalyzer.getFunctionResult(function, getValues(), getValue());
	}
	
	public Vector<Object> getFunctionResults() {
		if (functionResults == null)
			functionResults = Lists.newVector();
		return functionResults;
	}
	
	
	public Class<?> getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Vector<Object> getValues() {
		if (values == null)
			values = Lists.newVector();
		return values;
	}
	
	public Object getFunctionValue(String function) {
	
		if (getType() == null && (function.equalsIgnoreCase(Expression.OF_SUM) || function.equalsIgnoreCase(Expression.OF_AVG))) {
	
			//Si la variable es global y estamos en una suma o media, obtenemos los values con el GlobalDataConverter
			Object globalValue = null;
			if (getValue() != null)
				globalValue = GlobalDataConverter.getNonAlphanumericData(getValue().toString());
			
			if (globalValue == null)
				globalValue = getValue();
	
			Vector<Object> globalValues = Lists.newVector();
			for (int i = 0; i < getValues().size(); i++) {
	
				Object v = null;
				if (getValues().elementAt(i) != null)
					v = GlobalDataConverter.getNonAlphanumericData(getValues().elementAt(i).toString());
				
				if (v == null)
					v = getValues().elementAt(i);
	
				globalValues.addElement(v);
			}
			
			return ExpressionsAnalyzer.getFunctionValue(function, globalValues, globalValue);
		}
		else {
			
			return ExpressionsAnalyzer.getFunctionValue(function, getValues(), getValue());
		}
	}
	
	public Object getMaxValue() {
		return ExpressionsAnalyzer.getMaxValue(getValues(), getValue());
	}
	
	public Object getMaxValue2() {
		return ExpressionsAnalyzer.getMaxValue2(getValues(), getValue());
	}
	
	public Object getMinValue() {
		return ExpressionsAnalyzer.getMinValue(getValues(), getValue());
	}
	
	public Object getMinValue2() {
		return ExpressionsAnalyzer.getMinValue2(getValues(), getValue());
	}
	
	public static Variable getVariable(String encodedVariable) {
	
		try {
	
			String name = null;
			Class<?> type = null;
			Object value = null;
			
			String[] elements = Strings.split(encodedVariable, SEPARATOR_ELEMENTS);
			int numElements = elements.length;
			
			if (numElements > 0)
				name = elements[0];
			if (numElements > 1)
				type = Class.forName(elements[1]);
			if (numElements > 2 && !elements[2].equals("null"))
				value = elements[2];
	
			return new Variable(name, type, value);
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	
		return null;
	}
	
	public String getEncodedVariable() {
		
		String type = getType().toString();
		if (type.startsWith("class"))
			type = type.substring(6, type.length());
		
		return getName() + SEPARATOR_ELEMENTS + 
				type + SEPARATOR_ELEMENTS + 
			   getValue();
	}
	
	public static Vector<Variable> getVariables(String encodedVariables) {
		
		Vector<Variable> variables = Lists.newVector();
		try {
	
			if (encodedVariables != null && !encodedVariables.equals(Constants.VOID)) {
				
				String[] arrayEncodedVariables = Strings.split(encodedVariables, SEPARATOR_VARIABLES);
				for (int i = 0; i < arrayEncodedVariables.length; i++) {
					String encodedVariable = arrayEncodedVariables[i];
					Variable variable = getVariable(encodedVariable);
					if (variable != null)
						variables.addElement(variable);
				}
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		
		return variables;
	}
	
	public static String getEncodedVariables(Vector<Variable> variables) {
	
		String encodedVariables = Constants.VOID;
		
		try {
			
			for (int i = 0; i < variables.size(); i++) {
	
				Variable variable = variables.elementAt(i);
	
				if (encodedVariables.equals(Constants.VOID))
					encodedVariables = variable.getEncodedVariable();
				else
					encodedVariables += SEPARATOR_VARIABLES + variable.getEncodedVariable();	
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
			encodedVariables = Constants.VOID;
		}
			
		return encodedVariables;
		
	}
	
	protected void setMetPartialExpression(boolean cumpleExpresionParcial) {
		this.metPartialExpression = cumpleExpresionParcial;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	protected void setCalledOutOfFunction(boolean calledOutOfFunction) {
		this.calledOutOfFunction = calledOutOfFunction;
	}
	private void setName(String name) {
		this.name = name;
	}
	protected void setType(Class<?> type) {
		this.type = type;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return getName();
	}
}
