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
package linaje.gui.editor;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import linaje.LocalizedStrings;
import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LCombo;
import linaje.gui.LPanel;
import linaje.gui.LToggleButton;
import linaje.gui.components.ColorButton;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Files;

@SuppressWarnings("serial")
public class LEditor extends JPanel implements ActionListener, ItemListener, KeyListener, MouseListener, PropertyChangeListener, CaretListener {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String open;
		public String save;
		public String delete;
		public String cut;
		public String copy;
		public String paste;
		public String undo;
		public String redo;
		public String bold;
		public String italic;
		public String underline;
		public String background;
		public String foreground;
		
		public String ksOpen;
		public String ksSave;
		public String ksDelete;
		public String ksCut;
		public String ksCopy;
		public String ksPaste;
		public String ksUndo;
		public String ksRedo;
		public String ksBold;
		public String ksItalic;
		public String ksUnderline;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	
	public static final int FORMAT_DEFAULT = 0;
	public static final int FORMAT_CONSOLE = 1;
	
	public static final String FONT_SERIF = "Serif";
	public static final String FONT_SANS_SERIF = "SansSerif";
	public static final String FONT_MONOSPACED = "Monospaced";
	public static final String FONT_DEFAULT = FONT_SANS_SERIF;
	public static final int SIZE_DEFAULT = 12;
	
	private File directory = null;
	private boolean caretChange = false;
	private int caretPosition = 0;
	private int format = FORMAT_DEFAULT;
	
	private LButton btnDelete = null;
	private LButton btnOpen = null;
	private LButton btnSave = null;
	private LButton btnCut = null;
	private LButton btnCopy = null;
	private LButton btnPaste = null;
	private LButton btnUndo = null;
	private LButton btnRedo = null;
	private LToggleButton tbtnBold = null;
	private LToggleButton tbtnItalic = null;
	private LToggleButton tbtnUnderline = null;
	private ColorButton colorButtonBackground = null;
	private ColorButton colorButtonForeground = null;
	private LCombo<String> comboFontName = null;
	private LCombo<Integer> comboFontSize = null;
	
	private LPanel toolBar = null;
	private LPanel panelCombosAux = null;
	private JPanel panelCombos = null;
	
	private JScrollPane scrollPane = null;
	private LTextPane textPane = null;
	
	public LEditor() {
		super();
		initialize();
	}
	
