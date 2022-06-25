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
package linaje.gui.tests;

import java.lang.reflect.Field;

import linaje.gui.components.LColorChooser;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.logs.Console;
import linaje.utils.ReferencedColor;

public class TestUIColorChooserPanel {

	public static void main(String[] args) {
		try {
			LinajeLookAndFeel.init();
			LColorChooser.showDialog(null, "Color", new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorRollover)}, 0.27));
			System.exit(0);		
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
