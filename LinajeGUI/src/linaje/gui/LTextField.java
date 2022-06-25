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
package linaje.gui;

import javax.swing.*;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormatSymbols;
import java.util.*;

import linaje.LocalizedStrings;
import linaje.gui.ui.UISupport;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Files;
import linaje.utils.FormattedData;
import linaje.utils.LFont;
import linaje.utils.Lists;
import linaje.utils.Numbers;
import linaje.utils.Utils;

/**
 * Añadidos respecto a un JTextField:
 * 	- Totalmente compatible con FormattedData
 * 		- Se le puede asignar un tipo de los soportados por FormattedData y automáticamente formateará el texto al formato establecido
 * 		- Nos devuelve el valor del tipo correspondiente
 * 		- Nos permite modificar el formattedData y aprovechar todas sus ventajas como:
 * 		  cambiar decimales o añadir prefijos y postfijos, establecer máximo y mínimo, número de caracteres...
 *	- Validate State: Si lo usamos, coloreará el borde del textField según el estado
 * 	- Se puede activar requerido rellenado y coloreará el fondo de amarillo
 *  - Compatible Windows Files: Si se activa no dejará escribir caracteres no compatibles con el sistema de archivos de windows
 *  - Text Background Void: Se mostrará un texto de fondo cuando el textfield esté vacío (Ej: Inserte el país a buscar)
 *  - Métodos de acceso a las propiedades de la fuente del texto
 **/
public class LTextField extends JTextField implements KeyListener, FocusListener {

	private static final long serialVersionUID = 5018476780169580665L;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String insertErrorMaxChars;
		public String insertErrorForbiddenChars;
		public String characters;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final int VALIDATE_OFF = 0;
	public static final int VALIDATE_YES = 1;
	public static final int VALIDATE_NO = 2;
	public static final int VALIDATE_INPROGRESS = 3;
	
	public static final int TYPE_TEXT = FormattedData.TYPE_TEXT;
	public static final int TYPE_NUMBER = FormattedData.TYPE_NUMBER;
	public static final int TYPE_DATE = FormattedData.TYPE_DATE;
	public static final int TYPE_BOOLEAN = FormattedData.TYPE_BOOLEAN;
	public static final int TYPE_COLOR = FormattedData.TYPE_COLOR;
	public static final int TYPE_FONT = FormattedData.TYPE_FONT;
	
	public static final String PROPERTY_TYPE = FormattedData.PROPERTY_TYPE;
	public static final String PROPERTY_DECIMALS = FormattedData.PROPERTY_DECIMALS;
	public static final String PROPERTY_MAX_VALUE = FormattedData.PROPERTY_MAX_VALUE;
	public static final String PROPERTY_MIN_VALUE = FormattedData.PROPERTY_MIN_VALUE;
	public static final String PROPERTY_VALIDATE_STATE = "validateState";
	
	public static final Color TEXTFIELD_REQUIRED_COLOR = ColorsGUI.YELLOW_BRIGHT;
	
	private FormattedData formattedData = null;
	
	private boolean deletingText = false;
	private boolean selectTextWithFocus = true;
	private boolean filledRequired = false;
	private Color backgroundOriginal = null;

	private int horizontalAlignmentSet = -1;
	private int validateState = VALIDATE_OFF;
	private boolean compatibleWindowsFiles = false;
	
	private String textBackgroundVoid = null;
	private Font fontTextBackgroundVoid = null;

	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			String propertyName = evt.getPropertyName();
			Object oldValue = evt.getOldValue();
			Object newValue = evt.getNewValue();
			
			if (propertyName.equals(FormattedData.PROPERTY_FORMATTED_TEXT)) {
				formattedTextChanged();				
			}
			else if (propertyName.equals(FormattedData.PROPERTY_TYPE)) {
				typeChanged();				
			}
			
