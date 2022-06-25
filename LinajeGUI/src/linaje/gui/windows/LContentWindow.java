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
package linaje.gui.windows;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Usamos un JDialog Undecorated ya que si usabamos un JWindow
 * seguían pintandose mal el antialiasing de los componentes
 * cuando el padre del JWindow era una ventana transparente (Como es nuestro caso)
 */
@SuppressWarnings("serial")
public class LContentWindow extends JDialog {

	private Window windowOwner = null;
	private JComponent borderContainer = null;
		
	private ComponentListener componentListener = new ComponentListener() {
		
		public void componentShown(ComponentEvent e) {
			setVisible(true);
			//Se han dado casos en los que si se mostraba el díalógo mientras se manipulaba el Frame padre
			//se mostraba con size negativo, por lo que nos aseguramos que al mostrarse tiene el size adecuado
			if (getWidth() <= 0 || getHeight() <= 0)
				adjustSize();
		}
		public void componentHidden(ComponentEvent e) {
			setVisible(false);
		}
		public void componentMoved(ComponentEvent e) {
			adjustLocation();
		}
		public void componentResized(ComponentEvent e) {
			adjustSize();
		}
	};
	
	public LContentWindow(LWindowComponents windowComponents) {
		//this(windowComponents.getWindow(), windowComponents.getWindowElementsPanel(), windowComponents.getLWindowInsets());
		this(windowComponents.getWindow(),
			windowComponents.getWindowElementsPanel(),
			windowComponents.getWindowBorderPanel());
	}
	
	public LContentWindow(Window windowOwner, Container contentPane, JComponent borderContainer) {
		super(windowOwner != null && (windowOwner instanceof Dialog || windowOwner instanceof Frame) ? windowOwner : null);
		setModal(false);
		setUndecorated(true);
		this.windowOwner = windowOwner;
		this.borderContainer = borderContainer;
		setLayout(new BorderLayout());
		setContentPane(contentPane);
		adjustLocation();
		adjustSize();
		windowOwner.addComponentListener(componentListener);
		if (windowOwner.isVisible())
			setVisible(true);
	}

	private void adjustLocation() {
		
		Point locationOwner = windowOwner.getLocation();
		Insets insets = borderContainer.getInsets();
		Point location = new Point(locationOwner.x + insets.left, locationOwner.y + insets.top);
		setLocation(location);
	}
	private void adjustSize() {
		Dimension sizeOwner = windowOwner.getSize();
		Insets insets = borderContainer.getInsets();
		Dimension size = new Dimension(sizeOwner.width - insets.left - insets.right, sizeOwner.height - insets.top - insets.bottom);
		setSize(size);
	}	
	
	public void finalize() throws Throwable {
		removeAll();
		if (windowOwner != null) {
			windowOwner.removeComponentListener(componentListener);
			windowOwner = null;
		}
		if (isVisible())
			dispose();
		super.finalize();
	}
}