	public LEditor(int format) {
		super();
		this.format = format;
		initialize();
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == getBtnOpen()) {
			open();
		}
		else if (e.getSource() == getBtnSave()) {
		    save();
		}
		else if (e.getSource() == getBtnDelete()) {
		 	delete();
		}
		else if (e.getSource() == getTbtnBold()) {
			changeBold();
		}
		else if (e.getSource() == getTbtnItalic()) {
			changeItalic();
		}
		else if (e.getSource() == getTbtnUnderline()) {
			changeUnderline();
		}
		getTextPane().requestFocus();
	}
	
	public void updateToolBar() {
		
		try {
			
			AttributeSet attributes;
			if (caretPosition != 0)
				attributes = getTextPane().getStyledDocument().getCharacterElement(caretPosition-1).getAttributes();
			else
				attributes = getTextPane().getStyledDocument().getCharacterElement(caretPosition).getAttributes();
	
			Font font = getTextPane().getStyledDocument().getFont(attributes);
			
			//Actulaizamos el color de los botones de color
			Color foreground = getTextPane().getStyledDocument().getForeground(attributes);
			Color background = getTextPane().getBackground();
			getColorButtonBackground().setSelectedColor(background);
			getColorButtonForeground().setSelectedColor(foreground);
	
			//Actualizamos los combos
			if (font.getName().equalsIgnoreCase(FONT_SERIF))
				getComboFontName().setSelectedItem(FONT_SERIF);
			else if (font.getName().equalsIgnoreCase(FONT_MONOSPACED))
				getComboFontName().setSelectedItem(FONT_MONOSPACED);
			else
				getComboFontName().setSelectedItem(FONT_SANS_SERIF);
	
			int fontSize = font.getSize();
				
			getComboFontSize().setSelectedItem(Integer.toString(fontSize));
				
			getTbtnItalic().setSelected(font.isItalic());
			getTbtnBold().setSelected(font.isBold());
			getTbtnUnderline().setSelected(StyleConstants.isUnderline(attributes));
			
			//Repintamos
			repaint();
			caretChange = false;
		} 
		catch (Throwable ex) {
			handleException(ex);
		}	
	}
	
	protected void addKeyMap() {
		/*** Ver tambien en el método keyReleased() ***/
		
		//Añadimos un nuevo mapa de teclas
		Keymap keymap = JTextComponent.addKeymap("LEditorKeyMap", getTextPane().getKeymap());
		Action action;
		KeyStroke key;
		
		//Ctrl-n poner o quitar la negrita
		action = new StyledEditorKit.BoldAction();
		key = KeyStroke.getKeyStroke(getKeyCode(TEXTS.ksBold), Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
	
		//Ctrl-k poner o quitar la cursiva
		action = new StyledEditorKit.ItalicAction();
		key = KeyStroke.getKeyStroke(getKeyCode(TEXTS.ksItalic), Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
	
		//Ctrl-s poner o quitar el subrrallado
		action = new StyledEditorKit.UnderlineAction();
		key = KeyStroke.getKeyStroke(getKeyCode(TEXTS.ksUnderline), Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
		
		//Ctrl-a le asignamos beepAction que no funciona porque "Ctrl+a" por defecto esta para seleccionar todo
		//y no queda muy bien al asignarle tb la acción de abrir
		/*action = getTextPane().getActionByName(DefaultEditorKit.beepAction);
		key = KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
	
		//Ctrl-e para seleccionar todo
		action = getTextPane().getActionByName(DefaultEditorKit.selectAllAction);
		key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);*/
	
		//Ctrl-z para deshacer
		action = getTextPane().undoAction;
		key = KeyStroke.getKeyStroke(getKeyCode(TEXTS.ksUndo), Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
	
		//Ctrl-y para rehacer
		action = getTextPane().redoAction;
		key = KeyStroke.getKeyStroke(getKeyCode(TEXTS.ksRedo), Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
		
		getTextPane().setKeymap(keymap);
	}
	
	private String getCtrlDesc(String keyStroke) {
		return LocalizedStrings.getCtrlDesc(keyStroke);
	}
	private int getKeyCode(String keyStroke) {
		return LocalizedStrings.getKeyCode(keyStroke);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 9:35:56)
	 * @param boton javax.swing.JButton
	 */
	public static void asignButtonProperties(AbstractButton boton) {
	
		boton.setText(Constants.VOID);
		boton.setMargin(new Insets(4, 4, 4, 4));
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void delete() {
		
		try {
			getTextPane().getDocument().remove(0, getTextPane().getDocument().getLength());
			//getJTextPane().getUndoableEditListener().limpiar();			
			
			reasignAttributes();		
		}catch (Throwable ex) {
			handleException(ex);
		}
	}
	/**
	 * This method was created in VisualAge.
	 */
	private void open() {
		
		Frame frame = AppGUI.getCurrentAppGUI().getFrame();
		DlgOpenSaveFile dlgOpenFile = new DlgOpenSaveFile(frame, getDirectory(), DlgOpenSaveFile.MODE_OPEN);
		int response = dlgOpenFile.showInDialog();
		if (response == ButtonsPanel.RESPONSE_ACCEPT_YES) {
			try {
				if (format == FORMAT_CONSOLE) {
					Files.open(dlgOpenFile.getSelectedFile());
				} 
				else {
					getTextPane().setText(Constants.VOID);
					getTextPane().setPage(dlgOpenFile.getSelectedFile().toURI().toURL());
				}
			} catch (Throwable e) {
				handleException(e);
			}
		}
	}
	
	/**
	 * This method was created in VisualAge.
	 */
	private void save() {
		
		Frame frame = AppGUI.getCurrentAppGUI().getFrame();
		DlgOpenSaveFile dlgSaveFile = new DlgOpenSaveFile(frame, getDirectory(), DlgOpenSaveFile.MODE_SAVE);
		int response = dlgSaveFile.showInDialog();
		if (response == ButtonsPanel.RESPONSE_ACCEPT_YES) {
			save(dlgSaveFile.getSelectedFile());
		}
	}
	
	public void save(File file) {
		try {
			String htmlText = format == FORMAT_CONSOLE ? getTextPane().getTextHtmlBrowserOptimized() : getTextPane().getTextHtml();
			Files.saveText(htmlText, file);
		}
		catch (Throwable e) {
			Console.printException(e);
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 11:57:32)
	 * @param background java.lang.String
	 */
	public void changeBackground(Color background) {
	
		getTextPane().changeBackground(background);
	
		//Comprobamos el estado del combo
		if (!getColorButtonBackground().getSelectedColor().equals(background)) {
			getColorButtonBackground().setSelectedColor(background);	
		}
	
		repaint();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 11:57:32)
	 * @param foreground java.lang.String
	 */
	public void changeForeground(Color foreground) {
	
		getTextPane().changeForeground(foreground);
		
		//Comprobamos el estado del combo
		if (!getColorButtonForeground().getSelectedColor().equals(foreground)) {
			getColorButtonForeground().setSelectedColor(foreground);	
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 11:57:32)
	 */
	public void changeItalic() {
	
		AttributeSet attributes = getTextPane().getInputAttributes();
		Font font = getTextPane().getStyledDocument().getFont(attributes);
		
		getTbtnItalic().setSelected(!font.isItalic());
		getTextPane().changeItalic();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 11:57:32)
	 * @param fontName java.lang.String
	 */
	public void changeFontName(String fontName) {
	
		getTextPane().changeFontName(fontName);
		
		//Comprobamos el estado del combo
		String fontCombo = null;
		if (fontName.equalsIgnoreCase(FONT_SERIF) && !getComboFontName().getSelectedItem().equalsIgnoreCase(FONT_SERIF)) {
			fontCombo = FONT_SERIF;
		}
		else if (fontName.equalsIgnoreCase(FONT_SANS_SERIF) && !getComboFontName().getSelectedItem().equalsIgnoreCase(FONT_SANS_SERIF)) {
			fontCombo = FONT_SANS_SERIF;
		}
		else if (fontName.equalsIgnoreCase(FONT_MONOSPACED) && !getComboFontName().getSelectedItem().equalsIgnoreCase(FONT_MONOSPACED)) {
			fontCombo = FONT_MONOSPACED;
		}
		
		if (fontCombo != null) {
			getComboFontName().removeItemListener(this);
			getComboFontName().setSelectedItem(fontCombo);
			getComboFontName().addItemListener(this);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 11:57:32)
	 */
	public void changeBold() {
	
		AttributeSet attributes = getTextPane().getInputAttributes();
		Font font = getTextPane().getStyledDocument().getFont(attributes);
		getTbtnBold().setSelected(!font.isBold());
		getTextPane().changeBold();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2004 11:57:32)
	 */
	public void changeUnderline() {
	
		AttributeSet attributes = getTextPane().getInputAttributes();
		getTbtnUnderline().setSelected(!StyleConstants.isUnderline(attributes));
		getTextPane().changeUnderline();
	}
	
	public void changeFontSize(int fontSize) {
		getTextPane().changeFontSize(fontSize);
		//Comprobamos el estado del combo
		if (fontSize != getComboFontSize().getSelectedItem().intValue()) {
			getComboFontSize().removeItemListener(this);
			getComboFontSize().setSelectedItem(Integer.toString(fontSize));
			getComboFontSize().addItemListener(this);
		}
	}
	
	public void caretUpdate(CaretEvent e) {
		if (caretPosition != e.getDot()) {
			caretPosition = e.getDot();
			caretChange = true;
		} else {
			caretChange = false;
		}
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void destruir() {
		
		try {
			
			finalizeConnections();
			
			removeAll();
			getPanelCombos().removeAll();
			getScrollPane().removeAll();
			getToolBar().removeAll();
			
			getColorButtonBackground().destruir();
			getColorButtonForeground().destruir();
	
			getTextPane().destroy();
				
			directory = null;
		
			btnDelete = null;
			btnOpen = null;
			btnSave = null;
			btnCut = null;
			btnCopy = null;
			btnPaste = null;
			btnUndo = null;
			btnRedo = null;
			tbtnBold = null;
			tbtnItalic = null;
			tbtnUnderline = null;
	
			colorButtonBackground = null;
			colorButtonForeground = null;
			comboFontName = null;
			comboFontSize = null;
			scrollPane = null;
			textPane = null;
			panelCombos = null;
			toolBar = null;
			panelCombosAux = null;
		
			super.finalize();
			//System.gc();
			
		} catch (Throwable ex) {
			handleException(ex);
		}
	}
	/**
	 * This method was created in VisualAge.
	 */
	private void finalizeConnections() {
		try {
			getBtnOpen().removeActionListener(this);
			getBtnSave().removeActionListener(this);
			getTbtnBold().removeActionListener(this);
			getTbtnItalic().removeActionListener(this);
			getTbtnUnderline().removeActionListener(this);
			getBtnCut().removeActionListener(this);
			getBtnCopy().removeActionListener(this);
			getBtnPaste().removeActionListener(this);
			getBtnUndo().removeActionListener(this);
			getBtnRedo().removeActionListener(this);
			getBtnDelete().removeActionListener(this);
			
			getBtnOpen().removeMouseListener(this);
			getBtnSave().removeMouseListener(this);
			getTbtnBold().removeMouseListener(this);
			getTbtnItalic().removeMouseListener(this);
			getTbtnUnderline().removeMouseListener(this);
			getBtnCut().removeMouseListener(this);
			getBtnCopy().removeMouseListener(this);
			getBtnPaste().removeMouseListener(this);
			getBtnUndo().removeMouseListener(this);
			getBtnRedo().removeMouseListener(this);
			getBtnDelete().removeMouseListener(this);
			getTextPane().removeMouseListener(this);
			
			getComboFontName().removeItemListener(this);
			getComboFontSize().removeItemListener(this);
			
			getTextPane().removeCaretListener(this);
			getTextPane().removeKeyListener(this);
			
			getColorButtonBackground().removePropertyChangeListener(this);
			getColorButtonForeground().removePropertyChangeListener(this);
			
		} catch (Throwable ex) {
			handleException(ex);
		}
	}
		
	public ColorButton getColorButtonBackground() {
		if (colorButtonBackground == null) {
			colorButtonBackground = new ColorButton();
			colorButtonBackground.setPreferredSize(new Dimension(50, 23));
			colorButtonBackground.getLabelSelectedColor().setText(TEXTS.background);
			colorButtonBackground.setSelectedColor(Color.white);
		}
		return colorButtonBackground;
	}
	
	public ColorButton getColorButtonForeground() {
		if (colorButtonForeground == null) {
			colorButtonForeground = new ColorButton();
			colorButtonForeground.setPreferredSize(new Dimension(50, 23));
			colorButtonForeground.getLabelSelectedColor().setText(TEXTS.foreground);
			colorButtonForeground.setSelectedColor(Color.black);
		}
		return colorButtonForeground;
	}
	
	private LButton getBtnOpen() {	
		if (btnOpen == null) {
			btnOpen = new LButton();
        	btnOpen.setToolTipText(TEXTS.open + getCtrlDesc(TEXTS.ksOpen));
			btnOpen.setIcon(Icons.FOLDER);
			
			asignButtonProperties(btnOpen);	
		}
		return btnOpen;
	}
	
	public LButton getBtnDelete() {
		if (btnDelete == null) {
			btnDelete = new LButton();
        	btnDelete.setToolTipText(TEXTS.delete + getCtrlDesc(TEXTS.ksDelete));
			btnDelete.setIcon(Icons.DOCUMENT_WHITE);
			
			asignButtonProperties(btnDelete);
		}
		return btnDelete;
	}
	
	private LButton getBtnCopy() {
		if (btnCopy == null) {
			Action action = getTextPane().getActionByName(DefaultEditorKit.copyAction);
			btnCopy = new LButton(action);
        	btnCopy.setToolTipText(TEXTS.copy + getCtrlDesc(TEXTS.ksCopy));
			btnCopy.setIcon(Icons.COPY);
			
			asignButtonProperties(btnCopy);
		}
		return btnCopy;
	}
	
	private LButton getBtnCut() {
		
		if (btnCut == null) {
			Action action = getTextPane().getActionByName(DefaultEditorKit.cutAction);
			btnCut = new LButton(action);
        	btnCut.setToolTipText(TEXTS.cut + getCtrlDesc(TEXTS.ksCut));
			btnCut.setIcon(Icons.CUT);
			
			asignButtonProperties(btnCut);
		}
		return btnCut;
	}
	
	private LButton getBtnUndo() {
		
		if (btnUndo == null) {
			btnUndo = new LButton(getTextPane().undoAction);
			btnUndo.setToolTipText(TEXTS.undo + getCtrlDesc(TEXTS.ksUndo));
			btnUndo.setIcon(Icons.UNDO);
			
			asignButtonProperties(btnUndo);
		}
		return btnUndo;
	}
	
	private LButton getBtnRedo() {
		
		if (btnRedo == null) {
			btnRedo = new LButton(getTextPane().redoAction);
			btnRedo.setToolTipText(TEXTS.redo + getCtrlDesc(TEXTS.ksRedo));
			btnRedo.setIcon(Icons.REDO);
			
			asignButtonProperties(btnRedo);
		}
		return btnRedo;
	}
	
	private LButton getBtnSave() {
		
		if (btnSave == null) {
			btnSave = new LButton();
			btnSave.setToolTipText(TEXTS.save + getCtrlDesc(TEXTS.ksSave));
			btnSave.setIcon(Icons.SAVE);
			
			asignButtonProperties(btnSave);
		}
		return btnSave;
	}
	
	private LButton getBtnPaste() {
		
		if (btnPaste == null) {
			Action action = getTextPane().getActionByName(DefaultEditorKit.pasteAction);
			btnPaste = new LButton(action);
			btnPaste.setToolTipText(TEXTS.paste + getCtrlDesc(TEXTS.ksPaste));
			btnPaste.setIcon(Icons.PASTE);
			
			asignButtonProperties(btnPaste);
		}
		return btnPaste;
	}
	
	public LToggleButton getTbtnBold() {
		
		if (tbtnBold == null) {
			tbtnBold = new LToggleButton();
			tbtnBold.setToolTipText(TEXTS.bold + getCtrlDesc(TEXTS.ksBold));
			tbtnBold.setIcon(Icons.BOLD);
			
			asignButtonProperties(tbtnBold);
		}
		return tbtnBold;
	}
	
	public LToggleButton getTbtnItalic() {
		
		if (tbtnItalic == null) {
			tbtnItalic = new LToggleButton();
			tbtnItalic.setToolTipText(TEXTS.italic + getCtrlDesc(TEXTS.ksItalic));
			tbtnItalic.setIcon(Icons.ITALIC);
			
			asignButtonProperties(tbtnItalic);
		}
		return tbtnItalic;
	}
		
	public LToggleButton getTbtnUnderline() {
		
		if (tbtnUnderline == null) {
			tbtnUnderline = new LToggleButton();
			tbtnUnderline.setToolTipText(TEXTS.underline + getCtrlDesc(TEXTS.ksUnderline));
			tbtnUnderline.setIcon(Icons.UNDERLINE);
			
			asignButtonProperties(tbtnUnderline);
		}
		return tbtnUnderline;
	}
	
	public LCombo<String> getComboFontName() {
		if (comboFontName == null) {
			comboFontName = new LCombo<>();
			comboFontName.setName("ComboFuente");
			comboFontName.addItem(FONT_SANS_SERIF);
			comboFontName.addItem(FONT_SERIF);
			comboFontName.addItem(FONT_MONOSPACED);
		}
		return comboFontName;
	}
	
	public LCombo<Integer> getComboFontSize() {
		if (comboFontSize == null) {
			comboFontSize = new LCombo<>();
			comboFontSize.addItem(10);
			comboFontSize.addItem(12);
			comboFontSize.addItem(14);
			comboFontSize.addItem(18);
			comboFontSize.addItem(24);
			comboFontSize.setSelectedItem(SIZE_DEFAULT);
		}
		return comboFontSize;
	}
	
	public File getDirectory() {
		if (directory == null) {
			String dirName = format == FORMAT_CONSOLE ? "Console" : "Editor";
			directory = new File(Directories.getAppGeneratedFiles(), dirName);
		}
		return directory;
	}
	
	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setViewportView(getTextPane());
		}
		return scrollPane;
	}
	public LTextPane getTextPane() {
		if (textPane == null) {
			textPane = new LTextPane();
		}
		return textPane;
	}
	
	private JPanel getPanelCombos() {
		if (panelCombos == null) {
			panelCombos = new JPanel();
			panelCombos.setOpaque(false);
			panelCombos.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 1));
			panelCombos.add(getPanelCombosAux());
		}
		return panelCombos;
	}
	
	private LPanel getPanelCombosAux() {
		if (panelCombosAux == null) {
			panelCombosAux = new LPanel();
			panelCombosAux.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
			panelCombosAux.add(getComboFontName());
			panelCombosAux.add(getComboFontSize());
		}
		return panelCombosAux;
	}

	public LPanel getToolBar() {
		if (toolBar == null) {
			toolBar = new LPanel();
			toolBar.setLayout(new LFlowLayout(FlowLayout.LEFT, 1, 1, false));
		}
		return toolBar;
	}
	
	private void handleException(Throwable exception) {
		Console.printException(exception);
	}
	
	private void inicializarConexiones() {
		
		getBtnOpen().addActionListener(this);
		getBtnSave().addActionListener(this);
		getTbtnBold().addActionListener(this);
		getTbtnItalic().addActionListener(this);
		getTbtnUnderline().addActionListener(this);
		getBtnCut().addActionListener(this);
		getBtnCopy().addActionListener(this);
		getBtnPaste().addActionListener(this);
		getBtnUndo().addActionListener(this);
		getBtnRedo().addActionListener(this);
		getBtnDelete().addActionListener(this);
		
		getBtnOpen().addMouseListener(this);
		getBtnSave().addMouseListener(this);
		getTbtnBold().addMouseListener(this);
		getTbtnItalic().addMouseListener(this);
		getTbtnUnderline().addMouseListener(this);
		getBtnCut().addMouseListener(this);
		getBtnCopy().addMouseListener(this);
		getBtnPaste().addMouseListener(this);
		getBtnUndo().addMouseListener(this);
		getBtnRedo().addMouseListener(this);
		getBtnDelete().addMouseListener(this);
		getTextPane().addMouseListener(this);
		
		getComboFontName().addItemListener(this);
		getComboFontSize().addItemListener(this);
		
		getTextPane().addCaretListener(this);
		getTextPane().addKeyListener(this);
		
		getColorButtonBackground().addPropertyChangeListener(this);
		getColorButtonForeground().addPropertyChangeListener(this);
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		setSize(600, 200);
		add(getScrollPane(), BorderLayout.CENTER);
		add(getToolBar(), BorderLayout.NORTH);
		
		addButtons();
		
		inicializarConexiones();
		addKeyMap();
		
		if (format==FORMAT_CONSOLE) {
			getToolBar().remove(getBtnCut());
			getPanelCombosAux().remove(getComboFontName());
		}
	
		reasignAttributes();
	
		getPanelCombosAux().setGradientBackground(true);
	}
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			if (e.getSource() == getComboFontName()) {
				changeFontName(getComboFontName().getSelectedItem());
			}
			if (e.getSource() == getComboFontSize()) {
				int fontSize = getComboFontSize().getSelectedItem().intValue();
				changeFontSize(fontSize);
			}
			
			getTextPane().requestFocus();
			caretChange = false;
		}
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void keyReleased(KeyEvent e) {
		/*** Ver tambien en el método addKeyMap() ***/
		
		if (e.getModifiers() == KeyEvent.CTRL_MASK) {
			if (e.getKeyCode() == getKeyCode(TEXTS.ksBold)) {
				getTbtnBold().setSelected(!getTbtnBold().isSelected());
			}
			if (e.getKeyCode() == getKeyCode(TEXTS.ksItalic)) {
				getTbtnItalic().setSelected(!getTbtnItalic().isSelected());
			}
			if (e.getKeyCode() == getKeyCode(TEXTS.ksUnderline)) {
				getTbtnUnderline().setSelected(!getTbtnUnderline().isSelected());
			}
			
			if (e.getKeyCode() == getKeyCode(TEXTS.ksOpen)) {
				open();
			}
			if (e.getKeyCode() == getKeyCode(TEXTS.ksSave)) {
				save();
			}
			if (e.getKeyCode() == getKeyCode(TEXTS.ksDelete)) {
				delete();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
		/*	if (getJTextPane().getDocument().getLength() == 0) {
				AttributeSet attributes = reasignAttributes();
				try {
					((HTMLEditorKit) getJTextPane().getEditorKit()).getInputAttributes().addAttributes(attributes);
					getTextPane().getDocument().insertString(0, " ", attributes);
					getTextPane().setCharacterAttributes(attributes, true);
				}
				catch (Throwable ex) {
					handleException(ex);
				}
				e.consume();
			}
		*/
		}
		if (caretChange && e.getModifiers() != 1) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
				updateToolBar();
			}
		}
	}
	
	public static void main(String[] args) {
		
		try {
			
			LinajeLookAndFeel.init();
			
			LEditor editor = new LEditor();
			LDialogContent.showComponentInFrame(editor);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
	public void mostrar(File fichero) {
		try {
			getTextPane().setPage(fichero.getAbsolutePath());
			repaint();
		} catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == getTextPane() && caretChange){
			updateToolBar();
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void propertyChange(PropertyChangeEvent e) {
		
		if (e.getSource() == getColorButtonBackground() && e.getPropertyName().equals(ColorButton.PROPERTY_selectedColor)) {
			
			changeBackground(getColorButtonBackground().getSelectedColor());
		}
		if (e.getSource() == getColorButtonForeground() && e.getPropertyName().equals(ColorButton.PROPERTY_selectedColor)) {
			
			changeForeground(getColorButtonForeground().getSelectedColor());
		}
		
		getTextPane().requestFocus();
		caretChange = false;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (30/04/2004 9:24:58)
	 */
	public synchronized AttributeSet reasignAttributes() {
	
		try {
			
			//Fuente
			String fontName = getComboFontName().getSelectedItem();
			changeFontName(fontName);
				
			//Tamaño de la fuente
			int fontSize = getComboFontSize().getSelectedItem().intValue();
			changeFontSize(fontSize);
	
			//Colors
			changeForeground(getColorButtonForeground().getSelectedColor());
			
			//Si el estado del botón no está acorde con el estilo, lo cambiamos.
			AttributeSet attributes = getTextPane().getInputAttributes();
			Font font = getTextPane().getStyledDocument().getFont(attributes);
			
			if (font.isBold() != getTbtnBold().isSelected()) {
				changeBold();
			}
			if (font.isItalic() != getTbtnItalic().isSelected()) {
				changeItalic();
			}
			if (StyleConstants.isUnderline(attributes) != getTbtnUnderline().isSelected()) {
				changeUnderline();
			}
	
			return attributes;
		}
		catch (Throwable ex) {
			handleException(ex);
		}
		return null;
	}
	
	private void addButtons() {
	
		getToolBar().removeAll();
		
		getToolBar().add(getBtnDelete());
		getToolBar().add(getBtnOpen());
		getToolBar().add(getBtnSave());
	
		getToolBar().add(getBtnCut());
		getToolBar().add(getBtnCopy());
		getToolBar().add(getBtnPaste());
	
		getToolBar().add(getBtnUndo());
		getToolBar().add(getBtnRedo());
		getToolBar().add(getPanelCombos());
		getToolBar().add(getTbtnBold());
		getToolBar().add(getTbtnItalic());
		getToolBar().add(getTbtnUnderline());
		getToolBar().add(new JSeparator(JSeparator.VERTICAL));
		getToolBar().add(getColorButtonForeground());
		getToolBar().add(new JSeparator(JSeparator.VERTICAL));
		getToolBar().add(getColorButtonBackground());
	}
}
