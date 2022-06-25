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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import linaje.gui.components.LColorChooser;
import linaje.gui.ui.LButtonUI;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.utils.StateColor;

/**
 * ColorChooserPanel para agregar a un JColorChooser y poder asignar StateColors
 * Es usado por LColorChooser
 **/
@SuppressWarnings("serial")
public class StateColorChooserPanel extends AbstractColorChooserPanel {

	private StateColorChooser stateColorChooser = null;
	private boolean selectingStateColor = false;
	
	PropertyChangeListener stateValuesListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			selectingStateColor = true;
			Color oldColor = getColorFromModel();
			StateColor newStateColor = getStateColorChooser().getValue().clone();
			if (oldColor.getRGB() == newStateColor.getRGB() && !oldColor.toString().equals(newStateColor.toString())) {
				//El colorSelectionModel del ColorChooser no detecta cuando cambiamos uno de los stateColors distinto de defaultColor,
				//por lo que lo tenemos que detectar aquí y provocar un cambio previo
				Color tempColor = new Color(oldColor.getRGB() != Color.white.getRGB() ? Color.white.getRGB() : Color.black.getRGB());
				getColorSelectionModel().setSelectedColor(tempColor);
			}
			
			getColorSelectionModel().setSelectedColor(newStateColor);
			selectingStateColor = false;
		}
	};
	
	public StateColorChooserPanel() {
		super();
	}

	public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			LColorChooser.showDialog(null, "Color", UISupportButtons.getDefaultButtonUIProperties(LButtonUI.class).getBackground());
			System.exit(0);		
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
	public StateColorChooser getStateColorChooser() {
		if (stateColorChooser == null) {
			stateColorChooser = new StateColorChooser(null);
			stateColorChooser.getValue().getStateValues().addPropertyChangeListener(stateValuesListener);
		}
		return stateColorChooser;
	}
	
	private void updateStateColorChooserFromModel() {
		
		if (!selectingStateColor) {
			StateColor origStateColor = getColorFromModel() instanceof StateColor ? (StateColor) getColorFromModel() : null;
			if (origStateColor != null) {
				
				//StateColor newStateColor = origStateColor != null ? origStateColor.clone() : new StateColor(getStateColorChooser().getValue(), getColorFromModel());
				StateColor newStateColor = origStateColor != null ? origStateColor : getStateColorChooser().getValue();
				getStateColorChooser().getValue().getStateValues().removePropertyChangeListener(stateValuesListener);
				getStateColorChooser().setValue(newStateColor);
				getStateColorChooser().getValue().getStateValues().addPropertyChangeListener(stateValuesListener);
				
				JTabbedPane tabbedPane = (JTabbedPane) UtilsGUI.getParentInstanceOf(this, JTabbedPane.class);
				if (tabbedPane != null) {
					tabbedPane.setSelectedComponent(this.getParent());
				}
			}
		}
	}
	
	@Override
	public void updateChooser() {
		
		updateStateColorChooserFromModel();
	}

	@Override
	protected void buildChooser() {
		setLayout(new BorderLayout());
		add(getStateColorChooser(), BorderLayout.CENTER);
	}

	@Override
	public String getDisplayName() {
		return "State colors";
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

}
