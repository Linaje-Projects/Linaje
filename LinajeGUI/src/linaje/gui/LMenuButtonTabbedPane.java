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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linaje.gui.LTabbedPane.TabProperties;
import linaje.gui.components.Link;
import linaje.gui.layouts.LFlowLayout;
import linaje.utils.Lists;
import linaje.utils.Utils;

/**
 * Revisar que no está funcionando bien cuando tenemos pestañas con aspecto de botón o de link 
 **/
@SuppressWarnings("serial")
public class LMenuButtonTabbedPane extends LPanel {

	private LPanel panelFixedElements = null;
	private LTabbedPane tabbedPane = null;
	private LMenuButtonBar lMenuButtonBar = null;
	private LPanel panelMargin = null;
	private List<List<Integer>> visibleTabIndices = null;
	private List<Integer> fixedTabIndices = null;
	private List<Component> fixedLeftExtraComponents = null;
	private List<Component> fixedRightExtraComponents = null;
	
	private boolean creatingTabbedPane = true;
	
	private ChangeListener changeListener = new ChangeListener() {
		
		@Override
		public void stateChanged(ChangeEvent e) {

			if (!isCreatingTabbedPane()) {
				if (e.getSource() == getTabbedPane()) {
					
					if (getTabbedPane().getSelectedIndex() != getLMenuButtonBar().getSelectedIndex())
						getLMenuButtonBar().setSelectedIndex(getTabbedPane().getSelectedIndex());
					
					updateFixedElements();
				}
				else if (e.getSource() == getLMenuButtonBar()) {
					if (getTabbedPane().getSelectedIndex() != getLMenuButtonBar().getSelectedIndex())
						getTabbedPane().setSelectedIndex(getLMenuButtonBar().getSelectedIndex());
				}
			}
		}
	};
	
