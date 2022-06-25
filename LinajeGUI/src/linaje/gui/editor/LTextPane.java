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

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import linaje.logs.Console;
import linaje.utils.Colors;
import linaje.utils.Strings;

/**
 * Insert the type's description here.
 * Creation date: (26/04/2004 15:56:37)
 * @author: Pablo Linaje
 */
@SuppressWarnings("serial")
public class LTextPane extends JTextPane {

	protected UndoManager undoManager = new UndoManager();
	protected UndoAction undoAction = new UndoAction(null, undoManager);
	protected RedoAction redoAction = new RedoAction(null, undoManager);
	private MyUndoableEditListener undoableEditListener = null;

	protected Hashtable<Object, Action> actions = null;
	
	public LTextPane() {
		super();
		initialize();
	}
	
	private void initialize() {
		
		try {
			setContentType("text/html");
			
			createActionTable();
			initParagraphAttributes();
			
			redoAction = new RedoAction(undoAction, undoManager);
			undoAction = new UndoAction(redoAction, undoManager);
			undoAction.redoAction = redoAction;
			redoAction.undoAction = undoAction;
	
			undoableEditListener = new MyUndoableEditListener(undoAction, redoAction, undoManager);
			getStyledDocument().addUndoableEditListener(undoableEditListener);
		}
		catch (Throwable ex){
			handleException(ex);
		}	
	}
	
	private void createActionTable() {
		actions = new Hashtable<>();
		Action[] actionsArray = this.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
	}
	
	private void initParagraphAttributes() {
		
		try {
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setSpaceAbove(attributes, (float)0.0);
			StyleConstants.setSpaceBelow(attributes, (float)0.0);
			setParagraphAttributes(attributes, false);
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	public void changeAlignment(int alignment) {
	
		String actionName;
		if (alignment == StyleConstants.ALIGN_CENTER)
			actionName = "alignCenter";
		else if (alignment == StyleConstants.ALIGN_RIGHT)
			actionName = "alignRight";
		else if (alignment == StyleConstants.ALIGN_JUSTIFIED)
			actionName = "alignJustified";
		else
			actionName = "alignLeft";
			
		Action action = new StyledEditorKit.AlignmentAction(actionName, alignment);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionName));
	}
	
	public void changeBackground(Color background) {
	
		setBackground(background);
	
		//Ajustamos el color de la seleccion de texto según sea el fondo claro u oscuro
		if (Colors.isColorDark(background)) {
			setSelectionColor(SystemColor.control);
			setSelectedTextColor(Color.black);
			setCaretColor(SystemColor.control);
		}
		else {
			setSelectionColor(SystemColor.activeCaption);
			setSelectedTextColor(SystemColor.activeCaptionText);
			setCaretColor(SystemColor.activeCaption);
		}
	}
	
	public void changeForeground(Color foreground) {
		
		if (!getStyledDocument().getForeground(getInputAttributes()).equals(foreground)) {
			String rgb = String.valueOf(foreground.getRGB());
			Action action = new StyledEditorKit.ForegroundAction(rgb, foreground);
			action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, rgb));
		}
	}
	
	public void changeItalic() {
		
		Action action = new StyledEditorKit.ItalicAction();
		String actionName = "Italic";
		action.putValue(Action.NAME, actionName);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionName));
	}
	
	public void changeBold() {
	
		Action action = new StyledEditorKit.BoldAction();
		String actionName = "Bold";
		action.putValue(Action.NAME, actionName);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionName));
	}
	
	public void changeUnderline() {
	
		Action action = new StyledEditorKit.UnderlineAction();
		String actionName = "Underline";
		action.putValue(Action.NAME, actionName);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionName));
	}
	
	public void changeFontName(String fontName) {
		
		Action action = new StyledEditorKit.FontFamilyAction(fontName, fontName);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, fontName));
	}
	
	public void changeFontSize(int fontSize) {
		String fontSizeStr = String.valueOf(fontSize);
		Action action = new StyledEditorKit.FontSizeAction(fontSizeStr, fontSize);
		action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, fontSizeStr));
	}
	
	public void destroy() {
		
		try {
		
			getStyledDocument().removeUndoableEditListener(undoableEditListener);
			
			undoManager = null;
			undoAction = null;
			redoAction = null;
			undoableEditListener = null;
	
			actions = null;	
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	public Action getActionByName(String name) {
		return (Action) (actions.get(name));
	}
	
	public String getTextHtmlBrowserOptimized() {
	
		Color background = getBackground();
		String textHtml = getTextHtml();
		try {
			if (background != null && !background.equals(Color.white)) {
				//Insertamos el color en el fichero html
				textHtml = Strings.replaceFirst(textHtml, "<body", "<body bgcolor=\"#"+Colors.convertColorToHex(background)+"\"");
			}
			//Ponemos el tamaño de letra mas pequeño
			for (int i = 2; i <= 6; i++) {
				textHtml = Strings.replace(textHtml, "size=\""+i+"\"", "size=\""+(i-1)+"\"");
			}
			//Insertamos saltos de linea en parrafos vacíos, ya que sino el navegador los ignora
			textHtml = Strings.replace(textHtml, ">\r\n<", "><br><");
			textHtml = textHtml.replaceAll(">\\s+\r\n<", "><br><");
		} 
		catch (Throwable e) {
			handleException(e);
		}
		return textHtml;
	}
	
	public String getTextHtml() {
		return getText();
	}
	
	public String getTextPlain() {
		try {
			return getDocument().getText(0, getDocument().getLength());
		}
		catch (Throwable ex) {
			handleException(ex);
			return "Error al recuperar el texto del JTextPane";
		}
	}
	
	private void handleException(Throwable exception) {
		Console.printException(exception);
	}
	
	public URL getPage() {
		return null;
	}
	
	public void setPage(String url) {
		getStyledDocument().removeUndoableEditListener(undoableEditListener);
		try {
			super.setPage(url);
		} catch (IOException e) {}
		getStyledDocument().addUndoableEditListener(undoableEditListener);	
	}
}
