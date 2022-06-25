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
package linaje.gui.ui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.swing.plaf.UIResource;

import linaje.utils.ReferencedColor;

@SuppressWarnings("serial")
public class ReferencedColorUIResource extends ReferencedColor implements UIResource {

	public ReferencedColorUIResource(String encodedUIColor)
			throws NoSuchFieldException, IllegalAccessException, SecurityException, InvocationTargetException {
		super(encodedUIColor);
	}

	public ReferencedColorUIResource(Object source, Field[] colorFieldTree, double luminanceFactor)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		super(source, colorFieldTree, luminanceFactor);
	}

	public ReferencedColorUIResource(Object source, String path) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(source, path);
	}

	public ReferencedColorUIResource(Object source, String path, double luminanceFactor) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super(source, path, luminanceFactor);
	}
	
	public ReferencedColorUIResource(ReferencedColor referencedColor)
			throws NoSuchFieldException, IllegalAccessException, SecurityException, InvocationTargetException {
		super(referencedColor.getSource(), referencedColor.getColorFieldTree(), referencedColor.getLuminanceFactor());
	}
}
