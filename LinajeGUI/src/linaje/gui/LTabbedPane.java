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
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import linaje.gui.ui.LTabbedPaneUI;
import linaje.statics.Constants;
import linaje.utils.Lists;
import linaje.utils.Utils;

/**
 * Añadidos respecto a un JTabbedPane:
 *  - Acceso a las propiedades de LTabbedPaneUI
 *  	- borderType
 *		- lineBorderColor
 *		- selectedTabLineColor
 *		- adjustTabsWidth
 *		- selectedAlwaysFixed
 *  - Posibilidad de ocultar todas las pestañas o de ocultarlas cuando sólo hay una visible
 *  - Además todas las pestañas tendrán asociado un TabProperties que permite:
 *  	- Ocultar la solapa
 *		- Mostrar la solapa con aspecto de link o de botón
 *  	- Mostrarla en diálogo (Se ocultará la solapa y solo se mostrará el diálogo si el aspecto es de botón o de link o si seleccionamos la pestaña por código)
 *  	- Propiedades exclusivas de LMenuButtonTabbedPane
 *			- grouped: Agrupar la pestaña (se agrupará a la anterior pestaña - No afecta a la primera pestaña visible)
 *			- iconAux: Icono auxiliar (Revisar)
 *	- Si queremos usar pestañas "cerrables" podemos usar TabCloseComponent
 *
 * @see TabCloseComponent
 * @see LMenuButtonTabbedPane
 **/
@SuppressWarnings("serial")
public class LTabbedPane extends JTabbedPane {

	public static int ASPECT_DEFAULT = 0;
	public static int ASPECT_BUTTON = 1;
	public static int ASPECT_LINK = 2;
	
	public static final int BORDER_SHADOW_LIGHT_ALL = LTabbedPaneUI.BORDER_SHADOW_LIGHT_ALL;
	public static final int BORDER_SHADOW_LIGHT_TABS = LTabbedPaneUI.BORDER_SHADOW_LIGHT_TABS;
	public static final int BORDER_LINE_ALL = LTabbedPaneUI.BORDER_LINE_ALL;
	public static final int BORDER_LINE_TABS = LTabbedPaneUI.BORDER_LINE_TABS;
	public static final int BORDER_NONE = LTabbedPaneUI.BORDER_NONE;
	
	
	public static final String PROPERTY_VISIBLE = "visible";
	public static final String PROPERTY_GROUPED = "grouped";
	public static final String PROPERTY_SHOW_IN_DIALOG = "showInDialog";
	public static final String PROPERTY_DIALOG_SIZE = "dialogSize";
	public static final String PROPERTY_ICON = "icon";
	public static final String PROPERTY_ICON_AUX = "iconAux";
	public static final String PROPERTY_ASPECT = "aspect";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_ENABLED = "enabled";
	public static final String PROPERTY_TITLE = "title";
	
	private List<TabProperties> tabProperties = null;
	
	private boolean hideAllTabs = false;
	private boolean hideSingleTab = true;
	
	public class TabProperties {
		
		private PropertyChangeSupport propertyChangeSupport = null;
		
		public TabProperties(LTabbedPane parent) {
			this.parent = parent;
		}
		
		private LTabbedPane parent = null;
		private boolean visible = true;
		private boolean grouped = false;
		private boolean showInDialog = false;
		private Dimension dialogSize = null;
		private Icon iconAux = null;
		private int aspect = ASPECT_DEFAULT;
		private String description = null;
		
		public LTabbedPane getParent() {
			return parent;
		}
		
		public boolean isVisible() {
			return visible;
		}
		public boolean isGrouped() {
			return grouped;
		}
		public boolean isShowInDialog() {
			return showInDialog;
		}
		public Dimension getDialogSize() {
			if (dialogSize == null)
				dialogSize = new Dimension(800, 600);
			return dialogSize;
		}
		public Icon getIconAux() {
			return iconAux;
		}
		public int getAspect() {
			return aspect;
		}
		public String getDescription() {
			if (description == null)
				description = Constants.VOID;
			return description;
		}
		
