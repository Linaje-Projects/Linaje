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

import java.awt.Color;
import java.awt.image.RGBImageFilter;

import linaje.utils.Colors;

/**
 * Filtro usado por Icons.createColorizedImage(...) para colorizar imágenes e iconos 
 **/
public class ColorizedImageFilter extends RGBImageFilter {

	private Color filterColor = null;
	private float luminance = 0;
	
    public ColorizedImageFilter(Color filterColor, boolean optimizeNonPlainImages) {
    	this.filterColor = filterColor;
    	if (filterColor != null) {
    		this.luminance = Colors.getLuminance(filterColor);
    		if (optimizeNonPlainImages && luminance > 0.5) {
    			//No dejamos que el color del filtro sea demasiado claro, para que se pueda distinguir algún contraste entre colores muy oscuros y muy claros
    			//Si nuestras imagenes son planas (de un sólo color) , es mejor no usar esto para que se ajuste al color final
    			this.filterColor = Colors.darker(filterColor, 0.12);
    		}
    	}
    }

	public int filterRGB(int x, int y, int rgb) {

		if (filterColor == null)
			return rgb;
		
		Color sourceColor = new Color(rgb, true);
		
		Color newColor;
		if (filterColor != null) {
			newColor = Colors.colorize(sourceColor, filterColor, true);
			if (luminance < 0.5f) {
				//Oscurecemos el color a filtrar proporcionalmente a la luminosidad del color del filtro
				float factor = 0.5f - luminance;
				newColor = Colors.darker(newColor, factor);
			}
		}
		else {
			newColor = sourceColor;
		}
		
		return newColor.getRGB();
    }
}
