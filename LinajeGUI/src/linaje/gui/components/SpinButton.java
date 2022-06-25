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

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import linaje.gui.LArrowButton;
import linaje.gui.LTextField;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.FormattedData;
import linaje.utils.Numbers;

/**
 * Gestionar en un componente visual los eventos de incrementar o decrementar el valor de un JTextField numérico.
 * - Si el textField es editable se podrá escribir el valor que queramos siempre que este dentro
 *   del rango (minimum, maximum), si no esta dentro de este rango, el componente no dejará escribir
 *   el nuevo valor, así como tampoco dejará escribir caracteres (Si es un LTextField, este control lo hará el propio LTextField).
 * - Se le puede poner a la escucha de ActionEvents como un botón normal, con la diferencia de
 *   que SpinButton lanzará este tipo de evento siempre que pulsemos sobre cualquiera de sus botones
 *   así como cuando el textField pierda el foco y el valor sea distinto al que tenía cuando lo
 *   cogió (Nota: si pulsamos la tecla RETROCESO o INTRO, el textField perderá el foco)
 * - Se puede incrementar y decrementar con las teclas de flechas ARRIBA y ABAJO cuando el textField tiene el foco.
 * 
 * @author Pablo Linaje
 * @version 1.10
 * 
 */
public class SpinButton extends JPanel implements FocusListener, KeyListener {
	private static final long serialVersionUID = 1L;
	
	private LArrowButton btnDown = null;
	private LArrowButton btnUp = null;
	private int maximum = Numbers.MAX_INTEGER_NUMBER;
	private int minimum = Numbers.MIN_INTEGER_NUMBER;
	private int initialValue = 0;
	private int increment = 1;
	private JTextField textField = new JTextField();
	protected EventListenerList listenerList = new EventListenerList();
	private int value = 0;
	private boolean enabled = true;
	
	public SpinButton() {
		super();
		initialize();
	}
	
	public void addActionListener(ActionListener listener) {
		listenerList.add(ActionListener.class, listener);
	}
	
	public void increment(boolean upDirection) {
	
		if (getTextField() == null)
			return;
		
		String text = getTextField().getText();
		
		LTextField lTextField = getTextField() instanceof LTextField ? (LTextField) getTextField() : null;
		
		int value = -1;
		try {
			value = lTextField != null ? lTextField.getValueNumber().intValue() : Integer.parseInt(getTextField().getText());
		}
		catch (NumberFormatException nfe) {
			value = 0;
		}
		
		if (upDirection) {
			if (value <= getMaximum() - getIncrement()) {
				value = value + getIncrement();
			}
		}
		else if (value >= getMinimum() + getIncrement()) {
			value = value - getIncrement();
		}
		
		String newText = Integer.toString(value);
		
		if (!newText.equals(text)) {
			
			setValue(value);
			
			fireActionPerformed();
		}
		
	}
	