		public int getTabIndex() {
			return getParent().getTabProperties().indexOf(this);
		}
		public String getTitle() {
			return getParent().getTitleAt(getTabIndex());
		}
		public boolean isEnabled() {
			return getParent().isEnabledAt(getTabIndex());
		}
		public Icon getIcon() {
			return getParent().getIconAt(getTabIndex());
		}
		
		public void setEnabled(boolean enabled) {
			getParent().setEnabledAt(getTabIndex(), enabled);
		}
		public void setTitle(String title) {
			getParent().setTitleAt(getTabIndex(), title);
		}
		public void setIcon(Icon icon) {
			getParent().setIconAt(getTabIndex(), icon);
		}
		
		public void setVisible(boolean visible) {
			boolean oldValue = this.visible;
			boolean newValue = visible;
			if (oldValue != newValue) {
				this.visible = visible;
				firePropertyChange(PROPERTY_VISIBLE, oldValue, newValue);
			}
		}
		public void setGrouped(boolean grouped) {
			boolean oldValue = this.grouped;
			boolean newValue = grouped;
			if (oldValue != newValue) {
				this.grouped = grouped;
				firePropertyChange(PROPERTY_GROUPED, oldValue, newValue);
			}
		}
		public void setShowInDialog(boolean showInDialog) {
			boolean oldValue = this.showInDialog;
			boolean newValue = showInDialog;
			if (oldValue != newValue) {
				this.showInDialog = showInDialog;
				firePropertyChange(PROPERTY_SHOW_IN_DIALOG, oldValue, newValue);
			}
		}
		public void setDialogSize(Dimension dialogSize) {
			Dimension oldValue = this.dialogSize;
			Dimension newValue = dialogSize;
			if (Utils.propertyChanged(oldValue, newValue)) {
				this.dialogSize = dialogSize;
				firePropertyChange(PROPERTY_DIALOG_SIZE, oldValue, newValue);
			}
		}
		public void setIconAux(Icon iconAux) {
			Icon oldValue = this.iconAux;
			Icon newValue = iconAux;
			if (Utils.propertyChanged(oldValue, newValue)) {
				this.iconAux = iconAux;
				firePropertyChange(PROPERTY_ICON_AUX, oldValue, newValue);
			}
		}
		public void setAspect(int aspect) {
			int oldValue = this.aspect;
			int newValue = aspect;
			if (oldValue != newValue) {
				this.aspect = aspect;
				firePropertyChange(PROPERTY_ASPECT, oldValue, newValue);
			}
		}
		
		/**
		 * La descripción se muestra en aspecto diálogo, añadiendo una cabecera al diálogo con el texto que asignemos 
		 **/
		public void setDescription(String description) {
			String oldValue = this.description;
			String newValue = description;
			if (Utils.propertyChanged(oldValue, newValue)) {
				this.description = description;
				firePropertyChange(PROPERTY_DESCRIPTION, oldValue, newValue);
			}
		}
		
		//
		// PropertyChange methods
		//
		
