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
package linaje.gui.expressions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledEditorKit;

import linaje.expressions.ExpressionsAnalyzer;
import linaje.expressions.StackElement;
import linaje.expressions.Expression;
import linaje.LocalizedStrings;
import linaje.expressions.ExpressionException;
import linaje.expressions.Variable;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.GraphicsUtils;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;

@SuppressWarnings("serial")
public class TextFieldExpressions extends JTextPane implements KeyListener {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String correctExp;
			
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final String FIELD_errorDescription = "errorDescription";
	public static final String FIELD_expression = "expression";
	
	private String errorDescription = null;
	private Expression expression = null;
	private Vector<Variable> variables = null;
	
	private int errorPosition = -1;

	private boolean characterWritten = false;
	private boolean forceLogicalType = true;
	
	public TextFieldExpressions() {
		super();
		initialize();
	}
	
	public void analizeExpression() {
	
		String descError = getText().trim().equals(Constants.VOID) ? Constants.VOID : TEXTS.correctExp;
		
		int errorPosition = -1;
		
		try {
	
			Expression expression = ExpressionsAnalyzer.parseExpression(getText(), getVariables(), getForceLogicalType());
			setExpression(expression);
		} 
		catch (ExpressionException ex) {
			
			setExpression(null);
			descError = ex.getErrorDescription();
			errorPosition = ex.getErrorPosition();
		}
	
		setErrorDescription(descError);
		setErrorPosition(errorPosition);
	
		paintColorText();
	}
	
	public void changeFontColor(Color fontColor) {
	
		if (!getStyledDocument().getForeground(getInputAttributes()).equals(fontColor)) {
			String rgbText = String.valueOf(fontColor.getRGB());
			Action action = new StyledEditorKit.ForegroundAction(rgbText, fontColor);
			action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, rgbText));
		}
	}
	
	public void changeUnderline() {
	
		Action action = new StyledEditorKit.UnderlineAction();
		String actionName = "Underline";
		action.putValue(Action.NAME, actionName);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionName));
	}
	
	public int getWidthText(String text) {
	
		if (text != null && getFont() != null)
			return SwingUtilities.computeStringWidth(getFontMetrics(getFont()), text);
		else
			return 0;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	public Expression getExpresion() {
		return expression;
	}
	public boolean getForceLogicalType() {
		return forceLogicalType;
	}
	public int getErrorPosition() {
		return errorPosition;
	}
	public Vector<Variable> getVariables() {
		return variables;
	}
	public boolean isForceLogicalType() {
		return forceLogicalType;
	}
	
	private void initConnections() {
		this.addKeyListener(this);
	}
	private void initialize() {
		initConnections();
		//No dejamos poner un color oscuro de fondo, ya que los colores del texto son para fondo claro
		//if (Colors.isColorDark(getBackground()))
		//	setBackground(Colors.brighter(getBackground(), 0.5));
	}
	
	
	public void keyPressed(KeyEvent e) {
	
		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			
			e.consume();
			transferFocus();
		}
	}
	
	public void keyReleased(KeyEvent e) {
	
		if (characterWritten || e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			analizeExpression();
		}
		characterWritten = false;
	}
	
	public void keyTyped(KeyEvent e) {
		characterWritten = true;
	}
	
	
	public void paintComponent(Graphics g) {
	
		super.paintComponent(g);
		paintErrorPosition(g);
	}
	
	private void paintColorText() {
	
		int caretPosition = getCaretPosition();
			
		try {
	
			Vector<StackElement> stackElements;
			if (getExpresion() == null)
				stackElements = ExpressionsAnalyzer.createStackElements(getText(), false);
			else
				stackElements = getExpresion().getStackElements();
	
			StackElement stackElement;
			String elementText;
			int selectStart, selectEnd;
			Color color;
			for (int i = 0; i < stackElements.size(); i++) {
	
				stackElement = stackElements.elementAt(i);
	
				if (getExpresion() == null) {
					
					try {
					 	stackElement.initElement(getVariables());
					} catch (Throwable ex) {
					}	
				}
				
				elementText = stackElement.toString();
				if (Colors.isColorDark(getBackground()))
					color = stackElement.getNature() == Expression.NATURE_NUMERICAL ? GeneralUIProperties.getInstance().getColorText() : Colors.optimizeColor(stackElement.getColor(), getBackground());
				else
					color = stackElement.getColor();
				
				selectStart = stackElement.getPositionInText();
				selectEnd = selectStart + elementText.length();
	
				select(selectStart, selectEnd);
				
				changeFontColor(color);
			}
			select(caretPosition, caretPosition);
			setCaretPosition(caretPosition);
		} 
		catch (Throwable ex) {
			Console.printException(ex);
			select(caretPosition, caretPosition);
			setCaretPosition(caretPosition);
		}
	}
	
	private void paintErrorPosition(Graphics g) {
	
		if (errorPosition != -1 && getText().length() > errorPosition) {
			try {
	
				String textToError = getText().substring(0, errorPosition);
				int widthTextField = getWidth() - getInsets().left - getInsets().right;
				int x = getWidthText(textToError) + getInsets().left;
				int y = getHeight() + 3;
	
				while (widthTextField > 0 && x > widthTextField) {
	
					x = x - widthTextField;
				}
				
				g.setColor(GeneralUIProperties.getInstance().getColorNegative());
				GraphicsUtils.drawString(g, "^", x, y);
					
			} catch (Throwable ex) {
				Console.printException(ex);
			}
		}
	}
	
	private void setErrorDescription(String newErrorDescription) {
		if (this.errorDescription != newErrorDescription) {
			String oldValue = this.errorDescription;
			this.errorDescription = newErrorDescription;
			firePropertyChange(FIELD_errorDescription, oldValue, newErrorDescription);
		}
	}
	
	private void setExpression(Expression newExpression) {
		if (this.expression != newExpression) {
			Vector<Object> oldValue = this.expression;
			this.expression = newExpression;
			firePropertyChange(FIELD_expression, oldValue, newExpression);
		}
	}
	
	public void setForceLogicalType(boolean forceLogicalType) {
		this.forceLogicalType = forceLogicalType;
	}
	private void setErrorPosition(int errorPosition) {
		this.errorPosition = errorPosition;
	}
	
	public void setText(String newText) {
	
		if (newText == null)
			newText = Constants.VOID;
	
		if (!newText.equalsIgnoreCase(getText())) {
			super.setText(newText);
			setCaretPosition(newText.length());
			analizeExpression();
		}
	}
	
	public void setVariables(Vector<Variable> newVariables) {
		this.variables = newVariables;
		analizeExpression();
	}
}
