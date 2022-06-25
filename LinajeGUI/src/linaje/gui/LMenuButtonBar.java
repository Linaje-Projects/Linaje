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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.windows.LWindow;
import linaje.statics.Constants;
import linaje.utils.Lists;


@SuppressWarnings("serial")
public class LMenuButtonBar extends LPanel {
	
	private List<List<String>> elements = null;
	private List<LMenuButton> lMenuButtons = null;
	private ButtonGroup buttonGroup = null;
	private LButton btnPlus = null;
	
	private LPanel panelMenuButtons = null;
	private LPanel panelBtnPlus = null;
	private LWindow popup = null;
	
	private static final int MARGIN_TOP = 5;
	
	//private List<LMenuButton> fixedElements = null;
		
	protected transient ChangeEvent changeEvent = null;
	private boolean validatingComponents = false;
	
	private ItemListener itemListener = new ItemListener() {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				updateComponents();
				fireStateChanged();
			}
		}
	};
	
	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {
		
		public void mouseMoved(MouseEvent e) {
			if (e.getSource() instanceof LMenuButton) {
				LMenuButton buttonMouseOver = (LMenuButton) e.getSource();
				for (int i = 0; i < getLMenuButtons().size(); i++) {
					LMenuButton button = getLMenuButtons().get(i);
					if (button != buttonMouseOver && button.getPopup().isVisible())
						button.getPopup().setVisible(false);
				}
				if (!buttonMouseOver.isInPopup() && getPopup().getWindow().isVisible())
					getPopup().closeWindow();
			}
		}
		
		public void mouseDragged(MouseEvent e) {}
	};
	
	public LMenuButtonBar() {
		super();
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		add(getPanelMenuButtons(), BorderLayout.CENTER);
		
		getBtnPlus().addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				setPopupVisible(!getPopup().getWindow().isVisible());
			}
			
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}	
			public void mouseClicked(MouseEvent e) {}
		});
		
		getBtnPlus().addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent e) {
				setPopupVisible(true);
				
				for (int i = 0; i < getLMenuButtons().size(); i++) {
					LMenuButton button = getLMenuButtons().get(i);
					if (button.getPopup().isVisible())
						button.getPopup().setVisible(false);
				}
			}
			
			public void mouseDragged(MouseEvent e) {}
		});
	}
	
	public void destroy() {
		
		setElements(null);
		elements = null;
		lMenuButtons = null;
		buttonGroup = null;
		btnPlus = null;
		panelMenuButtons = null;
		panelBtnPlus = null;
		popup = null;
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		updateComponents();
	}
	
	private void setPopupVisible(boolean visible) {
		
		if (visible && !getPopup().getWindow().isVisible()) {
			getPopup().setSize(getPopup().getPreferredSize());
			getPopup().showWindow(getBtnPlus().getLocationOnScreen());
		}
		else if (!visible && getPopup().getWindow().isVisible()) {
			getPopup().closeWindow();
		}
	}
	
	private void updateMenuButtons() {
		
		for (int i = 0; i < getLMenuButtons().size(); i++) {
			LMenuButton lMenuButton = getLMenuButtons().get(i);
			getButtonGroup().remove(lMenuButton);
			lMenuButton.removeItemListener(itemListener);
			lMenuButton.removeMouseMotionListener(mouseMotionListener);
			lMenuButton.destroy();
		}
		getLMenuButtons().clear();
		
		for (int i = 0; i < getElements().size(); i++) {
			
			List<String> element = getElements().get(i);
			if (element != null) {
				LMenuButton lMenuButton;
				lMenuButton = new LMenuButton(element);
				
				getLMenuButtons().add(lMenuButton);
				getButtonGroup().add(lMenuButton);
				lMenuButton.addItemListener(itemListener);
				lMenuButton.addMouseMotionListener(mouseMotionListener);
			}
		}
		
		updateComponents();
	}
	
	private void updateComponents() {

		if (!validatingComponents) {
			validatingComponents = true;
			try {
				
				List<LMenuButton> itemsVisibles = Lists.newList();
				
				remove(getPanelBtnPlus());
				getPanelMenuButtons().removeAll();
				
				for (int i = 0; i < getLMenuButtons().size(); i++) {
					LMenuButton lMenuButton = getLMenuButtons().get(i);
					if (lMenuButton.isVisible()) {
						lMenuButton.setInPopup(false);
						getPanelMenuButtons().add(lMenuButton);
						itemsVisibles.add(lMenuButton);
					}
				}
				validate();
				
				List<LMenuButton> itemsBar = Lists.newList();
				List<LMenuButton> itemsPopup = Lists.newList();
				
				//Miramos que botones no caben y los ponemos en el popup
				for (int i = 0; i < itemsVisibles.size(); i++) {
					LMenuButton lMenuButton = itemsVisibles.get(i);
					if (lMenuButton.isVisible()) {
						if (lMenuButton.getLocation().y > MARGIN_TOP)
							itemsPopup.add(lMenuButton);
						else
							itemsBar.add(lMenuButton);
					}
				}
				
				if (!itemsPopup.isEmpty()) {
					
					add(getPanelBtnPlus(), BorderLayout.EAST);
					validate();
					
					//Volvemos a calcular cuales caben o no, ahora sin el espacio del botón mas
					itemsBar.clear();
					itemsPopup.clear();
					for (int i = 0; i < itemsVisibles.size(); i++) {
						LMenuButton lMenuButton = itemsVisibles.get(i);
						if (lMenuButton.getLocation().y > MARGIN_TOP)
							itemsPopup.add(lMenuButton);
						else
							itemsBar.add(lMenuButton);
					}
					
					//getWindowPopup().setVisible(false);
					getPopup().closeWindow();
					LMenuButton selectedItem = getSelectedItem();
					if (selectedItem != null && itemsPopup.contains(selectedItem) && !itemsBar.isEmpty()) {
						//Movemos el elemento seleccionado a la barra y ponemos en el popup el último elemento de la barra
						LMenuButton lastItemBar = Lists.getLastElement(itemsBar);
						itemsBar.remove(lastItemBar);
						itemsBar.add(selectedItem);
						itemsPopup.remove(selectedItem);
						itemsPopup.add(0, lastItemBar);
						//Volvemos a pintar
						getPanelMenuButtons().removeAll();
						for (int i = 0; i < itemsBar.size(); i++) {
							LMenuButton lMenuButton = itemsBar.get(i);
							getPanelMenuButtons().add(lMenuButton);
						}
						validate();
						//Volvemos a comprobar si cabe el elemento seleccionado en la barra
						if (selectedItem.getLocation().y > MARGIN_TOP && itemsBar.size() > 1) {
							//No cabe, por lo que movemos el penultimo elemento al popup
							LMenuButton penultimateItemBar = itemsBar.get(itemsBar.size()-2);
							itemsBar.remove(penultimateItemBar);
							itemsPopup.add(0, penultimateItemBar);
						}
					}
					
					getPanelMenuButtons().removeAll();
					getPopup().removeAll();
					for (int i = 0; i < itemsBar.size(); i++) {
						LMenuButton lMenuButton = itemsBar.get(i);
						lMenuButton.setInPopup(false);
						getPanelMenuButtons().add(lMenuButton);
					}
					for (int i = 0; i < itemsPopup.size(); i++) {
						LMenuButton lMenuButton = itemsPopup.get(i);
						lMenuButton.setInPopup(true);
						LPanel panelAux = new LPanel(new BorderLayout());
						panelAux.add(lMenuButton, BorderLayout.CENTER);
						getPopup().add(panelAux);
					}
					validate();
					getPanelBtnPlus().repaint();
				}
			} finally {
				validatingComponents = false;
			}
		}
	}
		
	public LMenuButton getSelectedItem() {
		
		for (int i = 0; i < getLMenuButtons().size(); i++) {
			LMenuButton lMenuButton = getLMenuButtons().get(i);
			if (lMenuButton.isSelected())
				return lMenuButton;
		}
		return null;
	}
	
	public int getSelectedIndex() {
		
		int selectedIndex = -1;
		LMenuButton selectedItem = getSelectedItem();
		if (selectedItem != null) {
			int indexButton = getLMenuButtons().indexOf(selectedItem);
			selectedIndex = indexButton + selectedItem.getSelectedIndex();
			//Añadimos los elements agrupados que haya delante
			for (int i = 0; i < indexButton; i++) {
				LMenuButton lMenuButton = getLMenuButtons().get(i);
				selectedIndex = selectedIndex + lMenuButton.getElements().size() - 1;
			}
		}
		
		return selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		
		int indexStart = 0;
		boolean encontrado = false;
		for (int i = 0; !encontrado && i < getLMenuButtons().size(); i++) {
			LMenuButton lMenuButton = getLMenuButtons().get(i);
			int indexEnd = indexStart + lMenuButton.getElements().size() - 1;
			if (selectedIndex >= indexStart && selectedIndex <= indexEnd) {
				lMenuButton.setSelectedIndex(selectedIndex - indexStart);
				encontrado = true;
			}
			else {
				indexStart = indexEnd + 1;
			}
		}
	}
	
	public void addElement(String element) {
		List<String> elementList = Lists.newList();
		elementList.add(element);
		addElement(elementList);
	}
	
	public void addElement(List<String> element) {
		getElements().add(element);
		updateMenuButtons();
	}
	
	public List<List<String>> getElements() {
		if (elements == null)
			elements = Lists.newList();
		return elements;
	}

	public void setElements(List<List<String>> elements) {
		this.elements = elements;
		updateMenuButtons();
		Dimension dimension = elements.isEmpty() ? new Dimension(0, 0) : null;
		setPreferredSize(dimension);
	}
	
	public List<LMenuButton> getLMenuButtons() {
		if (lMenuButtons == null)
			lMenuButtons = Lists.newList();
		return lMenuButtons;
	}

	public ButtonGroup getButtonGroup() {
		if (buttonGroup == null)
			buttonGroup = new ButtonGroup();
		return buttonGroup;
	}

	private LButton getBtnPlus() {
		if (btnPlus == null) {
			btnPlus = new LButton("A");
			Dimension preferredSize = btnPlus.getPreferredSize();
			btnPlus.getButtonProperties().setIconForegroundEnabled(false);
			btnPlus.setIcon(Icons.getIconPlus(btnPlus.getFontSize()-1, 2, null));
			btnPlus.setText(Constants.VOID);
			btnPlus.setPreferredSize(new Dimension(preferredSize.height, preferredSize.height));
		}
		return btnPlus;
	}

	private LPanel getPanelMenuButtons() {
		if (panelMenuButtons == null) {
			panelMenuButtons = new LPanel(new FlowLayout(FlowLayout.LEFT, 0, MARGIN_TOP));
			panelMenuButtons.setOpaque(false);
		}
		return panelMenuButtons;
	}

	private LPanel getPanelBtnPlus() {
		if (panelBtnPlus == null) {
			panelBtnPlus = new LPanel(new FlowLayout(FlowLayout.RIGHT, MARGIN_TOP, MARGIN_TOP));
			panelBtnPlus.setOpaque(false);
			panelBtnPlus.add(getBtnPlus());
		}
		return panelBtnPlus;
	}

	private LWindow getPopup() {
		if (popup == null) {
			popup = new LWindow();
			popup.setOpaque(false);
			popup.setLayout(new VerticalBagLayout());
		}
		return popup;
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	/*public List<LMenuButton> getFixedElements() {
		if (fixedElements == null)
			fixedElements = Lists.newList();
		return fixedElements;
	}*/
	
	public boolean isOnlyOneVisibleTab() {
		
		boolean onlyOneVisibleTab = getPanelMenuButtons().getComponentCount() == 0;
		if (!onlyOneVisibleTab && getPanelMenuButtons().getComponentCount() == 1) {
			LMenuButton lMenuButton = (LMenuButton) getPanelMenuButtons().getComponent(0); 
			if (!lMenuButton.canShowPopup())
				onlyOneVisibleTab = true;
		}
		
		return onlyOneVisibleTab;
	}
}
