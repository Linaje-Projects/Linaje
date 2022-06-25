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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;

import linaje.LocalizedStrings;
import linaje.expressions.Expression;
import linaje.expressions.ExpressionsAnalyzer;
import linaje.expressions.StackElement;
import linaje.expressions.Variable;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LLabel;
import linaje.gui.LList;
import linaje.gui.LPanel;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;

@SuppressWarnings("serial")
public class ExpressionsPanel extends LPanel implements ActionListener, PropertyChangeListener, ListSelectionListener {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String basicOperators;
		public String advancedOperators;
		public String variables;
		public String functions;
		
		public String sum;
		public String substract;
		public String multiply;
		public String divide;
		
		public String greater;
		public String less;
		public String equal;
		public String distinct;
		public String starts;
		public String ends;
		public String contains;
		
		public String parenthesisOpen;
		public String parenthesisClose;
		public String quote;
		public String del;
		
		public String and;
		public String or;
		public String not;
		
		public String greaterIgnore;
		public String lessIgnore;
		public String equalIgnore;
		public String distinctIgnore;
		public String startsIgnore;
		public String endsIgnore;
		public String containsIgnore;
		
		public String delExp;
		
		public String addVarToExp;
		public String addVarToList;
		public String removeVariable;
		public String addFuncToExp;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private JPanel panelNorth = null;
	private JPanel panelOperators = null;
	private JPanel panelBasicOperators = null;
	private JPanel panelAdvancedOperators = null;
	private JPanel panelCenter = null;
	private JPanel panelVariables = null;
	private JPanel panelFunctions = null;
	private JPanel panelDescFunctions = null;
	
	private LButton btnSum = null;
	private LButton btnSubstract = null;
	private LButton btnMultiply = null;
	private LButton btnDivide = null;
	
	private LButton btnGreater = null;
	private LButton btnLess = null;
	private LButton btnEqual = null;
	private LButton btnDistinct = null;
	private LButton btnStarts = null;
	private LButton btnEnds = null;
	private LButton btnContains = null;
	
	private LButton btnParenthesisOpen = null;
	private LButton btnParenthesisClose = null;
	private LButton btnQuote = null;
	private LButton btnDel = null;
	
	private LButton btnAnd = null;
	private LButton btnOr = null;
	private LButton btnNot = null;
	
	private LButton btnGreaterIgnore = null;
	private LButton btnLessIgnore = null;
	private LButton btnEqualIgnore = null;
	private LButton btnDistinctIgnore = null;
	private LButton btnStartsIgnore = null;
	private LButton btnEndsIgnore = null;
	private LButton btnContainsIgnore = null;
	
	private LButton btnDelExp = null;
	
	private LButton btnAddVarToExp = null;
	private LButton btnAddVarToList = null;
	private LButton btnRemoveVariable = null;
	private LButton btnAddFuncToExp = null;
	
	private LLabel labelDescVariables = null;
	private LLabel labelError = null;
	private LLabel labelDescSelectedFunction = null;
	private LLabel labelDescFunctions = null;
	
	private JScrollPane scrollPaneListVariables = null;
	private JScrollPane scrollPaneTextField = null;
	private JScrollPane scrollPaneListFunctions = null;
	
	private LList<String> listFunctions = null;
	private LList<Variable> listVariables = null;
	
	private TextFieldExpressions textFieldExpressions = null;
	private DlgVariablesGeneric dlgVariables = null;
	
	private int maxVisibleLines = 2;
	
	public ExpressionsPanel() {
		super();
		initialize();
	}
	
