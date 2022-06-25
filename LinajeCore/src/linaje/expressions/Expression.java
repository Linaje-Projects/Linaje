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

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import linaje.LocalizedStrings;
import linaje.logs.Console;
import linaje.utils.Dates;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class Expression extends Vector<Object> {
	
	private StackElement comparativeOperator = null;
	private Expression currentExpression = null;
	private Expression parent = null;
	private Vector<StackElement> stackElements = null;
	private Vector<Variable> variables = null;
	
	private int nature = -1;
	
	private boolean forceLogicalType = false;
	private boolean forbidLogicalType = false;
	private boolean function = false;
	
	public static final int OPERATOR_LOGICAL = 0;
	public static final int OPERATOR_COMPARATIVE = 1;
	public static final int OPERATOR_ARITHMETIC = 2;
	public static final int OPERATOR_SEPARATOR = 3;
	public static final int OPERATOR_FUNCTION = 4;
	
	public static final int NATURE_NUMERICAL = 0;
	public static final int NATURE_ALPHANUMERIC = 1;
	public static final int NATURE_DATE = 2;
	public static final int NATURE_LOGICAL = 3;
	public static final int NATURE_GLOBAL = 4;
	
	//OL -> Operator Logical
	//OC -> Operator Comparative
	//OA -> Operator Arithmetic
	//OS -> Operator Separator
	//OF -> Operator Function
	
	public static final String OL_AND = "AND";
	public static final String OL_OR = "OR";
	public static final String OL_NOT = "NOT";
	
	public static final String OC_GREATER = ">";
	public static final String OC_LESS = "<";
	public static final String OC_EQUAL = "=";
	public static final String OC_GREATER_EQUAL = ">=";
	public static final String OC_LESS_EQUAL = "<=";
	public static final String OC_EQUAL_GREATER = "=>";
	public static final String OC_EQUAL_LESS = "=<";
	public static final String OC_DISTINCT = "!=";
	public static final String OC_STARTS = "S=";
	public static final String OC_ENDS = "E=";
	public static final String OC_CONTAINS = "C=";
	public static final String OC_GREATER_IGNORE = "i>";
	public static final String OC_LESS_IGNORE = "i<";
	public static final String OC_EQUAL_IGNORE = "i=";
	public static final String OC_GREATER_EQUAL_IGNORE = "i>=";
	public static final String OC_LESS_EQUAL_IGNORE = "i<=";
	public static final String OC_EQUAL_GREATER_IGNORE = "i=>";
	public static final String OC_EQUAL_LESS_IGNORE = "i=<";
	public static final String OC_DISTINCT_IGNORE = "i!=";
	public static final String OC_STARTS_IGNORE = "iS=";
	public static final String OC_ENDS_IGNORE = "iE=";
	public static final String OC_CONTAINS_IGNORE = "iC=";
	
	public static final String OA_SUM = "+";
	public static final String OA_SUB = "-";
	public static final String OA_MUL = "*";
	public static final String OA_DIV = "/";
	
	public static final String OS_OPEN = "(";
	public static final String OS_CLOSE = ")";
	
	public static final String OF_MAX = "MAX";
	public static final String OF_MIN = "MIN";
	public static final String OF_SUM = "SUM";
	public static final String OF_AVG = "AVG";
	public static final String OF_ABS = "ABS";
	public static final String OF_MAX2 = "MX2";
	public static final String OF_MIN2 = "MN2";
	
	public static final String END_EXPRESSION = "_EndEX_";
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String afterFunctionHaveToOpenParenthesis;
		public String functionMustContainVariablesOrValues;
		public String partExpMustContainCompOp;
		public String partExpMustContainSomething;
		public String cannotIntroduceLogicalOps;
		public String cannotIntroduceCompOps;
		public String datExpsNotSupportArtimOps;
		public String artimOpNotValidForAlphaExps;
		public String errorTryingToResolveExp;
		
		public String errorTryingToResolveReducedExp;
		public String forNotHavingNature;
		
		public String expAnalized;
		public String cannotMixDateValues;
		public String expNatureNotConsistentWithCompOp;
		
		public String cannotInsertAlpha;
		public String partialExpOnlyOneOp;
		public String compOpNotConsistentWithPartialExpNature;
		public String logicalExpsMustBeSparatedWithLogicalOp;
		
		public String descMax;
		public String descMin;
		public String descSum;
		public String descAvg;
		public String descAbs;
		public String descMax2;
		public String descMin2;

		public String alphanumericReducedExp;
		public String dateReducedExp;
		public String logicalReducedExp;
		public String numericReducedExp;
		public String function;
		public String couldNotBeSolved;

		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();

	public static final String DESC_MAX = TEXTS.descMax;
	public static final String DESC_MIN = TEXTS.descMin;
	public static final String DESC_SUM = TEXTS.descSum;
	public static final String DESC_AVG = TEXTS.descAvg;
	public static final String DESC_ABS = TEXTS.descAbs;
	public static final String DESC_MAX2 = TEXTS.descMax2;
	public static final String DESC_MIN2 = TEXTS.descMin2;
	
	
	public Expression(Expression parentExpression) {
		
		super();
		setParent(parentExpression);
		setCurrentExpression(this);
	}
	
	public synchronized void addElement(StackElement stackElement) {
		super.addElement(stackElement);
		//Si añadimos algo con variables se las indicamos a la expresión
		if (stackElement.getLinkedVariable() != null) {
			Vector<Variable> variables = new Vector<>();
			variables.addElement(stackElement.getLinkedVariable());
			addVariables(variables);
		}
	}

	public synchronized void addElement(Expression expression) {
		super.addElement(expression);
		//Si añadimos algo con variables se las indicamos a la expresión
		addVariables(expression.getVariables());
	}
	
	private synchronized void addVariables(Vector<Variable> variables) {
		
		if (variables != null) {
			
			for (int i = 0; i < variables.size(); i++) {
	
				Variable var = variables.elementAt(i);
				if (!getVariables().contains(var))
					getVariables().addElement(var);
	
				//El/los parent/s deben tener tambien las variables así que si no las tienen se añaden tambien a estos
				Expression parentExpression = getParent();
				while (parentExpression != null) {
	
					if (!parentExpression.getVariables().contains(var))
						parentExpression.getVariables().addElement(var);
	
					parentExpression = parentExpression.getParent();
				}
			}
		}
	}
	public void addStackElement(StackElement stackElement, int elementNature) throws ExpressionException {
	
		String elementText = stackElement.toString();
		int position = stackElement.getPositionInText();
		int operatorType = stackElement.getOperatorType();
	
		if (getCurrentExpression().isFunction()) {
	
			if (!getCurrentExpression().firstElement().toString().equalsIgnoreCase(Expression.OF_ABS)) {
				
				//Una función solo admite llevar una variable entre parentesis. ej: MAX(VAR)
				if (getCurrentExpression().size() == 1) {
	
					if (!elementText.equalsIgnoreCase(Expression.OS_OPEN) && operatorType != -1)
						throw new ExpressionException(TEXTS.afterFunctionHaveToOpenParenthesis, position);
				}
				else {
	
					if (!elementText.equalsIgnoreCase(Expression.OS_CLOSE) && operatorType != -1)
						throw new ExpressionException(TEXTS.functionMustContainVariablesOrValues, position);
				}
			}
		}
		if (elementText.equals(Expression.END_EXPRESSION)) {
	
			if (getCurrentExpression().getParent() != null) {
	
				elementNature = getCurrentExpression().getNature();
				boolean isLogicalType = getCurrentExpression().getComparativeOperator() != null || getCurrentExpression().getNature() == Expression.NATURE_LOGICAL;
				setCurrentExpression(getCurrentExpression().getParent());
				if (isLogicalType)
					getCurrentExpression().setNature(Expression.NATURE_LOGICAL, null);
				else
					getCurrentExpression().setNature(elementNature, null);
				
				//Nos llega el elemento fin expresión por lo que comprobamos que la última expresión parcial introducida es de tipo lógico
				if (isForceLogicalType() && !isLogicalType)
					throw new ExpressionException(TEXTS.partExpMustContainCompOp, position);
			}
			return;
		}
		else if (this.getParent() == null && this.isEmpty() && !stackElement.toString().equalsIgnoreCase(Expression.OL_NOT)) {
	
			//Lo primero que entre pasará a formar parte de una subexpresión (Excepto si empieza por NOT)
			Expression childExpression = new Expression(getCurrentExpression());
			getCurrentExpression().addElement(childExpression);
			setCurrentExpression(childExpression);
		}
		
		if (elementText.equalsIgnoreCase(Expression.OS_OPEN) || operatorType == Expression.OPERATOR_FUNCTION) {
	
			//Añadimos un hijo
			Expression childExpression = new Expression(getCurrentExpression());
			elementNature = getCurrentExpression().getNature();
			
			if (elementNature != -1 && elementNature != Expression.NATURE_LOGICAL)
				childExpression.setForbidLogicalType(true);
			
			if (operatorType == Expression.OPERATOR_FUNCTION) {
				
				childExpression.setFunction(true);
				childExpression.addElement(stackElement);
				getCurrentExpression().addElement(childExpression);
				setCurrentExpression(childExpression);
			}
			//Si la expresión es una función omitimos el parentesis de apertura ya que se habra creado la subexpresión al analizar el operador de función
			else if (!getCurrentExpression().isFunction()) {
	
				getCurrentExpression().addElement(childExpression);
				setCurrentExpression(childExpression);
			}
		}
		else if (elementText.equalsIgnoreCase(Expression.OS_CLOSE)) {
	
			//Damos por finalizada la expresion actual y hacemos que la actual sea el parent
			elementNature = getCurrentExpression().getNature();
			if (elementNature == -1)
				throw new ExpressionException(TEXTS.partExpMustContainSomething, position);
				
			boolean isForceLogicalType = getCurrentExpression().isForceLogicalType();
			boolean isLogicalType = getCurrentExpression().getComparativeOperator() != null || getCurrentExpression().getNature() == Expression.NATURE_LOGICAL;
		
			setCurrentExpression(getCurrentExpression().getParent());
			
			if (isLogicalType)
				getCurrentExpression().setNature(Expression.NATURE_LOGICAL, stackElement);
			else
				getCurrentExpression().setNature(elementNature, stackElement);
			
			if (isForceLogicalType && !isLogicalType)
				throw new ExpressionException(TEXTS.partExpMustContainCompOp, position);
		}
		else {
	
			if (elementText.equalsIgnoreCase(Expression.OL_AND) || elementText.equalsIgnoreCase(Expression.OL_OR)  || elementText.equalsIgnoreCase(Expression.OL_NOT)) {
	
				if (getCurrentExpression().isForbidLogicalType()) {
					
					throw new ExpressionException(TEXTS.cannotIntroduceLogicalOps, position);
				}
				else if (!elementText.equalsIgnoreCase(Expression.OL_NOT) && getCurrentExpression().getComparativeOperator() == null && getCurrentExpression().getNature() != Expression.NATURE_LOGICAL) {
					throw new ExpressionException(TEXTS.partExpMustContainCompOp, position);
				}
				else {
	
					Expression parentExpression = getCurrentExpression().getParent();
					//En cuanto introducimos un operador lógico forzamos a que la expresión sea de tipo lógico
					getCurrentExpression().setForceLogicalType(true);
					if (parentExpression != null)
						parentExpression.setForceLogicalType(true);
					
					//Añadimos inmediatamente otro hijo
					if (elementText.equalsIgnoreCase(Expression.OL_NOT)) {
	
						if (parentExpression != null && parentExpression.size() > 1) {
	
							Object parentElement = parentExpression.elementAt(parentExpression.size() - 2);
							if (parentElement.toString().equalsIgnoreCase(Expression.OL_AND) || parentElement.toString().equalsIgnoreCase(Expression.OL_OR)) {
	
								parentExpression.insertElementAt(stackElement, parentExpression.size() - 1);
								return;
							}
						}	
					}
					else {
						
						setCurrentExpression(parentExpression);
					}				
					getCurrentExpression().setNature(Expression.NATURE_LOGICAL, null);
					Expression childExpression = new Expression(getCurrentExpression());
					//Agregamos el operador lógico
					getCurrentExpression().addElement(stackElement);
					//Agregamos un nuevo hijo
					getCurrentExpression().addElement(childExpression);
					setCurrentExpression(childExpression);
				}
			}
			else {
	
				//Añadimos el elemento
				getCurrentExpression().addElement(stackElement);
	
				//Comprobamos si es válido el último elemento añadido
				if (operatorType == Expression.OPERATOR_COMPARATIVE) {
	
					if (getCurrentExpression().isForbidLogicalType())
						throw new ExpressionException(TEXTS.cannotIntroduceCompOps, position);
					
					getCurrentExpression().setComparativeOperator(stackElement);
				}
				else if (operatorType == Expression.OPERATOR_ARITHMETIC) {
	
					if (getCurrentExpression().getNature() == -1) {
	
						getCurrentExpression().setNature(Expression.NATURE_NUMERICAL, stackElement);
					}
					else if (getCurrentExpression().getNature() == Expression.NATURE_DATE) {
	
						throw new ExpressionException(TEXTS.datExpsNotSupportArtimOps, position);
					}
					else if (getCurrentExpression().getNature() == Expression.NATURE_LOGICAL) {
	
						throw new ExpressionException(TEXTS.logicalExpsMustBeSparatedWithLogicalOp, position);
					}
					else if (getCurrentExpression().getNature() == Expression.NATURE_ALPHANUMERIC && !elementText.equalsIgnoreCase(Expression.OA_SUM)) {
	
						if (getCurrentExpression().getNature() != Expression.NATURE_NUMERICAL)
							throw new ExpressionException(TEXTS.artimOpNotValidForAlphaExps, position);
					}
				}
				else {
						
					if (elementNature != -1) {
					
						//El elemento actual es un valor
						getCurrentExpression().setNature(elementNature, stackElement);
					}
				}
			}
		}
	}
	
	public boolean isFunction() {
		return function;
	}
	protected Expression getCurrentExpression() {
		return currentExpression;
	}
	protected boolean isForceLogicalType() {
		return forceLogicalType;
	}
	public int getNature() {
		return nature;
	}
	public StackElement getComparativeOperator() {
		return comparativeOperator;
	}
	public Expression getParent() {
		return parent;
	}
	public Vector<StackElement> getStackElements() {
		return stackElements;
	}
	private boolean isForbidLogicalType() {
		return forbidLogicalType;
	}
	
	public StackElement getValue() throws ExpressionException {
	
		Object element = null;
		
		Expression expressionReduced = new Expression(null);
		try {
	
			for (int i = 0; i < this.size(); i++) {
	
				//Cuando encontramos una subExpresion dentro de la expresion llamamos RECURSIVAMENTE a getValor() de dicha subExpresión
				//De esta forma al final tendremos una única expresión sin subExpresiones que nos dará el valor final
				element = this.elementAt(i);
				if (element instanceof Expression) {
	
					Expression expression = (Expression) element;
					expressionReduced.addElement(expression.getValue());
	
					//Añadimos las variables de la subexpresión a la nueva expresión reducida
					for (int j = 0; j < expression.getVariables().size(); j++) {
	
						Variable variable = expression.getVariables().elementAt(j);
						if (!expressionReduced.getVariables().contains(variable))
							expressionReduced.getVariables().addElement(variable);
					}
				}
				else {
					
					expressionReduced.addElement(element);
					Variable variableAsociada = ((StackElement) element).getLinkedVariable();
					if (variableAsociada != null && !isFunction())
						variableAsociada.setCalledOutOfFunction(true);
				}
			}
	
			//En este punto la expresión reducida no contiene expresiones dentro
			StackElement value = getValueExpressionReduced(expressionReduced);
	
			if (value != null && value.toString().equalsIgnoreCase("true") && getNature() != Expression.NATURE_LOGICAL) {
	
				//Indicamos a las variables que se han cumplido para la subexpresión en la que intervienen
				for (int i = 0; i < expressionReduced.getVariables().size(); i++) {
	
					Variable variable = (Variable) expressionReduced.getVariables().elementAt(i);
					variable.setMetPartialExpression(true);
				}
			}
			return value;
		}
		catch (ExpressionException expEx) {
	
			throw expEx;
		}
		catch (Throwable ex) {
	
			int posicion = 0;
			if (element != null && element instanceof StackElement)
				posicion = ((StackElement) element).getPositionInText();
			
			throw new ExpressionException(TEXTS.errorTryingToResolveExp + " \"" + expressionReduced + "\"", posicion);
		}
	}
	
	private StackElement getValueExpressionReduced(Expression expressionReduced) throws ExpressionException {
	
		if (expressionReduced.size() == 0)
			return null;
		else if (expressionReduced.size() == 1) {
			
			StackElement stackElement = (StackElement) expressionReduced.firstElement();
			if (stackElement.getNature() != -1)
				return new StackElement("" + stackElement.getValue(), -1, stackElement.getPositionInText());
			else
				return stackElement;
		}
		else if (isFunction())
			return getValueExpressionReducedFunction(expressionReduced);
		else if (getNature() == Expression.NATURE_NUMERICAL)
			return getValueExpressionReducedNumerica(expressionReduced);
		else if (getNature() == Expression.NATURE_ALPHANUMERIC)
			return getValueExpressionReducedAlphanumeric(expressionReduced);
		else if (getNature() == Expression.NATURE_DATE)
			return getValueExpressionReducedDate(expressionReduced);
		else if (getNature() == Expression.NATURE_LOGICAL)
			return getValueExpressionReducedLogica(expressionReduced);
		else if (getNature() == Expression.NATURE_GLOBAL)
			return getValueExpressionReducedGlobal(expressionReduced);
		else
			throw new ExpressionException(TEXTS.errorTryingToResolveReducedExp + " \"" + expressionReduced + "\" "+ TEXTS.forNotHavingNature, 0);
	}
	
	private StackElement getValueExpressionReducedAlphanumeric(Expression expressionReduced) throws ExpressionException {
	
		StackElement elemOp1;
		StackElement elemOp2;
		StackElement elemResult = null;
		
		String operand1;
		String operand2;
		String result;
		//Hacemos las concatenaciones
		int indexSum = expressionReduced.indexOfStackElement(Expression.OA_SUM);
	
		while (indexSum != -1) {
	
			elemOp1 = (StackElement) expressionReduced.elementAt(indexSum - 1);
			elemOp2 = (StackElement) expressionReduced.elementAt(indexSum + 1);
	
			operand1 = elemOp1.getAlphanumericValue();
			operand2 = elemOp2.getAlphanumericValue();
			result = operand1 + operand2;
	
			elemResult = new StackElement("'" + result + "'", -1, elemOp1.getPositionInText());
			
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indexSum - 1);
			expressionReduced.removeElementAt(indexSum - 1);
			expressionReduced.removeElementAt(indexSum - 1);
			expressionReduced.insertElementAt(elemResult, indexSum - 1);
	
			indexSum = expressionReduced.indexOfStackElement(Expression.OA_SUM);
		}
	
		if (getComparativeOperator() != null) {
	
			//Si la expresión tiene operador comparativo buscamos cual es (Solo puede haber uno)
			String operator = Expression.OC_EQUAL;
			int index = expressionReduced.indexOfStackElement(operator);
	
			if (index == -1) {
	
				operator = Expression.OC_STARTS;
				index = expressionReduced.indexOfStackElement(operator);
				
				if (index == -1) {
	
					operator = Expression.OC_ENDS;
					index = expressionReduced.indexOfStackElement(operator);
	
					if (index == -1) {
	
						operator = Expression.OC_CONTAINS;
						index = expressionReduced.indexOfStackElement(operator);
	
						if (index == -1) {
	
							operator = Expression.OC_DISTINCT;
							index = expressionReduced.indexOfStackElement(operator);
	
							if (index == -1) {
	
								operator = Expression.OC_EQUAL_IGNORE;
								index = expressionReduced.indexOfStackElement(operator);
								
								if (index == -1) {
	
									operator = Expression.OC_STARTS_IGNORE;
									index = expressionReduced.indexOfStackElement(operator);
	
									if (index == -1) {
	
										operator = Expression.OC_ENDS_IGNORE;
										index = expressionReduced.indexOfStackElement(operator);
	
										if (index == -1) {
	
											operator = Expression.OC_CONTAINS_IGNORE;
											index = expressionReduced.indexOfStackElement(operator);
	
											if (index == -1) {
	
												operator = Expression.OC_DISTINCT_IGNORE;
												index = expressionReduced.indexOfStackElement(operator);
											}
										}
									}
								}
							}
						}
					}
				}
			}
	
			if (index != -1) {
	
				elemOp1 = (StackElement) expressionReduced.elementAt(index - 1);
				elemOp2 = (StackElement) expressionReduced.elementAt(index + 1);
	
				try {
					
					operand1 = elemOp1.getAlphanumericValue();
					operand2 = elemOp2.getAlphanumericValue();
	
					if (operator.equals(Expression.OC_EQUAL))			
						elemResult = new StackElement("" + operand1.equals(operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_STARTS))				
						elemResult = new StackElement("" + operand1.startsWith(operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_ENDS))
						elemResult = new StackElement("" + operand1.endsWith(operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_CONTAINS))
						elemResult = new StackElement("" + (operand1.indexOf(operand2) != -1), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_DISTINCT))
						elemResult = new StackElement("" + !operand1.equals(operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_EQUAL_IGNORE))
						elemResult = new StackElement("" + operand1.trim().equalsIgnoreCase(operand2.trim()), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_STARTS_IGNORE))
						elemResult = new StackElement("" + operand1.trim().toUpperCase().startsWith(operand2.trim().toUpperCase()), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_ENDS_IGNORE))
						elemResult = new StackElement("" + operand1.trim().toUpperCase().endsWith(operand2.trim().toUpperCase()), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_CONTAINS_IGNORE))
						elemResult = new StackElement("" + (operand1.trim().toUpperCase().indexOf(operand2.trim().toUpperCase()) != -1), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_DISTINCT_IGNORE))
						elemResult = new StackElement("" + !operand1.trim().equalsIgnoreCase(operand2.trim()), -1, elemOp1.getPositionInText());
	
				} catch (Throwable ex) {
					elemResult = new StackElement("false", -1, elemOp1.getPositionInText());
				}
				//Sustituimos los operandos y el operador por el resultado
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.insertElementAt(elemResult, index - 1);
			}
		}
	
		//LLegados a este punto la expresión debe tener un único elemento
		if (expressionReduced.size() == 1) {
	
			return elemResult;
		}
		else throw new ExpressionException(TEXTS.alphanumericReducedExp + " \""+ expressionReduced +"\" " + TEXTS.couldNotBeSolved, elemResult.getPositionInText());
	}
	
	private StackElement getValueExpressionReducedDate(Expression expressionReduced) throws ExpressionException {
	
		StackElement elemOp1;
		StackElement elemOp2;
		StackElement elemResult = null;
	
		Date operand1, operand2;
		boolean result = false;
		
		if (getComparativeOperator() != null) {
	
			//Si la expresión tiene operador comparativo buscamos cual es (Solo puede haber uno)
			String operator = Expression.OC_EQUAL;
			int index = expressionReduced.indexOfStackElement(operator);
	
			if (index == -1) {
	
				operator = Expression.OC_GREATER;
				index = expressionReduced.indexOfStackElement(operator);
				
				if (index == -1) {
	
					operator = Expression.OC_LESS;
					index = expressionReduced.indexOfStackElement(operator);
	
					if (index == -1) {
	
						operator = Expression.OC_DISTINCT;
						index = expressionReduced.indexOfStackElement(operator);
	
						if (index == -1) {
	
							operator = Expression.OC_EQUAL_IGNORE;
							index = expressionReduced.indexOfStackElement(operator);
	
							if (index == -1) {
	
								operator = Expression.OC_GREATER_IGNORE;
								index = expressionReduced.indexOfStackElement(operator);
								
								if (index == -1) {
	
									operator = Expression.OC_LESS_IGNORE;
									index = expressionReduced.indexOfStackElement(operator);
	
									if (index == -1) {
	
										operator = Expression.OC_DISTINCT_IGNORE;
										index = expressionReduced.indexOfStackElement(operator);
	
										if (index == -1) {
	
											operator = Expression.OC_GREATER_EQUAL;
											index = expressionReduced.indexOfStackElement(operator);
	
											if (index == -1) {
	
												operator = Expression.OC_LESS_EQUAL;
												index = expressionReduced.indexOfStackElement(operator);
												
												if (index == -1) {
	
													operator = Expression.OC_EQUAL_GREATER;
													index = expressionReduced.indexOfStackElement(operator);
	
													if (index == -1) {
	
														operator = Expression.OC_EQUAL_LESS;
														index = expressionReduced.indexOfStackElement(operator);
	
														if (index == -1) {
	
															operator = Expression.OC_GREATER_EQUAL_IGNORE;
															index = expressionReduced.indexOfStackElement(operator);
	
															if (index == -1) {
	
																operator = Expression.OC_LESS_EQUAL_IGNORE;
																index = expressionReduced.indexOfStackElement(operator);
																
																if (index == -1) {
	
																	operator = Expression.OC_EQUAL_GREATER_IGNORE;
																	index = expressionReduced.indexOfStackElement(operator);
	
																	if (index == -1) {
	
																		operator = Expression.OC_EQUAL_LESS_IGNORE;
																		index = expressionReduced.indexOfStackElement(operator);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
	
			if (index != -1) {
	
				elemOp1 = (StackElement) expressionReduced.elementAt(index - 1);
				elemOp2 = (StackElement) expressionReduced.elementAt(index + 1);
	
				try {
	
					if (elemOp1.getValue() instanceof Date)
						operand1 = (Date) elemOp1.getValue();
					else if (elemOp1.getValue() instanceof Calendar)
						operand1 = ((Calendar)elemOp1.getValue()).getTime();
					else
						operand1 = null;
						
					if (elemOp2.getValue() instanceof Date)
						operand2 = (Date) elemOp2.getValue();
					else if (elemOp1.getValue() instanceof Calendar)
						operand2 = ((Calendar)elemOp2.getValue()).getTime();
					else
						operand2 = null;
	
					if (operator.equals(Expression.OC_EQUAL))			
						result = Dates.compare(operand1, operand2) == 0;
					
					else if (operator.equals(Expression.OC_GREATER))				
						result = Dates.compare(operand1, operand2) > 0;
					
					else if (operator.equals(Expression.OC_LESS))
						result = Dates.compare(operand1, operand2) < 0;
					
					else if (operator.equals(Expression.OC_DISTINCT))
						result = Dates.compare(operand1, operand2) != 0;
					
					else if (operator.equals(Expression.OC_GREATER_EQUAL) || operator.equals(Expression.OC_EQUAL_GREATER))
						result = Dates.compare(operand1, operand2) >= 0;
					
					else if (operator.equals(Expression.OC_LESS_EQUAL) || operator.equals(Expression.OC_EQUAL_LESS))
						result = Dates.compare(operand1, operand2) <= 0;
					
					else if (operator.equals(Expression.OC_EQUAL_IGNORE))
						result = Dates.compareIgnoringTimeOfDay(operand1, operand2) == 0;
					
					else if (operator.equals(Expression.OC_GREATER_IGNORE))
						result = Dates.compareIgnoringTimeOfDay(operand1, operand2) > 0;
					
					else if (operator.equals(Expression.OC_LESS_IGNORE))
						result = Dates.compareIgnoringTimeOfDay(operand1, operand2) < 0;
					
					else if (operator.equals(Expression.OC_DISTINCT_IGNORE))
						result = Dates.compareIgnoringTimeOfDay(operand1, operand2) != 0;
	
					else if (operator.equals(Expression.OC_GREATER_EQUAL_IGNORE) || operator.equals(Expression.OC_EQUAL_GREATER_IGNORE))
						result = Dates.compareIgnoringTimeOfDay(operand1, operand2) >= 0;
	
					else if (operator.equals(Expression.OC_LESS_EQUAL_IGNORE) || operator.equals(Expression.OC_EQUAL_LESS_IGNORE))
						result = Dates.compareIgnoringTimeOfDay(operand1, operand2) <= 0;
					
					elemResult = new StackElement(String.valueOf(result), -1, elemOp1.getPositionInText());
	
				} catch (Throwable ex) {
					elemResult = new StackElement(String.valueOf(false), -1, elemOp1.getPositionInText());
				}
				
				//Sustituimos los operandos y el operador por el resultado
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.insertElementAt(elemResult, index - 1);
			}
		}
	
		//LLegados a este punto la expresión debe tener un único elemento
		if (expressionReduced.size() == 1) {
	
			return elemResult;
		}
		else throw new ExpressionException(TEXTS.dateReducedExp + " \""+ expressionReduced +"\" " + TEXTS.couldNotBeSolved, elemResult.getPositionInText());
	}
	
	private StackElement getValueExpressionReducedFunction(Expression expressionReduced) throws ExpressionException {
	
		StackElement functionElement;
		StackElement stackElement;
		StackElement elemResult = null;
		Vector<StackElement> functionElements;
		Variable variable;
		Vector<Object> values;
		
		final String[] functions = {Expression.OF_MAX,
							  Expression.OF_MIN,
							  Expression.OF_MAX2,
							  Expression.OF_MIN2,
							  Expression.OF_SUM,
							  Expression.OF_AVG,
							  Expression.OF_ABS};
	
		for (int i = 0; i < functions.length; i++) {
	
			final String function = functions[i];
			int indexFunction = expressionReduced.indexOfStackElement(function);
	
			while (indexFunction != -1) {
	
				Object functionResult;
				values = new Vector<>();
				functionElements = new Vector<>();
				functionElement = (StackElement) expressionReduced.elementAt(indexFunction);
				
				if (function.equalsIgnoreCase(Expression.OF_ABS) && expressionReduced.size() > 2) {
	
					//eliminamos el elemento función y obtenemos el valor de lo que haya dentro de la función
					expressionReduced.removeElementAt(0);
					Vector<StackElement> elementsReducedExp = new Vector<>();
					for (int j = 0; j < expressionReduced.size(); j++) {
						Object elem = expressionReduced.elementAt(i);
						if (elem != null && elem instanceof StackElement)
							elementsReducedExp.add((StackElement) elem);
					}
					Expression expressionContentFunction = ExpressionsAnalyzer.parseStackElements(elementsReducedExp, expressionReduced.getVariables(), false);
					StackElement resultFunctionContent = expressionContentFunction.getValue();
					//Volvemos a componer la función con el operador función y el resultado del contenido
					expressionReduced.removeAllElements();
					expressionReduced.addElement(functionElement);
					expressionReduced.addElement(resultFunctionContent);
				}
				
				stackElement = (StackElement) expressionReduced.elementAt(indexFunction + 1);
				
				//Recorremos todos los elementos de la función para formar otro vector con los resulatados de aplicar la función a cada elemento
				while (stackElement != null && stackElement.getOperatorType() == -1) {
	
					variable = stackElement.getLinkedVariable();
					if (variable == null)					
						functionResult = stackElement.getValue();
					else					
						functionResult = variable.getFunctionValue(function);
					
					functionElements.addElement(stackElement);
					values.addElement(functionResult);
	
					//Eliminamos el elemento de la expresión una vez almacenado el valor de la función aplicado sobre el.
					expressionReduced.removeElementAt(indexFunction + 1);
					stackElement = null;
					if (expressionReduced.size() > indexFunction + 1)
						stackElement = (StackElement) expressionReduced.elementAt(indexFunction + 1);
				}
				//Eliminamos el elemento de la función
				expressionReduced.removeElementAt(indexFunction);
	
				//Recorremos todos los elementos de la función para actualizarlos con la función y su resultado
				for (int j = 0; j < functionElements.size(); j++) {
	
					Object valor = ExpressionsAnalyzer.getFunctionValue(function, values, null);
					stackElement = (StackElement) functionElements.elementAt(j);
					stackElement.addCallingFunction(function);
					stackElement.addFunctionResult(valor);
				}
	
				//Aplicamos la función al vector creado anteriormente
				functionResult = ExpressionsAnalyzer.getFunctionResult(function, values, null);
				
				//terminamos de convertir la función en un único elemento resultado
				if (getNature() == Expression.NATURE_ALPHANUMERIC)
					functionResult = "'" + functionResult + "'";
				
				elemResult = new StackElement(functionResult.toString(), -1, functionElement.getPositionInText());
				expressionReduced.insertElementAt(elemResult, indexFunction);
	
				//Buscamos otra función dentro de la expresión
				indexFunction = expressionReduced.indexOfStackElement(Expression.OF_MAX);
			}
		}
		
		//LLegados a este punto la expresión debe tener un único elemento
		if (expressionReduced.size() == 1) {
	
			return elemResult;
		}
		else throw new ExpressionException(TEXTS.function + " \""+ expressionReduced +"\" " + TEXTS.couldNotBeSolved , elemResult.getPositionInText());
	}
	
	private StackElement getValueExpressionReducedGlobal(Expression expressionReduced) throws ExpressionException {
	
		Vector<StackElement> elementsWithoutGlobalVariables = new Vector<>();
		
		for (int i = 0; i < expressionReduced.size(); i++) {
	
			StackElement stackElement = (StackElement) expressionReduced.elementAt(i);
			if (stackElement.getNature() == Expression.NATURE_GLOBAL) {
	
				//Convertimos el elemento con naturaleza global en un elemento con naturaleza
				Object value = stackElement.getValue();
				Object nonAlphanumericData = GlobalDataConverter.getNonAlphanumericData(value.toString());
	
				if (nonAlphanumericData == null) {
	
					stackElement = new StackElement("'"+value.toString()+"'", -1, stackElement.getPositionInText());
				}
				else {
					
					if (nonAlphanumericData instanceof Date)
						stackElement = new StackElement(StackElement.getFormattedDate((Date) nonAlphanumericData), -1, stackElement.getPositionInText());
					else
						stackElement = new StackElement(nonAlphanumericData.toString(), -1, stackElement.getPositionInText());
	
					stackElement.setAlphanumericValue(value.toString());
				}
			}
			elementsWithoutGlobalVariables.add(stackElement);
		}	
	
		Expression expressionWithoutGlobalVariables = ExpressionsAnalyzer.parseStackElements(elementsWithoutGlobalVariables, getVariables(), false);
		
		return expressionWithoutGlobalVariables.getValue();
	}
	
	private StackElement getValueExpressionReducedLogica(Expression expressionReduced) throws ExpressionException {
	
		StackElement elemOp1;
		StackElement elemOp2;
		StackElement elemResult = null;
		boolean operand1;
		boolean operand2;
		boolean result;
		
		//Hacemos los NOT
		int indiceNOT = expressionReduced.indexOfStackElement(Expression.OL_NOT);
	
		while (indiceNOT != -1) {
	
			elemOp1 = (StackElement) expressionReduced.elementAt(indiceNOT + 1);
			
			operand1 = new Boolean(elemOp1.toString()).booleanValue();
			result = !operand1;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indiceNOT);
			expressionReduced.removeElementAt(indiceNOT);
			expressionReduced.insertElementAt(elemResult, indiceNOT);
	
			indiceNOT = expressionReduced.indexOfStackElement(Expression.OL_NOT);
		}
	
		//Hacemos los AND
		int indiceAND = expressionReduced.indexOfStackElement(Expression.OL_AND);
	
		while (indiceAND != -1) {
	
			elemOp1 = (StackElement) expressionReduced.elementAt(indiceAND - 1);
			elemOp2 = (StackElement) expressionReduced.elementAt(indiceAND + 1);
	
			operand1 = new Boolean(elemOp1.toString()).booleanValue();
			operand2 = new Boolean(elemOp2.toString()).booleanValue();
			result = operand1 && operand2;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indiceAND - 1);
			expressionReduced.removeElementAt(indiceAND - 1);
			expressionReduced.removeElementAt(indiceAND - 1);
			expressionReduced.insertElementAt(elemResult, indiceAND - 1);
	
			indiceAND = expressionReduced.indexOfStackElement(Expression.OL_AND);
		}
		
		//Hacemos los OR
		int indiceOR = expressionReduced.indexOfStackElement(Expression.OL_OR);
	
		while (indiceOR != -1) {
	
			elemOp1 = (StackElement) expressionReduced.elementAt(indiceOR - 1);
			elemOp2 = (StackElement) expressionReduced.elementAt(indiceOR + 1);
	
			operand1 = new Boolean(elemOp1.toString()).booleanValue();
			operand2 = new Boolean(elemOp2.toString()).booleanValue();
			result = operand1 || operand2;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indiceOR - 1);
			expressionReduced.removeElementAt(indiceOR - 1);
			expressionReduced.removeElementAt(indiceOR - 1);
			expressionReduced.insertElementAt(elemResult, indiceOR - 1);
	
			indiceOR = expressionReduced.indexOfStackElement(Expression.OL_OR);
		}
	
		//LLegados a este punto la expresión debe tener un único elemento
		if (expressionReduced.size() == 1) {
	
			return elemResult;
		}
		else throw new ExpressionException(TEXTS.logicalReducedExp + " \""+ expressionReduced +"\" " + TEXTS.couldNotBeSolved, elemResult.getPositionInText());
	}
	
	private StackElement getValueExpressionReducedNumerica(Expression expressionReduced) throws ExpressionException {
	
		StackElement elemOp1;
		StackElement elemOp2;
		StackElement elemResult = null;
		double operand1;
		double operand2;
		double result;
		//Hacemos las multiplicaciones y divisiones
		int indiceMultiplica = expressionReduced.indexOfStackElement(Expression.OA_MUL);
	
		while (indiceMultiplica != -1) {
	
			elemOp1 = (StackElement) expressionReduced.elementAt(indiceMultiplica - 1);
			elemOp2 = (StackElement) expressionReduced.elementAt(indiceMultiplica + 1);
	
			if (elemOp2.equals(Expression.OA_SUB)) {
	
				//Sustituímos el menos por el elemento siguiente en negativo
				elemOp2 = new StackElement(elemOp2.toString() + expressionReduced.elementAt(indiceMultiplica + 2).toString(), -1, elemOp2.getPositionInText());
	
				expressionReduced.removeElementAt(indiceMultiplica + 1);
				expressionReduced.setElementAt(elemOp2, indiceMultiplica + 1);
			}
	
			operand1 = new Double(elemOp1.getValue().toString()).doubleValue();
			operand2 = new Double(elemOp2.getValue().toString()).doubleValue();
			result = operand1 * operand2;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indiceMultiplica - 1);
			expressionReduced.removeElementAt(indiceMultiplica - 1);
			expressionReduced.removeElementAt(indiceMultiplica - 1);
			expressionReduced.insertElementAt(elemResult, indiceMultiplica - 1);
	
			indiceMultiplica = expressionReduced.indexOfStackElement(Expression.OA_MUL);
		}
	
		int indiceDivision = expressionReduced.indexOfStackElement(Expression.OA_DIV);
	
		while (indiceDivision != -1) {
	
			elemOp1 = (StackElement) expressionReduced.elementAt(indiceDivision - 1);
			elemOp2 = (StackElement) expressionReduced.elementAt(indiceDivision + 1);
	
			if (elemOp2.equals(Expression.OA_SUB)) {
	
				//Sustituímos el menos por el elemento siguiente en negativo
				elemOp2 = new StackElement(elemOp2.toString() + expressionReduced.elementAt(indiceDivision + 2).toString(), -1, elemOp2.getPositionInText());
	
				expressionReduced.removeElementAt(indiceDivision + 1);
				expressionReduced.setElementAt(elemOp2, indiceDivision + 1);
			}
	
			operand1 = new Double(elemOp1.getValue().toString()).doubleValue();
			operand2 = new Double(elemOp2.getValue().toString()).doubleValue();
			result = operand1 / operand2;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indiceDivision - 1);
			expressionReduced.removeElementAt(indiceDivision - 1);
			expressionReduced.removeElementAt(indiceDivision - 1);
			expressionReduced.insertElementAt(elemResult, indiceDivision - 1);
	
			indiceDivision = expressionReduced.indexOfStackElement(Expression.OA_DIV);
		}
	
		//Hacemos las sumas y restas
		int indexSum = expressionReduced.indexOfStackElement(Expression.OA_SUM);
	
		while (indexSum != -1) {
	
			if (indexSum == 0) {
				//Insertamos el elmento virtual cero
				elemOp1 = new StackElement("0", -1, ((StackElement) expressionReduced.elementAt(0)).getPositionInText());
				expressionReduced.insertElementAt(elemOp1, 0);
			}
			else {
				elemOp1 = (StackElement) expressionReduced.elementAt(indexSum - 1);
			}
			elemOp2 = (StackElement) expressionReduced.elementAt(indexSum + 1);
	
			if (elemOp2.equals(Expression.OA_SUB)) {
	
				//Sustituímos el menos por el elemento siguiente en negativo
				elemOp2 = new StackElement(elemOp2.toString() + expressionReduced.elementAt(indexSum + 2).toString(), -1, elemOp2.getPositionInText());
	
				expressionReduced.removeElementAt(indexSum + 1);
				expressionReduced.setElementAt(elemOp2, indexSum + 1);
			}
	
			operand1 = new Double(elemOp1.getValue().toString()).doubleValue();
			operand2 = new Double(elemOp2.getValue().toString()).doubleValue();
			result = operand1 + operand2;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indexSum - 1);
			expressionReduced.removeElementAt(indexSum - 1);
			expressionReduced.removeElementAt(indexSum - 1);
			expressionReduced.insertElementAt(elemResult, indexSum - 1);
	
			indexSum = expressionReduced.indexOfStackElement(Expression.OA_SUM);
		}
	
		int indexSubtract = expressionReduced.indexOfStackElement(Expression.OA_SUB);
	
		while (indexSubtract != -1) {
	
			if (indexSubtract == 0 || ((StackElement) expressionReduced.elementAt(indexSubtract - 1)).getOperatorType() != -1) {
				//Insertamos el elmento virtual cero
				elemOp1 = new StackElement("0", -1, ((StackElement) expressionReduced.elementAt(0)).getPositionInText());
				expressionReduced.insertElementAt(elemOp1, indexSubtract);
				indexSubtract = indexSubtract + 1;
			}
			else {
				elemOp1 = (StackElement) expressionReduced.elementAt(indexSubtract - 1);
			}
			elemOp2 = (StackElement) expressionReduced.elementAt(indexSubtract + 1);
	
			if (elemOp2.equals(Expression.OA_SUB)) {
	
				//Sustituímos el menos por el elemento siguiente en negativo
				elemOp2 = new StackElement(elemOp2.toString() + expressionReduced.elementAt(indexSubtract + 2).toString(), -1, elemOp2.getPositionInText());
	
				expressionReduced.removeElementAt(indexSubtract + 1);
				expressionReduced.setElementAt(elemOp2, indexSubtract + 1);
			}
	
			operand1 = new Double(elemOp1.getValue().toString()).doubleValue();
			operand2 = new Double(elemOp2.getValue().toString()).doubleValue();
			result = operand1 - operand2;
	
			elemResult = new StackElement("" + result, -1, elemOp1.getPositionInText());
	
			//Sustituimos los operandos y el operador por el resultado
			expressionReduced.removeElementAt(indexSubtract - 1);
			expressionReduced.removeElementAt(indexSubtract - 1);
			expressionReduced.removeElementAt(indexSubtract - 1);
			expressionReduced.insertElementAt(elemResult, indexSubtract - 1);
	
			indexSubtract = expressionReduced.indexOfStackElement(Expression.OA_SUB);
		}
		if (getComparativeOperator() != null) {
	
			//Si la expresión tiene operador comparativo buscamos cual es (Solo puede haber uno)
			String operator = Expression.OC_EQUAL;
			int index = expressionReduced.indexOfStackElement(operator);
	
			if (index == -1) {
	
				operator = Expression.OC_GREATER;
				index = expressionReduced.indexOfStackElement(operator);
				
				if (index == -1) {
	
					operator = Expression.OC_LESS;
					index = expressionReduced.indexOfStackElement(operator);
	
					if (index == -1) {
	
						operator = Expression.OC_DISTINCT;
						index = expressionReduced.indexOfStackElement(operator);
	
						if (index == -1) {
	
							operator = Expression.OC_EQUAL_IGNORE;
							index = expressionReduced.indexOfStackElement(operator);
	
							if (index == -1) {
	
								operator = Expression.OC_GREATER_IGNORE;
								index = expressionReduced.indexOfStackElement(operator);
								
								if (index == -1) {
	
									operator = Expression.OC_LESS_IGNORE;
									index = expressionReduced.indexOfStackElement(operator);
	
									if (index == -1) {
	
										operator = Expression.OC_DISTINCT_IGNORE;
										index = expressionReduced.indexOfStackElement(operator);
	
										if (index == -1) {
	
											operator = Expression.OC_GREATER_EQUAL;
											index = expressionReduced.indexOfStackElement(operator);
	
											if (index == -1) {
	
												operator = Expression.OC_LESS_EQUAL;
												index = expressionReduced.indexOfStackElement(operator);
												
												if (index == -1) {
	
													operator = Expression.OC_EQUAL_GREATER;
													index = expressionReduced.indexOfStackElement(operator);
	
													if (index == -1) {
	
														operator = Expression.OC_EQUAL_LESS;
														index = expressionReduced.indexOfStackElement(operator);
	
														if (index == -1) {
	
															operator = Expression.OC_GREATER_EQUAL_IGNORE;
															index = expressionReduced.indexOfStackElement(operator);
	
															if (index == -1) {
	
																operator = Expression.OC_LESS_EQUAL_IGNORE;
																index = expressionReduced.indexOfStackElement(operator);
																
																if (index == -1) {
	
																	operator = Expression.OC_EQUAL_GREATER_IGNORE;
																	index = expressionReduced.indexOfStackElement(operator);
	
																	if (index == -1) {
	
																		operator = Expression.OC_EQUAL_LESS_IGNORE;
																		index = expressionReduced.indexOfStackElement(operator);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
	
			if (index != -1) {
	
				elemOp1 = (StackElement) expressionReduced.elementAt(index - 1);
				elemOp2 = (StackElement) expressionReduced.elementAt(index + 1);
	
				try {
				
					operand1 = new Double(elemOp1.getValue().toString()).doubleValue();
					operand2 = new Double(elemOp2.getValue().toString()).doubleValue();
					
					if (operator.equals(Expression.OC_EQUAL))			
						elemResult = new StackElement("" + (operand1 == operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_GREATER))				
						elemResult = new StackElement("" + (operand1 > operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_LESS))
						elemResult = new StackElement("" + (operand1 < operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_DISTINCT))
						elemResult = new StackElement("" + (operand1 != operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_EQUAL_IGNORE))
						elemResult = new StackElement("" + (operand1 == operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_GREATER_IGNORE))
						elemResult = new StackElement("" + (operand1 > operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_LESS_IGNORE))
						elemResult = new StackElement("" + (operand1 < operand2), -1, elemOp1.getPositionInText());
					
					else if (operator.equals(Expression.OC_DISTINCT_IGNORE))
						elemResult = new StackElement("" + (operand1 != operand2), -1, elemOp1.getPositionInText());
	
					else if (operator.equals(Expression.OC_GREATER_EQUAL) || operator.equals(Expression.OC_EQUAL_GREATER))
						elemResult = new StackElement("" + (operand1 >= operand2), -1, elemOp1.getPositionInText());
	
					else if (operator.equals(Expression.OC_LESS_EQUAL) || operator.equals(Expression.OC_EQUAL_LESS))
						elemResult = new StackElement("" + (operand1 <= operand2), -1, elemOp1.getPositionInText());
	
					else if (operator.equals(Expression.OC_GREATER_EQUAL_IGNORE) || operator.equals(Expression.OC_EQUAL_GREATER_IGNORE))
						elemResult = new StackElement("" + (operand1 >= operand2), -1, elemOp1.getPositionInText());
	
					else if (operator.equals(Expression.OC_LESS_EQUAL_IGNORE) || operator.equals(Expression.OC_EQUAL_LESS_IGNORE))
						elemResult = new StackElement("" + (operand1 <= operand2), -1, elemOp1.getPositionInText());			
				
				} catch (Throwable ex) {
					elemResult = new StackElement("false", -1, elemOp1.getPositionInText());
				}
				
				//Sustituimos los operandos y el operador por el resultado
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.removeElementAt(index - 1);
				expressionReduced.insertElementAt(elemResult, index - 1);
			}
		}
	
		//LLegados a este punto la expresión debe tener un único elemento
		if (expressionReduced.size() == 1) {
	
			return elemResult;
		}
		else throw new ExpressionException(TEXTS.numericReducedExp + " \""+ expressionReduced +"\" " + TEXTS.couldNotBeSolved, elemResult.getPositionInText());
	}
	
	public Vector<Variable> getVariables() {
	
		if (variables == null)
			variables = Lists.newVector();
		
		return variables;
	}
	
	public final int indexOfStackElement(String str) {
	
		try {
	
			for (int i = 0; i < this.size(); i++) {
	
				if (this.elementAt(i).toString().equalsIgnoreCase(str))
					return i;
			}
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
		return -1;
	}
	
	public void printConsole() {
	
		try {
	
			Vector<StackElement> elements = getStackElements();
	
			Console.println("\n----- " + TEXTS.expAnalized + " -----", Console.TYPE_DATA_OTHER);
			
			StackElement stackElement;
			StackElement nextStackElement;
			String elementText;
			int operatorType;
	
			boolean weAreInFunction = false;
			int indexFuncion = -1;
			
			Color color;
			for (int i = 0; i < elements.size(); i++) {
	
				stackElement = elements.elementAt(i);
				nextStackElement = null;
				if (i < elements.size() - 1)
					nextStackElement = elements.elementAt(i + 1);
				
				operatorType = stackElement.getOperatorType();
				color = stackElement.getColor();
				elementText = stackElement.toString();
	
				if (operatorType == Expression.OPERATOR_FUNCTION)	{
						
					weAreInFunction = true;
				}
				else if (weAreInFunction && elementText.equalsIgnoreCase(Expression.OS_CLOSE)) {
	
					weAreInFunction = false;
				}
				
				if (operatorType == Expression.OPERATOR_ARITHMETIC
					|| operatorType == Expression.OPERATOR_COMPARATIVE
					|| operatorType == Expression.OPERATOR_LOGICAL) {
	
					if (elementText.equalsIgnoreCase(Expression.OL_NOT))
						elementText = elementText + " ";
					else
						elementText = " " + elementText + " ";
				}
				else if (weAreInFunction && operatorType == -1 && nextStackElement != null && !nextStackElement.toString().equalsIgnoreCase(Expression.OS_CLOSE)) {
	
					elementText = elementText + " ";
				}
	
				if (color.equals(java.awt.Color.black))
					Console.print(elementText, Console.TYPE_DATA_OTHER);
				else
					Console.print(elementText, color, Console.TYPE_DATA_OTHER);
			}
			
			Console.println("\n<--->", Console.TYPE_DATA_OTHER);
	
			weAreInFunction = false;
			indexFuncion = 0;
			
			boolean functionResultObtained = false;
			
			for (int i = 0; i < elements.size(); i++) {
	
				stackElement = (StackElement) elements.elementAt(i);
				operatorType = stackElement.getOperatorType();
				color = stackElement.getColor();
				elementText = stackElement.toString();
				
				if (operatorType == Expression.OPERATOR_FUNCTION) {
	
					functionResultObtained = false;
					weAreInFunction = true;
				}
				else if (weAreInFunction) {
	
					if (elementText.equalsIgnoreCase(Expression.OS_CLOSE)) {
	
						weAreInFunction = false;
					}
					else if (operatorType == -1 && !functionResultObtained) {
	
						functionResultObtained = true;
						//Cualquiera de los elementos de la función tiene almacenado el resultado, así que lo obtenemos del primero que pillemos.
						Object functionValue = stackElement.getFunctionsResults().elementAt(indexFuncion);
						if (functionValue != null)
							elementText = functionValue.toString();
						else
							elementText = "null";
	
						if (color.equals(java.awt.Color.black))
							Console.print(elementText, Console.TYPE_DATA_OTHER);
						else
							Console.print(elementText, color, Console.TYPE_DATA_OTHER);
					}
				}
				else {
					
					if (stackElement.getLinkedVariable() != null) {
	
						Variable variable = stackElement.getLinkedVariable();
						if (variable.getValue() != null)
							elementText = variable.getValue().toString();
						else
							elementText = "null";
					}
					else if (operatorType == Expression.OPERATOR_ARITHMETIC
						  || operatorType == Expression.OPERATOR_COMPARATIVE
						  || operatorType == Expression.OPERATOR_LOGICAL) {
	
						if (elementText.equalsIgnoreCase(Expression.OL_NOT))
							elementText = elementText + " ";
						else
							elementText = " " + elementText + " ";
					}
					
					if (color.equals(java.awt.Color.black))
						Console.print(elementText, Console.TYPE_DATA_OTHER);
					else
						Console.print(elementText, color, Console.TYPE_DATA_OTHER);
				}
			}
			Console.println("\n-----------------------------------", Console.TYPE_DATA_OTHER);
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void setCurrentExpression(Expression newExpresionActual) {
		currentExpression = newExpresionActual;
	}
	protected void setForceLogicalType(boolean newForzarTipoLogico) {
		forceLogicalType = newForzarTipoLogico;
	}
	private void setFunction(boolean newFuncion) {
		function = newFuncion;
	}
	
	private void setNature(int newNature, StackElement lastInsertedElement) throws ExpressionException {
	
		if (lastInsertedElement != null && nature != -1 && newNature != -1 && nature != newNature) {
	
			if (nature == Expression.NATURE_NUMERICAL) {
				
				if (newNature == Expression.NATURE_DATE) {
					
					throw new ExpressionException(TEXTS.cannotMixDateValues, lastInsertedElement.getPositionInText());
				}
				else if (newNature == Expression.NATURE_ALPHANUMERIC) {
					
					if (hasNumericArithmeticOperators()) {
						
						throw new ExpressionException(TEXTS.cannotInsertAlpha, lastInsertedElement.getPositionInText());
					}
					else if (getComparativeOperator() != null) {
	
						String comparativeOperator = getComparativeOperator().toString();
						if (comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER_EQUAL) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS_EQUAL) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_GREATER) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_LESS) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER_EQUAL_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS_EQUAL_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_GREATER_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_LESS_IGNORE)) {
							
							throw new ExpressionException(TEXTS.expNatureNotConsistentWithCompOp, lastInsertedElement.getPositionInText());
						}
					}
				}
			}
			else if (nature == Expression.NATURE_ALPHANUMERIC) {
	
				if (newNature == Expression.NATURE_DATE)
					throw new ExpressionException(TEXTS.cannotMixDateValues, lastInsertedElement.getPositionInText());
				else
					return; //Si la nature es alfanumérica no puede llegar a ser numérica o global
			}
			else if (nature == Expression.NATURE_DATE) {
	
				if (newNature != Expression.NATURE_DATE || newNature != Expression.NATURE_GLOBAL)
					throw new ExpressionException(TEXTS.cannotMixDateValues, lastInsertedElement.getPositionInText());
			}
			else if (nature == Expression.NATURE_GLOBAL) {
	
				if (newNature == Expression.NATURE_ALPHANUMERIC) {
					
					if (hasNumericArithmeticOperators()) {
						
						throw new ExpressionException(TEXTS.cannotInsertAlpha, lastInsertedElement.getPositionInText());
					}
					else if (getComparativeOperator() != null) {
	
						String comparativeOperator = getComparativeOperator().toString();
						if (comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER_EQUAL) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS_EQUAL) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_GREATER) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_LESS) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_GREATER_EQUAL_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_LESS_EQUAL_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_GREATER_IGNORE) ||
							comparativeOperator.equalsIgnoreCase(Expression.OC_EQUAL_LESS_IGNORE)) {
							
							throw new ExpressionException(TEXTS.expNatureNotConsistentWithCompOp, lastInsertedElement.getPositionInText());
						}
					}
				}
				else
					return; //Si la nature es global no se podrá cambiar por numérica o fecha
			}
		}
		nature = newNature;
	}
	
	private void setComparativeOperator(StackElement comparativeOperatorElement) throws ExpressionException {
	
		if (comparativeOperatorElement != null) {
	
			int positionInText = comparativeOperatorElement.getPositionInText();
			String comparativeOperatorText = comparativeOperatorElement.toString();
			
			if (this.getComparativeOperator() != null) {
				throw new ExpressionException(TEXTS.partialExpOnlyOneOp, positionInText);
			}
			else {
	
				if (this.getNature() == -1) {
	
					throw new ExpressionException(TEXTS.compOpNotConsistentWithPartialExpNature, positionInText);
				}
				else if (this.getNature() == Expression.NATURE_ALPHANUMERIC) {
	
					if (comparativeOperatorText.equalsIgnoreCase(Expression.OC_GREATER) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_LESS) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_GREATER_IGNORE) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_LESS_IGNORE) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_GREATER_EQUAL) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_LESS_EQUAL) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_EQUAL_GREATER) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_EQUAL_LESS) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_GREATER_EQUAL_IGNORE) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_LESS_EQUAL_IGNORE) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_EQUAL_GREATER_IGNORE) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_EQUAL_LESS_IGNORE)) {
						
						throw new ExpressionException(TEXTS.compOpNotConsistentWithPartialExpNature, positionInText);
					}
				}
				else if (this.getNature() == Expression.NATURE_DATE) {
	
					if (comparativeOperatorText.equalsIgnoreCase(Expression.OC_STARTS) || 
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_ENDS) || 
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_CONTAINS) ||
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_STARTS_IGNORE) || 
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_ENDS_IGNORE) || 
						comparativeOperatorText.equalsIgnoreCase(Expression.OC_CONTAINS_IGNORE)) {
						
						throw new ExpressionException(TEXTS.compOpNotConsistentWithPartialExpNature, positionInText);
					}
				}
				else if (this.getNature() == Expression.NATURE_LOGICAL) {
	
					throw new ExpressionException(TEXTS.logicalExpsMustBeSparatedWithLogicalOp, positionInText);
				}
				
				if (comparativeOperatorText.equalsIgnoreCase(Expression.OC_STARTS) || 
					comparativeOperatorText.equalsIgnoreCase(Expression.OC_ENDS) || 
					comparativeOperatorText.equalsIgnoreCase(Expression.OC_CONTAINS) ||
					comparativeOperatorText.equalsIgnoreCase(Expression.OC_STARTS_IGNORE) || 
					comparativeOperatorText.equalsIgnoreCase(Expression.OC_ENDS_IGNORE) || 
					comparativeOperatorText.equalsIgnoreCase(Expression.OC_CONTAINS_IGNORE)) {
					
					this.setNature(Expression.NATURE_ALPHANUMERIC, comparativeOperatorElement);
				}
			}
		}
		
		this.comparativeOperator = comparativeOperatorElement;
	}
	
	private void setParent(Expression parent) {
		this.parent = parent;
	}
	protected void setStackElements(Vector<StackElement> stackElements) {
		this.stackElements = stackElements;
	}
	private void setForbidLogicalType(boolean forbidLogicalType) {
		this.forbidLogicalType = forbidLogicalType;
	}
	
	private boolean hasNumericArithmeticOperators() {
		
		return this.indexOfStackElement(Expression.OA_SUB) != -1
			   ||this.indexOfStackElement(Expression.OA_MUL) != -1
			   ||this.indexOfStackElement(Expression.OA_DIV) != -1;
	}
}
