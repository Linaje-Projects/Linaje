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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import linaje.gui.AppGUI;
import linaje.gui.LPanel;
import linaje.gui.RoundedBorder;
import linaje.gui.utils.UtilsGUI;


@SuppressWarnings("serial")
public class LWindow extends LPanel {

	private JWindow window = null;
	private JWindow windowForContent = null;
	private JPanel contentPane = null;
	private Window frameOwner = null;
	private JComponent ancestor = null;
	
	private RoundedBorder windowBorder = null;
	
	private AncestorListener ancestorListener = new AncestorListener() {
		
		public void ancestorRemoved(AncestorEvent event) {
			closeWindow();
		}
		public void ancestorMoved(AncestorEvent event) {
		}
		public void ancestorAdded(AncestorEvent event) {
		}
	};
	
	private ComponentListener componentListener = new ComponentListener() {
		
		public void componentShown(ComponentEvent e) {
			getWindowForContent().setVisible(true);
		}
		public void componentHidden(ComponentEvent e) {
			getWindowForContent().setVisible(false);
		}
		public void componentMoved(ComponentEvent e) {
			adjustWindowForContentLocation();
		}
		public void componentResized(ComponentEvent e) {
			adjustWindowForContentSize();
		}
	};
	
	private class ContentPane extends LPanel {
		
		public ContentPane(LayoutManager layout) {
			super(layout);
		}
		
		@Override
		public Color getBackground() {
			// Cogemos el background de la LWindow para que se pinte bien el borde
			return LWindow.this.getBackground();
		}
		@Override
		public boolean isOpaque() {
			//Siempre será transparente para que se pinte la sombra transparente
			return false;
		}
		
	}
	
	public LWindow() {
		this(null, AppGUI.getCurrentAppGUI().getFrame());
	}
	public LWindow(JComponent ancestor) {
		this(ancestor, AppGUI.getCurrentAppGUI().getFrame());
	}
	public LWindow(Window frameOwner) {
		this(null, frameOwner);
	}
	public LWindow(JComponent ancestor, Window frameOwner) {
		super();
		setFrameOwner(frameOwner);
		setAncestor(ancestor);
	}

	private void adjustWindowForContentLocation() {
		
		Point location = getWindow().getLocation();
		Insets borderInsets = getContentPane().getBorder().getBorderInsets(getContentPane());
		Point contentLocation = new Point(location.x + borderInsets.left, location.y+borderInsets.top);
		getWindowForContent().setLocation(contentLocation);
	}
	private void adjustWindowForContentSize() {
		Dimension size = getWindow().getSize();
		Insets borderInsets = getContentPane().getBorder().getBorderInsets(getContentPane());
		Dimension contentSize = new Dimension(size.width - borderInsets.left - borderInsets.right, size.height - borderInsets.top - borderInsets.bottom);
		getWindowForContent().setSize(contentSize);
	}
	public void adjustWindowSize() {
		Insets borderInsets = getContentPane().getBorder().getBorderInsets(getContentPane());
		int width = getWidth() + borderInsets.left + borderInsets.right;
		int height = getHeight() + borderInsets.top + borderInsets.bottom;
		Dimension size = new Dimension(width, height);
		getWindow().setSize(size);
		
	}
		
	public void destroy() {
		
		destroyWindows();
		setAncestor(null);
		
		window = null;
		windowForContent = null;
		contentPane = null;
		frameOwner = null;
		ancestor = null;
	}
	
	private void destroyWindows() {
		
		closeWindow();
		if (window != null) {
			window.removeComponentListener(componentListener);
			window = null;
		}
		windowForContent = null;
	}
	
	public void showWindow() {
		showWindow(null);
	}
	public void showWindow(Point windowLocation) {

		if (windowLocation == null)
			windowLocation = getWindowLocation();
		
		adjustWindowSize();
		
		Point adjustedLocation = UtilsGUI.getWindowLocationAdjusted(getWindow(), windowLocation);
		setWindowLocation(adjustedLocation);
		
		adjustWindowForContentLocation();
		adjustWindowForContentSize();
		
		getWindow().validate();
		getWindow().setVisible(true);
	}
	
	public void closeWindow() {
		if (window != null && window.isVisible()) {
			getWindow().invalidate();
			getWindow().dispose();
		}
	}
	
	public JPanel getContentPane() {

		if (contentPane == null) {

			contentPane = new ContentPane(new BorderLayout());
			contentPane.setBorder(getWindowBorder());
		}
		return contentPane;
	}
	
	public JWindow getWindow() {
		if (window == null) {
			window = new JWindow(getFrameOwner());
			//UtilsGUI.setWindowOpacity(window, 0.0f);
			UtilsGUI.setWindowOpaque(window, false);
			//UtilsGUI.setWindowOpacity(window, (float) 0.9);
			window.setLayout(new BorderLayout());
			window.add(getContentPane(), BorderLayout.CENTER);
			window.addComponentListener(componentListener);
		}
		return window;
	}
	
	private JWindow getWindowForContent() {
		if (windowForContent == null) {
			windowForContent = new JWindow(getWindow());
			windowForContent.setLayout(new BorderLayout());
			windowForContent.add(this, BorderLayout.CENTER);
		}
		return windowForContent;
	}

	public Point getWindowLocation() {
		return getWindow().getLocation();
	}
	public void setWindowLocation(Point location) {
		getWindow().setLocation(location);
	}
	
	public boolean isVisibleWindow() {
		return getWindow().isVisible();
	}
	
	public JComponent getAncestor() {
		return ancestor;
	}
	public void setAncestor(JComponent ancestor) {
		
		if (this.ancestor != null)
			this.ancestor.removeAncestorListener(ancestorListener);
		
		this.ancestor = ancestor;
		
		if (ancestor != null) {
			ancestor.addAncestorListener(ancestorListener);
			
			if (getFrameOwner() == null) {
				
				Window windowOwner = SwingUtilities.windowForComponent(ancestor);
				if (windowOwner != null)
					setFrameOwner(windowOwner);
			}
		}
	}
	
	public Window getFrameOwner() {
		return frameOwner;
	}
	private void setFrameOwner(Window newFrameOwner) {
		
		Window oldFrameOwner = this.frameOwner;
		this.frameOwner = newFrameOwner;
		
		if (oldFrameOwner != null && oldFrameOwner != newFrameOwner && window != null) {
			//Si cambia el frameOwner destruimos las ventanas para que se creen con el nuevo frameOwner
			destroyWindows();
		}
	}

	public RoundedBorder getWindowBorder() {
		if (windowBorder == null) {
			windowBorder = new RoundedBorder();
			windowBorder.setPaintInsideAlways(true);
			windowBorder.setCornersCurveSize(new Dimension(6, 6));
		}
		return windowBorder;
	}
	
	public void setBorderColor(Color borderColor) {
		getWindowBorder().setLineBorderColor(borderColor);
	}
}
