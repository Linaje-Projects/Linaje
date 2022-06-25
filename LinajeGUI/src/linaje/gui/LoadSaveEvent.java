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

import java.io.File;
import java.util.EventObject;

@SuppressWarnings("serial")
public class LoadSaveEvent extends EventObject {
	
	public static final int TYPE_LOAD = 0;
	public static final int TYPE_SAVE = 1;
	public static final int TYPE_CLOSE = 2;
	
	private File file = null;
	int type = -1;
	
	public LoadSaveEvent(Object source, File file, int type) {
		super(source);
		this.file = file;
		this.type = type;
	}

	public File getFile() {
		return file;
	}
	public int getType() {
		return type;
	}
}
