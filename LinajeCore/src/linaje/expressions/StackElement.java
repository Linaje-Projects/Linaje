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
 *  @author: Pablo Linaje (02/12/2005 11:55:16)
 *  @version 00.01
 */

import java.awt.Color;
import java.util.Date;
import java.util.Vector;

import linaje.LocalizedStrings;
import linaje.statics.Constants;
import linaje.utils.Dates;
import linaje.utils.Lists;
import linaje.utils.Strings;

public class StackElement {
	
	private int operatorType = -1;
	private int positionInText = 0;
	private String text = null;
	public int nature = -1;
	private Object value = null;
	private String alphanumericValue = null;
	private Variable linkedVariable = null;
	private Vector<String> callingFunctions = null;
	private Vector<Object> functionsResults = null;
	
	public static final String DEFAULT_DATE_FORMAT = "d_M_y";
	
	public static final Color COLOR_OPERATOR_ARITHMETIC = Color.blue;
	public static final Color COLOR_OPERATOR_COMPARATIVE = new Color(70, 160, 70);//Verde oscuro apagado
	public static final Color COLOR_OPERATOR_LOGICAL = new Color(128, 0, 0);//Rojo oscuro
	public static final Color COLOR_OPERATOR_SEPARATOR = Color.gray;
	public static final Color COLOR_NATURE_ALPHANUMERIC = Color.gray.darker();
	public static final Color COLOR_NATURE_DATE = new Color(10, 36, 106);
	public static final Color COLOR_NATURE_NUMERICAL = Color.black;
	public static final Color COLOR_NATURE_GLOBAL = new Color(0, 64, 0); //Verde oscuro
	
	private static String defaultDateFormat = null;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String theVariable;
		public String isNotValid;
		public String theElement;
		public String isWrong;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public StackElement(String text, int operatorType, int positionInText) {
		
		super();
		
		setOperatorType(operatorType);
		int position = positionInText - text.length();
		if (position < 0)
			position = 0;
		setPositionInText(position);
		setText(text);
	}
	
	public StackElement(StringBuffer text, int operatorType, int positionInText) {
		
		super();
		
		setOperatorType(operatorType);
		int position = positionInText - text.length();
		if (position < 0)
			position = 0;
		setPositionInText(position);
		setText(text.toString());
	}
	
	public void addCallingFunction(String callingFunction) {
	
		getCallingFunctions().addElement(callingFunction);
		
		if (getLinkedVariable() != null)
			getLinkedVariable().getCallingFunctions().addElement(callingFunction);
	}
	
	public void addFunctionResult(Object result) {
	
		getFunctionsResults().addElement(result);
		
		if (getLinkedVariable() != null)
			getLinkedVariable().getFunctionResults().addElement(result);
	}
	
	public Color getColor() {
		
		Color color = getColorOperator(getOperatorType());
		if (color == null)
			color = getColorNature(getNature());
		if (color == null)
			color = Color.black;
		
		return color;
	}
	
	public static Color getColorOperator(int operatorType) {
	
		Color color = null;
		
		if (operatorType == Expression.OPERATOR_ARITHMETIC)
			color = COLOR_OPERATOR_ARITHMETIC;
		else if (operatorType == Expression.OPERATOR_COMPARATIVE)
			color = COLOR_OPERATOR_COMPARATIVE;
		else if (operatorType == Expression.OPERATOR_LOGICAL)
			color = COLOR_OPERATOR_LOGICAL;
		else if (operatorType == Expression.OPERATOR_SEPARATOR)
			color = COLOR_OPERATOR_SEPARATOR;
					
		return color;
	}
	
	public static Color getColorNature(int nature) {
		
		Color color = null;
		
		if (nature == Expression.NATURE_GLOBAL)
			color = COLOR_NATURE_GLOBAL;	
		else if (nature == Expression.NATURE_ALPHANUMERIC)
			color = COLOR_NATURE_ALPHANUMERIC;
		else if (nature == Expression.NATURE_DATE)
			color = COLOR_NATURE_DATE;
		else
			color = COLOR_NATURE_NUMERICAL;
					
		return color;
	}
	
