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
package linaje.gui.utils;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;

import javax.swing.JDialog;

import linaje.LocalizedStrings;
import linaje.gui.AppGUI;
import linaje.statics.Directories;
import linaje.utils.Files;

public class FilesGUI extends Files {

	public static int MODE_LOAD = FileDialog.LOAD;
	public static int MODE_SAVE = FileDialog.SAVE;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String load;
		public String save;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static File chooseFile(int mode, File defaultFile) {
		return chooseFile(mode, defaultFile, (Window) null);
	}
	public static File chooseFile(int mode, File defaultFile, JDialog parentDialog) {
		return chooseFile(mode, defaultFile, parentDialog);
	}
	public static File chooseFile(int mode, File defaultFile, Frame parentFrame) {
		return chooseFile(mode, defaultFile, parentFrame);
	}
	public static File chooseFile(int mode, File defaultFile, Window parentWindow) {
		
		if (parentWindow == null)
			parentWindow = AppGUI.getCurrentAppGUI().getFrame();
		
		String title = mode == MODE_LOAD ? TEXTS.load : TEXTS.save;
		String fileName = defaultFile != null && !defaultFile.isDirectory() ? defaultFile.getName() : null;
		String fileDir = defaultFile != null ? defaultFile.isDirectory() ? defaultFile.getAbsolutePath() : defaultFile.getParentFile().getAbsolutePath() : Directories.SYSTEM_USER_DOCUMENTS_DIR.getAbsolutePath();
		
		FileDialog fileDialog;
		if (parentWindow instanceof JDialog)
			fileDialog = new FileDialog((JDialog) parentWindow, title, mode);
		else
			fileDialog = new FileDialog((Frame) parentWindow, title, mode);
			
		fileDialog.setFile(fileName);
		fileDialog.setDirectory(fileDir);
		fileDialog.setVisible(true);
		
		File file = fileDialog.getDirectory() != null && fileDialog.getFile() != null ? new File(fileDialog.getDirectory(), fileDialog.getFile()) : null;
		
		return file;
	}
}
