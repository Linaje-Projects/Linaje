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

import java.awt.Component;

import javax.swing.Popup;
import javax.swing.PopupFactory;

public class TranslucentPopupFactory extends PopupFactory {

	private final PopupFactory storedFactory;

    private TranslucentPopupFactory(PopupFactory storedFactory) {
        this.storedFactory = storedFactory;
    }

    public static void install() {
        
        PopupFactory factory = PopupFactory.getSharedInstance();
        if (factory instanceof TranslucentPopupFactory)
            return;

        PopupFactory.setSharedInstance(new TranslucentPopupFactory(factory));
    }

    public static void uninstall() {
        PopupFactory factory = PopupFactory.getSharedInstance();
        if (!(factory instanceof TranslucentPopupFactory))
            return;

        PopupFactory stored = ((TranslucentPopupFactory) factory).storedFactory;
        PopupFactory.setSharedInstance(stored);
    }

    public Popup getPopup(Component owner, Component contents, int x, int y)
            throws IllegalArgumentException {
        Popup popup = super.getPopup(owner, contents, x, y);
        return TranslucentPopup.getInstance(owner, contents, x, y, popup);
    }
}