		private PropertyChangeSupport getPropertyChangeSupport() {
			if (propertyChangeSupport == null)
				propertyChangeSupport = new PropertyChangeSupport(this);
			return propertyChangeSupport;
		}
		
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			getPropertyChangeSupport().addPropertyChangeListener(listener);
		}
		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
		}
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			getPropertyChangeSupport().removePropertyChangeListener(listener);
		}
		public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
		}

		private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			if (propertyChangeSupport != null)
				getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
		}
	}
	
	//
	// Constructors
	//
	
	public LTabbedPane() {
		super();
	}
	public LTabbedPane(int tabPlacement) {
		super(tabPlacement);
	}
	public LTabbedPane(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
	}

	public int getTabIndex(Component component) {
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponentAt(i) == component)
				return i;
		}
		return -1;
	}
	
	//
	// Hidding Tabs
	//
	
	public boolean isHideAllTabs() {
		return hideAllTabs;
	}
	public boolean isHideSingleTab() {
		return hideSingleTab;
	}
	
	public void setHideAllTabs(boolean hideAllTabs) {
		this.hideAllTabs = hideAllTabs;
	}
	public void setHideSingleTab(boolean hideSingleTab) {
		this.hideSingleTab = hideSingleTab;
	}
	
	//
	// TabProperties manage
	//
	
	public List<TabProperties> getTabProperties() {
		if (tabProperties == null)
			tabProperties = Lists.newList();
		return tabProperties;
	}
	
	public TabProperties getTabProperties(int index) {
		try {
			return getTabProperties().get(index);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void insertTab(String title, Icon icon, Component component,	String tip, int index) {
		super.insertTab(title, icon, component, tip, index);
		
		try {
			getTabProperties().add(index, new TabProperties(this));
		} catch (Exception e) {
			getTabProperties().add(new TabProperties(this));
		}
		
	}
	
	@Override
	public void removeTabAt(int index) {
		super.removeTabAt(index);
		
		getTabProperties().remove(index);
	}
	
	@Override
	public void setTitleAt(int index, String title) {
		String oldValue = getTitleAt(index);
		String newValue = title;
		if (Utils.propertyChanged(oldValue, newValue)) {
			super.setTitleAt(index, title);
			getTabProperties(index).firePropertyChange(PROPERTY_TITLE, oldValue, newValue);
		}
	}
	
	@Override
	public void setEnabledAt(int index, boolean enabled) {
		boolean oldValue = isEnabledAt(index);
		boolean newValue = enabled;
		if (oldValue != newValue) {
			super.setEnabledAt(index, enabled);
			getTabProperties(index).firePropertyChange(PROPERTY_ENABLED, oldValue, newValue);
		}
	}
	
	@Override
	public void setIconAt(int index, Icon icon) {
		Icon oldValue = getIconAt(index);
		Icon newValue = icon;
		if (Utils.propertyChanged(oldValue, newValue)) {
			super.setIconAt(index, icon);
			getTabProperties(index).firePropertyChange(PROPERTY_ICON, oldValue, newValue);
		}
	}
	
	public LTabbedPaneUI getLTabbedPaneUI() {
		LTabbedPaneUI ui;
		if (getUI() == null || !(getUI() instanceof LTabbedPaneUI)) {
			ui = new LTabbedPaneUI();
			setUI(ui);
		}
		else {
			ui = (LTabbedPaneUI) getUI();
		}
		return ui;
	}
	
	//LTabbedPaneUI Properties
	public int getBorderType() {
		return getLTabbedPaneUI().getBorderType();
	}
	public Color getLineBorderColor() {
		return getLTabbedPaneUI().getLineBorderColor();
	}
	public Color getSelectedTabLineColor() {
		return getLTabbedPaneUI().getSelectedTabLineColor();
	}
	public boolean isAdjustTabsWidth() {
		return getLTabbedPaneUI().isAdjustTabsWidth();
	}
	public boolean isSelectedAlwaysFixed() {
		return getLTabbedPaneUI().isSelectedAlwaysFixed();
	}
	
	public void setBorderType(int borderType) {
		getLTabbedPaneUI().setBorderType(borderType);
	}
	public void setLineBorderColor(Color lineBorderColor) {
		getLTabbedPaneUI().setLineBorderColor(lineBorderColor);
	}
	public void setSelectedTabLineColor(Color selectedTabLineColor) {
		getLTabbedPaneUI().setSelectedTabLineColor(selectedTabLineColor);
	}
	public void setAdjustTabsWidth(boolean adjustTabsWidth) {
		getLTabbedPaneUI().setAdjustTabsWidth(adjustTabsWidth);
	}
	public void setSelectedAlwaysFixed(boolean selectedAlwaysFixed) {
		getLTabbedPaneUI().setSelectedAlwaysFixed(selectedAlwaysFixed);
	}
}