			firePropertyChange(propertyName, oldValue, newValue);
		}
	};
	
	public LTextField() {
		this(null, null, 0);
	}	
	
	public LTextField(String text) {
		this(null, text, 0);
	}
	
	public LTextField(int columns) {
		this(null, null, columns);
	}
	
	public LTextField(String text, int columns) {
		 this(null, text, columns);
	}

	public LTextField(Document doc, String text, int columns) {
		super(doc, null, columns);
		initialize();
		if (text != null)
			setText(text);
	}

	private void formattedTextChanged() {
		super.setText(getFormattedData().getFormattedText());
		checkFilledRequired();
		
		if (!isEditable() || !isEnabled()) {
	
			setSelectionStart(0);
			setSelectionEnd(0);
		}
		
		repaint();
	}
	
	private void typeChanged() {
		
		if (horizontalAlignmentSet == -1) {
			
			int dataType = getFormattedData().getType();
			int horizAlignment = SwingConstants.LEFT;
			
			if (dataType == FormattedData.TYPE_NUMBER)
				horizAlignment = SwingConstants.RIGHT;
			else if (dataType == FormattedData.TYPE_DATE)
				horizAlignment = SwingConstants.CENTER;
			
			super.setHorizontalAlignment(horizAlignment);
		}
	}
	
	private void checkFilledRequired() {
	
		if (getType() == FormattedData.TYPE_DATE) {
	
			Date date = getValueDate();
			if (date == null)
				setBackground(TEXTFIELD_REQUIRED_COLOR);
			else
				setBackground(getBackgroundOriginal());	
		}
		else if (isFilledRequired() && getText().equals(Constants.VOID))
			setBackground(TEXTFIELD_REQUIRED_COLOR);
		else
			setBackground(getBackgroundOriginal());	
	}
	
	public Object getValue() {
		return getFormattedData().getValue();
	}
	public Date getValueDate() {
		return getFormattedData().getValueDate();
	}
	public Number getValueNumber() {
		return getFormattedData().getValueNumber();
	}
	public Boolean getValueBoolean() {
		return getFormattedData().getValueBoolean();
	}
	public Color getValueColor() {
		return getFormattedData().getValueColor();
	}
	public Font getValueFont() {
		return getFormattedData().getValueFont();
	}
	public void focusGained(FocusEvent e) {
	
		if (isSelectTextWithFocus() && isEditable() && isEnabled()) {
			if (getSelectedText() == null)
				selectAll();
		}
		//if (getType() == TYPE_COLOR)
		//repaint();
	}
	public void focusLost(FocusEvent e) {
		if (e.getSource() == this) {
			getFormattedData().setValue(getText());
			if (isInitialValueEnabled() && getType() != TYPE_TEXT && getText().equals(Constants.VOID) || !getText().equals(getFormattedData().getFormattedText()))
				formattedTextChanged();
		}
		//if (getType() == TYPE_COLOR)
		//repaint();
	}
	
	public Color getBackgroundOriginal() {
		if (backgroundOriginal == null)
			backgroundOriginal = getBackground();
		return backgroundOriginal;
	}
	
	public String getFontName() {
		return getFont().getName();
	}
	public int getFontSize() {
		return getFont().getSize();
	}
	public int getFontStyle() {
		return getFont().getStyle();
	}
	public int getFontLayout() {
		return getFont() instanceof LFont ? ((LFont) getFont()).getLayoutMode() : LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
	}
	
	public boolean isAllToUpperCase() {
		return getFormattedData().getCapitalizeType() == FormattedData.CAPS_ALL_TO_UPPERCASE;
	}
	
	public int getMaxCharacters() {
		return getFormattedData().getMaxCharacters();
	}
	public int getDecimals() {
		return getFormattedData().getDecimals();
	}
	
	public double getMaxValue() {
		return getFormattedData().getMaxValue();
	}
	public double getMinValue() {
		return getFormattedData().getMinValue();
	}
	public int getType() {
		return getFormattedData().getType();
	}
	public Class<?> getClassType() {
		return getFormattedData().getClassType();
	}
	public boolean isInitialValueEnabled() {
		return getFormattedData().isFormatEmptyTextEnabled();
	}
		
	private void initialize() {
	
		addKeyListener(this);
		addFocusListener(this);
		setTooltipEnabled(true);
			
		getFormattedData().addPropertyChangeListener(propertyChangeListener);
	}
	
	public void keyPressed(KeyEvent e) {
	
		deletingText = e.getKeyCode() == KeyEvent.VK_BACK_SPACE ||	e.getKeyCode() == KeyEvent.VK_DELETE;
		
		if (deletingText && getText().length() > 0) {
	
			boolean textDeleted = true;
			
			if (getSelectedText() == null) {
				
				int cursorPosition = getCaretPosition();
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					textDeleted = cursorPosition > 0;
				else if (e.getKeyCode() == KeyEvent.VK_DELETE)
					textDeleted = cursorPosition < getText().length();
			}
			
			if (textDeleted)
				setValidateState(VALIDATE_INPROGRESS);
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			e.consume();
			transferFocus();
			//Esto es por si no hay otro componente que pueda recibir el foco
			boolean isfocusable = isFocusable();
			setFocusable(false);
			setFocusable(isfocusable);
		}
	}
	
	public void keyReleased(KeyEvent e) {
		checkFilledRequired();
	}
	
	public void keyTyped(KeyEvent e) {
		
		char keyChar = e.getKeyChar();
		
		if (!deletingText) {
			String errorInsert = getInsertTextError(String.valueOf(keyChar));
			if (errorInsert != null) {
				e.consume();
			}
			else if (isAllToUpperCase()) {
				e.setKeyChar(Character.toUpperCase(keyChar));
			}
		}
		
		if (getType() == FormattedData.TYPE_NUMBER) {
			
			if (keyChar == '-') {
				if (!minusAlowed()) {
					e.consume();
				}
			}
			else if (keyChar == '.' || keyChar == ',') {
	
				final char DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().getDecimalSeparator();
				if (getDecimals() == 0)
					e.consume(); 
				if (keyChar != DECIMAL_SEPARATOR) {
					keyChar = DECIMAL_SEPARATOR;
					e.setKeyChar(keyChar);
				}
				if (getSelectedText() == null || getSelectedText().indexOf(String.valueOf(DECIMAL_SEPARATOR)) == -1) {
	
					int indexDecimalSeparator = getText().indexOf(DECIMAL_SEPARATOR);
					if (indexDecimalSeparator != -1) {
						e.consume();	
					}
				}
			}
			else if (keyChar == '0' || keyChar == '1' || 
				keyChar == '2' || keyChar == '3' ||
				keyChar == '4' || keyChar == '5' ||
				keyChar == '6' || keyChar == '7' ||
				keyChar == '8' || keyChar == '9') 
			{
				/*String newText = obtainTotalValue(String.valueOf(keyChar));
				double totalValue = getFormattedData().getValueNumber(newText).doubleValue();
				if (totalValue > getMaxValue() || totalValue < getMinValue()) {
					e.consume();	
				}
				if (getDecimals() >= 0) {
					
					int indexDecimalSeparator = newText.indexOf(Numbers.DECIMAL_SEPARATOR);
					if (indexDecimalSeparator != -1) {
						
						String textDecimal = newText.substring(indexDecimalSeparator + 1, newText.length());
						if (textDecimal.length() > getDecimals()) {
							e.consume();
						}
					}
				}*/
			} 
			else if (keyChar != KeyEvent.VK_BACK_SPACE && keyChar != KeyEvent.VK_DELETE) {
				e.consume();
			}
		}
		checkFilledRequired();
	
		if (keyChar != KeyEvent.VK_ENTER && keyChar != KeyEvent.VK_ESCAPE && keyChar != KeyEvent.VK_BACK_SPACE && keyChar != KeyEvent.VK_DELETE)
			setValidateState(VALIDATE_INPROGRESS);
	}
	
	/**
	 * <b>Descripción:</b><br>
	 *
	 * Se utiliza cuando captamos que se ha presionado la tecla "-"
	 *
	 * Devuelve un boolean que estará a "true" si el TextField permite 
	 * valores negativos y ademas el valor está comprendido entre
	 * el máximo y el mínimo permitido.
	 * 
	 * @return boolean
	 */
	private boolean minusAlowed() {
		//Solo dejaremos escribir el cáracter "-" si el mínimo es menor de cero
		if (getMinValue() < 0) {
			try {
				String text = this.getText();
				if (this.getSelectedText() == null) {
					//Solo lo escribiremos si el cursor está en la posición 0
					int caretPosition = this.getCaretPosition();
					if (caretPosition != 0) {
						return false;
					}
					String valueText = text.substring(caretPosition);
					//Si hay algun valor tras el menos, comprobamos que es mayor que el mínimo y menor que el máximo
					if (!valueText.equals(Constants.VOID)) {
						double value = getFormattedData().getValueNumber("-" + valueText).doubleValue();
						if (value > getMaxValue() || value < getMinValue()) {
							return false;
						}
					}
				}
				else {
					int selectionStart = this.getSelectionStart();
					int selectionEnd = this.getSelectionEnd();
					//Solo lo escribiremos si el inicio de la selección es la posición 0
					if (selectionStart != 0) {
						return false;
					}
					String valueText = text.substring(selectionEnd);
					//Si hay algun valor tras el menos, comprobamos que es mayor que el mínimo y menor que el máximo
					if (!valueText.equals(Constants.VOID)) {
						double value = getFormattedData().getValueNumber("-" + valueText).doubleValue();
						if (value > getMaxValue() || value < getMinValue()) {
							return false;
						}
					}
				}
			} catch (Throwable ex) {
				Console.printException(ex);
			}
		} else {
			return false;
		}
		return true;
	}
	
	/*private String obtainTotalValue(String value) {
		//Devuelve el valor que quedaría tras escribir un número en el textField
		String totalValue;
		String text = this.getText();
		//A la hora de coger el valor total hay que mirar si ya hay algo en
		//el textField y en caso de que lo haya si esta seleccionado totalmente
		//o en parte y además mirar la posición del cursor
		if (text.equals(Constants.VOID)) {
			//Caso mas sencillo no hay nada en el textField, por lo que el valor pulsado es el valor total
			totalValue = value;
		} else {
			String selectedText = this.getSelectedText();
			//Si ya hay algun valor es cuando se complica
			if (selectedText == null) {
				//Nos importa lo que hay antes y después del cursor
				int caretPosition = this.getCaretPosition();
				totalValue = text.substring(0, caretPosition) + value + text.substring(caretPosition);
			} else {
				//Nos importa lo que hay antes y después de la selección
				if (text.equals(selectedText)) {
					//Este es el otro caso sencillo, el valor pulsado es el valor total
					totalValue = value;
				} else {
					int selectionStart = this.getSelectionStart();
					int selectionEnd = this.getSelectionEnd();
					totalValue = text.substring(0, selectionStart) + value + text.substring(selectionEnd);
				}
			}
		}
		return totalValue;
	}*/
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintTextBackgroundVoid(g);
		paintExtraBackground(g);
	}
	
	/**
	 * Transfers the contents of the system clipboard into the
	 * associated text model.  If there is a selection in the
	 * associated view, it is replaced with the contents of the
	 * clipboard.  If there is no selection, the clipboard contents
	 * are inserted in front of the current insert position in
	 * the associated view.  If the clipboard is empty, does nothing.
	 * @see #replaceSelection
	 */
	public void paste() {
	
		Clipboard clipboard = getToolkit().getSystemClipboard();
		Transferable content = clipboard.getContents(this);
		if (content != null) {
			try {
	
				deletingText = true;
				String textCopied = (String) (content.getTransferData(DataFlavor.stringFlavor));
				String pasteError = getInsertTextError(textCopied);
				if (pasteError == null) {
					if (isAllToUpperCase())
						textCopied = textCopied.toUpperCase();
					replaceSelection(textCopied);
				}
				else {
					MessageDialog.showMessage(pasteError, MessageDialog.ICON_ERROR);
				}
			}
			catch (Exception e) {
				getToolkit().beep();
			}
		}
	}
	
	private String getInsertTextError(String textInserted) {
		
		int selectedTextLength = 0;
		if (getSelectedText() != null)
			selectedTextLength = getSelectedText().length();

		if (getText().length() - selectedTextLength + textInserted.length() - 1 >= getMaxCharacters()) {
			return TEXTS.insertErrorMaxChars + getMaxCharacters() + TEXTS.characters;
		}
		else if (isCompatibleWindowsFiles() && !Files.isNameCompatibleWithWindowsFiles(textInserted)) {
			return TEXTS.insertErrorForbiddenChars + Lists.listToString(Lists.arrayToList(Files.FORBIDDEN_FILE_CHARACTERS));
		}
	
		return null;
	}
	
	private void paintTextBackgroundVoid(Graphics g) {
	
		if (!getTextBackgroundVoid().trim().equals(Constants.VOID) && getText().equals(Constants.VOID)) {
	
			final Font font = getFontTextBackgroundVoid() == null ? getFont() : getFontTextBackgroundVoid();
			final String text = getTextBackgroundVoid();
			final Color foreground = Colors.isColorDark(getForeground()) ? Colors.brighter(getForeground(), 0.3) : Colors.darker(getForeground(), 0.3);
			paintBGText(g, text, font, foreground);
		}
	}
	
	private void paintBGText(Graphics g, String text, Font font, Color foreground) {
	
		g.setFont(font);
		g.setColor(foreground);
		
		UISupport.paintText(g, text, this, SwingConstants.CENTER, SwingConstants.CENTER);
	}
	
	private void paintExtraBackground(Graphics g) {
		
		if (getType() == TYPE_COLOR && ((!isEditable() && isEnabled()) || !isFocusOwner())) {
			Color color = getFormattedData().getValueColor();
			GraphicsUtils.paintBackground(g, this, color);
			final String formattedText = getFormattedData().getFormattedText();
			final Color colorText = Colors.getLuminance(color) > 0.5 ? Colors.darker(color, 0.5) : Colors.brighter(color, 0.5);
			final Font font = getFont();
			paintBGText(g, formattedText, font, colorText);
		}
	}
	
	@Override
	public void setBackground(Color bg) {
	
		super.setBackground(bg);
		if (!bg.equals(TEXTFIELD_REQUIRED_COLOR))
			setBackgroundOriginal(bg);
	}
	
	private void setBackgroundOriginal(java.awt.Color backgroundOriginal) {
		this.backgroundOriginal = backgroundOriginal;
	}
	public void setCompatibleWindowsFiles(boolean compatibleWindowsFiles) {
		this.compatibleWindowsFiles = compatibleWindowsFiles;
	}
	
	public void setFontName(String fontName) {
		UtilsGUI.setFontName(this, fontName);
	}
	public void setFontSize(int fontSize) {
		UtilsGUI.setFontSize(this, fontSize);
	}
	public void setFontStyle(int fontStyle) {
		UtilsGUI.setFontStyle(this, fontStyle);
	}
	public void setFontLayout(int fontLayout) {
		UtilsGUI.setFontLayout(this, fontLayout);
	}
	
	@Override
	public void setHorizontalAlignment(int alignment) {
		horizontalAlignmentSet = alignment;
		super.setHorizontalAlignment(alignment);
	}
	
	public void setInitialValueEnabled(boolean initialValueEnabled) {
		getFormattedData().setFormatEmptyText(initialValueEnabled);
	}
	public void setMaxCharacters(int maxCharacters) {
		getFormattedData().setMaxCharacters(maxCharacters);
	}
	public void setDecimals(int decimals) {
		getFormattedData().setDecimals(decimals);
	}
	public void setMaxValue(double maxValue) {
		getFormattedData().setMaxValue(maxValue);
	}
	public void setMinValue(double minValue) {
		getFormattedData().setMinValue(minValue);
	}
	public void setAllToUpperCase(boolean allToUpperCase) {
		getFormattedData().setCapitalizeType(allToUpperCase ? FormattedData.CAPS_ALL_TO_UPPERCASE : FormattedData.CAPS_DO_NOTHING);
	}
	public void setType(int type) {
		getFormattedData().setType(type);
	}
	public void setClassType(Class<?> classType) {
		getFormattedData().setClassType(classType);
	}
	
	@Override
	public void setText(String text) {
		getFormattedData().setValue(text);
		//El super.setText() se hará en el propertyChangeListener
	}
	public void setValue(Object value) {
		getFormattedData().setValue(value);
	}
	
	public void setTooltipEnabled(boolean enabled) {
		if (enabled)
			ToolTip.getInstance().registerComponent(this);
		else
			ToolTip.getInstance().unRegisterComponent(this);
	}
	
	public void clearSelection() {
		setSelectionStart(0);
		setSelectionEnd(0);
		setScrollOffset(0);
		setCaretPosition(0);
	}
	
	public int getValidateState() {
		return validateState;
	}
	public boolean isCompatibleWindowsFiles() {
		return compatibleWindowsFiles;
	}
	public boolean isFilledRequired() {
		return filledRequired;
	}
	public boolean isSelectTextWithFocus() {
		return selectTextWithFocus;
	}
	public Font getFontTextBackgroundVoid() {
		return fontTextBackgroundVoid;
	}
	
	public FormattedData getFormattedData() {
		if (formattedData == null) {
			formattedData = new FormattedData(getText());
			formattedData.setThousandsSeparatorEnabled(false);
		}
		return formattedData;
	}

	
	public void setFilledRequired(boolean filledRequired) {
		this.filledRequired = filledRequired;
		checkFilledRequired();
	}
	public void setSelectTextWithFocus(boolean selectTextWithFocus) {
		this.selectTextWithFocus = selectTextWithFocus;
	}
	public void setTextBackgroundVoid(String textBackgroundVoid) {
		this.textBackgroundVoid = textBackgroundVoid;
	}
	public String getTextBackgroundVoid() {
		if (textBackgroundVoid == null)
			textBackgroundVoid = Constants.VOID;
		return textBackgroundVoid;
	}
	public void setFontTextBackgroundVoid(Font fontTextBackgroundVoid) {
		this.fontTextBackgroundVoid = fontTextBackgroundVoid;
	}
	
	public void setValidateState(int validateState) {
		
		int oldValue = validateState;
		this.validateState = validateState;
		
		if (oldValue != validateState) {
			repaint();
			firePropertyChange(PROPERTY_VALIDATE_STATE, oldValue, validateState);
		}
	}
	
	public void setFormattedData(FormattedData formattedData) {
		if (formattedData != null) {
			FormattedData oldValue = this.formattedData;
			FormattedData newValue = formattedData;
			if (Utils.propertyChanged(oldValue, newValue)) {
				if (oldValue != null)
					oldValue.removePropertyChangeListener(propertyChangeListener);
				this.formattedData = newValue;
				newValue.addPropertyChangeListener(propertyChangeListener);
				formattedTextChanged();
			}
		}
	}
	
	public void reload() {
		//Reasignamos el texto para que se actualice el valor de nuevo
		setText(getText());
	}
}
