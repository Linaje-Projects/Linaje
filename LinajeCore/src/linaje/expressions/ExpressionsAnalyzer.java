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

import java.util.Date;
import java.util.Vector;

import linaje.LocalizedStrings;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Lists;
import linaje.utils.Numbers;
import linaje.utils.Utils;

public class ExpressionsAnalyzer {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String writeExp;
		public String writelogicalExp;
		public String cannotBeTwoNegations;
		public String wrongValue;
		public String expressionMustStartWith;
		public String afterArithmeticOpMustGo;
		public String afterOpPlusMustGo;
		public String afterOpenParentNotOp;
		public String afterCloseParentNotValue;
		public String afterCloseParentNotOpenParent;
		public String afterGtLtMustGo;
		public String AfterAlphaCompOpMustGo;
		public String afterEqualOpMustGo;
		public String afterFunctionIsMandatory;
		public String afterValueMustGo;
		public String afterAlphaValueMustGo;
		public String afterDateValueMustGo;
		public String openParentIsMissing;
		
		public String pendingToClose;
		public String parenthesis;
		public String parentheses;
		
		public String expMustEndWith;
		public String globalExpMustBeLogical;
		public String beforeOpeningQuote;
		public String pendingToCloseQuote;

		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
/**
 * ExpressionsAnalyzer constructor comment.
 */
public ExpressionsAnalyzer() {
	super();
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (02/12/2005 11:22:38)
 * 
 * @param expression java.lang.String
 */
public static Expression parseExpression(String expression) throws ExpressionException {
	return parseExpression(expression, null);
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (02/12/2005 11:22:38)
 * 
 * @param expression java.lang.String
 */
public static Expression parseExpression(String expression, Vector<Variable> variables) throws ExpressionException {

	return parseExpression(expression, variables, true);
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (02/12/2005 11:22:38)
 * 
 * @param expression java.lang.String
 */
public static Expression parseExpression(String expression, Vector<Variable> variables, boolean forceLogicalType) throws ExpressionException {

	Vector<StackElement> stackElements = createStackElements(expression, true);
	
	Expression parsedExpression = parseStackElements(stackElements, variables, forceLogicalType);

	return parsedExpression;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (02/12/2005 10:49:25)
 * 
 * @param stackElements java.util.Vector
 */
public static Expression parseStackElements(Vector<StackElement> stackElements) throws ExpressionException {

	return parseStackElements(stackElements, null);
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (02/12/2005 10:49:25)
 * 
 * @param stackElements java.util.Vector
 */
public static Expression parseStackElements(Vector<StackElement> stackElements, Vector<Variable> variables) throws ExpressionException {

	return parseStackElements(stackElements, variables, true);
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (02/12/2005 10:49:25)
 * 
 * @param stackElements java.util.Vector
 */
public static Expression parseStackElements(Vector<StackElement> stackElements, Vector<Variable> variables, boolean forceLogicalType) throws ExpressionException {

	Expression expression = new Expression(null);
	expression.setForceLogicalType(forceLogicalType);
	StackElement stackElement;
	String previousElement = null;
	String element = null;
	int numParenthesisOpen = 0;
	int numParenthesisClose = 0;
	int position = 0;
	int operatorType = -1;
	int previousOperatorType = -1;
	int elementNature = -1;
	int previousElementNature = -1;

	if (stackElements.size() == 0) {
		if (forceLogicalType)
			throw new ExpressionException(TEXTS.writelogicalExp, 0);
		else
			throw new ExpressionException(TEXTS.writeExp, 0);
	}
	
	for (int i = 0; i < stackElements.size(); i++) {

		stackElement = stackElements.elementAt(i);
		element = stackElement.getText();
		operatorType = stackElement.getOperatorType();
		position = stackElement.getPositionInText();

		//Pasamos el vector de variables al elemento para que se inicie su nature y valor
		stackElement.initElement(variables);
		
		elementNature = stackElement.getNature();
		
		if (previousElement == null || previousElement.equalsIgnoreCase(Expression.OL_AND) || previousElement.equalsIgnoreCase(Expression.OL_OR) || previousElement.equalsIgnoreCase(Expression.OL_NOT)) {

			//Una expresión debe empezar con un valor o con un parentesis de apertura o con negación o con '-' o con una función
			if (element.equalsIgnoreCase(Expression.OL_NOT) && previousElement != null && previousElement.equalsIgnoreCase(Expression.OL_NOT)) {

				throw new ExpressionException(TEXTS.cannotBeTwoNegations, position);
			}
			else if (!element.equalsIgnoreCase(Expression.OL_NOT) && !element.equalsIgnoreCase(Expression.OA_SUB) && operatorType != Expression.OPERATOR_FUNCTION) {

				if (element.equalsIgnoreCase(Expression.OS_OPEN)) {
					
					numParenthesisOpen++;
				}
				else if (operatorType == -1) {

					if (elementNature == -1) {
						throw new ExpressionException(TEXTS.wrongValue, position);
					}
				}
				else throw new ExpressionException(TEXTS.expressionMustStartWith, position);
			}
		}
		else {

			//Reglas respecto al elemento anterior
			if (previousElement.equalsIgnoreCase(Expression.OA_SUB) || 
				previousElement.equalsIgnoreCase(Expression.OA_MUL) || 
				previousElement.equalsIgnoreCase(Expression.OA_DIV)) {
				//El elemento anterior es un operador aritmético numérico
				if (elementNature != Expression.NATURE_NUMERICAL && elementNature != Expression.NATURE_GLOBAL && !element.equalsIgnoreCase(Expression.OS_OPEN) && operatorType != Expression.OPERATOR_FUNCTION)
					throw new ExpressionException(TEXTS.afterArithmeticOpMustGo, position);
			}
			if (previousElement.equalsIgnoreCase(Expression.OA_SUM)) {
				//El elemento anterior es un operador aritmético numérico
				if (elementNature != Expression.NATURE_NUMERICAL && elementNature != Expression.NATURE_ALPHANUMERIC && elementNature != Expression.NATURE_GLOBAL && !element.equalsIgnoreCase(Expression.OS_OPEN) && operatorType != Expression.OPERATOR_FUNCTION)
					throw new ExpressionException(TEXTS.afterOpPlusMustGo, position);
			}
			/*else if (elementoAnterior.equalsIgnoreCase(OL_NOT)) {
				if (!elemento.equalsIgnoreCase(OS_OPEN))
					throw new ExpressionException("Debe abrir paréntesis tras una negación", posicion);
			}*/
			else if (previousElement.equalsIgnoreCase(Expression.OS_OPEN)) {
				//Tras abrir paréntesis no puede ir un operador
				if ((operatorType == Expression.OPERATOR_ARITHMETIC  && !element.equalsIgnoreCase(Expression.OA_SUB)) || (operatorType == Expression.OPERATOR_LOGICAL  && !element.equalsIgnoreCase(Expression.OL_NOT)) || operatorType == Expression.OPERATOR_COMPARATIVE)
					throw new ExpressionException(TEXTS.afterOpenParentNotOp, position);

				//Una función solo admite llevar una variable entre parentesis. ej: MAX(VAR1 VAR2)
				if (expression.getCurrentExpression().isFunction() && operatorType != -1 && !expression.getCurrentExpression().firstElement().toString().equalsIgnoreCase(Expression.OF_ABS))
					throw new ExpressionException(Expression.TEXTS.functionMustContainVariablesOrValues, position);
			}
			else if (previousElement.equalsIgnoreCase(Expression.OS_CLOSE)) {
				//Tras cerrar un paréntesis no puede ir un valor
				if (operatorType == -1)
					throw new ExpressionException(TEXTS.afterCloseParentNotValue, position);
				else if (element.equalsIgnoreCase(Expression.OS_OPEN))
					throw new ExpressionException(TEXTS.afterCloseParentNotOpenParent, position);
			}
			else if (previousElement.equalsIgnoreCase(Expression.OC_GREATER) || 
					 previousElement.equalsIgnoreCase(Expression.OC_LESS) ||
					 previousElement.equalsIgnoreCase(Expression.OC_GREATER_EQUAL) || 
					 previousElement.equalsIgnoreCase(Expression.OC_LESS_EQUAL) || 
					 previousElement.equalsIgnoreCase(Expression.OC_EQUAL_GREATER) || 
					 previousElement.equalsIgnoreCase(Expression.OC_EQUAL_LESS) || 
					 previousElement.equalsIgnoreCase(Expression.OC_GREATER_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_LESS_IGNORE) ||
					 previousElement.equalsIgnoreCase(Expression.OC_GREATER_EQUAL_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_LESS_EQUAL_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_EQUAL_GREATER_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_EQUAL_LESS_IGNORE)) {
				//Tras los operadores '>' o '<' debe ir '(' o un valor númerico o de fecha
				if (elementNature != Expression.NATURE_NUMERICAL && elementNature != Expression.NATURE_DATE && elementNature != Expression.NATURE_GLOBAL && !element.equalsIgnoreCase(Expression.OS_OPEN) && !element.equalsIgnoreCase(Expression.OA_SUB) && operatorType != Expression.OPERATOR_FUNCTION)
					throw new ExpressionException(TEXTS.afterGtLtMustGo, position);
			}
			else if (previousElement.equalsIgnoreCase(Expression.OC_STARTS) || 
					 previousElement.equalsIgnoreCase(Expression.OC_ENDS) || 
					 previousElement.equalsIgnoreCase(Expression.OC_CONTAINS) ||
					 previousElement.equalsIgnoreCase(Expression.OC_STARTS_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_ENDS_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_CONTAINS_IGNORE)) {
				//Tras un operador comparativo alfanumérico debe ir '(' o un valor alfanumérico
				if (elementNature != Expression.NATURE_ALPHANUMERIC && elementNature != Expression.NATURE_NUMERICAL && elementNature != Expression.NATURE_GLOBAL && !element.equalsIgnoreCase(Expression.OS_OPEN) && operatorType != Expression.OPERATOR_FUNCTION)
					throw new ExpressionException(TEXTS.AfterAlphaCompOpMustGo, position);
			}
			else if (previousElement.equalsIgnoreCase(Expression.OC_EQUAL) || 
					 previousElement.equalsIgnoreCase(Expression.OC_DISTINCT) || 
					 previousElement.equalsIgnoreCase(Expression.OC_EQUAL_IGNORE) || 
					 previousElement.equalsIgnoreCase(Expression.OC_DISTINCT_IGNORE)) {
				//Tras el operador '=' debe ir '(' o un valor
				if (elementNature == -1 && !element.equalsIgnoreCase(Expression.OS_OPEN) && !element.equalsIgnoreCase(Expression.OA_SUB) && operatorType != Expression.OPERATOR_FUNCTION)
					throw new ExpressionException(TEXTS.afterEqualOpMustGo, position);
			}
			else if (previousOperatorType == Expression.OPERATOR_FUNCTION) {

				//Tras una función es obligatorio abrir paréntesis
				if (!element.equalsIgnoreCase(Expression.OS_OPEN))
					throw new ExpressionException(TEXTS.afterFunctionIsMandatory, position);
			}
			else if (previousElementNature != -1 && (!expression.getCurrentExpression().isFunction() || expression.getCurrentExpression().firstElement().toString().equalsIgnoreCase(Expression.OF_ABS))) {
				//Tras un valor debe ir un operador o ')'
				if (elementNature != -1 || element.equalsIgnoreCase(Expression.OS_OPEN)) {
					throw new ExpressionException(TEXTS.afterValueMustGo, position);
				}
				/*else if (expression.getNaturaleza() == NATURE_NUMERICAL) {
					if (elemento.equalsIgnoreCase(OC_STARTS) || elemento.equalsIgnoreCase(OC_ENDS) || elemento.equalsIgnoreCase(OC_CONTAINS))
						throw new ExpressionException("Tras un valor numérico debe ir un operador aritmético numérico o un operador comparativo numérico o ')' o '&' o '|'", posicion);
				}
				*/
				else if (expression.getCurrentExpression().getNature() == Expression.NATURE_ALPHANUMERIC) {
					if (!element.equalsIgnoreCase(Expression.OC_STARTS) && 
						!element.equalsIgnoreCase(Expression.OC_ENDS) && 
						!element.equalsIgnoreCase(Expression.OC_CONTAINS) && 
						!element.equalsIgnoreCase(Expression.OC_EQUAL) && 
						!element.equalsIgnoreCase(Expression.OC_DISTINCT) &&
						!element.equalsIgnoreCase(Expression.OC_STARTS_IGNORE) && 
						!element.equalsIgnoreCase(Expression.OC_ENDS_IGNORE) && 
						!element.equalsIgnoreCase(Expression.OC_CONTAINS_IGNORE) && 
						!element.equalsIgnoreCase(Expression.OC_EQUAL_IGNORE) && 
						!element.equalsIgnoreCase(Expression.OC_DISTINCT_IGNORE) &&
						!element.equalsIgnoreCase(Expression.OA_SUM) && 
						!element.equalsIgnoreCase(Expression.OS_CLOSE) && 
						!element.equalsIgnoreCase(Expression.OL_AND) && 
						!element.equalsIgnoreCase(Expression.OL_OR))
						
						throw new ExpressionException(TEXTS.afterAlphaValueMustGo, position);
				}
				else if (expression.getCurrentExpression().getNature() == Expression.NATURE_DATE) {
					if (!element.equalsIgnoreCase(Expression.OC_LESS) &&
						!element.equalsIgnoreCase(Expression.OC_GREATER) &&
						!element.equalsIgnoreCase(Expression.OC_GREATER_EQUAL) &&
					 	!element.equalsIgnoreCase(Expression.OC_LESS_EQUAL) &&
					 	!element.equalsIgnoreCase(Expression.OC_EQUAL_GREATER) &&
					 	!element.equalsIgnoreCase(Expression.OC_EQUAL_LESS) &&
					 	!element.equalsIgnoreCase(Expression.OC_EQUAL) &&
						!element.equalsIgnoreCase(Expression.OC_DISTINCT) &&
						!element.equalsIgnoreCase(Expression.OC_LESS_IGNORE) &&
						!element.equalsIgnoreCase(Expression.OC_GREATER_IGNORE) &&
						!element.equalsIgnoreCase(Expression.OC_GREATER_EQUAL_IGNORE) &&
					 	!element.equalsIgnoreCase(Expression.OC_LESS_EQUAL_IGNORE) &&
					 	!element.equalsIgnoreCase(Expression.OC_EQUAL_GREATER_IGNORE) &&
					 	!element.equalsIgnoreCase(Expression.OC_EQUAL_LESS_IGNORE) &&
						!element.equalsIgnoreCase(Expression.OC_EQUAL_IGNORE) &&
						!element.equalsIgnoreCase(Expression.OC_DISTINCT_IGNORE) &&
						!element.equalsIgnoreCase(Expression.OS_CLOSE) &&
						!element.equalsIgnoreCase(Expression.OL_AND) &&
						!element.equalsIgnoreCase(Expression.OL_OR))
					
						throw new ExpressionException(TEXTS.afterDateValueMustGo, position);
				}
			}

			//Acciones según el elemento actual
			if (element.equalsIgnoreCase(Expression.OS_CLOSE)) {//')'

				numParenthesisClose++;
				if (numParenthesisOpen < numParenthesisClose) {
					throw new ExpressionException(TEXTS.openParentIsMissing, position);
				}
			}
			else if (element.equalsIgnoreCase(Expression.OS_OPEN)) {//'('

				numParenthesisOpen++;
			}
		}
		//Si se cumplen las condiciones agregamos el elemento a la expresión
		expression.addStackElement(stackElement, elementNature);

		//Preparamos los valores para el elemento siguiente
		previousElement = element;
		previousOperatorType = operatorType;
		previousElementNature = elementNature;
	}
	//Reglas respecto a los parentesis
	int parentesisCerradosPendientes = numParenthesisOpen - numParenthesisClose;
	if (parentesisCerradosPendientes > 0)
		throw new ExpressionException(TEXTS.pendingToClose + parentesisCerradosPendientes + (parentesisCerradosPendientes == 1 ? TEXTS.parenthesis : TEXTS.parentheses), position);
	//Respecto a la finalización de la expresión
	if (operatorType != -1 && !element.equalsIgnoreCase(Expression.OS_CLOSE))
		throw new ExpressionException(TEXTS.expMustEndWith, position);
	
	//Indicamos que hemos terminado la expresión
	expression.addStackElement(new StackElement(Expression.END_EXPRESSION, -1, position), -1);
	if (forceLogicalType && expression.getNature() != Expression.NATURE_LOGICAL)
		throw new ExpressionException(TEXTS.globalExpMustBeLogical, position);

	expression.setStackElements(stackElements);

	return expression;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (29/11/2005 11:39:06)
 * 
 * @return int
 */
public static Vector<StackElement> createStackElements(String expression) {

	try {
		return createStackElements(expression, false);
	} 
	catch (Throwable ex) {
		Console.printException(ex);
		return null;
	}
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (29/11/2005 11:39:06)
 * 
 * @return int
 */
public static Vector<StackElement> createStackElements(String expression, boolean handleErrors) throws ExpressionException {

	Vector<StackElement> stack = new Vector<>();
	StringBuffer stackElement = new StringBuffer();
	boolean quoteFound = false;
	char character;
	char characterNext;

	if (expression == null || expression.trim().equals(Constants.VOID))
		expression = "true";//Si no hay expresión hacemos como si viniese true (se cumple siempre la condición)
	
	//Analizamos cada cáracter de la expresión identificando cada uno se sus elementos y añadiendolos a la pila de elementos
	for (int i = 0; i < expression.length(); i++) {

		character = expression.charAt(i);
		if (!quoteFound) {


			boolean characterSpace = Character.isSpaceChar(character);
			if (!characterSpace && character == ',') {

				//Si lo que hay antes de la coma no es un numero entero, la tratamos como si fuese un espacio en blanco
				if (stackElement.length() == 0) {
					
					characterSpace = true;
				}
				else {

					Object possibleIntegerNumber = GlobalDataConverter.getNonAlphanumericData(stackElement.toString());
					if (possibleIntegerNumber == null || !Numbers.isIntegerNumber(possibleIntegerNumber.toString()))
						characterSpace = true;
				}
			}
			
			if (characterSpace && stackElement.length() != 0) {
				//Si el buffer del elemento no esta vacio es que tenemos ya un elemento completo y lo añadimos a la pila
				stack.addElement(new StackElement(stackElement, -1, i));
				stackElement = new StringBuffer();
			}
			else if (!characterSpace) {
				if (character == '\'') {
					//Si encontramos una comilla aquí es que es de apertura
					if (stackElement.length() != 0) {
						//Si el buffer del elemento no esta vacio lo añadimos a la pila
						/**** ERROR: No debería haber nada antes de una apertura de comillas que no sea un operador ***/
						if (handleErrors) {
							throw new ExpressionException(TEXTS.beforeOpeningQuote, i);
						}
						else {
							stack.addElement(new StackElement(stackElement, -1, i));
							stackElement = new StringBuffer();
						}
					}
					stackElement.append(character);
					quoteFound = true;
				}
				else {

					//Miramos si nos llega un operador
					String possibleOperator;
					if (character == '!' && i < expression.length() - 1) {

						characterNext = expression.charAt(i + 1);
						if (characterNext == '=') {
							possibleOperator = "!=";
							i++;
						}
						else
							possibleOperator = String.valueOf(character);
					}
					else {

						possibleOperator = String.valueOf(character);
					}
					int operatorType = getOperatorType(possibleOperator);
					int numMaxCharacters = 3; //Caracteres maximos que puede tener un operador
					int extraCharacters = 0;
					while (operatorType == -1 && i < expression.length() - extraCharacters - 1 && extraCharacters + 1 < numMaxCharacters) {
						
						//Comprobamos si es un operador compuesto por DOS O PLUS CARACTERES
						characterNext = expression.charAt(i + extraCharacters + 1);
						possibleOperator = possibleOperator + characterNext;
						
						extraCharacters = possibleOperator.length() - 1;
						operatorType = getOperatorType(possibleOperator);
						if (operatorType == Expression.OPERATOR_LOGICAL || operatorType == Expression.OPERATOR_FUNCTION) {

							boolean previousCharacterIsValid = (i == 0);
							if (!previousCharacterIsValid) {

								char caracterAnterior = expression.charAt(i - 1);
								if (!Character.isLetterOrDigit(caracterAnterior))
									previousCharacterIsValid = true;
							}
							boolean nextCharacterIsValid = (expression.length() == i + extraCharacters + 1);
							if (!nextCharacterIsValid) {

								characterNext = expression.charAt(i + extraCharacters + 1);
								if (!Character.isLetterOrDigit(characterNext))
									nextCharacterIsValid = true;
							}

							if (!nextCharacterIsValid || !previousCharacterIsValid)
								operatorType = -1;
							else
								i = i + extraCharacters;
						}
						else if (operatorType != -1)
							i = i + extraCharacters;
					}

					if (operatorType != -1) {

						if (stackElement.length() != 0) {
							//Si el buffer del elemento no esta vacio es que tenemos ya un elemento completo y lo añadimos a la pila
							stack.addElement(new StackElement(stackElement, -1, i));
							stackElement = new StringBuffer();
						}

						//Si viene un operador comparativo aritmético comprobamos si despues viene otro con el que formar >=, =>, <=, =<
						if (i < expression.length() - 1 && operatorType == Expression.OPERATOR_COMPARATIVE && !possibleOperator.equalsIgnoreCase(Expression.OC_DISTINCT) && !possibleOperator.equalsIgnoreCase(Expression.OC_DISTINCT_IGNORE)) {

							char ultimoCaracterOperador = possibleOperator.charAt(possibleOperator.length() - 1);
							if (ultimoCaracterOperador == '=' || ultimoCaracterOperador == '>' || ultimoCaracterOperador == '<') {

								characterNext = expression.charAt(i + 1);
								if (ultimoCaracterOperador != characterNext && (characterNext == '=' || characterNext == '>' || characterNext == '<')) {

									possibleOperator = possibleOperator + characterNext;
									i++;
								}
							}
						}
						//Añadimos el operador a la pila
						stack.addElement(new StackElement(possibleOperator, operatorType, i + 1));
						stackElement = new StringBuffer();
					}
					else {

						//Viene algo que no es un operador (Mas tarde se analizará si lo que viene es correcto o no)
						//Se almacenará en el buffer hasta que llegue otro operador
						stackElement.append(character);
					}
				}
			}
		}
		else {

			if (character == '\\' && i < expression.length() - 1) {
				//Si el siguiente carácter es comilla es que el valor alfanumérico contiene el carácter 'comilla' y no es la comilla de cierre de valor
				characterNext = expression.charAt(i + 1);
				if (characterNext == '\'') {
					//Omitimos la barra y añadimos la comilla
					stackElement.append('\'');
					i++;
				}
				else {
					//Añadimos la barra al valor alfanumérico
					stackElement.append(character);
				}
			}
			else {
				
				//Hemos encontrado una comilla anteriormente por lo que añadiremos cualquier carácter que venga al buffer del elemento
				stackElement.append(character);
				if (character == '\'') { 
					//Si encontramos una comilla aquí es que es de cierre
					quoteFound = false;
					//Añadimos el valor alfanumérico a la pila
					stack.addElement(new StackElement(stackElement, -1, i + 1));
					stackElement = new StringBuffer();
				}
			}
		}
	}
	if (stackElement.length() != 0) {

		//Si el buffer del elemento no esta vacio añadimos el último elemento a la pila
		stack.addElement(new StackElement(stackElement, -1, expression.length()));

		if (quoteFound) {
			//La expresión termina con un alfanúmerico que no ha cerrado comillas
			/**** ERROR DE NO CERRAR QUOTE ***/
			if (handleErrors)
				throw new ExpressionException(TEXTS.pendingToCloseQuote, expression.length());
		}
	}

	return stack;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (09/01/2007 10:50:47)
 * 
 * @return java.lang.String
 * @param function java.lang.String
 */
public static String getFunctionDescription(String function) {
	
	if (function.equalsIgnoreCase(Expression.OF_MAX))
		return Expression.DESC_MAX;
	else if (function.equalsIgnoreCase(Expression.OF_MIN))
		return Expression.DESC_MIN;
	else if (function.equalsIgnoreCase(Expression.OF_SUM))
		return Expression.DESC_SUM;
	else if (function.equalsIgnoreCase(Expression.OF_AVG))
		return Expression.DESC_AVG;
	if (function.equalsIgnoreCase(Expression.OF_MAX2))
		return Expression.DESC_MAX2;
	else if (function.equalsIgnoreCase(Expression.OF_MIN2))
		return Expression.DESC_MIN2;
	else if (function.equalsIgnoreCase(Expression.OF_ABS))
		return Expression.DESC_ABS;
	else
		return Constants.VOID;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Double getAvg(Vector<?> values, Object defaultValue) {

	try {

		if (values != null && values.size() > 0) 
			return new Double(getSum(values, defaultValue).doubleValue() / values.size());
		else
			return new Double(defaultValue.toString());

	} catch (Throwable ex) {
		Console.printException(ex);
		return new Double(0);
	}
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (28/03/2005 12:09:11)
 * 
 * @return char
 * @param tipo java.lang.Class
 */
public final static char getNature(Class<?> type) {

	if (type == null)
		return Expression.NATURE_GLOBAL;
	else if (type == Integer.class || type == int.class)
		return Expression.NATURE_NUMERICAL;
	else if (type == Long.class || type == Double.class || type == long.class || type == double.class)
		return Expression.NATURE_NUMERICAL;
	else if (type == Date.class)
		return Expression.NATURE_DATE;
	else
		return Expression.NATURE_ALPHANUMERIC;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Object getFunctionResult(String function, Vector<?> values, Object defaultValue) {

	Object functionValue = getFunctionValue(function, values, defaultValue);
	Object result;

	if (function.equalsIgnoreCase(Expression.OF_MAX)
	 || function.equalsIgnoreCase(Expression.OF_MIN)
	 || function.equalsIgnoreCase(Expression.OF_MAX2)
	 || function.equalsIgnoreCase(Expression.OF_MIN2)) {
		 
		result = GlobalDataConverter.getNonAlphanumericData(functionValue.toString());
		
		if (result == null)
			result = functionValue;
		else if (result instanceof Date)
			result = StackElement.getFormattedDate((Date) result);
	}
	else result = functionValue;
	
	return result;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Double getSum(Vector<?> values, Object defaultValue) {

	try {
		
		if (values.size() > 0) {

			double sum = 0;
			for (int i = 0; i < values.size(); i++) {

				double value;
				try {

					value = new Double(values.elementAt(i).toString()).doubleValue();

				}
				catch (Throwable ex) {

					try {

						Object posibleNumero = GlobalDataConverter.getNonAlphanumericData(values.elementAt(i).toString());
						value = new Double(posibleNumero.toString()).doubleValue();

					}
					catch (Throwable ex2) {
						return new Double(0);
					}
				}
				sum = sum + value;
			}

			return new Double(sum);
		}
		return new Double(defaultValue.toString());

	} catch (Throwable ex) {
		Console.printException(ex);
		return new Double(0);
	}
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (29/11/2005 15:14:20)
 * 
 * @param str java.lang.String
 */
public static int getOperatorType(String str) {

	if (str.equalsIgnoreCase(Expression.OL_AND) ||
		str.equalsIgnoreCase(Expression.OL_OR) ||
		str.equalsIgnoreCase(Expression.OL_NOT))
		
		return Expression.OPERATOR_LOGICAL;
	
	else if (str.equalsIgnoreCase(Expression.OC_GREATER) || 
			 str.equalsIgnoreCase(Expression.OC_LESS) || 
			 str.equalsIgnoreCase(Expression.OC_EQUAL) ||
			 str.equalsIgnoreCase(Expression.OC_GREATER_EQUAL) || 
			 str.equalsIgnoreCase(Expression.OC_LESS_EQUAL) || 
			 str.equalsIgnoreCase(Expression.OC_EQUAL_GREATER) || 
			 str.equalsIgnoreCase(Expression.OC_EQUAL_LESS) || 
			 str.equalsIgnoreCase(Expression.OC_DISTINCT) || 
			 str.equalsIgnoreCase(Expression.OC_STARTS) || 
			 str.equalsIgnoreCase(Expression.OC_ENDS) || 
			 str.equalsIgnoreCase(Expression.OC_CONTAINS) ||
			 str.equalsIgnoreCase(Expression.OC_GREATER_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_LESS_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_EQUAL_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_GREATER_EQUAL_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_LESS_EQUAL_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_EQUAL_GREATER_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_EQUAL_LESS_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_DISTINCT_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_STARTS_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_ENDS_IGNORE) || 
			 str.equalsIgnoreCase(Expression.OC_CONTAINS_IGNORE))
	
		return Expression.OPERATOR_COMPARATIVE;
	
	else if (str.equalsIgnoreCase(Expression.OA_SUM) ||
			 str.equalsIgnoreCase(Expression.OA_SUB) ||
			 str.equalsIgnoreCase(Expression.OA_MUL) ||
			 str.equalsIgnoreCase(Expression.OA_DIV))
	
		return Expression.OPERATOR_ARITHMETIC;
	
	else if (str.equalsIgnoreCase(Expression.OS_OPEN) ||
			 str.equalsIgnoreCase(Expression.OS_CLOSE))
	
		return Expression.OPERATOR_SEPARATOR;

	else if (str.equalsIgnoreCase(Expression.OF_MAX) ||
			 str.equalsIgnoreCase(Expression.OF_MIN) ||
			 str.equalsIgnoreCase(Expression.OF_MAX2) ||
			 str.equalsIgnoreCase(Expression.OF_MIN2) || 
			 str.equalsIgnoreCase(Expression.OF_SUM) ||
			 str.equalsIgnoreCase(Expression.OF_AVG) ||
			 str.equalsIgnoreCase(Expression.OF_ABS))
	
		return Expression.OPERATOR_FUNCTION;
	
	else return -1;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Double getAbsValue(Object defaultValue) {

	try {
		
		double value = 0;
		if (defaultValue != null) {

			try {

				value = new Double(defaultValue.toString()).doubleValue();

			}
			catch (Throwable ex) {

				try {

					Object possibleNumber = GlobalDataConverter.getNonAlphanumericData(defaultValue.toString());
					value = new Double(possibleNumber.toString()).doubleValue();

				}
				catch (Throwable ex2) {
					return new Double(0);
				}
			}
		}
		return new Double(Math.abs(value));

	} catch (Throwable ex) {
		Console.printException(ex);
		return new Double(0);
	}
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Double getAbsValue(Vector<?> values, Object defaultValue) {

	Object value = null;
	try {

		if (defaultValue != null)
			value = defaultValue;
		else if (values != null && !values.isEmpty())
			value = values.elementAt(0);
	
	} catch (Throwable ex) {
		Console.printException(ex);
	}
	
	return getAbsValue(value);
}

public static Object getFunctionValue(String function, Vector<?> values, Object defaultValue) {

	Object functionValue = null;
	
	if (function.equalsIgnoreCase(Expression.OF_MAX))
		functionValue = getMaxValue(values, defaultValue);
	
	else if (function.equalsIgnoreCase(Expression.OF_MIN))
		functionValue = getMinValue(values, defaultValue);
	
	else if (function.equalsIgnoreCase(Expression.OF_MAX2))
		functionValue = getMaxValue2(values, defaultValue);
	
	else if (function.equalsIgnoreCase(Expression.OF_MIN2))
		functionValue = getMinValue2(values, defaultValue);
	
	else if (function.equalsIgnoreCase(Expression.OF_SUM))
		functionValue = getSum(values, defaultValue);
	
	else if (function.equalsIgnoreCase(Expression.OF_AVG))
		functionValue = getAvg(values, defaultValue);

	else if (function.equalsIgnoreCase(Expression.OF_ABS))		
		functionValue = getAbsValue(values, defaultValue);
		
	return functionValue;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Object getMaxValue(Vector<?> values, Object defaultValue) {

	if (values.size() > 1) {

		Object maxValue = null;
		Object vMax = null;
		for (int i = 0; i < values.size(); i++) {

			Object value = values.elementAt(i);
			
			Object v1 = GlobalDataConverter.getNonAlphanumericData(value.toString());
			Object v2 = vMax;
			if (v1 == null)
				v1 = value;
						
			if (maxValue == null || Utils.compare(v1, v2) > 0) {
				maxValue = value;
				vMax = v1;
			}
		}

		return maxValue;
	}
	else if (values.size() == 1)
		return values.elementAt(0);
	else
		return defaultValue;
}

public static Object getMaxValue2(Vector<?> values, Object defaultValue) {

	if (values.size() > 1) {

		//Obtenemos el vector de valores con tipo a ordenar
		Object value;
		Object valueWithType;
		
		Vector<Object> valuesWithType = new Vector<>();
		for (int i = 0; i < values.size(); i++) {

			value = values.elementAt(i);
			
			valueWithType = GlobalDataConverter.getNonAlphanumericData(value.toString());
			if (valueWithType == null)
				valueWithType = value;
			
			valuesWithType.addElement(valueWithType);
		}

		//Ordenamos el vector de valores con tipo
		Lists.sortUndefinedList(valuesWithType, false);
		
		return valuesWithType.elementAt(1);
	}
	else if (values.size() == 1)
		return values.elementAt(0);
	else
		return defaultValue;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Object getMinValue(Vector<?> values, Object defaultValue) {

	if (values.size() > 1) {

		Object minValue = null;
		Object vMin = null;
		for (int i = 0; i < values.size(); i++) {

			Object value = values.elementAt(i);
			
			Object v1 = GlobalDataConverter.getNonAlphanumericData(value.toString());
			Object v2 = vMin;
			if (v1 == null)
				v1 = value;
						
			if (minValue == null || Utils.compare(v1, v2) < 0) {

				minValue = value;
				vMin = v1;
			}
		}

		return minValue;
	}
	else if (values.size() == 1)
		return values.elementAt(0);
	else
		return defaultValue;
}
/**
 * <b>Descripción:</b><br>
 * Creado por: Pablo Linaje (26/12/2006 15:35:23)
 * 
 * @return java.lang.Object
 */
public static Object getMinValue2(Vector<?> values, Object defaultValue) {

	if (values.size() > 1) {

		//Obtenemos el vector de valores con tipo a ordenar
		Object value;
		Object valueWithType;
		
		Vector<Object> valuesWithType = new Vector<>();
		for (int i = 0; i < values.size(); i++) {

			value = values.elementAt(i);
			
			valueWithType = GlobalDataConverter.getNonAlphanumericData(value.toString());
			if (valueWithType == null)
				valueWithType = value;
			
			valuesWithType.addElement(valueWithType);
		}

		//Ordenamos el vector de valores con tipo
		Lists.sortUndefinedList(valuesWithType);
		
		return valuesWithType.elementAt(1);
	}
	else if (values.size() == 1)
		return values.elementAt(0);
	else
		return defaultValue;
}
}
