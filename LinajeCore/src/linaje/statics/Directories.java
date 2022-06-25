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
package linaje.statics;

import java.io.*;

import linaje.App;
import linaje.utils.Security;
 
public final class Directories {

	public static final String USER_NAME = Security.getSystemProperty(Security.KEY_USER_NAME);
	
	public static final File APP_EXECUTION_DIR = new File(Security.getSystemProperty(Security.KEY_USER_DIR));
	public static final File SYSTEM_USER_DIR = new File(Security.getSystemProperty(Security.KEY_USER_HOME));
	public static final File SYSTEM_USER_DOCUMENTS_DIR = new File(SYSTEM_USER_DIR, "Documents");
	public static final File SYSTEM_USER_DOWNLOADS_DIR = new File(SYSTEM_USER_DIR, "Downloads");
	public static final File SYSTEM_TEMP_DIR = new File(Security.getSystemProperty(Security.KEY_JAVA_IO_TMPDIR), "Linaje");

	private static File appGeneratedFiles = null;
		 
	public static File getAppGeneratedFiles() {
		if (appGeneratedFiles == null)
			appGeneratedFiles = new File(SYSTEM_TEMP_DIR, App.getCurrentApp().getName());
		return appGeneratedFiles;
	}
	
	public static void setAppGeneratedFiles(File appGeneratedFiles) {
		Directories.appGeneratedFiles = appGeneratedFiles;
	}
}
