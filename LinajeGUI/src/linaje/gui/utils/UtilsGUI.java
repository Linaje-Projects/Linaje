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
package linaje.gui.utils;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import linaje.gui.AppGUI;
import linaje.gui.LPanel;
import linaje.gui.LTitledBorder;
import linaje.gui.RoundedBorder;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.Strings;
import linaje.utils.Utils;

/**
 * Muchos de los métodos que hay aquí se utilizaban para pintar los componentes y están cayendo en desuso por los que se utilizan en UISupport
 * Todavía se pintan algunos componentes con éstos métodos, como los LMenuItems
 * 
 **/
public class UtilsGUI extends Utils {

	public static Point getWindowLocationAdjusted(Window window, Point location) {
		
		Point locationAjustado = new Point(location.x, location.y);
		
		Rectangle allScreenslBounds = new Rectangle();
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = environment.getScreenDevices();
		if (screens.length > 1) {
			//Si tenemos mas de una pantalla calculamos el tamaño total de todas las pantallas
			for (GraphicsDevice screen : screens) {
				Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
				if (screenBounds.x >= allScreenslBounds.x)
					allScreenslBounds.width += screenBounds.x + screenBounds.width - (allScreenslBounds.x + allScreenslBounds.width);
				else
					allScreenslBounds.width += allScreenslBounds.x - screenBounds.x + Math.max(screenBounds.width - allScreenslBounds.width - (allScreenslBounds.x - screenBounds.x), 0);
				
				if (screenBounds.y >= allScreenslBounds.y)
					allScreenslBounds.height += screenBounds.y + screenBounds.height - (allScreenslBounds.y + allScreenslBounds.height);
				else
					allScreenslBounds.height += allScreenslBounds.y - screenBounds.y + Math.max(screenBounds.height - allScreenslBounds.height - (allScreenslBounds.y - screenBounds.y), 0);
				
				allScreenslBounds.x = Math.min(allScreenslBounds.x, screenBounds.x);
				allScreenslBounds.y = Math.min(allScreenslBounds.y, screenBounds.y);
			}
		}
		else {
			allScreenslBounds = new Rectangle(new Point(0,0), Toolkit.getDefaultToolkit().getScreenSize());
		}
		
		if (locationAjustado.x - allScreenslBounds.x + window.getWidth() > allScreenslBounds.width)
			locationAjustado.x = allScreenslBounds.x + allScreenslBounds.width - window.getWidth();
		if (locationAjustado.y - allScreenslBounds.y + window.getHeight() > allScreenslBounds.height)
			locationAjustado.y = allScreenslBounds.y + allScreenslBounds.height - window.getHeight();
		if (locationAjustado.x < allScreenslBounds.x)
			locationAjustado.x = allScreenslBounds.x;
		if (locationAjustado.y < allScreenslBounds.y)
			locationAjustado.y = allScreenslBounds.y;
		
		return locationAjustado;
	}
	
	public static void centerWindow(Window window) {
		
		//Tamaño de la ventana
		Dimension windowSize = window.getSize();
		int vW = windowSize.width;
		int vH = windowSize.height;
	
		boolean centered = false;
		if (window.getOwner() != null && window.getOwner().isVisible()) {
			
			//Tamaño de la ventana dueña
			Dimension ownerSize = window.getOwner().getSize();
			int oW = ownerSize.width;
			int oH = ownerSize.height;
	
			//Posición de la ventana dueña
			Point ownerLocation = window.getOwner().getLocation();
			int oX = ownerLocation.x;
			int oY = ownerLocation.y;
			
			if (oW != 0 || oH != 0 || oX != 0 || oY != 0) {
				//Centramos la ventana respecto al padre
				window.setLocation(((oW-vW)/2) + oX, ((oH-vH)/2) + oY);
				centered = true;
			}
		}
	
		if (!centered) {
	
			//Tamaño de la pantalla
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int pW = screenSize.width;
			int pH = screenSize.height;
	
			//Centramos la ventana respecto a la pantalla
			window.setLocation((pW-vW)/2,(pH-vH)/2);
		}
	}
	
	public static int getStringWidth(String text, Font font, Graphics g) {
		if (text != null && font != null && g != null) {
			FontMetrics fontMetrics = g.getFontMetrics(font);
			return getStringWidth(text, fontMetrics);
		}
		else
			return 0;
	}
	
	public static int getStringWidth(String text, Font font, Component component) {
		if (text != null && font != null && component != null) {
			FontMetrics fontMetrics = component.getFontMetrics(font);
			return getStringWidth(text, fontMetrics);
		}
		else
			return 0;
	}
	
	public static int getStringWidth(String text, FontMetrics fontMetrics) {
		if (text != null && fontMetrics != null)
			return Strings.getStringWidth(text, fontMetrics);
		else
			return 0;
	}
	
	/**
	 * Este método devuelve el padre del componente que sea del tipo clase
	 */
	public static Container getParent(Component component, Class<?> cl) {

		Container parent = component.getParent();
		while (parent != null && parent.getClass() != cl) {
			parent = parent.getParent();	
		}
		return parent;	
	}
	/**
	 * Este método devuelve el padre del componente que sea del tipo clase
	 */
	public static Container getParentInstanceOf(Component component, Class<?> cl) {

		Container parent = component.getParent();
		while (parent != null && !cl.isInstance(parent)) {
			parent = parent.getParent();	
		}
		return parent;		
	}
	
