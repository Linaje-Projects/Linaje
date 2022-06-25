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
package linaje.gui.renderers;

import java.awt.Rectangle;
import java.util.List;

public interface ActionsRenderer {

	/**
	 * Sirve para poder crear renders complejos en los que al pinchar o pasar el ratón en determinadas coordenadas, donde se pinte algún tipo de botón o algo así, lance alguna acción en lugar de seleccionar la celda
	 * 
	 * Aquí hay que difnir las coordenadas de cada elemento de la celda sobre el que queramos hacer una acción
	 * Si usamos una LTable o una LList nos dirá por que acción se está pasando el ratón en ese momento
	 **/
	abstract List<Rectangle> getActionsRects();
	
}