	public static Date getDate(String formattedDate) {
	
		try {
	
			Date date = Dates.getDate(formattedDate, getDefaultDateFormat());
			if (date == null)
				date = Dates.getDate(formattedDate);
			
			return date;	
		}
		catch (Throwable ex) {
			return null;
		}
	}
	
	public static String getFormattedDate(Date date) {
		return Dates.getFormattedDate(date, getDefaultDateFormat());
	}
	
	private Vector<String> getCallingFunctions() {
		if (callingFunctions == null)
			callingFunctions = Lists.newVector();
		return callingFunctions;
	}
	public int getNature() {
		return nature;
	}
	public int getPositionInText() {
		return positionInText;
	}
	public Vector<Object> getFunctionsResults() {
		if (functionsResults == null)
			functionsResults = Lists.newVector();
		return functionsResults;
	}
	public String getText() {
		return text;
	}
	public int getOperatorType() {
		return operatorType;
	}
	
	public Object getValue() throws ExpressionException {
		if (value == null)
			initElement(null);
		return value;
	}
	
	public String getAlphanumericValue() throws ExpressionException {
		
		if (alphanumericValue == null) {
			
			if (getValue() == null)
				return null;
			else if (getLinkedVariable() != null)
				return getValue().toString().trim();
			else
				return getValue().toString();
		}
		
		return alphanumericValue;
	}
	
	protected Variable getLinkedVariable() {
		return linkedVariable;
	}
	
	public void initElement(Vector<Variable> variables) throws ExpressionException {
	
		String text = getText();
		if (text != null && getOperatorType() == -1) {
			
			if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false")) {
				
				setNature(Expression.NATURE_LOGICAL);
				setValue(new Boolean(text));
			}
			else if (text.startsWith("'")) {
	
				setNature(Expression.NATURE_ALPHANUMERIC);
				if (text.length() > 1)
					setValue(text.substring(1, text.length() - 1));
				else
					setValue(Constants.VOID);
			}
			else {
	
				Variable variable = Variable.searchVariable(text, variables);
				if (variable != null) {
	
					if (variable.isValid()) {
						
						int nature = variable.getNature();
	
						setNature(nature);
						setValue(variable.getValue());
						setLinkedVariable(variable);
						variable.setMetPartialExpression(false);
					}
					else {
	
						throw new ExpressionException(TEXTS.theVariable + variable.getName() + TEXTS.isNotValid, getPositionInText());
					}
				}
				else {
					try {
						//Miramos si es un número
						String formattedNumber = Strings.replace(text, ",", ".");
						if (formattedNumber.indexOf(".") != -1)
							setValue(new Double(formattedNumber));
						else
							setValue(new Integer(formattedNumber));
						setNature(Expression.NATURE_NUMERICAL);
					} 
					catch (Throwable ex1) {
						//Miramos si es una fecha del tipo DD_MM_AAAA
						try {
							Date date = getDate(text);
							if (date != null) {
								setNature(Expression.NATURE_DATE);
								setValue(date);
							}
							else {
								throw new ExpressionException(TEXTS.theElement + getText() + TEXTS.isWrong, getPositionInText());
							}
						} catch (Throwable ex2) {
							throw new ExpressionException(TEXTS.theElement + getText() + TEXTS.isWrong, getPositionInText());
						}
					}
				}
			}
		}
	}
	
	private void setNature(int nature) {
		this.nature = nature;
	}
	private void setPositionInText(int positionInText) {
		this.positionInText = positionInText;
	}
	private void setText(String text) {
		this.text = text;
	}
	private void setOperatorType(int operatorType) {
		this.operatorType = operatorType;
	}
	private void setValue(Object value) {
		this.value = value;
	}
	protected void setAlphanumericValue(String alphanumericValue) {
		this.alphanumericValue = alphanumericValue;
	}
	protected void setLinkedVariable(Variable linkedVariable) {
		this.linkedVariable = linkedVariable;
	}
	
	public static String getDefaultDateFormat() {
		if (defaultDateFormat == null)
			defaultDateFormat = DEFAULT_DATE_FORMAT;
		return defaultDateFormat;
	}

	public static void setDefaultDateFormat(String defaultDateFormat) {
		StackElement.defaultDateFormat = defaultDateFormat;
	}
	
	public String toString() {
		return getText();
	}
}
