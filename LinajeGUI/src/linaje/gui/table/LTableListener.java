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
package linaje.gui.table;

import java.util.EventListener;

public interface LTableListener<E> extends EventListener {

	/**
	 * Se disparará cuando hagamos click con el ratón en alguna celda de la tabla
	 * @param event
	 */
	void cellClicked(LTableEvent<E> event);
	
	/**
	 * Se disparará cuando hagamos click con el ratón en la cabecera de una columna
	 * @param event
	 */
	void columnClicked(LTableEvent<E> event);
	
	/**
	 * Se disparará cuando cabien las celdas seleccionadas de la tabla
	 */
	void selectionChanged();
}
