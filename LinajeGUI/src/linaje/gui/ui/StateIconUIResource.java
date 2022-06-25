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

import javax.swing.ImageIcon;
import javax.swing.plaf.UIResource;

import linaje.gui.StateIcon;
import linaje.utils.StateColor;

@SuppressWarnings("serial")
public class StateIconUIResource extends StateIcon implements UIResource{

	public StateIconUIResource(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon selectedIcon,
			ImageIcon rolloverIcon, ImageIcon pressedIcon) {
		super(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, pressedIcon);
	}

	public StateIconUIResource(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon selectedIcon,
			ImageIcon rolloverIcon, ImageIcon rolloverSelectedIcon, ImageIcon pressedIcon,
			ImageIcon pressedSelectedIcon, ImageIcon disabledSelectedIcon) {
		super(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon,
				pressedSelectedIcon, disabledSelectedIcon);
	}

	public StateIconUIResource(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon selectedIcon,
			ImageIcon rolloverIcon, ImageIcon rolloverSelectedIcon, ImageIcon pressedIcon,
			ImageIcon pressedSelectedIcon, ImageIcon disabledSelectedIcon, int mode) {
		super(defaultIcon, disabledIcon, selectedIcon, rolloverIcon, rolloverSelectedIcon, pressedIcon,
				pressedSelectedIcon, disabledSelectedIcon, mode);
	}

	public StateIconUIResource(ImageIcon baseIcon, Color baseColor, boolean brighterStateIcons) {
		super(baseIcon, baseColor, brighterStateIcons);
	}

	public StateIconUIResource(ImageIcon baseIcon, Color baseColor, boolean brighterStateIcons, float factor) {
		super(baseIcon, baseColor, brighterStateIcons, factor);
	}

	public StateIconUIResource(ImageIcon baseIcon, StateColor stateColor) {
		super(baseIcon, stateColor);
	}

	public StateIconUIResource(ImageIcon baseIcon, StateColor stateColor, boolean colorizeDefaultIcon) {
		super(baseIcon, stateColor, colorizeDefaultIcon);
	}

	public StateIconUIResource(StateIcon stateIcon, ImageIcon newMainIcon) {
		super(stateIcon, newMainIcon);
	}

	public StateIconUIResource(StateIcon stateIcon) {
		super(stateIcon, (ImageIcon) null);
	}
}
