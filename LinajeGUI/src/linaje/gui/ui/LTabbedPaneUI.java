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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LButtonProperties;
import linaje.gui.LPanel;
import linaje.gui.LTabbedPane;
import linaje.gui.LTabbedPane.TabProperties;
import linaje.gui.components.Link;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.HeaderPanel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.LFont;
import linaje.utils.Lists;
import linaje.utils.Utils;

public class LTabbedPaneUI extends BasicTabbedPaneUI implements MouseListener, MouseMotionListener {

	//private static final int BUTTON_HEIGHT = 20;
	//private static final int MARGIN_BUTTON_LEFT = 10;
	//private static final int MARGIN_BUTTON_RIGHT = 5;
	//private static final int MARGIN_BUTTON_BOTTOM = 1;
	
	public static final int BORDER_SHADOW_LIGHT_ALL = 0;
	public static final int BORDER_SHADOW_LIGHT_TABS = 1;
	public static final int BORDER_LINE_ALL = 2;
	public static final int BORDER_LINE_TABS = 3;
	public static final int BORDER_NONE = 4;
		
	protected int indexMouseOver = -1;
	protected int indexMousePressed = -1;
	protected int fixedTab = -1;
	protected int oldSelectedindex = -1;
	private boolean adjustTabsWidth = true;
	private boolean selectedAlwaysFixed = true;
	private HashMap<Integer, Integer> hashTabsWidths = null;
	private LTabbedPane lTabbedPane = null;
	
	private LDialogContent dialogTab = null;
	private LButton buttonAux = null; 
	private Link linkAux = null;
	private LButton labelAux = null;
	private LPanel dialogPanelDesignTime = null;
	private JInternalFrame dialogDesignTime = null;
	private LPanel dialogPanelAux = null;
	
	//Dialog Tab manage
	private int normalTabSelectedIndex = 0;
	private boolean processingDialogTab = false;
	//
	
	private int borderType = BORDER_LINE_TABS;//BORDER_SHADOW_LIGHT_ALL;
	private Color lineBorderColor = null;
	private Color selectedTabLineColor = null;
	
	private class LTabbedPaneLayout extends TabbedPaneLayout {
		
		public void layoutContainer(Container parent) {
			
			super.layoutContainer(parent);
			if (isAdjustTabsWidth()) {
				if (tabPane.getSelectedIndex() != oldSelectedindex)
					fixedTab = tabPane.getSelectedIndex();
				hashTabsWidths = recalculateTabsWidths();
				if (hashTabsWidths != null)
					super.layoutContainer(parent);
			}
			
           for (int i = 0; i < tabPane.getTabCount(); i++) {
				
				Component tabComponent = tabPane.getTabComponentAt(i);
				JComponent tlb = tabComponent != null ? (JComponent) tabComponent : null;
				if (tlb != null && tlb.getParent() != null) {
					Rectangle bounds = tlb.getBounds();
					Rectangle boundsTab = getTabBounds(i, new Rectangle());
					Insets tabInsets = getTabInsets(tabPane.getTabPlacement(), i);
					int margen = bounds.x - boundsTab.x;
					if (margen > 0) {
						bounds.width = bounds.width + margen - tabInsets.left - tabInsets.right;
						if (tabPane.getSelectedIndex() == i) {
							bounds.x -= 3;
							bounds.width += 7;
						}
						else {
							bounds.x -= 3;
							bounds.width += 4;
						}
							
					}
					else {
						bounds.x = boundsTab.x + tabInsets.left;
						bounds.width = boundsTab.width - tabInsets.left - tabInsets.right;
					}
					tlb.setBounds(bounds);
				}
			}
            //Reiniciamos los anchos de las columnas
            hashTabsWidths = null;
            oldSelectedindex = tabPane.getSelectedIndex();
		}

		private HashMap<Integer, Integer> recalculateTabsWidths() {

			//Reiniciamos los anchos de las columnas
			hashTabsWidths = null;
			
			Dimension sizeTabPane = tabPane.getSize();
			Insets insetsTabPane = tabPane.getInsets();
			Insets insetsTabArea = getTabAreaInsets(tabPane.getTabPlacement());

			HashMap<Integer, Integer> tabsWidthsMap = null;

			int numTabs = tabPane.getTabCount();
			int widthTotal = sizeTabPane.width - insetsTabPane.left - insetsTabPane.right - insetsTabArea.left - insetsTabArea.right - 4;
			int totalWidths = 0;
			int[] widths = new int[numTabs];

			for (int i = 0; i < numTabs; i++) {

				int width = calculateTabWidth(tabPane.getTabPlacement(), i, getFontMetrics());
				widths[i] = width;
				totalWidths += width;
			}

			if (totalWidths > widthTotal && numTabs > 0 && totalWidths > 0 && widthTotal > 0) {

				tabsWidthsMap = new HashMap<Integer, Integer>();
				int remainingWidthTotal = widthTotal;
				int numTabsResize = numTabs;
				int selectedTabIndex = tabPane.getSelectedIndex();
				
				List<Integer> fixedTabs = Lists.newList();
				if (isSelectedAlwaysFixed()) {
					if (selectedTabIndex != -1)
						fixedTabs.add(selectedTabIndex);
					if (fixedTab != -1 && fixedTab != selectedTabIndex)
						fixedTabs.add(fixedTab);
				}
				else {
					fixedTabs.add(fixedTab);
				}
				
				if (lTabbedPane != null) {
					//Añadimos las pestañas link o botton como fijas
					for (int index = 0; index < numTabs; index++) {
						TabProperties tabProperties = lTabbedPane.getTabProperties(index);
						boolean fixedTabWidth = tabProperties.getAspect() != LTabbedPane.ASPECT_DEFAULT;
						if (fixedTabWidth && !fixedTabs.contains(index))
							fixedTabs.add(index);
					}
				}
				//int fixedTab = indexMouseOver;// != -1 ? indexMouseOver : tabPane.getSelectedIndex();
				for (int i = 0; i < fixedTabs.size(); i++) {
					int fTab = fixedTabs.get(i);
					if (fTab != -1) {
						int minWidthTab = 13;
						int maxWidthFixed = remainingWidthTotal - ((numTabsResize - 1)*minWidthTab);
						int widthFixed = Math.min(widths[fTab], maxWidthFixed);
						Integer key = new Integer(fTab);
						tabsWidthsMap.put(key, widthFixed);
						numTabsResize--;
						remainingWidthTotal -= widthFixed;
					}
				}
				
				// Hacemos varias pasadas para ver que columnas no hay que redimensionar
				// El ancho sobrante lo repartiremos entre las demás columnas
				int excessWidth = 1;// Ponemos algo distinto de cero para que pase la primera vez
				int newTotalWidths = 0;
				int maxTabWidth = remainingWidthTotal > 0 ? remainingWidthTotal / numTabsResize : 0;
				while (excessWidth != 0) {

					excessWidth = 0;
					for (int i = 0; i < numTabs; i++) {

						Integer key = new Integer(i);
						boolean ignoreColumn = tabsWidthsMap.get(key) != null;
						int tabWidth = widths[i];
						if (!ignoreColumn && tabWidth < maxTabWidth) {
							numTabsResize--;
							int excessTabWidth = maxTabWidth - tabWidth;
							excessWidth += excessTabWidth;
							remainingWidthTotal = remainingWidthTotal - tabWidth;
							// Asignamos el ancho a la columna y la ignoraremose en la siguiente pasada
							tabsWidthsMap.put(key, tabWidth);
							newTotalWidths += tabWidth;
						}
					}
					if (numTabsResize > 0)
						maxTabWidth = remainingWidthTotal / numTabsResize;
				}
				//Ajustamos mas el anchoTabMaximo
				for (int i = 0; i < numTabsResize; i++) {
					newTotalWidths += maxTabWidth;
				}
				int excessWidthExtra = newTotalWidths - widthTotal;
				int tabWidthExtra = 0;
				int rest = 0;
				if (excessWidthExtra > 0 && numTabsResize > 0) {
					tabWidthExtra = excessWidthExtra / numTabsResize;
					rest = excessWidthExtra % numTabsResize;
				}
				
				for (int i = 0; i < numTabs; i++) {

					Integer key = new Integer(i);
					boolean ignoreColumn = tabsWidthsMap.get(key) != null;
					if (!ignoreColumn) {
						int width = maxTabWidth - tabWidthExtra;
						if (rest > i)
							width--;
						tabsWidthsMap.put(key, width);
					}
				}
			}

			//Console.println(tabsWidthsMap);
			return tabsWidthsMap;
		}
	}

