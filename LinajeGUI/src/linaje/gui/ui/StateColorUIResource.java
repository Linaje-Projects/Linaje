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

import java.awt.Color;

import javax.swing.plaf.UIResource;

import linaje.utils.StateColor;

@SuppressWarnings("serial")
public class StateColorUIResource extends StateColor implements UIResource {

	public StateColorUIResource() {
		super();
	}

	public StateColorUIResource(Color defaultColor) {
		super(defaultColor);
	}

	public StateColorUIResource(Color defaultColor, Color selectedColor, Color rolloverColor) {
		super(defaultColor, selectedColor, rolloverColor);
	}

	public StateColorUIResource(Color defaultColor, Color disabledColor, Color selectedColor, Color rolloverColor,
			Color rolloverSelectedColor, Color pressedColor, Color pressedSelectedColor, Color disabledSelectedColor) {
		super(defaultColor, disabledColor, selectedColor, rolloverColor, rolloverSelectedColor, pressedColor,
				pressedSelectedColor, disabledSelectedColor);
	}

	public StateColorUIResource(Color defaultColor, Color disabledColor, Color selectedColor, Color rolloverColor,
			Color rolloverSelectedColor, Color pressedColor, Color pressedSelectedColor, Color disabledSelectedColor,
			int mode) {
		super(defaultColor, disabledColor, selectedColor, rolloverColor, rolloverSelectedColor, pressedColor,
				pressedSelectedColor, disabledSelectedColor, mode);
	}

	public StateColorUIResource(Color baseColor, boolean brighterStateColors) {
		super(baseColor, brighterStateColors);
	}

	public StateColorUIResource(Color baseColor, boolean brighterStateColors, float factor) {
		super(baseColor, brighterStateColors, factor);
	}

	public StateColorUIResource(StateColor stateColor, Color newMainColor) {
		super(stateColor, newMainColor);
	}
	
	public StateColorUIResource(StateColor stateColor) {
		super(stateColor, null);
	}
}
