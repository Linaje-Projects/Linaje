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

import java.awt.Font;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

import javax.swing.plaf.UIResource;

import linaje.utils.LFont;

@SuppressWarnings("serial")
public class LFontUIResource extends LFont implements UIResource {

	public LFontUIResource(Map<? extends Attribute, ?> attributes) {
		super(attributes);
	}

	public LFontUIResource(Font font) {
		super(font);
	}

	public LFontUIResource(String name, int style, int size) {
		super(name, style, size);
	}

	public LFontUIResource(LFont lfont) {
		super(lfont);
	}

	public LFontUIResource(Map<? extends Attribute, ?> attributes, int textHeightLayoutMode) {
		super(attributes, textHeightLayoutMode);
	}

	public LFontUIResource(Font font, int textHeightLayoutMode) {
		super(font, textHeightLayoutMode);
	}

	public LFontUIResource(String name, int style, int size, int textHeightLayoutMode) {
		super(name, style, size, textHeightLayoutMode);
	}
}
