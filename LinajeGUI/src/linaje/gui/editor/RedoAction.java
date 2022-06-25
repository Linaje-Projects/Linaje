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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import linaje.logs.Console;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class RedoAction extends AbstractAction {
	
	protected UndoAction undoAction;
	protected UndoManager undo;
	
	public RedoAction(UndoAction ua, UndoManager u) {
		super("Redo");
		setEnabled(false);
		undoAction = ua;
		undo = u;
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			undo.redo();
		} catch (CannotRedoException ex) {
			Console.println("Unable to redo: " + ex, Console.TYPE_DATA_ERROR);
			Console.printException(ex);
		}
		updateRedoState();
		undoAction.updateUndoState();
	}
	
	protected void updateRedoState() {
		setEnabled(undo.canRedo());
	}
}
