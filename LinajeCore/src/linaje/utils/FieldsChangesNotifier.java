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
package linaje.utils;

/**
 * Debe implementar esta interfaz aquella clase que queramos monitorizar el cambio de valor de sus campos.
 * Implementaremos un PropertyChangeListener sobre la instancia de FieldsChangeSupport 
 * a través de addChangeLister, removeChangeListener, firePropertyChange y resetDefaultFieldsValues 
 * 
 * @see FieldsChangeSupport
 **/
public interface FieldsChangesNotifier {

	public FieldsChangeSupport getFieldsChangeSupport();
}
