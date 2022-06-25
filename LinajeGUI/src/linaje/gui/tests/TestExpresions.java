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
package linaje.gui.tests;


import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import linaje.LocalizedStrings;
import linaje.expressions.Expression;
import linaje.expressions.Variable;
import linaje.gui.components.LabelCombo;
import linaje.gui.expressions.DlgVariablesDefault;
import linaje.gui.expressions.ExpressionsPanel;
import linaje.gui.expressions.TextFieldExpressions;
import linaje.gui.AppGUI;
import linaje.gui.LLabel;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.AuxDescriptionPanel;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.HeaderPanel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class TestExpresions extends LDialogContent implements ItemListener, PropertyChangeListener {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String stackElements;
		public String forceLogical;
		public String title;
		public String desc;
		public String error;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private JPanel panelCenter = null;
	private JPanel panelNorthStack = null;
	private JPanel panelStackElements = null;
	private JScrollPane scrollPane = null;
	private LLabel labelDescStack = null;
	private JTextArea textArea = null;
	private ExpressionsPanel expressionsPanel = null;
	private LabelCombo<String> lblComboForceLogical = null;
	
	public TestExpresions() {
		super();
		initialize();
	}
	
	private LLabel getLabelDescStack() {
		if (labelDescStack == null) {
			labelDescStack = new LLabel();
			labelDescStack.setText(TEXTS.stackElements);
			labelDescStack.setFontStyle(1);
			labelDescStack.setVerticalAlignment(SwingConstants.BOTTOM);
		}
		return labelDescStack;
	}
	
	private LabelCombo<String> getLblComboForceLogical() {
		if (lblComboForceLogical == null) {
			lblComboForceLogical = new LabelCombo<>();
			lblComboForceLogical.setName("lblComboForceLogical");
			//lblComboForceLogical.setPreferredSize(new Dimension(170, 20));
			lblComboForceLogical.setTextLabel(TEXTS.forceLogical);
			lblComboForceLogical.setAutoSizeLabel(true);
		}
		return lblComboForceLogical;
	}
	
	private JPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new JPanel(new BorderLayout());
			panelCenter.setOpaque(false);
			panelCenter.add(getPanelStackElements(), BorderLayout.CENTER);
			panelCenter.add(getExpressionsPanel(), BorderLayout.NORTH);
		}
		return panelCenter;
	}
	
	private ExpressionsPanel getExpressionsPanel() {
		if (expressionsPanel == null) {
			expressionsPanel = new ExpressionsPanel();
		}
		return expressionsPanel;
	}
	
	private JPanel getPanelNorthStack() {
		if (panelNorthStack == null) {
			panelNorthStack = new JPanel(new BorderLayout());
			panelNorthStack.setOpaque(false);
			panelNorthStack.add(getLabelDescStack(), BorderLayout.WEST);
			panelNorthStack.add(getLblComboForceLogical(), BorderLayout.EAST);
		}
		return panelNorthStack;
	}
	
	private JPanel getPanelStackElements() {
		if (panelStackElements == null) {
			panelStackElements = new JPanel(new BorderLayout());
			panelStackElements.setOpaque(false);
			panelStackElements.add(getScrollPane(), BorderLayout.CENTER);
			panelStackElements.add(getPanelNorthStack(), BorderLayout.NORTH);
		}
		return panelStackElements;
	}
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}
	
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
		}
		return textArea;
	}
	
	private void initLDialogContent() {
	
		//Añadimos los paneles habituales
		AuxDescriptionPanel pa = new AuxDescriptionPanel();
		
		HeaderPanel headerPanel = new HeaderPanel();
		headerPanel.setTitle(TEXTS.title);
		headerPanel.setDescription(TEXTS.desc);
		
		ButtonsPanel buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
		
		setHeaderPanel(headerPanel);
		setButtonsPanel(buttonsPanel);
		setAuxDescriptionPanel(pa);
		
		buttonsPanel.setAutoCloseOnAccept(true);
		setMargin(5);
		setTitle(TEXTS.title);
		setResizable(true);
	}
	
	private void initConnections() {
		getExpressionsPanel().getTextFieldExpressions().addPropertyChangeListener(this);
		getLblComboForceLogical().getCombo().addItemListener(this);
	}
	
	private void initialize() {
		
		setName("Expressions");
		
		setLayout(new BorderLayout());
		setSize(800, 600);
		add(getPanelCenter(), BorderLayout.CENTER);
		getLblComboForceLogical().addItem(Constants.YES);
		getLblComboForceLogical().addItem(Constants.NO);
		
		getExpressionsPanel().setDlgVariables(new DlgVariablesDefault(getExpressionsPanel()));
		
		initLDialogContent();
		//getScrollPane().setBorder(BorderFactory.createLoweredBevelBorder());
		
		initConnections();
	}
	
	public void itemStateChanged(ItemEvent e) {
	
		if (e.getStateChange() == ItemEvent.SELECTED) {
	
			if (e.getSource() == getLblComboForceLogical().getCombo()) {
				boolean forzarTipoLogico = getLblComboForceLogical().getCombo().getSelectedIndex() == 0;
				getExpressionsPanel().getTextFieldExpressions().setForceLogicalType(forzarTipoLogico);
				getExpressionsPanel().getTextFieldExpressions().analizeExpression();
			}
		}
	}
	
	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
	public void propertyChange(PropertyChangeEvent e) {
	
		if (e.getSource() == getExpressionsPanel().getTextFieldExpressions()) {
	
			if (e.getPropertyName().equals(TextFieldExpressions.FIELD_expression)) {
	
				getTextArea().setText(Constants.VOID);
				Expression expression = getExpressionsPanel().getTextFieldExpressions().getExpresion();
				String texto = Constants.VOID;
					
				if (expression != null) {
					
					for (int i = 0; i < expression.size(); i++) {
	
						String textoPrevio;
						if (i == 0)
							textoPrevio = texto;
						else
							textoPrevio = texto + "\n";
							
						texto = textoPrevio + expression.elementAt(i);
					}
					texto = texto + "\n----------------------------------------\n";
					try {
	
						Object valor = expression.getValue();
						texto = texto + valor;
						expression.printConsole();
						Console.println(""+valor);
					} 
					catch (Throwable ex) {
						texto = TEXTS.error+":\n" + ex;
						Console.printException(ex);
					}
				}
				getTextArea().setText(texto);
			}
		}
	}
	
	public static void main(String[] args) {
		
		try {
			
			LinajeLookAndFeel.init();
			TestExpresions dlgExpresiones = getTestComponent();
			AppGUI.getCurrentApp().setName(dlgExpresiones.getName());
			dlgExpresiones.showInFrame();
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
	public static TestExpresions getTestComponent() {
		
		TestExpresions dlgExpresiones = new TestExpresions();
		//Agregamos algunas variables
		Vector<Variable> variablesIniciales = new Vector<Variable>();

		Variable varX = new Variable("X", Integer.class, new Integer(25));
		Variable varY = new Variable("Y", String.class, "c");
		Variable varZ = new Variable("Z", Date.class, new GregorianCalendar().getTime());

		varX.getValues().addElement(new Integer(5));
		varX.getValues().addElement(new Integer(10));
		varX.getValues().addElement(new Integer(15));
		varX.getValues().addElement(new Integer(20));
		varX.getValues().addElement(new Integer(25));
		varX.getValues().addElement(new Integer(30));
		varX.getValues().addElement(new Integer(35));
		varX.getValues().addElement(new Integer(40));
		varX.getValues().addElement(new Integer(45));
		varX.getValues().addElement(new Integer(50));

		varY.getValues().addElement("a");
		varY.getValues().addElement("b");
		varY.getValues().addElement("c");
		varY.getValues().addElement("d");
		varY.getValues().addElement("e");
		
		varZ.getValues().addElement(new GregorianCalendar(2007, 11, 31).getTime());
		varZ.getValues().addElement(new GregorianCalendar(2006, 11, 31).getTime());
		varZ.getValues().addElement(new GregorianCalendar(2005, 5, 15).getTime());
		varZ.getValues().addElement(new GregorianCalendar(2004, 7, 10).getTime());
		varZ.getValues().addElement(new GregorianCalendar(2003, 2, 1).getTime());
		varZ.getValues().addElement(new GregorianCalendar().getTime());

		variablesIniciales.addElement(varX);
		variablesIniciales.addElement(varY);
		variablesIniciales.addElement(varZ);

		//Ponemos una expresión de ejemplo
		String expresionInicial = "(2 + 2) / (4-8) > 4 + 5 AND 'hola' = 23 OR NOT(45<89-56)";
		
		dlgExpresiones.getExpressionsPanel().initExpressionsPanel(expresionInicial, variablesIniciales);		
		
		return dlgExpresiones;
	}
}
