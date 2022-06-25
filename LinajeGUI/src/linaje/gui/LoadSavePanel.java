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
import java.awt.FlowLayout;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LoadSavePanel extends JPanel {

	private LButton btnLoadFrom = null;
	private LButton btnSave = null;
	private LButton btnSaveAs = null;
	private LButton btnSaveAsDefault = null;
	private LButton btnOpenInSystem = null;
	
	private LoadSaveActions loadSaveActions = null;
	
	private JMenuBar menuBar = null;
	
	public LoadSavePanel(LoadSaveActions lsActions) {
		super();
		this.loadSaveActions = lsActions;
		initialize();
	}

	private void initialize() {
		
		getLoadSaveActions().setParent(this);
		setLayout(new BorderLayout());
		
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(getLoadSaveActions().getComboLoadFastFiles());
		buttonsPanel.add(getBtnLoadFrom());
		buttonsPanel.add(getBtnSave());
		buttonsPanel.add(getBtnSaveAs());
		buttonsPanel.add(getBtnSaveAsDefault());
		buttonsPanel.add(getBtnOpenInSystem());
		
		//add(getMenuBar(), BorderLayout.NORTH);
		add(buttonsPanel, BorderLayout.CENTER);

	}
	
	public LoadSaveActions getLoadSaveActions() {
		return loadSaveActions;
	}
	
	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			JMenu menuFiles = new JMenu("Files");
			menuFiles.add(new JMenuItem(getLoadSaveActions().getActionSave()));
			menuFiles.add(new JMenuItem(getLoadSaveActions().getActionSaveAs()));
			menuFiles.add(new JMenuItem(getLoadSaveActions().getActionSaveAsDefault()));
			menuFiles.addSeparator();
			menuFiles.add(new JMenuItem(getLoadSaveActions().getActionClose()));
			menuFiles.add(new JMenuItem(getLoadSaveActions().getActionLoadFrom()));
			menuFiles.add(new JMenuItem(getLoadSaveActions().getActionOpenInSystem()));
			menuFiles.addSeparator();
			menuFiles.add(getLoadSaveActions().getMenuLoadFastFiles());
			menuBar.add(menuFiles);
		}
		return menuBar;
	}
	
	public LButton getBtnLoadFrom() {
		if (btnLoadFrom == null) {
			btnLoadFrom = new LButton(getLoadSaveActions().getActionLoadFrom());
		}
		return btnLoadFrom;
	}
	public LButton getBtnSave() {
		if (btnSave == null) {
			btnSave = new LButton(getLoadSaveActions().getActionSave());	
		}
		return btnSave;
	}
	public LButton getBtnSaveAs() {
		if (btnSaveAs == null) {
			btnSaveAs = new LButton(getLoadSaveActions().getActionSaveAs());
		}
		return btnSaveAs;
	}
	public LButton getBtnSaveAsDefault() {
		if (btnSaveAsDefault == null) {
			btnSaveAsDefault = new LButton(getLoadSaveActions().getActionSaveAsDefault());
		}
		return btnSaveAsDefault;
	}
	
	public LButton getBtnOpenInSystem() {
		if (btnOpenInSystem == null) {
			btnOpenInSystem = new LButton(getLoadSaveActions().getActionOpenInSystem());
		}
		return btnOpenInSystem;
	}
}