	public void actionPerformed(ActionEvent e) {
	
		if (e.getSource() == getBtnAddVarToList()) {
	
			addVariableToExp();
		}
		else if (e.getSource() == getBtnRemoveVariable()) {
	
			removeSlectedVariable();
		}
		else if (e.getSource() == getBtnDelExp()) {
	
			getTextFieldExpressions().setText(Constants.VOID);
		}
		else if (e.getSource() == getBtnDel()) {
	
			delete();
		}
		else {
			
			int caretPosition = deleteSelectedText();
			AttributeSet attributeSet = getTextFieldExpressions().getInputAttributes();
			String textToInsert = null;
			boolean isFunction = false;
			
			//Variable
			if (e.getSource() == getBtnAddVarToExp()) {
				
				if (getSelectedVariable() != null)
					textToInsert = getSelectedVariable().getName();
			}
			//Función
			else if (e.getSource() == getBtnAddFuncToExp()) {
	
				isFunction = true;
				if (getListFunctions().getSelectedValue() != null)
					textToInsert = getListFunctions().getSelectedValue() + "()";
			}
			//Operadores Aritméticos
			else
				textToInsert = ((JButton) e.getSource()).getText();
			
			try {
	
				if (textToInsert != null) {
	
					Document document = getTextFieldExpressions().getDocument();
					int cursorFunc = 1;
					if (!textToInsert.equals("'")
					 && !textToInsert.equals("(")
					 && !textToInsert.equals(")")) {
	
						String textNext = null;
						String textPrevious = null;
						if (caretPosition < document.getLength())
							textNext = document.getText(caretPosition, 1);
						if (caretPosition > 0)
							textPrevious = document.getText(caretPosition -1, 1);
						
						boolean insertBlankTrailing =  textNext != null
													&& !textNext.equals(" ")
													&& !textNext.equals("'")
													&& !textNext.equals("(")
													&& !textNext.equals(")");
	
						boolean insertBlankLeading = textPrevious != null
													&& !textPrevious.equals(" ")
													&& !textPrevious.equals("'")
													&& !textPrevious.equals("(")
													&& !textPrevious.equals(")");
	
						if (insertBlankTrailing) {
							textToInsert = textToInsert + Constants.SPACE;
							cursorFunc = 2;
						}
						if (insertBlankLeading)
							textToInsert = Constants.SPACE + textToInsert;
					}
					
					getTextFieldExpressions().getStyledDocument().insertString(caretPosition, textToInsert, attributeSet);
					getTextFieldExpressions().analizeExpression();
					if (isFunction)
						getTextFieldExpressions().setCaretPosition(caretPosition + textToInsert.length() - cursorFunc);
					getTextFieldExpressions().requestFocus();
				}
				
			} catch (Throwable ex) {
				Console.printException(ex);
			}
		}
	}
	
	private void addVariableToExp() {
	
		getDlgVariables().showInDialog();
		if (getDlgVariables().getButtonsPanel().getResponse() == ButtonsPanel.RESPONSE_ACCEPT_YES) {
			Variable selectedVariable = getDlgVariables().getSelectedVariable();	
			if (selectedVariable != null)
				addVariableToExp(selectedVariable);
		}
	}
	
	public void addVariableToExp(Variable variable) {
		
		if (variable != null) {		
			getListVariables().addElement(variable);
			getTextFieldExpressions().setVariables(getVariables());
		}
	}
	
