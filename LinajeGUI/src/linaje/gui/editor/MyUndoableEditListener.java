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

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import linaje.logs.Console;

/**
 * This type was created in VisualAge.
 */
public class MyUndoableEditListener implements UndoableEditListener {
	
	protected UndoAction undoAction;
	protected RedoAction redoAction;
	protected UndoManager undo;
	
	public MyUndoableEditListener(UndoAction ua, RedoAction ra, UndoManager u) {
		super();
		undoAction = ua;
		redoAction = ra;
		undo = u;
	}
	
	public void clean() {
		try{
			if (undo!=null){
				undo.discardAllEdits();
				undoAction.updateUndoState();
				redoAction.updateRedoState();
			}
		}catch(Exception e){
			Console.printException(e);
		}
	}
	
	public void undoableEditHappened(UndoableEditEvent e) {
		//Remember the edit and update the menus.
		undo.addEdit(e.getEdit());
		undoAction.updateUndoState();
		redoAction.updateRedoState();
	}
}
