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
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import linaje.logs.Console;
 
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class UndoAction extends AbstractAction {
	
	protected RedoAction redoAction;
	protected UndoManager undo;

	public UndoAction(RedoAction ra, UndoManager u) {
		super("Undo");
		setEnabled(false);
		redoAction = ra;
		undo = u;
	}
	public void actionPerformed(ActionEvent e) {
		try {
			undo.undo();
		} catch (CannotUndoException ex) {
			Console.println("Unable to undo: " + ex, Console.TYPE_DATA_ERROR);
			Console.printException(ex);
		}
		updateUndoState();
		redoAction.updateRedoState();
	}
	
	protected void updateUndoState() {
		setEnabled(undo.canUndo());
	}
}
