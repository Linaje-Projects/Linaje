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
package linaje.gui.components;

/**
 * <b>Funcionalidad:</b><br>
 * Buscar un texto determinado en un componente de texto(JTextAreas, JTextPanes, etc.)
 * <p>
 * <b>Uso:</b><br>
 * Instanciarlo, pasándole el componente de texto donde se buscará el texto, que
 * por defecto será el "selectedText" del componente
 * <p>
 * 
 * @author Pablo Linaje
 * @version 1.1
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import linaje.LocalizedStrings;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LButtonProperties;
import linaje.gui.LLabel;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.LToggleButton;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
 
@SuppressWarnings("serial")
public class DialogFindText extends LDialogContent {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String dialogTitle;
		public String findText;
		public String findNext;
		public String textNotFound;
		public String backward;
		public String forward;
		public String cyclic;
		public String matchCase;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private JPanel panelSouth = null;
	private JPanel panelNorth = null;
	
	private LButton buttonFindNext = null;
	
	private LToggleButton tglBtnUp = null;
	private LToggleButton tglBtnDown = null;
	private LToggleButton tglBtnCycle = null;
	private LToggleButton tglBtnMatchCase = null;
	
	private LTextField textField = null;
	private LLabel labelMessage = null;
	private JTextComponent textComponent = null;
	
	public DialogFindText() {
		super();
		initialize();
	}

	public static void main(String[] args) {
		LinajeLookAndFeel.init();
		DialogFindText dialogFindText = new DialogFindText();
		dialogFindText.showInDialog();
	}
	
	public DialogFindText(Frame owner, JTextComponent textComponent) {
		super(owner);
		setJTextComponent(textComponent);
		initialize();
	}
	
	private void initialize() {
		
		setTitle(TEXTS.dialogTitle);
		setLayout(new BorderLayout());
		setModal(false);
		setResizable(false);
		setMargin(5);
		
		add(getPanelNorth(), BorderLayout.NORTH);
		add(getPanelSouth(), BorderLayout.SOUTH);
		
		getTglBtnDown().setSelected(true);
				
		setSize(getPreferredSize());
		
		getDialog().addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				//Al hacerse visible el diálogo, se deja de pintar el texto seleccionado, así que lo volvemos a poner visible
				if (getJTextComponent() != null && !getJTextComponent().getCaret().isSelectionVisible())
					getJTextComponent().getCaret().setSelectionVisible(true);
				getTextField().requestFocus();
			}
			public void windowLostFocus(WindowEvent e) {}
		});
		
		setButtonsPanel(new ButtonsPanel());
	}
	
	public void findNext() {
	
		try {
			int fromIndex = getJTextComponent().getCaretPosition();
			if (getJTextComponent().getSelectedText() != null) {
				if (getTglBtnDown().isSelected())
					//Si buscamos hacia abajo la posición inicial será el fin de la selección
					fromIndex = getJTextComponent().getSelectionEnd();
				else
					//Sino la posición inicial será el principio de la selección
					fromIndex = getJTextComponent().getSelectionStart();
			}
			String textToFind = getTextField().getText();
			
			String text;
			if (getJTextComponent() instanceof JEditorPane)
				text = getJTextComponent().getText(0, getJTextComponent().getDocument().getLength());
			else
				text = getJTextComponent().getText();
			
			boolean forward = getTglBtnDown().isSelected();
			int positionTextFound = findText(textToFind, text, fromIndex, forward, getTglBtnMatchCase().isSelected(), getTglBtnCycle().isSelected());
			String message = Constants.VOID;
			if (positionTextFound != fromIndex && positionTextFound != -1) {
				if (!getJTextComponent().getCaret().isSelectionVisible())
					getJTextComponent().getCaret().setSelectionVisible(true);
				getJTextComponent().setCaretPosition(positionTextFound + textToFind.length());
				getJTextComponent().select(positionTextFound, positionTextFound + textToFind.length());
			} 
			else {
				message = TEXTS.textNotFound;
			}
			getLabelMessage().setText(message);
			/*if (message.length() > 0) {
				MessageDialog.showMessage(message, MessageDialog.ICON_WARNING);
			}*/
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * Busca una palabra o frase en un texto desde la posición inicial hacia arriba o hacia abajo
	 * y devuelve la posicion en la que empieza la palabra encontrada.
	 *
	 * Devolverá -1 si no encuentra el texto a buscar.
	 */
	public static int findText(String textToFind, String text, int fromIndex, boolean forward, boolean matchCase, boolean cyclicSearch) {
		int index = -1;
		if (textToFind != null && !textToFind.equals(Constants.VOID) && text != null) {
			try {
				if (forward) {
					String forwardText = text.substring(fromIndex, text.length());
					index = matchCase ? forwardText.indexOf(textToFind) : forwardText.toLowerCase().toLowerCase().indexOf(textToFind.toLowerCase());
					if (index != -1)
						index = index + fromIndex;
					else if (cyclicSearch)
						index = findText(textToFind, text, 0, forward, matchCase, false);
				}
				else {
					String backwardText = text.substring(0, fromIndex);
					index = matchCase ? backwardText.lastIndexOf(textToFind) : backwardText.toLowerCase().lastIndexOf(textToFind.toLowerCase());
					if (cyclicSearch && index == -1)
						index = findText(textToFind, text, text.length(), forward, matchCase, false);
				}
			} catch (Throwable ex) {
				Console.printException(ex);
			}
		}
		return index;
	}
	
	private LButton getButtonFindNext() {
		if (buttonFindNext == null) {
			buttonFindNext = new LButton();
			buttonFindNext.setText(TEXTS.findNext);
			buttonFindNext.setIcon(Icons.SEARCH);
			buttonFindNext.setEnabled(false);
			buttonFindNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findNext();
				}
			});
		}
		return buttonFindNext;
	}
	
	public JTextComponent getJTextComponent() {
		return textComponent;
	}
	
	private JPanel getPanelNorth() {
		if (panelNorth == null) {
			LFlowLayout lflowLayout = new LFlowLayout(FlowLayout.CENTER, 5, 0, true);
			panelNorth = new JPanel(lflowLayout);
			panelNorth.setOpaque(false);
			panelNorth.add(getTextField());
			panelNorth.add(getLabelMessage());
		}
		return panelNorth;
	}
	
	private JPanel getPanelSouth() {
		if (panelSouth == null) {
			LFlowLayout lflowLayout = new LFlowLayout(FlowLayout.CENTER, 5, 0, false);
			panelSouth = new JPanel(lflowLayout);
			panelSouth.setOpaque(false);
			ButtonGroup bg = new ButtonGroup();
			bg.add(getTglBtnUp());
			bg.add(getTglBtnDown());
			
			LPanel panelAux1 = new LPanel(new LFlowLayout(FlowLayout.CENTER, 0, 0, false));
			LPanel panelAux2 = new LPanel(new LFlowLayout(FlowLayout.CENTER, 0, 0, true));
			
			panelAux1.add(getTglBtnUp());
			panelAux1.add(getTglBtnDown());
			
			panelAux2.add(getTglBtnMatchCase());
			panelAux2.add(getTglBtnCycle());
			
			panelSouth.add(panelAux1);
			panelSouth.add(new JLabel());
			panelSouth.add(panelAux2);
			panelSouth.add(new JLabel());
			panelSouth.add(getButtonFindNext());
		}
		return panelSouth;
	}
	
	private LToggleButton getTglBtnUp() {
		if (tglBtnUp == null) {
			tglBtnUp = new LToggleButton(Constants.SPACE);
			int size = tglBtnUp.getPreferredSize().height;
			tglBtnUp.setPreferredSize(new Dimension(size, size));
			tglBtnUp.setText(Constants.VOID);
			tglBtnUp.setIcon(Icons.ARROW_UP);
			tglBtnUp.setToolTipText(TEXTS.backward);
		}
		return tglBtnUp;
	}
	
	private LToggleButton getTglBtnDown() {
		if (tglBtnDown == null) {
			tglBtnDown = new LToggleButton();
			tglBtnDown.setPreferredSize(getTglBtnUp().getPreferredSize());
			tglBtnDown.setIcon(Icons.ARROW_DOWN);
			tglBtnDown.setToolTipText(TEXTS.forward);
		}
		return tglBtnDown;
	}
	
	private LToggleButton getTglBtnCycle() {
		if (tglBtnCycle == null) {
			tglBtnCycle = new LToggleButton();
			tglBtnCycle.setToolTipText(TEXTS.cyclic);
			int w = getTglBtnUp().getPreferredSize().width;
			int h = w/2+1;
			tglBtnCycle.setPreferredSize(new Dimension(w, h));
			int iconSize = h/2+1;
			tglBtnCycle.setIcon(Icons.getScaledIcon(Icons.REDO, iconSize, iconSize));
			tglBtnCycle.setSelected(true);
		}
		return tglBtnCycle;
	}
	
	private LToggleButton getTglBtnMatchCase() {
		if (tglBtnMatchCase == null) {
			tglBtnMatchCase = new LToggleButton("Ab");
			tglBtnMatchCase.setMargin(new Insets(0, 0, 1, 0));
			tglBtnMatchCase.setToolTipText(TEXTS.matchCase);
			tglBtnMatchCase.setFontSize(tglBtnMatchCase.getFont().getSize()/2+1);
			tglBtnMatchCase.setFontStyle(Font.BOLD);
			tglBtnMatchCase.getButtonProperties().setShadowTextMode(LButtonProperties.SHADOW_TEXT_MODE_ALWAYS);
			tglBtnMatchCase.setPreferredSize(getTglBtnCycle().getPreferredSize());
			//tglBtnMatchCase.setIcon();
		}
		return tglBtnMatchCase;
	}
	
	private LLabel getLabelMessage() {
		if (labelMessage == null) {
			labelMessage = new LLabel();
			labelMessage.setForeground(ColorsGUI.getColorNegative());
			labelMessage.setPreferredSize(getTextField().getPreferredSize());
		}
		return labelMessage;
	}
	private LTextField getTextField() {
		if (textField == null) {
			textField = new LTextField() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() != KeyEvent.VK_ENTER)
						textChanged();
				}
				@Override
				public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						getTglBtnUp().setSelected(true);
					}
					else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						getTglBtnDown().setSelected(true);
					}
					else if (e.getKeyCode() != KeyEvent.VK_ENTER)
						textChanged();
				}
				@Override
				public void keyPressed(KeyEvent e) {
					if (getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
						getButtonFindNext().doClick();
						requestFocus();
					}
					else {
						super.keyPressed(e);
					}
				}
				@Override
				public void setText(String text) {
					super.setText(text);
					textChanged();
				}
			};
			textField.setTextBackgroundVoid(TEXTS.findText);
			textField.setPreferredSize(new Dimension(getPanelSouth().getPreferredSize().width - 10, textField.getPreferredSize().height));
		}
		return textField;
	}
	
	private void textChanged() {
		boolean emptyText = textField.getText().length() == 0;
		getButtonFindNext().setEnabled(!emptyText);
		getLabelMessage().setText(Constants.VOID);
	}
	
	public void setJTextComponent(JTextComponent textComponent) {
		this.textComponent = textComponent;
		String selectedText = textComponent.getSelectedText();
		if (selectedText != null)
			getTextField().setText(selectedText);
	}
}