	private void delete() {
	
		try {
	
			int caretPosition = deleteSelectedText();
			int textLength = getTextFieldExpressions().getText().length();
	
			if (textLength > 0) {
				
				if (caretPosition < textLength)
					getTextFieldExpressions().getDocument().remove(caretPosition, 1);
				else
					getTextFieldExpressions().getDocument().remove(caretPosition - 1, 1);
	
				getTextFieldExpressions().analizeExpression();
				getTextFieldExpressions().requestFocus();
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private int deleteSelectedText() {
	
		try {
	
			int selectionStart = getTextFieldExpressions().getSelectionStart();
			int selectionEnd = getTextFieldExpressions().getSelectionEnd();
			int lesserPosition = selectionStart < selectionEnd ? selectionStart : selectionEnd;
		
			if (selectionStart != selectionEnd)
				getTextFieldExpressions().getStyledDocument().remove(lesserPosition, getTextFieldExpressions().getSelectedText().length());
	
			return lesserPosition;		
		}
		catch (Throwable ex) {
			Console.printException(ex);
			return getTextFieldExpressions().getCaretPosition();
		}
	}
	
	public void disableAddRemoveVariables() {
	
		getBtnAddVarToList().setEnabled(false);
		getBtnRemoveVariable().setEnabled(false);
		getBtnAddVarToExp().setEnabled(true);
		
		getBtnAddVarToList().removeActionListener(this);
		getBtnRemoveVariable().removeActionListener(this);
	
		getListVariables().getSelectionModel().removeListSelectionListener(this);
	}

	private void resizeScrollPaneTextField() {
	
		Insets borderInsets = getTextFieldExpressions().getBorder().getBorderInsets(getTextFieldExpressions());
		Insets margin = getTextFieldExpressions().getMargin();
		int lineSize = Math.round(getTextFieldExpressions().getFont().getSize()*1.333f);
		
		final int width = getScrollPaneTextField().getPreferredSize().width;
		final int height = (lineSize * getMaxVisibleLines()) + borderInsets.top + borderInsets.bottom + margin.top + margin.bottom;
		
		getScrollPaneTextField().setPreferredSize(new Dimension(width, height));
		getScrollPaneTextField().getVerticalScrollBar().setUnitIncrement(lineSize);
	}
	
	public void removeVariable(Variable variable) {
	
		if (getListVariables().getDefaultListModel().indexOf(variable) != -1) {
	
			getListVariables().getDefaultListModel().removeElement(variable);
			getTextFieldExpressions().setVariables(getVariables());
	
			if (getListVariables().getElements().size() == 0) {
				getBtnRemoveVariable().setEnabled(false);
				getBtnAddVarToExp().setEnabled(false);
			}
		}
	}
	
	public void removeVariable(String variableName) {
	
		Variable variable = Variable.searchVariable(variableName, getVariables());
		if (variable != null) {
	
			removeVariable(variable);
		}
	}
	
	private void removeSlectedVariable() {
	
		if (getListVariables().getSelectedIndex() != -1) {
				
			getListVariables().removeSelectedElement();
			getTextFieldExpressions().setVariables(getVariables());
	
			if (getListVariables().getElements().size() == 0) {
				getBtnRemoveVariable().setEnabled(false);
				getBtnAddVarToExp().setEnabled(false);
			}
		}
	}
	
	private LButton getBtnAddVarToExp() {
		if (btnAddVarToExp == null) {
			btnAddVarToExp = new LButton(Constants.VOID);
			btnAddVarToExp.setToolTipText(TEXTS.addVarToExp);
			btnAddVarToExp.setIcon(Icons.ARROW_UP);
		}
		return btnAddVarToExp;
	}
	
	private LButton getBtnAddVarToList() {
		if (btnAddVarToList == null) {
			btnAddVarToList = new LButton(Constants.VOID);
			btnAddVarToList.setToolTipText(TEXTS.addVarToList);
			btnAddVarToList.setIcon(Icons.ARROW_LEFT);
		}
		return btnAddVarToList;
	}
	
	public LButton getBtnRemoveVariable() {
		if (btnRemoveVariable == null) {
			btnRemoveVariable = new LButton(Constants.VOID);
			btnRemoveVariable.setToolTipText(TEXTS.removeVariable);
			btnRemoveVariable.setIcon(Icons.getIconX(getFont().getSize(), 2, Color.black));
			btnRemoveVariable.setPreferredSize(getBtnAddVarToList().getPreferredSize());
			//btnRemoveVariable.setForeground(Colors.darker(btnRemoveVariable.getForeground(), 0.5f));
		}
		return btnRemoveVariable;
	}
	
	private LButton getBtnAddFuncToExp() {
		if (btnAddFuncToExp == null) {
			btnAddFuncToExp = new LButton(Constants.VOID);
			btnAddFuncToExp.setToolTipText(TEXTS.addFuncToExp);
			btnAddFuncToExp.setIcon(Icons.ARROW_UP);
		}
		return btnAddFuncToExp;
	}
	
	private LButton getBtnSum() {
		if (btnSum == null) {
			btnSum = new LButton(Expression.OA_SUM);
			btnSum.setToolTipText(TEXTS.sum);
		}
		return btnSum;
	}
	
	private LButton getBtnSubstract() {
		if (btnSubstract == null) {
			btnSubstract = new LButton(Expression.OA_SUB);
			btnSubstract.setToolTipText(TEXTS.substract);
		}
		return btnSubstract;
	}
	
	private LButton getBtnMultiply() {
		if (btnMultiply == null) {
			btnMultiply = new LButton(Expression.OA_MUL);
			btnMultiply.setToolTipText(TEXTS.multiply);
			btnMultiply.setVerticalAlignment(SwingConstants.TOP);
		}
		return btnMultiply;
	}
	
	private LButton getBtnDivide() {
		if (btnDivide == null) {
			btnDivide = new LButton(Expression.OA_DIV);
			btnDivide.setToolTipText(TEXTS.divide);
		}
		return btnDivide;
	}
	
	private LButton getBtnGreater() {
		if (btnGreater == null) {
			btnGreater = new LButton(Expression.OC_GREATER);
			btnGreater.setToolTipText(TEXTS.greater);
		}
		return btnGreater;
	}
	
	private LButton getBtnLess() {
		if (btnLess == null) {
			btnLess = new LButton(Expression.OC_LESS);
			btnLess.setToolTipText(TEXTS.less);
		}
		return btnLess;
	}
	
	private LButton getBtnEqual() {
		if (btnEqual == null) {
			btnEqual = new LButton(Expression.OC_EQUAL);
			btnEqual.setToolTipText(TEXTS.equal);
		}
		return btnEqual;
	}
	
	private LButton getBtnDistinct() {
		if (btnDistinct == null) {
			btnDistinct = new LButton(Expression.OC_DISTINCT);
			btnDistinct.setToolTipText(TEXTS.distinct);
		}
		return btnDistinct;
	}
	
	private LButton getBtnStarts() {
		if (btnStarts == null) {
			btnStarts = new LButton(Expression.OC_STARTS);
			btnStarts.setToolTipText(TEXTS.starts);
		}
		return btnStarts;
	}
	
	private LButton getBtnEnds() {
		if (btnEnds == null) {
			btnEnds = new LButton(Expression.OC_ENDS);
			btnEnds.setToolTipText(TEXTS.ends);
		}
		return btnEnds;
	}
	
	private LButton getBtnContains() {
		if (btnContains == null) {
			btnContains = new LButton(Expression.OC_CONTAINS);
			btnContains.setToolTipText(TEXTS.contains);
		}
		return btnContains;
	}
	
	private LButton getBtnAnd() {
		if (btnAnd == null) {
			btnAnd = new LButton(Expression.OL_AND);
			btnAnd.setToolTipText(TEXTS.and);
		}
		return btnAnd;
	}
	
	private LButton getBtnOr() {
		if (btnOr == null) {
			btnOr = new LButton(Expression.OL_OR);
			btnOr.setToolTipText(TEXTS.or);
		}
		return btnOr;
	}
	
	private LButton getBtnNot() {
		if (btnNot == null) {
			btnNot = new LButton(Expression.OL_NOT);
			btnNot.setToolTipText(TEXTS.not);
		}
		return btnNot;
	}
	
	private LButton getBtnParenthesisOpen() {
		if (btnParenthesisOpen == null) {
			btnParenthesisOpen = new LButton(Expression.OS_OPEN);
			btnParenthesisOpen.setToolTipText(TEXTS.parenthesisOpen);
		}
		return btnParenthesisOpen;
	}
	
	private LButton getBtnParenthesisClose() {
		if (btnParenthesisClose == null) {
			btnParenthesisClose = new LButton(Expression.OS_CLOSE);
			btnParenthesisClose.setToolTipText(TEXTS.parenthesisClose);
		}
		return btnParenthesisClose;
	}
	
	private LButton getBtnQuote() {
		if (btnQuote == null) {
			btnQuote = new LButton("\'");
			btnQuote.setToolTipText(TEXTS.quote);
		}
		return btnQuote;
	}
	
	private LButton getBtnDel() {
		if (btnDel == null) {
			btnDel = new LButton("C");
			btnDel.setToolTipText(TEXTS.del);
		}
		return btnDel;
	}
	
	private LButton getBtnGreaterIgnore() {
		if (btnGreaterIgnore == null) {
			btnGreaterIgnore = new LButton(Expression.OC_GREATER_IGNORE);
			btnGreaterIgnore.setToolTipText(TEXTS.greaterIgnore);
		}
		return btnGreaterIgnore;
	}
	
	private LButton getBtnLessIgnore() {
		if (btnLessIgnore == null) {
			btnLessIgnore = new LButton(Expression.OC_LESS_IGNORE);
			btnLessIgnore.setToolTipText(TEXTS.lessIgnore);
		}
		return btnLessIgnore;
	}
	
	private LButton getBtnDistinctIgnore() {
		if (btnDistinctIgnore == null) {
			btnDistinctIgnore = new LButton(Expression.OC_DISTINCT_IGNORE);
			btnDistinctIgnore.setToolTipText(TEXTS.distinctIgnore);
		}
		return btnDistinctIgnore;
	}
	
	private LButton getBtnEqualIgnore() {
		if (btnEqualIgnore == null) {
			btnEqualIgnore = new LButton(Expression.OC_EQUAL_IGNORE);
			btnEqualIgnore.setToolTipText(TEXTS.equalIgnore);
		}
		return btnEqualIgnore;
	}
	
	private LButton getBtnStartsIgnore() {
		if (btnStartsIgnore == null) {
			btnStartsIgnore = new LButton(Expression.OC_STARTS_IGNORE);
			btnStartsIgnore.setToolTipText(TEXTS.startsIgnore);
		}
		return btnStartsIgnore;
	}
	
	private LButton getBtnEndsIgnore() {
		if (btnEndsIgnore == null) {
			btnEndsIgnore = new LButton(Expression.OC_ENDS_IGNORE);
			btnEndsIgnore.setToolTipText(TEXTS.endsIgnore);
		}
		return btnEndsIgnore;
	}
	
	private LButton getBtnContainsIgnore() {
		if (btnContainsIgnore == null) {
			btnContainsIgnore = new LButton(Expression.OC_CONTAINS_IGNORE);
			btnContainsIgnore.setToolTipText(TEXTS.containsIgnore);
		}
		return btnContainsIgnore;
	}
	
	private LButton getBtnDelExp() {
		if (btnDelExp == null) {
			btnDelExp = new LButton("DEL");
			btnDelExp.setToolTipText(TEXTS.delExp);
			btnDelExp.setMargin(new Insets(0, 15, 0, 15));
		}
		return btnDelExp;
	}
	
	public DlgVariablesGeneric getDlgVariables() {
		
		if (dlgVariables == null)
			dlgVariables = new DlgVariablesDefault(this);
		
		return dlgVariables;
	}
	
	private LLabel getLabelDescFunctions() {
		if (labelDescFunctions == null) {
			labelDescFunctions = new LLabel(TEXTS.functions);
			labelDescFunctions.setFontStyle(Font.BOLD);
			labelDescFunctions.setMargin(new Insets(0, 0, 0, 0));
			labelDescFunctions.setVerticalAlignment(SwingConstants.BOTTOM);
		}
		return labelDescFunctions;
	}
	
	private LLabel getLabelDescSelectedFunction() {
		if (labelDescSelectedFunction == null) {
			labelDescSelectedFunction = new LLabel(Constants.SPACE);
			labelDescSelectedFunction.setForeground(GeneralUIProperties.getInstance().getColorInfo());
			labelDescSelectedFunction.setMargin(new Insets(0, 0, 0, 0));
			labelDescSelectedFunction.setVerticalAlignment(SwingConstants.BOTTOM);
		}
		return labelDescSelectedFunction;
	}
	
	private LLabel getLabelDescVariables() {
		if (labelDescVariables == null) {
			labelDescVariables = new LLabel(TEXTS.variables);
			labelDescVariables.setFontStyle(Font.BOLD);
			labelDescVariables.setMargin(new Insets(0, 0, 0, 0));
			labelDescVariables.setVerticalAlignment(SwingConstants.BOTTOM);
		}
		return labelDescVariables;
	}
	
	private LLabel getLabelError() {
		if (labelError == null) {
			labelError = new LLabel();
			labelError.setMargin(new Insets(0,0,0,0));
			labelError.setVerticalAlignment(SwingConstants.TOP);
		}
		return labelError;
	}
	
	private LList<String> getListFunctions() {
		if (listFunctions == null) {
			listFunctions = new LList<>();
			listFunctions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return listFunctions;
	}
	
	public LList<Variable> getListVariables() {
		if (listVariables == null) {
			listVariables = new LList<>();
		}
		return listVariables;
	}
	
	private int getMaxVisibleLines() {
		return maxVisibleLines;
	}
	
	private JPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new JPanel(new GridBagLayout());
			panelCenter.setOpaque(false);
			
			//Variables
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets.left = 10;
			panelCenter.add(getPanelVariables(), gbc);
			
			//Funciones
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.insets.top = 5;
			panelCenter.add(getPanelFunctions(), gbc);
		}
		return panelCenter;
	}
	
	private JPanel getPanelFunctions() {
		if (panelFunctions == null) {
			panelFunctions = new JPanel(new GridBagLayout());
			panelFunctions.setOpaque(false);
			panelFunctions.setPreferredSize(getPanelVariables().getPreferredSize());
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panelFunctions.add(getPanelDescFunctions(), gbc);
			
			gbc.gridy = 2;
			gbc.gridwidth = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			
			panelFunctions.add(getScrollPaneListFunctions(), gbc);
			
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.insets.left = 2;
			panelFunctions.add(getBtnAddFuncToExp(), gbc);
		}
		return panelFunctions;
	}
	
	private JPanel getPanelNorth() {
		if (panelNorth == null) {
			panelNorth = new JPanel(new GridBagLayout());
			panelNorth.setOpaque(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			panelNorth.add(getScrollPaneTextField(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTH;
			panelNorth.add(getLabelError(), gbc);
			
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.NORTHEAST;
			panelNorth.add(getBtnDelExp(), gbc);
		}
		return panelNorth;
	}
	
	private JPanel getPanelDescFunctions() {
		if (panelDescFunctions == null) {
			panelDescFunctions = new JPanel(new GridBagLayout());
			panelDescFunctions.setOpaque(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			panelDescFunctions.add(getLabelDescFunctions(), gbc);
			
			gbc.gridx = 2;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets.left = 3;
			panelDescFunctions.add(getLabelDescSelectedFunction(), gbc);
		}
		return panelDescFunctions;
	}
	
	private JPanel getPanelOperators() {
		if (panelOperators == null) {
			panelOperators = new JPanel(new GridBagLayout());
			panelOperators.setOpaque(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			panelOperators.add(getPanelBasicOperators(), gbc);
			
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(3, 0, 0, 0);
			panelOperators.add(getPanelAdvancedOperators(), gbc);
		}
		return panelOperators;
	}
	
	private JPanel getPanelAdvancedOperators() {
		if (panelAdvancedOperators == null) {
			
			panelAdvancedOperators = new JPanel(new GridBagLayout());
			panelAdvancedOperators.setOpaque(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(3, 0, 0, 3);
			panelAdvancedOperators.add(getBtnGreaterIgnore(), gbc);
	
			gbc.gridx = 1;
			gbc.insets = new Insets(3, 3, 0, 3);
			panelAdvancedOperators.add(getBtnLessIgnore(), gbc);
	
			gbc.gridx = 2;
			panelAdvancedOperators.add(getBtnEqualIgnore(), gbc);
	
			gbc.gridx = 3;
			panelAdvancedOperators.add(getBtnDistinctIgnore(), gbc);
	
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(3, 0, 0, 3);
			panelAdvancedOperators.add(getBtnStartsIgnore(), gbc);
	
			gbc.gridx = 1;
			gbc.insets = new Insets(3, 3, 0, 3);
			panelAdvancedOperators.add(getBtnEndsIgnore(), gbc);
	
			gbc.gridx = 2;
			panelAdvancedOperators.add(getBtnContainsIgnore(), gbc);
		}
		return panelAdvancedOperators;
	}
	
	private JPanel getPanelBasicOperators() {
		if (panelBasicOperators == null) {
			
			panelBasicOperators = new JPanel(new GridBagLayout());
			panelBasicOperators.setOpaque(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(3, 0, 0, 3);
			panelBasicOperators.add(getBtnSum(), gbc);
	
			gbc.gridx = 1;
			gbc.insets = new Insets(3, 3, 0, 3);
			panelBasicOperators.add(getBtnSubstract(), gbc);
	
			gbc.gridx = 2;
			panelBasicOperators.add(getBtnMultiply(), gbc);
	
			gbc.gridx = 3;
			panelBasicOperators.add(getBtnDivide(), gbc);
	
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(3, 0, 0, 3);
			panelBasicOperators.add(getBtnGreater(), gbc);
	
			gbc.gridx = 1;
			gbc.insets = new Insets(3, 3, 0, 3);
			panelBasicOperators.add(getBtnLess(), gbc);
	
			gbc.gridx = 2;
			panelBasicOperators.add(getBtnEqual(), gbc);
	
			gbc.gridx = 3;
			panelBasicOperators.add(getBtnDistinct(), gbc);
	
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.insets = new Insets(3, 0, 0, 3);
			panelBasicOperators.add(getBtnStarts(), gbc);
	
			gbc.gridx = 1;
			gbc.insets = new Insets(3, 3, 0, 3);
			panelBasicOperators.add(getBtnEnds(), gbc);
	
			gbc.gridx = 2;
			panelBasicOperators.add(getBtnContains(), gbc);
	
			gbc.gridx = 4;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(3, 10, 0, 3);
			panelBasicOperators.add(getBtnAnd(), gbc);
	
			gbc.gridy = 1;
			panelBasicOperators.add(getBtnOr(), gbc);
	
			gbc.gridy = 2;
			panelBasicOperators.add(getBtnNot(), gbc);
	
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(3, 0, 0, 3);
			panelBasicOperators.add(getBtnParenthesisOpen(), gbc);
	
			gbc.gridx = 1;
			gbc.insets = new Insets(3, 3, 0, 3);
			panelBasicOperators.add(getBtnParenthesisClose(), gbc);
	
			gbc.gridx = 2;
			gbc.gridy = 3;
			panelBasicOperators.add(getBtnQuote(), gbc);
	
			gbc.gridx = 3;
			panelBasicOperators.add(getBtnDel(), gbc);
		}
		return panelBasicOperators;
	}
	
	private JPanel getPanelVariables() {
		if (panelVariables == null) {
			panelVariables = new JPanel(new GridBagLayout());
			panelVariables.setOpaque(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			panelVariables.add(getLabelDescVariables(), gbc);
			
			gbc.gridy = 2;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 4;
			gbc.fill = GridBagConstraints.BOTH;
			panelVariables.add(getScrollPaneListVariables(), gbc);
			
			gbc.gridx = 2;
			gbc.gridy = 2;
			gbc.gridheight = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.insets.left = 2;
			panelVariables.add(getBtnAddVarToExp(), gbc);
			
			gbc.gridy = 3;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.VERTICAL;
			JPanel panelAux = new JPanel();
			panelAux.setOpaque(false);
			panelVariables.add(panelAux, gbc);
			
			gbc.gridy = 4;
			gbc.weighty = 0;
			gbc.anchor = GridBagConstraints.SOUTHEAST;
			gbc.fill = GridBagConstraints.NONE;
			panelVariables.add(getBtnAddVarToList(), gbc);
			
			gbc.gridy = 5;
			panelVariables.add(getBtnRemoveVariable(), gbc);
		}
		return panelVariables;
	}
	
	private JScrollPane getScrollPaneListFunctions() {
		if (scrollPaneListFunctions == null) {
			scrollPaneListFunctions = new JScrollPane();
			scrollPaneListFunctions.setViewportView(getListFunctions());
		}
		return scrollPaneListFunctions;
	}
	
	private JScrollPane getScrollPaneListVariables() {
		if (scrollPaneListVariables == null) {
			scrollPaneListVariables = new JScrollPane();
			scrollPaneListVariables.setViewportView(getListVariables());
		}
		return scrollPaneListVariables;
	}
	
	private JScrollPane getScrollPaneTextField() {
		if (scrollPaneTextField == null) {
			scrollPaneTextField = new JScrollPane();
			scrollPaneTextField.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPaneTextField.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPaneTextField.setViewportView(getTextFieldExpressions());
		}
		return scrollPaneTextField;
	}
	
	public TextFieldExpressions getTextFieldExpressions() {
		if (textFieldExpressions == null) {
			textFieldExpressions = new TextFieldExpressions();
		}
		return textFieldExpressions;
	}
	
	public Vector<Variable> getVariables() {
		return getListVariables().getElements();
	}
	
	private Variable getSelectedVariable() {
	
		Object valorSeleccionado = getListVariables().getSelectedValue();
		if (valorSeleccionado != null)
			return (Variable) valorSeleccionado;
		else
			return null;
	}
	
	public void initExpressionsPanel(String textExpression, Vector<Variable> initialVariables) {
		setVariables(initialVariables);
		getTextFieldExpressions().setText(textExpression);
	}
	
	private void initConnections() {
		
		getTextFieldExpressions().addPropertyChangeListener(this);
		getBtnAddVarToList().addActionListener(this);
		getBtnRemoveVariable().addActionListener(this);
		getBtnAddVarToExp().addActionListener(this);
		getBtnAddFuncToExp().addActionListener(this);
		getBtnDelExp().addActionListener(this);
		getListVariables().getSelectionModel().addListSelectionListener(this);
		getListFunctions().getSelectionModel().addListSelectionListener(this);
		
		Component component;
		for (int i = 0; i < getPanelBasicOperators().getComponentCount(); i++) {
	
			component = getPanelBasicOperators().getComponent(i);
			if (component instanceof JButton) {
				JButton b = (JButton) component;
				b.addActionListener(this);
			}
		}
		
		for (int i = 0; i < getPanelAdvancedOperators().getComponentCount(); i++) {
	
			component = getPanelAdvancedOperators().getComponent(i);
			if (component instanceof JButton) {
				JButton b = (JButton) component;
				b.addActionListener(this);
			}
		}	
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		setSize(480, 216);
		add(getPanelNorth(), BorderLayout.NORTH);
		add(getPanelCenter(), BorderLayout.CENTER);
		add(getPanelOperators(), BorderLayout.WEST);
	
		UtilsGUI.setTitledBorder(getPanelBasicOperators(), TEXTS.basicOperators);
		UtilsGUI.setTitledBorder(getPanelAdvancedOperators(), TEXTS.advancedOperators);
	
		getListFunctions().addElement(Expression.OF_MAX);
		getListFunctions().addElement(Expression.OF_MIN);
		getListFunctions().addElement(Expression.OF_SUM);
		getListFunctions().addElement(Expression.OF_AVG);
		getListFunctions().addElement(Expression.OF_MAX2);
		getListFunctions().addElement(Expression.OF_MIN2);
		getListFunctions().addElement(Expression.OF_ABS);
		getListFunctions().clearSelection();
	
		getBtnRemoveVariable().setEnabled(false);
		getBtnAddVarToExp().setEnabled(false);
		getBtnAddFuncToExp().setEnabled(false);
	
		resizeScrollPaneTextField();
		
		assignColorsToButtons(getPanelBasicOperators());
		assignColorsToButtons(getPanelAdvancedOperators());
		
		getBtnQuote().setForeground(Colors.isColorDark(getBtnQuote().getBackground()) ? Colors.optimizeColor(StackElement.COLOR_NATURE_ALPHANUMERIC, getBtnQuote().getBackground())  : StackElement.COLOR_NATURE_ALPHANUMERIC);
		getBtnDel().setForeground(Color.red);
		getBtnDelExp().setForeground(Color.red);
		
		initConnections();
	
		getListVariables().addMouseListener(new MouseAdapter() {
		   
			public void mouseReleased(MouseEvent e) {
	
				if (e.getClickCount() == 2 && getListVariables().getElements().size() > 0)
					getBtnAddVarToExp().doClick();
		    }
		});
		getListFunctions().addMouseListener(new MouseAdapter() {
		   
			public void mouseReleased(MouseEvent e) {
	
				if (e.getClickCount() == 2 && getListFunctions().getElements().size() > 0)
					getBtnAddFuncToExp().doClick();
		    }
		});
	}
	
	private static void assignColorsToButtons(Container container) {
		
		for (int i = 0; i < container.getComponentCount(); i++) {
			Component c = container.getComponent(i);
			if (c instanceof JButton) {
				JButton b = (JButton) c;
				int operatorType = ExpressionsAnalyzer.getOperatorType(b.getText());
				Color foreground = StackElement.getColorOperator(operatorType);
				if (Colors.isColorDark(b.getBackground()))
					foreground = Colors.optimizeColor(foreground, b.getBackground());
				if (foreground != null)
					b.setForeground(foreground);
			}
		}
	}
	
	public void clean() {
		getTextFieldExpressions().setText(Constants.VOID);
		setVariables(null);
		getTextFieldExpressions().analizeExpression();
	}
	
	public void propertyChange(PropertyChangeEvent e) {
	
		if (e.getSource() == getTextFieldExpressions()) {
	
			if (e.getPropertyName().equals(TextFieldExpressions.FIELD_errorDescription)) {
	
				String errorDescription = getTextFieldExpressions().getErrorDescription();
				boolean correctExpression = errorDescription.equals(TextFieldExpressions.TEXTS.correctExp) || errorDescription.equals(Constants.VOID);
				Color foreground = correctExpression ? GeneralUIProperties.getInstance().getColorPositive() : GeneralUIProperties.getInstance().getColorNegative();
				
				getLabelError().setText(errorDescription);
				getLabelError().setForeground(foreground);
	
				getTextFieldExpressions().repaint();
			}
		}
	}
	
	public void setDlgVariables(DlgVariablesGeneric dlgVariables) {
		this.dlgVariables = dlgVariables;
	}
	
	public void setMaxVisibleLines(int maxVisibleLines) {
		
		this.maxVisibleLines = maxVisibleLines;
		resizeScrollPaneTextField();
	}
	
	public void setVariables(Vector<Variable> variables) {
	
		getListVariables().getDefaultListModel().removeAllElements();
	
		if (variables != null) {
	
			for (int i = 0; i < variables.size(); i++) {
	
				Variable variable = variables.elementAt(i);
				if (variable != null)
					getListVariables().addElement(variable);
			}
		}
		
		getTextFieldExpressions().setVariables(getVariables());
	}
	
	public void valueChanged(ListSelectionEvent e) {
	
		if (e.getSource() == getListFunctions().getSelectionModel()) {
	
			if (!e.getValueIsAdjusting()) {
	
				boolean thereAreFunctionSelected = getListFunctions().getSelectedIndices().length != 0;
	
				getBtnAddFuncToExp().setEnabled(thereAreFunctionSelected);
	
				String descFuncion = Constants.SPACE;
				if (thereAreFunctionSelected)				
					descFuncion = ExpressionsAnalyzer.getFunctionDescription(getListFunctions().getSelectedValue().toString());
				
				getLabelDescSelectedFunction().setText(descFuncion);
			}
		}
		else if (e.getSource() == getListVariables().getSelectionModel()) {
	
			if (!e.getValueIsAdjusting()) {
	
				boolean thereAreVariableSelected = getListVariables().getSelectedIndices().length != 0;
	
				getBtnRemoveVariable().setEnabled(thereAreVariableSelected);
				getBtnAddVarToExp().setEnabled(thereAreVariableSelected);
			}
		}
	}
}