	public void destroy() {
	
		removeAll();
		
		btnDown = null;
		btnUp = null;
		textField = null;
	
		listenerList = null;
		
		try {
			super.finalize();
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	protected void fireActionPerformed() {
		Object[] listeners = listenerList.getListenerList();
		ActionEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				if (e == null) {
					e = new ActionEvent(SpinButton.this, ActionEvent.ACTION_PERFORMED, "SpinButton", 0);
				}
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}
	
	public void focusGained(FocusEvent e) {
	}
	public void focusLost(FocusEvent e) {
		
		if (e.getSource() == getTextField()) {
			
			String text = getTextField().getText();
			if (!Numbers.isIntegerNumber(text)) {
				text = Integer.toString(getInitialValue());
				getTextField().setText(text);
			}
				
			int newValue = Integer.parseInt(text);
			if (newValue != getValue()) {
				setValue(newValue);
				fireActionPerformed();
			}
		}
	}
	
	public LArrowButton getBtnDown() {
		if (btnDown == null) {
			btnDown = new LArrowButton(LArrowButton.SOUTH);
			btnDown.setMargin(new Insets(0, 0, 0, 0));
			btnDown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					increment(false);
				}
			});
		}
		return btnDown;
	}
	
	public LArrowButton getBtnUp() {
		if (btnUp == null) {
			btnUp = new LArrowButton(LArrowButton.NORTH);
			btnUp.setMargin(new Insets(0, 0, 0, 0));
			btnUp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					increment(true);
				}
			});
		}
		return btnUp;
	}
	
	public int getIncrement() {
		return increment;
	}
	public int getMaximum() {
		return maximum;
	}
	public int getMinimum() {
		return minimum;
	}
	public JTextField getTextField() {
		return textField;
	}
	public int getValue() {
		return value;
	}
	public int getInitialValue() {
		return initialValue;
	}
	public boolean isEnabled() {
		return enabled;
	}
	
	private void initialize() {
		
		setPreferredSize(new Dimension(20, 20));
		setLayout(new GridLayout(2, 0));
		setSize(20, 20);
		add(getBtnUp());
		add(getBtnDown());
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			increment(true);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			increment(false);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER){
			e.consume();
			transferFocus();
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
		
		if (!(e.getSource() instanceof LTextField)) {
			
			//Este control ya lo hacemos internamente en el LTextField
			char keyChar = e.getKeyChar();
			if (keyChar == '-') {
				
				if (!minusAllowed()) {
					e.consume();
				}
			}
			else if (keyChar == '0' || keyChar == '1' || 
				keyChar == '2' || keyChar == '3' ||
				keyChar == '4' || keyChar == '5' ||
				keyChar == '6' || keyChar == '7' ||
				keyChar == '8' || keyChar == '9') {
					
					int value = Integer.parseInt(String.valueOf(keyChar));
					int totalValue = obtainTotalValue(value);
					if (totalValue > getMaximum()) {// || totalValue < getMinimum()) {
						e.consume();	
					}
			}
			else if (keyChar != KeyEvent.VK_BACK_SPACE && keyChar != KeyEvent.VK_DELETE) {
				e.consume();
			}
		}
	}
	
	/**
	 * <b>Descripción:</b><br>
	 *
	 * Se utiliza cuando captamos que se ha presionado la tecla "-"
	 *
	 * Devuelve un boolean que estará a "true" si el Spin permite 
	 * valores negativos y ademas el value está comprendido entre
	 * el máximo y el mínimo permitido.
	 * 
	 * @return boolean
	 */
	private boolean minusAllowed() {
		boolean minusAllowed = true;
		//Solo dejaremos escribir el cáracter "-" si el mínimo es menor de cero
		if (getMinimum() < 0) {
			String text = getTextField().getText();
			if (getTextField().getSelectedText() == null) {
				//Solo lo escribiremos si el cursor está en la posición 0
				int caretPosition = getTextField().getCaretPosition();
				if (caretPosition != 0) {
					minusAllowed = false;
				}
				String valueText = text.substring(caretPosition);
				//Si hay algun valor tras el menos, comprobamos que es mayor que el mínimo y menor que el máximo
				if (!valueText.equals(Constants.VOID)) {
					int value = Integer.parseInt("-" + valueText);
					if (value > getMaximum() || value < getMinimum()) {
						minusAllowed = false;
					}
				}
			} else {
				int selectionStart = getTextField().getSelectionStart();
				int selectionEnd = getTextField().getSelectionEnd();
				//Solo lo escribiremos si el inicio de la selección es la posición 0
				if (selectionStart != 0) {
					minusAllowed = false;
				}
				String valueText = text.substring(selectionEnd);
				//Si hay algun valor tras el menos, comprobamos que es mayor que el mínimo y menor que el máximo
				if (!valueText.equals(Constants.VOID)) {
					int value = Integer.parseInt("-" + valueText);
					if (value > getMaximum() || value < getMinimum()) {
						minusAllowed = false;
					}
				}
			}
		} else {
			minusAllowed = false;
		}
		return minusAllowed;
	}
	/**
	 * <b>Descripción:</b><br>
	 * Devuelve el value que quedaría tras escribir un número
	 * en el textField del Spinbutton
	 * 
	 * @return int
	 * @param value int
	 */
	private int obtainTotalValue(int value) {
		int totalValue = 0;
		String text = getTextField().getText();
		//A la hora de coger el value total hay que mirar si ya hay algo en
		//el textField y en caso de que lo haya si esta seleccionado totalmente
		//o en parte y además mirar la posición del cursor
		if (text.equals(Constants.VOID)) {
			//Caso mas sencillo no hay nada en el textField, por lo que el value pulsado es el value total
			totalValue = value;
		} else {
			String selectedText = getTextField().getSelectedText();
			String totalValueText;
			//Si ya hay algun value es cuando se complica
			if (selectedText == null) {
				//Nos importa lo que hay antes y después del cursor
				int caretPosition = getTextField().getCaretPosition();
				totalValueText = text.substring(0, caretPosition) + value + text.substring(caretPosition);
				totalValue = Integer.parseInt(totalValueText);
			} else {
				//Nos importa lo que hay antes y después de la selección
				if (text.equals(selectedText)) {
					//Este es el otro caso sencillo, el value pulsado es el value total
					totalValue = value;
				} else {
					int selectionStart = getTextField().getSelectionStart();
					int selectionEnd = getTextField().getSelectionEnd();
					totalValueText = text.substring(0, selectionStart) + value + text.substring(selectionEnd);
					totalValue = Integer.parseInt(totalValueText);
				}
			}
		}
		return totalValue;
	}
	public void removeActionListener(ActionListener listener) {
		listenerList.remove(ActionListener.class, listener);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		getBtnDown().setEnabled(enabled);
		getBtnUp().setEnabled(enabled);
		if (getTextField() != null)
			getTextField().setEnabled(enabled);
	}
	
	public void setIncrement(int increment) {
		this.increment = increment;
	}
	
	public void setMaximum(int maximum) {
		
		this.maximum = maximum;
		
		if (getTextField() != null && getTextField() instanceof LTextField && ((LTextField) getTextField()).getDecimals() == 0)
			((LTextField) getTextField()).setMaxValue(maximum);
	}
	
	public void setMinimum(int minimum) {
		
		this.minimum = minimum;
	
		if (getTextField() != null && getTextField() instanceof LTextField && ((LTextField) getTextField()).getDecimals() == 0)
			((LTextField) getTextField()).setMinValue(minimum);
	}
	
	public void setTextField(JTextField textField) {
	
		if (this.textField != textField) {
			
			if (this.textField != null) {
	
				this.textField.removeKeyListener(this);
				this.textField.removeFocusListener(this);
			}
			
			this.textField = textField;
			
			if (textField != null) {
				
				if (textField instanceof LTextField) {
	
					LTextField lTextField = (LTextField) textField;
					if (lTextField.getType() != FormattedData.TYPE_NUMBER)
						lTextField.setType(FormattedData.TYPE_NUMBER);
					if (lTextField.getDecimals() > 0)
						lTextField.setDecimals(0);
					if (getMaximum() != Numbers.MAX_INTEGER_NUMBER)
						lTextField.setMaxValue(getMaximum());
					if (getMinimum() != Numbers.MIN_INTEGER_NUMBER)
						lTextField.setMinValue(getMinimum());
					
					Number currentValue = lTextField.getValueNumber();
					setValue(currentValue.intValue());
				}
				else {
					
					setValue(getInitialValue());
					textField.setText(Integer.toString(getInitialValue()));
				}
				textField.addKeyListener(this);
				textField.addFocusListener(this);
			}
		}
	}
	
	public void setValue(int newValue) {
	
		if (newValue < getMinimum()) {
			newValue = getMinimum();
		}
		else if (newValue > getMaximum()) {
			newValue = getMaximum();
		}
		this.value = newValue;
	
		if (getTextField() != null)
			getTextField().setText(Integer.toString(newValue));
	}
	
	public void setInitialValue(int initialValue) {
		this.initialValue = initialValue;
	}
}