	private ContainerListener containerListener = new ContainerListener() {
		
		@Override
		public void componentRemoved(ContainerEvent e) {
			if (!isCreatingTabbedPane() && AppGUI.getCurrentAppGUI().isDesignTime()) {
				updateTabs();
			}
		}
		
		@Override
		public void componentAdded(ContainerEvent e) {
			if (!isCreatingTabbedPane() && AppGUI.getCurrentAppGUI().isDesignTime()) {
				updateTabs();
			}
		}
	};
	
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			String propertyName = evt.getPropertyName();
			if (propertyName.equals(LTabbedPane.PROPERTY_GROUPED)
			 || propertyName.equals(LTabbedPane.PROPERTY_TITLE)
			 || propertyName.equals(LTabbedPane.PROPERTY_ENABLED)
			 || propertyName.equals(LTabbedPane.PROPERTY_VISIBLE)) {
			
				if (!isCreatingTabbedPane()) {
					updateTabs();
				}
			}
			//else
			//	getLMenuButtonBar().repaint();
		}
	};
	
	public LMenuButtonTabbedPane() {
		super();
		initialice();
	}
	public LMenuButtonTabbedPane(LTabbedPane tabbedPane) {
		super();
		initialice();
		setTabbedPane(tabbedPane);
	}
	
	private void initialice() {
		
		setBackground(Color.white);
		setLayout(new BorderLayout());
		setOpaque(false);
		
		add(getPanelMargin(), BorderLayout.WEST);
		
		setCreatingTabbedPane(false);
	}
	
	public void destruir() {
		
		getLMenuButtonBar().removeChangeListener(changeListener);
		if (this.tabbedPane != null) {
			this.tabbedPane.removeChangeListener(changeListener);
			this.tabbedPane.removeContainerListener(containerListener);
			this.tabbedPane.removePropertyChangeListener(propertyChangeListener);
		}
		
		panelFixedElements = null;
		tabbedPane = null;
		lMenuButtonBar = null;
		panelMargin = null;
	}

	public void updateTabs() {
		
		getLMenuButtonBar().removeChangeListener(changeListener);
		
		updateTabIndices();
		
		getLMenuButtonBar().setSelectedIndex(getTabbedPane() != null ? getTabbedPane().getSelectedIndex() : 0);
		getLMenuButtonBar().addChangeListener(changeListener);
		remove(getLMenuButtonBar());
		boolean hideSingleTab = getTabbedPane().isHideSingleTab();
		if (!hideSingleTab || !getLMenuButtonBar().isOnlyOneVisibleTab())
			add(getLMenuButtonBar(), BorderLayout.CENTER);
		validate();
		
		updateFixedElements();
	}
	
	private void updateTabIndices() {
		
		List<Integer> fixedTabIndices = Lists.newList();
		List<List<Integer>> normalTabIndices = Lists.newList();
		List<List<String>> menuButtonLabels = Lists.newList();
		
		if (getTabbedPane() != null) {
			
			for (int index = 0; index < getTabbedPane().getTabCount(); index++) {
				
				boolean groupTab = !normalTabIndices.isEmpty();
				TabProperties tabProperties = getTabbedPane().getTabProperties(index);
				boolean visible = tabProperties.isVisible();
				
				if (visible) {
					
					int aspect = tabProperties.getAspect();
					if (aspect == LTabbedPane.ASPECT_DEFAULT) {
						
						boolean grouped = tabProperties.isGrouped();
						groupTab = groupTab && grouped;
						
						List<Integer> groupedTabIndices;
						List<String> groupedTabLabels;
						if (groupTab) {
							groupedTabIndices = Lists.getLastElement(normalTabIndices);
							groupedTabLabels = Lists.getLastElement(menuButtonLabels);
						}
						else {
							groupedTabIndices = Lists.newList();
							groupedTabLabels = Lists.newList();
						}
						
						groupedTabIndices.add(index);
						groupedTabLabels.add(getTabbedPane().getTitleAt(index));
						if (!groupTab) {
							normalTabIndices.add(groupedTabIndices);
							menuButtonLabels.add(groupedTabLabels);
						}
					}
					else {
						fixedTabIndices.add(index);
					}
				}
			}
			setVisibleTabIndices(normalTabIndices);
			setFixedTabIndices(fixedTabIndices);
			getLMenuButtonBar().setElements(menuButtonLabels);
		}
	}
	
	public LPanel getPanelFixedElements() {	
		if (panelFixedElements == null) {
			LFlowLayout lFlowLayout = new LFlowLayout(FlowLayout.CENTER, SwingConstants.BOTTOM, 4, 4, true);
			lFlowLayout.setVerticalAlignment(SwingConstants.TOP);
			panelFixedElements = new LPanel(lFlowLayout);
			panelFixedElements.setOpaque(false);
		}
		return panelFixedElements;
	}

	public LTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(LTabbedPane tabbedPane) {
		
		if (Utils.propertyChanged(this.tabbedPane, tabbedPane)) {
			
			if (this.tabbedPane != null) {
				this.tabbedPane.removeChangeListener(changeListener);
				this.tabbedPane.removeContainerListener(containerListener);
				this.tabbedPane.removePropertyChangeListener(propertyChangeListener);
			}
			
			if (tabbedPane != null) {
				tabbedPane.addChangeListener(changeListener);
				tabbedPane.addContainerListener(containerListener);
				tabbedPane.addPropertyChangeListener(propertyChangeListener);
			}
			
			this.tabbedPane = tabbedPane;
			
			updateTabs();
		}
	}

	public LMenuButtonBar getLMenuButtonBar() {
		if (lMenuButtonBar == null) {
			lMenuButtonBar = new LMenuButtonBar();
			lMenuButtonBar.setOpaque(false);
		}
		return lMenuButtonBar;
	}
	
	private void updateFixedElements() {
		
		getPanelFixedElements().removeAll();
		
		for (int i = 0; i < getFixedLeftExtraComponents().size(); i++) {
			getPanelFixedElements().add(getFixedLeftExtraComponents().get(i));
		}
		
		for (int i = 0; i < getFixedTabIndices().size(); i++) {
			
			final int index = i;
			TabProperties tabProperties = getTabbedPane().getTabProperties(index);
			int aspect = tabProperties.getAspect();
			Icon icon = tabProperties.getIconAux();
			String text = getTabbedPane().getTitleAt(index);
			boolean enabled = getTabbedPane().isEnabledAt(index);
			
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTabbedPane().setSelectedIndex(index);
				}
			};
			
			if (aspect == LTabbedPane.ASPECT_LINK) {
				Link link = new Link(text);
				link.setIcon(icon);
				link.setMargin(new Insets(6,5,6,5));
				link.setEnabled(enabled);
				if (enabled)
					link.addActionListener(actionListener);
				getPanelFixedElements().add(link);
			}
			else {
				LButton button = new LButton(text);
				button.setIcon(icon);
				button.setEnabled(enabled);
				if (enabled)
					button.addActionListener(actionListener);
				getPanelFixedElements().add(button);
			}
		}
		
		for (int i = 0; i < getFixedRightExtraComponents().size(); i++) {
			getPanelFixedElements().add(getFixedRightExtraComponents().get(i));
		}
		
		if (getPanelFixedElements().getComponentCount() > 0) {
			add(getPanelFixedElements(), BorderLayout.EAST);
			getPanelFixedElements().validate();
		}
		else {
			remove(getPanelFixedElements());
		}
	}
	
	public LPanel getPanelMargin() {
		if (panelMargin == null) {
			panelMargin = new LPanel();
			panelMargin.setLayout(null);
			panelMargin.setPreferredSize(new Dimension(5, 0));
			panelMargin.setOpaque(false);
		}
		return panelMargin;
	}

	private boolean isCreatingTabbedPane() {
		return creatingTabbedPane;
	}

	private void setCreatingTabbedPane(boolean creatingTabbedPane) {
		this.creatingTabbedPane = creatingTabbedPane;
	}
	
	public List<List<Integer>> getVisibleTabIndices() {
		if (visibleTabIndices == null)
			visibleTabIndices = Lists.newList();
		return visibleTabIndices;
	}
	public List<Integer> getFixedTabIndices() {
		if (fixedTabIndices == null)
			fixedTabIndices = Lists.newList();
		return fixedTabIndices;
	}
	public List<Component> getFixedLeftExtraComponents() {
		if (fixedLeftExtraComponents == null)
			fixedLeftExtraComponents = Lists.newList();
		return fixedLeftExtraComponents;
	}
	public List<Component> getFixedRightExtraComponents() {
		if (fixedRightExtraComponents == null)
			fixedRightExtraComponents = Lists.newList();
		return fixedRightExtraComponents;
	}
	
	public void setVisibleTabIndices(List<List<Integer>> visibleTabIndices) {
		this.visibleTabIndices = visibleTabIndices;
	}
	public void setFixedTabIndices(List<Integer> fixedTabIndices) {
		this.fixedTabIndices = fixedTabIndices;
	}
	public void setFixedLeftExtraComponents(List<Component> fixedLeftExtraComponents) {
		this.fixedLeftExtraComponents = fixedLeftExtraComponents;
	}
	public void setFixedRightExtraComponents(
			List<Component> fixedRightExtraComponents) {
		this.fixedRightExtraComponents = fixedRightExtraComponents;
	}
}