	public LTabbedPaneUI() {
		super();
	}
	
	public static ComponentUI createUI(JComponent c) {
        return new LTabbedPaneUI();
    }
	
	protected LayoutManager createLayoutManager() {
	    if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
	        return super.createLayoutManager();
	    } else { /* WRAP_TAB_LAYOUT */
	        return new LTabbedPaneLayout();
	    }
	}
	
	public Font getFont(int index) {
		Font font = tabPane.getFont();
		if (index == tabPane.getSelectedIndex() && font.getStyle() != Font.BOLD)
			font = Utils.getFontWithStyle(font, Font.BOLD);
		return font;
	}
	
	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
	
		//int tabWidth = getSpecialTabWidth(tabIndex);
		
		int tabWidth;
		Component tabComponent = tabPane.getTabComponentAt(tabIndex);
		JComponent tlb = tabComponent != null ? (JComponent) tabComponent : null;
		if (tlb != null && tlb.getParent() != null) {
			Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
			tabWidth = tlb.getPreferredSize().width + tabInsets.left + tabInsets.right + 4;
			//tabWidth = tlb.getPreferredSize().width;
		}
		else {
			tabWidth = getSpecialTabWidth(tabIndex);
		}
		
		boolean aspectNormal = true;
		if (lTabbedPane != null) {
			TabProperties tabProperties = lTabbedPane.getTabProperties(tabIndex);
			aspectNormal = tabProperties.getAspect() == LTabbedPane.ASPECT_DEFAULT;
		}
		
		if (tabWidth == -1 || aspectNormal) {
			
			if (!isAdjustTabsWidth() || hashTabsWidths == null || hashTabsWidths.get(tabIndex) == null || hashTabsWidths.get(tabIndex).intValue() < 0) {
				
				if (tabPane.getGraphics() != null) {
					Font font = getFont(tabIndex);
					if (tabIndex == tabPane.getSelectedIndex())
						font = UtilsGUI.getFontWithStyle(font, Font.BOLD);
					metrics = tabPane.getGraphics().getFontMetrics(font);
				}
				
				if (tabWidth == -1)
					tabWidth = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
				
				if (isAdjustTabsWidth()) {
					if (hashTabsWidths == null)
						hashTabsWidths = new HashMap<Integer, Integer>();
					hashTabsWidths.put(tabIndex, new Integer(tabWidth));
				}
			}
			else {
				tabWidth = hashTabsWidths.get(tabIndex).intValue();
			}
		}
		
		return tabWidth;
	}
	
	protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
		
		int tabHeight = getSpecialTabHeight(tabIndex);
		if (tabHeight == -1) {
			tabHeight = super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
		}
		
		return tabHeight;
	}
	
	
	protected void installListeners() {
	
		super.installListeners();
		tabPane.addMouseListener(this);
		tabPane.addMouseMotionListener(this);
	}
	
	@Override
	protected void installDefaults() {
		super.installDefaults();
		tabInsets = new Insets(2, 4, 2, 4);
	}
	
	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	public void mouseClicked(MouseEvent e) {}
	/**
	 * Invoked when a mouse button is pressed on a component and then 
	 * dragged.  Mouse drag events will continue to be delivered to
	 * the component where the first originated until the mouse button is
	 * released (regardless of whether the mouse position is within the
	 * bounds of the component).
	 */
	public void mouseDragged(MouseEvent e) {}
	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
	
		int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY());
		if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
	
			if (indexMouseOver != tabIndex) {
				indexMouseOver = tabIndex;
				tabPane.repaint();
			}
		}
	}
	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent e) {
	
		if (indexMouseOver != -1 || indexMousePressed != -1) {
			indexMouseOver = -1;
			indexMousePressed = -1;
			tabPane.repaint();
		}
	}
	/**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 */
	public void mouseMoved(MouseEvent e) {
	
		int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY());
		if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
	
			if (indexMouseOver != tabIndex) {
				indexMouseOver = tabIndex;
				tabPane.repaint();
			}
		}
		else if (indexMouseOver != -1) {
			
			indexMouseOver = -1;
			tabPane.repaint();
		}
		if (isAdjustTabsWidth() && /*tabIndex != -1 && */tabIndex != fixedTab) {
			int oldFixedTab = fixedTab;
			fixedTab = tabIndex;
			if (fixedTab != oldFixedTab) {
				//int prefWidth = calculateTabWidth(tabPane.getTabPlacement(), tabIndex, getFontMetrics());
				//Rectangle tabRect = getTabBounds(tabIndex, new Rectangle());
				//if (prefWidth > tabRect.width) {
					tabPane.revalidate();
					tabPane.repaint();
					//System.out.println("tabIndex " +tabIndex+ " prefWidth "+prefWidth+" tabRect.width "+tabRect.width);
				//}
			}
		}
	}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {
		
		int tabIndex = indexMouseOver;
		if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
	
			if (indexMousePressed != tabIndex) {
				indexMousePressed = tabIndex;
				tabPane.repaint();
			}
		}
	}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	public void mouseReleased(MouseEvent e) {
		if (indexMousePressed != -1) {
			indexMousePressed = -1;
			tabPane.repaint();
		}
	}

	public void installUI(JComponent c) {
		super.installUI(c);
		if (c != null && c instanceof LTabbedPane)
			this.lTabbedPane = (LTabbedPane) c;
	}
	
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		
		this.hashTabsWidths = null;
		this.lTabbedPane = null;
		this.dialogTab = null;
		this.buttonAux = null; 
		this.linkAux = null;
		this.labelAux = null;
		this.dialogPanelDesignTime = null;
		this.dialogDesignTime = null;
		this.dialogPanelAux = null;
		this.lineBorderColor = null;
		this.selectedTabLineColor = null;
	}
	
	protected void uninstallListeners() {
		super.uninstallListeners();
		tabPane.removeMouseListener(this);
		tabPane.removeMouseMotionListener(this);
	}
	
	protected void layoutLabel(int tabPlacement, 
	        FontMetrics metrics, int tabIndex,
	        String title, Icon icon,
	        Rectangle tabRect, Rectangle iconRect, 
	        Rectangle textRect, boolean isSelected ) {
		
		super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
	}
	
	public void setAdjustTabsWidth(boolean adjustTabsWidth) {
		this.adjustTabsWidth = adjustTabsWidth;
	}
	public boolean isAdjustTabsWidth() {
		return adjustTabsWidth;
	}
	public void setSelectedAlwaysFixed(boolean selectedAlwaysFixed) {
		this.selectedAlwaysFixed = selectedAlwaysFixed;
	}
	public boolean isSelectedAlwaysFixed() {
		return selectedAlwaysFixed;
	}
	public Color getTabBackground(int index) {
		Component tabComponent = tabPane.getComponentAt(index);
		Color color = tabComponent.isOpaque() ? tabComponent.getBackground() : tabPane.isOpaque() ? tabPane.getBackground() : null;
		if (color == null) {
			Container parent = tabPane.getParent();
			color = parent != null && parent.isOpaque() ? parent.getBackground() : GeneralUIProperties.getInstance().getColorPanels();
		}	
		return color;
	}
	
	//
	// LTabbedPane specific
	//

	protected Insets getTabAreaInsets(int tabPlacement) {

		if (lTabbedPane != null) {
			
			boolean ocultarTabArea = lTabbedPane.isHideAllTabs();
			if (!ocultarTabArea && lTabbedPane.isHideSingleTab()) {
				ocultarTabArea = getVisibleTabs() < 2;
			}
			
			if (ocultarTabArea) {
				Insets tabAreaInsetsAux = new Insets(tabAreaInsets.top, tabAreaInsets.left, tabAreaInsets.bottom, tabAreaInsets.right);
				//Eliminamos el margen superior de las pestañas ya que no se van a mostrar
				tabAreaInsetsAux.top = 0;
				return tabAreaInsetsAux;
			}
		}
	    return super.getTabAreaInsets(tabPlacement);
	}
	
	private boolean isVisibleTabAt(int index) {
		
		boolean isVisibleTab = true;
		TabProperties tabProperties = lTabbedPane != null ? lTabbedPane.getTabProperties(index) : null;
		if (tabProperties != null) {
			
			boolean visible = tabProperties.isVisible();
			boolean showInDialog = tabProperties.isShowInDialog();
			
			isVisibleTab = visible && !showInDialog;
			
			if (visible && showInDialog) {
				int aspect = tabProperties.getAspect();
				isVisibleTab = aspect == LTabbedPane.ASPECT_BUTTON || aspect == LTabbedPane.ASPECT_LINK;
			}
		}
		
		return isVisibleTab;
	}
	
	public int getFirstVisibleTabIndex() {
		for (int i = 0; i < tabPane.getTabCount(); i++) {
			if (isVisibleTabAt(i))
				return i;
		}
		return -1;
	}
	
	private int getVisibleTabs() {
		
		int visibleTabs = 0;
		for (int i = 0; i < tabPane.getTabCount(); i++) {
			if (isVisibleTabAt(i))
				visibleTabs++;
		}
		return visibleTabs;
	}

	private boolean isTabPaintable(int index) {
		
		boolean tabPaintable = index >= 0 && index < tabPane.getTabCount();
		
		if (tabPaintable && lTabbedPane != null && !AppGUI.getCurrentAppGUI().isDesignTime()) {
			
			if (lTabbedPane.isHideAllTabs()) {
				tabPaintable = false;
			}
			//No pintamos el selector de la pestaña en Modo diálogo ni cuando esta mostrar pestaña a false
			else if(!isVisibleTabAt(index)) {
				tabPaintable = false;
			}
			//No mostramos el selector de pestañas cuando solo tenemos una pestaña
			else if (lTabbedPane.isHideSingleTab() && getVisibleTabs() < 2) {
				tabPaintable = false;
			}
		}
		
		return tabPaintable;
	}
	
	//
	// Tab aspects support components
	//
	
	protected LDialogContent getDialogTab() {
		
		if (dialogTab == null) {

			dialogTab = new LDialogContent();
			dialogTab.setSize(800, 600);
			dialogTab.setLayout(new BorderLayout());
			HeaderPanel headerPanel = new HeaderPanel();
			ButtonsPanel buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
			buttonsPanel.setAutoCloseOnAccept(true);

			dialogTab.setHeaderPanel(headerPanel);
			dialogTab.setButtonsPanel(buttonsPanel);
			
			
			dialogTab.setMargin(5);
			dialogTab.setModal(true);
			dialogTab.setResizable(false);
		}
		return dialogTab;
	}
	
	public LPanel getDialogPanelDesignTime() {
		if (dialogPanelDesignTime == null) {
			dialogPanelDesignTime = new LPanel(new GridBagLayout());
			dialogDesignTime.setOpaque(false);
			dialogPanelDesignTime.add(getDialogDesignTime());
		}
		return dialogPanelDesignTime;
	}

	public JInternalFrame getDialogDesignTime() {
		if (dialogDesignTime == null) {
			dialogDesignTime = new JInternalFrame();
			dialogDesignTime.getContentPane().setLayout(new BorderLayout());
		}
		return dialogDesignTime;
	}
	
	private LButton getButtonAux() {
		
		if (buttonAux == null) {
			buttonAux = new LButton();
			buttonAux.setText(Constants.VOID);
			buttonAux.setMargin(new Insets(1,5,2,5));
			buttonAux.getButtonProperties().setShadowTextMode(LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND);
		}
		return buttonAux;
	}
	
	private Link getLinkAux() {
		if (linkAux == null) {
			linkAux = new Link();
			linkAux.setText(Constants.VOID);
			linkAux.setHorizontalAlignment(SwingConstants.CENTER);
			linkAux.setMargin(new Insets(2,1,2,1));
		}
		return linkAux;
	}
	
	private LButton getLabelAux() {
		if (labelAux == null) {
			labelAux = new LButton();
			labelAux.setText(Constants.VOID);
			labelAux.setHorizontalAlignment(SwingConstants.CENTER);
			labelAux.setBorder(BorderFactory.createEmptyBorder());
			labelAux.setOpaque(false);
			labelAux.setMargin(new Insets(2, 5, 2, 5));
			labelAux.getButtonProperties().setGradientBackgroundEnabled(false);
			labelAux.getButtonProperties().setPaintBgEffectsWhenTransparent(false);
		}
		return labelAux;
	}
	
	private LPanel getDialogPanelAux() {
		if (dialogPanelAux == null) {
			dialogPanelAux = new LPanel();
		}
		return dialogPanelAux;
	}
	
	private int getSpecialTabWidth(int tabIndex) {
		Dimension dimension = getSpecialTabDimension(tabIndex);
		return dimension != null ? dimension.width : -1;
	}
	
	private int getSpecialTabHeight(int tabIndex) {
		Dimension dimension = getSpecialTabDimension(tabIndex);
		return dimension != null ? dimension.height : -1;
	}
	
	private Dimension getSpecialTabDimension(int tabIndex) {
		
		if (lTabbedPane != null) {
			
			if (!isTabPaintable(tabIndex))
				return new Dimension(0, 0);
			
			TabProperties tabProperties = lTabbedPane.getTabProperties(tabIndex);
			boolean aspectButton = tabProperties.getAspect() == LTabbedPane.ASPECT_BUTTON;
			boolean aspectLink = tabProperties.getAspect() == LTabbedPane.ASPECT_LINK;
			
			String text =  tabPane.getTitleAt(tabIndex);
			Icon icon = getIconForTab(tabIndex);
			Font font = tabPane.getFont();
			Font fontSelected = Utils.getFontWithStyle(font, Font.BOLD);
			
			int wNormal, hNormal, wSelected, hSelected;
			
			Insets margin;
			if (aspectButton || aspectLink) {
				
				margin = getButtonMargin(tabIndex);
				if (aspectButton) {
					
					getButtonAux().setText(text);
					getButtonAux().setIcon(icon);
					
					//Usamos el ancho mayor entre la fuente normal y la seleccionada
					getButtonAux().setFont(font);
					getButtonAux().validate();
					wNormal = getButtonAux().getPreferredSize().width;
					hNormal = getButtonAux().getPreferredSize().height;
					
					getButtonAux().setFont(fontSelected);
					getButtonAux().validate();
					wSelected = getButtonAux().getPreferredSize().width;
					hSelected = getButtonAux().getPreferredSize().height;
				}
				else {
					getLinkAux().setText(text);
					getLinkAux().setIcon(icon);
					
					//Usamos el ancho mayor entre la fuente normal y la seleccionada
					getLinkAux().setFont(font);
					getLinkAux().validate();
					wNormal = getLinkAux().getPreferredSize().width;
					hNormal = getLinkAux().getPreferredSize().height;
					
					getLinkAux().setFont(fontSelected);
					getLinkAux().validate();
					wSelected = getLinkAux().getPreferredSize().width;
					hSelected = getLinkAux().getPreferredSize().height;
				}
			}
			else {
				
				margin = getTabInsets(tabPane.getTabPlacement(), tabIndex);
				
				getLabelAux().setText(text);
				getLabelAux().setIcon(icon);
				
				//Usamos el ancho mayor entre la fuente normal y la seleccionada
				getLabelAux().setFont(new LFont(font, LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT));
				getLabelAux().validate();
				wNormal = getLabelAux().getPreferredSize().width;
				hNormal = getLabelAux().getPreferredSize().height;
				
				getLabelAux().setFont(fontSelected);
				getLabelAux().validate();
				wSelected = getLabelAux().getPreferredSize().width;
				hSelected = getLabelAux().getPreferredSize().height;
			}
			
			int w = Math.max(wNormal, wSelected);
			int h = Math.max(hNormal, hSelected);
			w += margin.left + margin.right;
			h += margin.bottom + margin.top;
			
			return new Dimension(w, h);
		}
		
		return null;
	}
	
	protected void setVisibleComponent(Component component) {

		if (lTabbedPane != null) {

			if (processingDialogTab || getDialogTab().getDialog().isVisible())
				return;
			
			int tabIndex = lTabbedPane.getTabIndex(component);
			if (tabIndex != -1) {
				
				TabProperties tabProperties = lTabbedPane.getTabProperties(tabIndex);
				
				if (tabProperties.isShowInDialog()) {
					//Dialog Tab
					if (AppGUI.getCurrentAppGUI().isDesignTime()) {
						//Show in internal Dialog Design Time
						//updateDialogDesignTime(tabIndex);
						//super.setVisibleComponent(getDialogPanelDesignTime());
						//return;
					}
					else {
						//Show in customDialog
						/*if (normalTabSelectedIndex != -1) {
							Component normalTabSelectedComponent = lTabbedPane.getComponentAt(normalTabSelectedIndex);
							normalTabSelectedComponent.setVisible(false);
							getDialogPanelAux().setBackground(normalTabSelectedComponent.getBackground());
							getDialogPanelAux().setOpaque(normalTabSelectedComponent.isOpaque());
							super.setVisibleComponent(normalTabSelectedComponent);
						}
						else
							super.setVisibleComponent(new LPanel());*/
						
						processDialogTab(tabIndex);
						/*
						//Select last normal tab before closing customDialog
						indexMouseOver = -1;
						indexMousePressed = -1;
						
						int tabIndexToSelect = 0;
						if (normalTabSelectedIndex != -1)
							tabIndexToSelect = normalTabSelectedIndex;
							
						lTabbedPane.setSelectedIndex(tabIndexToSelect);
						*/
						
						return;
					}
				}
				else if (!AppGUI.getCurrentAppGUI().isDesignTime()) {
					
					if (!tabProperties.isVisible())
						return;
					
					normalTabSelectedIndex = tabIndex;
				}
			}
		}
		super.setVisibleComponent(component);
	}
	
	protected void processDialogTab(int tabIndex) {
		
		if (!processingDialogTab && lTabbedPane != null) {
			
			processingDialogTab = true;
				
			try {
				Component dialogTabComponent = lTabbedPane.getComponentAt(tabIndex);
				updateDialog(tabIndex);
				dialogWillBecomeVisible();
				getDialogTab().showInDialog();
				
				//Select last normal tab before closing customDialog
				indexMouseOver = -1;
				indexMousePressed = -1;
				
				int tabIndexToSelect = 0;
				if (normalTabSelectedIndex != -1)
					tabIndexToSelect = normalTabSelectedIndex;
					
				lTabbedPane.setSelectedIndex(tabIndexToSelect);
				lTabbedPane.setComponentAt(tabIndex, dialogTabComponent);
				
				dialogClosed();
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
			finally {
				processingDialogTab = false;
			}
		}
	}
	
	private void updateDialog(int tabIndex) {
		
		Component dialogTabComponent = lTabbedPane.getComponentAt(tabIndex);
		TabProperties tabProperties = lTabbedPane.getTabProperties(tabIndex);
		String title = lTabbedPane.getTitleAt(tabIndex);
		
		if (!AppGUI.getCurrentAppGUI().isDesignTime()) {
			Component visibleComponent;
			if (normalTabSelectedIndex != -1) {
				Component normalTabSelectedComponent = lTabbedPane.getComponentAt(normalTabSelectedIndex);
				normalTabSelectedComponent.setVisible(false);
				getDialogPanelAux().setBackground(normalTabSelectedComponent.getBackground());
				getDialogPanelAux().setOpaque(normalTabSelectedComponent.isOpaque());
				visibleComponent = normalTabSelectedComponent;
			}
			else
				visibleComponent = new LPanel();
			
			lTabbedPane.setComponentAt(tabIndex, getDialogPanelAux());
			super.setVisibleComponent(visibleComponent);
		}
		
		getDialogTab().add(dialogTabComponent, BorderLayout.CENTER);
		getDialogTab().setMargin(2);
		getDialogTab().setTitle(title);
		
		HeaderPanel headerPanel = null;
		if (!tabProperties.getDescription().trim().equals(Constants.VOID))
			headerPanel = new HeaderPanel(null, tabProperties.getDescription());
		getDialogTab().setHeaderPanel(headerPanel);
		
		Dimension size = tabProperties.getDialogSize();
		getDialogTab().setSize(size);
		getDialogTab().setPreferredSize(size);
	}
	
	private void updateDialogDesignTime(int tabIndex) {
		
		updateDialog(tabIndex);
		
		getDialogTab().addComponents();
		getDialogDesignTime().getContentPane().add(getDialogTab(), BorderLayout.CENTER);
		getDialogDesignTime().getContentPane().add(getDialogTab().getButtonsPanel(), BorderLayout.SOUTH);
		if (getDialogTab().getHeaderPanel() != null)
			getDialogDesignTime().getContentPane().add(getDialogTab().getHeaderPanel(), BorderLayout.NORTH);
		
		getDialogDesignTime().setPreferredSize(getDialogTab().getDialog().getSize());
	}
	
	protected void dialogWillBecomeVisible() {
		//OverWrite this method to do something before customDialog will be visible
	}
	protected void dialogClosed() {
		//OverWrite this method to do something after customDialog close
	}

	//
	// Paint Methods
	//
		
	protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
		
		super.paintTabArea(g, tabPlacement, selectedIndex);
		
		if (lTabbedPane != null) {
			//Hand Cursor on Link aspect tabs
			TabProperties tabProperties = lTabbedPane.getTabProperties(indexMouseOver);
			int cursor = tabProperties != null && tabProperties.getAspect() == LTabbedPane.ASPECT_LINK ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
			tabPane.setCursor(Cursor.getPredefinedCursor(cursor));
		}
	}
	
	protected void paintTab(Graphics g, int tabPlacement,
								Rectangle[] rects, int tabIndex,
								Rectangle iconRect, Rectangle textRect) {

		if (lTabbedPane != null) {
			
			if (!isTabPaintable(tabIndex))
				return;
			
			TabProperties tabProperties = lTabbedPane.getTabProperties(tabIndex);
			if (tabProperties != null) {
				
				boolean aspectButton = tabProperties.getAspect() == LTabbedPane.ASPECT_BUTTON;
				boolean aspectLink = tabProperties.getAspect() == LTabbedPane.ASPECT_LINK;
				
				int selectedIndex = tabPane.getSelectedIndex();
				boolean isSelected = selectedIndex == tabIndex;
				
				Icon iconDesignTime = getIconDesignTime(tabIndex);
				Icon icon = getIconForTab(tabIndex);
				
				if (aspectButton || aspectLink) {
					
					String title = tabPane.getTitleAt(tabIndex);
					Rectangle tabRect = rects[tabIndex];
					Font font = getFont(tabIndex);
					//FontMetrics metrics = g.getFontMetrics(font);
					
					//layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
					
					/*if (isSelected && AplicacionGUI.getAplicacionGUIActual().isDesignTime()) {
						paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
										   tabRect.width, tabRect.height, isSelected);
					}*/
					paintButtonAux(g, title, tabRect, tabIndex, icon, aspectLink, font);
				}
				else {
				
					paintStandardTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
				}
				
				if (iconDesignTime != null && icon != null && !iconDesignTime.equals(icon))
					paintIcon(g, tabPlacement, tabIndex, iconDesignTime, iconRect, isSelected);
			}
			else paintStandardTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
		}
		else paintStandardTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
	}
	
	protected void paintStandardTab(Graphics g, int tabPlacement,
			Rectangle[] rects, int tabIndex, Rectangle iconRect,
			Rectangle textRect) {

		Rectangle tabRect = rects[tabIndex];

		int selectedIndex = tabPane.getSelectedIndex();
		boolean isSelected = selectedIndex == tabIndex;

		String title = tabPane.getTitleAt(tabIndex);
		Font font = getFont(tabIndex);
		//FontMetrics metrics = g.getFontMetrics(font);
		Icon icon = getIconForTab(tabIndex);

		// Pintamos el tab de la pestaña
		//layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
		paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,	tabRect.width, tabRect.height, isSelected);
		paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,	tabRect.width, tabRect.height, isSelected);
		
		if (tabPane.getTabComponentAt(tabIndex) == null)
			paintLabelAux(g, title, tabRect, tabIndex, icon, font);
		/*
		paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
		paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
		paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect,	textRect, isSelected);
		*/
	}
	
	private void paintButtonAux(Graphics g, String texto, Rectangle rects, int tabIndex, Icon icon, boolean isLink, Font font) {
		
		boolean selected = tabPane.getSelectedIndex() == tabIndex;
		boolean enabled = tabPane.isEnabledAt(tabIndex);
		boolean pressed = indexMousePressed == tabIndex;
		boolean rollOver = indexMouseOver == tabIndex;
		
		Insets buttonMargin = getButtonMargin(tabIndex);
		
		int w = rects.width - buttonMargin.left - buttonMargin.right;
		int h = rects.height - buttonMargin.top - buttonMargin.bottom;//isLink ? getLinkAux().getPreferredSize().height : getButtonAux().getPreferredSize().height;
		int x = rects.x + buttonMargin.left;
		int y = rects.y + buttonMargin.top;
		if (selected) {
			h = h - 4;
			y = y + 2;
			w = w - 3;
			x = x + 2;
		}
		
		Rectangle bounds = new Rectangle(x, y, w, h);
		Graphics cg = g.create(bounds.x, bounds.y, bounds.width, bounds.height);
		
		int tabPlacement = tabPane.getTabPlacement();
		int vTextPosition = tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM ? SwingConstants.CENTER : SwingConstants.BOTTOM;
		int hTextPosition = tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM ? SwingConstants.RIGHT : SwingConstants.CENTER;
		
		if (isLink) {
			getLinkAux().setEnabled(enabled);
			getLinkAux().rollover(rollOver);
			getLinkAux().setIcon(icon);
			
			getLinkAux().setSize(w, h);
			getLinkAux().setText(texto);
			getLinkAux().setFont(font);
			getLinkAux().setVerticalTextPosition(vTextPosition);
			getLinkAux().setHorizontalTextPosition(hTextPosition);
			getLinkAux().paint(cg);
		}
		else {
			
			boolean pressedAspect = pressed || selected;
			getButtonAux().setEnabled(enabled);
			getButtonAux().setSelected(pressedAspect);
			getButtonAux().setRolloverEnabled(rollOver);
			getButtonAux().getModel().setEnabled(enabled);
			getButtonAux().getModel().setPressed(pressedAspect);
			getButtonAux().getModel().setArmed(pressedAspect);
			getButtonAux().getModel().setSelected(pressedAspect);
			getButtonAux().getModel().setRollover(rollOver);
			getButtonAux().setIcon(icon);
			
			getButtonAux().setSize(w, h);
			getButtonAux().setText(texto);
			if (rollOver)
				font = UtilsGUI.getFontWithStyle(font, Font.BOLD);
			getButtonAux().setFont(font);
			getButtonAux().setVerticalTextPosition(vTextPosition);
			getButtonAux().setHorizontalTextPosition(hTextPosition);
			
			getButtonAux().paint(cg);
		}
	}
	
	public void paintLabelAux(Graphics g, String texto, Rectangle rects, int tabIndex, Icon icon, Font font) {
		paintLabelAux(g, texto, rects, tabIndex, icon, font, false);
	}
	public void paintLabelAux(Graphics g, String texto, Rectangle rects, int tabIndex, Icon icon, Font font, boolean ignoreMargin) {
		
		boolean selected = tabPane.getSelectedIndex() == tabIndex;
		boolean enabled = tabPane.isEnabledAt(tabIndex);
		boolean pressed = indexMousePressed == tabIndex;
		boolean rollOver = indexMouseOver == tabIndex && !selected;
		
		Insets buttonMargin = ignoreMargin ? new Insets(0, 0, 0, 0) : getButtonMargin(tabIndex);
		
		int w = rects.width - buttonMargin.left - buttonMargin.right;
		int h = rects.height - buttonMargin.top - buttonMargin.bottom;//isLink ? getLinkAux().getPreferredSize().height : getButtonAux().getPreferredSize().height;
		int x = rects.x + buttonMargin.left;
		int y = rects.y + buttonMargin.top;
		if (selected) {
			h = h - 4;
			y = y + 2;
			w = w - 3;
			x = x + 2;
		}
		
		Rectangle bounds = new Rectangle(x, y, w, h);
		Graphics cg = g.create(bounds.x, bounds.y, bounds.width, bounds.height);
		Graphics2D g2d = (Graphics2D) cg;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		boolean pressedAspect = pressed || selected;
		getLabelAux().setEnabled(enabled);
		
		//getLabelAux().setSelected(pressedAspect);
		getLabelAux().setRolloverEnabled(rollOver);
		getLabelAux().getModel().setEnabled(enabled);
		//getLabelAux().getModel().setPressed(pressedAspect);
		//getLabelAux().getModel().setArmed(pressedAspect);
		//getLabelAux().getModel().setSelected(pressedAspect);
		getLabelAux().getModel().setRollover(rollOver);
		getLabelAux().setOpaque(rollOver);
		
		getLabelAux().setIcon(icon);
		getLabelAux().setOpaque(false);
		
		getLabelAux().setSize(w, h);
		getLabelAux().setText(texto);
		//if (rollOver)
			//font = UtilsGUI.getFontWithStyle(font, Font.BOLD);
		getLabelAux().setFont(font);
		
		Color foreground = tabPane.getForegroundAt(tabIndex);//ColorsGUI.getColorText()
		//Optimizamos el foreground ya que el background variará dependiendo del color de la pestaña seleccionada
		boolean optimizeForeground = selected;
	    if (optimizeForeground) {
	    	Color background = getLabelAux().getBackground();
	    	boolean backgroundIsDark = Colors.isColorDark(background);
	    	boolean foregroundIsDark = Colors.isColorDark(foreground);
	    	if (backgroundIsDark == foregroundIsDark) {
	    		Color optimizedForeground = Colors.optimizeColor(foreground, background);
	    		foreground = optimizedForeground;
	    	}
	    }
	    
	    getLabelAux().setForeground(foreground);
		
	    int tabPlacement = tabPane.getTabPlacement();
		int vTextPosition = tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM ? SwingConstants.CENTER : SwingConstants.BOTTOM;
		int hTextPosition = tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM ? SwingConstants.RIGHT : SwingConstants.CENTER;
		getLabelAux().setVerticalTextPosition(vTextPosition);
		getLabelAux().setHorizontalTextPosition(hTextPosition);
		
		//getLabelAux().updateUI();
		//SwingUtilities.updateComponentTreeUI(getLabelAux());
		
		getLabelAux().paint(cg);
	}
	
	private Insets getButtonMargin(int tabIndex) {
		
		final int OUT_GAP = 1;
		final int IN_GAP = 1;
		final int TAB_GAP = 4;
				
		boolean isFirstTab = tabIndex == getFirstVisibleTabIndex();
		int tabPlacement = tabPane.getTabPlacement();
		boolean previousTabIsButton = false;
		if (!isFirstTab) {
			TabProperties previousTabProperties = lTabbedPane != null ? lTabbedPane.getTabProperties(tabIndex - 1) : null;
			int previousAspect = previousTabProperties != null ? previousTabProperties.getAspect() : LTabbedPane.ASPECT_DEFAULT;
			previousTabIsButton = previousAspect == LTabbedPane.ASPECT_BUTTON || previousAspect == LTabbedPane.ASPECT_LINK;
		}
		
		int top, left, bottom, right;
		switch (tabPlacement) {
		case LEFT:
			top = isFirstTab ? TAB_GAP/2 : previousTabIsButton ? 0 : TAB_GAP;
			left = OUT_GAP;
			bottom = TAB_GAP;
			right = IN_GAP + 1;
			break;
		case RIGHT:
			top = isFirstTab ? TAB_GAP/2 : previousTabIsButton ? 0 : TAB_GAP;
			left = IN_GAP;
			bottom = TAB_GAP;
			right = OUT_GAP;
			break;
		case BOTTOM:
			top = IN_GAP;
			left = isFirstTab ? TAB_GAP/2 : previousTabIsButton ? 0 : TAB_GAP;
			bottom = OUT_GAP;
			right = TAB_GAP;
			break;
		default:
			top = OUT_GAP;
			left = isFirstTab ? TAB_GAP/2 : previousTabIsButton ? 0 : TAB_GAP;
			bottom = IN_GAP;
			right = TAB_GAP;
		}
		
		return new Insets(top, left, bottom, right);
	}
	protected Icon getIconDesignTime(int tabIndex) {
		
		if (lTabbedPane != null && AppGUI.getCurrentAppGUI().isDesignTime()) {

			TabProperties tabProperties = lTabbedPane.getTabProperties(indexMouseOver);
			if (tabProperties != null) {
				
				if (tabProperties.isShowInDialog())					
					return Icons.WINDOW;
				else if (!tabProperties.isVisible())
					return Icons.CANCEL;
			}
		}
		return null;
	}

	protected void paintTabBackground(Graphics g, int tabPlacement,	int tabIndex, int x, int y, int w, int h, boolean isSelected) {

		//super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h,	isSelected);

		Color color;
		if (isSelected) {
			color = getTabBackground(tabIndex);
		}
		else {
			
			GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
			
			if (!tabPane.isEnabledAt(tabIndex))
				color = generalUIProperties.getColorPanels();
			else if (tabIndex == indexMouseOver)
				color = generalUIProperties.getColorRolloverDark();//ColorsGUI.COLOR_PESTANA_ROLLOVER;
			else
				color = Colors.darker(generalUIProperties.getColorPanels(), 0.1);//ColorsGUI.COLOR_PESTANA_NO_SELECCIONADA;
		}

		getLabelAux().setBackground(color);
		g.setColor(color);
		switch (tabPlacement) {
		case LEFT:
			g.fillRect(x, y, w, h);
			break;
		case RIGHT:
			g.fillRect(x, y, w, h);
			break;
		case BOTTOM:
			g.fillRect(x, y, w, h);
			break;
		default:
			g.fillRect(x, y, w, h);
		}
		
		//paintTabBackgroundSelectable(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
	}
	
	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		
		//super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
		
		Rectangle tabRects = new Rectangle(x,y,w,h);
		if (isTabPaintable(tabIndex)) {
			
			if (getBorderType() != BORDER_NONE) {
				boolean isFirstTab = tabIndex == getFirstVisibleTabIndex();
				boolean isLineBorder = getBorderType() == BORDER_LINE_ALL || getBorderType() == BORDER_LINE_TABS;
				Color colorTop = isLineBorder && isSelected ? getLineBorderColor() : getLightBorderColor();
				Color colorLeft = colorTop;
				Color colorBottom = isLineBorder && isSelected ? getLineBorderColor() : getShadowBorderColor();
				Color colorRight = colorBottom;
				
				if (!isSelected || !isLineBorder) {
					switch (tabPlacement) {
						case LEFT:
							if (!isSelected && !isFirstTab)
								colorTop = null;
							colorRight = null;
						    break;
						
						case RIGHT:
							if (!isSelected && !isFirstTab)
								colorTop = null;
							colorLeft = null;
						    break;
						
						case BOTTOM:
							if (!isSelected && !isFirstTab)
								colorLeft = null;
							colorTop = null;
							break;
						
						case TOP:
						default:
							if (!isSelected && !isFirstTab)
								colorLeft = null;
							colorBottom = null;
					}
				}
				
				GraphicsUtils.paintBorderColors(g, tabRects, colorTop, colorLeft, colorBottom, colorRight);
			}
			
			Color selectedTabLineColor = isSelected ? getSelectedTabLineColor() : null;
			
			if (selectedTabLineColor != null) {
				final int SELECTED_LINE_THICKNESS = 3;
				Insets tabInsets = new Insets(2, 2, 2, 2);//Tab Border + 1 pixel margin
				GraphicsUtils.paintLineBackground(g, selectedTabLineColor, tabPlacement, SELECTED_LINE_THICKNESS, tabRects, tabInsets);
			}
		}
	}
	
	private void paintTabBackgroundSelectable(Graphics g, int tabPlacement,
									  int tabIndex,
									  int x, int y, int w, int h,
									  boolean isSelected ) {

		if (isSelected && AppGUI.getCurrentAppGUI().isDesignTime()) {
			
			//Ponemos el color COLOR_ROLLOVER y el borde cuando seleccionamos en Modo diseño la pestaña
			GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
			
			g.setColor(generalUIProperties.getColorRolloverDark());
			g.drawRect(x+2, y+1, w-6, h-4);
			Color colorFondo = generalUIProperties.getColorRollover();
			int width = w-7;
			int height = h-5;
			x = x+3;
			y = y+2;
			
			int x1 = width/2;
			int y1 = y;
			int x2 = x1;
			int y2 = y + height;

			Graphics2D g2d = (Graphics2D) g;
			GradientPaint gp = new GradientPaint(x1, y1, colorFondo.brighter(), x2, y2, colorFondo, false);
			g2d.setPaint(gp);
			java.awt.geom.Rectangle2D.Double rectangle = new java.awt.geom.Rectangle2D.Double(x, y, width, height);	
			g2d.fill(rectangle);
		}
	}
	
	protected void paintFocusIndicator(Graphics g, int tabPlacement,
			Rectangle[] rects, int tabIndex, Rectangle iconRect,
			Rectangle textRect, boolean isSelected) {
			// Sobreescribimos el método para que no se pinte el foco
	}

	protected void paintText(Graphics g, int tabPlacement, Font font,
			FontMetrics metrics, int tabIndex, String title,
			Rectangle textRect, boolean isSelected) {
/*
		title = UtilsGUI.getTextoAjustado(title, textRect.width + 8, font);
		boolean habilitada = tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex);
		Component tabComponent = tabPane.getTabComponentAt(tabIndex);
		TabCloseButton tcb = null;
		if (tabComponent != null && tabComponent instanceof TabCloseButton)
			tcb = (TabCloseButton) tabComponent;

		if (habilitada || tcb != null) {

			//Color colorTexto = isSelected ? ColorsGUI.COLOR_FUENTE_PESTANA_SELECCIONADA : ColorsGUI.COLOR_FUENTE_PESTANA_NO_SELECCIONADA;
			boolean rollover = !isSelected && tabIndex == indexMouseOver;
			//if (rollover)
			//	colorTexto = ColorsGUI.COLOR_FUENTE_PESTANA_ROLLOVER;
			Color colorTexto = rollover ? GeneralUIProperties.getInstance().getColorTextBrightest() : GeneralUIProperties.getInstance().getColorText();
					
			if (tcb != null) {

				tcb.actualizar(font, colorTexto, habilitada, rollover);
			} else if (habilitada) {

				g.setFont(font);
				g.setColor(colorTexto);

				int x = textRect.x;
				int y = textRect.y + metrics.getAscent();
				if (getBorderType() != BORDER_NONE && isSelected) {
					if (tabPlacement == TOP)
						y = y + 1;
					// else if (tabPlacement == BOTTOM)
					// y = y - 1;
				}

				UtilsGUI.drawString(g, title, x, y);
			} else
				super.paintText(g, tabPlacement, font, metrics, tabIndex,
						title, textRect, isSelected);
		} else
			super.paintText(g, tabPlacement, font, metrics, tabIndex, title,
					textRect, isSelected);
		*/
	}

	protected void paintContentBorder(Graphics g, int tabPlacement,	int selectedIndex) {

		boolean paintBorderNoVisibleTabs = false;
		boolean isTabAreaHidden = lTabbedPane != null && lTabbedPane.isHideAllTabs();
		if (!isTabAreaHidden && lTabbedPane != null && lTabbedPane.isHideSingleTab()) {
			isTabAreaHidden = getVisibleTabs() < 2;
		}
		
		if (paintBorderNoVisibleTabs || !isTabAreaHidden) {
			
			//boolean isTabPaintable = isTabPaintable(selectedIndex);
			
			// super.paintContentBorder(g, tabPlacement, selectedIndex);
			int width = tabPane.getWidth();
			int height = tabPane.getHeight();
			Insets insets = tabPane.getInsets();
			// Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
	
			int x = insets.left;
			int y = insets.top;
			int w = width - insets.right - insets.left;
			int h = height - insets.top - insets.bottom;
	
			switch (tabPlacement) {
				case LEFT:
					x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
					w -= (x - insets.left);
					break;
				case RIGHT:
					w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
					break;
				case BOTTOM:
					h -= calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
					break;
				case TOP:
				default:
					y += calculateTabAreaHeight(tabPlacement, runCount,	maxTabHeight);
					h -= (y - insets.top);
			}
			
			if (tabPane.getTabCount() > 0) {
				// Fill region behind content area
				Color color = getTabBackground(selectedIndex);
				if (color != null) {
					g.setColor(color);
					g.fillRect(x, y, w, h);
				}
			}
	
			if (getBorderType() != BORDER_NONE) {
				
				if (getBorderType() == BORDER_LINE_ALL) {
					g.setColor(getLineBorderColor());
					g.drawRect(x, y, w-1, h-1);
				}
				else {
					
					boolean isLineBorder = getBorderType() == BORDER_LINE_ALL || getBorderType() == BORDER_LINE_TABS;
					Color colorTop = isLineBorder ? getLineBorderColor() : getLightBorderColor();
					Color colorLeft = colorTop;
					Color colorBottom = isLineBorder ? getLineBorderColor() : getShadowBorderColor();
					Color colorRight = colorBottom;
					
					//colorLeft = colorTop = colorBottom = colorRight = Color.green;
					
					if (getBorderType() == BORDER_LINE_TABS || getBorderType() == BORDER_SHADOW_LIGHT_TABS) {
						switch (tabPlacement) {
							case LEFT:
								colorTop = null;
								colorRight = null;
								colorBottom = null;
							    break;
							
							case RIGHT:
								colorTop = null;
								colorLeft = null;
								colorBottom = null;
								break;
							
							case BOTTOM:
								colorTop = null;
								colorLeft = null;
								colorRight = null;
								break;
							
							case TOP:
							default:
								colorLeft = null;
								colorRight = null;
								colorBottom = null;
						}
					}
					if (colorTop != null) {
						g.setColor(colorTop);
						g.drawLine(x, y, x+w-1, y); // top
					}
					if (colorLeft != null) {
						g.setColor(colorLeft);
						g.drawLine(x, y, x, y+h-1); // left
					}
					if (colorBottom != null) {
						g.setColor(colorBottom);
						g.drawLine(x+1, y+h-1, x+w-1, y+h-1); // bottom
					}
					if (colorRight != null) {
						g.setColor(colorRight);
						g.drawLine(x+w-1, y+1, x+w-1, y+h-1); // right
					}
				}
				
				// Break line border to show visual connection to selected tab
				if (selectedIndex >= 0) {
					
					TabProperties tabProperties = lTabbedPane != null ? lTabbedPane.getTabProperties(selectedIndex) : null;
					int aspect = tabProperties != null ? tabProperties.getAspect() : LTabbedPane.ASPECT_DEFAULT;
					
					if (aspect != LTabbedPane.ASPECT_BUTTON && aspect != LTabbedPane.ASPECT_LINK) {
						// Break line border to show visual connection to selected tab
						Rectangle selRect = getTabBounds(selectedIndex, calcRect);
						g.setColor(getTabBackground(selectedIndex));
						//g.setColor(Color.red);
						switch (tabPlacement) {
							case LEFT:
								g.drawLine(selRect.x+selRect.width-2, selRect.y+1, selRect.x+selRect.width-2, selRect.y+selRect.height-2);
								break;
							
							case RIGHT:
								g.drawLine(selRect.x+1, selRect.y+1, selRect.x+1, selRect.y+selRect.height-1);
								break;
							
							case BOTTOM:
								g.drawLine(selRect.x+1, selRect.y+1, selRect.x+selRect.width-2, selRect.y+1);
								break;
							
							case TOP:
							default:
								g.drawLine(selRect.x+1, selRect.y+selRect.height-2, selRect.x+selRect.width-2, selRect.y+selRect.height-2);
						}
					}
				}
			}
		}
	}
	
	public int getBorderType() {
		return borderType;
	}

	public void setBorderType(int borderType) {
		this.borderType = borderType;
	}

	public Color getLightBorderColor() {
		Color parentColor = ColorsGUI.getFirstOpaqueParentBackground(tabPane.getParent());
		if (parentColor != null)
			return Colors.brighter(parentColor, 0.4);
		else
			return lightHighlight;
	}
	public Color getShadowBorderColor() {
		Color parentColor = ColorsGUI.getFirstOpaqueParentBackground(tabPane.getParent());
		if (parentColor != null)
			return Colors.darker(parentColor, 0.4);
		else
			return shadow;
	}
	public Color getLineBorderColor() {
		if (lineBorderColor == null) {
			//Color parentColor = ColorsGUI.getFirstOpaqueParentBackground(tabPane.getParent());
			//if (parentColor != null)
			//	lineBorderColor = Colors.esColorOscuro(parentColor) ? Colors.brighter(parentColor, 0.2) : Colors.darker(parentColor, 0.2);
			//else
				lineBorderColor = ColorsGUI.getColorApp();
		}
		return lineBorderColor;
	}
	public Color getSelectedTabLineColor() {
		return selectedTabLineColor;
	}
	
	public void setLineBorderColor(Color lineBorderColor) {
		this.lineBorderColor = lineBorderColor;
	}
	public void setSelectedTabLineColor(Color selectedTabLineColor) {
		this.selectedTabLineColor = selectedTabLineColor;
	}
}