	public static void setFontName(Component component, String fontName) {
		component.setFont(getFontWithName(component.getFont(), fontName));
	}
	public static void setFontSize(Component component, int fontSize) {
		component.setFont(getFontWithSize(component.getFont(), fontSize));
	}
	public static void setFontStyle(Component component, int fontStyle) {
		component.setFont(getFontWithStyle(component.getFont(), fontStyle));
	}
	public static void setFontLayout(Component component, int fontLayout) {
		component.setFont(getFontWithLayout(component.getFont(), fontLayout));
	}
	
	public static LTitledBorder setTitledBorder(JComponent component, String title) {
		return setTitledBorder(component, title, 4);
	}
	public static LTitledBorder setTitledBorder(JComponent component, String title, int internalMargin) {
		RoundedBorder border = new RoundedBorder();
		border.setThicknessShadow(0);
		LTitledBorder titledBorder = getTitledBorder(border, title, getFontWithStyle(GeneralUIProperties.getInstance().getFontApp(), Font.BOLD));
		titledBorder.setInternalMargin(internalMargin);
		//border.setLineBorderColor(titledBorder.getTitleColor());
		component.setBorder(titledBorder);
		return titledBorder;
	}
	public static LTitledBorder getTitledBorder(Border border, String title, Font font) {
		
		if (border == null)
			border = BorderFactory.createEmptyBorder();
		if (title == null)
			title = Constants.VOID;
		
		LTitledBorder titledBorder = new LTitledBorder(border, title);
		titledBorder.setTitleFont(font);
		titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
		
		return titledBorder;
	}
	
	private static int[] getAllColumnsIndices(JTable table) {
		TableColumnModel columnModel = table.getColumnModel();
		int[] columns = new int[columnModel.getColumnCount()];
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columns[i] = i;
		}
		return columns;
	}
	
	private static int[] getAllRowIndices(JTable table) {
		int[] rows = new int[table.getRowCount()];
		for (int i = 0; i < table.getRowCount(); i++) {
			rows[i] = i;
		}
		return rows;
	}
	
	public static Rectangle getSelectionRects(JTable table) {
		
		boolean columnSelection = !table.getRowSelectionAllowed() && table.getColumnSelectionAllowed();
		boolean rowSelection = table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed();
		
		int[] selectedRows = columnSelection ? getAllRowIndices(table) : table.getSelectedRows();
		int[] selectedColumns = rowSelection ? getAllColumnsIndices(table) : table.getSelectedColumns();
		
		if (selectedRows.length > 0 && selectedColumns.length > 0) {
			Rectangle firstCellRects = table.getCellRect(selectedRows[0], selectedColumns[0], true);
			Rectangle lastCellRects = firstCellRects;
			if (selectedRows.length > 1 || selectedColumns.length > 1) {
				lastCellRects = table.getCellRect(selectedRows[selectedRows.length-1], selectedColumns[selectedColumns.length-1], true);
			}
			
			return new Rectangle(firstCellRects.x, firstCellRects.y, lastCellRects.x + lastCellRects.width - firstCellRects.x, lastCellRects.y + lastCellRects.height - firstCellRects.y);
		}
		return null;
	}
	
	public static Rectangle getSelectionRects(JList<?> list) {
		
		int[] selectedIndices = list.getSelectedIndices();
		
		if (selectedIndices.length > 0) {
			return list.getCellBounds(selectedIndices[0], selectedIndices[selectedIndices.length-1]);
		}
		return null;
	}
	
	public static Rectangle getSelectionRects(JTree tree) {
		
		int[] selectedIndices = tree.getSelectionRows();
		
		if (selectedIndices.length > 0) {
			return tree.getPathBounds(tree.getSelectionPath());
		}
		return null;
	}
	
	public static void showDialogDarkenOwner(JDialog dialog) {
		
		Component oldGlassPane = null;
		RootPaneContainer rpContainer = null;
		if (dialog.isModal()) {
			
			Window ownerWindow = dialog.getOwner();
			if (ownerWindow != null && ownerWindow instanceof RootPaneContainer) {
				
				setWindowOpacity(ownerWindow, 0.5f);
				
				rpContainer = (RootPaneContainer) ownerWindow;
				oldGlassPane = rpContainer.getGlassPane();
				LPanel newGlassPane = new LPanel();
				newGlassPane.setOpacity(0.5f);
				
				rpContainer.setGlassPane(newGlassPane);
				ownerWindow.validate();
				ownerWindow.repaint();
			}
		}
		
		dialog.setVisible(true);
		
		if (rpContainer != null) {
			rpContainer.setGlassPane(oldGlassPane);
			setWindowOpacity(dialog.getOwner(), 1f);
		}
	}
	
	public static void quickMain(Class<? extends JComponent> classToInstance) {
		
		try {
			
			LinajeLookAndFeel.init();
			
			Class<?>[] parameterTypes = null;
			Object[] parameterValues = null;
			JComponent component = ReflectAccessSupport.newInstance(classToInstance, parameterTypes, parameterValues);
			
			AppGUI.getCurrentApp().setName(component.getName());
			
			LDialogContent.showComponentInFrame(component);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public static float getWindowOpacity(Window window) {
		return window.getOpacity();
		//return AWTUtilities.getWindowOpacity(window);
	}
	
	public static void setWindowOpacity(Window window, float opacity) {
		window.setOpacity(opacity);
		//AWTUtilities.setWindowOpacity(window, opacity);
	}
	
	public static boolean isWindowOpaque(Window window) {
		return window.isOpaque();
		//return AWTUtilities.isWindowOpaque(window);
	}
	
	public static void setWindowOpaque(Window window, boolean opaque) {
		 Color bg = window.getBackground();
         if (bg == null) {
             bg = new Color(0, 0, 0, 0);
         }
         window.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), opaque ? 255 : 0));
		//AWTUtilities.setWindowOpaque(window, opaque);
	}
}
