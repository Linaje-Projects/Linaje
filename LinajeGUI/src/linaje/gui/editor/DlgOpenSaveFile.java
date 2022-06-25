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
package linaje.gui.editor;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import linaje.LocalizedStrings;
import linaje.gui.LButton;
import linaje.gui.LList;
import linaje.gui.cells.LabelCell;
import linaje.gui.components.LabelTextField;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Files;

@SuppressWarnings("serial")
public class DlgOpenSaveFile extends LDialogContent {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String name;
				
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final int MODE_OPEN = 0;
	public static final int MODE_SAVE = 1;
	
	private LButton buttonOpenSave = null;
	private LButton buttonCancel = null;
	private LList<File> list = null;
	private JScrollPane scrollPane = null;
	private LabelTextField lblTxtFileName = null;
	
	private int mode;
	private File directory;
	private File selectedFile;
	
	public DlgOpenSaveFile(Frame owner, File directory, int mode) {
		super(owner);
		setMode(mode);
		setDirectory(directory);
		initialize();
	}

	public DlgOpenSaveFile(File directory, int mode) {
		super();
		setMode(mode);
		setDirectory(directory);
		initialize();
	}

	private void initialize() {
		
		setLayout(new GridBagLayout());
		setSize(250, 150);
		setModal(true);
		setMargin(5);
		
		ButtonsPanel buttonsPanel = new ButtonsPanel();
		buttonsPanel.addJComponent(getButtonOpenSave(), ButtonsPanel.POSITION_RIGHT);
		buttonsPanel.addJComponent(getButtonCancel(), ButtonsPanel.POSITION_RIGHT);

		setButtonsPanel(buttonsPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		
		add(getScrollPane(), gbc);
		
		if (getMode() == MODE_OPEN) {
			setTitle(LEditor.TEXTS.open);
			getButtonOpenSave().setText(LEditor.TEXTS.open);
		}
		else if (getMode() == MODE_SAVE) {
			setTitle(LEditor.TEXTS.save);
			getButtonOpenSave().setText(LEditor.TEXTS.save);
			
			gbc.gridy = 2;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets.top = 5;
			add(getLblTxtFileName(), gbc);
			
			getLblTxtFileName().getTextField().requestFocus();
		}
	}
	
	private void open() {
		if (getList().getSelectedValue() != null) {
			setSelectedFile(getList().getSelectedValue());
			getButtonsPanel().setResponse(ButtonsPanel.RESPONSE_ACCEPT_YES);
			dispose();
		}
	}
	
	private void save() {
		
		if (!getLblTxtFileName().getText().trim().equals(Constants.VOID)) {
			String fileName = getLblTxtFileName().getText();
			if (!fileName.endsWith(".html"))
				fileName = fileName + ".html";
			
			File saveFile = new File(getDirectory(), fileName);
			setSelectedFile(saveFile);
			getButtonsPanel().setResponse(ButtonsPanel.RESPONSE_ACCEPT_YES);
			dispose();
		}
	}

	
	private LButton getButtonOpenSave() {
		if (buttonOpenSave == null) {
			buttonOpenSave = new LButton(LEditor.TEXTS.open);
			buttonOpenSave.setEnabled(false);
			buttonOpenSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (getMode() == MODE_SAVE)
						save();
					else
						open();
				}
			});
		}
		return buttonOpenSave;
	}

	private LButton getButtonCancel() {
		if (buttonCancel == null) {
			buttonCancel = new LButton(ButtonsPanel.TEXTS.cancel);
			buttonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getButtonsPanel().setResponse(ButtonsPanel.RESPONSE_CANCEL);
					dispose();
				}
			});
		}
		return buttonCancel;
	}

	public class FileRender extends LCellRenderer<File>{
		
		public LabelCell getListCellRendererComponent(JList<? extends File> list, File value, int index, boolean isSelected, boolean cellHasFocus) {
			
			LabelCell labelCell = (LabelCell) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			String fileName = value.getName();
			labelCell.setCode(fileName);
			labelCell.setDescription(Files.getNameWithoutExt(value));
			
			return labelCell;
		}
	}

	private LList<File> getList() {
		if (list == null) {
			list = new LList<>();
			list.setCellRenderer(new FileRender());
			List<File> htmlFiles =  Files.getFilesFromDir(getDirectory(), Console.FILTER_HTML_EXTENSION);
			list.setElements(htmlFiles);
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						File selectedValue = getList().getSelectedValue();
						getButtonOpenSave().setEnabled(selectedValue != null);
						getLblTxtFileName().setText(selectedValue != null ? Files.getNameWithoutExt(selectedValue) : Constants.VOID);
					}
				}
			});
		}
		return list;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getList());
		}
		return scrollPane;
	}

	private LabelTextField getLblTxtFileName() {
		if (lblTxtFileName == null) {
			lblTxtFileName = new LabelTextField();
			lblTxtFileName.setTextLabel(TEXTS.name);
			lblTxtFileName.setAutoSizeLabel(true);
			lblTxtFileName.getTextField().setCompatibleWindowsFiles(true);
			lblTxtFileName.getTextField().addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {}
				public void keyReleased(KeyEvent e) {
					getButtonOpenSave().setEnabled(!getLblTxtFileName().getText().trim().equals(Constants.VOID));
				}
				public void keyPressed(KeyEvent e) {}
			});
		}
		return lblTxtFileName;
	}

	private File getDirectory() {
		return directory;
	}
	private void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public File getSelectedFile() {
		return selectedFile;
	}
	private void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	private int getMode() {
		return mode;
	}
	private void setMode(int mode) {
		this.mode = mode;
	}
}
